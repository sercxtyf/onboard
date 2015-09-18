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

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;

import com.onboard.domain.mapper.model.CompanyApplicationExample;
import com.onboard.domain.mapper.model.common.BaseExample;
import com.onboard.domain.model.CompanyApplication;
import com.onboard.service.collaboration.impl.CompanyApplicationServiceImpl;
import com.onboard.service.collaboration.impl.abstractfiles.AbstractCompanyApplicationTest;
import com.onboard.test.exampleutils.CriterionVerifier;
import com.onboard.test.exampleutils.ExampleMatcher;
import com.onboard.test.exampleutils.ObjectMatcher;
import com.onboard.test.moduleutils.ModuleHelper;

public class CompanyApplicationServiceImplTest extends AbstractCompanyApplicationTest {

    @InjectMocks
    private CompanyApplicationServiceImpl companyApplicationServiceImpl;

    private void runAsserts(CompanyApplication companyApplication) {
        assertEquals(ModuleHelper.id, (int) companyApplication.getId());
        assertEquals(ModuleHelper.code, companyApplication.getCode());
        assertEquals(ModuleHelper.codeHost, companyApplication.getCodeHost());
        assertEquals(ModuleHelper.contectEmail, companyApplication.getContactEmail());
        assertEquals(ModuleHelper.contectName, companyApplication.getContactName());
        assertEquals(ModuleHelper.description, companyApplication.getDescription());
        assertEquals(ModuleHelper.teamName, companyApplication.getTeamName());
        assertEquals(ModuleHelper.teamSize, companyApplication.getTeamSize());
    }

    private void runAsserts(List<CompanyApplication> companyApplications) {
        for (CompanyApplication companyApplication : companyApplications) {
            runAsserts(companyApplication);
        }
    }

    @Test
    public void testGetCompanyApplicationById() {
        CompanyApplication companyApplication = companyApplicationServiceImpl.getCompanyApplicationById(ModuleHelper.id);
        verify(mockedCompanyApplicationMapper, times(1)).selectByPrimaryKey(ModuleHelper.id);
        Mockito.verifyNoMoreInteractions(mockedCompanyApplicationMapper);
        runAsserts(companyApplication);
    }

    @Test
    public void testGetCompanyApplicationByToken() {
        CompanyApplication companyApplication = companyApplicationServiceImpl.getCompanyApplicationByToken(ModuleHelper.code);
        verify(mockedCompanyApplicationMapper, times(1)).selectByExample(
                Mockito.argThat(new ExampleMatcher<CompanyApplicationExample>() {

                    @Override
                    public boolean matches(BaseExample example) {
                        return CriterionVerifier.verifyEqualTo(example, "code", ModuleHelper.code);
                    }
                }));
        Mockito.verifyNoMoreInteractions(mockedCompanyApplicationMapper);
        runAsserts(companyApplication);
    }

    @Test
    public void testgetCompanyApplications() {
        List<CompanyApplication> companyApplications = companyApplicationServiceImpl.getCompanyApplications(ModuleHelper.start,
                ModuleHelper.limit);
        verify(mockedCompanyApplicationMapper, times(1)).selectByExample(
                Mockito.argThat(new ExampleMatcher<CompanyApplicationExample>() {

                    @Override
                    public boolean matches(BaseExample example) {
                        return CriterionVerifier.verifyLimit(example, ModuleHelper.limit)
                                && CriterionVerifier.verifyStart(example, ModuleHelper.start);
                    }
                }));
        Mockito.verifyNoMoreInteractions(mockedCompanyApplicationMapper);
        runAsserts(companyApplications);
    }

    @Test
    public void testCreateCompanyApplication() {
        CompanyApplication returnedCollection = companyApplicationServiceImpl.createCompanyApplication(companyApplication);
        verify(mockedCompanyApplicationMapper, times(1)).insert(Mockito.argThat(new ObjectMatcher<CompanyApplication>() {

            @Override
            public boolean verifymatches(CompanyApplication item) {
                return item.equals(companyApplication);
            }
        }));
        runAsserts(returnedCollection);
    }

    @Test
    public void testDeleteCompanyApplication() {
        companyApplicationServiceImpl.deleteCompanyApplication(ModuleHelper.id);
        verify(mockedCompanyApplicationMapper, times(1)).deleteByPrimaryKey(ModuleHelper.id);
    }

    @Test
    public void testDisableCompanyApplicationToken() {
        companyApplicationServiceImpl.disableCompanyApplicationToken(ModuleHelper.id);
        verify(mockedCompanyApplicationMapper, times(1)).updateByPrimaryKey(
                Mockito.argThat(new ObjectMatcher<CompanyApplication>() {

                    @Override
                    public boolean verifymatches(CompanyApplication item) {
                        return item.getCode() == null;
                    }
                }));
    }
}
