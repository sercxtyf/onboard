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
package com.onboard.service.security.impl.test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.anyInt;

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

import com.onboard.domain.mapper.UserCompanyMapper;
import com.onboard.domain.mapper.UserMapper;
import com.onboard.domain.mapper.UserProjectMapper;
import com.onboard.domain.mapper.model.UserCompanyExample;
import com.onboard.domain.mapper.model.UserProjectExample;
import com.onboard.domain.mapper.model.common.BaseExample;
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
import com.onboard.service.security.impl.RoleServiceImpl;
import com.onboard.test.exampleutils.CriterionVerifier;
import com.onboard.test.exampleutils.ExampleMatcher;
import com.onboard.test.exampleutils.ObjectMatcher;
import com.onboard.test.moduleutils.ModuleHelper;

@RunWith(MockitoJUnitRunner.class)
public class RoleServiceImplTest {
    @InjectMocks
    private RoleServiceImpl roleServiceImpl;

    @Mock
    private CompanyService companyService;

    @Mock
    private UserService userService;

    @Mock
    private ProjectService projectService;

    @Mock
    private CompanyPrivilegeService companyPrivilegeService;

    @Mock
    private ProjectPrivilegeService projectPrivilegeService;

    @Mock
    private UserProjectMapper userProjectMapper;

    @Mock
    private UserCompanyMapper userCompanyMapper;

    @Mock
    private UserMapper userMapper;

    private Company company;

    private Project project;

    private CompanyPrivilege companyPrivilege, companyPrivilegeFalse;
    private List< CompanyPrivilege > companyPrivilegeList;

    private ProjectPrivilege projectPrivilege, projectPrivilegeFalse;
    private List< ProjectPrivilege > projectPrivilegeList;

    private User user;
    private List< User > userList;

    @Before
    public void setUpBefore() throws Exception {
        project = ModuleHelper.getASampleProject();
             
        company = ModuleHelper.getASampleCompany();
        user = ModuleHelper.getASampleUser();
        user.setId(ModuleHelper.creatorId);
        
        userList = new ArrayList<User>();
        userList.add(user);
        userList.add(user);
        
        companyPrivilege = new CompanyPrivilege();
        companyPrivilege.setCompanyId(company.getId());
        companyPrivilege.setUserId(user.getId());
        companyPrivilege.setIsAdmin(true);
        companyPrivilege.setCanCreateProject(true);
        
        companyPrivilegeList = new ArrayList< CompanyPrivilege >();
        companyPrivilegeList.add(companyPrivilege);
        companyPrivilegeList.add(companyPrivilege);
        
        companyPrivilegeFalse = new CompanyPrivilege();
        companyPrivilegeFalse.setCompanyId(company.getId() + 1);
        companyPrivilegeFalse.setUserId(user.getId() + 1);
        companyPrivilegeFalse.setIsAdmin(false);
        companyPrivilegeFalse.setCanCreateProject(false);
        
        projectPrivilege = new ProjectPrivilege();
        projectPrivilege.setProjectId(project.getId());
        projectPrivilege.setUserId(user.getId());
        projectPrivilege.setIsAdmin(true);
        
        projectPrivilegeList = new ArrayList< ProjectPrivilege >();
        projectPrivilegeList.add(projectPrivilege);
        projectPrivilegeList.add(projectPrivilege);
        
        projectPrivilegeFalse = new ProjectPrivilege();
        projectPrivilegeFalse.setProjectId(project.getId() + 1);
        projectPrivilegeFalse.setUserId(user.getId() + 1);
        projectPrivilegeFalse.setIsAdmin(false);
        
        when(companyService.getById( Mockito.eq(company.getId()) )).thenReturn(company);
        when(projectService.getById( Mockito.eq(project.getId()) )).thenReturn(project);
        when(userService.getById(anyInt())).thenReturn(user);
        when(userService.isUserInProject(any(Integer.class), any(Integer.class), any(Integer.class))).thenReturn(true);
        when(userService.getUserByCompanyId(Mockito.eq(company.getId()))).thenReturn(userList);
        when(userService.getUserByProjectId(Mockito.eq(project.getId()))).thenReturn(userList);
        
    }

    @Test
    public void testCompanyOwnerTrue() {
        boolean result = roleServiceImpl.companyOwner(user.getId(), company.getId());
        assertEquals(result, true);
    }
    
