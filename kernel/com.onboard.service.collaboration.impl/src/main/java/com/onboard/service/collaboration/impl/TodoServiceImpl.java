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
package com.onboard.service.collaboration.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.onboard.domain.mapper.AttachTodoMapper;
import com.onboard.domain.mapper.TodoMapper;
import com.onboard.domain.mapper.TodolistMapper;
import com.onboard.domain.mapper.base.BaseMapper;
import com.onboard.domain.mapper.model.AttachTodoExample;
import com.onboard.domain.mapper.model.KeywordExample;
import com.onboard.domain.mapper.model.TodoExample;
import com.onboard.domain.mapper.model.TodoExample.Criteria;
import com.onboard.domain.model.AttachTodo;
import com.onboard.domain.model.IterationItemStatus;
import com.onboard.domain.model.Keyword;
import com.onboard.domain.model.Subscriber;
import com.onboard.domain.model.Todo;
import com.onboard.domain.model.TodoType;
import com.onboard.domain.model.Todolist;
import com.onboard.domain.model.User;
import com.onboard.domain.model.type.BaseOperateItem;
import com.onboard.domain.model.type.IdentifiableOperator;
import com.onboard.domain.model.type.ProjectItem;
import com.onboard.service.account.UserService;
import com.onboard.service.base.AbstractBaseService;
import com.onboard.service.collaboration.CommentService;
import com.onboard.service.collaboration.IdInProjectService;
import com.onboard.service.collaboration.KeywordService;
import com.onboard.service.collaboration.ProjectItemService;
import com.onboard.service.collaboration.ProjectService;
import com.onboard.service.collaboration.TodoService;
import com.onboard.service.collaboration.TodolistService;
import com.onboard.service.collaboration.TopicService;
import com.onboard.service.common.identifiable.IdentifiableManager;
import com.onboard.service.common.subscrible.SubscriberService;
import com.onboard.service.web.SessionService;

/**
 * {@link TodoService}接口实现
 * 
 * @author huangsz, ruici, yewei
 */

@Transactional
@Service("todoServiceBean")
public class TodoServiceImpl extends AbstractBaseService<Todo, TodoExample> implements TodoService, ProjectItemService {

    public static final Logger logger = LoggerFactory.getLogger(TodoServiceImpl.class);

    public static final int DEFAULT_LIMIT = -1;

    @Autowired
    TodoMapper todoMapper;
    @Autowired
    TodolistMapper todolistMapper;
    @Autowired
    TodolistService todolistService;
    @Autowired
    UserService userService;
    @Autowired
    ProjectService projectService;
    @Autowired
    SubscriberService subscriberService;
    @Autowired
    CommentService commentService;
    @Autowired
    TopicService topicService;
    @Autowired
    IdInProjectService idInProjectService;
    @Autowired
    AttachTodoMapper attachTodoMapper;
    @Autowired
    SessionService sessionService;
    @Autowired
    IdentifiableManager identifiableManager;
    @Autowired
    KeywordService keywordService;

    @Override
    public void postgenerateTodoKeywords() {
        keywordService.deleteKeywordByExample(new KeywordExample());
        Keyword keyword = new Keyword();
        keyword.setAttachType(new Todo().getType());
        TodoExample todoExample = new TodoExample();
        todoExample.or().andCompletedEqualTo(true);
        todoExample.or().andStatusEqualTo(IterationItemStatus.CLOSED.getValue());
        List<Todo> completedTodos = todoMapper.selectByExample(todoExample);
        int size = completedTodos.size();
        logger.info("start generate keyword, total count: " + size);
        int hasFinished = 0;
        if (completedTodos != null) {
            for (Todo completedTodo : completedTodos) {
                keywordService.generateOrUpdateKeywordsByIdentifiable(completedTodo);
                if (completedTodo.getAssigneeId() != null) {
                    keywordService.addKeywordToUser(completedTodo, completedTodo.getAssigneeId());
                }
                logger.info(String.format("finish generating todo:%d, has finished %d/%d", completedTodo.getId(), ++hasFinished,
                        size));
            }
        }

    }

