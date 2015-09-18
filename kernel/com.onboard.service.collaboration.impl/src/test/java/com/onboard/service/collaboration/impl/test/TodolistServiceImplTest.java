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
package com.onboard.service.collaboration.impl.test;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.onboard.domain.mapper.ProjectMapper;
import com.onboard.domain.mapper.TodoMapper;
import com.onboard.domain.mapper.TodolistMapper;
import com.onboard.domain.mapper.model.TodoExample;
import com.onboard.domain.mapper.model.TodolistExample;
import com.onboard.domain.mapper.model.common.BaseExample;
import com.onboard.domain.model.IterationItemStatus;
import com.onboard.domain.model.Project;
import com.onboard.domain.model.Todo;
import com.onboard.domain.model.Todolist;
import com.onboard.domain.model.User;
import com.onboard.service.account.UserService;
import com.onboard.service.collaboration.CommentService;
import com.onboard.service.collaboration.TodoService;
import com.onboard.service.collaboration.TodolistService;
import com.onboard.service.collaboration.TopicService;
import com.onboard.service.collaboration.impl.TodolistServiceImpl;
import com.onboard.service.common.subscrible.SubscriberService;
import com.onboard.service.web.SessionService;
import com.onboard.test.exampleutils.CriterionVerifier;
import com.onboard.test.exampleutils.ExampleMatcher;
import com.onboard.test.moduleutils.ModuleHelper;

@RunWith(MockitoJUnitRunner.class)
public class TodolistServiceImplTest {

    @Mock
    private TodolistMapper mockTodolistMapper;

    @Mock
    private TodoService mockTodoService;

    @Mock
    private CommentService mockCommentService;

    @Mock
    private SubscriberService mockSubscriberService;

    @Mock
    private ProjectMapper mockProjectMapper;

    @Mock
    private TodoMapper mockTodoMapper;

    @Mock
    private TopicService mockTopicService;

    @Mock
    private SessionService mockSessionService;

    @Mock
    private UserService mockUserService;

    @InjectMocks
    private TodolistServiceImpl todolistService;

    private static int start = 1;
    private static int limit = 5;
    private static int id = 1;
    private static int DEFAULT_LIMIT = -1;
    private static int count = 5;
    private static String todolistType = "todolist";
    private static String userName = "Test User";

    private Todolist sampleTodolist;
    private Todolist sampleTodolist2;
    private List<Todolist> sampleListofTodolists;
    private List<Todo> sampleListofTodos;
    private Project sampleProject;
    private Project sampleProjectArchived;
    private TodolistService spyTodolistService;
    private User sampleUser;

    private boolean isToday(Date date) {
        return new DateTime(date).toLocalDate().equals(new DateTime().toLocalDate());
    }

    private Todo getASampleTodo() {
        Todo todo = new Todo();
        todo.setId(id);
        todo.setProjectId(id);
        todo.setCompanyId(id);
        todo.setTodolistId(id);
        return todo;
    }

    private List<Todo> getASampleListofTodos() {
        List<Todo> list = new ArrayList<Todo>();
        list.add(getASampleTodo());
        list.add(getASampleTodo());
        return list;
    }

    private Todolist getASampleTodolist() {
        Todolist sample = new Todolist();
        sample.setId(id);
        sample.setProjectId(id);
        sample.setCompanyId(id);
        sample.setCreatorId(ModuleHelper.creatorId);
        sample.setCreatorName(ModuleHelper.creatorName);
        return sample;
    }

    private List<Todolist> getAListofTodolists() {
        List<Todolist> list = new ArrayList<Todolist>();
        list.add(getASampleTodolist());
        list.add(getASampleTodolist());
        return list;
    }

    private Project getASampleProject() {
        Project retProject = new Project();
        retProject.setId(id);
        retProject.setArchived(false);
        return retProject;
    }

    private User getASampleUser() {
        User sample = new User();
        sample.setId(id);
        sample.setName(userName);
        return sample;
    }

