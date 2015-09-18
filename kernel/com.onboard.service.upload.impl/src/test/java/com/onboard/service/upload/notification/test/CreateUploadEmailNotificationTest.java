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
package com.onboard.service.upload.notification.test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.onboard.domain.model.Activity;
import com.onboard.domain.model.Project;
import com.onboard.domain.model.Upload;
import com.onboard.domain.model.User;
import com.onboard.domain.model.type.Subscribable;
import com.onboard.service.account.UserService;
import com.onboard.service.collaboration.ProjectService;
import com.onboard.service.email.TemplateEngineService;
import com.onboard.service.upload.notification.CreateUploadEmailNotification;
import com.onboard.test.moduleutils.ModuleHelper;

@RunWith(MockitoJUnitRunner.class)
public class CreateUploadEmailNotificationTest {
    @InjectMocks
    private CreateUploadEmailNotification createUploadEmailNotification;

    private static int id = 1;
    private static int companyId = 3;
    private static int projectId = 4;
    private static String creatorName = "Martin";
    private static String projectName = "onboard";
    private static int creatorId = 1;
    private static final String VM_NAME = "upload-created.vm";
    private static final String VM_PATH = "templates/";
    private static String emailSubject = "emailSubject";

    @Mock
    private ProjectService projectService;

    @Mock
    private UserService userService;

    @Mock
    private TemplateEngineService templateEngineService;

    private User user;

    @Before
    public void setUpBefore() throws Exception {
        user = this.getAUser();
        CreateUploadEmailNotification createUploadEmailNotificationSpy = Mockito.spy(createUploadEmailNotification);
        Mockito.doReturn(ModuleHelper.type).when(createUploadEmailNotificationSpy).modelType();
        when(userService.getById(id)).thenReturn(user);
    }

    @After
    public void tearDownAfter() throws Exception {
    }

    private Activity getASampleActivity() {
        Activity activity = new Activity();
        activity.setId(id);
        activity.setCompanyId(companyId);
        activity.setCreatorName(creatorName);
        activity.setProjectName(projectName);
        activity.setCreatorId(creatorId);

        return activity;
    }

    private Upload getASampleUpload(Boolean deleted) {
        Upload upload = new Upload();
        upload.setId(id);
        upload.setProjectId(projectId);
        upload.setCompanyId(companyId);
        upload.setDeleted(deleted);
        upload.setAttachments(null);

        return upload;
    }

    private User getAUser() {
        User user = new User();
        user.setName(creatorName);

        return user;
    }

    private Project getASampleProject() {
        Project project = new Project();
        project.setName(projectName);

        return project;
    }

    @Test
    public void testGetModel() {
        Activity activity = getASampleActivity();
        Upload upload = getASampleUpload(false);
        CreateUploadEmailNotification createUploadEmailNotificationSpy = Mockito.spy(createUploadEmailNotification);
        // Mockito.doReturn(attachementListEmail).when(templateEngineService)
        // .process(getClass(), attachementListEmail, new HashMap<String, Object>());
        // Mockito.doReturn(this.getAUser()).doReturn(this.getAUser()).when(userService).getUserById(id);

        Mockito.doReturn(this.getASampleProject()).when(projectService).getById(upload.getProjectId());

        Map<String, Object> model = createUploadEmailNotificationSpy.getModel(activity, upload, new HashMap<String, Object>());
        User user = this.getAUser();

        assertEquals(model.get("userName"), user.getName());
        assertEquals(model.get("projectName"), projectName);
        assertEquals(model.get("attachmentList"), "");
    }

    @Test
    public void testGetTemplatePath() {
        CreateUploadEmailNotification createUploadEmailNotificationSpy = Mockito.spy(createUploadEmailNotification);
        String templetePath = createUploadEmailNotificationSpy.getTemplatePath();

        assertEquals(templetePath, VM_PATH + VM_NAME);
    }

    class CreateUploadEmailNotificationSon extends CreateUploadEmailNotification {
        public String testGetEmailSubject(Activity activity, Subscribable item) {
            return super.getEmailSubject(activity, item);
        }
    }

    @Test
    public void testGetEmailSubject() {
        Activity activity = this.getASampleActivity();
        CreateUploadEmailNotificationSon createUploadEmailNotificationSon = Mockito.mock(CreateUploadEmailNotificationSon.class);
        when(createUploadEmailNotificationSon.testGetEmailSubject(activity, null)).thenReturn(emailSubject);

        String testEmail = createUploadEmailNotificationSon.testGetEmailSubject(activity, null);

        assertEquals(testEmail, emailSubject);
    }
}
