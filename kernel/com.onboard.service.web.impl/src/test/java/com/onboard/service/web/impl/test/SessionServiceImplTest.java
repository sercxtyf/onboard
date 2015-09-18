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
package com.onboard.service.web.impl.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;

import javax.servlet.http.HttpSession;

import org.elevenframework.web.GlobalService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.core.env.Environment;

import com.onboard.domain.mapper.UserMapper;
import com.onboard.domain.model.Company;
import com.onboard.domain.model.Project;
import com.onboard.domain.model.User;
import com.onboard.service.web.SessionService;
import com.onboard.service.web.impl.SessionServiceImpl;
import com.onboard.test.moduleutils.ModuleHelper;

@RunWith(MockitoJUnitRunner.class)
public class SessionServiceImplTest {

    private static final String PRODUCTION_ENV = "production";

    @Mock
    private Environment mockedEnvironment;

    @Mock
    private GlobalService mockedGlobalService;

    @Mock
    private UserMapper mockedUserMapper;

    @Mock
    private HttpSession mockedSession;

    @InjectMocks
    private SessionServiceImpl sessionServiceImpl;

    private User user;

    public static User getASampleUser() {
        User user = new User();
        user.setId(ModuleHelper.id);
        return user;
    }

    public static Project getASampleProject() {
        Project project = new Project();
        project.setId(ModuleHelper.id);
        return project;
    }

    public static Company getASampleCompany() {
        Company company = new Company();
        company.setId(ModuleHelper.id);
        return company;
    }

    @Before
    public void setup() {
        Mockito.reset(mockedSession);
        Mockito.reset(mockedEnvironment);
        when(mockedGlobalService.getSession()).thenReturn(mockedSession);
        when(mockedUserMapper.selectByPrimaryKey(Mockito.anyInt())).thenReturn(getASampleUser());
        sessionServiceImpl.init();
        try {
            Field userField = SessionServiceImpl.class.getDeclaredField("user");
            userField.setAccessible(true);
            user = (User) userField.get(sessionServiceImpl);
        } catch (Exception e) {
            e.printStackTrace();
        }
        when(mockedEnvironment.acceptsProfiles(PRODUCTION_ENV)).thenReturn(true);
    }

    @Test
    public void initTest() {
        sessionServiceImpl.init();
        assertEquals(ModuleHelper.id, (int) user.getId());
    }

    @Test
    public void getCurrentUserTest() {
        when(mockedGlobalService.get(SessionService.TEMP_USER)).thenReturn(null);
        when(mockedGlobalService.getSession()).thenReturn(null);
        User result = sessionServiceImpl.getCurrentUser();
        assertNull(result);
        verify(mockedGlobalService).get(Mockito.anyString());
        when(mockedSession.getAttribute(SessionService.CURRENT_USER)).thenReturn(getASampleUser());
        when(mockedGlobalService.getSession()).thenReturn(mockedSession);
        result = sessionServiceImpl.getCurrentUser();
        assertEquals(ModuleHelper.id, (int) result.getId());
        when(mockedGlobalService.getSession()).thenReturn(null);
        when(mockedEnvironment.acceptsProfiles(PRODUCTION_ENV)).thenReturn(false);
        assertEquals(ModuleHelper.id, (int) result.getId());
        result = sessionServiceImpl.getCurrentUser();
        when(mockedEnvironment.acceptsProfiles(PRODUCTION_ENV)).thenReturn(true);
        assertNull(result);

    }

    @Test
    public void getCurrentCompanyTest() {
        when(mockedSession.getAttribute(SessionService.CURRENT_COMPANY)).thenReturn(getASampleCompany());
        Company result = sessionServiceImpl.getCurrentCompany();
        assertEquals(ModuleHelper.id, (int) result.getId());
        when(mockedSession.getAttribute(SessionService.CURRENT_COMPANY)).thenReturn(null);
        result = sessionServiceImpl.getCurrentCompany();
        assertNull(result);

    }

    @Test
    public void getCurrentProjectTest() {
        when(mockedSession.getAttribute(SessionService.CURRENT_PROJECT)).thenReturn(getASampleProject());
        Project result = sessionServiceImpl.getCurrentProject();
        assertEquals(ModuleHelper.id, (int) result.getId());
        when(mockedSession.getAttribute(SessionService.CURRENT_PROJECT)).thenReturn(null);
        result = sessionServiceImpl.getCurrentProject();
        assertNull(result);
    }

    @Test
    public void setCurrentUserTest() {
        sessionServiceImpl.setCurrentUser(getASampleUser());
        verify(mockedSession).setAttribute(Mockito.anyString(), Mockito.any(User.class));
    }

    @Test
    public void setCurrentCompanyTest() {
        sessionServiceImpl.setCurrentCompany(getASampleCompany());
        verify(mockedSession).setAttribute(Mockito.anyString(), Mockito.any(Company.class));
    }

    @Test
    public void setCurrentProjectTest() {
        sessionServiceImpl.setCurrentProject(getASampleProject());
        verify(mockedSession).setAttribute(Mockito.anyString(), Mockito.any(Project.class));
    }

    @Test
    public void removeUserInformationTest() {
        sessionServiceImpl.removeUserInformation();
        verify(mockedSession).removeAttribute(SessionService.CURRENT_COMPANY);
        verify(mockedSession).removeAttribute(SessionService.CURRENT_PROJECT);
        verify(mockedSession).removeAttribute(SessionService.CURRENT_USER);
    }

    @Test
    public void setTempUserTest() {
        User user = getASampleUser();
        sessionServiceImpl.setTempUser(user);
        verify(mockedGlobalService).set(Mockito.anyString(), Mockito.any(User.class));
    }
}
