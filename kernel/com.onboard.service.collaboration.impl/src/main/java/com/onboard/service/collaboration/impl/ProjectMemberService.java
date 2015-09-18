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
package com.onboard.service.collaboration.impl;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.onboard.domain.mapper.ProjectMapper;
import com.onboard.domain.mapper.ProjectPrivilegeMapper;
import com.onboard.domain.mapper.UserCompanyMapper;
import com.onboard.domain.mapper.UserProjectMapper;
import com.onboard.domain.mapper.model.ProjectPrivilegeExample;
import com.onboard.domain.mapper.model.UserProjectExample;
import com.onboard.domain.model.Activity;
import com.onboard.domain.model.ProjectPrivilege;
import com.onboard.domain.model.User;
import com.onboard.domain.model.UserCompany;
import com.onboard.domain.model.UserProject;
import com.onboard.service.account.AccountService;
import com.onboard.service.account.CompanyService;
import com.onboard.service.account.UserService;
import com.onboard.service.activity.ActivityService;
import com.onboard.service.collaboration.activity.ActivityRecorderHelper;

@Service
public class ProjectMemberService {

    public static final Logger logger = LoggerFactory.getLogger(ProjectMemberService.class);

    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private UserProjectMapper userProjectMapper;

    @Autowired
    private UserCompanyMapper userCompanyMapper;

    @Autowired
    private ProjectPrivilegeMapper projectPrivilegeMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private ActivityService activityService;

    public void add(int companyId, int projectId, int... userIds) {
        for (Integer userId : userIds) {
            if (!companyService.containsUser(companyId, userId)) {
                UserCompany userCompany = new UserCompany();
                userCompany.setCompanyId(companyId);
                userCompany.setUserId(userId);
                userCompanyMapper.insert(userCompany);
            }

            UserProject userProject = new UserProject();
            userProject.setCompanyId(companyId);
            userProject.setProjectId(projectId);
            userProject.setUserId(userId);
            if (!userProjectMapper.selectByExample(new UserProjectExample(userProject)).isEmpty()) {
                continue;
            }
            userProjectMapper.insert(userProject);

            User user = userService.getById(userId);
            accountService.addActivityInfo(user, projectId);
        }
    }

    public void remove(int projectId, int... userIds) {
        for (Integer userId : userIds) {
            UserProject userProject = new UserProject();
            userProject.setProjectId(projectId);
            userProject.setUserId(userId);
            userProjectMapper.deleteByExample(new UserProjectExample(userProject));

            ProjectPrivilege sample = new ProjectPrivilege();
            sample.setProjectId(projectId);
            sample.setUserId(userId);
            projectPrivilegeMapper.deleteByExample(new ProjectPrivilegeExample(sample));

            Activity activity = ActivityRecorderHelper.generateActivityOfRemoveUser(projectId, userId);
            activityService.create(activity);
        }
    }

    public void invite(int companyId, int projectId, String... emails) {
        for (String email : emails) {
            User user = userService.getUserByEmail(email);
            if (user == null) {
                accountService.sendInvitation(companyId, email,
                  Arrays.asList(projectMapper.selectByPrimaryKey(projectId)));
            } else {
                add(companyId, projectId, user.getId());
            }
        }
    }

    public List<Integer> get(int projectId) {
        UserProject userProject = new UserProject();
        userProject.setProjectId(projectId);

        List<UserProject> results = userProjectMapper.selectByExample(new UserProjectExample(userProject));

        return Lists.transform(results, new Function<UserProject, Integer>() {
            @Override
            public Integer apply(UserProject input) {
                return input.getUserId();
            }
        });
    }

}
