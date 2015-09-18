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

import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.Maps;
import com.onboard.domain.model.Activity;
import com.onboard.domain.model.IterationItemStatus;
import com.onboard.domain.model.Step;
import com.onboard.service.account.UserService;
import com.onboard.service.activity.ActivityActionType;
import com.onboard.service.collaboration.ProjectService;
import com.onboard.service.collaboration.activity.StepActivityGenerator;
import com.onboard.test.moduleutils.ModuleHelper;

@RunWith(MockitoJUnitRunner.class)
public class StepActivityGeneratorTest {
    
    private static final Map<String, String> TODO_STATUS_MAP = Maps.newHashMap();
    static {
        TODO_STATUS_MAP.put(IterationItemStatus.TODO.getValue(), "未开始");
        TODO_STATUS_MAP.put(IterationItemStatus.INPROGESS.getValue(), "正在做");
        TODO_STATUS_MAP.put(IterationItemStatus.FIXED.getValue(), "已提交");
        TODO_STATUS_MAP.put(IterationItemStatus.APPROVED.getValue(), "同意完成");
        TODO_STATUS_MAP.put(IterationItemStatus.REVIEWED.getValue(), "复审通过");
        TODO_STATUS_MAP.put(IterationItemStatus.VERIFIED.getValue(), "测试通过");
        TODO_STATUS_MAP.put(IterationItemStatus.CLOSED.getValue(), "已完成");
    }
    
    @InjectMocks
    StepActivityGenerator testedStepActivityGenerator;
    
    @Mock
    ProjectService mockProjectService;
    
    @Mock
    UserService mockUserService;

    @Before
    public void setupStepActivityGeneratorTest() {
        Mockito.doReturn(ModuleHelper.getASampleProject()).when(mockProjectService).getById(ModuleHelper.projectId);
        Mockito.doReturn(ModuleHelper.getASampleUser()).when(mockUserService).getById(ModuleHelper.userId);
    }
    
    public void runAsserts(Activity activity, String actionType, String subject) {
        /* StepActivityGenerator.generateActivityByActionType */
        assertEquals(ModuleHelper.projectId, (int) activity.getProjectId());
        assertEquals(ModuleHelper.companyId, (int) activity.getCompanyId());
        assertEquals(ModuleHelper.content, activity.getTarget());
        assertEquals(ModuleHelper.projectName, activity.getProjectName());
        
        /* ActivityHelper.generateActivityByActionType */
        assertEquals(ModuleHelper.stepId, (int) activity.getAttachId());
        assertEquals(new Step().getType(), activity.getAttachType());
        assertEquals(subject, activity.getSubject());
        assertEquals(actionType, activity.getAction());
        
        // TODO this should be working but isn't
        /*
        assertEquals(ModuleHelper.userId, (int) activity.getCreatorId());
        assertEquals(ModuleHelper.userName, activity.getCreatorName());
        assertTrue(ModuleHelper.compareCreatedItemDateWithToday(activity.getCreated()));
        */
    }
    
    public void runAsserts(Activity activity, Step step, String actionType, String subject) {
        /* StepActivityGenerator.generateActivityByActionType */
        assertEquals((int) step.getProjectId(), (int) activity.getProjectId());
        assertEquals((int) step.getCompanyId(), (int) activity.getCompanyId());
        assertEquals(step.getContent(), activity.getTarget());
        assertEquals(ModuleHelper.projectName, activity.getProjectName());
        
        /* ActivityHelper.generateActivityByActionType */
        assertEquals((int) step.getId(), (int) activity.getAttachId());
        assertEquals(new Step().getType(), activity.getAttachType());
        assertEquals(subject, activity.getSubject());
        assertEquals(actionType, activity.getAction());
    }
    
    /**
     * @author Steven
     * Test that assignee is null and dueDate is null
     */
    @Test
    public void testGenerateCreateActivity_Test1() {
        Step step = ModuleHelper.getASampleStep();
        step.setAssigneeId(null);
        step.setDueDate(null);
        
        Activity activity = testedStepActivityGenerator.generateCreateActivity(step);
        
        runAsserts(activity, ActivityActionType.CREATE, StepActivityGenerator.CREATE_SUBJECT);
    }
    
    /**
     * @author Steven
     * Test that assignee is null and dueDate isn't null
     */
    @Test
    public void testGenerateCreateActivity_Test2() {
        Step step = ModuleHelper.getASampleStep();
        step.setAssigneeId(null);
        step.setDueDate(ModuleHelper.dueDate);
        
        Activity activity = testedStepActivityGenerator.generateCreateActivity(step);
        
        runAsserts(activity, ActivityActionType.CREATE, StepActivityGenerator.CREATE_SUBJECT);
        assertEquals(String.format(StepActivityGenerator.DUEDATE_SET, StepActivityGenerator.dateFormat.format(ModuleHelper.dueDate)), 
                activity.getContent());
    }
    