    @Override
    public Todo getTodoByProjectIdAndProjectTodoId(int projectId, int projectTodoId) {
        Todo sample = new Todo();
        sample.setProjectId(projectId);
        sample.setProjectTodoId(projectTodoId);
        List<Todo> todos = todoMapper.selectByExample(new TodoExample(sample));
        if (todos.isEmpty()) {
            return null;
        }
        assert todos.size() == 1;
        return todos.get(0);
    }

    @Override
    public Todo getTodoByIdWithCommentAndSubscriable(int id) {
        Todo todo = todoMapper.selectByPrimaryKey(id);

        if (todo == null) {
            return todo;
        }
        Todo newTodo = new Todo(todo);
        newTodo.setTodolist(todolistMapper.selectByPrimaryKey(todo.getTodolistId()));
        commentService.fillCommentable(newTodo, 0, DEFAULT_LIMIT);
        subscriberService.fillSubcribers(newTodo);

        return newTodo;
    }

    @Override
    public List<Todo> getTodosByTodoListWithoutComments(int todolistId) {
        Todo todo = new Todo();
        todo.setDeleted(false);
        todo.setTodolistId(todolistId);

        TodoExample unClosetodoExample = new TodoExample(todo);
        // 未关闭任务按顺序排序
        unClosetodoExample.setOrderByClause("position");
        unClosetodoExample.getOredCriteria().get(0).andStatusNotEqualTo(IterationItemStatus.CLOSED.getValue());
        List<Todo> todos = todoMapper.selectByExample(unClosetodoExample);
        TodoExample closetodoExample = new TodoExample(todo);
        // 已完成任务按完成时间排序
        closetodoExample.setOrderByClause("updated");
        closetodoExample.getOredCriteria().get(0).andStatusEqualTo(IterationItemStatus.CLOSED.getValue());
        List<Todo> result = Lists.newArrayList(todos);
        result.addAll(todoMapper.selectByExample(closetodoExample));
        return result;
    }

    /**
     * whether need to fill commentable?
     */
    @Override
    public List<Todo> getTodosByTodoList(int todolistId) {
        List<Todo> todos = getTodosByTodoListWithoutComments(todolistId);
        List<Todo> newTodos = new ArrayList<Todo>();
        for (Todo t : todos) {
            Todo newTodo = new Todo(t);
            commentService.fillCommentable(newTodo, 0, DEFAULT_LIMIT);
            newTodo.setTodolist(todolistMapper.selectByPrimaryKey(t.getTodolistId()));
            newTodos.add(newTodo);
        }
        return todos;
    }

    @Override
    public List<Todo> getTodosByTodoListWithDiscard(int todolistId) {

        Todo todo = new Todo();
        todo.setDeleted(true);
        todo.setTodolistId(todolistId);

        TodoExample todoExample = new TodoExample(todo);
        todoExample.setOrderByClause("position");

        return todoMapper.selectByExample(todoExample);
    }

    @Override
    public Todo create(Todo todo) {
        Date now = new Date();
        todo.setCreated(now);
        todo.setUpdated(now);
        todo.setDeleted(false);
        todo.setCompleted(false);
        todo.setStatus(IterationItemStatus.TODO.getValue());
        todo.setDoing(false);
        todo.setProjectTodoId(idInProjectService.getNextIdByProjectId(todo.getProjectId()));
        todo.setCreatorAvatar(sessionService.getCurrentUser().getAvatar());
        todoMapper.insert(todo);
        if (todo.getAssigneeId() != null) {
            todo.setSubscribers(new ArrayList<User>(Arrays.asList(userService.getById(todo.getAssigneeId()))));
        }
        if (todo.getDueDate() != null) {
            DateTime dt = new DateTime(todo.getDueDate());
            todo.setDueDate(dt.withTimeAtStartOfDay().plusDays(1).plusSeconds(-1).toDate());
        }
        subscriberService.generateSubscribers(todo, userService.getById(todo.getCreatorId()));
        subscriberService.addSubscribers(todo);
        keywordService.generateOrUpdateKeywordsByIdentifiable(todo);
        return todo;
    }

