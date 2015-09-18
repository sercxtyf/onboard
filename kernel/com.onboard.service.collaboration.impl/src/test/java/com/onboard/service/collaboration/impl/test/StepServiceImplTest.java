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
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.AdditionalMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.onboard.domain.mapper.StepMapper;
import com.onboard.domain.mapper.UserMapper;
import com.onboard.domain.mapper.model.StepExample;
import com.onboard.domain.mapper.model.common.BaseExample;
import com.onboard.domain.model.IterationItemStatus;
import com.onboard.domain.model.Step;
import com.onboard.service.account.UserService;
import com.onboard.service.collaboration.IdInProjectService;
import com.onboard.service.collaboration.IterationService;
import com.onboard.service.collaboration.KeywordService;
import com.onboard.service.collaboration.ProjectService;
import com.onboard.service.collaboration.impl.StepServiceImpl;
import com.onboard.service.web.SessionService;
import com.onboard.test.exampleutils.CriterionVerifier;
import com.onboard.test.exampleutils.ExampleMatcher;
import com.onboard.test.exampleutils.ObjectMatcher;
import com.onboard.test.moduleutils.ModuleHelper;

@RunWith(MockitoJUnitRunner.class)
public class StepServiceImplTest {

    @InjectMocks
    private StepServiceImpl testedStepServiceImpl;
    
    @Mock
    StepMapper mockStepMapper;
    
    @Mock
    UserMapper mockUserMapper;
    
    @Mock
    UserService mockUserService;
    
    @Mock
    ProjectService mockProjectService;
    
    @Mock
    SessionService mockSessionService;
    
    @Mock
    IdInProjectService mockIdInProjectService;
    
    @Mock
    IterationService mockIterationService;
    
    @Mock
    KeywordService mockKeywordService;
        
    /**
     * Generate a sample list with custom id
     * @param stepId
     * @return A Sample of Step that has the given stepId
     */
    public Step getASampleStep(int stepId) {
        Step step = new Step();
        step.setId(stepId);
        step.setAssigneeId(ModuleHelper.userId);
        step.setProjectId(ModuleHelper.projectId);
        step.setCompletedTime(ModuleHelper.until);
        step.setDueDate(ModuleHelper.until);
        step.setStatus(IterationItemStatus.CLOSED.getValue());
        return step;
    }
    
    /**
     * Generate a sample step-list
     * @param size
     * @return A list of step which id is ranged from 0 to size - 1
     */
    public List<Step> getAListOfSampleSteps(int size) {
        List<Step> list = new ArrayList<Step>();
        for (int i = 0; i < size; i++) {
            list.add(getASampleStep(i));
        }
        return list;
    }
        
    /**
     * Assert a step with getASampleStep(stepId)
     * @param step - the step need to be assert
     * @param stepId - the id that the step should have
     */
    public void runAsserts(Step step, int stepId, Boolean needAssignee) {
        assertEquals(stepId, (int) step.getId());
        if (needAssignee) {
            assertTrue(ModuleHelper.isSampleUser(step.getAssignee()));
        }
    }
    
    /**
     * Assert a step-list with getAListOfSampleSteps(size)
     * @param list - the list need to be assert
     * @param size - the size that the list should have
     */
    public void runAsserts(List<Step> list, int size, Boolean needAssignee) {
        assertEquals(size, list.size());
        for (int i = 0; i < size; i++) {
            runAsserts(list.get(i), i, needAssignee);
        }
    }
    
    @Before
    public void setupStepServiceTest() {
        Mockito.doReturn(ModuleHelper.getASampleUser()).when(mockUserService).getById(ModuleHelper.userId);
    }
    
    /**
     * @author Steven
     * Test that such step doesn't exist
     */
    @Test
    public void testGetById_Test1() {
        Mockito.doReturn(null).when(mockStepMapper).selectByPrimaryKey(ModuleHelper.stepId);
        
        Step step = testedStepServiceImpl.getById(ModuleHelper.stepId);
        
        Mockito.verify(mockStepMapper, times(1)).selectByPrimaryKey(ModuleHelper.stepId);
        Mockito.verifyNoMoreInteractions(mockStepMapper);
        
        assertNull("Step should be null in this test", step);
    }
    
