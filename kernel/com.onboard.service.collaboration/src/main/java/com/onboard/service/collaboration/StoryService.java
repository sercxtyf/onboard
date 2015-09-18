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

import com.onboard.domain.mapper.model.StoryExample;
import com.onboard.domain.model.Story;
import com.onboard.service.base.BaseService;

/**
 * TODO
 * @author xr
 *
 */
public interface StoryService extends BaseService<Story, StoryExample> {

    String ALL_STORY = "all-story";
    String COMPLETED_STORY = "completed-story";
    String UNCOMPLETED_STORY = "uncompleted-story";

    /**
     * 根据id获取需求，不填充需求树
     * 
     * @param storyId
     * @return
     */
    Story getStoryByIdWithoutChilds(int storyId);

    /**
     * 根据父节点id获取所有未完成的需求，并填充每个子需求的需求树
     * 
     * @param projectId
     * @param parentStoryId
     * @return
     */
    List<Story> getUnCompletedStoriesByParentId(int projectId, int parentStoryId);

    /**
     * 获取项目所有需求
     * 
     * @param projectId
     * @return
     */
    List<Story> getAllStoriesByProjectId(int projectId, int parentStoryId);

    /**
     * 根据父节点id获取所有完成的需求，并填充每个子需求的需求树
     * 
     * @param projectId
     * @param parentStoryId
     * @return
     */
    List<Story> getCompletedStoriesByParentId(int projectId, int parentStoryId);

    /**
     * 获取一个项目所有已完成需求
     * 
     * @param projectId
     * @return
     */
    List<Story> getAllCompletedStoriesByProjectId(int projectId);

    /**
     * 根据创建者id获取所有需求，不填充需求树
     * 
     * @param projectId
     * @param parentStoryId
     * @return
     */
    List<Story> getStoriesByCreatorId(int creatorId);

    /**
     * 统计一个父节点中完成子需求的数量
     * 
     * @param parentStoryId 父节点id
     * @return 统计数量
     */
    Integer getCompletedStoryCount(int parentStoryId);

    /**
     * 统计一个父节点中未完成子需求的数量
     * @param parentStoryId 父节点id
     * @return 统计数量
     */
    Integer getUncompletedStoryCount(int parentStoryId);

    /**
     * 更新需求，但是并不更新需求的父节点
     * 
     * @param story
     * @return
     */
    Story updateStoryWithoutChangingParent(Story story);

    /**
     * 将storyId对应的需求的父节点id改为targetParentId, 并避免出现环, 如果此需求未完成，目标父需求已完成则重新打开父需求
     * 
     * @param storyId
     * @param targetParentId
     * @return 是否移动成功
     */
    Boolean changeStoryParentStoryId(int storyId, int targetParentId);

    /**
     * 开启需求，并打开所有祖先需求
     * 
     * @param storyId
     */
    void updateAndOpenStory(int storyId);

    /**
     * 完成需求，并更新父需求可完成状态。
     * 
     * @param storyId
     */
    void updateAndCompleteStory(int storyId);

    /**
     * TODO: unused method
     */
    List<Story> getUnCompletedStoriesByProjectIdOrderByPosition(int projectId, int start, int limit);

    /**
     * TODO: unused method
     */
    List<Story> getCompletedStoriesByProjectIdOrderByPosition(int projectId, int start, int limit);

    /**
     * 获取一个父节点的子节点数
     * 
     * @param parentId 父节点Id
     * @return 该节点下子节点的数目
     */
    int getChildCountByParentId(int parentId);
}
