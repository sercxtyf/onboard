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
package com.onboard.service.account.impl.test;

import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.onboard.domain.mapper.CompanyMapper;
import com.onboard.domain.mapper.ProjectMapper;
import com.onboard.domain.mapper.UserCompanyMapper;
import com.onboard.domain.mapper.UserMapper;
import com.onboard.domain.mapper.UserProjectMapper;
import com.onboard.domain.mapper.model.UserCompanyExample;
import com.onboard.domain.mapper.model.UserExample;
import com.onboard.domain.mapper.model.UserProjectExample;
import com.onboard.domain.model.Project;
import com.onboard.domain.model.User;
import com.onboard.domain.model.UserCompany;
import com.onboard.domain.model.UserProject;
import com.onboard.service.account.impl.AccountConfigure;
import com.onboard.service.account.redis.Repository;
import com.onboard.service.email.EmailService;
import com.onboard.service.email.TemplateEngineService;
import com.onboard.test.moduleutils.ModuleHelper;

@RunWith(MockitoJUnitRunner.class)
public abstract class AbstractUserServiceTest {

    @Mock
    public UserMapper mockUserMapper;
    
    @Mock
    public UserProjectMapper mockUserProjectMapper;
    
    @Mock
    public UserCompanyMapper mockUserCompanyMapper;

    @Mock
    public ProjectMapper mockProjectMapper; 
    
    @Mock
    public CompanyMapper mockCompanyMapper;
    
    @Mock
    public Repository mockRepository;
    
    @Mock
    public EmailService mockEmailService;
    
    @Mock
    public AccountConfigure mockConfigurer;
    
    @Mock
    public TemplateEngineService mockTemplateEngineService;
    
    @Before
    public void setupUserTest() {
        initUserMapper();
    }
    
    public User user;
    public UserExample userExample;
    public List<User> listOfUsers;
    
    public UserProject userProject;
    public List<UserProject> listOfUserProjects;
    
    public UserCompany userCompany;
    public List<UserCompany> listOfUserCompanys;
    
    public void initUserMapper() {
        user = getASampleUser();
        userExample = getASampleUserExample();
        listOfUsers = getAListOfSampleUsers();
        
        userProject = getASampleUserProject();
        listOfUserProjects = getAListOfSampleUserProjects();
        
        userCompany = getASampleUserCompany();
        listOfUserCompanys = getAListOfSampleUserCompanys(2);
        
        when(mockUserMapper.selectByPrimaryKey(Mockito.anyInt())).thenReturn(user);
        when(mockUserMapper.selectByExample(Mockito.any(UserExample.class))).thenReturn(listOfUsers);
        
        when(mockUserProjectMapper.selectByExample(Mockito.any(UserProjectExample.class))).thenReturn(listOfUserProjects);
        
        when(mockUserCompanyMapper.selectByExample(Mockito.any(UserCompanyExample.class))).thenReturn(listOfUserCompanys);
    }
    
    public User getASampleUser() {
        User user = new User();
        user.setId(ModuleHelper.userId);
        user.setEmail(ModuleHelper.email);
        user.setPassword(ModuleHelper.password);
        user.setNewPassword(ModuleHelper.newPassword);
        user.setUsername(ModuleHelper.username);
        return user;
    }
    
    public User getASampleUserWithoutPassword() {
        User user = getASampleUser();
        user.setPassword(null);
        user.setNewPassword(null);
        return user;
    }
    
    public User getASampleUser(int userId) {
        User user = new User();
        user.setId(userId);
        user.setEmail(ModuleHelper.email);
        return user;
    }
    
    public UserExample getASampleUserExample() {
        UserExample example = new UserExample(getASampleUser());
        return example;
    }
    
    public List<User> getAListOfSampleUsers() {
        List<User> list = new ArrayList<User>();
        list.add(getASampleUser());
        return list;
    }
    
    public UserProject getASampleUserProject() {
        UserProject userProject = new UserProject();
        userProject.setCompanyId(ModuleHelper.companyId);
        userProject.setId(ModuleHelper.id);
        userProject.setProjectId(ModuleHelper.projectId);
        userProject.setUserId(ModuleHelper.userId);
        return userProject;
    }
    
    public List<UserProject> getAListOfSampleUserProjects() {
        List<UserProject> list = new ArrayList<UserProject>();
        list.add(getASampleUserProject());
        list.add(getASampleUserProject());
        return list;
    }
    
    public UserCompany getASampleUserCompany() {
        UserCompany userCompany = new UserCompany();
        userCompany.setCompanyId(ModuleHelper.companyId);
        userCompany.setDepartmentId(ModuleHelper.groupId);
        userCompany.setId(ModuleHelper.userCompanyId);
        userCompany.setUserId(ModuleHelper.userId);
        return userCompany;
    }
    
    public List<UserCompany> getAListOfSampleUserCompanys(int size) {
        List<UserCompany> list = new ArrayList<UserCompany>();
        for (int i = 0; i < size; i++) {
            list.add(getASampleUserCompany());
        }
        return list;
    }
    
    public Project getASampleProject(int projectId) {
        Project project = new Project();
        project.setId(projectId);
        project.setProjectId(1);
        return project;
    }
}
