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

import com.onboard.domain.mapper.CompanyPrivilegeMapper;
import com.onboard.domain.mapper.model.CompanyPrivilegeExample;
import com.onboard.domain.mapper.model.common.BaseExample;
import com.onboard.domain.model.CompanyPrivilege;
import com.onboard.service.security.impl.CompanyPrivilegeServiceImpl;
import com.onboard.test.exampleutils.AbstractMatcher;
import com.onboard.test.exampleutils.CriterionVerifier;
import com.onboard.test.exampleutils.ExampleMatcher;
import com.onboard.test.exampleutils.ObjectMatcher;
import com.onboard.test.moduleutils.ModuleHelper;

@RunWith(MockitoJUnitRunner.class)
public class CompanyPrivilegeServiceImplTest {
    CompanyPrivilege companyPrivilege1;

    CompanyPrivilege companyPrivilege2;

    List<CompanyPrivilege> companyPrivileges, singleCompanyPrivileges;

    @InjectMocks
    private CompanyPrivilegeServiceImpl companyPrivilegeServiceImpl;

    @Mock
    private CompanyPrivilegeMapper companyPrivilegeMapper;

    @Before
    public void setUpBefore() throws Exception {
        initUploadMapperOPerations();
    }

    private void initUploadMapperOPerations() {
        companyPrivilege1 = getASampleCompanyPrivilege1(false);
        companyPrivilege2 = getASampleCompanyPrivilege2(true);
        companyPrivileges = getCompanyPrivilegeList();
        List<CompanyPrivilege> c = new ArrayList<CompanyPrivilege>();
        c.add(companyPrivilege1);
        singleCompanyPrivileges = c;
        when(companyPrivilegeMapper.selectByPrimaryKey(ModuleHelper.id)).thenReturn(companyPrivilege1);
        when(companyPrivilegeMapper.insert(Mockito.any(CompanyPrivilege.class))).thenReturn(1);
        /*
        when(companyPrivilegeMapper.selectByExample(Matchers.argThat(new ExampleMatcher<CompanyPrivilegeExample>() {
            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "userId", ModuleHelper.userId)
                        && CriterionVerifier.verifyEqualTo(example, "companyId", ModuleHelper.companyId);
            }
        }))).thenReturn(c);

        when(companyPrivilegeMapper.selectByExample(Matchers.argThat(new ExampleMatcher<CompanyPrivilegeExample>() {
            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "userId", null);
            }
        }))).thenReturn(getCompanyPrivilegeList());
         */
        when(companyPrivilegeMapper.countByExample(any(CompanyPrivilegeExample.class))).thenReturn(
                getCompanyPrivilegeList().size());
        /*
        when(companyPrivilegeServiceImpl.createCompanyPrivilege(any(CompanyPrivilege.class))).thenReturn(
                companyPrivilege2);
                */
    }

    private CompanyPrivilege getASampleCompanyPrivilege1(Boolean isAdmin) {
        CompanyPrivilege companyPrivilege = new CompanyPrivilege();
        companyPrivilege.setId(ModuleHelper.id);
        companyPrivilege.setUserId(ModuleHelper.userId);
        companyPrivilege.setCompanyId(ModuleHelper.companyId);
        companyPrivilege.setCanCreateProject(true);
        companyPrivilege.setIsAdmin(isAdmin);
        return companyPrivilege;
    }

    private CompanyPrivilege getASampleCompanyPrivilege2(Boolean isAdmin) {
        CompanyPrivilege companyPrivilege = new CompanyPrivilege();
        companyPrivilege.setId(ModuleHelper.id + 1);
        companyPrivilege.setUserId(ModuleHelper.userId + 1);
        companyPrivilege.setCompanyId(ModuleHelper.companyId + 1);
        companyPrivilege.setCanCreateProject(true);
        companyPrivilege.setIsAdmin(isAdmin);
        return companyPrivilege;
    }

    private List<CompanyPrivilege> getCompanyPrivilegeList() {
        List<CompanyPrivilege> companyPrivileges = new ArrayList<CompanyPrivilege>();
        companyPrivileges.add(companyPrivilege1);
        companyPrivileges.add(companyPrivilege2);
        return companyPrivileges;

    }

    private void runCommonEquals(CompanyPrivilege companyPrivilege, Boolean isAdmin) {
        assertEquals((int) companyPrivilege.getId(), ModuleHelper.id);
        assertEquals((int) companyPrivilege.getCompanyId(), ModuleHelper.companyId);
        assertEquals((int) companyPrivilege.getUserId(), ModuleHelper.userId);
        assertEquals(companyPrivilege.getCanCreateProject(), true);
        assertEquals(companyPrivilege.getIsAdmin(), isAdmin);
    }

    @Test
    public void testGetCompanyPrivilegeById() {
        CompanyPrivilege companyPrivilege = companyPrivilegeServiceImpl.getCompanyPrivilegeById(ModuleHelper.id);
        verify(companyPrivilegeMapper, times(1)).selectByPrimaryKey(ModuleHelper.id);
        Mockito.verifyNoMoreInteractions(companyPrivilegeMapper);
        runCommonEquals(companyPrivilege, false);
    }

