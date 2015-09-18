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

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.onboard.domain.model.Activity;
import com.onboard.domain.model.Story;
import com.onboard.service.activity.ActivityActionType;
import com.onboard.service.collaboration.activity.StoryActivityGenerator;
import com.onboard.service.web.SessionService;
import com.onboard.test.moduleutils.ModuleHelper;

@RunWith(MockitoJUnitRunner.class)
public class StoryActivityGeneratorTest {
    
    private final static Map<Integer, String> PR_MAP = new HashMap<Integer, String>() {
        private static final long serialVersionUID = 1L;
        {
            put(1, "非常紧急");
            put(2, "紧急");
            put(3, "重要");
            put(4, "普通");
            put(5, "可忽略");
        }
    };

    @InjectMocks
    StoryActivityGenerator testedStoryActivityGenerator;
    
    @Mock
    SessionService mockSessionService;
    
    @Before
    public void setupStoryActivityGeneratorTest() {
        Mockito.doReturn(ModuleHelper.getASampleUser()).when(mockSessionService).getCurrentUser();
    }
    
    public void runAsserts(Activity activity, Story story, String actionType, String subject) {
        assertEquals(story.getProjectId(), activity.getProjectId());
        assertEquals(story.getCompanyId(), activity.getCompanyId());
        assertEquals(story.getDescription(), activity.getTarget());
        assertEquals(story.getId(), activity.getAttachId());
        assertEquals(story.getType(), activity.getAttachType());
        assertEquals(subject, activity.getSubject());
        assertEquals(actionType, activity.getAction());
        assertEquals(ModuleHelper.userId, (int) activity.getCreatorId());
        assertEquals(ModuleHelper.userName, activity.getCreatorName());
        assertTrue(ModuleHelper.compareCreatedItemDateWithToday(activity.getCreated()));
    }
    
    /**
     * @author Steven
     * This test also includes tests for private method "generateActivityByActionType" and "enrichActivity"
     */
    @Test
    public void testGenerateCreateActivity() {
        Story story = ModuleHelper.getASampleStory();
        Activity activity = testedStoryActivityGenerator.generateCreateActivity(story);
        
        assertEquals(story.getProjectId(), activity.getProjectId());
        assertEquals(story.getCompanyId(), activity.getCompanyId());
        assertEquals(story.getDescription(), activity.getTarget());
        assertEquals(story.getDescription(), activity.getContent());
        assertEquals(story.getId(), activity.getAttachId());
        assertEquals(story.getType(), activity.getAttachType());
        assertEquals(StoryActivityGenerator.CREATE_STORY_SUBJECT, activity.getSubject());
        assertEquals(ActivityActionType.CREATE, activity.getAction());
        assertEquals(ModuleHelper.userId, (int) activity.getCreatorId());
        assertEquals(ModuleHelper.userName, activity.getCreatorName());
        assertTrue(ModuleHelper.compareCreatedItemDateWithToday(activity.getCreated()));
    }
    
    /**
     * @author Steven
     * Test for updating priority
     */
    @Test
    public void testGenerateUpdateActivity_Test1() {
        Story origin = ModuleHelper.getASampleStory();
        Story update = ModuleHelper.getASampleStory();
        origin.setPriority(0);
        update.setPriority(1);
        
        Activity activity = testedStoryActivityGenerator.generateUpdateActivity(origin, update);
        
        runAsserts(activity, update, ActivityActionType.UPDATE, StoryActivityGenerator.UPDATE_STORY_SUBJECT);
        assertEquals(String.format(StoryActivityGenerator.UPDATE_STORY_PRIORITY_SUBJECT, PR_MAP.get(origin.getPriority()),
                PR_MAP.get(update.getPriority())), activity.getContent());
    }
    
