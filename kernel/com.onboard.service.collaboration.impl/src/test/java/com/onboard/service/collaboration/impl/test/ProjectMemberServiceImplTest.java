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
package com.onboard.service.collaboration.impl.test;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.onboard.domain.mapper.ProjectMapper;
import com.onboard.domain.mapper.ProjectPrivilegeMapper;
import com.onboard.domain.mapper.UserCompanyMapper;
import com.onboard.domain.mapper.UserProjectMapper;
import com.onboard.domain.mapper.model.ProjectPrivilegeExample;
import com.onboard.domain.mapper.model.UserProjectExample;
import com.onboard.domain.mapper.model.common.BaseExample;
import com.onboard.domain.model.Activity;
import com.onboard.domain.model.Company;
import com.onboard.domain.model.Project;
import com.onboard.domain.model.User;
import com.onboard.domain.model.UserCompany;
import com.onboard.domain.model.UserProject;
import com.onboard.service.account.AccountService;
import com.onboard.service.account.CompanyService;
import com.onboard.service.account.UserService;
import com.onboard.service.activity.ActivityService;
import com.onboard.service.collaboration.ProjectService;
import com.onboard.service.collaboration.activity.ActivityRecorderHelper;
import com.onboard.service.collaboration.impl.ProjectMemberService;
import com.onboard.service.web.SessionService;
import com.onboard.test.exampleutils.CriterionVerifier;
import com.onboard.test.exampleutils.ExampleMatcher;
import com.onboard.test.exampleutils.ObjectMatcher;
import com.onboard.test.moduleutils.ModuleHelper;

@RunWith(MockitoJUnitRunner.class)
public class ProjectMemberServiceImplTest {

    @InjectMocks
    private ProjectMemberService projectMemberService;

    @Mock
    private ProjectService projectService;

    @Mock
    private SessionService sessionService;

    @Mock
    private ProjectMapper projectMapper;

    @Mock
    private UserProjectMapper userProjectMapper;

    @Mock
    private UserCompanyMapper userCompanyMapper;

    @Mock
    private ProjectPrivilegeMapper projectPrivilegeMapper;

    @Mock
    private UserService userService;

    @Mock
    private CompanyService companyService;

    @Mock
    private AccountService accountService;

    @Mock
    private ActivityService activityService;

    private Company company;

    private Project project;

    private User user;

    @Before
    public void setUpBefore() throws Exception {
        project = ModuleHelper.getASampleProject();
        company = ModuleHelper.getASampleCompany();
        user = ModuleHelper.getASampleUser();
        user.setId(ModuleHelper.creatorId);
        when(companyService.containsUser(any(Integer.class), Matchers.argThat(new ObjectMatcher<Integer>() {
            @Override
            public boolean verifymatches(Integer integer) {
                return integer < 3;
            }
        }))).thenReturn(true);
        List<UserProject> userProjects = new ArrayList<UserProject>();
        userProjects.add(new UserProject());
        when(userProjectMapper.selectByExample(Matchers.argThat(new ExampleMatcher<UserProjectExample>() {
            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyLessThan(example, "userId", 3);
            }
        }))).thenReturn(userProjects);
        when(userService.getUserByEmail(Matchers.argThat(new ObjectMatcher<String>() {
            @Override
            public boolean verifymatches(String string) {
                return string.equals("1@qq.com");
            }
        }))).thenReturn(ModuleHelper.getASampleUser());
        when(projectMapper.selectByPrimaryKey(Mockito.eq(ModuleHelper.projectId))).thenReturn(project);

        ActivityRecorderHelper activityRecorderHelper = new ActivityRecorderHelper();
        activityRecorderHelper.setProjectService(projectService);
        activityRecorderHelper.setSession(sessionService);
        activityRecorderHelper.setUserService(userService);

        when(projectService.getById(anyInt())).thenReturn(project);
        when(userService.getById(anyInt())).thenReturn(ModuleHelper.getASampleUser());
        when(sessionService.getCurrentUser()).thenReturn(ModuleHelper.getASampleUser());
    }

    @Test
    public void testadd() {
        projectMemberService.add(company.getId(), project.getId(), 1, 2, 3, 4, 5);
        verify(companyService, times(5)).containsUser(any(Integer.class), any(Integer.class));
        verify(userCompanyMapper, times(3)).insert(any(UserCompany.class));
        verify(userProjectMapper, times(5)).selectByExample(Matchers.argThat(new ExampleMatcher<UserProjectExample>() {
            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "projectId", project.getId());
            }
        }));
        verify(userProjectMapper, times(5)).insert(any(UserProject.class));
        verify(accountService, times(5)).addActivityInfo(any(User.class), any(Integer.class));
    }

    @Test
    public void testRemove() {
        projectMemberService.remove(project.getId(), 1, 2, 3, 4, 5);
        verify(userProjectMapper, times(1)).deleteByExample(Matchers.argThat(new ExampleMatcher<UserProjectExample>() {
            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "projectId", project.getId())
                        && CriterionVerifier.verifyEqualTo(example, "userId", 5);
            }
        }));
        verify(projectPrivilegeMapper, times(1)).deleteByExample(Matchers.argThat(new ExampleMatcher<ProjectPrivilegeExample>() {
            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "projectId", project.getId())
                        && CriterionVerifier.verifyEqualTo(example, "userId", 5);
            }
        }));
        verify(activityService, times(5)).create(any(Activity.class));
    }

    @Test
    public void testInvite() {
        projectMemberService.invite(company.getId(), project.getId(), "1@qq.com", "2@qq.com", "3@qq.com");
        verify(userService, times(1)).getUserByEmail(Matchers.argThat(new ObjectMatcher<String>() {
            @Override
            public boolean verifymatches(String string) {
                return string.equals("3@qq.com");
            }
        }));
        verify(accountService, times(1)).sendInvitation(any(Integer.class), Matchers.argThat(new ObjectMatcher<String>() {
            @Override
            public boolean verifymatches(String string) {
                return string.equals("2@qq.com");
            }
        }), any(List.class));
        /*
         * ProjectMemberService roleServiceImplSpy = Mockito.spy(projectMemberService); verify(roleServiceImplSpy,
         * times(0)).add(Matchers.argThat(new ObjectMatcher<Integer>() {
         * 
         * @Override public boolean verifymatches(Integer integer) { return integer.equals(company.getId()); } }),
         * Matchers.argThat(new ObjectMatcher<Integer>() {
         * 
         * @Override public boolean verifymatches(Integer integer) { return integer.equals(project.getId()); } }),
         * any(Integer.class));
         */
    }

    @Test
    public void testGet() {
        projectMemberService.get(project.getId());
        verify(userProjectMapper).selectByExample(Matchers.argThat(new ExampleMatcher<UserProjectExample>() {
            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "projectId", project.getId());
            }
        }));
    }
}
