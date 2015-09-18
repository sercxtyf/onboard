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
package com.onboard.web.api.story;

import java.util.List;

import javax.validation.Valid;

import org.elevenframework.web.exception.BadRequestException;
import org.elevenframework.web.interceptor.Interceptors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.google.common.collect.Lists;
import com.onboard.domain.model.Story;
import com.onboard.domain.transform.StepTransform;
import com.onboard.domain.transform.StoryTransform;
import com.onboard.dto.StepDTO;
import com.onboard.dto.StoryDTO;
import com.onboard.service.collaboration.StoryService;
import com.onboard.service.security.exception.NoPermissionException;
import com.onboard.service.security.interceptors.ProjectMemberRequired;
import com.onboard.service.web.SessionService;
import com.onboard.web.api.form.StoryForm;
import com.onboard.web.api.iteration.IterationApiController;

@RequestMapping(value = "/{companyId}/projects/{projectId}/stories")
@Controller
public class StoryApiController {

    public final static String GET_STORIES_CACHE = "get-stories-cache";
    private final static String GET_COMPLETED_STORIES = "completed";
    private final static String GET_UNCOMPLETED_STORIES = "uncompleted";

    public static final Logger logger = LoggerFactory.getLogger(StoryApiController.class);

    @Autowired
    private StoryService storyService;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private LocalValidatorFactoryBean validator;

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.setValidator(validator);
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    @Interceptors({ ProjectMemberRequired.class })
    @ResponseBody
    @Cacheable(value = GET_STORIES_CACHE, key = "#projectId + #type + #parentStoryId")
    public List<StoryDTO> getProjectStories(@PathVariable("projectId") int projectId,
            @RequestParam(value = "type", required = false, defaultValue = "all") String type,
            @RequestParam(value = "parentStoryId", required = false, defaultValue = "0") Integer parentStoryId) {
        if (parentStoryId < 0) {
            throw new BadRequestException("parameter page shoud be positive integer.");
        }
        List<Story> stories = null;
        if (type.equals(GET_COMPLETED_STORIES)) {
            stories = storyService.getAllCompletedStoriesByProjectId(projectId);
        } else if (type.equals(GET_UNCOMPLETED_STORIES)) {
            stories = storyService.getUnCompletedStoriesByParentId(projectId, parentStoryId);
        } else {
            stories = storyService.getAllStoriesByProjectId(projectId, parentStoryId);
        }

        List<StoryDTO> storyDTOs = Lists.newArrayList(Lists.transform(stories, StoryTransform.STORY_DTO_FUNCTION));
        fillStoryDTOs(storyDTOs);
        return storyDTOs;
    }

    private void fillStoryDTOs(List<StoryDTO> storyDTOs) {
        for (StoryDTO storyDTO : storyDTOs) {
            fillStoryDTO(storyDTO);
        }
    }

    private void fillStoryDTO(StoryDTO storyDTO) {
        storyDTO.setCompletedChildStoryCount(storyService.getCompletedStoryCount(storyDTO.getId()));
        storyDTO.setUncompletedChildStoryCount(storyService.getUncompletedStoryCount(storyDTO.getId()));
        if (storyDTO.getChildStoryDTOs() != null) {
            fillStoryDTOs(storyDTO.getChildStoryDTOs());
        }
    }

    @RequestMapping(value = "/{storyId}", method = RequestMethod.GET)
    @Interceptors({ ProjectMemberRequired.class })
    @ResponseBody
    public StoryDTO getStoryById(@PathVariable("projectId") int projectId, @PathVariable("storyId") int storyId) {
        StoryDTO storyDTO = StoryTransform.storyToStoryDTO(storyService.getById(storyId));
        fillStoryDTO(storyDTO);
        return storyDTO;
    }

