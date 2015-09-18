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
package com.onboard.web.api.project;

import org.elevenframework.web.interceptor.Interceptors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.onboard.domain.model.type.ProjectItem;
import com.onboard.service.collaboration.IdInProjectService;
import com.onboard.service.security.interceptors.ProjectChecking;
import com.onboard.service.security.interceptors.ProjectMemberRequired;

@RequestMapping(value = "/{companyId}/projects/{projectId}/projectItem")
@Controller
public class ProjectItemApiController {

    @Autowired
    private IdInProjectService idInProjectService;

    @RequestMapping(value = "/{projectItemId}", method = RequestMethod.GET)
    @Interceptors({ ProjectMemberRequired.class, ProjectChecking.class })
    @ResponseBody
    public ProjectItem getProjectItemByProject(@PathVariable("projectId") int projectId,
            @PathVariable("projectItemId") int projectItemId) {
        return idInProjectService.get(projectId, projectItemId);
    }
}
