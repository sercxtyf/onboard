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
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.onboard.domain.mapper.TopicMapper;
import com.onboard.domain.mapper.UserMapper;
import com.onboard.domain.mapper.model.TopicExample;
import com.onboard.domain.mapper.model.common.BaseExample;
import com.onboard.domain.model.Comment;
import com.onboard.domain.model.Discussion;
import com.onboard.domain.model.Topic;
import com.onboard.domain.model.User;
import com.onboard.domain.model.type.Commentable;
import com.onboard.service.collaboration.impl.TopicServiceImpl;
import com.onboard.service.common.identifiable.IdentifiableManager;
import com.onboard.test.exampleutils.CriterionVerifier;
import com.onboard.test.exampleutils.ExampleMatcher;
import com.onboard.test.exampleutils.ObjectMatcher;
import com.onboard.test.moduleutils.ModuleHelper;

@RunWith(MockitoJUnitRunner.class)
public class TopicServiceImplTest {
    @Mock
    private UserMapper mockUserMapper;
    @Mock
    private TopicMapper mockTopicMapper;
    @Mock
    private IdentifiableManager mockIdentifiableManager;

    @InjectMocks
    private TopicServiceImpl topicServiceImpl;

    private static int id = 1;
    private final int size = 5;
    private static String excerptString = "ExcerptString";
    private static String lastUpdatorNameString = "topic lastUpdatorName";
    private static String refTypeString = "refType";
    private static String titleString = "Title";
    private static String lastUpdatorEmailString = "last@lastupdate.com";
    private static String contentString = "commentContent";
    private static String attachTypeString = "commentAttachType";
    private static Date date = new Date();
    private User user;
    private List<Topic> topicList;
    private Topic topic;
    private Comment comment;
    private Discussion discussion;

    private Topic getATopic() {
        Topic topic = new Topic();
        topic.setId(id);
        topic.setProjectId(id);
        topic.setExcerpt(excerptString);
        topic.setLastUpdatorId(id);
        topic.setLastUpdatorName(lastUpdatorNameString);
        topic.setRefId(id);
        topic.setTitle(titleString);
        topic.setRefType(refTypeString);
        topic.setCompanyId(id);
        return topic;
    }

    private Comment getAComment() {
        Comment comment = new Comment();
        comment.setId(id);
        comment.setCreated(date);
        comment.setContent(contentString);
        comment.setCreatorId(id);
        comment.setAttachId(id);
        comment.setAttachType(attachTypeString);
        return comment;
    }

    private Discussion getADiscussion() {
        Discussion discussion = new Discussion();
        discussion.setId(id);
        discussion.setCreated(date);
        discussion.setContent(contentString);
        discussion.setCreatorId(id);
        discussion.setProjectId(id);
        discussion.setSubject(attachTypeString);
        return discussion;
    }

    private User getAUser() {
        User user = new User();
        user.setId(id);
        user.setName(lastUpdatorNameString);
        user.setEmail(lastUpdatorEmailString);
        return user;
    }

    private List<Topic> getATopicList() {
        Topic topic1 = getATopic();
        Topic topic2 = getATopic();

        List<Topic> list = new ArrayList<Topic>();
        list.add(topic1);
        list.add(topic2);
        return list;
    }

    @Before
    public void setupBefore() {
        user = getAUser();
        topicList = getATopicList();
        topic = getATopic();
        comment = getAComment();
        discussion = getADiscussion();

        when(mockTopicMapper.updateByExampleSelective(any(Topic.class), any(TopicExample.class))).thenReturn(1);
        when(mockTopicMapper.countByExample(any(TopicExample.class))).thenReturn(size);
        when(mockTopicMapper.updateByPrimaryKeySelective(topic)).thenReturn(1);
        when(mockTopicMapper.updateByPrimaryKeySelective(Mockito.any(Topic.class))).thenReturn(0);
        when(mockTopicMapper.insert(topic)).thenReturn(1);
        when(mockTopicMapper.deleteByPrimaryKey(id)).thenReturn(size);
        when(mockTopicMapper.deleteByExample(any(TopicExample.class))).thenReturn(1);
        when(mockTopicMapper.selectByExample(any(TopicExample.class))).thenReturn(topicList);
        when(mockTopicMapper.selectByPrimaryKey(id)).thenReturn(topic);
        when(mockUserMapper.selectByPrimaryKey(id)).thenReturn(user).thenReturn(user);

    }