    @Before
    public void setUp() throws Exception {
        this.sampleTodolist = getASampleTodolist();
        this.sampleTodolist2 = getASampleTodolist();
        sampleTodolist2.setDeleted(true);
        this.sampleListofTodolists = getAListofTodolists();
        this.sampleListofTodos = getASampleListofTodos();
        this.sampleProject = getASampleProject();
        this.sampleProjectArchived = getASampleProject();
        sampleProjectArchived.setArchived(true);
        this.sampleUser = getASampleUser();

        this.spyTodolistService = spy(todolistService);
        doReturn(sampleListofTodolists).when(spyTodolistService).getTodolistAccordingToTodos(sampleListofTodos);
        doReturn(sampleTodolist2).when(spyTodolistService).getById(id);

        when(mockTodolistMapper.selectByExample(any(TodolistExample.class))).thenReturn(sampleListofTodolists);
        when(mockTodolistMapper.selectByPrimaryKey(id)).thenReturn(sampleTodolist);
        when(mockTodolistMapper.countByExample(any(TodolistExample.class))).thenReturn(count);
        when(mockTodolistMapper.updateByPrimaryKeySelective(sampleTodolist)).thenReturn(1);
        when(mockTodolistMapper.insert(sampleTodolist)).thenReturn(1);

        when(mockTodoService.getTodosByTodoListWithoutComments(id)).thenReturn(sampleListofTodos);
        when(mockTodoService.getTodosByTodoList(id)).thenReturn(sampleListofTodos);
        when(mockTodoService.getTodosByTodoListWithDiscard(id)).thenReturn(sampleListofTodos);
        when(mockTodoService.getDeletedTodosByTodoList(id)).thenReturn(sampleListofTodos);

        doNothing().when(mockCommentService).fillCommentable(any(Todolist.class), any(Integer.class), any(Integer.class));

        doNothing().when(mockSubscriberService).fillSubcribers(any(Todolist.class));
        doNothing().when(mockSubscriberService).generateSubscribers(sampleTodolist, sampleUser);
        doNothing().when(mockSubscriberService).addSubscribers(sampleTodolist);

        when(mockProjectMapper.selectByPrimaryKey(id)).thenReturn(sampleProject);
        when(mockProjectMapper.selectByPrimaryKey(2)).thenReturn(sampleProjectArchived);

        when(mockTodoMapper.selectByExample(any(TodoExample.class))).thenReturn(sampleListofTodos);

        doNothing().when(mockTopicService).discardTopcicByTypeAndId(todolistType, id);
        doNothing().when(mockTopicService).recoverTopcicByTypeAndId(todolistType, id);

        when(mockSessionService.getCurrentUser()).thenReturn(sampleUser);

        when(mockUserService.getById(Mockito.anyInt())).thenReturn(ModuleHelper.getASampleUser());
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testGetTodolistsByProject() {
        List<Todolist> ret = todolistService.getTodolistsByProject(id, start, limit);
        verify(mockTodolistMapper).selectByExample(Mockito.argThat(new ExampleMatcher<TodolistExample>() {

            @Override
            public boolean matches(BaseExample example) {

                return CriterionVerifier.verifyEqualTo(example, "deleted", false)
                        && CriterionVerifier.verifyEqualTo(example, "projectId", id)
                        && CriterionVerifier.verifyOrderByClause(example, "position desc");
            }
        }));
        assertNotNull(ret);
        assertEquals(2, ret.size());
    }

    @Test
    public void testGetTodolistDetailsByProject() {

        List<Todolist> ret = todolistService.getTodolistDetailsByProject(id, start, limit);
        verify(mockTodolistMapper).selectByExample(any(TodolistExample.class));
        verify(mockTodoService, times(2)).getTodosByTodoListWithoutComments(id);
        assertNotNull(ret);
        assertEquals(2, ret.size());
        for (Todolist todolist : ret) {
            assertEquals(sampleListofTodos, todolist.getTodos());
        }

    }

    @Test
    public void testGetTodolistById() {
        Todolist ret = todolistService.getById(id);
        verify(mockTodolistMapper).selectByPrimaryKey(id);
        assertEquals(sampleTodolist, ret);
    }

    @Test
    public void testGetTodolistByIdWithExtraInfo_NullTest() {
        reset(mockTodolistMapper);
        when(mockTodolistMapper.selectByPrimaryKey(id)).thenReturn(null);
        Todolist ret = todolistService.getTodolistByIdWithExtraInfo(id);
        verify(mockTodolistMapper).selectByPrimaryKey(id);
        assertNull(ret);
    }

    @Test
    public void testgetTodolistWithClosedTodos() {
        todolistService.getTodolistWithClosedTodos(id);
        verify(mockTodolistMapper).selectByPrimaryKey(id);
        verify(mockTodoService).getTodosByTodoList(anyInt());
    }

    @Test
    public void testGetTodolistByIdWithExtraInfo() {
        Todolist ret = todolistService.getTodolistByIdWithExtraInfo(id);

        verify(mockTodolistMapper).selectByPrimaryKey(id);
        verify(mockTodoService).getTodosByTodoList(id);
        verify(mockTodoService).getTodosByTodoListWithDiscard(id);
        verify(mockCommentService).fillCommentable(any(Todolist.class), any(Integer.class), any(Integer.class));
        verify(mockSubscriberService).fillSubcribers(any(Todolist.class));

        assertNotNull(ret);
        assertEquals(sampleListofTodos, ret.getTodos());
        assertEquals(sampleListofTodos, ret.getDicardTodos());
    }

    @Test
    public void testGetTodolistAccordingToTodos() {

        List<Todolist> ret = todolistService.getTodolistAccordingToTodos(sampleListofTodos);
        verify(mockTodolistMapper).selectByPrimaryKey(id);
        verify(mockProjectMapper).selectByPrimaryKey(id);
        assertNotNull(ret);
        assertEquals(1, ret.size());
        assertEquals(sampleListofTodos, ret.get(0).getTodos());
    }

    @Test
    public void testGetOpenTodolistByUser_ProjectListIsNull() {

        List<Todolist> ret = spyTodolistService.getOpenTodolistByUser(id, null);

        verify(mockTodoMapper).selectByExample(argThat(new ExampleMatcher<TodoExample>() {

            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "deleted", false)
                        && CriterionVerifier.verifyEqualTo(example, "assigneeId", id)
                        && CriterionVerifier.verifyNotEqualTo(example, "status", IterationItemStatus.CLOSED.getValue());
            }
        }));
        assertEquals(sampleListofTodolists, ret);

    }

    @Test
    public void testGetOpenTodolistByUser_ProjectListSizeIsZero() {
        List<Integer> projectIdList = new ArrayList<Integer>();

        List<Todolist> ret = todolistService.getOpenTodolistByUser(id, projectIdList);

        assertNotNull(ret);
        assertEquals(0, ret.size());
    }

    @Test
    public void testGetOpenTodolistByUser_ProjectListIsNormal() {
        final List<Integer> projectIdList = new ArrayList<Integer>();
        projectIdList.add(1);
        projectIdList.add(2);

        List<Todolist> ret = spyTodolistService.getOpenTodolistByUser(id, projectIdList);

        verify(mockTodoMapper).selectByExample(argThat(new ExampleMatcher<TodoExample>() {

            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "deleted", false)
                        && CriterionVerifier.verifyEqualTo(example, "assigneeId", id)
                        && CriterionVerifier.verifyIn(example, "projectId", projectIdList);
            }
        }));

        verify(mockProjectMapper).selectByPrimaryKey(id);

        assertEquals(sampleListofTodolists, ret);

    }

    @Test
    public void testGetCompletedTodolistByUser_ProjectListIsNull() {

        List<Todolist> ret = spyTodolistService.getCompletedTodolistByUser(id, id, DEFAULT_LIMIT, null);
        verify(mockTodoMapper).selectByExample(argThat(new ExampleMatcher<TodoExample>() {

            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "deleted", false)
                        && CriterionVerifier.verifyEqualTo(example, "assigneeId", id)
                        && CriterionVerifier.verifyEqualTo(example, "status", IterationItemStatus.CLOSED.getValue())
                        && CriterionVerifier.verifyEqualTo(example, "companyId", id)
                        && CriterionVerifier.verifyLimit(example, DEFAULT_LIMIT)
                        && CriterionVerifier.verifyOrderByClause(example, "updated desc");
            }
        }));
        assertEquals(sampleListofTodolists, ret);
    }

    @Test
    public void testGetCompletedTodolistByUser_ProjectListIsZero() {
        List<Integer> projectList = new ArrayList<Integer>();
        List<Todolist> ret = todolistService.getCompletedTodolistByUser(id, id, DEFAULT_LIMIT, projectList);
        assertNotNull(ret);
        assertEquals(0, ret.size());
    }

    @Test
    public void testGetCompletedTodolistByUser_ProjectListIsNormal() {
        final List<Integer> projectList = new ArrayList<Integer>();
        projectList.add(1);
        projectList.add(2);

        List<Todolist> ret = spyTodolistService.getCompletedTodolistByUser(id, id, DEFAULT_LIMIT, projectList);

        verify(mockTodoMapper).selectByExample(argThat(new ExampleMatcher<TodoExample>() {

            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "deleted", false)
                        && CriterionVerifier.verifyEqualTo(example, "assigneeId", id)
                        && CriterionVerifier.verifyEqualTo(example, "companyId", id)
                        && CriterionVerifier.verifyLimit(example, DEFAULT_LIMIT)
                        && CriterionVerifier.verifyOrderByClause(example, "updated desc")
                        && CriterionVerifier.verifyIn(example, "projectId", projectList);
            }
        }));

        assertEquals(sampleListofTodolists, ret);
    }

    @Test
    public void testCountByExample() {
        int ret = todolistService.countBySample(sampleTodolist);
        verify(mockTodolistMapper).countByExample(argThat(new ExampleMatcher<TodolistExample>() {

            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "projectId", 1);
            }
        }));

        assertEquals(count, ret);
    }

    @Test
    public void testCreateTodolist() {
        Todolist ret = todolistService.create(sampleTodolist);

        verify(mockTodolistMapper).insert(sampleTodolist);
        verify(mockSubscriberService).generateSubscribers(Mockito.any(Todolist.class), Mockito.any(User.class));
        verify(mockSubscriberService).addSubscribers(sampleTodolist);

        assertNotNull(ret);
        assertTrue(isToday(ret.getCreated()));
        assertTrue(isToday(ret.getUpdated()));
        assertEquals(ModuleHelper.creatorId, (int) ret.getCreatorId());
        assertFalse(ret.getArchived());
    }

    @Test
    public void testUpdateTodolist_NullTest() {
        sampleTodolist.setUpdated(null);
        Todolist ret = todolistService.update(sampleTodolist);
        assertNotNull(ret);
        assertTrue(isToday(ret.getUpdated()));
        assertEquals(sampleTodolist, ret);
    }

    @Test
    public void testDeleteTodolist() {
        todolistService.delete(id);
        verify(mockTodoService, times(2)).delete(id);
    }

    @Test
    public void testRecoverTodolist() {
        doReturn(sampleTodolist).when(spyTodolistService).update(any(Todolist.class));

        spyTodolistService.recover(id);
    }

}
