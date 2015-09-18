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
/**
 * 
 */
package com.onboard.service.account.impl.test;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.Lists;
import com.onboard.domain.mapper.CompanyMapper;
import com.onboard.domain.mapper.ProjectMapper;
import com.onboard.domain.mapper.UserCompanyMapper;
import com.onboard.domain.mapper.UserProjectMapper;
import com.onboard.domain.mapper.model.CompanyExample;
import com.onboard.domain.mapper.model.ProjectExample;
import com.onboard.domain.mapper.model.UserCompanyExample;
import com.onboard.domain.mapper.model.UserProjectExample;
import com.onboard.domain.mapper.model.common.BaseExample;
import com.onboard.domain.model.Company;
import com.onboard.domain.model.Project;
import com.onboard.domain.model.User;
import com.onboard.domain.model.UserCompany;
import com.onboard.service.account.AccountService;
import com.onboard.service.account.UserService;
import com.onboard.service.account.impl.CompanyServiceImpl;
import com.onboard.service.web.SessionService;
import com.onboard.test.exampleutils.CriterionVerifier;
import com.onboard.test.exampleutils.ExampleMatcher;

/**
 * @author XingLiang
 * 
 */
@RunWith(MockitoJUnitRunner.class)
public class CompanyServiceImplTest {

    @Mock
    private CompanyMapper mockCompanyMapper;

    @Mock
    private UserCompanyMapper mockUserCompanyMapper;

    @Mock
    private UserProjectMapper mockUserProjectMapper;

    @Mock
    private ProjectMapper mockProjectMapper;

    @Mock
    private UserService mockUserService;

    @Mock
    private AccountService mockAccountService;

    @Mock
    private SessionService mockSessionService;

    @InjectMocks
    private CompanyServiceImpl companyService;

    private final Integer ID = 1;

    private final String NAME = "serc";

    private final String DESCRIPTION = "sercxtyf";

    private final Integer USER_COUNT = 1;

    private final Date CREATE_DATE = new Date(Long.parseLong("1355155200683")); // 2012-Dec-11

    private final String EMAIL_ADDRESS_1 = "xl5555123@gmail.com";

    private final String EMAIL_ADDRESS_2 = "12345@fdsf.net";// need to be invited.

    private Company sampleCompany;

    private User sampleUser;

    private Project sampleProject;

    private List<Company> companyList;

    private List<Project> projectList;

    private List<UserCompany> userCompanyList;

    private List<String> emailAddresses;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        this.sampleCompany = this.getASampleNotDeletedCompany();
        this.sampleUser = this.getASampleUser();
        this.sampleProject = this.getASampleProject();
        this.userCompanyList = getAListOfUserCompany();
        this.companyList = getASampleCompanyList();
        this.projectList = getSampleProjectList();
        this.emailAddresses = getASampleEmailAddresses();

        when(mockCompanyMapper.selectByPrimaryKey(ID)).thenReturn(sampleCompany);
        when(mockCompanyMapper.selectByExample(any(CompanyExample.class))).thenReturn(companyList);
        when(mockUserCompanyMapper.selectByExample(any(UserCompanyExample.class))).thenReturn(userCompanyList);
        when(mockProjectMapper.selectByPrimaryKey(ID)).thenReturn(sampleProject);
        when(mockProjectMapper.selectByExample(any(ProjectExample.class))).thenReturn(projectList);

        when(mockUserCompanyMapper.countByExample(any(UserCompanyExample.class))).thenReturn(USER_COUNT);

        when(mockUserService.getById(ID)).thenReturn(sampleUser);
        when(mockSessionService.getCurrentUser()).thenReturn(sampleUser);
        when(mockUserService.getUserByEmail(EMAIL_ADDRESS_1)).thenReturn(sampleUser);
        when(mockUserService.getUserByEmail(EMAIL_ADDRESS_2)).thenReturn(null);

        when(mockCompanyMapper.insert(any(Company.class))).thenReturn(1);
        when(mockUserCompanyMapper.insert(any(UserCompany.class))).thenReturn(1);

        when(mockUserCompanyMapper.deleteByExample(any(UserCompanyExample.class))).thenReturn(1);
        when(mockUserProjectMapper.deleteByExample(any(UserProjectExample.class))).thenReturn(1);

        when(mockCompanyMapper.updateByPrimaryKeySelective(any(Company.class))).thenReturn(1);

        doNothing().when(mockAccountService).sendInvitation(any(Integer.class), any(String.class));
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    private Company getASampleNotDeletedCompany() {
        Company sample = new Company();
        sample.setCreated(new Date());
        sample.setId(ID);
        sample.setName(NAME);
        sample.setDeleted(false);
        sample.setCreatorId(ID);

        return sample;
    }

    private Company getASampleDeletedCompany() {
        Company sample = new Company();
        sample.setCreated(new Date());
        sample.setId(ID);
        sample.setName(NAME);
        sample.setDeleted(true);
        sample.setCreatorId(ID);

        return sample;
    }

    private List<String> getASampleEmailAddresses() {
        List<String> emailAddresses = Lists.newArrayList();
        emailAddresses.add(EMAIL_ADDRESS_1);
        emailAddresses.add(EMAIL_ADDRESS_2);

        return emailAddresses;
    }

    private List<Company> getASampleCompanyList() {
        companyList = Lists.newArrayList();
        companyList.add(getASampleNotDeletedCompany());

        return companyList;
    }

    private UserCompany getASampleUserCompany(int userId, int companyId) {
        UserCompany userCompany = new UserCompany();
        userCompany.setUserId(userId);
        userCompany.setCompanyId(companyId);
        userCompany.setId(ID);

        return userCompany;
    }

