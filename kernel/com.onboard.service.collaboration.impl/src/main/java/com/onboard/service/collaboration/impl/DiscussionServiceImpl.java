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
import org.springframework.util.StringUtils;

import com.onboard.domain.mapper.DiscussionMapper;
import com.onboard.domain.mapper.base.BaseMapper;
import com.onboard.domain.mapper.model.DiscussionExample;
import com.onboard.domain.model.Discussion;
import com.onboard.domain.model.Topic;
import com.onboard.service.account.UserService;
import com.onboard.service.base.AbstractBaseService;
import com.onboard.service.collaboration.AttachmentService;
import com.onboard.service.collaboration.CommentService;
import com.onboard.service.collaboration.DiscussionService;
import com.onboard.service.collaboration.TopicService;
import com.onboard.service.common.subscrible.SubscriberService;
import com.onboard.service.web.SessionService;

/**
 * {@link DiscussionService}接口实现
 * 
 * @author huangsz, yewei
 */
@Transactional
@Service("discussionServiceBean")
public class DiscussionServiceImpl extends AbstractBaseService<Discussion, DiscussionExample> implements DiscussionService {

    public static final int DEFAULT_LIMIT = -1;

    public static final Logger logger = LoggerFactory.getLogger(DiscussionServiceImpl.class);

    @Autowired
    private DiscussionMapper discussionMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private AttachmentService attachmentService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private SubscriberService subscriberService;

    @Autowired
    private TopicService topicService;

    @Autowired
    private SessionService sessionService;

    @Override
    public Discussion getDiscussionByIdForUpdate(int id) {
        Discussion discussion = discussionMapper.selectByPrimaryKey(id);
        if (discussion != null) {
            attachmentService.fillAttachments(discussion, 0, DEFAULT_LIMIT);
            subscriberService.fillSubcribers(discussion);
        }
        return discussion;
    }

    @Override
    public Discussion getByIdWithDetail(int id) {
        Discussion discussion = discussionMapper.selectByPrimaryKey(id);
        if (discussion != null) {
            attachmentService.fillAttachments(discussion, 0, DEFAULT_LIMIT);
            commentService.fillCommentable(discussion, 0, DEFAULT_LIMIT);
            subscriberService.fillSubcribers(discussion);
        }
        return discussion;
    }

    @Override
    public List<Discussion> getDiscussionsByProjectId(int projectId) {
        Discussion discussion = new Discussion();

        discussion.setProjectId(projectId);
        DiscussionExample discussionExmaple = new DiscussionExample(discussion);

        return discussionMapper.selectByExample(discussionExmaple);
    }

    @Override
    public List<Discussion> getDiscussionsByCompanyIdBetweenDates(int companyId, Date since, Date until) {
        Discussion discussion = new Discussion();
        discussion.setCompanyId(companyId);
        DiscussionExample discussionExmaple = new DiscussionExample(discussion);

        DateTime dt = new DateTime(since);
        since = dt.withTimeAtStartOfDay().toDate();
        dt = new DateTime(until);
        until = dt.withTimeAtStartOfDay().plusDays(1).toDate();

        discussionExmaple.getOredCriteria().get(0).andCreatedGreaterThanOrEqualTo(since).andCreatedLessThan(until);
        return discussionMapper.selectByExample(discussionExmaple);
    }

    @Override
    public List<Discussion> getDiscardedDiscussions() {
        Discussion discussion = new Discussion(true);
        return discussionMapper.selectByExample(new DiscussionExample(discussion));
    }

    @Override
    public Discussion create(Discussion discussion) {
        discussion.setCreated(new Date());
        discussion.setUpdated(discussion.getCreated());
        discussion.setDeleted(false);
        discussion.setCreatorId(sessionService.getCurrentUser().getId());
        discussion.setCreatorName(sessionService.getCurrentUser().getName());
        discussion.setCreatorAvatar(sessionService.getCurrentUser().getAvatar());
        if (!StringUtils.hasLength(discussion.getSubject())) {
            discussion.setSubject(String.format("%s%s", discussion.getCreatorName(), "发起了一个讨论"));
        }
        logger.info("Generating new discussion...");
        discussionMapper.insert(discussion);

        logger.info("Generating topic from new discussion...");
        Topic topic = topicService.buildTopicFromDiscussion(discussion);
        topicService.createTopic(topic);

        discussion.setAttachments(attachmentService.addAttachmentsForAttachable(discussion, discussion.getAttachments()));

        subscriberService.generateSubscribers(discussion, userService.getById(discussion.getCreatorId()));
        subscriberService.addSubscribers(discussion);

        return discussion;

    }

    @Override
    public void deleteFromTrash(int id) {
        topicService.discardTopcicByTypeAndId(getModelType(), id);
        attachmentService.deleteAttachmentByAttachTypeAndId(getModelType(), id);
        commentService.deleteCommentByAttachTypeAndId(getModelType(), id);
        discussionMapper.deleteByPrimaryKey(id);
    }

    @Override
    public Discussion updateSelective(Discussion discussion) {

        Discussion srcDiscussion = discussionMapper.selectByPrimaryKey(discussion.getId());

        discussion.setUpdated(new Date());
        discussionMapper.updateByPrimaryKeySelective(discussion);
        Discussion updatedDiscussion = discussionMapper.selectByPrimaryKey(discussion.getId());

        attachmentService.fillAttachments(updatedDiscussion, 0, -1);

        subscriberService.addSubscribers(discussion);

        if (discussion.getDeleted() != null && discussion.getDeleted()) {
            attachmentService.discardAttachment(new Discussion().getType(), discussion.getId());
            topicService.discardTopcicByTypeAndId(discussion.getType(), discussion.getId());
        } else if (discussion != null && srcDiscussion != null && srcDiscussion.getDeleted()) {
            attachmentService.recoverAttachment(new Discussion().getType(), discussion.getId());
            topicService.recoverTopcicByTypeAndId(discussion.getType(), discussion.getId());
        } else {
            attachmentService.appendAttachmentsForAttachable(discussion);
            Topic topic = topicService.buildTopicFromDiscussion(discussion);
            topicService.createOrUpdateTopic(topic);
        }

        return discussion;

    }

    @Override
    public void moveDiscussion(Discussion discussion, int projectId) {
        // TODO: move topic
        Discussion example = new Discussion();
        example.setId(discussion.getId());
        example.setProjectId(projectId);
        updateSelective(example);
    }

    @Override
    public void delete(int id) {
        Discussion discussion = new Discussion(id, true);
        updateSelective(discussion);

    }

    @Override
    public void recover(int id) {
        Discussion discussion = new Discussion();
        discussion.setId(id);
        discussion.setDeleted(false);
        updateSelective(discussion);
    }

    @Override
    protected BaseMapper<Discussion, DiscussionExample> getBaseMapper() {
        return discussionMapper;
    }

    @Override
    public Discussion newItem() {
        return new Discussion();
    }

    @Override
    public DiscussionExample newExample() {
        return new DiscussionExample();
    }

    @Override
    public DiscussionExample newExample(Discussion item) {
        return new DiscussionExample(item);
    }
}
