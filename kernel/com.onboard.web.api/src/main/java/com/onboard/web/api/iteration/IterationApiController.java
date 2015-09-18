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

import java.util.List;

import org.elevenframework.web.interceptor.Interceptors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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

import com.google.common.collect.Lists;
import com.onboard.domain.model.Iteration;
import com.onboard.domain.model.Iteration.IterationStatus;
import com.onboard.domain.transform.IterationTransform;
import com.onboard.dto.IterationDTO;
import com.onboard.service.collaboration.IterationService;
import com.onboard.service.security.interceptors.ProjectMemberRequired;
import com.onboard.service.security.interceptors.ProjectNotArchivedRequired;

@RequestMapping(value = "/{companyId}/projects/{projectId}/iterations")
@Controller
public class IterationApiController {

    public final static String ITERATIONS_CACHE_NAME = "iteration-cache";

    public final static String ITERATION_CACHE_NAME = "iterations-cache";

    @Autowired
    private IterationService iterationService;

    @RequestMapping(value = "", method = RequestMethod.POST)
    @Interceptors({ ProjectMemberRequired.class, ProjectNotArchivedRequired.class })
    @ResponseBody
    @Caching(evict = { @CacheEvict(value = ITERATION_CACHE_NAME, key = "#projectId + '*'"),
            @CacheEvict(value = ITERATIONS_CACHE_NAME, key = "#projectId + '*'") })
    public IterationDTO createTodo(@PathVariable("projectId") int projectId, @RequestBody Iteration iteration) {
        return IterationTransform.iterationToIterationDTO(iterationService.create(iteration));
    }

    @RequestMapping(value = "/{iterationId}", method = RequestMethod.GET)
    @Interceptors({ ProjectMemberRequired.class })
    @ResponseBody
    public IterationDTO getIterationById(@PathVariable("iterationId") Integer iterationId,
            @PathVariable("projectId") Integer projectId) {
        return IterationTransform.iterationToIterationDTO(iterationService.getById(iterationId));
    }

    @RequestMapping(value = "/{iterationId}", method = RequestMethod.PUT)
    @Interceptors({ ProjectMemberRequired.class, ProjectNotArchivedRequired.class })
    @ResponseStatus(HttpStatus.OK)
    @Caching(evict = { @CacheEvict(value = ITERATION_CACHE_NAME, key = "#projectId + #iterationId + '*'"),
            @CacheEvict(value = ITERATIONS_CACHE_NAME, key = "#projectId + '*'") })
    public void updateIteration(@PathVariable("companyId") int companyId, @PathVariable("projectId") int projectId,
            @PathVariable("iterationId") Integer iterationId, @RequestBody Iteration iteration) {
        iteration.setId(iterationId);
        iteration.setCompanyId(companyId);
        iteration.setProjectId(projectId);
        iterationService.updateSelective(iteration);
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    @Cacheable(value = ITERATIONS_CACHE_NAME, key = "#projectId + #status + #start + #limit")
    @Interceptors({ ProjectMemberRequired.class })
    @ResponseBody
    public List<IterationDTO> getIterations(@PathVariable("projectId") Integer projectId,
            @RequestParam(value = "status", required = false, defaultValue = "active") String status,
            @RequestParam(value = "start", required = false, defaultValue = "0") Integer start,
            @RequestParam(value = "limit", required = false, defaultValue = "5") Integer limit) {
        if (status.equals(IterationStatus.COMPLETED.getValue())) {
            List<Iteration> iterations = iterationService.getCompleteIterationsByProjectId(projectId, start, limit);
            return Lists.newArrayList(Lists.transform(iterations, IterationTransform.ITERATION_DTO_FUNCTION));
        } else if (status.equals(IterationStatus.ACTIVE.getValue())) {
            Iteration activeIteration = iterationService.getCurrentIterationByProjectId(projectId);
            if (activeIteration == null) {
                return Lists.newArrayList();
            }
            IterationDTO iterationDTO = IterationTransform.iterationToIterationDTO(activeIteration);
            return Lists.newArrayList(iterationDTO);
        }
        return Lists.newArrayList();
    }
}
