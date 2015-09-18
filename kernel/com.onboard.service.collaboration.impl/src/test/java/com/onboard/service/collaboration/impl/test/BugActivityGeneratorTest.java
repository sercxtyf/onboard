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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.onboard.domain.model.Activity;
import com.onboard.domain.model.Bug;
import com.onboard.service.activity.ActivityActionType;
import com.onboard.service.collaboration.activity.BugActivityGenerator;
import com.onboard.service.web.SessionService;
import com.onboard.test.moduleutils.ModuleHelper;

@RunWith(MockitoJUnitRunner.class)
public class BugActivityGeneratorTest {

    @InjectMocks
    BugActivityGenerator testedBugActivityGenerator;

    @Mock
    SessionService mockSessionService;
    
    @Before
    public void setupBugActivityGeneratorTest() {
        Mockito.doReturn(ModuleHelper.getASampleUser()).when(mockSessionService).getCurrentUser();
    }
    
    public void runAsserts(Activity activity, Bug bug, String actionType, String subject) {
        /* BugActivityGenerator.generateActivityByActionType */
        assertEquals((int) bug.getProjectId(), (int) activity.getProjectId());
        assertEquals((int) bug.getCompanyId(), (int) activity.getCompanyId());
        assertEquals(bug.getTitle(), activity.getTarget());

        /* ActivityHelper.generateActivityByActionType */
        assertEquals((int) bug.getId(), (int) activity.getAttachId());
        assertEquals(new Bug().getType(), activity.getAttachType());
        assertEquals(subject, activity.getSubject());
        assertEquals(actionType, activity.getAction());
        
        /* BugActivityGenerator.enrichActibity */
        assertEquals(ModuleHelper.userId, (int) activity.getCreatorId());
        assertEquals(ModuleHelper.userName, activity.getCreatorName());
        assertTrue(ModuleHelper.compareCreatedItemDateWithToday(activity.getCreated()));
    }
    
    /**
     * @author Steven
     */
    @Test
    public void testGenerateCreateActivity() {
        Bug bug = ModuleHelper.getASampleBug();
        
        Activity activity = testedBugActivityGenerator.generateCreateActivity(bug);
        
        runAsserts(activity, bug, ActivityActionType.CREATE, BugActivityGenerator.CREATE_BUG_SUBJECT);
    }
    
    /**
     * @author Steven
     * Test that discard the bug
     */
    @Test
    public void testGenerateUpdateActivity_Test1() {
        Bug origin = ModuleHelper.getASampleBug();
        Bug update = ModuleHelper.getASampleBug();
        origin.setDeleted(false);
        update.setDeleted(true);
        
        Activity activity = testedBugActivityGenerator.generateUpdateActivity(origin, update);
        
        runAsserts(activity, origin, ActivityActionType.DISCARD, BugActivityGenerator.DELETE_BUG_SUBJECT);
    }
    
    /**
     * @author Steven
     * Test that recover the bug
     */
    @Test
    public void testGenerateUpdateActivity_Test2() {
        Bug origin = ModuleHelper.getASampleBug();
        Bug update = ModuleHelper.getASampleBug();
        origin.setDeleted(true);
        update.setDeleted(false);
        
        Activity activity = testedBugActivityGenerator.generateUpdateActivity(origin, update);
        
        runAsserts(activity, origin, ActivityActionType.RECOVER, BugActivityGenerator.RECOVER_BUG_SUBJECT);
    }
    
    /**
     * @author Steven
     * Test that complete the bug
     */
    @Test
    public void testGenerateUpdateActivity_Test3() {
        Bug origin = ModuleHelper.getASampleBug();
        Bug update = ModuleHelper.getASampleBug();
        origin.setCompleted(false);
        update.setCompleted(true);
        
        Activity activity = testedBugActivityGenerator.generateUpdateActivity(origin, update);
        
        runAsserts(activity, origin, ActivityActionType.COMPLETE, BugActivityGenerator.COMPLETE_BUG_SUBJECT);
    }
    
    /**
     * @author Steven
     * Test that update the bug
     */
    @Test
    public void testGenerateUpdateActivity_Test4() {
        Bug origin = ModuleHelper.getASampleBug();
        Bug update = ModuleHelper.getASampleBug();
        
        Activity activity = testedBugActivityGenerator.generateUpdateActivity(origin, update);
        
        runAsserts(activity, origin, ActivityActionType.UPDATE, BugActivityGenerator.UPDATE_BUG_SUBJECT);
    }
}