    @Test
    public void testGetCompanyPrivilegesByExample() {
    	
    	when(companyPrivilegeMapper.selectByExample(Mockito.any(CompanyPrivilegeExample.class))).thenReturn(getCompanyPrivilegeList());
    	
        List<CompanyPrivilege> companyPrivileges = companyPrivilegeServiceImpl.getCompanyPrivilegesByExample(
                companyPrivilege1, ModuleHelper.start, ModuleHelper.limit);
        verify(companyPrivilegeMapper).selectByExample(Matchers.argThat(new ExampleMatcher<CompanyPrivilegeExample>() {

            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "companyId", ModuleHelper.companyId);
            }
        }));
        assertEquals(companyPrivileges.size(), 2);
        assertEquals(companyPrivileges.get(0), companyPrivilege1);
    }

    @Test
    public void testCountByExample() {
        int count = companyPrivilegeServiceImpl.countByExample(new CompanyPrivilege());
        verify(companyPrivilegeMapper).countByExample(Matchers.argThat(new ExampleMatcher<CompanyPrivilegeExample>() {
            @Override
            public boolean matches(BaseExample example) {
                return true;
            }
        }));
        assertEquals(count, 2);
    }

    @Test
    public void testCreateCompanyPrivilege() {
        CompanyPrivilege newCompanyPrivilege = new CompanyPrivilege(companyPrivilege1);
        newCompanyPrivilege.setId(null);
        newCompanyPrivilege = companyPrivilegeServiceImpl.createCompanyPrivilege(newCompanyPrivilege);
        verify(companyPrivilegeMapper).insert(argThat(new AbstractMatcher<CompanyPrivilege>() {
            @Override
            public boolean matches(Object arg0) {
                CompanyPrivilege c = (CompanyPrivilege) arg0;
                return c.getCanCreateProject() != null && c.getIsAdmin() != null
                        && c.getUserId().equals(companyPrivilege1.getUserId())
                        && c.getCompanyId().equals(companyPrivilege1.getCompanyId())
                        && c.getCanCreateProject().equals(true) && c.getIsAdmin().equals(false);
            }
        }));
    }

    @Test
    public void testUpdateCompanyPrivilege() {
        CompanyPrivilege c = new CompanyPrivilege(companyPrivilege1);
        final Integer newUserId = 2;
        c.setUserId(newUserId);

        companyPrivilegeServiceImpl.updateCompanyPrivilege(c);
        verify(companyPrivilegeMapper).updateByPrimaryKey(argThat(new ObjectMatcher<CompanyPrivilege>() {

            @Override
            public boolean verifymatches(CompanyPrivilege item) {
                return item.getId().equals(companyPrivilege1.getId()) && item.getUserId().equals(newUserId);
            }
        }));
    }

    @Test
    public void testSetCompanyPrivilege() {
        CompanyPrivilege c = new CompanyPrivilege(companyPrivilege1);
        final Integer newUserId = 2;
        c.setUserId(newUserId);
        companyPrivilegeServiceImpl.updateCompanyPrivilege(c);
        verify(companyPrivilegeMapper).updateByPrimaryKey(argThat(new ObjectMatcher<CompanyPrivilege>() {
            @Override
            public boolean verifymatches(CompanyPrivilege item) {
                return item.getId().equals(companyPrivilege1.getId()) && item.getUserId().equals(newUserId);
            }
        }));
    }

    @Test
    public void testGetOrCreateCompanyPrivilegeByUserId() {
    	when(companyPrivilegeMapper.selectByExample(Mockito.any(CompanyPrivilegeExample.class))).thenReturn(singleCompanyPrivileges);
    	
        companyPrivilegeServiceImpl.getOrCreateCompanyPrivilegeByUserId(ModuleHelper.companyId, ModuleHelper.userId);
        verify(companyPrivilegeMapper).selectByExample(Matchers.argThat(new ExampleMatcher<CompanyPrivilegeExample>() {
            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "companyId", ModuleHelper.companyId)
                        && CriterionVerifier.verifyEqualTo(example, "userId", ModuleHelper.userId);
            }
        }));

        CompanyPrivilege c = companyPrivilegeServiceImpl.getOrCreateCompanyPrivilegeByUserId(
                ModuleHelper.companyId + 1, ModuleHelper.userId + 1);
        verify(companyPrivilegeMapper).selectByExample(Matchers.argThat(new ExampleMatcher<CompanyPrivilegeExample>() {
            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "companyId", ModuleHelper.companyId + 1)
                        && CriterionVerifier.verifyEqualTo(example, "userId", ModuleHelper.userId + 1);
            }
        }));
        assertEquals((int) c.getCompanyId(), ModuleHelper.companyId);
        assertEquals((int) c.getUserId(), ModuleHelper.userId);

    }

    @Test
    public void testDeleteCompanyPrivilege() {
        companyPrivilegeServiceImpl.deleteCompanyPrivilege(ModuleHelper.id);
        verify(companyPrivilegeMapper, times(1)).deleteByPrimaryKey(Matchers.argThat(new ObjectMatcher<Integer>() {
            @Override
            public boolean verifymatches(Integer id) {
                return id.equals(ModuleHelper.id);
            }
        }));
        Mockito.verifyNoMoreInteractions(companyPrivilegeMapper);
    }
}