    /**
     * 
     * @author Chenlong
     * @return
     */
    @Override
    public Todo copyTodo(Todo todo, Integer projectId, Integer todolistId) {
        Todo copyTodo = new Todo(todo);
        todo.setProjectId(projectId);
        todo.setTodolistId(todolistId);
        if (todo.getAssigneeId() != null) {
            if (!userService.isUserInProject(todo.getAssigneeId(), todo.getCompanyId(), projectId)) {
                todo.setAssigneeId(null);
                todo.setDueDate(null);
            }
        }
        todo.setProjectTodoId(idInProjectService.getNextIdByProjectId(projectId));
        todoMapper.insert(todo);
        // copy todo subscribers
        todo.setSubscribers(userService.filterProjectMembers(todo.getSubscribers(), projectId));
        // subscriberService.generateSubscribers(todo,
        // sessionService.getCurrentUser());
        subscriberService.addSubscribers(todo);
        // copy comments
        commentService.copyComments(copyTodo, todo);
        keywordService.generateOrUpdateKeywordsByIdentifiable(todo);
        return todo;
    }

    /**
     * 
     * @author Chenlong
     * @return
     */
    private Todo relocateTodo(Todo todo, int projectId) {
        Todo example = new Todo();
        example.setId(todo.getId());
        example.setProjectId(projectId);
        example.setUpdated(new Date());
        // keep assignee info, otherwise should update every field
        todoMapper.updateByPrimaryKeySelective(example);
        // update subscribers
        example.setSubscribers(userService.filterProjectMembers(todo.getSubscribers(), projectId));
        // subscriberService.generateSubscribers(example,
        // sessionService.getCurrentUser());
        subscriberService.updateSubscribers(example);
        // relocate comments
        commentService.relocateComment(todo, projectId);
        // give to activity recoder updated object
        todo.setProjectId(projectId);
        keywordService.generateOrUpdateKeywordsByIdentifiable(todo);
        return todo;
    }

    @Override
    public Todo updateSelective(Todo todo) {
        Todo srcTodo = getById(todo.getId());
        keywordService.deleteKeywordsByIdentifiable(srcTodo);
        logger.info("Updating todo, projectId: " + todo.getProjectId());
        if (todo.getProjectId() != null && !srcTodo.getProjectId().equals(todo.getProjectId())) {
            logger.info("relocate todo " + todo.getContent() + " to another project");
            return relocateTodo(getTodoByIdWithCommentAndSubscriable(todo.getId()), todo.getProjectId());
        }
        todo.setUpdated(new Date());
        if (todo.getStatus() != null && !srcTodo.getStatus().equals(todo.getStatus())) {
            if (todo.getStatus().equals(IterationItemStatus.INPROGESS.getValue())) {
                todo.setStartTime(todo.getUpdated());
            }
            if (todo.getStatus().equals(IterationItemStatus.CLOSED.getValue())) {
                todo.setCompleteTime(todo.getUpdated());
                if (srcTodo.getAssigneeId() != null) {
                    todo.setCompleterId(srcTodo.getAssigneeId());
                } else {
                    if (sessionService.getCurrentUser() != null) {
                        todo.setCompleterId(sessionService.getCurrentUser().getId());
                    }
                }
            }
        }
        // TODO : fix for index writer, todo must get id and projectId
        todo.setProjectId(srcTodo.getProjectId());
        todoMapper.updateByPrimaryKeySelective(todo);

        if (todo.getDeleted() != null && todo.getDeleted()) {
            topicService.discardTopcicByTypeAndId(todo.getType(), todo.getId());
        } else if (todo.getDeleted() != null && srcTodo.getDeleted() != null && srcTodo.getDeleted()) {
            topicService.recoverTopcicByTypeAndId(todo.getType(), todo.getId());
        }

        keywordService.generateOrUpdateKeywordsByIdentifiable(todo);
        return todo;
    }

    @Override
    public void deleteFromTrash(int id) {
        commentService.deleteCommentByAttachTypeAndId(new Todo().getType(), id);
        todoMapper.deleteByPrimaryKey(id);
        Todo todo = new Todo();
        todo.setId(id);
        keywordService.deleteKeywordsByIdentifiable(todo);
    }

    @Override
    public void delete(int id) {
        Todo todo = new Todo(id, true);
        updateSelective(todo);
    }

