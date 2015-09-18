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
package com.onboard.service.upload.activity.test;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.onboard.domain.model.Activity;
import com.onboard.domain.model.Upload;
import com.onboard.domain.model.type.BaseProjectItem;
import com.onboard.service.upload.UploadService;
import com.onboard.service.upload.activity.UploadActivityGenerator;
import com.onboard.test.moduleutils.ModuleHelper;

@RunWith(MockitoJUnitRunner.class)
public class UploadActivityGeneratorTest {

    @Mock
    private UploadService uploadService;

    @InjectMocks
    private UploadActivityGenerator uploadActivityGenerator;

    private static final String CREATE = "create";
    private static final String CREATE_SUBJECT = "上传了文件";
    private static final String DISCARD = "discard";
    private static final String DISCARD_SUBJECT = "删除了文件";
    private static final String RECOVER = "recover";
    private static final String RECOVER_SUBJECT = "从回收站还原了文件";
    private static final String MOVE = "move";
    private static final String MOVE_SUBJECT = "移动了文件";

    private static int id = 1;
    private static int companyId = 3;
    private static String content = "this is upload test";

    @Before
    public void setUpBefore() throws Exception {
        UploadActivityGenerator uploadActivityGeneratorSpy = Mockito.spy(uploadActivityGenerator);
        Mockito.doReturn(ModuleHelper.type).when(uploadActivityGeneratorSpy).modelType();
        Mockito.doReturn(UploadService.class.getName()).when(uploadActivityGeneratorSpy).modelService();
    }

    @After
    public void tearDownAfter() throws Exception {
    }

    private void runCommonEquals(Upload upload, Activity activity) {
        assertEquals(upload.getProjectId(), activity.getProjectId());
        assertEquals(upload.getCompanyId(), activity.getCompanyId());
        assertEquals(upload.getContent(), activity.getTarget());
        assertEquals(upload.getType(), activity.getAttachType());
        assertEquals(upload.getId(), activity.getAttachId());
    }

    private Upload getASampleUpload(Boolean deleted, int projectId) {
        Upload upload = new Upload();
        upload.setId(id);
        upload.setProjectId(projectId);
        upload.setCompanyId(companyId);
        upload.setDeleted(deleted);
        upload.setContent(content);

        return upload;
    }

    @Test
    public void testGenerateCreateActivity() {
        Upload upload = this.getASampleUpload(false, 4);
        UploadActivityGenerator uploadActivityGeneratorSpa = Mockito.spy(uploadActivityGenerator);
        Activity activity = uploadActivityGeneratorSpa.generateCreateActivity(upload);

        assertEquals(CREATE, activity.getAction());
        assertEquals(CREATE_SUBJECT, activity.getSubject());
        runCommonEquals(upload, activity);
    }

    @Test
    public void testGenerateDiscardActivity() {
        Upload originUpload = this.getASampleUpload(false, 4);
        Upload modifiedUpload = this.getASampleUpload(true, 4);
        UploadActivityGenerator uploadActivityGeneratorSpa = Mockito.spy(uploadActivityGenerator);
        Activity activity = uploadActivityGeneratorSpa.generateUpdateActivity(originUpload, modifiedUpload);

        assertEquals(DISCARD, activity.getAction());
        assertEquals(DISCARD_SUBJECT, activity.getSubject());
        runCommonEquals(originUpload, activity);
    }

    @Test
    public void testGenerateRecoverActivity() {
        Upload originUpload = this.getASampleUpload(true, 4);
        Upload modifiedUpload = this.getASampleUpload(false, 4);

        UploadActivityGenerator uploadActivityGeneratorSpa = Mockito.spy(uploadActivityGenerator);
        Activity activity = uploadActivityGeneratorSpa.generateUpdateActivity(originUpload, modifiedUpload);

        assertEquals(RECOVER, activity.getAction());
        assertEquals(RECOVER_SUBJECT, activity.getSubject());
        runCommonEquals(originUpload, activity);
    }

    @Test
    public void testGenerateMoveActivityDeleteIsNull() {
        Upload originUpload = this.getASampleUpload(true, 4);
        Upload modifiedUpload = this.getASampleUpload(null, 5);
        UploadActivityGenerator uploadActivityGeneratorSpa = Mockito.spy(uploadActivityGenerator);
        Activity activity = uploadActivityGeneratorSpa.generateUpdateActivity(originUpload, modifiedUpload);

        assertEquals(MOVE, activity.getAction());
        assertEquals(MOVE_SUBJECT, activity.getSubject());
        runCommonEquals(modifiedUpload, activity);
    }

    @Test
    public void testGenerateMoveActivityDeletedEquals() {
        Upload originUpload = this.getASampleUpload(true, 4);
        Upload modifiedUpload = this.getASampleUpload(true, 5);
        UploadActivityGenerator uploadActivityGeneratorSpa = Mockito.spy(uploadActivityGenerator);
        Activity activity = uploadActivityGeneratorSpa.generateUpdateActivity(originUpload, modifiedUpload);

        assertEquals(MOVE, activity.getAction());
        assertEquals(MOVE_SUBJECT, activity.getSubject());
        runCommonEquals(modifiedUpload, activity);
    }

    @Test
    public void testGenerateMoveActivityDeleteIsNullAndNotEquals() {
        Upload originUpload = this.getASampleUpload(null, 4);
        Upload modifiedUpload = this.getASampleUpload(null, 5);
        UploadActivityGenerator uploadActivityGeneratorSpa = Mockito.spy(uploadActivityGenerator);
        Activity activity = uploadActivityGeneratorSpa.generateUpdateActivity(originUpload, modifiedUpload);

        assertEquals(MOVE, activity.getAction());
        assertEquals(MOVE_SUBJECT, activity.getSubject());
        runCommonEquals(modifiedUpload, activity);
    }

    @Test
    public void testGenerateMoveActivityNull() {

    }

    @Test
    public void testEnrichModel() {
        Upload identifiable = getASampleUpload(false, 4);
        UploadActivityGenerator uploadActivityGeneratorSpy = Mockito.spy(uploadActivityGenerator);

        Mockito.doReturn(identifiable).when(uploadService).getById(id);
        BaseProjectItem upload = uploadActivityGeneratorSpy.enrichModel(identifiable);

        Mockito.verify(uploadService, Mockito.times(1)).getById(id);
        Mockito.verifyNoMoreInteractions(uploadService);

        assertEquals(upload.getId(), Integer.valueOf(id));
    }
}
