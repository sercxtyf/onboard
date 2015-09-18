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
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.onboard.domain.mapper.DiscussionMapper;
import com.onboard.domain.mapper.model.DiscussionExample;
import com.onboard.domain.mapper.model.common.BaseExample;
import com.onboard.domain.model.Discussion;
import com.onboard.domain.model.Topic;
import com.onboard.domain.model.User;
import com.onboard.domain.model.type.Attachable;
import com.onboard.domain.model.type.Commentable;
import com.onboard.domain.model.type.Subscribable;
import com.onboard.service.account.UserService;
import com.onboard.service.collaboration.AttachmentService;
import com.onboard.service.collaboration.CommentService;
import com.onboard.service.collaboration.TopicService;
import com.onboard.service.collaboration.impl.DiscussionServiceImpl;
import com.onboard.service.common.subscrible.SubscriberService;
import com.onboard.test.exampleutils.AbstractMatcher;
import com.onboard.test.exampleutils.CriterionVerifier;
import com.onboard.test.exampleutils.ExampleMatcher;

@RunWith(MockitoJUnitRunner.class)
public class DiscussionServiceImplTest {

    @Mock
    private DiscussionMapper mockDiscussionMapper;

    @Mock
    private AttachmentService mockAttachmentService;

    @Mock
    private CommentService mockCommentService;

    @Mock
    private SubscriberService mockSubscriberService;

    @Mock
    private UserService userService;

    @Mock
    private TopicService mockTopicService;

    @InjectMocks
    private DiscussionServiceImpl discussionServiceImpl;

    private DiscussionServiceImpl spyDiscussionService;

    public static final int DEFAULT_LIMIT = -1;

    private static int id = 1;
    private static String content = "discussion-content";
    private static String subject = "discussion-subject";
    private static Date created = getDateByString();
    private static Date updated = getDateByString("2014-03-05 00:00");

    private static int companyId = 3;
    private static int projectId = 4;

    private static int userId = 5;
    private static String userName = "test_user";
    private User user;

    private static int topicId = 7;

    private Discussion discussion;
    private List<Discussion> discussions;
    private Topic topic;

    private Topic getTopic() {
        Topic t = new Topic();
        t.setId(topicId);
        t.setRefId(id);
        t.setRefType(new Discussion().getType());
        return t;
    }

    private Discussion getASampleDiscussion() {
        Discussion discussion = new Discussion();
        discussion.setCompanyId(companyId);
        discussion.setContent(content);
        discussion.setCreated(created);
        discussion.setCreatorId(userId);
        discussion.setCreatorName(userName);
        discussion.setDeleted(false);
        discussion.setId(id);
        discussion.setProjectId(projectId);
        discussion.setSubject(subject);
        discussion.setUpdated(updated);
        return discussion;
    }

    private List<Discussion> getASampleDiscussionList() {
        List<Discussion> discussions = new ArrayList<Discussion>();
        discussions.add(discussion);
        discussions.add(discussion);
        return discussions;
    }

    private User getASampleUser() {
        User user = new User();
        user.setId(userId);
        user.setName(userName);
        return user;
    }

    private static Date getDateByString() {
        return getDateByString("2014-03-04 00:00");
    }

    private static Date getDateByString(String strDate) {
        DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        Date date = null;
        try {
            date = fmt.parse(strDate);
        } catch (ParseException e) {
        }
        return date;
    }

    @Before
    public void setUpBefore() throws Exception {
        spyDiscussionService = spy(discussionServiceImpl);
        discussion = getASampleDiscussion();
        discussions = getASampleDiscussionList();
        user = getASampleUser();
        topic = getTopic();

        when(mockDiscussionMapper.selectByExample(any(DiscussionExample.class))).thenReturn(discussions);
        when(mockDiscussionMapper.insert(any(Discussion.class))).thenReturn(id);
        when(mockDiscussionMapper.selectByPrimaryKey(anyInt())).thenReturn(discussion);
        when(userService.getById(anyInt())).thenReturn(user);
        when(mockTopicService.buildTopicFromDiscussion(any(Discussion.class))).thenReturn(topic);
    }

    @After
    public void tearDownAfter() throws Exception {
    }

