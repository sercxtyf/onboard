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

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.onboard.domain.mapper.TopicMapper;
import com.onboard.domain.mapper.UserMapper;
import com.onboard.domain.mapper.model.TopicExample;
import com.onboard.domain.model.Comment;
import com.onboard.domain.model.Discussion;
import com.onboard.domain.model.Topic;
import com.onboard.domain.model.type.BaseOperateItem;
import com.onboard.domain.model.type.BaseProjectItem;
import com.onboard.domain.model.type.Commentable;
import com.onboard.domain.model.utils.HtmlTextParser;
import com.onboard.service.collaboration.TopicService;
import com.onboard.service.common.identifiable.IdentifiableManager;

/**
 * {@link TopicService}接口实现
 * 
 * @author yewei
 * 
 */
@Transactional
@Service("topicServiceBean")
public class TopicServiceImpl implements TopicService {

    private static final int MAX_EXCERPT_LENGTH = 200;

    private static final int MAX_TITLE_LENGTH = 50;

    public static final Logger logger = LoggerFactory.getLogger(TopicServiceImpl.class);

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private TopicMapper topicMapper;

    @Autowired
    private IdentifiableManager identifiableManager;

    @Override
    public Topic createOrUpdateTopic(Topic topic) {

        if (topic == null) {
            return null;
        }

        Topic t = new Topic();
        t.setRefId(topic.getRefId());
        t.setRefType(topic.getRefType());
        TopicExample example = new TopicExample(t);

        List<Topic> topics = topicMapper.selectByExample(example);
        if (topics.size() == 0) {
            createTopic(topic);
            return topic;
        } else {
            topic.setId(topics.get(0).getId());
            topic.setCreated(null);
            updateTopic(topic);
            return topic;
        }
    }

    @Override
    public int countByExample(Topic item) {
        TopicExample example = new TopicExample(item);
        return topicMapper.countByExample(example);
    }

    @Override
    public Topic updateTopic(Topic topic) {
        // topic.setUpdated(new Date());
        checkTopicTitleLength(topic);
        topicMapper.updateByPrimaryKeySelective(topic);
        return topic;
    }

    private void checkTopicTitleLength(Topic topic) {
        String title = topic.getTitle();
        if (title != null) {
            title = title.substring(0, Math.min(MAX_TITLE_LENGTH, title.length()));
            topic.setTitle(title);
        }
    }

    @Override
    public Topic createTopic(Topic topic) {
        // Date now = new Date();
        // topic.setCreated(now);
        // topic.setUpdated(now);
        topic.setStick(false);
        topic.setExcerpt(HtmlTextParser.getPlainText(topic.getExcerpt()));
        checkTopicTitleLength(topic);
        topicMapper.insert(topic);
        return topic;
    }

    private void checkTopicExcerptLength(Topic topic) {
        String excerpt = topic.getExcerpt();
        if (excerpt != null) {
            excerpt = excerpt.substring(0, Math.min(MAX_EXCERPT_LENGTH, excerpt.length()));
            topic.setExcerpt(excerpt);
        }
    }

    @Override
    public Topic buildTopicFromComment(Comment comment, int projectId) {
        Topic topic = new Topic();
        topic.setCreated(comment.getCreated());
        topic.setExcerpt(comment.getContent());
        checkTopicExcerptLength(topic);
        topic.setLastUpdatorId(comment.getCreatorId());
        topic.setProjectId(projectId);
        topic.setRefId(comment.getAttachId());
        topic.setRefType(comment.getAttachType());

        Commentable commentable = (Commentable) identifiableManager.getIdentifiableByTypeAndId(comment.getAttachType(),
                comment.getAttachId());

        if (commentable == null) {
            return null;
        }

        topic.setTitle(commentable.getCommentSubject());
        topic.setUpdated(comment.getUpdated());
        topic.setDeleted(false);
        return topic;
    }

    @Override
    public Topic buildTopicFromDiscussion(Discussion discussion) {
        Topic topic = new Topic();
        topic.setCreated(discussion.getCreated());
        topic.setExcerpt(discussion.getContent());
        checkTopicExcerptLength(topic);
        topic.setLastUpdatorId(discussion.getCreatorId());
        topic.setProjectId(discussion.getProjectId());
        topic.setRefId(discussion.getId());
        topic.setRefType(discussion.getType());
        topic.setTitle(discussion.getSubject());
        topic.setUpdated(discussion.getUpdated());
        topic.setDeleted(false);
        return topic;
    }

