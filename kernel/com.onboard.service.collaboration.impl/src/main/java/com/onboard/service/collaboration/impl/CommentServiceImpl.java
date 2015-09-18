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
package com.onboard.service.collaboration.impl;

import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.onboard.domain.mapper.CommentMapper;
import com.onboard.domain.mapper.base.BaseMapper;
import com.onboard.domain.mapper.model.CommentExample;
import com.onboard.domain.mapper.model.CommentObject;
import com.onboard.domain.model.Comment;
import com.onboard.domain.model.Discussion;
import com.onboard.domain.model.Topic;
import com.onboard.domain.model.type.Commentable;
import com.onboard.domain.model.type.Subscribable;
import com.onboard.service.account.UserService;
import com.onboard.service.base.AbstractBaseService;
import com.onboard.service.collaboration.AttachmentService;
import com.onboard.service.collaboration.CommentService;
import com.onboard.service.collaboration.DiscussionService;
import com.onboard.service.collaboration.TopicService;
import com.onboard.service.common.identifiable.IdentifiableManager;
import com.onboard.service.common.subscrible.SubscriberService;
import com.onboard.service.web.SessionService;

/**
 * {@link CommentService}接口实现
 * 
 * @author yewei
 * 
 */
@Transactional
@Service("commentServiceBean")
public class CommentServiceImpl extends AbstractBaseService<Comment, CommentExample> implements CommentService {

    public static final Logger logger = LoggerFactory.getLogger(CommentServiceImpl.class);

    public static final int MAX_ITEM_NO = -1;

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private AttachmentService attachmentService;

    @Autowired
    private TopicService topicService;

    @Autowired
    private DiscussionService discussionService;

    @Autowired
    private IdentifiableManager identifiableManager;

    @Autowired
    private SubscriberService subscriberService;

    @Autowired
    private UserService userService;

    @Autowired
    private SessionService sessionService;

    @Override
    public Comment getByIdWithDetail(int id) {
        Comment comment = commentMapper.selectByPrimaryKey(id);
        if (comment != null) {
            // fulfill attachments
            comment.setAttachments(attachmentService.getAttachmentsByTypeAndId(comment.getType(), id, 0, MAX_ITEM_NO));
            comment.setCreator(userService.getById(comment.getCreatorId()));
        }
        return comment;
    }

    @Override
    public List<Comment> getCommentsByTopic(String attachType, int attachId, int start, int limit) {
        Comment comment = new Comment();
        comment.setAttachId(attachId);
        comment.setAttachType(attachType);
        comment.setDeleted(false);
        CommentExample example = new CommentExample(comment);
        example.setLimit(start, limit);

        List<Comment> commentList = commentMapper.selectByExample(example);
        for (Comment com : commentList) {
            // fulfill attachments
            com.setAttachments(attachmentService.getAttachmentsByTypeAndId(comment.getType(), com.getId(), 0, MAX_ITEM_NO));
        }

        return commentList;
    }

    @Override
    public void fillCommentable(Commentable commentable, int start, int limit) {
        if (commentable != null) {
            commentable.setComments(getCommentsByTopic(commentable.getType(), commentable.getId(), start, limit));
        }
    }

    @Override
    public Comment create(Comment comment) {
        comment.setDeleted(false);
        comment.setCreated(new Date());
        comment.setUpdated(comment.getCreated());
        comment.setCreatorId(sessionService.getCurrentUser().getId());
        comment.setCreatorName(sessionService.getCurrentUser().getName());
        comment.setCreatorAvatar(sessionService.getCurrentUser().getAvatar());
        commentMapper.insertSelective(comment);
        createOrUpdateTopicByComment(comment);

        comment.setAttachments(attachmentService.addAttachmentsForAttachable(comment, comment.getAttachments()));

        Subscribable subscribable = (Subscribable) identifiableManager.getIdentifiableByTypeAndId(comment.getAttachType(),
                comment.getAttachId());
        subscribable.setSubscribers(comment.getSubscribers());
        subscriberService.generateSubscribers(subscribable, userService.getById(comment.getCreatorId()));
        subscriberService.addSubscribers(subscribable);

        return comment;

    }

