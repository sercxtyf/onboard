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
package com.onboard.service.upload.impl.test;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.onboard.domain.mapper.UploadMapper;
import com.onboard.domain.mapper.model.UploadExample;
import com.onboard.domain.mapper.model.common.BaseExample;
import com.onboard.domain.model.Attachment;
import com.onboard.domain.model.Comment;
import com.onboard.domain.model.Upload;
import com.onboard.domain.model.User;
import com.onboard.domain.model.type.Subscribable;
import com.onboard.service.account.UserService;
import com.onboard.service.collaboration.AttachmentService;
import com.onboard.service.collaboration.CommentService;
import com.onboard.service.collaboration.TopicService;
import com.onboard.service.common.identifiable.IdentifiableManager;
import com.onboard.service.common.subscrible.SubscriberService;
import com.onboard.service.upload.impl.UploadServiceImpl;
import com.onboard.test.exampleutils.CriterionVerifier;
import com.onboard.test.exampleutils.ExampleMatcher;
import com.onboard.test.exampleutils.ObjectMatcher;
import com.onboard.test.moduleutils.ModuleHelper;

@RunWith(MockitoJUnitRunner.class)
public class UploadServiceImplTest {

    private static String uploadType = "upload";
    private static int updateUploadValue = 1;

    Upload upload;
    List<Upload> uploads;
    Attachment attachment;
    List<Comment> comments;

    @Mock
    private UploadMapper uploadMapper;

    @Mock
    private AttachmentService attachmentService;
    @Mock
    private CommentService commentService;
    @Mock
    private SubscriberService subscriberService;
    @Mock
    private IdentifiableManager identifiableManager;
    @Mock
    private UserService userService;
    @Mock
    private TopicService topicService;

    @InjectMocks
    private UploadServiceImpl uploadServiceImpl;

    @Before
    public void setUpBefore() throws Exception {
        initUploadMapperOPerations();
    }

    private Upload getASampleUpload(Boolean deleted) {
        Upload upload = new Upload();
        upload.setId(ModuleHelper.id);
        upload.setProjectId(ModuleHelper.projectId);
        upload.setCompanyId(ModuleHelper.companyId);
        upload.setDeleted(deleted);
        upload.setContent(ModuleHelper.content);
        upload.setCreatorId(ModuleHelper.creatorId);
        upload.setAttachments(getAttachments());
        return upload;
    }

    private List<Upload> getUploadList() {
        List<Upload> uploads = new ArrayList<Upload>();

        uploads.add(upload);
        uploads.add(getASampleUpload(true));

        return uploads;

    }

    private Attachment getASampleAttachment() {
        Attachment attachment = new Attachment();
        attachment.setId(ModuleHelper.id);
        attachment.setProjectId(ModuleHelper.projectId);
        attachment.setCompanyId(ModuleHelper.companyId);
        attachment.setAttachId(ModuleHelper.attachId);
        attachment.setAttachType(ModuleHelper.attachType);

        return attachment;
    }

    private List<Attachment> getAttachments() {
        List<Attachment> attachments = new ArrayList<Attachment>();
        attachments.add(getASampleAttachment());

        return attachments;
    }

    private Comment getASampleComment(Boolean deleted) {
        Comment comment = new Comment();
        comment.setId(ModuleHelper.id);
        comment.setContent(ModuleHelper.content);
        comment.setCreatorId(ModuleHelper.creatorId);
        comment.setAttachId(ModuleHelper.attachId);
        comment.setAttachType(ModuleHelper.attachType);
        comment.setDeleted(deleted);

        return comment;
    }

    private List<Comment> getACommentList() {
        List<Comment> comments = new ArrayList<Comment>();
        comments.add(getASampleComment(true));
        comments.add(getASampleComment(false));

        return comments;
    }

    private void initUploadMapperOPerations() {
        upload = getASampleUpload(false);
        uploads = getUploadList();
        attachment = getASampleAttachment();
        comments = getACommentList();

        when(uploadMapper.selectByPrimaryKey(ModuleHelper.id)).thenReturn(upload);
        when(attachmentService.addAttachmentForAttachable(any(Upload.class), any(Attachment.class))).thenReturn(attachment);
        when(uploadMapper.selectByExample(any(UploadExample.class))).thenReturn(getUploadList());
        when(uploadMapper.countByExample(any(UploadExample.class))).thenReturn(getUploadList().size());
        when(uploadMapper.updateByPrimaryKeySelective(any(Upload.class))).thenReturn(updateUploadValue);

    }

