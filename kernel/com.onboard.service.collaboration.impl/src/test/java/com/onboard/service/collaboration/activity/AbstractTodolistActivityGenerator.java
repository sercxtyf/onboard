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

import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.onboard.domain.model.Project;
import com.onboard.domain.model.Todo;
import com.onboard.domain.model.Todolist;
import com.onboard.service.account.UserService;
import com.onboard.service.collaboration.ProjectService;
import com.onboard.service.collaboration.TodolistService;
import com.onboard.service.web.SessionService;
import com.onboard.test.moduleutils.ModuleHelper;

@RunWith(MockitoJUnitRunner.class)
public abstract class AbstractTodolistActivityGenerator {
    @Mock
    protected TodolistService mockedTodolistService;

    protected Todolist todolist, todolistWithTodos, todolistArchived, todolistDeleted, todolistMoved, todolistName,
            todolistDescription, todolistNameAndDescription;

    @Mock
    protected ProjectService projectService;
    @Mock
    protected SessionService sessionService;
    @Mock
    protected UserService userService;

    protected Project project;

    @Mock
    protected ActivityRecorderHelper activityRecorderHelper;

    @Before
    public void setupTest() {
        todolist = getASampleTodolist();
        todolistMoved = getASampleTodolist();
        todolistMoved.setProjectId(ModuleHelper.projectId + 1);
        todolistWithTodos = getASampleTodolist();
        todolistWithTodos.setTodos(getAListOfSampleTodos());
        todolistDeleted = getASampleTodolist();
        todolistDeleted.setDeleted(true);
        todolistArchived = getASampleTodolist();
        todolistArchived.setArchived(true);
        todolistName = getASampleTodolist();
        todolistName.setName(ModuleHelper.name + "1");
        todolistDescription = getASampleTodolist();
        todolistDescription.setDescription(ModuleHelper.description + "1");
        todolistNameAndDescription = getASampleTodolist();
        todolistNameAndDescription.setName(ModuleHelper.name + "1");
        todolistNameAndDescription.setDescription(ModuleHelper.description + "1");

        project = ModuleHelper.getASampleProject();
        when(projectService.getById(anyInt())).thenReturn(project);
        when(sessionService.getCurrentUser()).thenReturn(ModuleHelper.getASampleUser());

        ActivityRecorderHelper activityRecorderHelper = new ActivityRecorderHelper();
        activityRecorderHelper.setProjectService(projectService);
        activityRecorderHelper.setSession(sessionService);
        activityRecorderHelper.setUserService(userService);

        initTodolistService();
    }

    /** initTodolistService **/
    private void initTodolistService() {
        when(mockedTodolistService.getById(anyInt())).thenReturn(todolist);
    }

    private Todolist getASampleTodolist() {
        Todolist tl = new Todolist();
        tl.setId(ModuleHelper.todolistId);
        tl.setProjectId(ModuleHelper.projectId);
        tl.setCompanyId(ModuleHelper.companyId);
        tl.setCreatorId(ModuleHelper.creatorId);
        tl.setCreatorName(ModuleHelper.creatorName);
        tl.setArchived(false);
        tl.setDeleted(false);
        return tl;
    }

    private List<Todo> getAListOfSampleTodos() {
        List<Todo> list = new ArrayList<Todo>();
        list.add(ModuleHelper.getASampleTodo());
        list.add(ModuleHelper.getASampleTodo());
        return list;
    }
}
