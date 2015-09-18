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
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.joda.time.DateTime;
import org.junit.Test;
import org.mockito.AdditionalMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import com.onboard.domain.mapper.model.BugExample;
import com.onboard.domain.mapper.model.common.BaseExample;
import com.onboard.domain.model.Bug;
import com.onboard.domain.model.Bug.BugPriorities;
import com.onboard.domain.model.Bug.BugStatus;
import com.onboard.domain.model.Project;
import com.onboard.domain.model.User;
import com.onboard.domain.model.type.ProjectItem;
import com.onboard.service.account.UserService;
import com.onboard.service.collaboration.CommentService;
import com.onboard.service.collaboration.IdInProjectService;
import com.onboard.service.collaboration.IterationService;
import com.onboard.service.collaboration.ProjectService;
import com.onboard.service.collaboration.impl.BugServiceImpl;
import com.onboard.service.collaboration.impl.abstractfiles.AbstractBugServiceImplTest;
import com.onboard.service.common.subscrible.SubscriberService;
import com.onboard.test.exampleutils.CriterionVerifier;
import com.onboard.test.exampleutils.ExampleMatcher;
import com.onboard.test.exampleutils.ObjectMatcher;
import com.onboard.test.moduleutils.ModuleHelper;

public class BugServiceImplTest extends AbstractBugServiceImplTest {

    @Mock
    private CommentService mockCommentService;
    @Mock
    private SubscriberService mockSubscriberService;
    @Mock
    private UserService mockUserService;
    @Mock
    private IterationService mockiIterationService;
    @Mock
    private IdInProjectService mockidIdInProjectService;
    @Mock
    private ProjectService mockProjectService;

    @InjectMocks
    private BugServiceImpl testedBugServiceImpl;

    private User getANewUser() {
        User user = new User();
        user.setId(newUserId);
        return user;
    }

    private void runAsserts(Bug bug) {
        assertEquals(ModuleHelper.assignId, (int) bug.getAssigneeId());
        assertEquals(ModuleHelper.companyId, (int) bug.getCompanyId());
        assertEquals(false, bug.getCompleted());
        assertEquals(ModuleHelper.completed, bug.getCompletedTime());
        assertEquals(ModuleHelper.creatorId, (int) bug.getCreatorId());
        assertEquals(ModuleHelper.creatorName, bug.getCreatorName());
        assertEquals(ModuleHelper.dueTime, bug.getDueTime());
        assertEquals(ModuleHelper.id, (int) bug.getId());
        assertEquals(ModuleHelper.idInProject, (int) bug.getIdInProject());
        assertEquals(ModuleHelper.description, bug.getDescription());
        assertEquals(BugPriorities.BLOCKER.getValue(), (int) bug.getPriority());
        assertEquals(ModuleHelper.projectId, (int) bug.getProjectId());
        assertEquals(BugStatus.TODO.getValue(), (int) bug.getStatus());
        assertEquals(ModuleHelper.title, bug.getTitle());
    }

    @Test
    public void testGetById_Null_Test() {
        Mockito.reset(mockBugMapper);
        when(mockBugMapper.selectByPrimaryKey(ModuleHelper.id)).thenReturn(null);

        Bug bug = testedBugServiceImpl.getById(ModuleHelper.id);

        verify(mockBugMapper).selectByPrimaryKey(ModuleHelper.id);
        assertNull(bug);

    }

    @Test
    public void testGetById_Normal_Test() {

        User user = getANewUser();
        when(mockUserService.getById(ModuleHelper.assignId)).thenReturn(user);

        Bug bug = testedBugServiceImpl.getById(ModuleHelper.id);
        verify(mockBugMapper).selectByPrimaryKey(ModuleHelper.id);
        verify(mockUserService).getById(ModuleHelper.assignId);

        assertNotNull(bug);
        assertEquals(user, bug.getAssignee());
        assertEquals(newUserId, (int) bug.getAssignee().getId());
        runAsserts(bug);

    }

