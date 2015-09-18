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
package com.onboard.web.api.todo;

import java.util.Date;
import java.util.List;

import javax.validation.Valid;

import org.elevenframework.web.interceptor.Interceptors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.google.common.collect.Lists;
import com.onboard.domain.model.AttachTodo;
import com.onboard.domain.model.Todo;
import com.onboard.domain.transform.TodoTransform;
import com.onboard.domain.transform.TodolistTransform;
import com.onboard.dto.TodoDTO;
import com.onboard.dto.TodolistDTO;
import com.onboard.service.collaboration.KeywordService;
import com.onboard.service.collaboration.TodoService;
import com.onboard.service.security.interceptors.ProjectMemberRequired;
import com.onboard.service.security.interceptors.ProjectNotArchivedRequired;
import com.onboard.service.web.SessionService;
import com.onboard.web.api.exception.ResourceNotFoundException;
import com.onboard.web.api.form.TodoForm;
import com.onboard.web.api.form.UpdateAttachTodoForm;

@RequestMapping(value = "/{companyId}/projects/{projectId}/todos")
@Controller
public class TodoApiController {

    public static final Logger logger = LoggerFactory.getLogger(TodoApiController.class);

    @Autowired
    private TodoService todoService;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private LocalValidatorFactoryBean validator;

    @Autowired
    private KeywordService keywordService;

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.setValidator(validator);
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    @Interceptors({ ProjectMemberRequired.class, ProjectNotArchivedRequired.class })
    @ResponseBody
    public TodoDTO createTodo(@PathVariable int companyId, @PathVariable int projectId, @RequestBody TodoForm form) {
        form.setCompanyId(companyId);
        form.setProjectId(projectId);
        form.setCreatorId(sessionService.getCurrentUser().getId());
        form.setCreatorName(sessionService.getCurrentUser().getName());
        return TodoTransform.todoToTodoDTO(todoService.create(form));
    }

    @RequestMapping(value = "/completed", method = RequestMethod.GET)
    @Interceptors({ ProjectMemberRequired.class })
    @ResponseBody
    public List<TodoDTO> getCompletedTodosByProjectAndTime(@PathVariable int projectId,
            @RequestParam(value = "start", required = true) Long start, @RequestParam(value = "end", required = true) Long end) {
        Date since = new Date(start);
        Date until = new Date(end);
        // Date limit = new DateTime(until).plusMonths(-6).toDate();
        logger.info("searching completed todos, start: " + since.toString() + ", end: " + until.toString());
        if (since.after(until)) {
            return Lists.newArrayList();
        }
        List<Todo> todos = todoService.getCompletedTodoByTimeRangeProject(since, until, projectId);
        logger.info("Found completed todos: " + todos.size());
        return Lists.transform(todos, TodoTransform.TODO_DTO_FUNCTION);
    }

    @RequestMapping(value = "/{todoId}", method = RequestMethod.GET)
    @Interceptors({ ProjectMemberRequired.class })
    @ResponseBody
    public TodoDTO getTodoById(@PathVariable("todoId") Integer todoId) {
        Todo todo = todoService.getTodoByIdWithCommentAndSubscriable(todoId);
        return TodoTransform.todoToTodoDTOWithComments(todo);
    }

    @RequestMapping(value = "/{todoId}", method = RequestMethod.PUT)
    @Interceptors({ ProjectMemberRequired.class, ProjectNotArchivedRequired.class })
    @ResponseStatus(HttpStatus.OK)
    public void updateTodo(@PathVariable int projectId, @PathVariable int companyId, @PathVariable("todoId") Integer todoId,
            @Valid @RequestBody TodoForm todo) {
        todo.setId(todoId);
        todo.setProjectId(projectId);
        todo.setCompanyId(companyId);
        if (todo.isSelective()) {
            todoService.updateSelective(todo);
        } else {
            todoService.updateTodoAssigneeAndDueDate(todo);
        }
    }