    @Override
    public void recover(int id) {
        Todo todo = new Todo(id, false);
        updateSelective(todo);
    }

    @Override
    public void moveTodo(Todo todo, int todolistId) {
        Todo example = new Todo(todo.getId());
        example.setTodolistId(todolistId);
        updateSelective(example);
    }

    @Override
    public List<Todo> getByTimeRangeByCompany(Date startTime, Date endTime, int companyId, int userId) {
        Todo todo = new Todo(false);
        TodoExample example = new TodoExample(todo);
        example.getOredCriteria().get(0).andStatusNotEqualTo(IterationItemStatus.CLOSED.getValue());
        example.setOrderByClause("projectId desc");

        Criteria criteria = example.getOredCriteria().get(0);
        criteria.andDueDateBetween(startTime, endTime);
        List<Integer> projectIdList = projectService.getActiveProjectIdListByUserByCompany(userId, companyId, 0, -1);
        if (projectIdList != null && projectIdList.size() > 0) {
            criteria.andProjectIdIn(projectIdList);
        } else {
            return new ArrayList<Todo>();
        }
        List<Todo> todos = todoMapper.selectByExample(example);
        List<Todo> ret = new ArrayList<Todo>();
        for (Todo origin : todos) {
            Todo t = new Todo(origin);
            if (t.getAssigneeId() != null) {
                t.setAssignee(userService.getById(t.getAssigneeId()));
            }
            if (t.getTodolistId() != null) {
                t.setTodolist(todolistMapper.selectByPrimaryKey(t.getTodolistId()));
            }
            if (t.getProjectId() != null) {
                t.setProject(projectService.getById(t.getProjectId()));
            }
            ret.add(t);
        }
        return ret;
    }

    private Map<Date, List<Todo>> getTodosGroupByDate(List<Todo> todos) {
        Map<Date, List<Todo>> map = new TreeMap<Date, List<Todo>>();
        for (Todo todo : todos) {
            Date d = new DateTime(todo.getUpdated()).withTimeAtStartOfDay().toDate();
            if (!map.containsKey(d)) {
                List<Todo> list = Lists.newArrayList();
                list.add(todo);
                map.put(d, list);
            } else {
                map.get(d).add(todo);
            }
        }

        return map;
    }

    private TreeMap<Date, List<Todolist>> getTodolistsGroupByDate(List<Todo> todos) {
        Map<Date, List<Todo>> dateMap = getTodosGroupByDate(todos);
        TreeMap<Date, List<Todolist>> ret = new TreeMap<Date, List<Todolist>>(new Comparator<Date>() {

            @Override
            public int compare(Date o1, Date o2) {
                return o2.compareTo(o1);
            }
        });

        for (Date d : dateMap.keySet()) {
            ret.put(d, todolistService.getTodolistAccordingToTodos(dateMap.get(d)));
        }

        return ret;
    }

    @Override
    public int getCompletedTodosCountByProject(int projectId) {
        Todo sample = new Todo(false);
        sample.setProjectId(projectId);
        sample.setStatus(IterationItemStatus.CLOSED.getValue());

        return todoMapper.countByExample(new TodoExample(sample));
    }

    private List<Todo> appendTodosOfLastDay(List<Todo> todos, Date until, TodoExample example) {
        if (todos != null && todos.size() > 0) {
            Todo lastTodo = todos.get(todos.size() - 1);
            Date newUntil = new DateTime(lastTodo.getUpdated()).withTimeAtStartOfDay().toDate();
            example.getOredCriteria().get(0).andUpdatedBetween(newUntil, until);
            todos = todoMapper.selectByExample(example);
        }

        return todos;
    }

    @Override
    public TreeMap<Date, List<Todolist>> getCompletedTodolistGroupByDateByProject(int projectId, Date until, int limit) {
        Todo sample = new Todo(false);
        sample.setProjectId(projectId);
        sample.setStatus(IterationItemStatus.CLOSED.getValue());
        TodoExample example = new TodoExample(sample);
        example.getOredCriteria().get(0).andUpdatedLessThanOrEqualTo(until);
        example.setOrderByClause("updated desc");
        example.setLimit(limit);
        List<Todo> originTodos = todoMapper.selectByExample(example);
        if (originTodos == null || originTodos.size() == 0) {
            return null;
        }
        List<Todo> todos = appendTodosOfLastDay(originTodos, until, example);

        return getTodolistsGroupByDate(todos);
    }

