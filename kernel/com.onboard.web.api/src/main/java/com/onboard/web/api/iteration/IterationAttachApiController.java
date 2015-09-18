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
package com.onboard.web.api.iteration;

import org.elevenframework.web.interceptor.Interceptors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.onboard.domain.model.IterationAttach;
import com.onboard.domain.model.Step;
import com.onboard.domain.model.Story;
import com.onboard.service.collaboration.IterationService;
import com.onboard.service.collaboration.StoryService;
import com.onboard.service.security.interceptors.ProjectMemberRequired;
import com.onboard.service.security.interceptors.ProjectNotArchivedRequired;

@RequestMapping(value = "/{companyId}/projects/{projectId}/iterationattachs")
@Controller
public class IterationAttachApiController {

    public static final Logger LOGGER = LoggerFactory.getLogger(IterationAttachApiController.class);
    @Autowired
    private IterationService iterationService;

    @Autowired
    private StoryService storyService;

    @RequestMapping(value = "", method = RequestMethod.POST)
    @Interceptors({ ProjectMemberRequired.class, ProjectNotArchivedRequired.class })
    @ResponseBody
    @Caching(evict = {
            @CacheEvict(value = IterationApiController.ITERATION_CACHE_NAME, key = "#projectId + #iterationAttach.iterationId + '*'"),
            @CacheEvict(value = IterationApiController.ITERATIONS_CACHE_NAME, key = "#projectId + '*'") })
    public void createIterationAttach(@RequestBody IterationAttach iterationAttach, @PathVariable int projectId) {
        iterationService.addIterable(iterationAttach);
        if (iterationAttach.getObjectType().equals(new Story().getType())) {
            Story story = storyService.getById(iterationAttach.getObjectId());
            for (Step step : story.getSteps()) {
                if (!step.getCompleted()) {
                    iterationService.addIterable(step);
                }
            }
        }
    }

    @RequestMapping(value = "", method = RequestMethod.DELETE)
    @Interceptors({ ProjectMemberRequired.class, ProjectNotArchivedRequired.class })
    @ResponseStatus(HttpStatus.OK)
    @Caching(evict = { @CacheEvict(value = IterationApiController.ITERATION_CACHE_NAME, key = "#projectId + '*'"),
            @CacheEvict(value = IterationApiController.ITERATIONS_CACHE_NAME, key = "#projectId + '*'") })
    public void deleteIterationById(@PathVariable int projectId,
            @RequestParam(value = "iterationId", required = true) Integer iterationId,
            @RequestParam(value = "attachType", required = true) String attachType,
            @RequestParam(value = "attachId", required = true) Integer attachId) {
        IterationAttach iterationAttach = new IterationAttach();
        iterationAttach.setIterationId(iterationId);
        iterationAttach.setObjectId(attachId);
        iterationAttach.setObjectType(attachType);
        if (iterationAttach.getObjectType().equals(new Story().getType())) {
            Story story = storyService.getById(iterationAttach.getObjectId());
            for (Step step : story.getSteps()) {
                if (!step.getCompleted()) {
                    iterationService.removeIterable(step, iterationId);
                }
            }
        }
        iterationService.removeIterable(iterationAttach);
    }

}