    /**
     * @author Chenlong
     */
    @Override
    public void copyComments(Commentable oldItem, Commentable newItem) {
        List<Comment> comments = getCommentsByTopic(oldItem.getType(), oldItem.getId(), 0, -1);
        logger.info(oldItem.getType() + " has " + comments.size() + " comments");
        Comment newComment;
        for (Comment com : comments) {
            newComment = new Comment(com);
            newComment.setAttachId(newItem.getId());
            newComment.setProjectId(newItem.getProjectId());
            commentMapper.insertSelective(newComment);
            // copy attachments
            attachmentService.copyAttachments(com, newComment);
        }
    }

    /**
     * @author Chenlong
     */
    @Override
    public void relocateComment(Commentable item, int projectId) {
        Comment example = new Comment();
        List<Comment> comments = getCommentsByTopic(item.getType(), item.getId(), 0, -1);
        logger.info(item.getType() + " has " + comments.size() + " comments");
        for (Comment com : comments) {
            example.setId(com.getId());
            example.setProjectId(projectId);
            commentMapper.updateByPrimaryKeySelective(example);
            attachmentService.relocateAttachment(com, projectId);
        }
    }

    /**
     * 在更新comment的时候更新topic，在真正updateComment前调用
     * 
     * @param updatingComment
     */
    private void updateTopicWhenUpdateComment(Comment updatingComment) {
        Comment srcComment = commentMapper.selectByPrimaryKey(updatingComment.getId());
        if (updatingComment.getContent() != null) {
            Discussion dis = discussionService.getById(updatingComment.getAttachId());
            if (dis != null) {
                Topic topic = topicService.buildTopicFromDiscussion(dis);
                topicService.createOrUpdateTopic(topic);
            } else {
                Topic topic = new Topic();
                topic.setRefId(srcComment.getAttachId());
                topic.setRefType(srcComment.getAttachType());
                topic.setExcerpt(updatingComment.getContent().substring(0, Math.min(updatingComment.getContent().length(), 200)));
                topicService.createOrUpdateTopic(topic);
            }
        }
    }

    /**
     * 在执行真正Trash操作之前调用该函数
     * 
     */
    private void updateTopicWhenDiscardComment(Comment deletingComment) {
        // 如果要删除的Comment不是topic显示的，直接返回
        if (!isShowInTopic(deletingComment)) {
            return;
        }
        List<Comment> commentList = getCommentsByAttachTypeAndIdWithNotDiscard(deletingComment.getAttachType(),
                deletingComment.getAttachId());
        assert commentList != null && commentList.size() > 0;
        // 只有一个Comment，若为discussion，则用discussion更新topic
        if (commentList.size() == 1 && deletingComment.getAttachType().equals(new Discussion().getType())) {
            Discussion dis = discussionService.getById(deletingComment.getAttachId());
            Topic topic = topicService.buildTopicFromDiscussion(dis);
            topicService.createOrUpdateTopic(topic);
        }
        // 除了attachType为discussion外，只有一个Comment的情况应删除topic
        else if (commentList.size() == 1) {
            topicService.discardTopcicByTypeAndId(deletingComment.getAttachType(), deletingComment.getAttachId());
        } else {
            createOrUpdateTopicByComment(commentList.get(1));
        }
    }

    /**
     * 根据Comment更新topic
     * 
     * @param comment
     */
    private void createOrUpdateTopicByComment(Comment comment) {
        Topic topic = topicService.buildTopicFromComment(comment, comment.getProjectId());
        topicService.createOrUpdateTopic(topic);
    }