    @Override
    public TreeMap<Date, List<Todolist>> getCompletedTodolistsGroupByDateByUser(int companyId, int userId,
            List<Integer> projectList, Date until, int limit) {
        Todo sample = new Todo(false);
        sample.setAssigneeId(userId);
        sample.setStatus(IterationItemStatus.CLOSED.getValue());
        sample.setCompanyId(companyId);
        TodoExample example = new TodoExample(sample);
        if (projectList != null) {
            if (projectList.size() > 0) {
                example.getOredCriteria().get(0).andProjectIdIn(projectList);
            } else {
                return new TreeMap<Date, List<Todolist>>();
            }
        }
        example.getOredCriteria().get(0).andUpdatedLessThanOrEqualTo(until);
        example.setLimit(limit);
        example.setOrderByClause("updated desc");
        List<Todo> originTodos = todoMapper.selectByExample(example);
        if (originTodos == null || originTodos.size() == 0) {
            return new TreeMap<Date, List<Todolist>>();
        }
        List<Todo> todos = appendTodosOfLastDay(originTodos, until, example);

        return getTodolistsGroupByDate(todos);
    }

    @Override
    public Todo updateTodoAssigneeAndDueDate(Todo todo) {
        Todo original = new Todo(todoMapper.selectByPrimaryKey(todo.getId()));
        if (todo.getDueDate() != null) {
            DateTime dt = new DateTime(todo.getDueDate());
            original.setDueDate(dt.withTimeAtStartOfDay().plusDays(1).plusSeconds(-1).toDate());
        } else {
            original.setDueDate(null);
        }
        original.setAssigneeId(todo.getAssigneeId());
        // 如果todo的content不为空，说明这是由日历中对todo的操作传过来的，也要更新content，如果为空，说明是苟哥的交互产生的，不需要更新content
        if (todo.getContent() != null) {
            original.setContent(todo.getContent());
        }
        todoMapper.updateByPrimaryKey(original);
        if (todo.getAssigneeId() != null) {
            Subscriber subscriber = new Subscriber();
            subscriber.setCompanyId(original.getCompanyId());
            subscriber.setSubscribeId(original.getId());
            subscriber.setSubscribeType(todo.getType());
            subscriber.setUserId(todo.getAssigneeId());
            subscriberService.createSubscriber(subscriber);
        }
        return todo;

    }

    @Override
    public List<Todolist> getOpenTodosByUser(Integer userId, List<Integer> projectList) {
        Todo sample = new Todo(false);
        sample.setAssigneeId(userId);
        TodoExample example = new TodoExample(sample);
        example.getOredCriteria().get(0).andStatusNotEqualTo(IterationItemStatus.CLOSED.getValue());

        if (projectList != null) {
            if (projectList.size() > 0) {
                example.getOredCriteria().get(0).andProjectIdIn(projectList);
            } else {
                return new ArrayList<Todolist>();
            }
        }

        List<Todo> todos = todoMapper.selectByExample(example);

        return todolistService.getTodolistAccordingToTodos(todos);
    }

    @Override
    public List<Todo> getByTimeRange(Date startTime, Date endTime) {
        Todo todo = new Todo(false);
        TodoExample example = new TodoExample(todo);
        example.getOredCriteria().get(0).andStatusNotEqualTo(IterationItemStatus.CLOSED.getValue());
        Criteria criteria = example.getOredCriteria().get(0);

        if (criteria == null) {
            example.or().andDueDateBetween(startTime, endTime);
        } else {
            criteria.andDueDateBetween(startTime, endTime);
        }
        return todoMapper.selectByExample(example);
    }