    /**
     * @author Steven
     * Test for updating description
     */
    @Test
    public void testGenerateUpdateActivity_Test2() {
        Story origin = ModuleHelper.getASampleStory();
        Story update = ModuleHelper.getASampleStory();
        origin.setDescription("Description1");
        update.setDescription("Description2");
        
        Activity activity = testedStoryActivityGenerator.generateUpdateActivity(origin, update);
        
        runAsserts(activity, update, ActivityActionType.UPDATE, StoryActivityGenerator.UPDATE_STORY_SUBJECT);
        assertEquals(String.format(StoryActivityGenerator.UPDATE_STORY_TITLE_SUBJECT, origin.getDescription(),
                update.getDescription()), activity.getContent());
    }
    
    /**
     * @author Steven
     * Test for updating acceptanceLevel
     */
    @Test
    public void testGenerateUpdateActivity_Test3() {
        Story origin = ModuleHelper.getASampleStory();
        Story update = ModuleHelper.getASampleStory();
        origin.setAcceptanceLevel("AcceptanceLevel1");
        update.setAcceptanceLevel("AcceptanceLevel2");
        
        Activity activity = testedStoryActivityGenerator.generateUpdateActivity(origin, update);
        
        runAsserts(activity, update, ActivityActionType.UPDATE, StoryActivityGenerator.UPDATE_STORY_SUBJECT);
        assertEquals(String.format(StoryActivityGenerator.UPDATE_STORY_ACCEPTANCE_LEVEL_SUBJECT, origin.getAcceptanceLevel(),
                update.getAcceptanceLevel()), activity.getContent());
    }
    
    /**
     * @author Steven
     * Test for completing story
     */
    @Test
    public void testGenerateUpdateActivity_Test4() {
        Story origin = ModuleHelper.getASampleStory();
        Story update = ModuleHelper.getASampleStory();
        origin.setCompleted(false);
        update.setCompleted(true);
        
        Activity activity = testedStoryActivityGenerator.generateUpdateActivity(origin, update);
        
        runAsserts(activity, update, ActivityActionType.UPDATE, StoryActivityGenerator.UPDATE_STORY_COMPLETE_SUBJECT);
    }
    
    /**
     * @author Steven
     * Test for reopenning story
     */
    @Test
    public void testGenerateUpdateActivity_Test5() {
        Story origin = ModuleHelper.getASampleStory();
        Story update = ModuleHelper.getASampleStory();
        origin.setCompleted(true);
        update.setCompleted(false);
        
        Activity activity = testedStoryActivityGenerator.generateUpdateActivity(origin, update);
        
        runAsserts(activity, update, ActivityActionType.UPDATE, StoryActivityGenerator.UPDATE_STORY_REOPEN_SUBJECT);
    }
    
    /**
     * @author Steven
     * Test for deleting story
     */
    @Test
    public void testGenerateUpdateActivity_Test6() {
        Story origin = ModuleHelper.getASampleStory();
        Story update = ModuleHelper.getASampleStory();
        origin.setDeleted(false);
        update.setDeleted(true);
        
        Activity activity = testedStoryActivityGenerator.generateUpdateActivity(origin, update);
        
        runAsserts(activity, update, ActivityActionType.UPDATE, StoryActivityGenerator.UPDATE_STORY_SUBJECT);
        assertEquals(StoryActivityGenerator.DELETE_STORY_SUBJECT, activity.getContent());
    }
    
    /**
     * @author Steven
     * Test for doing nothing
     */
    @Test
    public void testGenerateUpdateActivity_Test7() {
        Story origin = ModuleHelper.getASampleStory();
        Story update = ModuleHelper.getASampleStory();
        
        Activity activity = testedStoryActivityGenerator.generateUpdateActivity(origin, update);
        
        runAsserts(activity, update, ActivityActionType.UPDATE, StoryActivityGenerator.UPDATE_STORY_SUBJECT);
        assertEquals(StoryActivityGenerator.UPDATE_STORY_CONTENT_SUBJECT, activity.getContent());
    }
}
