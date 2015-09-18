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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.onboard.domain.model.Activity;
import com.onboard.domain.model.Todo;
import com.onboard.domain.model.User;
import com.onboard.domain.model.type.BaseProjectItem;
import com.onboard.service.account.UserService;
import com.onboard.service.activity.ActivityActionType;
import com.onboard.service.collaboration.TodoService;
import com.onboard.test.moduleutils.ModuleHelper;

@RunWith(MockitoJUnitRunner.class)
public class TodoActivityGeneratorTest {

    private Todo todo;

    private User user;

    @InjectMocks
    private TodoActivityGenerator todoActivityGenerator;

    @Mock
    private TodoService todoService;

    @Mock
    private UserService userService;

    @Before
    public void setUpBefore() throws Exception {
        todo = ModuleHelper.getASampleTodo();
        user = ModuleHelper.getASampleUser();
        when(userService.getById(any(Integer.class))).thenReturn(user);
        when(todoService.getById(any(Integer.class))).thenReturn(todo);
    }

    @Test
    public void testModelType() {
        String ret = todoActivityGenerator.modelType();
        assertEquals(ret, "todo");
    }

    @Test
    public void testGenerateCreateActivity() {
        Activity ret = todoActivityGenerator.generateCreateActivity(todo);
        assertEquals(ret.getAction(), ActivityActionType.CREATE);
        assertEquals(ret.getCompanyId(), new Integer(todo.getCompanyId()));
        assertEquals(ret.getProjectId(), new Integer(todo.getProjectId()));
        assertEquals(ret.getTarget(), todo.getContent());
        assertNotNull(ret.getContent());
    }

    @Test
    public void testGenerateUpdateActivity() {
        Todo originTodo = new Todo(todo);
        Todo modifiedTodo = new Todo(todo);
        modifiedTodo.setDeleted(true);
        Activity ret = todoActivityGenerator.generateUpdateActivity(originTodo, modifiedTodo);
        assertEquals(ret.getAction(), ActivityActionType.DISCARD);
        assertEquals(ret.getCompanyId(), new Integer(todo.getCompanyId()));
        assertEquals(ret.getProjectId(), new Integer(todo.getProjectId()));
        assertEquals(ret.getTarget(), todo.getContent());
        ret = todoActivityGenerator.generateUpdateActivity(modifiedTodo, originTodo);
        assertEquals(ret.getAction(), ActivityActionType.RECOVER);
        assertEquals(ret.getCompanyId(), new Integer(todo.getCompanyId()));
        assertEquals(ret.getProjectId(), new Integer(todo.getProjectId()));
        assertEquals(ret.getTarget(), todo.getContent());

        modifiedTodo = new Todo(originTodo);
        modifiedTodo.setContent(originTodo.getContent() + "modified");
        ret = todoActivityGenerator.generateUpdateActivity(originTodo, modifiedTodo);
        assertEquals(ret.getAction(), ActivityActionType.UPDATE);
        assertEquals(ret.getCompanyId(), new Integer(todo.getCompanyId()));
        assertEquals(ret.getProjectId(), new Integer(todo.getProjectId()));

    }

    @Test
    public void testEnrichModel() {
        BaseProjectItem ret = todoActivityGenerator.enrichModel(todo);
        verify(todoService, times(1)).getById(anyInt());
        assertEquals(ret.getId(), todo.getId());
        assertEquals(ret.getCompanyId(), todo.getCompanyId());
        assertEquals(ret.getCreatorId(), todo.getCreatorId());
        assertEquals(ret.getProjectId(), todo.getProjectId());
    }

    @Test
    public void testModelService() {
        String ret = todoActivityGenerator.modelType();
        assertEquals(ret, "todo");
    }
}
