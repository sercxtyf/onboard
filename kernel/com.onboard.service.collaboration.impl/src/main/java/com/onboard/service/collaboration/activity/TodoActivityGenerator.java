/*******************************************************************************
 * Copyright [2015] [Onboard team of SERC, Peking University]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.onboard.service.collaboration.activity;

import java.text.SimpleDateFormat;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Maps;
import com.onboard.domain.model.Activity;
import com.onboard.domain.model.IterationItemStatus;
import com.onboard.domain.model.Project;
import com.onboard.domain.model.Todo;
import com.onboard.domain.model.TodoType;
import com.onboard.domain.model.Todolist;
import com.onboard.domain.model.User;
import com.onboard.domain.model.type.BaseProjectItem;
import com.onboard.service.account.UserService;
import com.onboard.service.activity.ActivityActionType;
import com.onboard.service.activity.ActivityGenerator;
import com.onboard.service.collaboration.ProjectService;
import com.onboard.service.collaboration.TodoService;
import com.onboard.service.collaboration.TodolistService;

/**
 * 生成任务相关活动信息的辅助类
 * 
 * TODO: content, assignee和due date的更新信息生成可以优化，在现有的页面交互模式下可以正确工作，即它们是分别通过不同的请求来更新的。 如果在同一个请求中更新，那么产生的Activity信息可能有错误
 * ，主要在于null值的判断（比如传入todo的assignee为null，是不去设置（如改变content），还是就是要设置为null）
 * 
 * @author yewei
 * 
 */
@Service("todoActivityGeneratorBean")
public class TodoActivityGenerator implements ActivityGenerator {

    public static final int MAX_CONTENT_LENGTH = 200;

    public static final String CREATE_SUBJECT = "创建了%s";
    public static final String COPY_SUBJECT = "复制创建了任务";
    public static final String UPDATE_SUBJECT = "更新了任务";
    public static final String STATUS_SUBJECT = "将任务状态由“%s”变为“%s”";
    public static final String TODOTYPE_SUBJECT = "更改了任务类型";
    public static final String DISCARD_SUBJECT = "删除了任务";
    public static final String RECOVER_SUBJECT = "从回收站还原了任务";
    public static final String MOVE_SUBJECT = "移动了任务";
    public static final String RELOCATE_SUBJECT = "搬运了任务";
    public static final String DOING_SUBJECT = "正在做任务";
    public static final String UNDOING_SUBJECT = "暂停了任务";

    public static final String ASSIGNEE_SET = "分配给了%s";
    public static final String DUEDATE_SET = "期限为%s";
    public static final String ASSIGNEE_AND_DUEDATE_SET = ASSIGNEE_SET + "；" + DUEDATE_SET;

    public static final String ASSIGNEE_UPDATE = "任务执行者由“%s”变为“%s”";
    public static final String DUEDATE_UPDATE = "期限由“%s”变为“%s”";
    public static final String NAME_UPDATE = "名称由“%s”变为“%s”";
    public static final String TODOLIST_MOVE = "从任务列表“%s”移动到任务列表“%s”";
    public static final String PROJECT_MOVE = "从项目“%s”搬运到项目“%s”";
    public static final String STATUS_UPDATE_CONTENT = "状态由“%s”变为“%s”";
    public static final String STATUS_UPDATE_TODOTYPE_CONTENT = "类型由“%s”变为“%s”";

    public static final String ASSIGNEE_NULL = "暂不分配";
    public static final String DUEDATE_NULL = "暂不指定";

    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("MM月dd日 ");
    private static final Logger logger = LoggerFactory.getLogger(TodoActivityGenerator.class);
    private static final Map<String, String> TODO_STATUS_MAP = Maps.newHashMap();
    static {
        TODO_STATUS_MAP.put(IterationItemStatus.TODO.getValue(), "未开始");
        TODO_STATUS_MAP.put(IterationItemStatus.INPROGESS.getValue(), "正在做");
        TODO_STATUS_MAP.put(IterationItemStatus.FIXED.getValue(), "已提交");
        TODO_STATUS_MAP.put(IterationItemStatus.APPROVED.getValue(), "同意完成");
        TODO_STATUS_MAP.put(IterationItemStatus.REVIEWED.getValue(), "复审通过");
        TODO_STATUS_MAP.put(IterationItemStatus.VERIFIED.getValue(), "测试通过");
        TODO_STATUS_MAP.put(IterationItemStatus.CLOSED.getValue(), "已完成");
    }
    private static final Map<String, String> TODO_TYPE_MAP = Maps.newHashMap();
    static {
        TODO_TYPE_MAP.put(TodoType.BUG.getValue(), "BUG");
        TODO_TYPE_MAP.put(TodoType.STORY.getValue(), "需求");
        TODO_TYPE_MAP.put(TodoType.TASK.getValue(), "任务");
    }