    @Test
    public void testCreateOrUpdateTopic1() {
        Topic topicNull = null;
        Topic returnTopic = topicServiceImpl.createOrUpdateTopic(topicNull);
        assertNull(returnTopic);
    }
    
    @Test
    public void testCreateOrUpdateTopic2() {
        List<Topic> topicList0 = new ArrayList<Topic>();
        
        TopicServiceImpl spyTopicService = spy(topicServiceImpl);
        Mockito.doReturn(topic).when(spyTopicService).createTopic(topic);
        when(mockTopicMapper.selectByExample(any(TopicExample.class))).thenReturn(topicList0);
        
        Topic returnTopic = spyTopicService.createOrUpdateTopic(topic);
        
        verify(spyTopicService).createOrUpdateTopic(Mockito.any(Topic.class));
        verify(mockTopicMapper).selectByExample(argThat(new ExampleMatcher<TopicExample>() {
            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "refId", topic.getRefId())
                        && CriterionVerifier.verifyEqualTo(example, "refType", topic.getRefType());
            }
        }));
        verify(spyTopicService).createTopic(Mockito.argThat(new ObjectMatcher<Topic>() {
            @Override
            public boolean verifymatches(Topic _topic) {
                return _topic.equals(topic);
            }
        }));
        Mockito.verifyNoMoreInteractions(spyTopicService);
        Mockito.verifyNoMoreInteractions(mockTopicMapper);

        assertEquals(returnTopic.getExcerpt(), topic.getExcerpt());
        assertEquals(returnTopic.getTitle(), topic.getTitle());
    }

    @Test
    public void testCreateOrUpdateTopic3() {
        Topic returnTopic = topicServiceImpl.createOrUpdateTopic(topic);
        verify(mockTopicMapper).selectByExample(argThat(new ExampleMatcher<TopicExample>() {
            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "refId", topic.getRefId())
                        && CriterionVerifier.verifyEqualTo(example, "refType", topic.getRefType());
            }
        }));

        TopicServiceImpl spyTopicService = spy(topicServiceImpl);

        doReturn(topic).when(spyTopicService).updateTopic(topic);

        assertEquals(returnTopic.getId(), topicList.get(0).getId());
        assertNull(returnTopic.getCreated());

    }

    @Test
    public void testCountByExample() {
        int result = topicServiceImpl.countByExample(topic);
        assertEquals(result, size);
    }

    @Test
    public void testUpdateTopic() {
        Topic topic0 = topicServiceImpl.updateTopic(topic);
        verify(mockTopicMapper).updateByPrimaryKeySelective(topic);

        assertEquals(topic0.getId(), topic.getId());
        assertEquals(topic0.getExcerpt(), topic.getExcerpt());
    }

    @Test
    public void testcreateTopic() {
        Topic topicTemp = topicServiceImpl.createTopic(topic);
        verify(mockTopicMapper).insert(topic);

        assertEquals(topic.getTitle(), topicTemp.getTitle());
        assertEquals(topicTemp.getExcerpt(), topic.getExcerpt());
    }

    @Test
    public void testBuildTopicFromComment1() {
        Commentable mockCommentable = mock(Commentable.class);
        when(mockIdentifiableManager.getIdentifiableByTypeAndId(comment.getAttachType(), comment.getAttachId())).thenReturn(
                mockCommentable);
        when(mockCommentable.getCommentSubject()).thenReturn(titleString);
        Topic topic1 = topicServiceImpl.buildTopicFromComment(comment, id);

        verify(mockIdentifiableManager).getIdentifiableByTypeAndId(comment.getAttachType(), comment.getAttachId());
        verify(mockCommentable).getCommentSubject();

        assertEquals(topic1.getCreated(), comment.getCreated());
        assertEquals(topic1.getExcerpt(), comment.getContent());
        assertEquals(topic1.getLastUpdatorId(), comment.getCreatorId());
        assertEquals(topic1.getProjectId(), Integer.valueOf(id));
        assertEquals(topic1.getRefId(), comment.getAttachId());
        assertEquals(topic1.getRefType(), comment.getAttachType());
        assertEquals(topic1.getUpdated(), comment.getUpdated());
        assertEquals(topic1.getDeleted(), false);
        assertEquals(topic1.getTitle(), titleString);
    }

    @Test
    public void testBuildTopicFromComment2() {
        when(mockIdentifiableManager.getIdentifiableByTypeAndId(comment.getAttachType(), comment.getAttachId())).thenReturn(null);
        Topic topic1 = topicServiceImpl.buildTopicFromComment(comment, id);

        verify(mockIdentifiableManager).getIdentifiableByTypeAndId(comment.getAttachType(), comment.getAttachId());

        assertNull(topic1);
    }

    @Test
    public void testBuildTopicFromDiscussion() {
        Topic topic2 = topicServiceImpl.buildTopicFromDiscussion(discussion);
        assertEquals(topic2.getCreated(), discussion.getCreated());
        assertEquals(topic2.getExcerpt(), discussion.getContent());
        assertEquals(topic2.getLastUpdatorId(), discussion.getCreatorId());
        assertEquals(topic2.getProjectId(), discussion.getProjectId());
        assertEquals(topic2.getRefId(), discussion.getId());
        assertEquals(topic2.getRefType(), "discussion");
        assertEquals(topic2.getTitle(), discussion.getSubject());
        assertEquals(topic2.getUpdated(), discussion.getUpdated());
        assertEquals(topic2.getDeleted(), false);

    }

    /*
     * @Test public void testGetTopicListByProjectId() {
     * 
     * Identifiable mockIdentifiable = mock(Identifiable.class); when(mockIdentifiable.getCreatorId()).thenReturn(1);
     * when(mockIdentifiableManager.getIdentifiableURL(mockIdentifiable)).thenReturn("/project/topic");
     * when(mockIdentifiableManager.getIdentifiableByTypeAndId(refTypeString, id)).thenReturn(null).thenReturn(mockIdentifiable);
     * List<Topic> resultList = topicServiceImpl.getTopicListByProjectId(id, start, limit);
     * 
     * verify(mockTopicMapper).selectByExample(argThat(new ExampleMatcher<TopicExample>() {
     * 
     * @Override public boolean matches(BaseExample example) { return CriterionVerifier.verifyStart(example, start) &&
     * CriterionVerifier.verifyLimit(example, limit) && CriterionVerifier.verifyOrderByClause(example, "updated desc") &&
     * CriterionVerifier.verifyEqualTo(example, "projectId", id) && CriterionVerifier.verifyEqualTo(example, "deleted", false); }
     * }));
     * 
     * for (int i = 0; i < resultList.size(); i++) { assertEquals(resultList.get(i).getId(), topicList.get(i).getId()); } }
     */
    @Test
    public void testDeleteTopcic() {
        int test = mockTopicMapper.deleteByPrimaryKey(id);
        topicServiceImpl.deleteTopcic(id);
        verify(mockTopicMapper, times(2)).deleteByPrimaryKey(id);
        assertEquals(test, size);
    }

    @Test
    public void testDiscardTopcicByTypeAndId() {
        topicServiceImpl.discardTopcicByTypeAndId(refTypeString, id);
        verify(mockTopicMapper).updateByExampleSelective(any(Topic.class), any(TopicExample.class));
    }

    @Test
    public void testRecoverTopcicByTypeAndId() {
        topicServiceImpl.recoverTopcicByTypeAndId(refTypeString, id);
        verify(mockTopicMapper).updateByExampleSelective(any(Topic.class), any(TopicExample.class));
    }

    @Test
    public void testGetTopicByTypeAndId1() {
        when(mockTopicMapper.selectByExample(any(TopicExample.class))).thenReturn(null);
        Topic topic = topicServiceImpl.getTopicByTypeAndId(refTypeString, id);

        verify(mockTopicMapper).selectByExample(argThat(new ExampleMatcher<TopicExample>() {
            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "refId", id)
                        && CriterionVerifier.verifyEqualTo(example, "refType", refTypeString);
            }
        }));
        assertNull(topic);
    }

    @Test
    public void testGetTopicByTypeAndId2() {
        List<Topic> topics = new ArrayList<Topic>();
        when(mockTopicMapper.selectByExample(any(TopicExample.class))).thenReturn(topics);
        Topic topic = topicServiceImpl.getTopicByTypeAndId(refTypeString, id);

        verify(mockTopicMapper).selectByExample(argThat(new ExampleMatcher<TopicExample>() {
            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "refId", id)
                        && CriterionVerifier.verifyEqualTo(example, "refType", refTypeString);
            }
        }));
        assertNull(topic);
    }

    @Test
    public void testGetTopicByTypeAndId3() {
        when(mockTopicMapper.selectByExample(any(TopicExample.class))).thenReturn(topicList);
        Topic topic = topicServiceImpl.getTopicByTypeAndId(refTypeString, id);

        verify(mockTopicMapper).selectByExample(argThat(new ExampleMatcher<TopicExample>() {
            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "refId", id)
                        && CriterionVerifier.verifyEqualTo(example, "refType", refTypeString);
            }
        }));
        assertEquals(topic, topicList.get(0));

    }
    
    /**
     * @author Steven
     */
    @Test
    public void testStickTopic() {
        Topic result = topicServiceImpl.stickTopic(id);
        
        Mockito.verify(mockTopicMapper).updateByPrimaryKeySelective(Mockito.argThat(new ObjectMatcher<Topic>() {
            @Override
            public boolean verifymatches(Topic topic) {
                return topic.getId() == id && topic.getStick() == true;
            }
        }));
        Mockito.verify(mockTopicMapper).selectByPrimaryKey(id);
        Mockito.verifyNoMoreInteractions(mockTopicMapper);
        
        assertEquals(result, topic);
    }
    
    /**
     * @author Steven
     */
    @Test
    public void testUnstickTopic() {
        Topic result = topicServiceImpl.unstickTopic(id);
        
        Mockito.verify(mockTopicMapper).updateByPrimaryKeySelective(Mockito.argThat(new ObjectMatcher<Topic>() {
            @Override
            public boolean verifymatches(Topic topic) {
                return topic.getId() == id && topic.getStick() == false;
            }
        }));
        Mockito.verify(mockTopicMapper).selectByPrimaryKey(id);
        Mockito.verifyNoMoreInteractions(mockTopicMapper);
        
        assertEquals(result, topic);
    }
    
    /**
     * @author Steven
     */
    @Test
    public void testGetTopicCount() {
        int result = topicServiceImpl.getTopicCount(ModuleHelper.projectId);
        Mockito.verify(mockTopicMapper).countByExample(argThat(new ExampleMatcher<TopicExample>() {
            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "projectId", ModuleHelper.projectId)
                        && CriterionVerifier.verifyEqualTo(example, "stick", true);
            }
        }));
        Mockito.verifyNoMoreInteractions(mockTopicMapper);
        
        assertEquals(size, result);
    }
    
    /**
     * @author Steven
     */
    @Test
    public void testGetCollectedTopics() {
        List<Topic> list = topicServiceImpl.getCollectedTopics(ModuleHelper.projectId);
        Mockito.verify(mockTopicMapper).selectByExample(argThat(new ExampleMatcher<TopicExample>() {
            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "projectId", ModuleHelper.projectId)
                        && CriterionVerifier.verifyEqualTo(example, "stick", true);
            }
        }));
        Mockito.verifyNoMoreInteractions(mockTopicMapper);
        
        assertEquals(topicList, list);
    }
}
