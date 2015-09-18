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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.onboard.domain.mapper.ProjectMapper;
import com.onboard.domain.mapper.TodoMapper;
import com.onboard.domain.mapper.TodolistMapper;
import com.onboard.domain.mapper.base.BaseMapper;
import com.onboard.domain.mapper.model.TodoExample;
import com.onboard.domain.mapper.model.TodolistExample;
import com.onboard.domain.model.IterationItemStatus;
import com.onboard.domain.model.Todo;
import com.onboard.domain.model.Todolist;
import com.onboard.domain.model.User;
import com.onboard.service.account.UserService;
import com.onboard.service.base.AbstractBaseService;
import com.onboard.service.collaboration.CommentService;
import com.onboard.service.collaboration.TodoService;
import com.onboard.service.collaboration.TodolistService;
import com.onboard.service.collaboration.TopicService;
import com.onboard.service.common.subscrible.SubscriberService;
import com.onboard.service.web.SessionService;

/**
 * {@link TodolistService}接口实现
 * <p/>
 * TODO: discard和complete的逻辑全部没有用到，直接在controller层调用update方法了。影响业务逻辑的处理
 * 如果要删除一个todo，controller应该直接调用discard操作，而不是设置todo为deleted，然后update。
 * 
 * @author yewei
 */
@Transactional
@Service("todolistServiceBean")
public class TodolistServiceImpl extends AbstractBaseService<Todolist, TodolistExample> implements TodolistService {

    public static final Logger logger = LoggerFactory.getLogger(TodolistServiceImpl.class);

    public static final int DEFAULT_LIMIT = -1;

    @Autowired
    private TodolistMapper todolistMapper;

    @Autowired
    private TodoMapper todoMapper;

    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private TodoService todoService;

    @Autowired
    private SubscriberService subscriberService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private TopicService topicService;

    @Autowired
    private UserService userService;

    @Autowired
    private SessionService sessionService;

    @Override
    public List<Todolist> getTodolistsByProject(int projectId, int start, int limit) {

        Todolist todoList = new Todolist();
        todoList.setDeleted(false);
        todoList.setProjectId(projectId);
        TodolistExample todolistExample = new TodolistExample(todoList);
        todolistExample.setOrderByClause("position desc");

        return todolistMapper.selectByExample(todolistExample);

    }

    @Override
    public List<Todolist> getTodolistDetailsByProject(int projectId, int start, int limit) {
        List<Todolist> rawTodolists = getTodolistsByProject(projectId, start, limit);

        for (Todolist todolist : rawTodolists) {
            todolist.setTodos(todoService.getTodosByTodoListWithoutComments(todolist.getId()));
        }

        return rawTodolists;
    }

    @Override
    public Todolist getTodolistByIdWithExtraInfo(int id) {

        Todolist todolist = todolistMapper.selectByPrimaryKey(id);

        if (todolist == null) {
            return null;
        }

        Todolist newTodolist = new Todolist(todolist);
        newTodolist.setTodos(todoService.getTodosByTodoList(todolist.getId()));
        newTodolist.setDicardTodos(todoService.getTodosByTodoListWithDiscard(todolist.getId()));
        commentService.fillCommentable(newTodolist, 0, DEFAULT_LIMIT);
        subscriberService.fillSubcribers(newTodolist);

        return newTodolist;
    }

    @Override
    public Todolist getTodolistWithClosedTodos(Integer id) {
        Todolist todolist = todolistMapper.selectByPrimaryKey(id);
        if (todolist == null) {
            return null;
        }
        Todolist newTodolist = new Todolist(todolist);
        newTodolist.setTodos(todoService.getTodosByTodoList(todolist.getId()));
        return newTodolist;
    }

    @Override
    public List<Todolist> getTodolistAccordingToTodos(List<Todo> todos) {
        Map<Integer, Todolist> todolistMap = new HashMap<Integer, Todolist>();

        for (Todo todo : todos) {
            if (!todolistMap.containsKey(todo.getTodolistId())) {
                Todolist todolist = todolistMapper.selectByPrimaryKey(todo.getTodolistId());
                // To Avoid Cache!!!
                Todolist newTodolist = new Todolist(todolist);
                newTodolist.setProject(projectMapper.selectByPrimaryKey(newTodolist.getProjectId()));
                newTodolist.setTodos(new ArrayList<Todo>());
                newTodolist.getTodos().add(todo);
                todolistMap.put(todo.getTodolistId(), newTodolist);
            } else {
                todolistMap.get(todo.getTodolistId()).getTodos().add(todo);
            }
        }
        return new ArrayList<Todolist>(todolistMap.values());
    }