    @Test
    public void testGetBugByIdWithCommentAndSubscriable_NullTest() {
        Mockito.reset(mockBugMapper);
        when(mockBugMapper.selectByPrimaryKey(ModuleHelper.id)).thenReturn(null);
        Bug bug = testedBugServiceImpl.getBugByIdWithCommentAndSubscriable(ModuleHelper.id);
        verify(mockBugMapper).selectByPrimaryKey(ModuleHelper.id);
        assertNull(bug);
    }

    @Test
    public void testGetBugByIdWithCommentAndSubscriable() {

        doNothing().when(mockCommentService).fillCommentable(Mockito.any(Bug.class), Mockito.anyInt(), Mockito.anyInt());
        doNothing().when(mockSubscriberService).fillSubcribers(Mockito.any(Bug.class));
        Bug bug = testedBugServiceImpl.getBugByIdWithCommentAndSubscriable(ModuleHelper.id);

        verify(mockBugMapper).selectByPrimaryKey(ModuleHelper.id);
        verify(mockCommentService).fillCommentable(Mockito.argThat(new ObjectMatcher<Bug>() {

            @Override
            public boolean verifymatches(Bug item) {
                return item.getId() == ModuleHelper.id && item.getProjectId() == ModuleHelper.projectId
                        && item.getCompanyId() == ModuleHelper.companyId;
            }
        }), Mockito.eq(0), Mockito.eq(DEFAULT_LIMIT));
        verify(mockSubscriberService).fillSubcribers(Mockito.argThat(new ObjectMatcher<Bug>() {

            @Override
            public boolean verifymatches(Bug item) {
                return item.getId() == ModuleHelper.id && item.getProjectId() == ModuleHelper.projectId
                        && item.getCompanyId() == ModuleHelper.companyId;
            }
        }));
        runAsserts(bug);
    }