    @Override
    public List<Topic> getTopicListByProjectId(int projectId, int start, int limit) {
        Topic topic = new Topic(false);
        topic.setProjectId(projectId);
        topic.setStick(false);
        TopicExample example = new TopicExample(topic);
        example.setLimit(start, limit);
        example.setOrderByClause("updated desc");

        List<Topic> topics = Lists.newArrayList();
        if (start == 0) {
            topics = getCollectedTopics(projectId);
        }
        topics.addAll(topicMapper.selectByExample(example));

        for (Topic t : topics) {
            t.setExcerpt(HtmlTextParser.getPlainText(t.getExcerpt()));
            t.setLastUpdator(userMapper.selectByPrimaryKey(t.getLastUpdatorId()));
            BaseOperateItem identifiable = identifiableManager.getIdentifiableByTypeAndId(t.getRefType(), t.getRefId());
            if (identifiable != null) {
                t.setTargetCreator(userMapper.selectByPrimaryKey(((BaseProjectItem)identifiable).getCreatorId()));
            }
        }
        return topics;
    }

    @Override
    public void deleteTopcic(int id) {
        topicMapper.deleteByPrimaryKey(id);
    }

    @Override
    public void discardTopcicByTypeAndId(String type, int id) {
        Topic example = new Topic();
        example.setRefId(id);
        example.setRefType(type);

        Topic topic = new Topic(true);

        topicMapper.updateByExampleSelective(topic, new TopicExample(example));

    }

    @Override
    public void recoverTopcicByTypeAndId(String type, int id) {
        Topic example = new Topic();
        example.setRefId(id);
        example.setRefType(type);

        Topic topic = new Topic(false);

        topicMapper.updateByExampleSelective(topic, new TopicExample(example));

    }

    @Override
    public Topic getTopicByTypeAndId(String type, int id) {
        Topic example = new Topic();
        example.setRefId(id);
        example.setRefType(type);

        List<Topic> topics = topicMapper.selectByExample(new TopicExample(example));
        if (topics != null && !topics.isEmpty()) {
            // 只可能有一个topic
            Topic topic = topics.get(0);
            if (topic.getLastUpdator() == null) {
                topic.setLastUpdator(userMapper.selectByPrimaryKey(topic.getLastUpdatorId()));
            }
            
            String a = topic.getExcerpt();
            String b = HtmlTextParser.getPlainText(a);
            
            
            topic.setExcerpt(HtmlTextParser.getPlainText(topic.getExcerpt()));

            BaseOperateItem identifiable = identifiableManager.getIdentifiableByTypeAndId(topic.getRefType(),
                    topic.getRefId());
            if (identifiable != null) {
                topic.setTargetCreator(userMapper.selectByPrimaryKey(((BaseProjectItem)identifiable).getCreatorId()));
            }

            return topic;
        }

        return null;

    }

    @Override
    public Topic stickTopic(int id) {
        Topic topicExample = new Topic(false);
        topicExample.setId(id);
        topicExample.setStick(true);
        topicMapper.updateByPrimaryKeySelective(topicExample);
        return topicMapper.selectByPrimaryKey(id);
    }

    @Override
    public Topic unstickTopic(int id) {
        Topic topicExample = new Topic(false);
        topicExample.setId(id);
        topicExample.setStick(false);
        topicMapper.updateByPrimaryKeySelective(topicExample);
        return topicMapper.selectByPrimaryKey(id);
    }

    @Override
    public int getTopicCount(int projectId) {
        Topic topicExample = new Topic(false);
        topicExample.setProjectId(projectId);
        topicExample.setStick(true);
        return topicMapper.countByExample(new TopicExample(topicExample));
    }

    @Override
    public List<Topic> getCollectedTopics(int projectId) {
        Topic topicExample = new Topic(false);
        topicExample.setProjectId(projectId);
        topicExample.setStick(true);

        return Lists.newArrayList(topicMapper.selectByExample(new TopicExample(topicExample)));
    }
}
