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

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.elevenframework.web.interceptor.Interceptors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.onboard.service.collaboration.ProjectService;
import com.onboard.service.security.interceptors.ProjectChecking;
import com.onboard.service.security.interceptors.ProjectMemberRequired;

@RequestMapping(value = "/{companyId}/projects/{projectId}/todostatus")
@Controller
public class ProjectTodoStatusApiController {

    @Autowired
    private ProjectService projectService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    @Interceptors({ ProjectMemberRequired.class, ProjectChecking.class })
    @ResponseBody
    public List<String> getTodoStatusByProject(@PathVariable("projectId") int projectId) {
        return projectService.getTodoStatusByProjectId(projectId);
    }

    @RequestMapping(value = "/{status}", method = RequestMethod.POST)
    @Interceptors({ ProjectMemberRequired.class, ProjectChecking.class })
    @ResponseStatus(HttpStatus.OK)
    public void addTodoStatusByProject(@PathVariable("projectId") int projectId, @PathVariable("status") String status,
            HttpServletResponse response) throws IOException {
        if (!projectService.addTodoStatusToProject(projectId, status)) {
            // fail to add, it means it is aleady exist
            response.sendError(HttpServletResponse.SC_CONFLICT);
            return;
        }
    }

    @RequestMapping(value = "/{status}", method = RequestMethod.DELETE)
    @Interceptors({ ProjectMemberRequired.class, ProjectChecking.class })
    @ResponseStatus(HttpStatus.OK)
    public void removeTodoStatusByProject(@PathVariable("projectId") int projectId, @PathVariable("status") String status,
            HttpServletResponse response) throws IOException {
        if (!projectService.removeTodoStatusFromProject(projectId, status)) {
            // fail to remove, it means the status is default status
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
}
