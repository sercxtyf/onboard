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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;

import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;
import com.onboard.domain.mapper.model.ProjectExample;
import com.onboard.domain.mapper.model.UserCompanyExample;
import com.onboard.domain.mapper.model.UserExample;
import com.onboard.domain.mapper.model.UserProjectExample;
import com.onboard.domain.mapper.model.common.BaseExample;
import com.onboard.domain.model.Project;
import com.onboard.domain.model.User;
import com.onboard.domain.model.UserCompany;
import com.onboard.service.account.impl.UserServiceImpl;
import com.onboard.service.account.redis.TokenType;
import com.onboard.test.exampleutils.CriterionVerifier;
import com.onboard.test.exampleutils.ExampleMatcher;
import com.onboard.test.exampleutils.ObjectMatcher;
import com.onboard.test.moduleutils.ModuleHelper;

public class UserServiceImplTest extends AbstractUserServiceTest {


    @InjectMocks
    private UserServiceImpl testedUserServiceImpl;
    
    @After
    public void afterUserTest() {
        Mockito.reset(mockUserMapper);
    }

    private void runAsserts(User user) {
        runAsserts(user, false);
    }
    
    private void runAsserts(User user, Boolean withPassword) {
        assertEquals(ModuleHelper.userId, (int) user.getId());
        if (withPassword) {
            assertEquals(ModuleHelper.password, user.getPassword());
            assertEquals(ModuleHelper.newPassword, user.getNewPassword());
        }
        else {
            assertNull(user.getPassword());
            assertNull(user.getNewPassword());
        }
    }
    
