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
package com.onboard.service.sampleProject.impl.test;

import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.Lists;
import com.onboard.domain.model.Discussion;
import com.onboard.domain.model.Project;
import com.onboard.domain.model.Todo;
import com.onboard.domain.model.Todolist;
import com.onboard.dto.ProjectDTO;
import com.onboard.service.collaboration.AttachmentService;
import com.onboard.service.collaboration.DiscussionService;
import com.onboard.service.collaboration.ProjectService;
import com.onboard.service.collaboration.TodoService;
import com.onboard.service.collaboration.TodolistService;
import com.onboard.service.upload.UploadService;
import com.onboard.test.moduleutils.ModuleHelper;

@RunWith(MockitoJUnitRunner.class)
public abstract class AbstractSampleProjectTest {

    @Mock
    protected ProjectService mockedProjectService;

    @Mock
    protected DiscussionService mockedDiscussionService;

    @Mock
    protected TodolistService mockedTodolistService;

    @Mock
    protected TodoService mockedTodoService;

    @Mock
    protected UploadService mockedUploadService;

    @Mock
    protected AttachmentService mockedAttachmentService;

    protected Project project;
    protected Todolist todolist;

    @Before
    public void setupSampleProjectTest() {
        initProjectService();
        initDiscussionService();
        initTodolistService();
        initTodoService();
        initUploadService();
        initAttachmentService();
    }

    /** initProjectService **/
    private void initProjectService() {
        project = getASampleProject();
        when(mockedProjectService.createProject(Mockito.any(ProjectDTO.class))).thenReturn(project);
    }

    public static Project getASampleProject() {
        Project project = new Project(ModuleHelper.projectId);
        return project;
    }

    /** initDiscussionService **/
    private void initDiscussionService() {
        when(mockedDiscussionService.create(Mockito.any(Discussion.class))).thenReturn(null);
    }

    /** initTodolistService **/
    private void initTodolistService() {
        todolist = getASampleTodolist();
        when(mockedTodolistService.create(Mockito.any(Todolist.class))).thenReturn(todolist);
    }

    public static Todolist getASampleTodolist() {
        Todolist todolist = new Todolist(ModuleHelper.todolistId);
        todolist.setTodos(getAListOfSampleTodo());
        return todolist;
    }

    public static List<Todo> getAListOfSampleTodo() {
        return Lists.newArrayList(ModuleHelper.getASampleTodo(), ModuleHelper.getASampleTodo());
    }

    /** initTodoService **/
    private void initTodoService() {
        when(mockedTodoService.create(Mockito.any(Todo.class))).thenReturn(null);
    }

    /** initUploadService **/
    private void initUploadService() {
    }

    /** initAttachmentService **/
    private void initAttachmentService() {
    }

}