    @Test
    public void getDiscussionByIdForUpdate() {

        Discussion result = discussionServiceImpl.getDiscussionByIdForUpdate(id);

        verify(mockDiscussionMapper).selectByPrimaryKey(id);
        verify(mockAttachmentService).fillAttachments(discussion, 0, DEFAULT_LIMIT);
        verify(mockSubscriberService).fillSubcribers(discussion);
        assertEquals(discussion, result);
    }

    @Test
    public void getDiscussionByIdForUpdateReturnNull() {
        when(mockDiscussionMapper.selectByPrimaryKey(anyInt())).thenReturn(null);

        Discussion result = discussionServiceImpl.getDiscussionByIdForUpdate(id);

        verify(mockDiscussionMapper).selectByPrimaryKey(id);
        verify(mockAttachmentService, times(0)).fillAttachments(any(Attachable.class), anyInt(), anyInt());
        verify(mockSubscriberService, times(0)).fillSubcribers(any(Subscribable.class));
        assertEquals(null, result);
    }

    @Test
    public void getDiscussionByIdWithExtraInfo() {

        Discussion result = discussionServiceImpl.getByIdWithDetail(id);

        verify(mockDiscussionMapper).selectByPrimaryKey(id);
        verify(mockAttachmentService).fillAttachments(discussion, 0, DEFAULT_LIMIT);
        verify(mockCommentService).fillCommentable(discussion, 0, DEFAULT_LIMIT);
        verify(mockSubscriberService).fillSubcribers(discussion);
        assertEquals(discussion, result);
    }

    @Test
    public void getDiscussionByIdWithExtraInfoReturnNull() {

        when(mockDiscussionMapper.selectByPrimaryKey(anyInt())).thenReturn(null);

        Discussion result = discussionServiceImpl.getByIdWithDetail(id);

        verify(mockDiscussionMapper).selectByPrimaryKey(id);
        verify(mockAttachmentService, times(0)).fillAttachments(any(Attachable.class), anyInt(), anyInt());
        verify(mockCommentService, times(0)).fillCommentable(any(Commentable.class), anyInt(), anyInt());
        verify(mockSubscriberService, times(0)).fillSubcribers(any(Subscribable.class));
        assertEquals(null, result);
    }