    @RequestMapping(value = "/{todoId}", method = RequestMethod.DELETE)
    @Interceptors({ ProjectNotArchivedRequired.class })
    @ResponseStatus(HttpStatus.OK)
    public void deleteTodoById(@PathVariable("todoId") Integer todoId) {
        todoService.delete(todoId);
    }

    @RequestMapping(value = "/{todoId}/keywords", method = RequestMethod.GET)
    @ResponseBody
    @Interceptors({ ProjectMemberRequired.class })
    public List<String> getTodoKeywords(@PathVariable("todoId") int todoId) {
        Todo todo = todoService.getById(todoId);
        if (todo == null) {
            throw new ResourceNotFoundException();
        }
        return keywordService.getKeywordsByText(todo.generateText());
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    @Interceptors({ ProjectMemberRequired.class })
    @ResponseBody
    public List<TodolistDTO> getTodosByAttachTypeAndId(@RequestParam(value = "attachType", required = true) String attachType,
            @RequestParam(value = "attachId", required = true) int attachId,
            @RequestParam(value = "type", required = false, defaultValue = "none") String type) {
        // 目前只支持绑定todo
        if (!attachType.equals(new Todo().getType())) {
            return Lists.newArrayList();
        }
        return Lists
                .transform(todoService.getAssociateTodosByType(attachId, type), TodolistTransform.TODOLIST_DTO_TODOS_FUNCTION);
    }

    @RequestMapping(value = "/attach", method = RequestMethod.DELETE)
    @Interceptors({ ProjectNotArchivedRequired.class })
    @ResponseStatus(HttpStatus.OK)
    public void deleteAttachTodos(@RequestParam(value = "attachType", required = true) String attachType,
            @RequestParam(value = "attachId", required = true) Integer attachId,
            @RequestParam(value = "todoId", required = true) Integer todoId) {
        todoService.removeAttachToTodo(attachType, attachId, todoId);
    }

    @RequestMapping(value = "/attach", method = RequestMethod.POST)
    @Interceptors({ ProjectNotArchivedRequired.class })
    @ResponseStatus(HttpStatus.OK)
    public void createAttachTodos(@Valid @RequestBody AttachTodo attachTodo) {
        todoService.attachToTodo(attachTodo.getAttachType(), attachTodo.getAttachId(), attachTodo.getTodoId());
    }

    @RequestMapping(value = "/attach", method = RequestMethod.PUT)
    @Interceptors({ ProjectNotArchivedRequired.class })
    @ResponseStatus(HttpStatus.OK)
    public void updateIterationTodo(@RequestBody UpdateAttachTodoForm updateIterationTodoMap) {
        for (AttachTodo attachTodo : updateIterationTodoMap.getAdd()) {
            todoService.attachToTodo(attachTodo.getAttachType(), attachTodo.getAttachId(), attachTodo.getTodoId());
        }
        for (AttachTodo attachTodo : updateIterationTodoMap.getRemove()) {
            // 两种关联都要删除
            todoService.removeAttachToTodo(new Todo().getType(), attachTodo.getAttachId(), attachTodo.getTodoId());
            todoService.removeAttachToTodo(new Todo().getType(), attachTodo.getTodoId(), attachTodo.getAttachId());
        }
    }

    /**
     * @author Chenlong
     * @param projectId
     * @return
     */
    @RequestMapping(value = "/projectTodo/{projectTodoId}", method = RequestMethod.GET)
    @Interceptors({ ProjectMemberRequired.class })
    @ResponseBody
    public TodoDTO getTodoByProjectId(@PathVariable int projectId, @PathVariable int projectTodoId) {
        Todo todo = todoService.getTodoByProjectIdAndProjectTodoId(projectId, projectTodoId);
        if (todo != null) {
            return TodoTransform.todoToTodoDTO(todo);
        } else {
            return null;
        }
    }
}
