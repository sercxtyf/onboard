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

import org.elevenframework.web.interceptor.Interceptors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.onboard.domain.model.User;
import com.onboard.domain.transform.ProjectTransform;
import com.onboard.domain.transform.UserTransform;
import com.onboard.dto.ProjectDTO;
import com.onboard.dto.UserDTO;
import com.onboard.service.account.UserService;
import com.onboard.service.collaboration.ProjectService;
import com.onboard.service.security.RoleService;
import com.onboard.service.security.interceptors.CompanyAdminRequired;
import com.onboard.service.security.interceptors.ProjectAdminRequired;
import com.onboard.service.security.interceptors.ProjectChecking;
import com.onboard.service.security.interceptors.ProjectMemberRequired;
import com.onboard.service.web.SessionService;

@RequestMapping(value = "/{companyId}/projects")
@Controller
public class ProjectApiController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @RequestMapping(value = "/{projectId}", method = RequestMethod.GET)
    @Interceptors({ ProjectMemberRequired.class, ProjectChecking.class })
    @ResponseBody
    public ProjectDTO viewProject(@PathVariable("companyId") int companyId, @PathVariable("projectId") int projectId) {
        int userId = sessionService.getCurrentUser().getId();
        ProjectDTO projectDTO = ProjectTransform.projectToProjectDTO(projectService.getById(projectId));
        projectDTO.setIsCurrentUserAdmin(roleService.projectAdmin(userId, companyId, projectId));
        return projectDTO;
    }

    @RequestMapping(value = "/{projectId}/users", method = RequestMethod.GET)
    @Interceptors({ ProjectMemberRequired.class })
    @ResponseBody
    public List<UserDTO> getProjectUsers(@PathVariable("companyId") int companyId, @PathVariable("projectId") int projectId) {
        return Lists.transform(userService.getUserByProjectId(projectId), UserTransform.USER_TO_USERDTO_FUNCTION);
    }

    @RequestMapping(value = "/{projectId}/currentUser", method = RequestMethod.GET)
    @Interceptors({ ProjectMemberRequired.class })
    @ResponseBody
    public Map<String, ?> getCurrentUsers(@PathVariable("companyId") int companyId, @PathVariable("projectId") int projectId) {
        User curUser = sessionService.getCurrentUser();
        UserDTO currentUser = UserTransform.userToUserDTO(curUser);
        return ImmutableMap.of("currentUser", currentUser);

    }

    @RequestMapping(value = "/{projectId}", method = RequestMethod.DELETE)
    @Interceptors({ ProjectAdminRequired.class, ProjectChecking.class })
    @ResponseStatus(HttpStatus.OK)
    public void deleteProject(@PathVariable int projectId, @PathVariable int companyId) {
        projectService.delete(projectId);
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    @Interceptors(CompanyAdminRequired.class)
    @ResponseBody
    public List<ProjectDTO> getCompanyProjects(@PathVariable("companyId") int companyId) {
        return Lists
                .transform(projectService.getActiveProjectsByCompany(companyId, 0, -1), ProjectTransform.PROJECT_DTO_FUNCTION);
    }

}