    @Test
    public void getDiscussionsByProjectId() {

        List<Discussion> result = discussionServiceImpl.getDiscussionsByProjectId(projectId);

        verify(mockDiscussionMapper).selectByExample(argThat(new ExampleMatcher<DiscussionExample>() {
            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "projectId", projectId);
            }
        }));
        assertEquals(discussions, result);
    }

    @Test
    public void getDiscardedDiscussions() {
        List<Discussion> result = discussionServiceImpl.getDiscardedDiscussions();
        verify(mockDiscussionMapper).selectByExample(argThat(new ExampleMatcher<DiscussionExample>() {
            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "deleted", true);
            }
        }));
        assertEquals(discussions, result);
    }

    @Test
    public void countByExample() {
        int count = 5;
        when(mockDiscussionMapper.countByExample(any(DiscussionExample.class))).thenReturn(count);
        Discussion d = new Discussion();
        d.setCompanyId(companyId);

        int result = discussionServiceImpl.countBySample(d);

        verify(mockDiscussionMapper).countByExample(argThat(new ExampleMatcher<DiscussionExample>() {
            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "companyId", companyId);
            }
        }));
        assertEquals(count, result);
    }

    // private void mockUpdateAttachments(int times) {
    // when(mockAttachmentService.getAttachmentById(anyInt())).thenReturn(attachment).thenReturn(attachment);
    // }
    //
    // private void verifyUpdateAttachments(int times) {
    //
    // }
    //
    // private void verifyUpdateAttachmentsWithAttachmentsNull() {
    //
    // }
    //
    // private void updateAttachments(Discussion discussion, List<Attachment>
    // attachments) {
    // if (attachments == null) {
    // return;
    // }
    // for (Attachment a : attachments) {
    // Attachment attachment = attachmentService.getAttachmentById(a.getId());
    // attachment.setAttachId(discussion.getId());
    // attachment.setAttachType(discussion.getType());
    // attachment.setTargetId(discussion.getId());
    // attachment.setTargetType(discussion.getType());
    // attachment.setCreatorName(discussion.getCreatorName());
    // attachmentService.updateAttachment(attachment);
    //
    // attachmentService.moveStageAttachment(a.getId(),
    // discussion.getProjectId());
    // }
    // }

    @Test
    public void createDiscussion() {

        Discussion d = new Discussion();
        d.setContent(content);
        d.setSubject(subject);
        d.setCreatorId(user.getId());
        d.setCreatorName(user.getName());

        Discussion result = discussionServiceImpl.create(d);

        verify(mockDiscussionMapper).insert(argThat(new AbstractMatcher<Discussion>() {

            @Override
            public boolean matches(Object arg0) {
                Discussion d = (Discussion) arg0;
                return d.getCreated() != null && d.getUpdated() != null && d.getCreatorId().equals(user.getId())
                        && d.getCreatorName().equals(user.getName()) && d.getDeleted().equals(false)
                        && d.getContent().equals(content) && d.getSubject().equals(subject);
            }

        }));
        verify(mockTopicService).buildTopicFromDiscussion(d);
        verify(mockTopicService).createTopic(topic);
        /**
         * TODO : verify updateAttachments
         */
        verify(mockSubscriberService).generateSubscribers(d, user);
        verify(mockSubscriberService).addSubscribers(d);
        assertEquals(d, result);

    }

    @Test
    public void createDiscussionWithSubjectNull() {

        Discussion d = new Discussion();
        d.setCreatorId(user.getId());
        d.setCreatorName(user.getName());

        Discussion result = discussionServiceImpl.create(d);

        verify(mockDiscussionMapper).insert(argThat(new AbstractMatcher<Discussion>() {

            @Override
            public boolean matches(Object arg0) {
                Discussion d = (Discussion) arg0;
                return d.getCreated() != null && d.getUpdated() != null && d.getCreatorId().equals(user.getId())
                        && d.getCreatorName().equals(user.getName()) && d.getDeleted().equals(false)
                        && d.getSubject().equals(String.format("%s%s", user.getName(), "发起了一个讨论"));
            }

        }));
        assertEquals(d, result);

    }

    @Test
    public void deleteDiscussion() {

        discussionServiceImpl.deleteFromTrash(id);

        verify(mockAttachmentService).deleteAttachmentByAttachTypeAndId(new Discussion().getType(), id);
        verify(mockCommentService).deleteCommentByAttachTypeAndId(new Discussion().getType(), id);
        verify(mockDiscussionMapper).deleteByPrimaryKey(id);

    }

    @Test
    public void updateDiscussion() {
        // mock
        Discussion srcDiscussion = getASampleDiscussion();
        Discussion updatedDiscussion = getASampleDiscussion();
        when(mockDiscussionMapper.selectByPrimaryKey(id)).thenReturn(srcDiscussion).thenReturn(updatedDiscussion)
                .thenReturn(discussion);
        when(mockTopicService.buildTopicFromDiscussion(discussion)).thenReturn(topic);

        // run
        Discussion result = discussionServiceImpl.updateSelective(discussion);

        // verify
        verify(mockDiscussionMapper, times(2)).selectByPrimaryKey(id);
        verify(mockDiscussionMapper, times(1)).updateByPrimaryKeySelective(discussion);
        verify(mockAttachmentService).fillAttachments(updatedDiscussion, 0, -1);
        // verify(mockSubscriberService).generateSubscribers(discussion, user);
        verify(mockSubscriberService).addSubscribers(discussion);
        // trash
        verify(mockAttachmentService, times(0)).discardAttachment(new Discussion().getType(), id);
        verify(mockTopicService, times(0)).discardTopcicByTypeAndId(new Discussion().getType(), id);
        // recover
        verify(mockAttachmentService, times(0)).recoverAttachment(new Discussion().getType(), id);
        verify(mockTopicService, times(0)).recoverTopcicByTypeAndId(new Discussion().getType(), id);
        // 正常更新
        verify(mockAttachmentService).appendAttachmentsForAttachable(discussion);
        verify(mockTopicService).buildTopicFromDiscussion(discussion);
        verify(mockTopicService).createOrUpdateTopic(topic);
        // result
        assertEquals(discussion, result);
    }

    @Test
    public void updateDiscussionWithDeleted() {
        // mock
        discussion.setDeleted(true);
        Discussion srcDiscussion = getASampleDiscussion();
        Discussion updatedDiscussion = getASampleDiscussion();
        when(mockDiscussionMapper.selectByPrimaryKey(id)).thenReturn(srcDiscussion).thenReturn(updatedDiscussion)
                .thenReturn(discussion);

        // run
        Discussion result = discussionServiceImpl.updateSelective(discussion);

        // verify
        verify(mockDiscussionMapper, times(2)).selectByPrimaryKey(id);
        verify(mockDiscussionMapper, times(1)).updateByPrimaryKeySelective(discussion);
        verify(mockAttachmentService).fillAttachments(updatedDiscussion, 0, -1);
        // verify(mockSubscriberService).generateSubscribers(discussion, user);
        verify(mockSubscriberService).addSubscribers(discussion);
        // trash
        verify(mockAttachmentService).discardAttachment(new Discussion().getType(), id);
        verify(mockTopicService).discardTopcicByTypeAndId(new Discussion().getType(), id);
        // recover
        verify(mockAttachmentService, times(0)).recoverAttachment(new Discussion().getType(), id);
        verify(mockTopicService, times(0)).recoverTopcicByTypeAndId(new Discussion().getType(), id);
        // 正常更新
        verify(mockAttachmentService, times(0)).appendAttachmentsForAttachable(discussion);
        verify(mockTopicService, times(0)).buildTopicFromDiscussion(discussion);
        verify(mockTopicService, times(0)).createOrUpdateTopic(topic);
        // result
        assertEquals(discussion, result);
    }

    @Test
    public void updateDiscussionWithRecover() {
        // mock
        Discussion srcDiscussion = getASampleDiscussion();
        srcDiscussion.setDeleted(true);
        Discussion updatedDiscussion = getASampleDiscussion();
        when(mockDiscussionMapper.selectByPrimaryKey(id)).thenReturn(srcDiscussion).thenReturn(updatedDiscussion)
                .thenReturn(discussion);

        // run
        Discussion result = discussionServiceImpl.updateSelective(discussion);

        // verify
        verify(mockDiscussionMapper, times(2)).selectByPrimaryKey(id);
        verify(mockDiscussionMapper, times(1)).updateByPrimaryKeySelective(discussion);
        verify(mockAttachmentService).fillAttachments(updatedDiscussion, 0, -1);
        // verify(mockSubscriberService).generateSubscribers(discussion, user);
        verify(mockSubscriberService).addSubscribers(discussion);
        // trash
        verify(mockAttachmentService, times(0)).discardAttachment(new Discussion().getType(), id);
        verify(mockTopicService, times(0)).discardTopcicByTypeAndId(new Discussion().getType(), id);
        // recover
        verify(mockAttachmentService).recoverAttachment(new Discussion().getType(), id);
        verify(mockTopicService).recoverTopcicByTypeAndId(new Discussion().getType(), id);
        // 正常更新
        verify(mockAttachmentService, times(0)).appendAttachmentsForAttachable(discussion);
        verify(mockTopicService, times(0)).createOrUpdateTopic(topic);
        // result
        assertEquals(discussion, result);
    }

    @Test
    public void discardDiscussion() {

        spyDiscussionService.delete(id);

        verify(spyDiscussionService).updateSelective(argThat(new AbstractMatcher<Discussion>() {
            @Override
            public boolean matches(Object arg0) {
                Discussion discussion = (Discussion) arg0;
                return discussion.getId().equals(id) && discussion.getDeleted().equals(true);
            }

        }));
    }

    @Test
    public void recoverDiscussion() {

        spyDiscussionService.recover(id);

        verify(spyDiscussionService).updateSelective(argThat(new AbstractMatcher<Discussion>() {
            @Override
            public boolean matches(Object arg0) {
                Discussion discussion = (Discussion) arg0;
                return discussion.getId().equals(id) && discussion.getDeleted().equals(false);
            }

        }));
    }

    @Test
    public void moveDiscussion() {

        spyDiscussionService.moveDiscussion(discussion, projectId + 1);

        verify(spyDiscussionService).updateSelective(argThat(new AbstractMatcher<Discussion>() {
            @Override
            public boolean matches(Object arg0) {
                Discussion discussion = (Discussion) arg0;
                return discussion.getId().equals(id) && discussion.getProjectId().equals(projectId + 1);
            }
        }));
    }

}