    @Override
    public List<Todo> getCompletedTodoByTimeRangeProject(Date start, Date end, int projectId) {
        DateTime dt = new DateTime(start);
        start = dt.withTimeAtStartOfDay().toDate();
        if (end == null) {
            dt = new DateTime(new Date());
        } else {
            dt = new DateTime(end);
        }
        end = dt.withTimeAtStartOfDay().plusDays(1).toDate();

        Todo todo = new Todo(false);
        todo.setProjectId(projectId);
        todo.setStatus(IterationItemStatus.CLOSED.getValue());
        TodoExample example = new TodoExample(todo);
        Criteria criteria = example.getOredCriteria().get(0);

        if (criteria == null) {
            example.or().andUpdatedBetween(start, end);
        } else {
            criteria.andUpdatedBetween(start, end);
        }
        return todoMapper.selectByExample(example);
    }

    @Override
    public List<Todo> getDeletedTodosByTodoList(int todolistId) {
        Todo todo = new Todo();
        todo.setDeleted(true);
        todo.setTodolistId(todolistId);

        TodoExample todoExample = new TodoExample(todo);

        return todoMapper.selectByExample(todoExample);
    }

    @Override
    public List<Todo> getOpenTodosByTodoListWithoutCommentsAndSubscribers(int todolistId) {
        Todo todo = new Todo();
        todo.setDeleted(false);
        todo.setTodolistId(todolistId);

        TodoExample todoExample = new TodoExample(todo);
        todoExample.getOredCriteria().get(0).andStatusNotEqualTo(IterationItemStatus.CLOSED.getValue());
        todoExample.setOrderByClause("position");

        return todoMapper.selectByExample(todoExample);
    }

    @Override
    public List<Todo> getUncompletedTodoByProject(int projectId) {
        Todo todo = new Todo();
        todo.setProjectId(projectId);

        TodoExample todoExample = new TodoExample(todo);
        todoExample.getOredCriteria().get(0).andStatusNotEqualTo(IterationItemStatus.CLOSED.getValue());
        todoExample.setOrderByClause("projectId desc");

        return todoMapper.selectByExample(todoExample);
    }

    @Override
    public AttachTodo attachToTodo(String attachType, int attachId, int todoId) {
        if (isAttachTodoRecorded(attachType, attachId, todoId)) {
            return null;
        }
        AttachTodo attachTodo = new AttachTodo();
        attachTodo.setAttachId(attachId);
        attachTodo.setAttachType(attachType);
        attachTodo.setTodoId(todoId);
        attachTodoMapper.insert(attachTodo);
        return attachTodo;
    }

    @Override
    public void removeAttachToTodo(String attachType, int attachId, int todoId) {
        AttachTodo attachTodo = new AttachTodo();
        attachTodo.setAttachId(attachId);
        attachTodo.setAttachType(attachType);
        attachTodo.setTodoId(todoId);
        attachTodoMapper.deleteByExample(new AttachTodoExample(attachTodo));
    }

    @Override
    public boolean isAttachTodoRecorded(String attachType, int attachId, int todoId) {
        AttachTodo attachTodo = new AttachTodo();
        attachTodo.setAttachId(attachId);
        attachTodo.setAttachType(attachType);
        attachTodo.setTodoId(todoId);
        return !attachTodoMapper.selectByExample(new AttachTodoExample(attachTodo)).isEmpty();
    }

    @Override
    public List<BaseOperateItem> getAttachesByTodoId(int todoId, String attachType) {
        AttachTodo sample = new AttachTodo();
        sample.setTodoId(todoId);
        if (!attachType.equals(IdentifiableOperator.NONE_TYPE)) {
            sample.setAttachType(attachType);
        }
        List<AttachTodo> attachTodos = attachTodoMapper.selectByExample(new AttachTodoExample(sample));
        List<BaseOperateItem> result = new ArrayList<BaseOperateItem>();
        for (AttachTodo attachTodo : attachTodos) {
            result.add(identifiableManager.getIdentifiableByTypeAndId(attachTodo.getAttachType(), attachTodo.getAttachId()));
        }
        return result;
    }

    @Override
    public List<Todo> getTodosByAttachTypeAndId(String attachType, int attachId) {
        AttachTodo sample = new AttachTodo();
        sample.setAttachId(attachId);
        sample.setAttachType(attachType);
        List<AttachTodo> attachTodos = attachTodoMapper.selectByExample(new AttachTodoExample(sample));
        List<Todo> result = new ArrayList<Todo>();
        for (AttachTodo attachTodo : attachTodos) {
            result.add(getById(attachTodo.getTodoId()));
        }
        return result;
    }

