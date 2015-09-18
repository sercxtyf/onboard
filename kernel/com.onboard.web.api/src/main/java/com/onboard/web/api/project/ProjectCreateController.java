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

import com.onboard.domain.model.Company;
import com.onboard.domain.model.Project;
import com.onboard.domain.transform.ProjectTransform;
import com.onboard.dto.ProjectDTO;
import com.onboard.service.account.CompanyService;
import com.onboard.service.collaboration.ProjectService;
import com.onboard.service.security.interceptors.ProjectCreationPrivilegeRequired;
import com.onboard.service.web.SessionService;
import com.onboard.web.api.form.UpdateProjectForm;

/**
 * Project相关的Controller
 * 
 * @author ruici, yewei
 */
@RequestMapping(value = "/{companyId}")
@Controller
public class ProjectCreateController {

    public static final Logger logger = LoggerFactory.getLogger(ProjectCreateController.class);

    @Autowired
    private ProjectService projectService;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private SessionService session;

    /**
     * 创建Project
     */
    @Interceptors({ ProjectCreationPrivilegeRequired.class })
    @RequestMapping(value = "/projects", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> newProject(@PathVariable int companyId, @Valid @RequestBody UpdateProjectForm form) {
        form.setCompanyId(companyId);
        form.setCreatorId(session.getCurrentUser().getId());
        Project project = projectService.createProject(form);

        return new ResponseEntity<ProjectDTO>(ProjectTransform.projectToProjectDTO(project), HttpStatus.CREATED);
    }

    /**
     * 获取一个company下活动的项目数
     */
    @Interceptors({ ProjectCreationPrivilegeRequired.class })
    @RequestMapping(value = "/projects-count", method = RequestMethod.GET)
    @ResponseBody
    public int getProjectCountByCompanyId(@PathVariable("companyId") int companyId) {
        Company company = companyService.getById(companyId);
        if (company.getPrivileged()) {
            return 0;
        }
        int count = projectService.getActiveProjectsByCompany(companyId, 0, -1).size();

        return count;
    }

}