    @Test
    public void testCompanyOwnerFalse() {
    	boolean result;
        result = roleServiceImpl.companyOwner(user.getId() + 1, company.getId());
        assertEquals(result, false);
        result = roleServiceImpl.companyOwner(user.getId() + 1, company.getId() + 1);
        assertEquals(result, false);
    }

    @Test
    public void testCompanyAdminTrue() {
        when(companyPrivilegeService.getOrCreateCompanyPrivilegeByUserId(anyInt(), anyInt())).thenReturn(companyPrivilege);
        
        boolean result = roleServiceImpl.companyAdmin(user.getId(), company.getId());
        assertEquals(result, true);
    }
    
    @Test
    public void testCompanyAdminFalse() {
        when(companyPrivilegeService.getOrCreateCompanyPrivilegeByUserId(anyInt(), anyInt())).thenReturn(companyPrivilegeFalse);
        
    	boolean result;
        result = roleServiceImpl.companyAdmin(user.getId() + 1, company.getId());
        assertEquals(result, false);
        result = roleServiceImpl.companyAdmin(user.getId(), company.getId() + 1);
        assertEquals(result, false);
    }

    @Test
    public void testCompanyMemberCanCreateProjectTrue() {
        when(companyPrivilegeService.getOrCreateCompanyPrivilegeByUserId(anyInt(), anyInt())).thenReturn(companyPrivilege);
        boolean result = roleServiceImpl.companyMemberCanCreateProject(user.getId(), company.getId());
        assertEquals(result, true);
    }
    
    @Test
    public void testCompanyMemberCanCreateProjectFalse() {
        when(companyPrivilegeService.getOrCreateCompanyPrivilegeByUserId(anyInt(), anyInt())).thenReturn(companyPrivilegeFalse);
        boolean result;
        result = roleServiceImpl.companyMemberCanCreateProject(user.getId() + 1, company.getId());
        assertEquals(result, false);
        result = roleServiceImpl.companyMemberCanCreateProject(user.getId(), company.getId() + 1);
        assertEquals(result, false);
    }

    @Test
    public void testProjectAdminTrue() {
        when(projectPrivilegeService.getOrCreateProjectPrivilegeByUserId(anyInt(), anyInt())).thenReturn(projectPrivilege);
        boolean result = roleServiceImpl.projectAdmin(user.getId(), company.getId(), project.getId());
        assertEquals(result, true);
    }
    
    @Test
    public void testProjectAdminFalse() {
        when(companyPrivilegeService.getOrCreateCompanyPrivilegeByUserId(anyInt(), anyInt())).thenReturn(companyPrivilegeFalse);
        when(projectPrivilegeService.getOrCreateProjectPrivilegeByUserId(anyInt(), anyInt())).thenReturn(projectPrivilegeFalse);
        boolean result;
        result = roleServiceImpl.projectAdmin(user.getId() + 1, company.getId() + 1, project.getId());
        assertEquals(result, false);
        result = roleServiceImpl.projectAdmin(user.getId() + 1, company.getId(), project.getId() + 1);
        assertEquals(result, false);
    }

    @Test
    public void testCompanyAdminInSpecificProject() {
        when(companyPrivilegeService.getOrCreateCompanyPrivilegeByUserId(anyInt(), anyInt())).thenReturn(companyPrivilege);
        boolean result = roleServiceImpl.companyAdminInSpecificProject(user.getId(), company.getId(), project.getId());
        assertEquals(result, true);
    }

    @Test
    public void testProjectMember() {
        roleServiceImpl.projectMember(user.getId(), company.getId(), project.getId());
        verify(userService).isUserInProject(Mockito.eq(user.getId()), Mockito.eq(company.getId()), Mockito.eq(project.getId()));

    }

    @Test
    public void testProjectCreator() {
        boolean result = roleServiceImpl.projectCreator(user.getId(), project.getId());
        assertEquals(result, true);
    }

    @Test
    public void testCompanyMember() {
    	when(companyService.containsUser(Mockito.eq(company.getId()), Mockito.eq(user.getId())) ).thenReturn(true);
        boolean ret = roleServiceImpl.companyMember(user.getId(), company.getId());
        verify(companyService).containsUser(anyInt(), anyInt());
        assertEquals(ret, true);
    }