    @RequestMapping(value = "/{storyId}/steps", method = RequestMethod.GET)
    @Interceptors({ ProjectMemberRequired.class })
    @ResponseBody
    public List<StepDTO> getStpesByStoryIdId(@PathVariable("projectId") int projectId,
            @PathVariable("storyId") int storyId) {
        return Lists.newArrayList(Lists.transform(storyService.getById(storyId).getSteps(),
                StepTransform.STEP_DTO_FUNCTION));
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    @Interceptors({ ProjectMemberRequired.class })
    @ResponseBody
    @Caching(evict = { @CacheEvict(value = GET_STORIES_CACHE, key = "#projectId + '*'") })
    public StoryDTO newStory(@PathVariable("companyId") int companyId, @PathVariable("projectId") int projectId,
            @Valid @RequestBody StoryForm storyForm) {
        storyForm.setProjectId(projectId);
        storyForm.setCompanyId(companyId);
        storyForm.setCreatorId(sessionService.getCurrentUser().getId());
        storyForm.setCreatorName(sessionService.getCurrentUser().getName());
        Story story = storyService.create(storyForm);

        StoryDTO storyDTO = StoryTransform.storyToStoryDTO(story);
        fillStoryDTO(storyDTO);
        return storyDTO;
    }

    @RequestMapping(value = "/{storyId}", method = RequestMethod.PUT)
    @Interceptors({ ProjectMemberRequired.class })
    @ResponseBody
    @Caching(evict = { @CacheEvict(value = GET_STORIES_CACHE, key = "#projectId + '*'"),
            @CacheEvict(value = IterationApiController.ITERATION_CACHE_NAME, key = "#projectId + '*'"),
            @CacheEvict(value = IterationApiController.ITERATIONS_CACHE_NAME, key = "#projectId + '*'") })
    public StoryDTO updateStory(@PathVariable("companyId") int companyId, @PathVariable("projectId") int projectId,
            @PathVariable("storyId") int storyId, @Valid @RequestBody StoryForm form) {
        Story story = storyService.getStoryByIdWithoutChilds(storyId);
        form.setId(storyId);
        if (form.getParentStoryId() != null && form.getParentStoryId() != story.getParentStoryId()) {
            boolean succeed = storyService.changeStoryParentStoryId(storyId, form.getParentStoryId());
            if (!succeed) {
                throw new BadRequestException();
            }
        }
        storyService.updateStoryWithoutChangingParent(form);
        Story updatedStory = storyService.getById(story.getId());
        StoryDTO storyDTO = StoryTransform.storyToStoryDTO(updatedStory);
        fillStoryDTO(storyDTO);
        return storyDTO;
    }

    @RequestMapping(value = "/{storyId}/complete", method = RequestMethod.PUT)
    @Interceptors({ ProjectMemberRequired.class })
    @ResponseStatus(HttpStatus.OK)
    @Caching(evict = { @CacheEvict(value = GET_STORIES_CACHE, key = "#projectId + '*'"),
            @CacheEvict(value = IterationApiController.ITERATION_CACHE_NAME, key = "#projectId + '*'"),
            @CacheEvict(value = IterationApiController.ITERATIONS_CACHE_NAME, key = "#projectId + '*'") })
    public void completeStory(@PathVariable("companyId") int companyId, @PathVariable("projectId") int projectId,
            @PathVariable("storyId") int storyId) {
        Story story = storyService.getById(storyId);
        if (!story.getCompletable()) {
            throw new NoPermissionException("user cannot complete an uncompletable story!");
        }
        Story newStory = new Story();
        newStory.setId(storyId);
        newStory.setCompleted(true);
        storyService.updateStoryWithoutChangingParent(newStory);
    }

    @RequestMapping(value = "/{storyId}/reopen", method = RequestMethod.PUT)
    @Interceptors({ ProjectMemberRequired.class })
    @ResponseStatus(HttpStatus.OK)
    @Caching(evict = { @CacheEvict(value = GET_STORIES_CACHE, key = "#projectId + '*'"),
            @CacheEvict(value = IterationApiController.ITERATION_CACHE_NAME, key = "#projectId + '*'"),
            @CacheEvict(value = IterationApiController.ITERATIONS_CACHE_NAME, key = "#projectId + '*'") })
    public void reopenStory(@PathVariable("companyId") int companyId, @PathVariable("projectId") int projectId,
            @PathVariable("storyId") int storyId) {
        Story story = new Story();
        story.setId(storyId);
        story.setCompleted(false);
        storyService.updateSelective(story);
    }

    @RequestMapping(value = "/{storyId}", method = RequestMethod.DELETE)
    @Interceptors({ ProjectMemberRequired.class })
    @ResponseStatus(HttpStatus.OK)
    @Caching(evict = { @CacheEvict(value = GET_STORIES_CACHE, key = "#projectId + '*'"),
            @CacheEvict(value = IterationApiController.ITERATION_CACHE_NAME, key = "#projectId + '*'"),
            @CacheEvict(value = IterationApiController.ITERATIONS_CACHE_NAME, key = "#projectId + '*'") })
    public void deleteStory(@PathVariable("storyId") int storyId, @PathVariable("projectId") int projectId) {
        if (storyService.getChildCountByParentId(storyId) != 0) {
            throw new BadRequestException();
        }
        storyService.delete(storyId);
    }

}
