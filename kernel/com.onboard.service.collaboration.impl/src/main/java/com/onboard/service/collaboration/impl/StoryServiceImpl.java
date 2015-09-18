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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.onboard.domain.mapper.StoryMapper;
import com.onboard.domain.mapper.base.BaseMapper;
import com.onboard.domain.mapper.model.StoryExample;
import com.onboard.domain.model.IterationItemStatus;
import com.onboard.domain.model.Step;
import com.onboard.domain.model.Story;
import com.onboard.service.base.AbstractBaseService;
import com.onboard.service.collaboration.IterableService;
import com.onboard.service.collaboration.StepService;
import com.onboard.service.collaboration.StoryService;
import com.onboard.service.web.SessionService;

/**
 * {@link StoryService}接口实现
 * 
 * @author xingliang
 * 
 */
@Transactional
@Service("storyServiceBean")
public class StoryServiceImpl extends AbstractBaseService<Story, StoryExample> implements StoryService, IterableService<Story> {

    public final static Logger LOGGER = LoggerFactory.getLogger(StoryServiceImpl.class);

    private final static int ROOT_ID = 0;

    @Autowired
    private StoryMapper storyMapper;

    @Autowired
    private StepService stepService;

    @Autowired
    private SessionService sessionService;

    @Override
    public Story getById(int storyId) {
        Story story = storyMapper.selectByPrimaryKey(storyId);
        if (story != null) {
            checkCompletable(story);
            fillStorySteps(story);
            Map<Integer, List<Story>> parentStoryIdStoriesMap = getAllProjectStoriesMapByParentId(story.getProjectId(), null);

            fillChildStory(story, null, new HashSet<Integer>(), parentStoryIdStoriesMap);
            return story;
        }
        return null;
    }

    private void checkCompletable(Story story) {
        if (ROOT_ID == story.getParentStoryId()) {
            return;
        }
        Story storyExample = new Story(false);
        storyExample.setParentStoryId(story.getId());
        List<Story> childStories = storyMapper.selectByExample(new StoryExample(storyExample));
        Boolean excepted = true;
        for (Story childStory : childStories) {
            if (!childStory.getCompleted()) {
                excepted = false;
            }
        }
        if (excepted != story.getCompletable()) {
            story.setCompletable(excepted);
            storyMapper.updateByPrimaryKeySelective(story);
        }
    }

    private Map<Integer, List<Story>> getAllProjectStoriesMapByParentId(int projectId, Boolean completed) {
        Story storyExample = new Story(false);
        storyExample.setProjectId(projectId);
        if (completed != null) {
            storyExample.setCompleted(completed);
        }
        List<Story> stories = storyMapper.selectByExample(new StoryExample(storyExample));
        Map<Integer, List<Story>> result = Maps.newHashMap();
        for (Story story : stories) {
            fillStorySteps(story);
            if (result.containsKey(story.getParentStoryId())) {
                List<Story> childStories = Lists.newArrayList(result.get(story.getParentStoryId()));
                childStories.add(story);
                result.put(story.getParentStoryId(), childStories);
            } else {
                result.put(story.getParentStoryId(), Lists.newArrayList(story));

            }
        }
        return result;
    }

    private void fillStorySteps(Story story) {
        Step allStepExample = new Step();
        allStepExample.setDeleted(false);
        allStepExample.setAttachType(new Story().getType());
        allStepExample.setAttachId(story.getId());
        story.setTodoCount(stepService.countBySample(allStepExample));
        Step finishedStepExample = new Step(allStepExample);
        finishedStepExample.setStatus(IterationItemStatus.CLOSED.getValue());
        story.setFinishedTodoCount(stepService.countBySample(finishedStepExample));
        story.setSteps(stepService.getBySample(allStepExample, 0, -1));
    }

    @Override
    public List<Story> getStoriesByCreatorId(int creatorId) {
        Story story = new Story(false);
        story.setCreatorId(creatorId);
        List<Story> stories = storyMapper.selectByExample(new StoryExample(story));
        fillStories(stories, null, story.getProjectId());
        return stories;
    }

    @Override
    public Story create(Story story) {
        story.setCreated(new Date());
        story.setUpdated(new Date());
        story.setDeleted(false);
        story.setCompleted(false);
        story.setCompletable(true);
        story.setCreatorAvatar(sessionService.getCurrentUser().getAvatar());
        story.setCreatorId(sessionService.getCurrentUser().getId());
        story.setCreatorName(sessionService.getCurrentUser().getName());

        storyMapper.insert(story);
        if (null != story.getParentStoryId() && ROOT_ID != story.getParentStoryId()) {
            updateAndOpenStory(story.getParentStoryId());
            Story parentStory = storyMapper.selectByPrimaryKey(story.getParentStoryId());
            parentStory.setCompletable(false);
            storyMapper.updateByPrimaryKeySelective(parentStory);
        }
        fillStorySteps(story);
        return story;
    }

