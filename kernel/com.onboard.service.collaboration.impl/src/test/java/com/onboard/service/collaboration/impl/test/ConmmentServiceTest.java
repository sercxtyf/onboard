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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import com.onboard.domain.mapper.model.CommentExample;
import com.onboard.domain.mapper.model.common.BaseExample;
import com.onboard.domain.model.Attachment;
import com.onboard.domain.model.Comment;
import com.onboard.domain.model.Topic;
import com.onboard.domain.model.User;
import com.onboard.domain.model.type.BaseProjectItem;
import com.onboard.domain.model.type.Commentable;
import com.onboard.domain.model.type.Subscribable;
import com.onboard.service.account.UserService;
import com.onboard.service.collaboration.AttachmentService;
import com.onboard.service.collaboration.CommentService;
import com.onboard.service.collaboration.DiscussionService;
import com.onboard.service.collaboration.TopicService;
import com.onboard.service.collaboration.impl.CommentServiceImpl;
import com.onboard.service.collaboration.impl.abstractfiles.AbstractCommentServiceImplTest;
import com.onboard.service.common.identifiable.IdentifiableManager;
import com.onboard.service.common.subscrible.SubscriberService;
import com.onboard.service.web.SessionService;
import com.onboard.test.exampleutils.CriterionVerifier;
import com.onboard.test.exampleutils.ExampleMatcher;
import com.onboard.test.exampleutils.ObjectMatcher;
import com.onboard.test.moduleutils.ModuleHelper;

public class ConmmentServiceTest extends AbstractCommentServiceImplTest {

    @Mock
    private AttachmentService mockAttachmentService;
    @Mock
    private UserService mockUserService;
    @Mock
    private TopicService mockTopicService;
    @Mock
    private SessionService mockSessionService;
    @Mock
    private DiscussionService mockDiscussionService;
    @Mock
    private IdentifiableManager mockIdentifiableManager;
    @Mock
    private SubscriberService mockSubscriberService;
    @Mock
    private Commentable mockCommentable;

    @InjectMocks
    private CommentServiceImpl mockCommentServiceImpl;

    @Test
    public void testGetByIdWithDetail1() {
        when(mockCommentMapper.selectByPrimaryKey(ModuleHelper.id)).thenReturn(null);
        Comment comment0 = mockCommentServiceImpl.getByIdWithDetail(ModuleHelper.id);
        verify(mockCommentMapper).selectByPrimaryKey(ModuleHelper.id);
        assertNull(comment0);
    }

    @Test
    public void testGetByIdWithDetail2() {

        when(mockAttachmentService.getAttachmentsByTypeAndId(comment.getType(), ModuleHelper.id, 0, -1)).thenReturn(
                attachmentList);
        when(mockUserService.getById(ModuleHelper.creatorId)).thenReturn(user);

        Comment retComment = mockCommentServiceImpl.getByIdWithDetail(ModuleHelper.id);

        verify(mockCommentMapper).selectByPrimaryKey(ModuleHelper.id);
        verify(mockAttachmentService).getAttachmentsByTypeAndId("comment", ModuleHelper.id, 0, -1);
        verify(mockUserService).getById(ModuleHelper.creatorId);

        assertEquals(retComment.getAttachments(), attachmentList);
        assertEquals(user, retComment.getCreator());

    }

