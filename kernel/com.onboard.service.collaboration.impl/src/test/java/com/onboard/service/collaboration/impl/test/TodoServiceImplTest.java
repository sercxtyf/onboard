package com.onboard.service.collaboration.impl.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.Lists;
import com.onboard.domain.mapper.ProjectTodoIdMapper;
import com.onboard.domain.mapper.TodoMapper;
import com.onboard.domain.mapper.TodolistMapper;
import com.onboard.domain.mapper.model.TodoExample;
import com.onboard.domain.mapper.model.common.BaseExample;
import com.onboard.domain.model.Iteration.IterationStatus;
import com.onboard.domain.model.Project;
import com.onboard.domain.model.Subscriber;
import com.onboard.domain.model.Todo;
import com.onboard.domain.model.Todolist;
import com.onboard.domain.model.User;
import com.onboard.domain.model.type.Recommendable;
import com.onboard.service.account.UserService;
import com.onboard.service.collaboration.CommentService;
import com.onboard.service.collaboration.IdInProjectService;
import com.onboard.service.collaboration.KeywordService;
import com.onboard.service.collaboration.ProjectService;
import com.onboard.service.collaboration.TodoService;
import com.onboard.service.collaboration.TodolistService;
import com.onboard.service.collaboration.TopicService;
import com.onboard.service.collaboration.impl.TodoServiceImpl;
import com.onboard.service.common.subscrible.SubscriberService;
import com.onboard.service.web.SessionService;
import com.onboard.test.exampleutils.CriterionVerifier;
import com.onboard.test.exampleutils.ExampleMatcher;
import com.onboard.test.exampleutils.ObjectMatcher;
import com.onboard.test.moduleutils.ModuleHelper;

/*******************************************************************************
 * Copyright [2015] [Onboard team of SERC, Peking University]
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 *******************************************************************************/

@RunWith(MockitoJUnitRunner.class)
public class TodoServiceImplTest {

    @Mock
    private TodoMapper mockTodoMapper;

    @Mock
    private TodolistMapper mockTodolistMapper;

    @Mock
    private TodolistService mockTodolistService;

    @Mock
    private CommentService mockCommentService;

    @Mock
    private SubscriberService mockSubscriberService;

    @Mock
    private SessionService mockSessionService;

    @Mock
    private UserService mockUserService;

    @Mock
    private TopicService mockTopicService;

    @Mock
    private ProjectService mockProjectService;

    @Mock
    private ProjectTodoIdMapper projectTodoIdMapper;

    @Mock
    private KeywordService mockKeywordService;

    @Mock
    private IdInProjectService mockIdInProjectService;

    @InjectMocks
    private TodoServiceImpl todoService;

    private static int id = 1;
    private static int DEFAULT_LIMIT = -1;
    private static int count = 2;
    // 2011-Dec-11
    private static Date startDate = new Date(Long.parseLong("1323532800691"));
    // 2012-Dec-11
    private static Date endDate = new Date(Long.parseLong("1355155200683"));

