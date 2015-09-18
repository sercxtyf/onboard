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

import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.elevenframework.web.interceptor.Interceptors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.onboard.service.collaboration.ProjectService;
import com.onboard.service.security.interceptors.CompanyMemberRequired;
import com.onboard.service.security.interceptors.ProjectAdminRequired;
import com.onboard.service.security.interceptors.ProjectChecking;
import com.onboard.service.security.interceptors.ProjectCreationPrivilegeRequired;
import com.onboard.web.api.form.UpdateProjectForm;

/**
 * Project相关的Controller
 * 
 * @author ruici, yewei
 */
@RequestMapping(value = "/{companyId}/projects")
@Controller
public class ProjectEditController {

    public static final Logger logger = LoggerFactory.getLogger(ProjectEditController.class);

    @Autowired
    private ProjectService projectService;

    @Interceptors({ ProjectCreationPrivilegeRequired.class, ProjectChecking.class, ProjectAdminRequired.class })
    @RequestMapping(value = "/{projectId}", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<Object> updateProject(@PathVariable int companyId, @PathVariable int projectId,
            @Valid @RequestBody UpdateProjectForm form) {
        form.setCompanyId(companyId);
        form.setId(projectId);
        projectService.updateProject(form);

        return new ResponseEntity<Object>(HttpStatus.OK);
    }

    /**
     * 对项目进行拖动排序
     * 
     * @param sortedIDs
     * @param model
     */
    @RequestMapping(value = "/sort", method = RequestMethod.POST)
    @Interceptors({ CompanyMemberRequired.class })
    public ResponseEntity<Object> sortProject(@RequestBody Map<String, List<Integer>> ids) {
        projectService.sortProject(ids.get("ids"));
        return new ResponseEntity<Object>(HttpStatus.OK);
    }

}