    /**
     * 判断当前Comment是否是topic正在显示的
     * 
     */
    private boolean isShowInTopic(Comment comment) {
        List<Comment> commentList = getCommentsByAttachTypeAndIdWithNotDiscard(comment.getAttachType(), comment.getAttachId());
        CommentObject lastComment = commentList.get(0);
        if (comment.getAttachType().equals(new Discussion().getType())) {
            Discussion dis = discussionService.getById(comment.getAttachId());
            // 当前显示的是讨论
            if (dis.getUpdated().after(lastComment.getUpdated())) {
                return false;
            }
        }
        return lastComment.getId().equals(comment.getId());
    }

    /**
     * 根据attachType和attachId获取Comment列表,根据updated排序
     * 
     * @param type
     * @param id
     * @return
     */
    private List<Comment> getCommentsByAttachTypeAndIdWithNotDiscard(String type, int id) {
        Comment comment = new Comment();
        comment.setAttachId(id);
        comment.setAttachType(type);
        comment.setDeleted(false);

        CommentExample example = new CommentExample(comment);
        example.setOrderByClause("updated desc");
        example.setLimit(0, -1);

        return commentMapper.selectByExample(example);
    }

    @Override
    public void deleteFromTrash(int id) {
        Comment comment = commentMapper.selectByPrimaryKey(id);
        if (comment == null) {
            return;
        }
        commentMapper.deleteByPrimaryKey(id);
        attachmentService.deleteAttachmentByAttachTypeAndId(getModelType(), id);
    }

    // TODO : recover function
    @Override
    public void delete(int id) {
        Comment comment = getById(id);
        updateTopicWhenDiscardComment(comment);
        commentMapper.updateByPrimaryKeySelective(new Comment(comment.getId(), true));
        attachmentService.discardAttachment(comment.getType(), comment.getId());
        comment.setDeleted(true);
    }

    @Override
    public Comment updateSelective(Comment comment) {
        updateTopicWhenUpdateComment(comment);
        comment.setUpdated(new Date());
        commentMapper.updateByPrimaryKeySelective(comment);
        attachmentService.appendAttachmentsForAttachable(comment);
        Comment updatedComment = commentMapper.selectByPrimaryKey(comment.getId());
        return updatedComment;
    }

    @Override
    public Commentable getCommentTarget(String targetType, int id) {
        return (Commentable) identifiableManager.getIdentifiableByTypeAndId(targetType, id);
    }

    @Override
    public String getCommentTargetName(String targetType, int id) {
        Commentable commentable = (Commentable) identifiableManager.getIdentifiableByTypeAndId(targetType, id);
        if (commentable != null) {
            return commentable.getCommentSubject();
        }
        return null;
    }

    @Override
    public int getCountOfCommentsByTopic(String attachType, int attachId) {
        Comment comment = new Comment();
        comment.setAttachId(attachId);
        comment.setAttachType(attachType);
        comment.setDeleted(false);
        CommentExample example = new CommentExample(comment);
        return commentMapper.countByExample(example);
    }

    @Override
    public void deleteCommentByAttachTypeAndId(String type, int id) {
        Comment comment = new Comment();
        comment.setAttachId(id);
        comment.setAttachType(type);
        commentMapper.deleteByExample(new CommentExample(comment));
    }

    @Override
    public List<Comment> getCommentsByCompanyIdBetweenDates(int companyId, Date since, Date until) {
        Comment comment = new Comment();
        comment.setCompanyId(companyId);
        CommentExample commentExmaple = new CommentExample(comment);

        DateTime dt = new DateTime(since);
        since = dt.withTimeAtStartOfDay().toDate();
        dt = new DateTime(until);
        until = dt.withTimeAtStartOfDay().plusDays(1).toDate();

        commentExmaple.getOredCriteria().get(0).andCreatedGreaterThanOrEqualTo(since).andCreatedLessThan(until);
        return commentMapper.selectByExample(commentExmaple);
    }

    @Override
    protected BaseMapper<Comment, CommentExample> getBaseMapper() {
        return commentMapper;
    }

    @Override
    public Comment newItem() {
        return new Comment();
    }

    @Override
    public CommentExample newExample() {
        return new CommentExample();
    }

    @Override
    public CommentExample newExample(Comment item) {
        return new CommentExample(item);
    }
}