    @Autowired
    private UserService userService;

    @Autowired
    private TodoService todoService;

    @Autowired
    private TodolistService todolistService;

    @Autowired
    private ProjectService projectService;

    public String appendString(String str, String toAppend) {
        if (str != null && toAppend != null) {
            return str + "，" + toAppend;
        } else if (str == null) {
            return toAppend;
        } else {
            return str;
        }
    }

    private Activity generateActivityByActionType(String actionType, String subject, Todo todo) {

        Activity activity = ActivityRecorderHelper.generateActivityByActionType(actionType, subject, todo);

        activity.setTarget(todo.getContent());

        activity.setProjectId(todo.getProjectId());
        activity.setCompanyId(todo.getCompanyId());

        return ActivityRecorderHelper.enrichActivity(activity);

    }

    private Activity generateMoveActivity(Todo todo, Todo modifiedTodo) {

        Activity activity = generateActivityByActionType(ActivityActionType.MOVE, MOVE_SUBJECT, todo);

        Todolist todolistFrom = todolistService.getById(todo.getTodolistId());
        Todolist todolistTo = todolistService.getById(modifiedTodo.getTodolistId());

        if (todolistFrom != null && todolistTo != null) {
            activity.setContent(String.format(TODOLIST_MOVE, todolistFrom.getName(), todolistTo.getName()));
            return activity;
        }

        return null;
    }

    /**
     * 项目之间移动
     * 
     * @author Chenlong
     * @param todo
     * @param modifiedTodo
     * @return
     */
    private Activity generateRelocateActivity(Todo todo, Todo modifiedTodo) {

        Activity activity = generateActivityByActionType(ActivityActionType.RELOCATE, RELOCATE_SUBJECT, todo);

        Project projectFrom = projectService.getById(todo.getProjectId());
        Project projectTo = projectService.getById(modifiedTodo.getProjectId());

        if (projectFrom != null && projectTo != null) {
            activity.setContent(String.format(PROJECT_MOVE, projectFrom.getName(), projectTo.getName()));
            return activity;
        }

        return null;
    }

    private String getDueDateChangeContent(Todo todo, Todo modifiedTodo) {
        if (modifiedTodo.getDueDate() != null && !modifiedTodo.getDueDate().equals(todo.getDueDate())) {
            if (todo.getDueDate() == null) {
                return String.format(DUEDATE_UPDATE, DUEDATE_NULL, dateFormat.format(modifiedTodo.getDueDate()));
            } else {
                return String.format(DUEDATE_UPDATE, dateFormat.format(todo.getDueDate()),
                        dateFormat.format(modifiedTodo.getDueDate()));
            }
        } else if (modifiedTodo.getDueDate() == null && todo.getDueDate() != null) {
            return String.format(DUEDATE_UPDATE, dateFormat.format(todo.getDueDate()), DUEDATE_NULL);
        }

        return null;
    }

    private String getAssigneeChangeContent(Todo todo, Todo modifiedTodo) {
        if (modifiedTodo.getAssigneeId() != null && !modifiedTodo.getAssigneeId().equals(todo.getAssigneeId())) {
            String modifiedName = userService.getById(modifiedTodo.getAssigneeId()).getName();

            if (todo.getAssigneeId() == null) {
                return String.format(ASSIGNEE_UPDATE, ASSIGNEE_NULL, modifiedName);
            } else {
                String name = userService.getById(todo.getAssigneeId()).getName();
                return String.format(ASSIGNEE_UPDATE, name, modifiedName);
            }
        } else if (modifiedTodo.getAssigneeId() == null && todo.getAssigneeId() != null) {
            String name = userService.getById(todo.getAssigneeId()).getName();
            return String.format(ASSIGNEE_UPDATE, name, ASSIGNEE_NULL);
        }
        return null;
    }

    private Activity generateUpdateTodoTypeActivity(Todo todo, Todo modifiedTodo) {
        Activity activity = generateActivityByActionType(ActivityActionType.UPDATE, TODOTYPE_SUBJECT, todo);
        activity.setContent(String.format(STATUS_UPDATE_TODOTYPE_CONTENT, TODO_TYPE_MAP.get(todo.getTodoType()),
                TODO_TYPE_MAP.get(modifiedTodo.getTodoType())));
        return activity;
    }

    private Activity generateUpdateStatusActivity(Todo todo, Todo modifiedTodo) {
        Activity activity = generateActivityByActionType(
                modifiedTodo.getStatus(),
                String.format(STATUS_SUBJECT, TODO_STATUS_MAP.get(todo.getStatus()),
                        TODO_STATUS_MAP.get(modifiedTodo.getStatus())), todo);
        // activity.setContent(String.format(STATUS_UPDATE_CONTENT,
        // TODO_STATUS_MAP.get(todo.getStatus()),
        // TODO_STATUS_MAP.get(modifiedTodo.getStatus())));
        return activity;
    }