    @Override
    public List<Todolist> getOpenTodolistByUser(Integer userId, List<Integer> projectList) {
        Todo todoSample = new Todo(false);
        todoSample.setAssigneeId(userId);
        TodoExample todoExample = new TodoExample(todoSample);
        todoExample.getOredCriteria().get(0).andStatusNotEqualTo(IterationItemStatus.CLOSED.getValue());

        // Null check should be in front of the first for loop.
        if (projectList != null) {
            // 先找出归档的projectId，再将他们从projectList中删除
            List<Integer> deleteProjectList = new ArrayList<Integer>();
            for (Integer projectId : projectList) {
                if (projectMapper.selectByPrimaryKey(projectId).getArchived().booleanValue()) {
                    deleteProjectList.add(projectId);
                }
            }
            for (Integer projectId : deleteProjectList) {
                projectList.remove(projectId);
            }
            if (projectList.size() > 0) {
                todoExample.getOredCriteria().get(0).andProjectIdIn(projectList);
            } else {
                return new ArrayList<Todolist>();
            }
        }

        List<Todo> todos = todoMapper.selectByExample(todoExample);
        return getTodolistAccordingToTodos(todos);
    }

    @Override
    public List<Todolist> getCompletedTodolistByUser(int companyId, int userId, int limit, List<Integer> projectList) {
        Todo todoSample = new Todo(false);
        todoSample.setAssigneeId(userId);
        todoSample.setStatus(IterationItemStatus.CLOSED.getValue());
        todoSample.setCompanyId(companyId);

        TodoExample example = new TodoExample(todoSample);
        example.setLimit(limit);
        example.setOrderByClause("updated desc");

        if (projectList != null) {
            if (projectList.size() > 0) {
                example.getOredCriteria().get(0).andProjectIdIn(projectList);
            } else {
                return new ArrayList<Todolist>();
            }
        }

        List<Todo> todos = todoMapper.selectByExample(example);
        return getTodolistAccordingToTodos(todos);
    }

    @Override
    public Todolist create(Todolist todolist) {
        Date now = new Date();
        todolist.setCreated(now);
        todolist.setUpdated(now);
        todolist.setArchived(false);
        todolist.setDeleted(false);
        todolist.setCreatorAvatar(sessionService.getCurrentUser().getAvatar());
        todolistMapper.insert(todolist);
        subscriberService.generateSubscribers(todolist, userService.getById(todolist.getCreatorId()));
        subscriberService.addSubscribers(todolist);
        return todolist;
    }

    /**
     * @author Chenlong
     */
    @Override
    public Todolist copyTodolist(Todolist todolist, Integer targetProjectId, boolean includeCompletedTodos) {
        Todolist copyTodolist = new Todolist(todolist);
        todolist.setProjectId(targetProjectId);
        setPositionToTop(todolist);
        todolist = create(todolist);
        // set subscribers
        todolist.setSubscribers(userService.filterProjectMembers(todolist.getSubscribers(), targetProjectId));
        // subscriberService.generateSubscribers(todolist,
        // sessionService.getCurrentUser());
        subscriberService.addSubscribers(todolist);
        logger.info("Todolist " + todolist.getName() + "Created! Subscribers are: ");
        for (User user : todolist.getSubscribers()) {
            logger.info(user.getName());
        }
        // copy comments
        commentService.copyComments(copyTodolist, todolist);

        // copy todo
        for (Todo todo : todolist.getTodos()) {
            if (!includeCompletedTodos && todo.getStatus().equals(IterationItemStatus.CLOSED.getValue())) {
                continue;
            }
            todo = todoService.getTodoByIdWithCommentAndSubscriable(todo.getId());
            todo = todoService.copyTodo(todo, targetProjectId, todolist.getId());
        }
        return todolist;
    }

    /**
     * set todolist's position to be at the top
     * 
     * @param todolist
     */
    private void setPositionToTop(Todolist todolist) {
        List<Todolist> todolists = getTodolistsByProject(todolist.getProjectId(), 0, -1);
        Double pos = todolist.getPosition();
        for (Todolist t : todolists) {
            if (!t.getArchived() && t.getPosition() > pos) {
                pos = t.getPosition();
            }
        }
        todolist.setPosition(pos + 10);
    }