    @Override
    public void associateTodos(Integer fromTodoId, Integer toTodoId) {
        attachToTodo(new Todo().getType(), fromTodoId, toTodoId);
    }

    @Override
    public List<Todolist> getAssociateTodosByType(Integer todoId, String todoType) {
        Set<Todo> result = Sets.newHashSet();
        List<BaseOperateItem> fromTodos = getAttachesByTodoId(todoId, new Todo().getType());
        for (BaseOperateItem fromTodo : fromTodos) {
            Todo todo = (Todo) fromTodo;
            if (todoType.equals(TodoType.NONE.getValue()) || todo.getTodoType().equals(todoType)) {
                result.add(todo);
            }
        }
        List<Todo> toTodos = getTodosByAttachTypeAndId(new Todo().getType(), todoId);
        for (Todo todo : toTodos) {
            if (todoType.equals(TodoType.NONE.getValue()) || todo.getTodoType().equals(todoType)) {
                result.add(todo);
            }
        }
        return todolistService.getTodolistAccordingToTodos(Lists.newArrayList(result));
    }

    private Integer countByUserAndCompletedTime(Integer companyId, Integer userId, Date since, Date until) {
        Todo sample = new Todo();
        sample.setCompanyId(companyId);
        sample.setAssigneeId(userId);
        sample.setCompleted(true);
        TodoExample example = new TodoExample(sample);

        example.getOredCriteria().get(0).andCreatedGreaterThanOrEqualTo(since).andCreatedLessThan(until);
        example.setOrderByClause("id desc");
        return todoMapper.countByExample(example);
    }

    @Override
    public Map<Integer, Integer> countForCompanyUsers(Integer companyId, Date since, Date until) {
        List<User> companyUsers = userService.getUserByCompanyId(companyId);
        Map<Integer, Integer> countMap = Maps.newHashMap();
        for (User user : companyUsers) {
            countMap.put(user.getId(), countByUserAndCompletedTime(companyId, user.getId(), since, until));
        }
        return countMap;
    }

    @Override
    public List<Todo> getCompletedTodosBetweenDates(Integer companyId, Date since, Date until) {
        Todo sample = new Todo();
        sample.setCompanyId(companyId);
        sample.setStatus("closed");
        TodoExample example = new TodoExample(sample);

        DateTime dt = new DateTime(since);
        since = dt.withTimeAtStartOfDay().toDate();
        dt = new DateTime(until);
        until = dt.withTimeAtStartOfDay().plusDays(1).toDate();

        example.getOredCriteria().get(0).andUpdatedGreaterThanOrEqualTo(since).andUpdatedLessThan(until);
        List<Todo> results = todoMapper.selectByExample(example);

        for (Todo todo : results) {
            todo.setProject(projectService.getById(todo.getProjectId()));
        }
        return results;
    }

    @Override
    public ProjectItem getItemByIdInProject(Integer projectId, Integer idInProject) {
        return getTodoByProjectIdAndProjectTodoId(projectId, idInProject);
    }

    @Override
    public List<Todo> getOpenTodosBetweenDatesByUser(Integer companyId, Integer userId, Date since, Date until) {
        Todo sample = new Todo();
        sample.setCompanyId(companyId);
        sample.setAssigneeId(userId);
        TodoExample example = new TodoExample(sample);
        example.getOredCriteria().get(0).andDueDateGreaterThanOrEqualTo(since).andDueDateLessThan(until);
        example.getOredCriteria().get(0).andStatusNotEqualTo(IterationItemStatus.CLOSED.getValue());
        List<Todo> results = todoMapper.selectByExample(example);
        return results;
    }

    @Override
    protected BaseMapper<Todo, TodoExample> getBaseMapper() {
        return todoMapper;
    }

    @Override
    public Todo newItem() {
        return new Todo();
    }

    @Override
    public TodoExample newExample() {
        return new TodoExample();
    }

    @Override
    public TodoExample newExample(Todo item) {
        return new TodoExample(item);
    }
}