    /**
     * @author Steven
     * Test that assignee isn't null and dueDate is null
     */
    @Test
    public void testGenerateCreateActivity_Test3() {
        Step step = ModuleHelper.getASampleStep();
        step.setAssigneeId(ModuleHelper.userId);
        step.setDueDate(null);
        
        Activity activity = testedStepActivityGenerator.generateCreateActivity(step);
        
        runAsserts(activity, ActivityActionType.CREATE, StepActivityGenerator.CREATE_SUBJECT);
        /*assertEquals(String.format(StepActivityGenerator.ASSIGNEE_AND_DUEDATE_SET, ModuleHelper.userName,
                dateFormat.format(ModuleHelper.dueDate)), activity.getContent());*/
        assertEquals(String.format(StepActivityGenerator.ASSIGNEE_SET, ModuleHelper.userName), activity.getContent());
    }
    
    /**
     * @author Steven
     * Test that assignee isn't null and dueDate isn't null
     */
    @Test
    public void testGenerateCreateActivity_Test4() {
        Step step = ModuleHelper.getASampleStep();
        step.setAssigneeId(ModuleHelper.userId);
        step.setDueDate(ModuleHelper.dueDate);
        
        Activity activity = testedStepActivityGenerator.generateCreateActivity(step);
        
        runAsserts(activity, ActivityActionType.CREATE, StepActivityGenerator.CREATE_SUBJECT);
        assertEquals(String.format(StepActivityGenerator.ASSIGNEE_AND_DUEDATE_SET, ModuleHelper.userName,
                StepActivityGenerator.dateFormat.format(ModuleHelper.dueDate)), activity.getContent());
    }
    
    /**
     * @author Steven
     * Test that delete the step
     */
    @Test
    public void testGenerateUpdateActivity_Test1() {
        Step origin = ModuleHelper.getASampleStep();
        Step update = ModuleHelper.getASampleStep();
        origin.setDeleted(false);
        update.setDeleted(true);
        
        Activity activity = testedStepActivityGenerator.generateUpdateActivity(origin, update);
        
        runAsserts(activity, ActivityActionType.DISCARD, StepActivityGenerator.DISCARD_SUBJECT);
    }
    
    /**
     * @author Steven
     * Test that recover the step
     */
    @Test
    public void testGenerateUpdateActivity_Test2() {
        Step origin = ModuleHelper.getASampleStep();
        Step update = ModuleHelper.getASampleStep();
        origin.setDeleted(true);
        update.setDeleted(false);
        
        Activity activity = testedStepActivityGenerator.generateUpdateActivity(origin, update);
        
        runAsserts(activity, ActivityActionType.RECOVER, StepActivityGenerator.RECOVER_SUBJECT);
    }
    
    /**
     * @author Steven
     * Test that complete the step
     */
    @Test
    public void testGenerateUpdateActivity_Test3() {
        Step origin = ModuleHelper.getASampleStep();
        Step update = ModuleHelper.getASampleStep();
        origin.setStatus(IterationItemStatus.TODO.getValue());
        update.setStatus(IterationItemStatus.CLOSED.getValue());
        
        Activity activity = testedStepActivityGenerator.generateUpdateActivity(origin, update);
 
        runAsserts(activity, IterationItemStatus.CLOSED.getValue(), "完成了任务");
    }
    
    /**
     * @author Steven
     * Test that inprogress the step
     */
    @Test
    public void testGenerateUpdateActivity_Test4() {
        Step origin = ModuleHelper.getASampleStep();
        Step update = ModuleHelper.getASampleStep();
        origin.setStatus(IterationItemStatus.TODO.getValue());
        update.setStatus(IterationItemStatus.INPROGESS.getValue());
        
        Activity activity = testedStepActivityGenerator.generateUpdateActivity(origin, update);
 
        runAsserts(activity, IterationItemStatus.INPROGESS.getValue(), "正在做任务");
    }
    
    /**
     * @author Steven
     * Test that stop the step
     */
    @Test
    public void testGenerateUpdateActivity_Test5() {
        Step origin = ModuleHelper.getASampleStep();
        Step update = ModuleHelper.getASampleStep();
        origin.setStatus(IterationItemStatus.INPROGESS.getValue());
        update.setStatus(IterationItemStatus.TODO.getValue());
        
        Activity activity = testedStepActivityGenerator.generateUpdateActivity(origin, update);
 
        runAsserts(activity, IterationItemStatus.TODO.getValue(), "停止了任务");
    }
    
