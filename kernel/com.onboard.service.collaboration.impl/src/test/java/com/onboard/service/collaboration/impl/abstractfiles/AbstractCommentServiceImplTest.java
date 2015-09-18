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
package com.onboard.service.collaboration.impl.abstractfiles;

import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.onboard.domain.mapper.CommentMapper;
import com.onboard.domain.mapper.model.CommentExample;
import com.onboard.domain.model.Attachment;
import com.onboard.domain.model.Comment;
import com.onboard.domain.model.Discussion;
import com.onboard.domain.model.Topic;
import com.onboard.domain.model.User;
import com.onboard.test.moduleutils.ModuleHelper;

@RunWith(MockitoJUnitRunner.class)
public abstract class AbstractCommentServiceImplTest {
    @Mock
    public CommentMapper mockCommentMapper;

    protected Comment comment;
    protected List<Comment> listOfComments;
    protected static int mapperReturnValue = 1;
    protected static int DEFAULT_LIMIT = -1;

    protected List<Attachment> attachmentList;
    protected User user;
    protected Topic topic;
    protected Attachment attachment;
    protected Discussion discussion;

    @Before
    public void setupCollectionTest() {
        initCommentMapper();
    }

    private void initCommentMapper() {

        comment = getAComment();
        listOfComments = getAListofComments();
        user = getAUser();
        topic = getATopic();
        attachment = getAAttachment();
        attachmentList = getAttachmentList();
        discussion = getADiscussion();

        when(mockCommentMapper.countByExample(Mockito.any(CommentExample.class))).thenReturn(ModuleHelper.count);

        when(mockCommentMapper.deleteByExample(Mockito.any(CommentExample.class))).thenReturn(mapperReturnValue);
        when(mockCommentMapper.deleteByPrimaryKey(ModuleHelper.id)).thenReturn(mapperReturnValue);

        when(mockCommentMapper.insert(Mockito.any(Comment.class))).thenReturn(mapperReturnValue);
        when(mockCommentMapper.insertSelective(Mockito.any(Comment.class))).thenReturn(mapperReturnValue);

        when(mockCommentMapper.selectByExample(Mockito.any(CommentExample.class))).thenReturn(listOfComments);
        when(mockCommentMapper.selectByPrimaryKey(ModuleHelper.id)).thenReturn(comment);

        when(mockCommentMapper.updateByExample(Mockito.any(Comment.class), Mockito.any(CommentExample.class))).thenReturn(
                mapperReturnValue);
        when(mockCommentMapper.updateByExampleSelective(Mockito.any(Comment.class), Mockito.any(CommentExample.class)))
                .thenReturn(mapperReturnValue);
        when(mockCommentMapper.updateByPrimaryKey(Mockito.any(Comment.class))).thenReturn(mapperReturnValue);
        when(mockCommentMapper.updateByPrimaryKeySelective(Mockito.any(Comment.class))).thenReturn(mapperReturnValue);

    }

    public Discussion getADiscussion() {
        Discussion discussion = new Discussion();
        discussion.setId(ModuleHelper.id);
        discussion.setProjectId(ModuleHelper.projectId);
        discussion.setCompanyId(ModuleHelper.companyId);
        discussion.setSubject(ModuleHelper.subject);

        return discussion;
    }

    public List<Comment> getAListofComments() {
        List<Comment> list = new ArrayList<Comment>();

        list.add(getAComment());
        list.add(getAComment());
        return list;
    }

    public Comment getAComment() {
        Comment comment = new Comment();
        comment.setId(ModuleHelper.id);
        comment.setAttachId(ModuleHelper.attachId);
        comment.setAttachType(ModuleHelper.attachType);
        comment.setCreatorName(ModuleHelper.creatorName);
        comment.setContent(ModuleHelper.content);
        comment.setCompanyId(ModuleHelper.companyId);
        comment.setCreatorId(ModuleHelper.creatorId);
        comment.setProjectId(ModuleHelper.projectId);
        comment.setUpdated(ModuleHelper.updated);
        comment.setCreated(ModuleHelper.created);
        return comment;
    }

    public Attachment getAAttachment() {
        Attachment attachment = new Attachment();
        attachment.setId(ModuleHelper.id);
        attachment.setAttachId(ModuleHelper.attachId);
        return attachment;
    }

    public List<Attachment> getAttachmentList() {
        List<Attachment> list = new ArrayList<Attachment>();
        list.add(getAAttachment());
        list.add(getAAttachment());
        return list;
    }

    public User getAUser() {
        User user = new User();
        user.setId(ModuleHelper.id);
        user.setName(ModuleHelper.name);
        return user;
    }

    public Topic getATopic() {
        Topic topic = new Topic();
        topic.setId(ModuleHelper.id);
        return topic;
    }
}