    /**
     * @author Steven
     * Test that such step doesn't exist
     */
    @Test
    public void testGetById_Test2() {
        Mockito.doReturn(getASampleStep(ModuleHelper.stepId)).when(mockStepMapper).selectByPrimaryKey(ModuleHelper.stepId);
        
        Step step = testedStepServiceImpl.getById(ModuleHelper.stepId);
        
        Mockito.verify(mockStepMapper, times(1)).selectByPrimaryKey(ModuleHelper.stepId);
        Mockito.verifyNoMoreInteractions(mockStepMapper);
        
        runAsserts(step, ModuleHelper.stepId, true);
    }
    
    /**
     * @author Steven
     */
    @Test
    public void testCreate() {
        Mockito.doReturn(ModuleHelper.getASampleUser()).when(mockSessionService).getCurrentUser();
        Mockito.doReturn(ModuleHelper.idInProject).when(mockIdInProjectService).getNextIdByProjectId(Mockito.anyInt());
        Mockito.doReturn(0).when(mockStepMapper).insert(Mockito.any(Step.class));
        Mockito.doNothing().when(mockIterationService).addIterable(Mockito.any(Step.class));
        
        testedStepServiceImpl.create(getASampleStep(ModuleHelper.stepId));
        
        Mockito.verify(mockStepMapper, times(1)).insert(Mockito.argThat(new ObjectMatcher<Step>() {
            @Override
            public boolean verifymatches(Step step) {
                return step.getCreatorId() == ModuleHelper.userId
                        && step.getCreatorName() == ModuleHelper.userName
                        && step.getIdInProject() == ModuleHelper.idInProject
                        && step.getAssigneeId() == ModuleHelper.userId
                        && step.getDeleted() == false;
            }
        }));
        Mockito.verifyNoMoreInteractions(mockStepMapper);
    }
    
    /**
     * @author Steven
     * Test that update due-date
     */
    @Test
    public void testUpdate_Test1() {
        Step oldStep = getASampleStep(ModuleHelper.stepId); 
        Step newStep = getASampleStep(ModuleHelper.stepId);
        oldStep.setDueDate(ModuleHelper.getDateByString("2014-04-01 00:00"));
        newStep.setDueDate(ModuleHelper.getDateByString("2014-04-02 00:00"));
        final DateTime dt = new DateTime(newStep.getDueDate());

        StepServiceImpl spyStepServiceImpl = Mockito.spy(testedStepServiceImpl);
        Mockito.doReturn(0).when(mockStepMapper).updateByPrimaryKeySelective(Mockito.any(Step.class));
        Mockito.doReturn(oldStep).when(spyStepServiceImpl).getById(Mockito.anyInt());
        
        spyStepServiceImpl.updateSelective(newStep);
        
        Mockito.verify(spyStepServiceImpl).updateSelective(newStep);
        Mockito.verify(spyStepServiceImpl).getById(ModuleHelper.stepId);
        Mockito.verify(mockStepMapper).updateByPrimaryKeySelective(Mockito.argThat(new ObjectMatcher<Step>() {
            @Override
            public boolean verifymatches(Step step) {
                return step.getDueDate().equals(dt.withTimeAtStartOfDay().plusDays(1).plusSeconds(-1).toDate());
            }
        }));
    }
    