    private List<UserCompany> getAListOfUserCompany() {
        List<UserCompany> userCompanies = Lists.newArrayList();
        userCompanies.add(getASampleUserCompany(ID, ID));
        userCompanies.add(getASampleUserCompany(ID, ID + 1));

        return userCompanies;

    }

    private Project getASampleProject() {
        Project project = new Project();
        project.setId(ID);
        project.setCreated(CREATE_DATE);
        project.setDeleted(false);
        project.setName(NAME);
        project.setDescription(DESCRIPTION);
        return project;
    }

    private List<Project> getSampleProjectList() {
        List<Project> projects = Lists.newArrayList();
        projects.add(getASampleProject());

        return projects;
    }

    private User getASampleUser() {
        User user = new User();
        user.setId(ID);
        user.setDescription(DESCRIPTION);
        user.setCreated(CREATE_DATE);
        user.setName(NAME);

        return user;
    }

    /**
     * Test method for {@link com.onboard.service.account.impl.CompanyServiceImpl#getCompaniesByUserId(int)}.
     */
    @Ignore
    public void testGetCompaniesByUserId() {
        when(mockCompanyMapper.selectByPrimaryKey(ID + 1)).thenReturn(this.getASampleDeletedCompany());
        List<Company> retCompanyList = companyService.getCompaniesByUserId(ID);
        verify(mockUserCompanyMapper).selectByExample(argThat(new ExampleMatcher<UserCompanyExample>() {

            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "userId", ID);
            }

        }));

        verify(mockCompanyMapper).selectByPrimaryKey(ID);
        verify(mockCompanyMapper).selectByPrimaryKey(ID + 1);

        Assert.assertEquals(companyList.size(), retCompanyList.size());
        Assert.assertEquals(companyList.get(0).getName(), retCompanyList.get(0).getName());
        Assert.assertEquals(companyList.get(0).getCreated(), retCompanyList.get(0).getCreated());
        Assert.assertEquals(companyList.get(0).getId(), retCompanyList.get(0).getId());
    }

    /**
     * Test method for {@link com.onboard.service.account.impl.CompanyServiceImpl#getAll()}.
     */
    @Test
    public void testGetAllCompanies() {
        List<Company> retCompanyList = companyService.getAll();

        verify(mockCompanyMapper).selectByExample(any(CompanyExample.class));

        Assert.assertEquals(companyList.size(), retCompanyList.size());
    }

    /**
     * Test method for {@link com.onboard.service.account.impl.CompanyServiceImpl#create(com.onboard.domain.model.Company)}
     * .
     */
    @Test
    public void testCreateCompany() {
        Company retCompany = companyService.create(sampleCompany);
        verify(mockCompanyMapper).insertSelective(any(Company.class));
        verify(mockUserCompanyMapper).insert(any(UserCompany.class));

        Assert.assertFalse(retCompany.getDeleted());
        Assert.assertEquals(sampleCompany.getCreated(), retCompany.getCreated());
        Assert.assertEquals(sampleCompany.getUpdated(), retCompany.getUpdated());
    }

    /**
     * Test method for {@link com.onboard.service.account.impl.CompanyServiceImpl#getById(int)}.
     */
    @Test
    public void testGetCompanyById() {
        Company company = companyService.getById(ID);
        verify(mockCompanyMapper).selectByPrimaryKey(ID);
        assertSame(sampleCompany, company);
    }

    /**
     * Test method for
     * {@link com.onboard.service.account.impl.CompanyServiceImpl#removeUser(java.lang.Integer, java.lang.Integer)}.
     */
    @Test
    public void testRemoveUser() {
        companyService.removeUser(ID, ID);
        verify(mockUserCompanyMapper).deleteByExample(argThat(new ExampleMatcher<UserCompanyExample>() {

            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "companyId", ID)
                        && CriterionVerifier.verifyEqualTo(example, "userId", ID);
            }

        }));
        verify(mockUserProjectMapper).deleteByExample(argThat(new ExampleMatcher<UserProjectExample>() {

            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "companyId", ID)
                        && CriterionVerifier.verifyEqualTo(example, "userId", ID);
            }

        }));
    }

    /**
     * Test method for
     * {@link com.onboard.service.account.impl.CompanyServiceImpl#containsUser(java.lang.Integer, java.lang.Integer)}.
     */
    @Test
    public void testContainsUser() {
        boolean retIfContainUser = companyService.containsUser(ID, ID);
        verify(mockUserCompanyMapper).countByExample(argThat(new ExampleMatcher<UserCompanyExample>() {

            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "companyId", ID)
                        && CriterionVerifier.verifyEqualTo(example, "userId", ID);
            }

        }));

        Assert.assertTrue(retIfContainUser);

        when(mockUserCompanyMapper.countByExample(any(UserCompanyExample.class))).thenReturn(0);
        retIfContainUser = companyService.containsUser(ID, ID);
        Assert.assertFalse(retIfContainUser);
    }

    /**
     * Test method for {@link com.onboard.service.account.impl.CompanyServiceImpl#updateSelective(com.onboard.domain.model.Company)}
     * .
     */
    @Test
    public void testUpdateCompany() {
        Company nullIdCompany = new Company();
        Company result = companyService.updateSelective(nullIdCompany);
        assertNull(result);
        companyService.updateSelective(sampleCompany);
        verify(mockCompanyMapper).updateByPrimaryKeySelective(any(Company.class));
    }

}