    /**
     * @author Steven
     * Test that normally update the status of step
     */
    @Test
    public void testGenerateUpdateActivity_Test6() {
        Step origin = ModuleHelper.getASampleStep();
        Step update = ModuleHelper.getASampleStep();
        origin.setStatus(IterationItemStatus.TODO.getValue());
        update.setStatus(IterationItemStatus.FIXED.getValue());
        
        Activity activity = testedStepActivityGenerator.generateUpdateActivity(origin, update);
 
        runAsserts(activity, IterationItemStatus.FIXED.getValue(), String.format(StepActivityGenerator.STATUS_SUBJECT, 
                TODO_STATUS_MAP.get(origin.getStatus()), TODO_STATUS_MAP.get(update.getStatus())));
    }
    
    /**
     * @author Steven
     * Test that nothing changed
     */
    @Test
    public void testGenerateUpdateActivity_Test7() {
        Step origin = ModuleHelper.getASampleStep();
        Step update = ModuleHelper.getASampleStep();
        
        Activity activity = testedStepActivityGenerator.generateUpdateActivity(origin, update);
 
        assertNull(activity);
    }
    
    /**
     * @author Steven
     * Test that update name
     */
    @Test
    public void testGenerateUpdateActivity_Test8() {
        Step origin = ModuleHelper.getASampleStep();
        Step update = ModuleHelper.getASampleStep();
        origin.setContent("Content1");
        update.setContent("Content2");
        
        Activity activity = testedStepActivityGenerator.generateUpdateActivity(origin, update);

        runAsserts(activity, origin, ActivityActionType.UPDATE, StepActivityGenerator.UPDATE_SUBJECT);
        assertEquals(String.format(StepActivityGenerator.NAME_UPDATE, origin.getContent(), update.getContent()), activity.getContent());
    }
    
    /**
     * @author Steven
     * Test that set assignee from null to someone
     */
    @Test
    public void testGenerateUpdateActivity_Test9() {
        Step origin = ModuleHelper.getASampleStep();
        Step update = ModuleHelper.getASampleStep();
        origin.setAssigneeId(null);
        update.setAssigneeId(ModuleHelper.userId);
        
        Activity activity = testedStepActivityGenerator.generateUpdateActivity(origin, update);

        runAsserts(activity, origin, ActivityActionType.UPDATE, StepActivityGenerator.UPDATE_SUBJECT);
        assertEquals(String.format(StepActivityGenerator.ASSIGNEE_UPDATE, StepActivityGenerator.ASSIGNEE_NULL, ModuleHelper.userName), activity.getContent());
    }

    /**
     * @author Steven
     * Test that set assignee from someone to null
     */
    @Test
    public void testGenerateUpdateActivity_Test10() {
        Step origin = ModuleHelper.getASampleStep();
        Step update = ModuleHelper.getASampleStep();
        origin.setAssigneeId(ModuleHelper.userId);
        update.setAssigneeId(null);
        
        Activity activity = testedStepActivityGenerator.generateUpdateActivity(origin, update);

        runAsserts(activity, origin, ActivityActionType.UPDATE, StepActivityGenerator.UPDATE_SUBJECT);
        assertEquals(String.format(StepActivityGenerator.ASSIGNEE_UPDATE, ModuleHelper.userName, StepActivityGenerator.ASSIGNEE_NULL), activity.getContent());
    }
    
    /**
     * @author Steven
     * Test that set dueDate from null to some date
     */
    @Test
    public void testGenerateUpdateActivity_Test11() {
        Step origin = ModuleHelper.getASampleStep();
        Step update = ModuleHelper.getASampleStep();
        origin.setDueDate(null);
        update.setDueDate(ModuleHelper.dueDate);
        
        Activity activity = testedStepActivityGenerator.generateUpdateActivity(origin, update);

        runAsserts(activity, origin, ActivityActionType.UPDATE, StepActivityGenerator.UPDATE_SUBJECT);
        assertEquals(String.format(StepActivityGenerator.DUEDATE_UPDATE, StepActivityGenerator.DUEDATE_NULL, 
                StepActivityGenerator.dateFormat.format(update.getDueDate())), activity.getContent());
    }
    
    /**
     * @author Steven
     * Test that set dueDate from some date to null
     */
    @Test
    public void testGenerateUpdateActivity_Test12() {
        Step origin = ModuleHelper.getASampleStep();
        Step update = ModuleHelper.getASampleStep();
        origin.setDueDate(ModuleHelper.dueDate);
        update.setDueDate(null);
        
        Activity activity = testedStepActivityGenerator.generateUpdateActivity(origin, update);

        runAsserts(activity, origin, ActivityActionType.UPDATE, StepActivityGenerator.UPDATE_SUBJECT);
        assertEquals(String.format(StepActivityGenerator.DUEDATE_UPDATE, StepActivityGenerator.dateFormat.format(ModuleHelper.dueDate), 
                StepActivityGenerator.DUEDATE_NULL), activity.getContent());
    }
}