    @Test
    public void testGetUserWithPasswordByEmail() {
        User user = testedUserServiceImpl.getUserWithPasswordByEmail(ModuleHelper.email);
        verify(mockUserMapper, times(1)).selectByExample(Mockito.argThat(new ExampleMatcher<UserExample>() {
            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "email", ModuleHelper.email);
            }
        }));
        Mockito.verifyNoMoreInteractions(mockUserMapper);
        runAsserts(user, true);
    }
    
    /* mock底层的when无法条件返回，暂时无法跑这个测试
    @Test
    public void testGetUserWithPasswordByEmail2() {
        User user = testedUserServiceImpl.getUserWithPasswordByEmail(ModuleHelper.wrongEmail);
        verify(mockUserMapper, times(1)).selectByExample(Mockito.argThat(new ExampleMatcher<UserExample>() {
            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "email", ModuleHelper.wrongEmail);
            }
        }));
        Mockito.verifyNoMoreInteractions(mockUserMapper);
        assertNull("Expect return user to be null but not", user);
    }*/
    
    @Test
    public void testGetUserByEmailOrUsernameWithPassword() {
        User user = testedUserServiceImpl.getUserByEmailOrUsernameWithPassword(ModuleHelper.email);
        verify(mockUserMapper, times(1)).selectByExample(Mockito.argThat(new ExampleMatcher<UserExample>() {
            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "email", ModuleHelper.email);
            }
        }));
        Mockito.verifyNoMoreInteractions(mockUserMapper);
        runAsserts(user, true);
    }

    // CriterionVerifier只检查第一个条件，暂时无法跑这个测试
    /*
    @Test
    public void testGetUserByEmailOrUsernameWithPassword2() {
        User user = testedUserServiceImpl.getUserByEmailOrUsernameWithPassword(ModuleHelper.userName);
        verify(mockUserMapper, times(1)).selectByExample(Mockito.argThat(new ExampleMatcher<UserExample>() {
            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "username", ModuleHelper.userName);
            }
        }));
        Mockito.verifyNoMoreInteractions(mockUserMapper);
        runAsserts(user, false);
    }*/
    
    // 调用了别的方法，不知道该怎么测
    /*
    @Test
    public void testIsEmailRegistered(String email) {
    }*/
    
    @Test
    public void testGetUserById() {
        User user = testedUserServiceImpl.getById(ModuleHelper.id);
        verify(mockUserMapper, times(1)).selectByPrimaryKey(ModuleHelper.id);
        Mockito.verifyNoMoreInteractions(mockUserMapper);
        runAsserts(user);
    }
    
    @Test
    public void testGetUserByProjectId() {
        List<User> list = testedUserServiceImpl.getUserByProjectId(ModuleHelper.projectId);
        verify(mockUserProjectMapper, times(1)).selectByExample(Mockito.argThat(new ExampleMatcher<UserProjectExample>() {
            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "projectId", ModuleHelper.projectId);
            }
        }));
        assertEquals(2, list.size());
        runAsserts(list.get(0));
        runAsserts(list.get(1));
        verify(mockUserMapper, times(2)).selectByPrimaryKey(ModuleHelper.userId);
        Mockito.verifyNoMoreInteractions(mockUserProjectMapper);
        Mockito.verifyNoMoreInteractions(mockUserMapper);
    }
    
    /*
    @Test
    public void testGetDepartmentedUserByCompanyId() {
        Map<Department, List<User>> map = testedUserServiceImpl.getDepartmentedUserByCompanyId(ModuleHelper.companyId);
        
        
    }*/
    
    /**
     * @author 胡天翔
     * Branch that successfully get user
     */
    @Test
    public void testGetUserByEmail_Branch1() {
        User user = testedUserServiceImpl.getUserByEmail(ModuleHelper.email);
        verify(mockUserMapper, times(1)).selectByExample(Mockito.argThat(new ExampleMatcher<UserExample>() {
            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "email", ModuleHelper.email);
            }
        }));
        Mockito.verifyNoMoreInteractions(mockUserMapper);
        assertEquals(ModuleHelper.userId, (int) user.getId());
        assertNull("", user.getPassword());
        assertNull("", user.getNewPassword());
    }
    
    /**
     * @author 胡天翔
     * Branch that can't get user
     */
    @Test
    public void testGetUserByEmail_Branch2() {
        when(mockUserMapper.selectByExample(Mockito.any(UserExample.class))).thenReturn(new ArrayList<User>());
        User user = testedUserServiceImpl.getUserByEmail(ModuleHelper.email);
        verify(mockUserMapper, times(1)).selectByExample(Mockito.argThat(new ExampleMatcher<UserExample>() {
            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "email", ModuleHelper.email);
            }
        }));
        Mockito.verifyNoMoreInteractions(mockUserMapper);
        assertNull("", user);
    }
        
    /**
     * @author 胡天翔
     * Branch that user is activated
     */
    @Test
    public void testSendConfirmationEmail_Branch1() {
        User user = getASampleUser();
        user.setActivated(true);
        
        testedUserServiceImpl.sendConfirmationEmail(user);
        
        Mockito.verifyNoMoreInteractions(mockEmailService); 
        Mockito.verifyNoMoreInteractions(mockRepository);
    }
    
    /**
     * @author 胡天翔
     * Branch that user isn't activated
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testSendConfirmationEmail_Branch2() {
        User user = getASampleUser();
        user.setActivated(false);
        
        Mockito.doReturn(ModuleHelper.token).when(mockRepository).addToken(Mockito.any(TokenType.class), Mockito.anyInt(), Mockito.anyInt());
        Mockito.doReturn(null).when(mockEmailService).sendEmail(Mockito.anyString(), Mockito.any(String[].class), Mockito.any(String[].class), 
                Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
        Mockito.doReturn(ModuleHelper.tokenExpired).when(mockConfigurer).getTokenExpired();
        Mockito.doReturn(ModuleHelper.protocol).when(mockConfigurer).getProtocol();
        Mockito.doReturn(ModuleHelper.host).when(mockConfigurer).getHost();
        Mockito.doReturn(ModuleHelper.emailContent).when(mockTemplateEngineService).process((Class<?>) Mockito.anyObject(), Mockito.anyString(), Mockito.anyMap());
        
        testedUserServiceImpl.sendConfirmationEmail(user);

        verify(mockRepository).addToken(TokenType.CONFIRMATION, ModuleHelper.userId, ModuleHelper.tokenExpired);
        verify(mockConfigurer).getTokenExpired();
        verify(mockConfigurer).getProtocol();
        verify(mockConfigurer).getHost();
        verify(mockEmailService, times(1)).sendEmail(ModuleHelper.email, null, null, "[OnBoard]注册确认", ModuleHelper.emailContent, null);
        
        Mockito.verifyNoMoreInteractions(mockRepository); 
        Mockito.verifyNoMoreInteractions(mockConfigurer);
        Mockito.verifyNoMoreInteractions(mockEmailService);
    }
    
    /**
     * @author 胡天翔
     * Branch that such unconfirmed user doesn't exists
     */
    @Test
    public void testConfirmRegisteredUser_Branch1() {
        Mockito.doReturn(false).when(mockRepository).authenticateToken(Mockito.any(TokenType.class), Mockito.anyInt(), Mockito.anyString());
        
        testedUserServiceImpl.confirmRegisteredUser(ModuleHelper.userId, ModuleHelper.token);
        
        verify(mockRepository).authenticateToken(TokenType.CONFIRMATION, ModuleHelper.userId, ModuleHelper.token);

        Mockito.verifyNoMoreInteractions(mockRepository);
        Mockito.verifyNoMoreInteractions(mockUserMapper); 
    }
    
    /**
     * @author 胡天翔
     * Branch that such unconfirmed user exists
     */
    @Test
    public void testConfirmRegisteredUser_Branch2() {
        Mockito.doReturn(true).when(mockRepository).authenticateToken(Mockito.any(TokenType.class), Mockito.anyInt(), Mockito.anyString());
        Mockito.doReturn(0).when(mockUserMapper).updateByPrimaryKeySelective(Mockito.any(User.class));
        Mockito.doNothing().when(mockRepository).delToken(Mockito.any(TokenType.class), Mockito.anyInt());
        
        testedUserServiceImpl.confirmRegisteredUser(ModuleHelper.userId, ModuleHelper.token);
        
        verify(mockRepository).authenticateToken(TokenType.CONFIRMATION, ModuleHelper.userId, ModuleHelper.token);
        verify(mockUserMapper).updateByPrimaryKeySelective(Mockito.argThat(new ObjectMatcher<User>() {
            @Override
            public boolean verifymatches(User user) {
                return user.getId() == ModuleHelper.userId && user.getActivated() == true;
            }
        }));
        verify(mockRepository).delToken(TokenType.CONFIRMATION, ModuleHelper.userId);

        Mockito.verifyNoMoreInteractions(mockRepository);
        Mockito.verifyNoMoreInteractions(mockUserMapper); 
    }
        
    /**
     * @author 胡天翔
     * Test that such user doesn't exist
     */
    @Test
    public void testGetUserByCompanyId_Test1() {
        UserServiceImpl spyUserServiceImpl = Mockito.spy(testedUserServiceImpl);
        
        Mockito.doReturn(getASampleUser()).when(spyUserServiceImpl).getById(ModuleHelper.userId);
        Mockito.doReturn(new ArrayList<UserCompany>()).when(mockUserCompanyMapper).selectByExample(Mockito.argThat(new ExampleMatcher<UserCompanyExample>() {
            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "companyId", ModuleHelper.companyId);
            }
        }));
        
        List<User> list = spyUserServiceImpl.getUserByCompanyId(ModuleHelper.companyId);
        verify(spyUserServiceImpl, times(1)).getUserByCompanyId(ModuleHelper.companyId);
        
        verify(mockUserCompanyMapper).selectByExample(Mockito.argThat(new ExampleMatcher<UserCompanyExample>() {
            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "companyId", ModuleHelper.companyId);
            }
        }));
        Mockito.verifyNoMoreInteractions(mockUserCompanyMapper);
        Mockito.verifyNoMoreInteractions(spyUserServiceImpl);
        
        assertEquals(0, list.size());
    }
    
    /**
     * @author 胡天翔
     * Test that such user exists
     */
    @Test
    public void testGetUserByCompanyId_Test2() {
        UserServiceImpl spyUserServiceImpl = Mockito.spy(testedUserServiceImpl);
                
        Mockito.doReturn(getASampleUserWithoutPassword()).when(spyUserServiceImpl).getById(ModuleHelper.userId);
        Mockito.doReturn(listOfUserCompanys).when(mockUserCompanyMapper).selectByExample(Mockito.argThat(new ExampleMatcher<UserCompanyExample>() {
            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "companyId", ModuleHelper.companyId);
            }
        }));
        
        List<User> list = spyUserServiceImpl.getUserByCompanyId(ModuleHelper.companyId);
        verify(spyUserServiceImpl, times(1)).getUserByCompanyId(ModuleHelper.companyId);
        
        verify(mockUserCompanyMapper).selectByExample(Mockito.argThat(new ExampleMatcher<UserCompanyExample>() {
            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "companyId", ModuleHelper.companyId);
            }
        }));
        Mockito.verifyNoMoreInteractions(mockUserCompanyMapper);
        
        assertEquals(2, list.size());
        runAsserts(list.get(0));
        runAsserts(list.get(1));
        verify(spyUserServiceImpl, times(2)).getById(ModuleHelper.userId);
        
        Mockito.verifyNoMoreInteractions(spyUserServiceImpl);
    }
    
    /**
     * @author 胡天翔
     * Test that such user doesn't exist
     */
    @Test
    public void testGetUserByCompanyIdByDepartmentId_Test1() {
        Mockito.reset(mockUserCompanyMapper);
        Mockito.doReturn(new ArrayList<User>()).when(mockUserCompanyMapper).selectByExample(Mockito.argThat(new ExampleMatcher<UserCompanyExample>() {
            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "companyId", ModuleHelper.companyId)
                        && CriterionVerifier.verifyEqualTo(example, "groupId", ModuleHelper.groupId);
            }
        }));
        
        List<User> list = testedUserServiceImpl.getUserByCompanyIdByDepartmentId(ModuleHelper.groupId, ModuleHelper.companyId);
        
        assertEquals(0, list.size());
        
        verify(mockUserCompanyMapper, times(1)).selectByExample(Mockito.argThat(new ExampleMatcher<UserCompanyExample>() {
            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "companyId", ModuleHelper.companyId)
                        && CriterionVerifier.verifyEqualTo(example, "groupId", ModuleHelper.groupId);
            }
        }));
        
        Mockito.verifyNoMoreInteractions(mockUserCompanyMapper);
        Mockito.verifyNoMoreInteractions(mockUserMapper);
    }
    
    /**
     * @author 胡天翔
     * Test that such user exists
     */
    @Test
    public void testGetUserByCompanyIdByDepartmentId_Test2() {
        Mockito.reset(mockUserCompanyMapper);
        UserServiceImpl spyUserServiceImpl = Mockito.spy(testedUserServiceImpl);
        Mockito.doReturn(getASampleUserWithoutPassword()).when(spyUserServiceImpl).getById(ModuleHelper.userId);
        Mockito.doReturn(listOfUserCompanys).when(mockUserCompanyMapper).selectByExample(Mockito.argThat(new ExampleMatcher<UserCompanyExample>() {
            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "companyId", ModuleHelper.companyId)
                        && CriterionVerifier.verifyEqualTo(example, "groupId", ModuleHelper.groupId);
            }
        }));

        List<User> list = spyUserServiceImpl.getUserByCompanyIdByDepartmentId(ModuleHelper.groupId, ModuleHelper.companyId);
        verify(spyUserServiceImpl, times(1)).getUserByCompanyIdByDepartmentId(ModuleHelper.groupId, ModuleHelper.companyId);
        
        assertEquals(2, list.size());
        runAsserts(list.get(0));
        runAsserts(list.get(1));
        
        verify(mockUserCompanyMapper, times(1)).selectByExample(Mockito.argThat(new ExampleMatcher<UserCompanyExample>() {
            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "companyId", ModuleHelper.companyId)
                        && CriterionVerifier.verifyEqualTo(example, "groupId", ModuleHelper.groupId);
            }
        }));
        verify(spyUserServiceImpl, times(2)).getById(ModuleHelper.userId);
        
        Mockito.verifyNoMoreInteractions(mockUserCompanyMapper);
        Mockito.verifyNoMoreInteractions(spyUserServiceImpl);
    }
    
    /**
     * @author 胡天翔
     * Test that no such project exists
     */
    @Test
    public void testGetAllProjectUsersInCompany_Test1() {
        final int projectId0 = 0, projectId1 = 1;
        User user0 = getASampleUser(0), user1 = getASampleUser(1), user2 = getASampleUser(2), user3 = getASampleUser(3);
        List<User> listOfUsers0 = new ArrayList<User>(), listOfUsers1 = new ArrayList<User>();
        listOfUsers0.add(user0); listOfUsers0.add(user1); listOfUsers1.add(user2); listOfUsers1.add(user3);
        
        Mockito.reset(mockUserCompanyMapper);
        UserServiceImpl spyUserServiceImpl = Mockito.spy(testedUserServiceImpl);
        Mockito.doReturn(new ArrayList<Project>()).when(mockProjectMapper).selectByExample(Mockito.argThat(new ExampleMatcher<ProjectExample>() {
            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "companyId", ModuleHelper.companyId);
            }
        }));
        Mockito.doReturn(listOfUsers0).when(spyUserServiceImpl).getUserByProjectId(projectId0);
        Mockito.doReturn(listOfUsers1).when(spyUserServiceImpl).getUserByProjectId(projectId1);
        
        Map<Integer, List<User>> map = spyUserServiceImpl.getAllProjectUsersInCompany(ModuleHelper.companyId);
        
        verify(spyUserServiceImpl, times(1)).getAllProjectUsersInCompany(ModuleHelper.companyId);
        
        verify(mockProjectMapper, times(1)).selectByExample(Mockito.argThat(new ExampleMatcher<ProjectExample>() {
            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "companyId", ModuleHelper.companyId);
            }
        }));
        
        assertEquals(0, map.size());

        Mockito.verifyNoMoreInteractions(mockUserCompanyMapper);
        Mockito.verifyNoMoreInteractions(spyUserServiceImpl);
    }
    
    /**
     * @author 胡天翔
     * Test that such project exists
     */
    @Test
    public void testGetAllProjectUsersInCompany_Test2() {
        Mockito.reset(mockUserCompanyMapper);
        
        final int projectId0 = 0, projectId1 = 1, projectId2 = 2;
        List<User> listOfUsers0 = new ArrayList<User>(), listOfUsers1 = new ArrayList<User>();
        listOfUsers0.add(getASampleUser(0)); 
        listOfUsers0.add(getASampleUser(1));
        listOfUsers1.add(getASampleUser(2)); 
        listOfUsers1.add(getASampleUser(3));
        List<Project> listOfProjects = new ArrayList<Project>();
        listOfProjects.add(getASampleProject(projectId0));
        listOfProjects.add(getASampleProject(projectId1));
        listOfProjects.add(getASampleProject(projectId2));
        
        UserServiceImpl spyUserServiceImpl = Mockito.spy(testedUserServiceImpl);
        Mockito.doReturn(listOfProjects).when(mockProjectMapper).selectByExample(Mockito.argThat(new ExampleMatcher<ProjectExample>() {
            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "companyId", ModuleHelper.companyId);
            }
        }));
        Mockito.doReturn(listOfUsers0).when(spyUserServiceImpl).getUserByProjectId(projectId0);
        Mockito.doReturn(listOfUsers1).when(spyUserServiceImpl).getUserByProjectId(projectId1);
        Mockito.doReturn(new ArrayList<User>()).when(spyUserServiceImpl).getUserByProjectId(projectId2);
        
        Map<Integer, List<User>> map = spyUserServiceImpl.getAllProjectUsersInCompany(ModuleHelper.companyId);
        
        verify(spyUserServiceImpl, times(1)).getAllProjectUsersInCompany(ModuleHelper.companyId);
        
        verify(mockProjectMapper, times(1)).selectByExample(Mockito.argThat(new ExampleMatcher<ProjectExample>() {
            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "companyId", ModuleHelper.companyId);
            }
        }));
        verify(spyUserServiceImpl, times(1)).getUserByProjectId(projectId0);
        verify(spyUserServiceImpl, times(1)).getUserByProjectId(projectId1);
        verify(spyUserServiceImpl, times(1)).getUserByProjectId(projectId2);
        Mockito.verifyNoMoreInteractions(mockUserCompanyMapper);
        Mockito.verifyNoMoreInteractions(spyUserServiceImpl);
        
        assertEquals(3, map.size());
        assertEquals(2, map.get(projectId0).size());
        assertEquals(2, map.get(projectId1).size());
        assertEquals(0, map.get(projectId2).size());
        assertEquals(0, (int) map.get(projectId0).get(0).getId());
        assertEquals(1, (int) map.get(projectId0).get(1).getId());
        assertEquals(2, (int) map.get(projectId1).get(0).getId());
        assertEquals(3, (int) map.get(projectId1).get(1).getId());
    }

    /**
     * @author 胡天翔
     * Test that such user-company exists
     */
    @Test
    public void testIsUserInCompany_Test1() {
        Mockito.reset(mockUserCompanyMapper);
        
        Mockito.doReturn(1).when(mockUserCompanyMapper).countByExample(Mockito.argThat(new ExampleMatcher<UserCompanyExample>() {
            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "userId", ModuleHelper.userId)
                        && CriterionVerifier.verifyEqualTo(example, "companyId", ModuleHelper.companyId);
            }
        }));
        
        Boolean result = testedUserServiceImpl.isUserInCompany(ModuleHelper.userId, ModuleHelper.companyId);
        
        verify(mockUserCompanyMapper, times(1)).countByExample(Mockito.argThat(new ExampleMatcher<UserCompanyExample>() {
            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "userId", ModuleHelper.userId)
                        && CriterionVerifier.verifyEqualTo(example, "companyId", ModuleHelper.companyId);
            }
        }));
        Mockito.verifyNoMoreInteractions(mockUserCompanyMapper);
        
        assertTrue(result);
    }
    
    /**
     * @author 胡天翔
     * Test that such user-company doesn't exists
     */
    @Test
    public void testIsUserInCompany_Test2() {
        Mockito.reset(mockUserCompanyMapper);
        
        Mockito.doReturn(0).when(mockUserCompanyMapper).countByExample(Mockito.argThat(new ExampleMatcher<UserCompanyExample>() {
            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "userId", ModuleHelper.userId)
                        && CriterionVerifier.verifyEqualTo(example, "companyId", ModuleHelper.companyId);
            }
        }));
        
        Boolean result = testedUserServiceImpl.isUserInCompany(ModuleHelper.userId, ModuleHelper.companyId);
        
        verify(mockUserCompanyMapper, times(1)).countByExample(Mockito.argThat(new ExampleMatcher<UserCompanyExample>() {
            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "userId", ModuleHelper.userId)
                        && CriterionVerifier.verifyEqualTo(example, "companyId", ModuleHelper.companyId);
            }
        }));
        Mockito.verifyNoMoreInteractions(mockUserCompanyMapper);
        
        assertFalse(result);
    }
   
    /**
     * @author 胡天翔
     * Test that such user doesn't exist
     */
    @Test
    public void testGetUserByEmailOrUsername_Test1() {
        Mockito.reset(mockUserCompanyMapper);

        UserServiceImpl spyUserServiceImpl = Mockito.spy(testedUserServiceImpl);
        Mockito.doReturn(null).when(spyUserServiceImpl).getUserByEmailOrUsernameWithPassword(ModuleHelper.emailOrUsername);
        
        User user = spyUserServiceImpl.getUserByEmailOrUsername(ModuleHelper.emailOrUsername);
        
        verify(spyUserServiceImpl, times(1)).getUserByEmailOrUsername(ModuleHelper.emailOrUsername);
        verify(spyUserServiceImpl, times(1)).getUserByEmailOrUsernameWithPassword(ModuleHelper.emailOrUsername);
        Mockito.verifyNoMoreInteractions(spyUserServiceImpl);
        
        assertNull(user);
    }
    
    /**
     * @author 胡天翔
     * Test that such user exists
     */
    @Test
    public void testGetUserByEmailOrUsername_Test2() {
        UserServiceImpl spyUserServiceImpl = Mockito.spy(testedUserServiceImpl);
        Mockito.doReturn(getASampleUser()).when(spyUserServiceImpl).getUserByEmailOrUsernameWithPassword(ModuleHelper.emailOrUsername);
        
        User user = spyUserServiceImpl.getUserByEmailOrUsername(ModuleHelper.emailOrUsername);
        
        verify(spyUserServiceImpl, times(1)).getUserByEmailOrUsername(ModuleHelper.emailOrUsername);
        verify(spyUserServiceImpl, times(1)).getUserByEmailOrUsernameWithPassword(ModuleHelper.emailOrUsername);
        Mockito.verifyNoMoreInteractions(spyUserServiceImpl);

        runAsserts(user, false);
    }
    
    /**
     * @author 胡天翔
     * Test that such user doesn't exist
     */
    @Test
    public void testContainUsername_Test1() {
        Mockito.doReturn(0).when(mockUserMapper).countByExample(Mockito.argThat(new ExampleMatcher<UserExample>() {
            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "username", ModuleHelper.username);
            }
        }));
        
        Boolean result = testedUserServiceImpl.containUsername(ModuleHelper.username);
        
        verify(mockUserMapper, times(1)).countByExample(Mockito.argThat(new ExampleMatcher<UserExample>() {
            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "username", ModuleHelper.username);
            }
        }));
        Mockito.verifyNoMoreInteractions(mockUserMapper);

        assertFalse(result);
    }
    
    /**
     * @author 胡天翔
     * Test that such user exists
     */
    @Test
    public void testContainUsername_Test2() {
        Mockito.reset(mockUserCompanyMapper);

        Mockito.doReturn(2).when(mockUserMapper).countByExample(Mockito.argThat(new ExampleMatcher<UserExample>() {
            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "username", ModuleHelper.username);
            }
        }));
        
        Boolean result = testedUserServiceImpl.containUsername(ModuleHelper.username);
        
        verify(mockUserMapper, times(1)).countByExample(Mockito.argThat(new ExampleMatcher<UserExample>() {
            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "username", ModuleHelper.username);
            }
        }));
        Mockito.verifyNoMoreInteractions(mockUserMapper);

        assertTrue(result);
    }
    
    /**
     * @author 胡天翔
     */
    @Test
    public void testIsPasswordValid() {
        // Covered by PasswordUtils.isPasswordValid
    }
    
    /**
     * @author 胡天翔
     */
    @Test
    public void testCreatePassword() {
        // Covered by PasswordUtils.createPassword
    }
    
    /**
     * @author 胡天翔
     */
    @Test
    public void testFilterProjectMembers() {
        List<User> list1 = new ArrayList<User>(), list2 = new ArrayList<User>();
        list1.add(getASampleUser(0));
        list1.add(getASampleUser(1));
        list1.add(getASampleUser(2));
        list2.add(getASampleUser(1));
        list2.add(getASampleUser(2));
        list2.add(getASampleUser(3));
        SetView<User> intersection = Sets.intersection(new HashSet<User>(list1), new HashSet<User>(list2));
        
        UserServiceImpl spyUserServiceImpl = Mockito.spy(testedUserServiceImpl);
        Mockito.doReturn(list2).when(spyUserServiceImpl).getUserByProjectId(ModuleHelper.projectId);
        
        List<User> user = spyUserServiceImpl.filterProjectMembers(list1, ModuleHelper.projectId);
        
        Mockito.verify(spyUserServiceImpl).filterProjectMembers(list1, ModuleHelper.projectId);
        Mockito.verify(spyUserServiceImpl).getUserByProjectId(ModuleHelper.projectId);
        Mockito.verifyNoMoreInteractions(spyUserServiceImpl);
        
        assertEquals(intersection.size(), Sets.intersection(intersection, new HashSet<User>(user)).size());
    }
    
}
