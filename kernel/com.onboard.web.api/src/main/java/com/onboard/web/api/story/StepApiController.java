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

import org.elevenframework.web.interceptor.Interceptors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.onboard.domain.model.IterationItemStatus;
import com.onboard.domain.model.Step;
import com.onboard.domain.transform.StepTransform;
import com.onboard.dto.StepDTO;
import com.onboard.service.account.UserService;
import com.onboard.service.collaboration.KeywordService;
import com.onboard.service.collaboration.StepService;
import com.onboard.service.collaboration.StoryService;
import com.onboard.service.security.interceptors.ProjectMemberRequired;
import com.onboard.service.web.SessionService;
import com.onboard.web.api.exception.ResourceNotFoundException;
import com.onboard.web.api.iteration.IterationApiController;

@RequestMapping(value = "/{companyId}/projects/{projectId}/steps")
@Controller
public class StepApiController {

    public static final Logger logger = LoggerFactory.getLogger(StepApiController.class);

    @Autowired
    private StepService stepService;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private UserService userService;

    @Autowired
    private StoryService storyService;

    @Autowired
    private KeywordService keywordService;

    @RequestMapping(value = "", method = RequestMethod.POST)
    @Interceptors({ ProjectMemberRequired.class })
    @ResponseBody
    @Caching(evict = { @CacheEvict(value = IterationApiController.ITERATION_CACHE_NAME, key = "#projectId + '*'"),
            @CacheEvict(value = IterationApiController.ITERATIONS_CACHE_NAME, key = "#projectId + '*'"),
            @CacheEvict(value = StoryApiController.GET_STORIES_CACHE, key = "#projectId + '*'") })
    public StepDTO newStep(@RequestBody Step step, @PathVariable int projectId) {
        step.setCreatorId(sessionService.getCurrentUser().getId());
        step.setCreatorName(sessionService.getCurrentUser().getName());
        step.setStatus(IterationItemStatus.TODO.getValue());
        step = stepService.create(step);
        if (step.getAssigneeId() != null) {
            step.setAssignee(userService.getById(step.getAssigneeId()));
        }
        return StepTransform.stepToStepDTO(step);
    }

    @RequestMapping(value = "/{stepId}", method = RequestMethod.GET)
    @Interceptors({ ProjectMemberRequired.class })
    @ResponseBody
    public StepDTO getStep(@PathVariable("stepId") int stepId) {
        StepDTO stepDTO = StepTransform.stepToStepDTO(stepService.getById(stepId));
        stepDTO.setAttachName(storyService.getById(stepDTO.getAttachId()).getDescription());
        return stepDTO;
    }

    @RequestMapping(value = "/{stepId}", method = RequestMethod.PUT)
    @Interceptors({ ProjectMemberRequired.class })
    @ResponseBody
    @Caching(evict = { @CacheEvict(value = IterationApiController.ITERATION_CACHE_NAME, key = "#projectId + '*'"),
            @CacheEvict(value = IterationApiController.ITERATIONS_CACHE_NAME, key = "#projectId + '*'"),
            @CacheEvict(value = StoryApiController.GET_STORIES_CACHE, key = "#projectId + '*'") })
    public StepDTO updateStep(@PathVariable("stepId") int stepId, @RequestBody Step step, @PathVariable int projectId) {
        step.setId(stepId);
        step = stepService.updateSelective(step);
        if (step.getAssigneeId() != null) {
            step.setAssignee(userService.getById(step.getAssigneeId()));
        }
        return StepTransform.stepToStepDTO(step);
    }

    @RequestMapping(value = "/{stepId}", method = RequestMethod.DELETE)
    @Interceptors({ ProjectMemberRequired.class })
    @ResponseBody
    @Caching(evict = { @CacheEvict(value = IterationApiController.ITERATION_CACHE_NAME, key = "#projectId + '*'"),
            @CacheEvict(value = IterationApiController.ITERATIONS_CACHE_NAME, key = "#projectId + '*'"),
            @CacheEvict(value = StoryApiController.GET_STORIES_CACHE, key = "#projectId + '*'") })
    public void deleteStory(@PathVariable("stepId") int stepId, @PathVariable int projectId) {
        stepService.deleteFromTrash(stepId);
    }

    @RequestMapping(value = "/{stepId}/keywords", method = RequestMethod.GET)
    @ResponseBody
    @Interceptors({ ProjectMemberRequired.class })
    public List<String> getStepKeywords(@PathVariable("stepId") int stepId) {
        Step step = stepService.getById(stepId);
        if (step == null) {
            throw new ResourceNotFoundException();
        }
        return keywordService.getKeywordsByText(step.generateText());
    }
}