    @Test
    public void testGetCommentsByTopic() {

        when(mockAttachmentService.getAttachmentsByTypeAndId(new Comment().getType(), ModuleHelper.id, 0, -1)).thenReturn(
                attachmentList);

        List<Comment> commentlist = mockCommentServiceImpl.getCommentsByTopic(ModuleHelper.attachType, ModuleHelper.attachId,
                ModuleHelper.start, ModuleHelper.limit);

        verify(mockCommentMapper).selectByExample(argThat(new ExampleMatcher<CommentExample>() {
            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "attachId", ModuleHelper.attachId)
                        && CriterionVerifier.verifyEqualTo(example, "attachType", ModuleHelper.attachType)
                        && CriterionVerifier.verifyEqualTo(example, "deleted", false)
                        && CriterionVerifier.verifyStart(example, ModuleHelper.start)
                        && CriterionVerifier.verifyLimit(example, ModuleHelper.limit);
            }
        }));
        verify(mockAttachmentService, times(2)).getAttachmentsByTypeAndId("comment", ModuleHelper.id, 0, -1);
        for (int i = 0; i < commentlist.size(); i++) {
            assertEquals(commentlist.get(i).getAttachments(), attachmentList);
        }
    }

    @Test
    public void testFillCommenttable1() {
        mockCommentServiceImpl.fillCommentable(null, ModuleHelper.start, ModuleHelper.limit);
        verify(mockCommentable, never()).getType();
        verify(mockCommentable, never()).getId();
        verify(mockCommentable, never()).setComments(listOfComments);
    }

    @Test
    public void testFillCommenttable2() {
        CommentServiceImpl spyCommentServiceImpl = spy(mockCommentServiceImpl);
        doReturn(listOfComments).when(spyCommentServiceImpl).getCommentsByTopic(ModuleHelper.attachType, ModuleHelper.attachId,
                ModuleHelper.start, ModuleHelper.limit);
        when(mockCommentable.getType()).thenReturn(ModuleHelper.attachType);
        when(mockCommentable.getId()).thenReturn(ModuleHelper.attachId);
        spyCommentServiceImpl.fillCommentable(mockCommentable, ModuleHelper.start, ModuleHelper.limit);

        verify(spyCommentServiceImpl).getCommentsByTopic(ModuleHelper.attachType, ModuleHelper.attachId, ModuleHelper.start,
                ModuleHelper.limit);
        verify(mockCommentable).getId();
        verify(mockCommentable).getType();
        verify(mockCommentable).setComments(listOfComments);
    }

    @Test
    public void testCreate() {
        BaseProjectItem mockIdentifiable = mock(Subscribable.class);
        when(mockUserService.getById(comment.getCreatorId())).thenReturn(new User(ModuleHelper.creatorId));
        when(mockTopicService.buildTopicFromComment(comment, comment.getProjectId())).thenReturn(topic);
        when(mockTopicService.createOrUpdateTopic(topic)).thenReturn(topic);
        when(mockIdentifiableManager.getIdentifiableByTypeAndId(comment.getAttachType(), comment.getAttachId())).thenReturn(
                mockIdentifiable);
        doNothing().when(mockSubscriberService).generateSubscribers(any(Subscribable.class), any(User.class));
        doNothing().when(mockSubscriberService).addSubscribers(any(Subscribable.class));
        List<Attachment> resultAttachmentList = new ArrayList<Attachment>();
        Attachment resultAttachment = getAAttachment();
        resultAttachmentList.add(resultAttachment);
        when(mockAttachmentService.addAttachmentsForAttachable(comment, attachmentList)).thenReturn(resultAttachmentList);

        comment.setAttachments(attachmentList);

        Comment comment1 = mockCommentServiceImpl.create(comment);

        verify(mockCommentMapper).insertSelective(comment);
        verify(mockTopicService).buildTopicFromComment(comment, ModuleHelper.projectId);
        verify(mockTopicService).createOrUpdateTopic(topic);
        verify(mockIdentifiableManager).getIdentifiableByTypeAndId(comment.getAttachType(), comment.getAttachId());
        verify(mockSubscriberService).generateSubscribers(any(Subscribable.class), any(User.class));
        verify(mockSubscriberService).addSubscribers(any(Subscribable.class));
        verify(mockAttachmentService).addAttachmentsForAttachable(comment, attachmentList);

        assertEquals(resultAttachmentList, comment1.getAttachments());
        assertEquals(resultAttachment, comment1.getAttachments().get(0));
        assertEquals(false, comment1.getDeleted());
        assertNotNull(comment1.getCreated());
        assertTrue(ModuleHelper.compareCreatedItemDateWithToday(comment1.getCreated()));
        assertEquals(comment1.getUpdated(), comment.getCreated());
        assertEquals(ModuleHelper.creatorName, comment1.getCreatorName());

    }

    @Test
    public void testCopyComments() {

        CommentService spyCommentService = Mockito.spy(mockCommentServiceImpl);
        doReturn(listOfComments).when(spyCommentService).getCommentsByTopic(ModuleHelper.attachType, ModuleHelper.attachId,
                ModuleHelper.start, ModuleHelper.limit);
        when(mockCommentMapper.insertSelective(any(Comment.class))).thenReturn(1);
        doNothing().when(mockAttachmentService).copyAttachments(any(Comment.class), any(Comment.class));

        spyCommentService.copyComments(discussion, discussion);

        verify(spyCommentService).getCommentsByTopic(discussion.getType(), ModuleHelper.id, 0, -1);
        verify(mockCommentMapper, times(2)).insertSelective(argThat(new ObjectMatcher<Comment>() {
            @Override
            public boolean verifymatches(Comment item) {
                return (item.getAttachId() == ModuleHelper.id) && (item.getProjectId() == ModuleHelper.projectId)
                        && (item.getCreated() == ModuleHelper.created) && (item.getUpdated() == ModuleHelper.updated)
                        && (item.getContent() == ModuleHelper.content) && (item.getCompanyId() == ModuleHelper.companyId)
                        && (item.getCreatorId() == ModuleHelper.creatorId) && (item.getCreatorName() == ModuleHelper.creatorName);
            }
        }));
        verify(mockAttachmentService, times(2)).copyAttachments(argThat(new ObjectMatcher<Comment>() {
            @Override
            public boolean verifymatches(Comment item) {
                return (item.getAttachId() == ModuleHelper.attachId) && (item.getProjectId() == ModuleHelper.projectId)
                        && (item.getCreated() == comment.getCreated()) && (item.getUpdated() == comment.getUpdated())
                        && (item.getContent() == comment.getContent()) && (item.getCompanyId() == comment.getCompanyId())
                        && (item.getCreatorId() == comment.getCreatorId()) && (item.getCreatorName() == comment.getCreatorName());
            }
        }), argThat(new ObjectMatcher<Comment>() {
            @Override
            public boolean verifymatches(Comment item) {
                return (item.getAttachId() == ModuleHelper.id) && (item.getProjectId() == ModuleHelper.projectId)
                        && (item.getCreated() == ModuleHelper.created) && (item.getUpdated() == ModuleHelper.updated)
                        && (item.getContent() == ModuleHelper.content) && (item.getCompanyId() == ModuleHelper.companyId)
                        && (item.getCreatorId() == ModuleHelper.creatorId) && (item.getCreatorName() == ModuleHelper.creatorName);
            }
        }));
    }

    @Test
    public void testRelocateComment() {
        doNothing().when(mockAttachmentService).relocateAttachment(any(Comment.class), Mockito.eq(ModuleHelper.projectId));
        when(mockCommentMapper.updateByPrimaryKeySelective(any(Comment.class))).thenReturn(1);
        CommentService spyCommentService = Mockito.spy(mockCommentServiceImpl);
        doReturn(listOfComments).when(spyCommentService).getCommentsByTopic(ModuleHelper.attachType, ModuleHelper.attachId,
                ModuleHelper.start, ModuleHelper.limit);

        spyCommentService.relocateComment(discussion, ModuleHelper.projectId);

        verify(spyCommentService).getCommentsByTopic(discussion.getType(), discussion.getId(), 0, -1);
        verify(mockCommentMapper, times(2)).updateByPrimaryKeySelective(argThat(new ObjectMatcher<Comment>() {

            @Override
            public boolean verifymatches(Comment item) {
                return (item.getId() == ModuleHelper.id) && (item.getProjectId() == ModuleHelper.projectId);
            }
        }));

        verify(mockAttachmentService, times(2)).relocateAttachment(argThat(new ObjectMatcher<Comment>() {

            @Override
            public boolean verifymatches(Comment item) {
                return (item.getId() == ModuleHelper.id) && (item.getProjectId() == ModuleHelper.projectId)
                        && (item.getCreated() == ModuleHelper.created) && (item.getUpdated() == ModuleHelper.updated)
                        && (item.getContent() == ModuleHelper.content) && (item.getCompanyId() == ModuleHelper.companyId)
                        && (item.getCreatorId() == ModuleHelper.creatorId) && (item.getCreatorName() == ModuleHelper.creatorName);
            }

        }), Mockito.eq(ModuleHelper.projectId));
    }

    @Test
    public void testDeleteFromTrash() {
        when(mockCommentMapper.selectByPrimaryKey(ModuleHelper.id)).thenReturn(comment);
        when(mockCommentMapper.deleteByPrimaryKey(ModuleHelper.id)).thenReturn(1);
        doNothing().when(mockAttachmentService).deleteAttachmentByAttachTypeAndId(ModuleHelper.attachType, ModuleHelper.attachId);
        CommentService spyCommentService = spy(mockCommentServiceImpl);
        doReturn(comment).when(spyCommentService).newItem();
        // doReturn(new Comment().getType()).when(spyCommentService).getModelType();

        spyCommentService.deleteFromTrash(ModuleHelper.id);

        verify(mockCommentMapper).selectByPrimaryKey(ModuleHelper.id);
        verify(mockCommentMapper).deleteByPrimaryKey(ModuleHelper.id);
        verify(mockAttachmentService).deleteAttachmentByAttachTypeAndId(new Comment().getType(), ModuleHelper.id);
        // verify(spyCommentService).getModelType();
        verify(spyCommentService).newItem();
    }

    @Test
    public void testDeleteFromTrashNull() {
        when(mockCommentMapper.selectByPrimaryKey(ModuleHelper.id)).thenReturn(null);
        mockCommentServiceImpl.deleteFromTrash(ModuleHelper.id);

        verify(mockCommentMapper).selectByPrimaryKey(ModuleHelper.id);
    }

    @Test
    public void testDelete() {
        mockCommentServiceImpl.deleteFromTrash(ModuleHelper.id);
        verify(mockCommentMapper).deleteByPrimaryKey(ModuleHelper.id);
        verify(mockAttachmentService).deleteAttachmentByAttachTypeAndId(new Comment().getType(), ModuleHelper.id);
    }

    @Test
    public void testUpdateSelective() {
        final String newContent = "new content";
        comment.setContent(newContent);

        mockCommentServiceImpl.updateSelective(comment);

        verify(mockTopicService).createOrUpdateTopic(any(Topic.class));
        verify(mockCommentMapper).updateByPrimaryKeySelective(argThat(new ObjectMatcher<Comment>() {
            @Override
            public boolean verifymatches(Comment item) {
                return item.getId().equals(ModuleHelper.id) && item.getContent().equals(newContent);
            }
        }));
    }

    @Test
    public void testGetCommentTarget() {
        mockCommentServiceImpl.getCommentTarget(comment.getAttachType(), comment.getAttachId());

        verify(mockIdentifiableManager).getIdentifiableByTypeAndId(ModuleHelper.attachType, ModuleHelper.attachId);
    }

    @Test
    public void testGetCommentTargetNameNull() {
        when(mockIdentifiableManager.getIdentifiableByTypeAndId(ModuleHelper.target, ModuleHelper.id)).thenReturn(null);

        String targetName = mockCommentServiceImpl.getCommentTargetName(ModuleHelper.target, ModuleHelper.id);

        verify(mockIdentifiableManager).getIdentifiableByTypeAndId(ModuleHelper.target, ModuleHelper.id);
        assertNull(targetName);
    }

    @Test
    public void testGetCommentTargetName() {
        when(mockIdentifiableManager.getIdentifiableByTypeAndId(ModuleHelper.target, ModuleHelper.id)).thenReturn(discussion);
        String targetName = mockCommentServiceImpl.getCommentTargetName(ModuleHelper.target, ModuleHelper.id);

        verify(mockIdentifiableManager).getIdentifiableByTypeAndId(ModuleHelper.target, ModuleHelper.id);
        assertEquals(targetName, ModuleHelper.subject);
    }

    @Test
    public void testGetCountOfCommentsByTopic() {
        int count = mockCommentServiceImpl.getCountOfCommentsByTopic(ModuleHelper.attachType, ModuleHelper.attachId);
        verify(mockCommentMapper).countByExample(argThat(new ExampleMatcher<CommentExample>() {

            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "attachId", ModuleHelper.attachId)
                        && CriterionVerifier.verifyEqualTo(example, "attachType", ModuleHelper.attachType);
            }
        }));

        assertEquals(count, ModuleHelper.count);
    }

    @Test
    public void testDeleteCommentByAttachTypeAndId() {
        mockCommentServiceImpl.deleteCommentByAttachTypeAndId(ModuleHelper.attachType, ModuleHelper.attachId);
        verify(mockCommentMapper).deleteByExample(argThat(new ExampleMatcher<CommentExample>() {

            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "attachId", ModuleHelper.attachId)
                        && CriterionVerifier.verifyEqualTo(example, "attachType", ModuleHelper.attachType);
            }
        }));
    }

    @Test
    public void testGetCommentsByCompanyIdBetweenDates() {

        List<Comment> comments = mockCommentServiceImpl.getCommentsByCompanyIdBetweenDates(ModuleHelper.companyId,
                ModuleHelper.since, ModuleHelper.until);

        verify(mockCommentMapper).selectByExample(argThat(new ExampleMatcher<CommentExample>() {
            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "companyId", ModuleHelper.companyId)
                        && CriterionVerifier.verifyGraterThanOrEqualTo(example, "created", new DateTime(ModuleHelper.since)
                                .withTimeAtStartOfDay().toDate())
                        && CriterionVerifier.verifyLessThan(example, "created", new DateTime(ModuleHelper.until)
                                .withTimeAtStartOfDay().plusDays(1).toDate());
            }
        }));

        assertNotNull(comments);
        assertEquals((int) comments.size(), 2);
    }
}