    private Activity generateUpdateActivityWithContent(Todo todo, Todo modifiedTodo) {
        // logger.info("modified: " + modifiedTodo.getProjectId());
        // logger.info("original: " + todo.getProjectId());
        if (!modifiedTodo.getProjectId().equals(todo.getProjectId())) {
            return generateRelocateActivity(todo, modifiedTodo);
        } else if (modifiedTodo.getTodolistId() != null && !modifiedTodo.getTodolistId().equals(todo.getTodolistId())) {
            return generateMoveActivity(todo, modifiedTodo);
        } else if (modifiedTodo.getPosition() != null && !modifiedTodo.getPosition().equals(todo.getPosition())) {
            return null;
        }

        Activity activity = generateActivityByActionType(ActivityActionType.UPDATE, UPDATE_SUBJECT, todo);

        String nameContent = null;
        String assigneeContent = null;
        String duedateContent = null;

        if (modifiedTodo.getContent() != null && !modifiedTodo.getContent().equals(todo.getContent())) {
            nameContent = String.format(NAME_UPDATE, todo.getContent(), modifiedTodo.getContent());
        }

        assigneeContent = getAssigneeChangeContent(todo, modifiedTodo);
        duedateContent = getDueDateChangeContent(todo, modifiedTodo);

        // String content = this.appendString(nameContent, assigneeContent);
        // content = this.appendString(content, duedateContent);

        String content = null;

        // TODO: if
        // 顺序判断改变的话，activity信息会有误，null值到底是表示“该属性修改为null”还是“忽略该属性”这个问题没有得到彻底解决

        if (nameContent != null) {
            content = nameContent;
        }

        if (duedateContent != null) {
            content = appendString(content, duedateContent);
        }

        if (assigneeContent != null) {
            content = appendString(content, assigneeContent);
        }

        if (content != null) {
            activity.setContent(content);
            return activity;
        }

        return null; // nothing changed
    }

    @Override
    public String modelType() {
        return new Todo().getType();
    }

    @Override
    public Activity generateCreateActivity(BaseProjectItem item) {

        Todo todo = (Todo) item;

        String subject = String.format(CREATE_SUBJECT, TODO_TYPE_MAP.get(todo.getTodoType()));

        // not always true
        if (todo.getComments() != null) {
            subject = COPY_SUBJECT;
        }

        Activity activity = generateActivityByActionType(ActivityActionType.CREATE, subject, todo);

        User user = null;
        if (todo.getAssigneeId() != null) {
            user = userService.getById(todo.getAssigneeId());
        }

        if (todo.getAssigneeId() != null && todo.getDueDate() != null) {
            activity.setContent(String.format(ASSIGNEE_AND_DUEDATE_SET, user.getName(), dateFormat.format(todo.getDueDate())));
        } else if (todo.getAssigneeId() != null) {
            activity.setContent(String.format(ASSIGNEE_SET, user.getName()));
        } else if (todo.getDueDate() != null) {
            activity.setContent(String.format(DUEDATE_SET, dateFormat.format(todo.getDueDate())));
        }

        return activity;
    }

    @Override
    public Activity generateUpdateActivity(BaseProjectItem item, BaseProjectItem modifiedItem) {
        Todo todo = (Todo) item;
        Todo modifiedTodo = (Todo) modifiedItem; // modifiedTodo只有改变的信息，大部分信息可能为空
        if (modifiedTodo.getDeleted() != null && todo.getDeleted() != modifiedTodo.getDeleted()) {
            if (modifiedTodo.getDeleted()) {
                return generateActivityByActionType(ActivityActionType.DISCARD, DISCARD_SUBJECT, todo);
            } else {
                return generateActivityByActionType(ActivityActionType.RECOVER, RECOVER_SUBJECT, todo);
            }
        } else if (modifiedTodo.getStatus() != null && !todo.getStatus().equals(modifiedTodo.getStatus())) {
            return generateUpdateStatusActivity(todo, modifiedTodo);
        } else if (modifiedTodo.getTodoType() != null && !todo.getTodoType().equals(modifiedTodo.getTodoType())) {
            return generateUpdateTodoTypeActivity(todo, modifiedTodo);
        } else {
            logger.info("To move todo...");
            return generateUpdateActivityWithContent(todo, modifiedTodo);
        }
    }

    @Override
    public BaseProjectItem enrichModel(BaseProjectItem identifiable) {
        return new Todo(todoService.getById(identifiable.getId()));
    }

    @Override
    public String modelService() {
        return TodoService.class.getName();
    }
}
