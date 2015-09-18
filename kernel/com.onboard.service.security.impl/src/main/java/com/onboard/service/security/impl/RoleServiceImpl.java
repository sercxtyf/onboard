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
package com.onboard.service.security.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.onboard.domain.model.Company;
import com.onboard.domain.model.CompanyPrivilege;
import com.onboard.domain.model.Project;
import com.onboard.domain.model.ProjectPrivilege;
import com.onboard.domain.model.User;
import com.onboard.service.account.CompanyService;
import com.onboard.service.account.UserService;
import com.onboard.service.collaboration.ProjectService;
import com.onboard.service.security.CompanyPrivilegeService;
import com.onboard.service.security.ProjectPrivilegeService;
import com.onboard.service.security.RoleService;

@Service("roleServiceBean")
public class RoleServiceImpl implements RoleService {

    @Autowired
    private CompanyService companyService;

    @Autowired
    private UserService userService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private CompanyPrivilegeService companyPrivilegeService;

    @Autowired
    private ProjectPrivilegeService projectPrivilegeService;

    @Override
    public boolean companyOwner(int userId, int companyId) {
        Company company = companyService.getById(companyId);
        return company != null && company.getCreatorId() == userId;
    }

    @Override
    public boolean companyAdmin(int userId, int companyId) {
        CompanyPrivilege p = companyPrivilegeService.getOrCreateCompanyPrivilegeByUserId(companyId, userId);
        return p.getIsAdmin() || companyOwner(userId, companyId);
    }

    @Override
    public boolean companyAdminInSpecificProject(int userId, int companyId, int projectId) {
        return userService.isUserInProject(userId, companyId, projectId) && companyAdmin(userId, companyId);
    }

    @Override
    public boolean companyMemberCanCreateProject(int userId, int companyId) {
        CompanyPrivilege p = companyPrivilegeService.getOrCreateCompanyPrivilegeByUserId(companyId, userId);
        return p.getCanCreateProject() || companyAdmin(userId, companyId);
    }

    @Override
    public boolean projectAdmin(int userId, int companyId, int projectId) {
        ProjectPrivilege p = projectPrivilegeService.getOrCreateProjectPrivilegeByUserId(projectId, userId);
        return p.getIsAdmin() || projectCreator(userId, projectId)
                || companyAdminInSpecificProject(userId, companyId, projectId);
    }

    @Override
    public boolean projectMember(int userId, int companyId, int projectId) {
        return userService.isUserInProject(userId, companyId, projectId);
    }

    @Override
    public boolean projectCreator(int userId, int projectId) {
        Project project = projectService.getById(projectId);
        return project != null && project.getCreatorId() == userId;
    }

    @Override
    public boolean companyMember(int userId, int companyId) {
        return companyService.containsUser(companyId, userId);
    }

    @Override
    public User getCompanyOwnerByCompanyId(int companyId) {
        Company company = companyService.getById(companyId);
        return userService.getById(company.getCreatorId());
    }

    @Override
    public List<User> getCompanyAdminsByCompanyId(int companyId) {
        Set<User> users = new HashSet<User>();
        CompanyPrivilege companyPrivilege = new CompanyPrivilege();
        companyPrivilege.setCompanyId(companyId);
        companyPrivilege.setIsAdmin(true);
        List<CompanyPrivilege> companyPrivileges = companyPrivilegeService.getCompanyPrivilegesByExample(
                companyPrivilege, 0, -1);
        for (CompanyPrivilege cp : companyPrivileges) {
            users.add(userService.getById(cp.getUserId()));
        }
        users.add(getCompanyOwnerByCompanyId(companyId));
        return new ArrayList<User>(users);
    }

    @Override
    public List<User> getCompanyAdminsByCompanyIdInSpecificProject(int companyId, int projectId) {
        List<User> users = new ArrayList<User>();
        for (User user : getCompanyAdminsByCompanyId(companyId)) {
            if (projectMember(user.getId(), companyId, projectId)) {
                users.add(user);
            }
        }
        return users;
    }

    @Override
    public List<User> getProjectAdminsByProjectId(int projectId) {
        Set<User> users = new HashSet<User>();
        users.add(userService.getById(projectService.getById(projectId).getCreatorId()));
        ProjectPrivilege pp = new ProjectPrivilege();
        pp.setIsAdmin(true);
        pp.setProjectId(projectId);
        List<ProjectPrivilege> pps = projectPrivilegeService.getProjectPrivilegesByExample(pp, 0, -1);
        for (ProjectPrivilege projectPrivilege : pps) {
            users.add(userService.getById(projectPrivilege.getUserId()));
        }
        int companyId = projectService.getById(projectId).getCompanyId();
        List<User> companyAdmins = getCompanyAdminsByCompanyId(companyId);
        for (User user : companyAdmins) {
            if (companyAdminInSpecificProject(user.getId(), companyId, projectId)) {
                users.add(user);
            }
        }
        return new ArrayList<User>(users);
    }

    @Override
    public List<User> getProjectMembersByProjectId(int projectId) {
        return userService.getUserByProjectId(projectId);
    }

    @Override
    public List<User> getCompanyMembersByCompanyId(int companyId) {
        return userService.getUserByCompanyId(companyId);
    }

}