    private void runCommonEquals(Upload upload, Boolean deleted) {
        assertEquals((int) upload.getId(), ModuleHelper.id);
        assertEquals((int) upload.getProjectId(), ModuleHelper.projectId);
        assertEquals((int) upload.getCompanyId(), ModuleHelper.companyId);
        assertEquals(upload.getContent(), ModuleHelper.content);
        assertEquals(upload.getType(), uploadType);
        assertEquals(upload.getDeleted(), deleted);
    }

    @After
    public void tearDownAfter() throws Exception {
    }

    @Test
    public void testGetUploadById() {
        Upload upload = uploadServiceImpl.getById(ModuleHelper.id);
        verify(uploadMapper, times(1)).selectByPrimaryKey(ModuleHelper.id);
        Mockito.verifyNoMoreInteractions(uploadMapper);
        runCommonEquals(upload, false);

    }

    @Test
    public void testGgetByIdWithDetailBranch1() {
        when(uploadMapper.selectByPrimaryKey(ModuleHelper.id)).thenReturn(null);

        Upload upload = uploadServiceImpl.getByIdWithDetail(ModuleHelper.id);

        verify(attachmentService, times(0)).fillAttachmentsWithDiscard(upload, 0, -1);
        verify(attachmentService, times(0)).fillAttachmentsWithNotDiscard(upload, 0, -1);
        verify(commentService, times(0)).fillCommentable(upload, 0, -1);
        verify(subscriberService, times(0)).fillSubcribers(upload);

        assertEquals(upload, null);
    }

    @Test
    public void testGetByIdWithDetailBranch2() {
        doNothing().when(attachmentService).fillAttachmentsWithDiscard(upload, 0, -1);
        doNothing().when(attachmentService).fillAttachmentsWithNotDiscard(upload, 0, -1);
        doNothing().when(commentService).fillCommentable(upload, 0, -1);
        doNothing().when(subscriberService).fillSubcribers(upload);

        Upload upload = uploadServiceImpl.getByIdWithDetail(ModuleHelper.id);

        verify(attachmentService).fillAttachmentsWithDiscard(upload, 0, -1);
        verify(attachmentService).fillAttachmentsWithNotDiscard(upload, 0, -1);
        verify(commentService).fillCommentable(upload, 0, -1);
        verify(subscriberService).fillSubcribers(upload);

        runCommonEquals(upload, false);
    }

    @Test
    public void testCreateUpload() {
        Upload newUpload = uploadServiceImpl.create(upload);
        Attachment attachment = newUpload.getAttachments().get(0);

        verify(uploadMapper).insert(upload);
        verify(subscriberService).generateSubscribers(any(Subscribable.class), any(User.class));
        verify(userService).getById(ModuleHelper.creatorId);
        verify(subscriberService).addSubscribers(upload);
        verify(attachmentService).addAttachmentForAttachable(upload, getASampleAttachment());

        assertEquals((int) attachment.getId(), ModuleHelper.id);
        assertEquals((int) attachment.getAttachId(), ModuleHelper.attachId);
        assertEquals((int) attachment.getProjectId(), ModuleHelper.projectId);
        assertEquals((int) attachment.getCompanyId(), ModuleHelper.companyId);
        assertEquals(attachment.getAttachType(), ModuleHelper.attachType);

    }