    private Todo sampleTodo;
    private Todolist sampelTodolist;
    private List<Todo> listOfTodos;
    private User sampleUser;
    private List<Todolist> listOfTodolists;
    private Project sampleProject;
    private List<Integer> sampleProjectList;
    private TodoService spyTodoService2;

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() throws Exception {
        this.sampleTodo = GetASampleTodo();
        this.sampelTodolist = GetASampleTodolist();
        this.listOfTodos = GetAListOfTodo();
        this.sampleUser = GetASampleUser();
        this.listOfTodolists = GetAListofTodolists();
        this.sampleProject = GetASampleProject();
        this.sampleProjectList = GetASampleProjectList();

        this.spyTodoService2 = spy(todoService);
        doReturn(sampleTodo).when(spyTodoService2).update(any(Todo.class));

        when(mockTodoMapper.selectByPrimaryKey(id)).thenReturn(sampleTodo);
        when(mockTodoMapper.selectByExample(any(TodoExample.class))).thenReturn(listOfTodos);
        when(mockTodoMapper.countByExample(any(TodoExample.class))).thenReturn(count);
        when(mockTodoMapper.insert(any(Todo.class))).thenReturn(1);
        when(mockTodoMapper.updateByPrimaryKeySelective(any(Todo.class))).thenReturn(1);
        when(mockTodoMapper.deleteByPrimaryKey(id)).thenReturn(1);
        when(mockTodoMapper.updateByPrimaryKey(sampleTodo)).thenReturn(1);
        when(mockIdInProjectService.getNextIdByProjectId(Mockito.anyInt())).thenReturn(ModuleHelper.projectId);
        when(mockSessionService.getCurrentUser()).thenReturn(sampleUser, sampleUser);

        when(mockUserService.getById(id)).thenReturn(sampleUser);

        when(mockTodolistMapper.selectByPrimaryKey(id)).thenReturn(sampelTodolist, sampelTodolist);
        when(mockTodolistService.getTodolistAccordingToTodos(any(List.class))).thenReturn(listOfTodolists);

        doNothing().when(mockCommentService).fillCommentable(sampleTodo, 0, DEFAULT_LIMIT);
        doNothing().when(mockCommentService).deleteCommentByAttachTypeAndId("todo", id);

        doNothing().when(mockSubscriberService).fillSubcribers(sampleTodo);
        doNothing().when(mockSubscriberService).generateSubscribers(any(Todo.class), any(User.class));
        doNothing().when(mockSubscriberService).addSubscribers(any(Todo.class));
        when(mockSubscriberService.createSubscriber(any(Subscriber.class))).thenReturn(null);

        doNothing().when(mockTopicService).discardTopcicByTypeAndId("todo", id);
        doNothing().when(mockTopicService).recoverTopcicByTypeAndId("todo", id);

        when(mockProjectService.getById(id)).thenReturn(sampleProject);
        when(mockProjectService.getActiveProjectIdListByUserByCompany(id, id, 0, -1)).thenReturn(sampleProjectList);
        when(mockUserService.getById(Mockito.anyInt())).thenReturn(ModuleHelper.getASampleUser());
    }

    private List<Integer> GetASampleProjectList() {
        List<Integer> projectList = new ArrayList<Integer>();
        projectList.add(1);
        projectList.add(2);
        return projectList;
    }

    @After
    public void tearDown() throws Exception {
    }

    private Project GetASampleProject() {
        Project aSampleProject = new Project();
        aSampleProject.setId(id);
        return aSampleProject;
    }

    private Todo GetASampleTodo() {
        Todo aSampleTodo = new Todo();
        aSampleTodo.setId(id);
        aSampleTodo.setTodolistId(id);
        aSampleTodo.setUpdated(startDate);
        aSampleTodo.setDueDate(endDate);
        aSampleTodo.setAssigneeId(id);
        aSampleTodo.setProjectId(id);
        aSampleTodo.setCreatorId(ModuleHelper.userId);

        return aSampleTodo;
    }

    private Todolist GetASampleTodolist() {
        Todolist sample = new Todolist();
        sample.setId(id);
        return sample;
    }

    private User GetASampleUser() {
        User user = new User();
        user.setId(id);
        return user;
    }

    private List<Todo> GetAListOfTodo() {

        List<Todo> list = new ArrayList<Todo>();
        list.add(GetASampleTodo());
        list.add(GetASampleTodo());

        return list;
    }

    private List<Todolist> GetAListofTodolists() {
        List<Todolist> list = new ArrayList<Todolist>();
        list.add(GetASampleTodolist());
        list.add(GetASampleTodolist());
        return list;
    }

    @Test
    public void testGetTodoById() {
        Todo retTodo = todoService.getById(id);
        verify(mockTodoMapper).selectByPrimaryKey(id);
        assertSame(sampleTodo, retTodo);
    }

    @Test
    public void testGetTodoByIdWithCommentAndSubscriable() {

        Todo retTodo = todoService.getTodoByIdWithCommentAndSubscriable(id);

        verify(mockTodolistMapper).selectByPrimaryKey(id);
        verify(mockCommentService).fillCommentable(retTodo, 0, DEFAULT_LIMIT);
        verify(mockSubscriberService).fillSubcribers(retTodo);
        assertEquals(sampleTodo.getId(), retTodo.getId());
    }