    @Test
    public void testGetAll() {

        List<Bug> list = testedBugServiceImpl.getAll(ModuleHelper.start, ModuleHelper.limit);
        verify(mockBugMapper).selectByExample(Mockito.argThat(new ExampleMatcher<BugExample>() {
            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyStart(example, ModuleHelper.start)
                        && CriterionVerifier.verifyLimit(example, ModuleHelper.limit);
            }
        }));
        assertNotNull(list);
        assertEquals(2, list.size());
        runAsserts(list.get(0));
    }

    @Test
    public void testGetBySample() {
        Bug bug = new Bug();
        final int projectId = 111;
        bug.setProjectId(projectId);

        List<Bug> list = testedBugServiceImpl.getBySample(bug, ModuleHelper.start, ModuleHelper.limit);

        verify(mockBugMapper).selectByExample(Mockito.argThat(new ExampleMatcher<BugExample>() {
            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyStart(example, ModuleHelper.start)
                        && CriterionVerifier.verifyLimit(example, ModuleHelper.limit)
                        && CriterionVerifier.verifyEqualTo(example, "projectId", projectId);
            }
        }));
        assertNotNull(list);
        assertEquals(2, list.size());
        runAsserts(list.get(0));
    }

    @Test
    public void testCountBySample() {
        Bug bug = getASampleBug();
        int count = testedBugServiceImpl.countBySample(bug);
        verify(mockBugMapper).countByExample(Mockito.argThat(new ExampleMatcher<BugExample>() {
            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "companyId", ModuleHelper.companyId)
                        && CriterionVerifier.verifyEqualTo(example, "projectId", ModuleHelper.projectId);
            }
        }));
        assertEquals(ModuleHelper.count, count);
    }

    @Test
    public void testCreate() {
        Bug bug = getASampleBug();
        when(mockidIdInProjectService.getNextIdByProjectId(ModuleHelper.projectId)).thenReturn(ModuleHelper.idInProject);
        Mockito.doNothing().when(mockiIterationService).addIterable(Mockito.any(Bug.class));

        Bug retBug = testedBugServiceImpl.create(bug);
        verify(mockidIdInProjectService).getNextIdByProjectId(ModuleHelper.projectId);
        verify(mockBugMapper).insert(Mockito.argThat(new ObjectMatcher<Bug>() {
            @Override
            public boolean verifymatches(Bug item) {
                return item.getProjectId() == ModuleHelper.projectId && item.getPriority() == BugPriorities.BLOCKER.getValue();
            }
        }));
        verify(mockiIterationService).addIterable(Mockito.argThat(new ObjectMatcher<Bug>() {
            @Override
            public boolean verifymatches(Bug item) {
                return item.getProjectId() == ModuleHelper.projectId && item.getPriority() == BugPriorities.BLOCKER.getValue();
            }
        }));

        assertNotNull(retBug);
        ModuleHelper.compareCreatedItemDateWithToday(retBug.getCreatedTime());
        runAsserts(retBug);

    }

    @Test
    public void testUpdate() {
        Bug b = new Bug();
        b.setId(ModuleHelper.id);
        Date dueTime = ModuleHelper.getDateByString("2014-12-04 00:00");
        final Date verifyDate = new DateTime(dueTime).withTimeAtStartOfDay().plusDays(1).plusSeconds(-1).toDate();
        b.setDueTime(dueTime);
        b.setStatus(0);

        Bug retBug = testedBugServiceImpl.updateSelective(b);
        verify(mockBugMapper).updateByPrimaryKeySelective(Mockito.argThat(new ObjectMatcher<Bug>() {
            @Override
            public boolean verifymatches(Bug item) {

                return item.getDueTime().equals(verifyDate)
                        && ModuleHelper.compareCreatedItemDateWithToday(item.getCompletedTime())
                        && item.getDueTime().equals(verifyDate);
            }
        }));
        assertEquals(ModuleHelper.id, (int) retBug.getId());
    }

    @Test
    public void testDelete() {
        testedBugServiceImpl.delete(ModuleHelper.id);
        verify(mockBugMapper).updateByPrimaryKeySelective(Mockito.argThat(new ObjectMatcher<Bug>() {
            @Override
            public boolean verifymatches(Bug item) {
                return item.getDeleted() == true;
            }
        }));
    }

    @Test
    public void testGetAllBugsByProject() {
        List<Bug> list = testedBugServiceImpl.getAllBugsByProject(ModuleHelper.projectId);
        verify(mockBugMapper).selectByExample(Mockito.argThat(new ExampleMatcher<BugExample>() {
            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "projectId", ModuleHelper.projectId);
            }
        }));
        assertEquals(2, list.size());
        runAsserts(list.get(0));

    }

    @Test
    public void testGetBugsByStatusByProject() {

        List<Bug> list = testedBugServiceImpl.getBugsByStatusByProject(ModuleHelper.projectId, BugStatus.APPROVED.getValue());

        verify(mockBugMapper).selectByExample(Mockito.argThat(new ExampleMatcher<BugExample>() {
            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "projectId", ModuleHelper.projectId)
                        && CriterionVerifier.verifyEqualTo(example, "status", BugStatus.APPROVED.getValue());
            }
        }));
        assertEquals(2, list.size());
        runAsserts(list.get(0));
    }

    @Test
    public void testGetOpenedBugsByProject() {

        List<Bug> list = testedBugServiceImpl.getOpenedBugsByProject(ModuleHelper.projectId, ModuleHelper.start,
                ModuleHelper.limit);

        verify(mockBugMapper).selectByExample(Mockito.argThat(new ExampleMatcher<BugExample>() {
            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "projectId", ModuleHelper.projectId)
                        && CriterionVerifier.verifyEqualTo(example, "deleted", false)
                        && CriterionVerifier.verifyStart(example, ModuleHelper.start)
                        && CriterionVerifier.verifyLimit(example, ModuleHelper.limit)
                        && CriterionVerifier.verifyGraterThan(example, "status", 0)
                        && CriterionVerifier.verifyOrderByClause(example, "id desc");
            }
        }));
        assertEquals(2, list.size());
        runAsserts(list.get(0));
    }

    @Test
    public void testGetFinishedBugsByProject() {
        List<Bug> list = testedBugServiceImpl.getFinishedBugsByProject(ModuleHelper.projectId, ModuleHelper.start,
                ModuleHelper.limit);
        verify(mockBugMapper).selectByExample(Mockito.argThat(new ExampleMatcher<BugExample>() {
            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "projectId", ModuleHelper.projectId)
                        && CriterionVerifier.verifyEqualTo(example, "deleted", false)
                        && CriterionVerifier.verifyStart(example, ModuleHelper.start)
                        && CriterionVerifier.verifyLimit(example, ModuleHelper.limit)
                        && CriterionVerifier.verifyEqualTo(example, "status", 0)
                        && CriterionVerifier.verifyOrderByClause(example, "id desc");
            }
        }));
        assertEquals(2, list.size());
        runAsserts(list.get(0));
    }

    @Test
    public void testGetItemByIdInProject_NullTest() {
        Mockito.reset(mockBugMapper);
        List<Bug> emptyList = new ArrayList<Bug>();
        when(mockBugMapper.selectByExample(Mockito.any(BugExample.class))).thenReturn(emptyList);

        ProjectItem projectItem = testedBugServiceImpl.getItemByIdInProject(ModuleHelper.projectId, ModuleHelper.idInProject);
        verify(mockBugMapper).selectByExample(Mockito.argThat(new ExampleMatcher<BugExample>() {
            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "projectId", ModuleHelper.projectId)
                        && CriterionVerifier.verifyEqualTo(example, "idInProject", ModuleHelper.idInProject);
            }
        }));

        assertNull(projectItem);
    }

    @Test
    public void testGetItemByIdInProject_NoramlTest() {
        Mockito.reset(mockBugMapper);
        List<Bug> bugList = new ArrayList<Bug>();
        Bug bug = getASampleBug();
        bugList.add(bug);
        when(mockBugMapper.selectByExample(Mockito.any(BugExample.class))).thenReturn(bugList);

        ProjectItem projectItem = testedBugServiceImpl.getItemByIdInProject(ModuleHelper.projectId, ModuleHelper.idInProject);
        verify(mockBugMapper).selectByExample(Mockito.argThat(new ExampleMatcher<BugExample>() {
            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "projectId", ModuleHelper.projectId)
                        && CriterionVerifier.verifyEqualTo(example, "idInProject", ModuleHelper.idInProject);
            }
        }));

        assertNotNull(projectItem);
        assertEquals(bug, projectItem);
    }

    @Test
    public void testGetCompletedBugsBetweenDates() {
        Project project = ModuleHelper.getASampleProject();
        when(mockProjectService.getById(ModuleHelper.projectId)).thenReturn(project);
        List<Bug> list = testedBugServiceImpl.getCompletedBugsBetweenDates(ModuleHelper.companyId, ModuleHelper.since,
                ModuleHelper.until);

        final Date since = new DateTime(ModuleHelper.since).withTimeAtStartOfDay().toDate();
        final Date until = new DateTime(ModuleHelper.until).withTimeAtStartOfDay().plusDays(1).toDate();

        verify(mockBugMapper).selectByExample(Mockito.argThat(new ExampleMatcher<BugExample>() {
            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "companyId", ModuleHelper.companyId)
                        && CriterionVerifier.verifyEqualTo(example, "status", 0)
                        && CriterionVerifier.verifyGraterThanOrEqualTo(example, "completedTime", since)
                        && CriterionVerifier.verifyLessThan(example, "completedTime", until);
            }
        }));
        verify(mockProjectService, times(2)).getById(ModuleHelper.projectId);
        assertNotNull(list);
        assertEquals(2, list.size());
        assertEquals(project, list.get(0).getProject());
        runAsserts(list.get(0));

    }

    @Test
    public void testGetCompletedBugsGroupByDateByUser_Test1() {
        List<Integer> projectList = new ArrayList<Integer>();
        TreeMap<Date, Map<Integer, List<Bug>>> ret = testedBugServiceImpl.getCompletedBugsGroupByDateByUser(
                ModuleHelper.companyId, ModuleHelper.userId, projectList, ModuleHelper.until, ModuleHelper.limit);

        assertNotNull(ret);
        assertEquals(0, ret.size());
    }

    @Test
    public void testGetCompletedBugsGroupByDateByUser_Test2() {
        List<Integer> projectList = new ArrayList<Integer>();
        projectList.add(ModuleHelper.projectId);

        Mockito.reset(mockBugMapper);
        when(mockBugMapper.selectByExample(Mockito.any(BugExample.class))).thenReturn(null);

        TreeMap<Date, Map<Integer, List<Bug>>> ret = testedBugServiceImpl.getCompletedBugsGroupByDateByUser(
                ModuleHelper.companyId, ModuleHelper.userId, projectList, ModuleHelper.until, ModuleHelper.limit);
        assertNotNull(ret);
        assertEquals(0, ret.size());
    }

    @Test
    public void testGetCompletedBugsGroupByDateByUser_Test3() {
        List<Integer> projectList = new ArrayList<Integer>();
        projectList.add(ModuleHelper.projectId);

        Mockito.reset(mockBugMapper);
        when(mockBugMapper.selectByExample(Mockito.any(BugExample.class))).thenReturn(new ArrayList<Bug>());

        TreeMap<Date, Map<Integer, List<Bug>>> ret = testedBugServiceImpl.getCompletedBugsGroupByDateByUser(
                ModuleHelper.companyId, ModuleHelper.userId, projectList, ModuleHelper.until, ModuleHelper.limit);
        assertNotNull(ret);
        assertEquals(0, ret.size());
    }

    @Test
    public void testGetCompletedBugsGroupByDateByUser_NormalTest() {
        List<Integer> projectList = new ArrayList<Integer>();
        projectList.add(ModuleHelper.projectId);
        testedBugServiceImpl.getCompletedBugsGroupByDateByUser(
                ModuleHelper.companyId, ModuleHelper.userId, projectList, ModuleHelper.until, ModuleHelper.limit);

        verify(mockBugMapper, times(2)).selectByExample(AdditionalMatchers.or(Mockito.argThat(new ExampleMatcher<BugExample>() {
            @Override
            public boolean matches(BaseExample example) {
                // verify bugMapper within getCompletedBugsGroupByDateByUser method
                return CriterionVerifier.verifyEqualTo(example, "assigneeId", ModuleHelper.userId)
                        && CriterionVerifier.verifyEqualTo(example, "deleted", false)
                        && CriterionVerifier.verifyEqualTo(example, "status", BugStatus.CLOSED.getValue())
                        && CriterionVerifier.verifyEqualTo(example, "companyId", ModuleHelper.companyId)
                        && CriterionVerifier.verifyLimit(example, ModuleHelper.limit)
                        && CriterionVerifier.verifyLessThanOrEqualTo(example, "completedTime", ModuleHelper.until)
                        && CriterionVerifier.verifyOrderByClause(example, "completedTime desc");
            }
        }), Mockito.argThat(new ExampleMatcher<BugExample>() {
            @Override
            public boolean matches(BaseExample example) {
                // verify bugMapper within appendBugsOfLastDay method
                return CriterionVerifier.verifyBetween(example, "completedTime", ModuleHelper.start, ModuleHelper.until);
            }
        })));

    }

    @Test
    public void testGetOpenBugsByUser_If_one() {
        List<Integer> projectList = new ArrayList<Integer>();
        TreeMap<Integer, List<Bug>> retMap = testedBugServiceImpl.getOpenBugsByUser(ModuleHelper.userId, projectList);
        assertNotNull(retMap);
        assertEquals(0, retMap.size());
    }

    @Test
    public void testGetOpenBugsByUser_If_two() {
        List<Integer> projectList = new ArrayList<Integer>();
        projectList.add(ModuleHelper.projectId);

        Mockito.reset(mockBugMapper);
        when(mockBugMapper.selectByExample(Mockito.any(BugExample.class))).thenReturn(new ArrayList<Bug>());

        TreeMap<Integer, List<Bug>> retMap = testedBugServiceImpl.getOpenBugsByUser(ModuleHelper.userId, projectList);
        assertNotNull(retMap);
        assertEquals(0, retMap.size());
    }

    @Test
    public void testGetOpenBugsByUser() {
        final List<Integer> projectList = new ArrayList<Integer>();
        projectList.add(ModuleHelper.projectId);
        TreeMap<Integer, List<Bug>> retMap = testedBugServiceImpl.getOpenBugsByUser(ModuleHelper.userId, projectList);

        verify(mockBugMapper).selectByExample(Mockito.argThat(new ExampleMatcher<BugExample>() {
            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "deleted", false)
                        && CriterionVerifier.verifyEqualTo(example, "assigneeId", ModuleHelper.userId)
                        && CriterionVerifier.verifyNotEqualTo(example, "status", BugStatus.CLOSED.getValue())
                        && CriterionVerifier.verifyIn(example, "projectId", projectList);
            }
        }));

        assertNotNull(retMap);
        assertEquals(1, retMap.size());
    }

    @Test
    public void testGetOpenBugsBetweenDatesByUser() {
        List<Bug> list = testedBugServiceImpl.getOpenBugsBetweenDatesByUser(ModuleHelper.companyId, ModuleHelper.userId,
                ModuleHelper.since, ModuleHelper.until);

        verify(mockBugMapper).selectByExample(Mockito.argThat(new ExampleMatcher<BugExample>() {
            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "companyId", ModuleHelper.companyId)
                        && CriterionVerifier.verifyEqualTo(example, "assigneeId", ModuleHelper.userId)
                        && CriterionVerifier.verifyNotEqualTo(example, "status", BugStatus.CLOSED.getValue())
                        && CriterionVerifier.verifyGraterThanOrEqualTo(example, "dueTime", ModuleHelper.since)
                        && CriterionVerifier.verifyLessThan(example, "dueTime", ModuleHelper.until);
            }
        }));

        assertEquals(2, list.size());
        runAsserts(list.get(0));

    }

    @Test
    public void testGetCompletedBugAveDurationByProjectIdDateBackByMonth_If_One() {
        Integer months = 1;
        long ret = testedBugServiceImpl.getCompletedBugAveDurationByProjectIdDateBackByMonth(ModuleHelper.projectId, months);

        DateTime dt = new DateTime(new Date());
        final Date since = dt.withTimeAtStartOfDay().plusMonths(-months).toDate();
        final Date until = dt.withTimeAtStartOfDay().plusDays(1).toDate();

        verify(mockBugMapper).selectByExample(Mockito.argThat(new ExampleMatcher<BugExample>() {
            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "projectId", ModuleHelper.projectId)
                        && CriterionVerifier.verifyEqualTo(example, "status", BugStatus.CLOSED.getValue())
                        && CriterionVerifier.verifyGraterThanOrEqualTo(example, "completedTime", since)
                        && CriterionVerifier.verifyLessThan(example, "completedTime", until);
            }
        }));
        assertEquals(0, ret);

    }

    @Test
    public void testGetCompletedBugAveDurationByProjectIdDateBackByMonth_if_two() {
        Mockito.reset(mockBugMapper);

        List<Bug> list = new ArrayList<Bug>();
        long interval = 111111;
        long created = Long.parseLong("1441602000000");
        long completed = created + interval;

        for (int i = 0; i < 20; i++) {
            Bug bug = new Bug();
            bug.setId(i);
            bug.setCreated(new Date(created));
            bug.setCompletedTime(new Date(completed));
            list.add(bug);
        }

        when(mockBugMapper.selectByExample(Mockito.any(BugExample.class))).thenReturn(list);
        long ret = testedBugServiceImpl.getCompletedBugAveDurationByProjectIdDateBackByMonth(ModuleHelper.projectId, 1);
        assertEquals(interval, ret);
    }

    @Test
    public void testRecoverIdentifiableById() {
        testedBugServiceImpl.recoverIdentifiableById(ModuleHelper.id);
        verify(mockBugMapper).updateByPrimaryKeySelective(Mockito.argThat(new ObjectMatcher<Bug>() {
            @Override
            public boolean verifymatches(Bug item) {
                return (item.getDeleted() == false) && (item.getId() == ModuleHelper.id);
            }
        }));
    }
}