    @Test
    public void testGetCompanyOwnerByCompanyId() {
        User ret = roleServiceImpl.getCompanyOwnerByCompanyId(company.getId());
        verify(companyService).getById(Mockito.eq( company.getId() ));
        verify(userService).getById(Mockito.eq( company.getCreatorId() ));
        assertEquals(ret, user);
    }

    @Test
    public void testGetProjectMembersByProjectId() {
    	when(userService.getUserByProjectId(Mockito.eq(project.getId()))).thenReturn(userList);
        List<User> ret = roleServiceImpl.getProjectMembersByProjectId(project.getId());
        verify(userService).getUserByProjectId(anyInt());
        assertEquals(ret.size(), 2);
        assertEquals(ret.get(0), user);
    }

    @Test
    public void testGetCompanyMembersByCompanyId() {
        List<User> ret = roleServiceImpl.getCompanyMembersByCompanyId(company.getId());
        verify(userService).getUserByCompanyId(anyInt());
        assertEquals(ret.size(), 2);
        assertEquals(ret.get(0), user);
    }

    @Test
    public void testGetCompanyAdminsByCompanyId() {
    	when(companyPrivilegeService.getCompanyPrivilegesByExample(any(CompanyPrivilege.class), anyInt(), anyInt())).thenReturn(companyPrivilegeList);
    	
    	List<User> ret = roleServiceImpl.getCompanyAdminsByCompanyId(company.getId());
    	
        verify(companyPrivilegeService).getCompanyPrivilegesByExample(argThat(new ObjectMatcher<CompanyPrivilege>() {
            @Override
            public boolean verifymatches(CompanyPrivilege item) {
                return item.getCompanyId().equals(company.getId()) && item.getIsAdmin();
            }
        }), any(Integer.class), any(Integer.class));
        
        assertEquals(ret.size(), 1);
        assertEquals(ret.get(0), user);
    }

    @Test
    public void testGetCompanyAdminsByCompanyIdInSpecificProject() {
    	when(companyPrivilegeService.getCompanyPrivilegesByExample(any(CompanyPrivilege.class), anyInt(), anyInt())).thenReturn(companyPrivilegeList);
    	List<User> ret = roleServiceImpl.getCompanyAdminsByCompanyIdInSpecificProject(company.getId(), project.getId());
    	verify(companyPrivilegeService).getCompanyPrivilegesByExample(argThat(new ObjectMatcher<CompanyPrivilege>() {
            @Override
            public boolean verifymatches(CompanyPrivilege item) {
                return item.getCompanyId().equals(company.getId()) && item.getIsAdmin();
            }
        }), any(Integer.class), any(Integer.class));
        assertEquals(ret.size(), 1);
        assertEquals(ret.get(0), user);

    }

    @Test
    public void testGetProjectAdminsByProjectId() {
        RoleServiceImpl roleServiceImplSpy = Mockito.spy(roleServiceImpl);
        when(projectPrivilegeService.getProjectPrivilegesByExample(any(ProjectPrivilege.class), anyInt(), anyInt())).thenReturn(projectPrivilegeList);
        when(companyPrivilegeService.getCompanyPrivilegesByExample(any(CompanyPrivilege.class), anyInt(), anyInt())).thenReturn(companyPrivilegeList);
        when(companyPrivilegeService.getOrCreateCompanyPrivilegeByUserId(anyInt(), anyInt())).thenReturn(companyPrivilege);
        
        List<User> ret = roleServiceImpl.getProjectAdminsByProjectId(project.getId());
        
        verify(projectService, Mockito.times(2)).getById(anyInt());
        verify(userService, Mockito.times(6)).getById(anyInt());
        verify(projectPrivilegeService).getProjectPrivilegesByExample(argThat(new ObjectMatcher<ProjectPrivilege>() {
            @Override
            public boolean verifymatches(ProjectPrivilege item) {
                return item.getProjectId().equals(project.getId()) && item.getIsAdmin();
            }
        }), any(Integer.class), any(Integer.class));
        assertEquals(ret.size(), 1);
        assertEquals(ret.get(0), user);
        //verify(roleServiceImplSpy).getCompanyAdminsByCompanyId(project.getCompanyId());

    }
}
