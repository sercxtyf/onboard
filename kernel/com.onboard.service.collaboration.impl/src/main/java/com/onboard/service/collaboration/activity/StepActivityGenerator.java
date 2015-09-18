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
import com.onboard.domain.model.Step;
import com.onboard.domain.model.User;
import com.onboard.domain.model.type.BaseProjectItem;
import com.onboard.service.account.UserService;
import com.onboard.service.activity.ActivityActionType;
import com.onboard.service.activity.ActivityGenerator;
import com.onboard.service.collaboration.ProjectService;
import com.onboard.service.collaboration.StepService;

@Service("stepActivityGeneratorBean")
public class StepActivityGenerator implements ActivityGenerator {

    public static final int MAX_CONTENT_LENGTH = 200;

    public static final String CREATE_SUBJECT = "创建了任务";
    public static final String UPDATE_SUBJECT = "更新了任务";
    public static final String STATUS_SUBJECT = "将任务状态由“%s”变为“%s”";
    public static final String DISCARD_SUBJECT = "删除了任务";
    public static final String RECOVER_SUBJECT = "从回收站还原了任务";
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

    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("MM月dd日");
    public static final Logger logger = LoggerFactory.getLogger(StepActivityGenerator.class);
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

    @Autowired
    private UserService userService;

    @Autowired
    private StepService stepService;

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

    private Activity generateActivityByActionType(String actionType, String subject, Step step) {
        Activity activity = ActivityRecorderHelper.generateActivityByActionType(actionType, subject, step);
        activity.setTarget(step.getContent());
        activity.setProjectId(step.getProjectId());
        activity.setCompanyId(step.getCompanyId());
        activity.setProjectName(projectService.getById(step.getProjectId()).getName());
        return ActivityRecorderHelper.enrichActivity(activity);

    }

    private String getDueDateChangeContent(Step step, Step modifiedStep) {
        if (modifiedStep.getDueDate() != null && !modifiedStep.getDueDate().equals(step.getDueDate())) {
            if (step.getDueDate() == null) {
                return String.format(DUEDATE_UPDATE, DUEDATE_NULL, dateFormat.format(modifiedStep.getDueDate()));
            } else {
                return String.format(DUEDATE_UPDATE, dateFormat.format(step.getDueDate()),
                        dateFormat.format(modifiedStep.getDueDate()));
            }
        } else if (modifiedStep.getDueDate() == null && step.getDueDate() != null) {
            return String.format(DUEDATE_UPDATE, dateFormat.format(step.getDueDate()), DUEDATE_NULL);
        }

        return null;
    }

    private String getAssigneeChangeContent(Step step, Step modifiedStep) {
        if (modifiedStep.getAssigneeId() != null && !modifiedStep.getAssigneeId().equals(step.getAssigneeId())) {
            String modifiedName = userService.getById(modifiedStep.getAssigneeId()).getName();

            if (step.getAssigneeId() == null) {
                return String.format(ASSIGNEE_UPDATE, ASSIGNEE_NULL, modifiedName);
            } else {
                String name = userService.getById(step.getAssigneeId()).getName();
                return String.format(ASSIGNEE_UPDATE, name, modifiedName);
            }
        } else if (modifiedStep.getAssigneeId() == null && step.getAssigneeId() != null) {
            String name = userService.getById(step.getAssigneeId()).getName();
            return String.format(ASSIGNEE_UPDATE, name, ASSIGNEE_NULL);
        }
        return null;
    }

    private Activity generateUpdateStatusActivity(Step step, Step modifiedStep) {
        String content = String.format(STATUS_SUBJECT, TODO_STATUS_MAP.get(step.getStatus()),
                TODO_STATUS_MAP.get(modifiedStep.getStatus()));
        if (modifiedStep.getIterationStatus().equals(IterationItemStatus.CLOSED.getValue())) {
            content = "完成了任务";
        } else if (modifiedStep.getIterationStatus().equals(IterationItemStatus.INPROGESS.getValue())) {
            content = "正在做任务";
        } else if (modifiedStep.getIterationStatus().equals(IterationItemStatus.TODO.getValue())) {
            content = "停止了任务";
        }
        Activity activity = generateActivityByActionType(modifiedStep.getStatus(), content, step);
        return activity;
    }

    private Activity generateUpdateActivityWithContent(Step step, Step modifiedStep) {
        Activity activity = generateActivityByActionType(ActivityActionType.UPDATE, UPDATE_SUBJECT, step);
        String nameContent = null;
        String assigneeContent = null;
        String duedateContent = null;
        if (modifiedStep.getContent() != null && !modifiedStep.getContent().equals(step.getContent())) {
            nameContent = String.format(NAME_UPDATE, step.getContent(), modifiedStep.getContent());
        }
        assigneeContent = getAssigneeChangeContent(step, modifiedStep);
        duedateContent = getDueDateChangeContent(step, modifiedStep);
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
        return new Step().getType();
    }

    @Override
    public Activity generateCreateActivity(BaseProjectItem item) {
        Step todo = (Step) item;
        String subject = CREATE_SUBJECT;
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
        Step todo = (Step) item;
        Step modifiedTodo = (Step) modifiedItem; // modifiedTodo只有改变的信息，大部分信息可能为空
        if (modifiedTodo.getDeleted() != null && todo.getDeleted() != modifiedTodo.getDeleted()) {
            if (modifiedTodo.getDeleted()) {
                return generateActivityByActionType(ActivityActionType.DISCARD, DISCARD_SUBJECT, todo);
            } else {
                return generateActivityByActionType(ActivityActionType.RECOVER, RECOVER_SUBJECT, todo);
            }
        } else if (modifiedTodo.getStatus() != null && !todo.getStatus().equals(modifiedTodo.getStatus())) {
            return generateUpdateStatusActivity(todo, modifiedTodo);
        } else {
            return generateUpdateActivityWithContent(todo, modifiedTodo);
        }
    }

    @Override
    public BaseProjectItem enrichModel(BaseProjectItem identifiable) {
        return new Step(stepService.getById(identifiable.getId()));
    }

    @Override
    public String modelService() {
        return StepService.class.getName();
    }
}
