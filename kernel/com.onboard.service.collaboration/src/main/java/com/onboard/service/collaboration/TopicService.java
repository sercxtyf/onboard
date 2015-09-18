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
package com.onboard.service.collaboration;

import java.util.List;

import com.onboard.domain.model.Comment;
import com.onboard.domain.model.Discussion;
import com.onboard.domain.model.Topic;

/**
 * {@link Topic}服务接口
 * 
 * @author yewei
 * 
 */
public interface TopicService {

    /**
     * Get item count by example
     * 
     * @param item
     * @return the count
     */
    int countByExample(Topic item);

    /**
     * 根据refId和refType，更新或者创建topic
     * 
     * @param topic
     * @return
     */
    public Topic createOrUpdateTopic(Topic topic);

    /**
     * 更新Topic
     * 
     * @param topic
     * @return
     */
    public Topic updateTopic(Topic topic);

    /**
     * 创建Topic
     * 
     * @param topic
     * @return
     */
    public Topic createTopic(Topic topic);

    /**
     * 从评论构造topic
     * 
     * @param comment
     * @return
     */
    public Topic buildTopicFromComment(Comment comment, int projectId);

    /**
     * 从讨论构造topic
     * 
     * @param discussion
     *            * @return
     */
    public Topic buildTopicFromDiscussion(Discussion discussion);

    /**
     * 获取项目的topic信息
     * 
     * @param projectId
     * @param start
     * @param limit
     * @return
     */
    public List<Topic> getTopicListByProjectId(int projectId, int start, int limit);

    /**
     * 删除Topic
     * 
     * @param id
     */
    public void deleteTopcic(int id);

    /**
     * 根据对象id查询topic
     * 
     * @param type
     * @param id
     */
    public Topic getTopicByTypeAndId(String type, int id);

    /**
     * 将Topic置于回收站
     * 
     * @param id
     * @param type
     */
    public void discardTopcicByTypeAndId(String type, int id);

    /**
     * 恢复Topic
     * 
     * @param id
     * @param type
     */
    public void recoverTopcicByTypeAndId(String type, int id);

    /**
     * 置顶Topic
     * 
     */
    public Topic stickTopic(int id);

    /**
     * 取消置顶Topic
     * 
     * @param type
     * @param id
     */
    public Topic unstickTopic(int id);

    /**
     * 获取置顶Topic数量
     * 
     * @return
     */
    public int getTopicCount(int projectId);

    /**
     * 获取收藏的topic
     * 
     * @return
     */
    public List<Topic> getCollectedTopics(int projectId);
}