    /**
     * @author Steven
     * Test that update status to CLOSE
     */
    @Test
    public void testUpdate_Test2() {
        Step oldStep = getASampleStep(ModuleHelper.stepId); 
        Step newStep = getASampleStep(ModuleHelper.stepId);
        oldStep.setStatus(IterationItemStatus.TODO.getValue());
        newStep.setStatus(IterationItemStatus.CLOSED.getValue());

        StepServiceImpl spyStepServiceImpl = Mockito.spy(testedStepServiceImpl);
        Mockito.doReturn(0).when(mockStepMapper).updateByPrimaryKeySelective(Mockito.any(Step.class));
        Mockito.doReturn(oldStep).when(spyStepServiceImpl).getById(Mockito.anyInt());
        Mockito.doNothing().when(mockKeywordService).addKeywordToUser(Mockito.any(Step.class), Mockito.anyInt());
        Mockito.doReturn(ModuleHelper.getASampleUser()).when(mockSessionService).getCurrentUser();
        
        spyStepServiceImpl.updateSelective(newStep);
        
        Mockito.verify(spyStepServiceImpl).updateSelective(newStep);
        Mockito.verify(spyStepServiceImpl).getById(ModuleHelper.stepId);
        Mockito.verify(mockStepMapper).updateByPrimaryKeySelective(Mockito.argThat(new ObjectMatcher<Step>() {
            @Override
            public boolean verifymatches(Step step) {
                return step.getCompleterId() == ModuleHelper.userId;
            }
        }));
    }
    
