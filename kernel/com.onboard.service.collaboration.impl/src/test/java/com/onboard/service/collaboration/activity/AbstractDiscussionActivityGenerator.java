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

import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.onboard.domain.model.Discussion;
import com.onboard.domain.model.Project;
import com.onboard.domain.model.type.BaseProjectItem;
import com.onboard.service.account.UserService;
import com.onboard.service.collaboration.DiscussionService;
import com.onboard.service.collaboration.ProjectService;
import com.onboard.service.web.SessionService;
import com.onboard.test.moduleutils.ModuleHelper;

@RunWith(MockitoJUnitRunner.class)
public abstract class AbstractDiscussionActivityGenerator {

    @Mock
    protected DiscussionService mockedDiscussionService;

    @Mock
    protected ProjectService projectService;
    @Mock
    protected UserService userService;
    @Mock
    protected SessionService sessionService;

    protected BaseProjectItem baseProjectItem;
    protected Discussion discussion, discussionDeleted, discussionOtherProject;

    protected Project project;

    @Before
    public void setupTest() {
        discussion = getASampleDiscussion();
        discussionDeleted = getASampleDiscussion();
        discussionDeleted.setDeleted(true);
        discussionOtherProject = getASampleDiscussion();
        discussionOtherProject.setProjectId(ModuleHelper.projectId + 1);
        project = ModuleHelper.getASampleProject();
        projectService = Mockito.mock(ProjectService.class);
        when(projectService.getById(anyInt())).thenReturn(project);
        when(sessionService.getCurrentUser()).thenReturn(ModuleHelper.getASampleUser());

        ActivityRecorderHelper activityRecorderHelper = new ActivityRecorderHelper();
        activityRecorderHelper.setProjectService(projectService);
        activityRecorderHelper.setSession(sessionService);
        activityRecorderHelper.setUserService(userService);
        // discussion = (Discussion) baseProjectItem;
        initDiscussionService();
    }

    /** initDiscussionService **/
    private void initDiscussionService() {
        when(mockedDiscussionService.getById(anyInt())).thenReturn(discussion);
    }

    /** **/
    private Discussion getASampleDiscussion() {
        Discussion d = new Discussion();
        d.setId(ModuleHelper.id);
        d.setProjectId(ModuleHelper.projectId);
        d.setCompanyId(ModuleHelper.companyId);
        d.setCreatorId(ModuleHelper.creatorId);
        d.setCreatorName(ModuleHelper.creatorName);
        d.setContent(ModuleHelper.content);
        d.setDeleted(false);
        return d;
    }

}
