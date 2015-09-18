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

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.onboard.domain.mapper.DepartmentMapper;
import com.onboard.domain.mapper.UserCompanyMapper;
import com.onboard.domain.mapper.model.DepartmentExample;
import com.onboard.domain.mapper.model.UserCompanyExample;
import com.onboard.domain.model.Department;
import com.onboard.domain.model.User;
import com.onboard.domain.model.UserCompany;
import com.onboard.test.moduleutils.ModuleHelper;

@RunWith(MockitoJUnitRunner.class)
public abstract class AbstractDepartmentServiceTest {

    @Mock
    protected DepartmentMapper mockedDepartmentMapper;

    @Mock
    protected UserCompanyMapper mockedUserCompayMapper;
    
    protected Department department;
    protected List<Department> departmentList;
    protected List<UserCompany> useCompanyList;
    protected List<Integer> groupIds;
    protected List<User> userList;
    
    @Before
    public void setupTest() {
        initDepartmentMapper();
        initUserCompanyMapper();
        
        groupIds = getAListOfInts();
        userList = getAListOfUsers();
    }
    
    /** initDepartmentMapper **/
    private void initDepartmentMapper() {
        department = ModuleHelper.getASampleDepartment();
        departmentList = getAListofSampleDepartments();
        
        when(mockedDepartmentMapper.selectByPrimaryKey(any(Integer.class))).thenReturn(department);
        when(mockedDepartmentMapper.selectByExample(Mockito.any(DepartmentExample.class))).thenReturn(departmentList);
        when(mockedDepartmentMapper.countByExample(Mockito.any(DepartmentExample.class))).thenReturn(ModuleHelper.count);
        when(mockedDepartmentMapper.insert(Mockito.any(Department.class))).thenReturn(0);
        when(mockedDepartmentMapper.updateByPrimaryKeySelective(Mockito.any(Department.class))).thenReturn(0);
        when(mockedDepartmentMapper.deleteByPrimaryKey(any(Integer.class))).thenReturn(0);
        
    }
    
    private List<Department> getAListofSampleDepartments() {
        List<Department> list = new ArrayList<Department>();
        list.add(ModuleHelper.getASampleDepartment());
        list.add(ModuleHelper.getASampleDepartment());
        return list;
    }
    
    /** initUserCompanyMapper **/
    private void initUserCompanyMapper() {
        
        useCompanyList = getAListOfSampaleUseCompanies();
        
        when(mockedUserCompayMapper.selectByExample(Mockito.any(UserCompanyExample.class))).thenReturn(useCompanyList);
        when(mockedUserCompayMapper.updateByExample(Mockito.any(UserCompany.class), Mockito.any(UserCompanyExample.class))).thenReturn(0);
        
    }
    
    private List<UserCompany> getAListOfSampaleUseCompanies() {
        List<UserCompany> list = new ArrayList<UserCompany>();
        list.add(ModuleHelper.getASampleUserCompany());
        list.add(ModuleHelper.getASampleUserCompany());
        return list;
    }
    
    /** **/
    private List<Integer> getAListOfInts() {
        List<Integer> list = new ArrayList<Integer>();
        for (Integer i = 1; i <= 5; ++i)
            list.add(i);
        return list;
    }
    
    private List<User> getAListOfUsers() {
        List<User> list = new ArrayList<User>();
        list.add(ModuleHelper.getASampleUser());
        list.add(ModuleHelper.getASampleUser());
        return list;
    }
}