    @Test
    public void testGetUploadsByProject() {
        List<Upload> uploads = uploadServiceImpl.getUploadsByProject(ModuleHelper.projectId, ModuleHelper.start,
                ModuleHelper.limit);

        verify(uploadMapper).selectByExample(Mockito.argThat(new ExampleMatcher<UploadExample>() {

            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "projectId", ModuleHelper.projectId);
            }
        }));
        assertEquals(uploads.size(), 2);
        assertEquals(uploads.get(0), upload);
    }

    @Test
    public void testCountByExample() {
        int count = uploadServiceImpl.countBySample(upload);
        verify(uploadMapper).countByExample(Mockito.argThat(new ExampleMatcher<UploadExample>() {

            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "companyId", ModuleHelper.companyId)
                        && CriterionVerifier.verifyEqualTo(example, "projectId", ModuleHelper.projectId)
                        && CriterionVerifier.verifyEqualTo(example, "content", ModuleHelper.content)
                        && CriterionVerifier.verifyEqualTo(example, "creatorId", ModuleHelper.creatorId)
                        && CriterionVerifier.verifyEqualTo(example, "deleted", false);
            }
        }));

        assertEquals(count, 2);
    }

    @Test
    public void testUpdateBranch1() {
        Upload srcUpload = getASampleUpload(true);
        doNothing().when(topicService).discardTopcicByTypeAndId(ModuleHelper.type, ModuleHelper.id);
        doNothing().when(attachmentService).discardAttachment(ModuleHelper.attachType, ModuleHelper.attachId);

        UploadServiceImpl uploadServiceImplSpy = Mockito.spy(uploadServiceImpl);
        doReturn(upload).when(uploadServiceImplSpy).getById(ModuleHelper.id);

        Upload upload = uploadServiceImplSpy.updateSelective(srcUpload);

        verify(uploadServiceImplSpy).getById(ModuleHelper.id);

        verify(uploadMapper).updateByPrimaryKeySelective(Mockito.argThat(new ObjectMatcher<Upload>() {
            @Override
            public boolean verifymatches(Upload item) {
                return ModuleHelper.compareCreatedItemDateWithToday(item.getUpdated());
            }

        }));
        verify(topicService).discardTopcicByTypeAndId(upload.getType(), upload.getId());
        Mockito.verifyNoMoreInteractions(topicService);
        verify(attachmentService).discardAttachment(upload.getType(), upload.getId());
        Mockito.verifyNoMoreInteractions(attachmentService);

        assertNotNull(upload);
        assertEquals(ModuleHelper.id, (int) upload.getId());
        assertEquals(true, upload.getDeleted());
    }

    @Test
    public void testUpdateBranch2() {
        Upload srcUpload = getASampleUpload(true);
        doNothing().when(topicService).recoverTopcicByTypeAndId(ModuleHelper.type, ModuleHelper.id);
        doNothing().when(attachmentService).recoverAttachment(ModuleHelper.attachType, ModuleHelper.attachId);

        UploadServiceImpl uploadServiceImplSpy = Mockito.spy(uploadServiceImpl);
        doReturn(srcUpload).when(uploadServiceImplSpy).getById(ModuleHelper.id);

        Upload upload = uploadServiceImplSpy.updateSelective(getASampleUpload(false));

        verify(uploadServiceImplSpy).getById(ModuleHelper.id);

        verify(uploadMapper).updateByPrimaryKeySelective(Mockito.argThat(new ObjectMatcher<Upload>() {
            @Override
            public boolean verifymatches(Upload item) {
                return ModuleHelper.compareCreatedItemDateWithToday(item.getUpdated());
            }

        }));
        verify(topicService).recoverTopcicByTypeAndId(upload.getType(), upload.getId());
        Mockito.verifyNoMoreInteractions(topicService);
        verify(attachmentService).recoverAttachment(upload.getType(), upload.getId());
        Mockito.verifyNoMoreInteractions(attachmentService);

        assertNotNull(upload);
        assertEquals(ModuleHelper.id, (int) upload.getId());
        assertEquals(false, upload.getDeleted());
    }

    @Test
    public void testDeleteFormTrash() {
        doNothing().when(attachmentService).deleteAttachmentByAttachTypeAndId(upload.getType(), upload.getId());
        doNothing().when(commentService).deleteCommentByAttachTypeAndId(upload.getType(), upload.getId());
        when(uploadMapper.deleteByPrimaryKey(upload.getId())).thenReturn(updateUploadValue);

        uploadServiceImpl.deleteFromTrash(upload.getId());

        verify(attachmentService).deleteAttachmentByAttachTypeAndId(upload.getType(), upload.getId());
        Mockito.verifyNoMoreInteractions(attachmentService);
        verify(commentService).deleteCommentByAttachTypeAndId(upload.getType(), upload.getId());
        Mockito.verifyNoMoreInteractions(commentService);
        verify(uploadMapper).deleteByPrimaryKey(upload.getId());
        Mockito.verifyNoMoreInteractions(uploadMapper);

    }

    @Test
    public void testDelete() {
        doNothing().when(attachmentService).discardAttachment(upload.getType(), upload.getId());
        doNothing().when(commentService).delete(ModuleHelper.id);

        when(commentService.getCommentsByTopic(upload.getType(), upload.getId(), 0, -1)).thenReturn(comments);
        UploadServiceImpl uploadServiceImplSpy = Mockito.spy(uploadServiceImpl);
        doReturn(upload).when(uploadServiceImplSpy).updateSelective(upload);

        uploadServiceImpl.delete(upload.getId());

        verify(attachmentService, times(2)).discardAttachment(upload.getType(), upload.getId());
        Mockito.verifyNoMoreInteractions(attachmentService);
        verify(commentService).getCommentsByTopic(upload.getType(), upload.getId(), 0, -1);
        verify(commentService, times(2)).delete(ModuleHelper.id);
        Mockito.verifyNoMoreInteractions(commentService);
    }

    @Test
    public void testRecover() {
        UploadServiceImpl uploadServiceImplSpy = Mockito.spy(uploadServiceImpl);
        doReturn(upload).when(uploadServiceImplSpy).updateSelective(getASampleUpload(false));

        uploadServiceImpl.recover(upload.getId());

    }
}