    @Test
    public void testGetTodoByIdWithCommentAndSubscriable_NullTest() {
        // Here we reset mock and return a null instead
        reset(mockTodoMapper);
        when(mockTodoMapper.selectByPrimaryKey(id)).thenReturn(null);

        Todo retTodo = todoService.getTodoByIdWithCommentAndSubscriable(id);
        verify(mockTodoMapper).selectByPrimaryKey(id);
        assertNull(retTodo);
    }

    @Test
    public void testGetTodosByTodoListWithoutComments() {

        List<Todo> retList = todoService.getTodosByTodoListWithoutComments(id);

        verify(mockTodoMapper).selectByExample((argThat(new ExampleMatcher<TodoExample>() {

            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "deleted", false)
                        && CriterionVerifier.verifyEqualTo(example, "todolistId", id)
                        && CriterionVerifier.verifyOrderByClause(example, "position");
            }
        })));
        runAssert(Lists.newArrayList(sampleTodo, sampleTodo, sampleTodo, sampleTodo), retList);
    }

    @Test
    public void testGetTodosByTodoList() {

        doNothing().when(mockCommentService).fillCommentable(any(Todo.class), any(Integer.class), any(Integer.class));

        List<Todo> retList = todoService.getTodosByTodoList(id);

        verify(mockTodoMapper, Mockito.times(2)).selectByExample(any(TodoExample.class));
        verify(mockCommentService, times(4)).fillCommentable(any(Todo.class), any(Integer.class), any(Integer.class));
        verify(mockTodolistMapper, times(4)).selectByPrimaryKey(id);

        runAssert(Lists.newArrayList(sampleTodo, sampleTodo, sampleTodo, sampleTodo), retList);
    }

    private void runAssert(Todo exceptedTodo, Todo actualTodo) {
        assertEquals(exceptedTodo.generateText(), actualTodo.generateText());
        assertEquals(exceptedTodo.getAssigneeId(), actualTodo.getAssigneeId());
        assertEquals(exceptedTodo.getCommentSubject(), actualTodo.getCommentSubject());
        assertEquals(exceptedTodo.getCompanyId(), actualTodo.getCompanyId());
        assertEquals(exceptedTodo.getCompleterId(), actualTodo.getCompleterId());
        assertEquals(exceptedTodo.getCompleteTime(), actualTodo.getCompleteTime());
        assertEquals(exceptedTodo.getContent(), actualTodo.getContent());
        assertEquals(exceptedTodo.getDeleted(), actualTodo.getDeleted());
        assertEquals(exceptedTodo.getDescription(), actualTodo.getDescription());
        assertEquals(exceptedTodo.getDoing(), actualTodo.getDoing());
    }

    private void runAssert(List<Todo> exceptedTodos, List<Todo> actualTodos) {
        assertEquals(exceptedTodos.size(), actualTodos.size());
        for (int i = 0; i < exceptedTodos.size(); i++) {
            runAssert(exceptedTodos.get(i), actualTodos.get(i));
        }
    }

    @Test
    public void testGetTodosByTodoListWithDiscard() {

        List<Todo> retList = todoService.getTodosByTodoListWithDiscard(id);

        verify(mockTodoMapper).selectByExample((argThat(new ExampleMatcher<TodoExample>() {

            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "deleted", true)
                        && CriterionVerifier.verifyEqualTo(example, "todolistId", id)
                        && CriterionVerifier.verifyOrderByClause(example, "position");
            }
        })));
        assertSame(listOfTodos, retList);
    }

    @Test
    public void testCountByExample() {
        int ret = todoService.countBySample(sampleTodo);
        verify(mockTodoMapper).countByExample(any(TodoExample.class));
        assertEquals(count, ret);
    }

    @Test
    public void testCreateTodo() {
        sampleTodo.setAssigneeId(id);
        Todo retTodo = todoService.create(sampleTodo);

        verify(mockTodoMapper).insert(any(Todo.class));
        verify(mockSubscriberService).generateSubscribers(any(Todo.class), any(User.class));
        verify(mockSubscriberService).addSubscribers(any(Todo.class));
        verify(mockUserService).getById(id);

        assertNotNull(retTodo.getCreated());
        assertNotNull(retTodo.getUpdated());
        assertFalse(retTodo.getStatus().equals(IterationStatus.COMPLETED.getValue()));

        List<User> subscribers = retTodo.getSubscribers();
        assertNotNull(subscribers);
        assertEquals(1, subscribers.size());
    }

    @Test
    public void testCreateTodo_NullTest() {
        Todo retTodo = todoService.create(sampleTodo);

        verify(mockTodoMapper).insert(any(Todo.class));
        verify(mockSubscriberService).generateSubscribers(any(Todo.class), any(User.class));
        verify(mockSubscriberService).addSubscribers(any(Todo.class));

        assertNotNull(retTodo.getCreated());
        assertNotNull(retTodo.getUpdated());
        assertFalse(retTodo.getStatus().equals(IterationStatus.COMPLETED.getValue()));
    }

    @Test
    public void testUpdateTodo1() {
        // None if switch tested sampleTodo.setDeleted(false);
        todoService.update(sampleTodo);
        verify(mockTodoMapper).updateByPrimaryKey(any(Todo.class));
    }

    @Test
    public void testDeleteTodo() {
        todoService.delete(id);
        mockTodoMapper.updateByPrimaryKeySelective(Mockito.any(Todo.class));
    }

    @Test
    public void testGetByTimeRanggeByCompany_IfReturnTest() {
        List<Integer> projectList = new ArrayList<Integer>();
        when(mockProjectService.getActiveProjectIdListByUserByCompany(id, id, 0, -1)).thenReturn(projectList);
        List<Todo> ret = todoService.getByTimeRangeByCompany(startDate, endDate, id, id);
        assertNotNull(ret);
        assertEquals(0, ret.size());
    }

    @Test
    public void testGetByTimeRanggeByCompany() {

        List<Todo> ret = todoService.getByTimeRangeByCompany(startDate, endDate, id, id);

        verify(mockProjectService).getActiveProjectIdListByUserByCompany(id, id, 0, -1);
        verify(mockTodoMapper).selectByExample(argThat(new ExampleMatcher<TodoExample>() {

            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "deleted", false)
                        && CriterionVerifier.verifyOrderByClause(example, "projectId desc")
                        && CriterionVerifier.verifyBetween(example, "dueDate", startDate, endDate)
                        && CriterionVerifier.verifyIn(example, "projectId", sampleProjectList);
            }
        }));

        verify(mockUserService, times(2)).getById(id);
        verify(mockTodolistMapper, times(2)).selectByPrimaryKey(id);
        verify(mockProjectService, times(2)).getById(id);

        assertNotNull(ret);
        assertEquals(2, ret.size());
    }

    @Test
    public void testGetCompletedTodosCountByProject() {

        int ret = todoService.getCompletedTodosCountByProject(id);

        verify(mockTodoMapper).countByExample((argThat(new ExampleMatcher<TodoExample>() {

            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "deleted", false)
                        && CriterionVerifier.verifyEqualTo(example, "projectId", id);
            }
        })));

        assertEquals(count, ret);
    }

    @Test
    public void testGetCompletedTodolistGroupByDateByProject_NullTest1() {

        reset(mockTodoMapper);
        when(mockTodoMapper.selectByExample(any(TodoExample.class))).thenReturn(null);
        TreeMap<Date, List<Todolist>> retMap = todoService.getCompletedTodolistGroupByDateByProject(id, endDate, DEFAULT_LIMIT);

        verify(mockTodoMapper).selectByExample(any(TodoExample.class));
        assertNull(retMap);
    }

    @Test
    public void testGetCompletedTodolistGroupByDateByProject_NullTest2() {

        List<Todo> list = new ArrayList<Todo>();

        // Reset the mock from the @Before setup reset(mockTodoMapper);
        when(mockTodoMapper.selectByExample(any(TodoExample.class))).thenReturn(list);

        TreeMap<Date, List<Todolist>> retMap = todoService.getCompletedTodolistGroupByDateByProject(id, endDate, DEFAULT_LIMIT);

        verify(mockTodoMapper).selectByExample(any(TodoExample.class));
        assertNull(retMap);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testGetCompletedTodolistGroupByDateByProject_Normal() {
        TreeMap<Date, List<Todolist>> retMap = todoService.getCompletedTodolistGroupByDateByProject(id, endDate, DEFAULT_LIMIT);

        verify(mockTodoMapper, times(2)).selectByExample((argThat(new ExampleMatcher<TodoExample>() {

            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "deleted", false)
                        && CriterionVerifier.verifyEqualTo(example, "projectId", id)
                        && CriterionVerifier.verifyLessThanOrEqualTo(example, "updated", endDate)
                        && CriterionVerifier.verifyOrderByClause(example, "updated desc")
                        && CriterionVerifier.verifyLimit(example, DEFAULT_LIMIT);
            }
        })));

        verify(mockTodolistService).getTodolistAccordingToTodos(any(List.class));
        assertNull(retMap.get(startDate));

    }

    @Test
    public void testGetCompletedTodolistsGroupByDateByUser_NullTest1() {
        reset(mockTodoMapper);
        when(mockTodoMapper.selectByExample(any(TodoExample.class))).thenReturn(null);
        TreeMap<Date, List<Todolist>> ret1 = todoService.getCompletedTodolistsGroupByDateByUser(id, id, null, endDate,
                DEFAULT_LIMIT);
        verify(mockTodoMapper).selectByExample(any(TodoExample.class));
        assertEquals(0, ret1.size());
    }

    @Test
    public void testGetCompletedTodolistsGroupByDateByUser_NullTest2() {
        reset(mockTodoMapper);
        List<Todo> list = new ArrayList<Todo>();
        when(mockTodoMapper.selectByExample(any(TodoExample.class))).thenReturn(list);

        TreeMap<Date, List<Todolist>> ret2 = todoService.getCompletedTodolistsGroupByDateByUser(id, id, null, endDate,
                DEFAULT_LIMIT);
        verify(mockTodoMapper).selectByExample(any(TodoExample.class));
        assertEquals(0, ret2.size());

    }

    @Test
    public void testGetCompletedTodolistsGroupByDateByUser_ProjectListTest1() {

        List<Integer> projectList = new ArrayList<Integer>();
        TreeMap<Date, List<Todolist>> ret = todoService.getCompletedTodolistsGroupByDateByUser(id, id, projectList, endDate,
                DEFAULT_LIMIT);
        assertNotNull(ret);
        assertEquals(0, ret.size());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testGetCompletedTodolistsGroupByDateByUser_ProjectListTest2() {
        List<Integer> projectList = null;
        TreeMap<Date, List<Todolist>> ret = todoService.getCompletedTodolistsGroupByDateByUser(id, id, projectList, endDate,
                DEFAULT_LIMIT);
        verify(mockTodoMapper, times(2)).selectByExample((argThat(new ExampleMatcher<TodoExample>() {

            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "deleted", false)
                        && CriterionVerifier.verifyEqualTo(example, "assigneeId", id)
                        && CriterionVerifier.verifyEqualTo(example, "companyId", id)
                        && CriterionVerifier.verifyLessThanOrEqualTo(example, "updated", endDate)
                        && CriterionVerifier.verifyLimit(example, DEFAULT_LIMIT)
                        && CriterionVerifier.verifyOrderByClause(example, "updated desc");
            }
        })));
        verify(mockTodolistService).getTodolistAccordingToTodos(any(List.class));
        assertNotNull(ret);
        assertEquals(1, ret.size());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testGetCompletedTodolistsGroupByDateByUser_ProjectListTest3() {
        final List<Integer> projectList = new ArrayList<Integer>();
        projectList.add(1);
        projectList.add(2);
        TreeMap<Date, List<Todolist>> ret = todoService.getCompletedTodolistsGroupByDateByUser(id, id, projectList, endDate,
                DEFAULT_LIMIT);
        verify(mockTodoMapper, times(2)).selectByExample((argThat(new ExampleMatcher<TodoExample>() {

            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "deleted", false)
                        && CriterionVerifier.verifyEqualTo(example, "assigneeId", id)
                        && CriterionVerifier.verifyEqualTo(example, "companyId", id)
                        && CriterionVerifier.verifyLessThanOrEqualTo(example, "updated", endDate)
                        && CriterionVerifier.verifyLimit(example, DEFAULT_LIMIT)
                        && CriterionVerifier.verifyOrderByClause(example, "updated desc")
                        && CriterionVerifier.verifyIn(example, "projectId", projectList);
            }
        })));
        verify(mockTodolistService).getTodolistAccordingToTodos(any(List.class));
        assertNotNull(ret);
        assertEquals(1, ret.size());
    }

    @Test
    public void testUpdateTodoAssigneeAndDueDate() {
        Todo ret = todoService.updateTodoAssigneeAndDueDate(sampleTodo);
        verify(mockTodoMapper).updateByPrimaryKey(any(Todo.class));
        assertEquals(sampleTodo.getDueDate(), ret.getDueDate());
        assertEquals(sampleTodo.getAssigneeId(), ret.getAssigneeId());
    }

    @Test
    public void testUpdateTodoAssigneeAndDueDate_SetContentTest() {
        String contentString = "This is a contentString";
        sampleTodo.setContent(contentString);
        Todo ret = todoService.updateTodoAssigneeAndDueDate(sampleTodo);
        verify(mockTodoMapper).updateByPrimaryKey(any(Todo.class));
        assertEquals(sampleTodo.getDueDate(), ret.getDueDate());
        assertEquals(sampleTodo.getAssigneeId(), ret.getAssigneeId());
        assertEquals(contentString, ret.getContent());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testGetOpenTodosByUser_ProjectListNullTest() {
        List<Todolist> ret = todoService.getOpenTodosByUser(id, null);

        verify(mockTodoMapper).selectByExample((argThat(new ExampleMatcher<TodoExample>() {

            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "deleted", false)
                        && CriterionVerifier.verifyEqualTo(example, "assigneeId", id);
            }
        })));

        verify(mockTodoMapper).selectByExample(any(TodoExample.class));
        verify(mockTodolistService).getTodolistAccordingToTodos(any(List.class));
        assertEquals(listOfTodolists, ret);
    }

    @Test
    public void testGetOpenTodosByUser_BlankProjectList() {
        List<Integer> projectList = new ArrayList<Integer>();
        List<Todolist> ret = todoService.getOpenTodosByUser(id, projectList);
        assertNotNull(ret);
        assertEquals(0, ret.size());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testGetOpenTodosByUser_NormalProjectList() {
        final List<Integer> projectList = new ArrayList<Integer>();
        projectList.add(1);
        projectList.add(2);
        List<Todolist> ret = todoService.getOpenTodosByUser(id, projectList);

        verify(mockTodoMapper).selectByExample((argThat(new ExampleMatcher<TodoExample>() {

            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "deleted", false)
                        && CriterionVerifier.verifyEqualTo(example, "assigneeId", id)
                        && CriterionVerifier.verifyIn(example, "projectId", projectList);
            }
        })));

        verify(mockTodoMapper).selectByExample(any(TodoExample.class));
        verify(mockTodolistService).getTodolistAccordingToTodos(any(List.class));
        assertEquals(listOfTodolists, ret);
    }

    @Test
    public void testGetByTimeRange() {
        List<Todo> ret = todoService.getByTimeRange(startDate, endDate);

        verify(mockTodoMapper).selectByExample((argThat(new ExampleMatcher<TodoExample>() {

            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "deleted", false)
                        && CriterionVerifier.verifyBetween(example, "dueDate", startDate, endDate);
            }
        })));

        assertEquals(listOfTodos, ret);
    }

    @Test
    public void testGetDeletedTodosByTodoList() {

        List<Todo> retList = todoService.getDeletedTodosByTodoList(id);

        verify(mockTodoMapper).selectByExample((argThat(new ExampleMatcher<TodoExample>() {

            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "deleted", true)
                        && CriterionVerifier.verifyEqualTo(example, "todolistId", id);
            }
        })));
        assertSame(listOfTodos, retList);
    }

    @Test
    public void testrecoverTodo() {
        spyTodoService2.recover(id);
        verify(mockKeywordService).deleteKeywordsByIdentifiable(Mockito.any(Recommendable.class));
        verify(spyTodoService2).updateSelective(argThat(new ObjectMatcher<Todo>() {

            @Override
            public boolean verifymatches(Todo item) {
                return item.getId() == 1 && !item.getDeleted();
            }
        }));
    }

    @Test
    public void testmoveTodo() {
        spyTodoService2.moveTodo(sampleTodo, id);
        verify(mockTodoMapper).updateByPrimaryKeySelective(Mockito.any(Todo.class));
    }
}
