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
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.mockito.InjectMocks;

import com.onboard.domain.model.Activity;
import com.onboard.domain.model.type.BaseProjectItem;
import com.onboard.service.activity.ActivityActionType;
import com.onboard.service.collaboration.DiscussionService;
import com.onboard.test.moduleutils.ModuleHelper;

public class DiscussionActivityGeneratorTest extends AbstractDiscussionActivityGenerator {

    @InjectMocks
    private DiscussionActivityGenerator discussionActivityGenerator;

    @Test
    public void testModelType() {
        String type = discussionActivityGenerator.modelType();
        assertEquals(type, "discussion");
    }

    @Test
    public void testGenerateCreateActivity() {
        Activity ret = discussionActivityGenerator.generateCreateActivity(discussion);
        assertEquals(ret.getAction(), ActivityActionType.CREATE);
        assertEquals(ret.getCompanyId(), new Integer(ModuleHelper.companyId));
        assertEquals(ret.getProjectId(), new Integer(ModuleHelper.projectId));
    }

    @Test
    public void testGenerateUpdateActivity() {
        Activity ret;
        ret = discussionActivityGenerator.generateUpdateActivity(discussion, discussionDeleted);
        assertEquals(ret.getAction(), ActivityActionType.DISCARD);
        ret = discussionActivityGenerator.generateUpdateActivity(discussionDeleted, discussion);
        assertEquals(ret.getAction(), ActivityActionType.RECOVER);
        ret = discussionActivityGenerator.generateUpdateActivity(discussion, discussionOtherProject);
        assertEquals(ret.getAction(), ActivityActionType.MOVE);
        ret = discussionActivityGenerator.generateUpdateActivity(discussion, discussion);
        assertEquals(ret.getAction(), ActivityActionType.UPDATE);
    }

    @Test
    public void testEnrichModel() {
        BaseProjectItem ret = discussionActivityGenerator.enrichModel(discussion);
        verify(mockedDiscussionService, times(1)).getById(anyInt());
        assertEquals(ret.getId(), new Integer(ModuleHelper.id));
        assertEquals(ret.getCompanyId(), new Integer(ModuleHelper.companyId));
        assertEquals(ret.getCreatorId(), new Integer(ModuleHelper.creatorId));
        assertEquals(ret.getProjectId(), new Integer(ModuleHelper.projectId));
        assertEquals(ret.getCreatorName(), ModuleHelper.creatorName);
    }

    @Test
    public void testModelService() {
        String ret = discussionActivityGenerator.modelService();
        assertEquals(ret, DiscussionService.class.getName());
    }
}
