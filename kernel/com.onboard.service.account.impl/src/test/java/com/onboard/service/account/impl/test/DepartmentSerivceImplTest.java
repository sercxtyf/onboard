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

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;

import com.onboard.domain.mapper.model.DepartmentExample;
import com.onboard.domain.mapper.model.UserCompanyExample;
import com.onboard.domain.mapper.model.common.BaseExample;
import com.onboard.domain.model.Department;
import com.onboard.domain.model.User;
import com.onboard.domain.model.UserCompany;
import com.onboard.service.account.impl.DepartmentServiceImpl;
import com.onboard.test.exampleutils.CriterionVerifier;
import com.onboard.test.exampleutils.ExampleMatcher;
import com.onboard.test.exampleutils.ObjectMatcher;
import com.onboard.test.moduleutils.ModuleHelper;

public class DepartmentSerivceImplTest extends AbstractDepartmentServiceTest {
    
    @InjectMocks
    private DepartmentServiceImpl departmentServiceImpl;
    
    @Test
    public void testGetDepartmentById() {
        Department retDepartment = departmentServiceImpl.getById(ModuleHelper.departmentId);
        
        // verify function
        verify(mockedDepartmentMapper, times(1)).selectByPrimaryKey(any(Integer.class));
        
        // check data
        assertSame(retDepartment, department);
    }
    
    @Test
    public void testGetDepartments() {
        List<Department> retDepartments = departmentServiceImpl.getAll(ModuleHelper.start, ModuleHelper.limit);
        
        // verify function
        verify(mockedDepartmentMapper, times(1)).selectByExample(Mockito.argThat(new ExampleMatcher<DepartmentExample>() {

            @Override
            public boolean matches(BaseExample example) {
                return example.getStart() == ModuleHelper.start && example.getLimit() == ModuleHelper.limit;
            }
        }));
        
        // check data
        assertEquals(2, retDepartments.size());
    }
    
    @Test
    public void testGetDepartmentsByExample() {
        List<Department> retDepartments = departmentServiceImpl.getBySample(ModuleHelper.getASampleDepartment(), ModuleHelper.start, ModuleHelper.limit);
        
        // verify function
        verify(mockedDepartmentMapper, times(1)).selectByExample(Mockito.argThat(new ExampleMatcher<DepartmentExample>() {

            @Override
            public boolean matches(BaseExample example) {
                return example.getStart() == ModuleHelper.start && example.getLimit() == ModuleHelper.limit;
            }
        }));
        
        // check data
        assertEquals(2, retDepartments.size());
    }
    
    @Test
    public void testCountByExample() {
        int retInt = departmentServiceImpl.countBySample(ModuleHelper.getASampleDepartment());
        
        // verify function
        verify(mockedDepartmentMapper, times(1)).countByExample(Mockito.any(DepartmentExample.class));
        
        // check data
        assertSame(ModuleHelper.count, retInt);
    }
    
    @Test
    public void testCreateDepartment() {
        Department retDepartment = departmentServiceImpl.create(department);
        
        // verify function
        verify(mockedDepartmentMapper, times(1)).insert(Mockito.any(Department.class));
        
        // check data
        assertEquals(department, retDepartment);
    }
    
    @Test
    public void testUpdateDepartment() {
        Department retDepartment = departmentServiceImpl.updateSelective(department);
        
        // verify function
        verify(mockedDepartmentMapper, times(1)).updateByPrimaryKeySelective(Mockito.any(Department.class));
        
        // check data
        assertSame(department, retDepartment);
    }
    
    @Test
    public void testDeleteDepartment() {
        departmentServiceImpl.delete(ModuleHelper.id);
        
        // verify function
        verify(mockedDepartmentMapper, times(1)).deleteByPrimaryKey(any(Integer.class));
        verify(mockedUserCompayMapper, times(1)).selectByExample(Mockito.any(UserCompanyExample.class));
        verify(mockedUserCompayMapper, times(2)).updateByExample(Mockito.any(UserCompany.class), Mockito.any(UserCompanyExample.class));
        
        // check data
    }
    
    @Test
    public void testUpdateDepartmentOfUser() {
        departmentServiceImpl.updateDepartmentOfUser(ModuleHelper.getASampleUserCompany());

        // verify function
        verify(mockedUserCompayMapper, times(1)).selectByExample(Mockito.any(UserCompanyExample.class));
        verify(mockedUserCompayMapper, times(1)).updateByExample(Mockito.argThat(new ObjectMatcher<UserCompany>() {
            @Override
            public boolean verifymatches(UserCompany item) {
                // TODO Auto-generated method stub
                return item.getId().equals(ModuleHelper.userCompanyId);
            }
        }), Mockito.any(UserCompanyExample.class));
    }
    
    @Test
    public void testSortDepartment() {
        departmentServiceImpl.sortDepartment(groupIds);

        verify(mockedDepartmentMapper, times(5)).updateByPrimaryKeySelective(Mockito.any(Department.class));
    }
    
    @Test
    public void testGetDepartmentByCompanyIdByUserId() {
        Department retDepartment = departmentServiceImpl.getDepartmentByCompanyIdByUserId(ModuleHelper.companyId, ModuleHelper.userId);
        

        verify(mockedUserCompayMapper, times(1)).selectByExample(Mockito.argThat(new ExampleMatcher<UserCompanyExample>() {
            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "companyId", ModuleHelper.companyId) && CriterionVerifier.verifyEqualTo(example, "userId", ModuleHelper.userId);
            }
        }));

        assertSame(department, retDepartment);
    }
    
    @Test 
    public void testFillUserDepartmentInCompany() {
        User user = ModuleHelper.getASampleUser();
        departmentServiceImpl.fillUserDepartmentInCompany(user, ModuleHelper.companyId);
        
        assertEquals(user.getDepartmentId(), department.getId());
    }
    
    @Test 
    public void testFillUsersDepartmentInCompany() {
        departmentServiceImpl.fillUsersDepartmentInCompany(userList, ModuleHelper.companyId);
        
        assertEquals(userList.get(0).getDepartmentId(), department.getId());
        assertEquals(userList.get(1).getDepartmentId(), department.getId());
    }
    
}
