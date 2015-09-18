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
package com.onboard.service.collaboration.impl.abstractfiles;

import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.Lists;
import com.onboard.domain.mapper.CompanyApplicationMapper;
import com.onboard.domain.mapper.model.CompanyApplicationExample;
import com.onboard.domain.model.CompanyApplication;
import com.onboard.test.moduleutils.ModuleHelper;

@RunWith(MockitoJUnitRunner.class)
public abstract class AbstractCompanyApplicationTest {
    @Mock
    protected CompanyApplicationMapper mockedCompanyApplicationMapper;

    protected CompanyApplication companyApplication;
    protected List<CompanyApplication> companyApplications;
    protected CompanyApplicationExample companyApplicationExample;

    public static int mapperReturnValue = 1;

    @Before
    public void setupCompanyApplicationTest() {
        initCompanyApplicationMapper();
    }

    private void initCompanyApplicationMapper() {
        companyApplication = getASampleCompanyApplication();
        companyApplications = getAListOfOneSampleCompanyApplication();
        companyApplicationExample = getASampleCompanyApplicationExample();

        when(mockedCompanyApplicationMapper.countByExample(Mockito.any(CompanyApplicationExample.class))).thenReturn(
                ModuleHelper.count);

        when(mockedCompanyApplicationMapper.deleteByExample(Mockito.any(CompanyApplicationExample.class))).thenReturn(
                mapperReturnValue);
        when(mockedCompanyApplicationMapper.deleteByPrimaryKey(ModuleHelper.id)).thenReturn(mapperReturnValue);

        when(mockedCompanyApplicationMapper.insert(Mockito.any(CompanyApplication.class))).thenReturn(mapperReturnValue);
        when(mockedCompanyApplicationMapper.insertSelective(Mockito.any(CompanyApplication.class))).thenReturn(mapperReturnValue);

        when(mockedCompanyApplicationMapper.selectByExample(Mockito.any(CompanyApplicationExample.class))).thenReturn(
                companyApplications);
        when(mockedCompanyApplicationMapper.selectByPrimaryKey(ModuleHelper.id)).thenReturn(companyApplication);

        when(
                mockedCompanyApplicationMapper.updateByExample(Mockito.any(CompanyApplication.class),
                        Mockito.any(CompanyApplicationExample.class))).thenReturn(mapperReturnValue);
        when(
                mockedCompanyApplicationMapper.updateByExampleSelective(Mockito.any(CompanyApplication.class),
                        Mockito.any(CompanyApplicationExample.class))).thenReturn(mapperReturnValue);
        when(mockedCompanyApplicationMapper.updateByPrimaryKey(Mockito.any(CompanyApplication.class))).thenReturn(
                mapperReturnValue);
        when(mockedCompanyApplicationMapper.updateByPrimaryKeySelective(Mockito.any(CompanyApplication.class))).thenReturn(
                mapperReturnValue);

    }

    public static CompanyApplication getASampleCompanyApplication() {
        CompanyApplication companyApplication = new CompanyApplication();
        companyApplication.setCode(ModuleHelper.code);
        companyApplication.setCodeHost(ModuleHelper.codeHost);
        companyApplication.setContactEmail(ModuleHelper.contectEmail);
        companyApplication.setContactName(ModuleHelper.contectName);
        companyApplication.setDescription(ModuleHelper.description);
        companyApplication.setId(ModuleHelper.id);
        companyApplication.setTeamName(ModuleHelper.teamName);
        companyApplication.setTeamSize(ModuleHelper.teamSize);
        return companyApplication;
    }

    public static List<CompanyApplication> getAListOfTwoSampleCompanyApplication() {
        return Lists.newArrayList(getASampleCompanyApplication(), getASampleCompanyApplication());
    }

    public static List<CompanyApplication> getAListOfOneSampleCompanyApplication() {
        return Lists.newArrayList(getASampleCompanyApplication());
    }

    public static CompanyApplicationExample getASampleCompanyApplicationExample() {
        return new CompanyApplicationExample(getASampleCompanyApplication());
    }

}