    @Override
    public Story updateStoryWithoutChangingParent(Story story) {
        Story originStory = storyMapper.selectByPrimaryKey(story.getId());
        story.setParentStoryId(originStory.getParentStoryId());
        if (originStory.getCompleted() && !story.getCompleted()) {
            updateAndOpenStory(originStory.getId());
        } else if (!originStory.getCompleted() && story.getCompleted()) {
            updateAndCompleteStory(originStory.getId());
        } else {
            storyMapper.updateByPrimaryKeySelective(story);
        }
        Story newStory = storyMapper.selectByPrimaryKey(originStory.getId());

        fillStorySteps(newStory);
        return newStory;
    }

    private void deleteByStory(Story story) {
        story.setDeleted(true);
        storyMapper.updateByPrimaryKeySelective(story);
        for (Story childStory : story.getChildStories()) {
            deleteByStory(childStory);
        }
    }

    @Override
    public void delete(int storyId) {
        Story story = storyMapper.selectByPrimaryKey(storyId);
        deleteByStory(story);
        if (ROOT_ID != story.getParentStoryId()) {
            Story parentStory = getById(story.getParentStoryId());
            if (isStoryCompletable(parentStory)) {
                parentStory.setCompletable(true);
                storyMapper.updateByPrimaryKeySelective(parentStory);
            }
        }
    }

    @Override
    public List<Story> getAllStoriesByProjectId(int projectId, int parentStoryId) {
        Story storyExample = new Story(false);
        storyExample.setProjectId(projectId);
        storyExample.setParentStoryId(parentStoryId);
        List<Story> stories = storyMapper.selectByExample(new StoryExample(storyExample));
        fillStories(stories, null, projectId);
        fillStoriesStep(stories);
        return stories;
    }

    @Override
    public List<Story> getUnCompletedStoriesByParentId(int projectId, int parentStoryId) {
        Story storyExample = new Story(false);
        storyExample.setProjectId(projectId);
        storyExample.setParentStoryId(parentStoryId);
        storyExample.setCompleted(false);
        storyExample.setDeleted(false);
        List<Story> stories = storyMapper.selectByExample(new StoryExample(storyExample));
        fillStories(stories, false, projectId);
        return stories;
    }

    @Override
    public List<Story> getCompletedStoriesByParentId(int projectId, int parentStoryId) {
        Story storyExample = new Story(false);
        storyExample.setProjectId(projectId);
        storyExample.setParentStoryId(parentStoryId);
        storyExample.setCompleted(true);
        List<Story> stories = storyMapper.selectByExample(new StoryExample(storyExample));
        fillStories(stories, true, projectId);
        return stories;
    }

    private void fillStories(List<Story> stories, Boolean completed, int projectId) {
        Map<Integer, List<Story>> parentStoryIdStoriesMap = getAllProjectStoriesMapByParentId(projectId, completed);
        for (Story story : stories) {
            fillChildStory(story, completed, new HashSet<Integer>(), parentStoryIdStoriesMap);
        }
    }

    private void fillChildStory(Story story, Boolean completed, Set<Integer> storyIds,
            Map<Integer, List<Story>> allProjectStoriesMap) {
        storyIds.add(story.getId());
        List<Story> childStories = allProjectStoriesMap.get(story.getId());
        if (childStories == null) {
            childStories = Lists.newArrayList();
        }
        removeConflictChild(storyIds, childStories);
        story.setChildStories(childStories);
        for (Story childStory : childStories) {
            fillChildStory(childStory, completed, storyIds, allProjectStoriesMap);
        }
    }

    private void removeConflictChild(Set<Integer> storyIds, List<Story> stories) {
        Iterator<Story> iterator = stories.iterator();
        while (iterator.hasNext()) {
            int storyId = iterator.next().getId();
            if (storyIds.contains(storyId)) {
                iterator.remove();
            } else {
                storyIds.add(storyId);
            }
        }
    }

    @Override
    public void updateAndOpenStory(int storyId) {
        Story story = storyMapper.selectByPrimaryKey(storyId);
        story.setCompleted(false);
        story.setCompletable(true);
        updateStoryWithoutChangingParent(story);
        int parentStoryId = story.getParentStoryId();
        while (ROOT_ID != parentStoryId) {
            Story parentStory = storyMapper.selectByPrimaryKey(parentStoryId);
            parentStory.setCompleted(false);
            parentStory.setCompletable(false);
            storyMapper.updateByPrimaryKeySelective(parentStory);
            parentStoryId = parentStory.getParentStoryId();
        }

    }