    /**
     * @author Chenlong
     */
    private Todolist relocateTodolist(Todolist todolist, int projectId) {
        int todolistId = todolist.getId();
        Todolist example = new Todolist();
        example.setId(todolist.getId());
        example.setProjectId(projectId);
        example.setPosition(todolist.getPosition());
        setPositionToTop(example);
        example.setUpdated(new Date());
        todolistMapper.updateByPrimaryKeySelective(example);

        // update subscribers
        example.setSubscribers(userService.filterProjectMembers(todolist.getSubscribers(), projectId));
        // subscriberService.generateSubscribers(example,
        // sessionService.getCurrentUser());
        subscriberService.updateSubscribers(example);
        logger.info("Todolist " + todolistId + ": " + todolist.getName() + "Moved! Subscribers are: ");
        for (User user : subscriberService.getSubscribeUsersByTopic(example.getType(), example.getSubscribableId())) {
            logger.info(user.getName());
        }
        // update comments
        commentService.relocateComment(todolist, projectId);

        // copy todo
        for (Todo todo : todolist.getTodos()) {
            todo.setProjectId(projectId);
            todoService.updateSelective(todo);
        }
        todolist.setProjectId(projectId);
        return todolist;
    }

    @Override
    public Todolist updateSelective(Todolist todolist) {
        logger.info("src: " + todolist.getProjectId());
        Todolist srcTodolist = getById(todolist.getId());
        if (todolist.getProjectId() != null && !srcTodolist.getProjectId().equals(todolist.getProjectId())) {
            logger.info("relocate the todolist to another project");
            return relocateTodolist(getTodolistByIdWithExtraInfo(todolist.getId()), todolist.getProjectId());
        }
        logger.info("Updating todolist...");
        todolist.setUpdated(new Date());
        // TODO:temp fix, as index writer need projectId
        todolist.setProjectId(srcTodolist.getProjectId());
        todolistMapper.updateByPrimaryKeySelective(todolist);

        /**
         * TODO temporary fix
         */
        if (todolist.getDeleted() != null && todolist.getDeleted()) {
            topicService.discardTopcicByTypeAndId(todolist.getType(), todolist.getId());

            List<Todo> todos = todoService.getTodosByTodoListWithoutComments(todolist.getId());
            for (Todo todo : todos) {
                todoService.delete(todo.getId());
            }
        } else if (todolist.getDeleted() != null && srcTodolist.getDeleted() != null && srcTodolist.getDeleted()) {
            topicService.recoverTopcicByTypeAndId(todolist.getType(), todolist.getId());

            List<Todo> todos = todoService.getDeletedTodosByTodoList(todolist.getId());
            for (Todo todo : todos) {
                todoService.recover(todo.getId());
            }
        }

        return todolist;
    }

    @Override
    public void deleteFromTrash(int id) {

        // 删除todolist下的所有现有todo
        Todo todo = new Todo();
        todo.setTodolistId(id);
        List<Todo> todos = todoMapper.selectByExample(new TodoExample(todo));
        for (Todo t : todos) {
            todoService.deleteFromTrash(t.getId());
        }

        commentService.deleteCommentByAttachTypeAndId(getModelType(), id);
        todolistMapper.deleteByPrimaryKey(id);
    }

    @Override
    public void delete(int id) {
        Todolist todolist = new Todolist(id, true);
        updateSelective(todolist);
    }

    @Override
    public void recover(int id) {
        Todolist todolist = new Todolist(id, false);
        updateSelective(todolist);
    }

    @Override
    public void moveTodolist(Todolist todolist, int projectId) {
        Todolist example = new Todolist(todolist.getId());
        example.setProjectId(projectId);
        updateSelective(example);
    }

    @Override
    public List<Todolist> getOpenTodolistsWithUncompletedTodosByProject(int projectId) {
        Todolist sample = new Todolist();
        sample.setDeleted(false);
        sample.setProjectId(projectId);
        sample.setArchived(false);
        TodolistExample todolistExample = new TodolistExample(sample);
        todolistExample.setOrderByClause("position desc");

        List<Todolist> todolists = todolistMapper.selectByExample(todolistExample);
        for (Todolist todolist : todolists) {
            todolist.setTodos(todoService.getOpenTodosByTodoListWithoutCommentsAndSubscribers(todolist.getId()));

        }
        return todolists;
    }

    @Override
    public List<Todolist> getAcrivedTodolists(Integer projectId) {
        Todolist todolist = new Todolist(false);
        todolist.setProjectId(projectId);
        todolist.setArchived(true);
        TodolistExample todolistExample = new TodolistExample(todolist);
        todolistExample.setOrderByClause("updated desc");
        return todolistMapper.selectByExample(todolistExample);
    }

    @Override
    protected BaseMapper<Todolist, TodolistExample> getBaseMapper() {
        return todolistMapper;
    }

    @Override
    public Todolist newItem() {
        return new Todolist();
    }

    @Override
    public TodolistExample newExample() {
        return new TodolistExample();
    }

    @Override
    public TodolistExample newExample(Todolist item) {
        return new TodolistExample(item);
    }

}
