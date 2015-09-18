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

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.elevenframework.web.interceptor.Interceptors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.google.common.collect.Lists;
import com.onboard.domain.model.Project;
import com.onboard.domain.model.Todolist;
import com.onboard.domain.transform.TodolistTransform;
import com.onboard.dto.TodolistDTO;
import com.onboard.service.account.UserService;
import com.onboard.service.collaboration.ProjectService;
import com.onboard.service.collaboration.TodolistService;
import com.onboard.service.security.interceptors.ProjectMemberRequired;
import com.onboard.service.security.interceptors.ProjectNotArchivedRequired;
import com.onboard.service.web.SessionService;
import com.onboard.web.api.form.TodolistForm;

@RequestMapping(value = "/{companyId}/projects/{projectId}/todolists")
@Controller
public class TodolistApiController {

    @Autowired
    private TodolistService todolistService;

    @Autowired
    private UserService userService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private SessionService session;

    public static final Logger logger = LoggerFactory.getLogger(TodolistApiController.class);

    @RequestMapping(value = "", method = RequestMethod.GET)
    @Interceptors({ ProjectMemberRequired.class })
    @ResponseBody
    public List<TodolistDTO> getTodolists(@PathVariable("projectId") int projectId) {
        return Lists.transform(todolistService.getOpenTodolistsWithUncompletedTodosByProject(projectId),
                TodolistTransform.TODOLIST_DTO_TODOS_FUNCTION);
    }

    @RequestMapping(value = "/archive", method = RequestMethod.GET)
    @Interceptors({ ProjectMemberRequired.class })
    @ResponseBody
    public List<TodolistDTO> getAttriveTodolists(@PathVariable("projectId") int projectId) {
        return Lists.transform(todolistService.getAcrivedTodolists(projectId),
                TodolistTransform.TODOLIST_DTO_TODOS_FUNCTION);
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    @Interceptors({ ProjectMemberRequired.class, ProjectNotArchivedRequired.class })
    @ResponseBody
    public TodolistDTO createTodolist(@PathVariable int companyId, @PathVariable int projectId,
            @RequestBody TodolistForm form) {
        form.setCompanyId(companyId);
        form.setProjectId(projectId);
        form.setCreatorId(session.getCurrentUser().getId());
        form.setCreatorName(session.getCurrentUser().getName());
        return TodolistTransform.todolistToTodolist(todolistService.create(form));
    }

    @RequestMapping(value = "/{todolistId}", method = RequestMethod.GET)
    @Interceptors({ ProjectMemberRequired.class })
    @ResponseBody
    public TodolistDTO getTodolist(@PathVariable("todolistId") int todolistId) {
        Todolist todolist = todolistService.getTodolistWithClosedTodos(todolistId);
        return TodolistTransform.todolistToTodolistWithTodos(todolist);
    }

    @RequestMapping(value = "/{todolistId}", method = RequestMethod.PUT)
    @Interceptors({ ProjectMemberRequired.class })
    @ResponseStatus(HttpStatus.OK)
    public void updateTodolist(@PathVariable("todolistId") int todolistId, @RequestBody TodolistForm todolistForm) {
        todolistForm.setId(todolistId);
        todolistService.updateSelective(todolistForm);
    }

    @RequestMapping(value = "/{todolistId}", method = RequestMethod.DELETE)
    @Interceptors({ ProjectMemberRequired.class })
    @ResponseStatus(HttpStatus.OK)
    public void deleteTodolist(@PathVariable("todolistId") int todolistId) {
        todolistService.delete(todolistId);
    }

    @RequestMapping(value = "/{todolistId}/copy", method = RequestMethod.GET)
    @Interceptors({ ProjectMemberRequired.class })
    @ResponseBody
    public TodolistDTO copyTodolist(@PathVariable("companyId") int companyId, @PathVariable("projectId") int projectId,
            @PathVariable("todolistId") int todolistId, @RequestParam(required = true) int targetProjectId,
            HttpServletResponse response) {
        Project targetProject = projectService.getById(targetProjectId);
        if (targetProject == null) {
            response.setStatus(403);
            return null;
        }
        // firstly determine whether the user is in the target project
        int userId = session.getCurrentUser().getId();
        boolean privilege = userService.isUserInProject(userId, companyId, targetProjectId);
        // secondly determine whether the target project is archived or the
        // todolist is deleted
        boolean isDeleted = todolistService.getById(todolistId).getDeleted();
        boolean isArchived = targetProject.getArchived();
        if (!privilege || isArchived || isDeleted) {
            response.setStatus(403);
            return null;
        }
        // thirdly, proceed with copying
        else {
            Todolist todolist = todolistService.getTodolistByIdWithExtraInfo(todolistId);
            todolist = todolistService.copyTodolist(todolist, targetProjectId, false);
            int newId = todolist.getId();
            logger.info("Successfully created todolist " + newId + " in project " + targetProjectId);
            return TodolistTransform.todolistToTodolistWithTodos(todolist);
        }
    }
}