    @Override
    public void updateAndCompleteStory(int storyId) {
        Story story = storyMapper.selectByPrimaryKey(storyId);
        story.setCompleted(true);
        story.setCompletedTime(new Date());
        updateStoryWithoutChangingParent(story);
        int parentId = story.getParentStoryId();
        if (ROOT_ID != parentId) {
            Story parentStory = storyMapper.selectByPrimaryKey(parentId);
            Boolean excepted = isStoryCompletable(parentStory);
            if (excepted != parentStory.getCompletable()) {
                parentStory.setCompletable(excepted);
                storyMapper.updateByPrimaryKeySelective(parentStory);
            }
        }
    }

    private boolean isStoryCompletable(Story story) {
        List<Story> childStories = story.getChildStories();
        for (Story childStory : childStories) {
            if (!childStory.getCompleted()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Boolean changeStoryParentStoryId(int storyId, int targetParentId) {
        Story story = storyMapper.selectByPrimaryKey(storyId);
        if (ifConflict(story, targetParentId)) {
            return false;
        }
        story.setParentStoryId(storyId);
        storyMapper.updateByPrimaryKeySelective(story);
        if (!story.getCompleted()) {
            Story parentStory = storyMapper.selectByPrimaryKey(story.getParentStoryId());
            if (parentStory.getCompleted()) {
                updateAndOpenStory(parentStory.getId());
            }
        }
        return true;
    }

    private boolean ifConflict(Story story, int targetParentId) {
        List<Story> childStories = story.getChildStories();
        for (Story childStory : childStories) {
            if (childStory.getId() == targetParentId) {
                return true;
            } else if (ifConflict(childStory, targetParentId)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Story getStoryByIdWithoutChilds(int storyId) {
        return storyMapper.selectByPrimaryKey(storyId);
    }

    @Override
    public Story getIterableWithBoardables(Integer id) {
        Story story = getById(id);
        story.setCompletedChildStoryCount(getCompletedStoryCount(id));
        story.setUncompletedChildStoryCount(getUncompletedStoryCount(id));
        return story;
    }

    @Override
    public List<Story> getUnCompletedStoriesByProjectIdOrderByPosition(int projectId, int start, int limit) {
        Story story = new Story();
        story.setProjectId(projectId);
        story.setCompleted(false);
        story.setDeleted(false);
        StoryExample storyExample = new StoryExample(story);
        storyExample.setOrderByClause("position");
        storyExample.setLimit(start, limit);
        List<Story> stories = storyMapper.selectByExample(storyExample);
        fillStoriesStep(stories);
        return stories;
    }

    private void fillStoriesStep(List<Story> stories) {
        for (Story story : stories) {
            fillStorySteps(story);
        }
    }

    @Override
    public List<Story> getCompletedStoriesByProjectIdOrderByPosition(int projectId, int start, int limit) {
        Story story = new Story();
        story.setProjectId(projectId);
        story.setCompleted(true);
        story.setDeleted(false);
        StoryExample storyExample = new StoryExample(story);
        storyExample.setOrderByClause("position");
        storyExample.setLimit(start, limit);
        List<Story> stories = storyMapper.selectByExample(storyExample);
        fillStoriesStep(stories);
        return stories;
    }

    @Override
    public Story getIterable(Integer id) {
        return getById(id);
    }

    @Override
    public Integer getCompletedStoryCount(int parentStoryId) {
        Story story = new Story(false);
        story.setParentStoryId(parentStoryId);
        story.setCompleted(true);
        return storyMapper.countByExample(new StoryExample(story));
    }

    @Override
    public Integer getUncompletedStoryCount(int parentStoryId) {
        Story story = new Story(false);
        story.setParentStoryId(parentStoryId);
        story.setCompleted(false);
        return storyMapper.countByExample(new StoryExample(story));
    }

    @Override
    public Story getByIdWithDetail(int id) {
        return getIterableWithBoardables(id);
    }

    @Override
    public List<Story> getAllCompletedStoriesByProjectId(int projectId) {
        Story storyExample = new Story(false);
        storyExample.setProjectId(projectId);
        storyExample.setCompleted(true);
        List<Story> stories = storyMapper.selectByExample(new StoryExample(storyExample));
        fillStories(stories, true, projectId);
        fillStoriesStep(stories);
        return stories;
    }

    @Override
    public int getChildCountByParentId(int parentId) {
        Story storyExample = new Story(false);
        storyExample.setParentStoryId(parentId);
        return storyMapper.countByExample(new StoryExample(storyExample));
    }

    @Override
    protected BaseMapper<Story, StoryExample> getBaseMapper() {
        return storyMapper;
    }

    @Override
    public Story newItem() {
        return new Story();
    }

    @Override
    public StoryExample newExample() {
        return new StoryExample();
    }

    @Override
    public StoryExample newExample(Story item) {
        return new StoryExample(item);
    }

}