    /**
     * @author Steven
     */
    @Test
    public void testGetByAttachTypeAndId() {
        Mockito.doReturn(getAListOfSampleSteps(3)).when(mockStepMapper).selectByExample(Mockito.argThat(new ExampleMatcher<StepExample>() {
            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "attachType", ModuleHelper.attachType)
                        && CriterionVerifier.verifyEqualTo(example, "attachId", ModuleHelper.attachId);
            }
        }));
        
        List<Step> list = testedStepServiceImpl.getByAttachTypeAndId(ModuleHelper.attachType, ModuleHelper.attachId);
        
        Mockito.verify(mockStepMapper, times(1)).selectByExample(Mockito.argThat(new ExampleMatcher<StepExample>() {
            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "attachType", ModuleHelper.attachType)
                        && CriterionVerifier.verifyEqualTo(example, "attachId", ModuleHelper.attachId);
            }
        }));
        
        runAsserts(list, 3, true);
    }
    
    /**
     * @author Steven
     * Test that such Item doesn't exist
     */
    @Test
    public void testGetItemByIdInProject_Test1() {
        Mockito.doReturn(new ArrayList<Step>()).when(mockStepMapper).selectByExample(Mockito.argThat(new ExampleMatcher<StepExample>() {
            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "projectId", ModuleHelper.projectId)
                        && CriterionVerifier.verifyEqualTo(example, "idInProject", ModuleHelper.idInProject);
            }
        }));
        
        Step step = (Step) testedStepServiceImpl.getItemByIdInProject(ModuleHelper.projectId, ModuleHelper.idInProject);
        
        Mockito.verify(mockStepMapper).selectByExample(Mockito.argThat(new ExampleMatcher<StepExample>() {
            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "projectId", ModuleHelper.projectId)
                        && CriterionVerifier.verifyEqualTo(example, "idInProject", ModuleHelper.idInProject);
            }
        }));
        Mockito.verifyNoMoreInteractions(mockStepMapper);
        
        assertNull("Step should be null in this test", step);
    }
    
    /**
     * @author Steven
     * Test that such Item exists
     */
    @Test
    public void testGetItemByIdInProject_Test2() {
        Mockito.doReturn(getAListOfSampleSteps(1)).when(mockStepMapper).selectByExample(Mockito.argThat(new ExampleMatcher<StepExample>() {
            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "projectId", ModuleHelper.projectId)
                        && CriterionVerifier.verifyEqualTo(example, "idInProject", ModuleHelper.idInProject);
            }
        }));
        
        Step step = (Step) testedStepServiceImpl.getItemByIdInProject(ModuleHelper.projectId, ModuleHelper.idInProject);
        
        Mockito.verify(mockStepMapper).selectByExample(Mockito.argThat(new ExampleMatcher<StepExample>() {
            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "projectId", ModuleHelper.projectId)
                        && CriterionVerifier.verifyEqualTo(example, "idInProject", ModuleHelper.idInProject);
            }
        }));
        Mockito.verifyNoMoreInteractions(mockStepMapper);
        
        runAsserts(step, 0, false);
    }
    
    /**
     * @author Steven
     */
    @Test
    public void testGetCompletedStepsBetweenDates() {
        Mockito.doReturn(getAListOfSampleSteps(3)).when(mockStepMapper).selectByExample(Mockito.any(StepExample.class));
        Mockito.doReturn(ModuleHelper.getASampleProject()).when(mockProjectService).getById(ModuleHelper.projectId);

        DateTime dt = new DateTime(ModuleHelper.since);
        final Date since = dt.withTimeAtStartOfDay().toDate();
        dt = new DateTime(ModuleHelper.until);
        final Date until = dt.withTimeAtStartOfDay().plusDays(1).toDate();
        
        List<Step> list = testedStepServiceImpl.getCompletedStepsBetweenDates(ModuleHelper.companyId, ModuleHelper.since, ModuleHelper.until);
        
        Mockito.verify(mockStepMapper).selectByExample(Mockito.argThat(new ExampleMatcher<StepExample>() {
            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "companyId", ModuleHelper.companyId)
                        && CriterionVerifier.verifyEqualTo(example, "status", IterationItemStatus.CLOSED.getValue())
                        && CriterionVerifier.verifyGraterThanOrEqualTo(example, "completedTime", since)
                        && CriterionVerifier.verifyLessThan(example, "completedTime", until);
            }
        }));
        Mockito.verify(mockProjectService, times(3)).getById(ModuleHelper.projectId);
        Mockito.verifyNoMoreInteractions(mockStepMapper);
        Mockito.verifyNoMoreInteractions(mockProjectService);
        
        runAsserts(list, 3, false);
        for (Step step : list) {
            assertNotNull(step.getProject());
        }
    }
    
    /**
     * @author Steven
     */
    @Test
    public void testGetOpenStepsBetweenDatesByUser() {
        Mockito.doReturn(getAListOfSampleSteps(3)).when(mockStepMapper).selectByExample(Mockito.any(StepExample.class));
        
        List<Step> list = testedStepServiceImpl.getOpenStepsBetweenDatesByUser(
                ModuleHelper.companyId, ModuleHelper.userId, ModuleHelper.since, ModuleHelper.until);
        
        Mockito.verify(mockStepMapper).selectByExample(Mockito.argThat(new ExampleMatcher<StepExample>() {
            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "companyId", ModuleHelper.companyId)
                        && CriterionVerifier.verifyEqualTo(example, "assigneeId", ModuleHelper.userId)
                        && CriterionVerifier.verifyGraterThanOrEqualTo(example, "dueDate", ModuleHelper.since)
                        && CriterionVerifier.verifyLessThan(example, "dueDate", ModuleHelper.until)
                        && CriterionVerifier.verifyNotEqualTo(example, "status", IterationItemStatus.CLOSED.getValue());
            }
        }));
        Mockito.verifyNoMoreInteractions(mockStepMapper);
        
        runAsserts(list, 3, false);
    }
    
    /**
     * @author Steven
     * Test that projectList is empty
     */
    @Test
    public void testGetCompletedStepsGroupByDateByUser_Test1() {
        TreeMap<Date, Map<Integer, List<Step>>> treeMap = 
                testedStepServiceImpl.getCompletedStepsGroupByDateByUser(
                        ModuleHelper.companyId, ModuleHelper.userId, new ArrayList<Integer>(), ModuleHelper.until, ModuleHelper.limit);
        
        Mockito.verifyNoMoreInteractions(mockStepMapper);
        
        assertEquals(0, treeMap.size());
    }
    
    /**
     * @author Steven
     * Test that projectList isn't empty
     */
    @Test
    public void testGetCompletedStepsGroupByDateByUser_Test2() {
        Mockito.doReturn(getAListOfSampleSteps(3)).when(mockStepMapper).selectByExample(Mockito.any(StepExample.class));
        
        final List<Integer> projectList = new ArrayList<Integer>();
        projectList.add(ModuleHelper.projectId);
        
        Step lastStep = getAListOfSampleSteps(3).get(2);
        final Date newUntil = new DateTime(lastStep.getCompletedTime()).withTimeAtStartOfDay().toDate();
        
        testedStepServiceImpl.getCompletedStepsGroupByDateByUser(
                        ModuleHelper.companyId, ModuleHelper.userId, projectList, ModuleHelper.until, ModuleHelper.limit);
        
        Mockito.verify(mockStepMapper, times(2)).selectByExample(AdditionalMatchers.or(Mockito.argThat(new ExampleMatcher<StepExample>() {
            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "companyId", ModuleHelper.companyId)
                        && CriterionVerifier.verifyEqualTo(example, "assigneeId", ModuleHelper.userId)
                        && CriterionVerifier.verifyEqualTo(example, "deleted", false)
                        && CriterionVerifier.verifyEqualTo(example, "status", IterationItemStatus.CLOSED.getValue())
                        && CriterionVerifier.verifyIn(example, "projectId", projectList)
                        && CriterionVerifier.verifyLessThanOrEqualTo(example, "completedTime", ModuleHelper.until)
                        && example.getLimit() == ModuleHelper.limit
                        && example.getOrderByClause() == "completedTime desc";
            }
        }),
        Mockito.argThat(new ExampleMatcher<StepExample>() {
            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "companyId", ModuleHelper.companyId)
                        && CriterionVerifier.verifyEqualTo(example, "assigneeId", ModuleHelper.userId)
                        && CriterionVerifier.verifyEqualTo(example, "deleted", false)
                        && CriterionVerifier.verifyEqualTo(example, "status", IterationItemStatus.CLOSED.getValue())
                        && CriterionVerifier.verifyIn(example, "projectId", projectList)
                        && CriterionVerifier.verifyLessThanOrEqualTo(example, "completedTime", ModuleHelper.until)
                        && example.getLimit() == ModuleHelper.limit
                        && example.getOrderByClause() == "completedTime desc"
                        && CriterionVerifier.verifyBetween(example, "completedTime", newUntil, ModuleHelper.until);
            }
        })));
        Mockito.verifyNoMoreInteractions(mockStepMapper);
        
        // TODO assert the treemap
    }
    
    /**
     * @author Steven
     * Test that projectList is empty
     */
    @Test
    public void testGetOpenStepsByUser_Test1() {
        Map<Integer, List<Step>> map = testedStepServiceImpl.getOpenStepsByUser(ModuleHelper.userId, new ArrayList<Integer>());
        
        Mockito.verifyNoMoreInteractions(mockStepMapper);
        
        assertEquals(0, map.size());
    }
    
    /**
     * @author Steven
     * Test that projectList isn't empty
     */
    @Test
    public void testGetOpenStepsByUser_Test2() {
        List<Step> steps = getAListOfSampleSteps(4);
        steps.get(0).setProjectId(0);
        steps.get(1).setProjectId(0);
        steps.get(2).setProjectId(1);
        steps.get(3).setProjectId(1);

        final List<Integer> projectList = new ArrayList<Integer>();
        projectList.add(0);
        projectList.add(1);
        
        Map<Integer, List<Step>> targetMap = new TreeMap<Integer, List<Step>>();
        List<Step> list1 = new ArrayList<Step>();
        List<Step> list2 = new ArrayList<Step>();
        list1.add(steps.get(0));
        list1.add(steps.get(1));
        list2.add(steps.get(2));
        list2.add(steps.get(3));
        targetMap.put(0, list1);
        targetMap.put(1, list2);
        
        Mockito.doReturn(steps).when(mockStepMapper).selectByExample(Mockito.any(StepExample.class));
        
        Map<Integer, List<Step>> map = testedStepServiceImpl.getOpenStepsByUser(ModuleHelper.userId, projectList);
        
        Mockito.verify(mockStepMapper).selectByExample(Mockito.argThat(new ExampleMatcher<StepExample>() {
            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "assigneeId", ModuleHelper.userId)
                        && CriterionVerifier.verifyEqualTo(example, "deleted", false)
                        && CriterionVerifier.verifyNotEqualTo(example, "status", IterationItemStatus.CLOSED.getValue())
                        && CriterionVerifier.verifyIn(example, "projectId", projectList)
                        && example.getOrderByClause() == "createdTime desc";
            }
        }));
        Mockito.verifyNoMoreInteractions(mockStepMapper);
        
        assertEquals(map, targetMap);
    }
}
