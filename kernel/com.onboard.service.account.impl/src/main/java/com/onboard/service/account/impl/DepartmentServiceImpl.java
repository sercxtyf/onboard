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
package com.onboard.service.account.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.onboard.domain.mapper.DepartmentMapper;
import com.onboard.domain.mapper.UserCompanyMapper;
import com.onboard.domain.mapper.base.BaseMapper;
import com.onboard.domain.mapper.model.DepartmentExample;
import com.onboard.domain.mapper.model.UserCompanyExample;
import com.onboard.domain.model.Department;
import com.onboard.domain.model.User;
import com.onboard.domain.model.UserCompany;
import com.onboard.service.account.DepartmentService;
import com.onboard.service.base.AbstractBaseService;

/**
 * {@link com.onboard.service.account.DepartmentService} Service implementation
 * 
 * @generated_by_elevenframework
 */
@Transactional
@Service("departmentServiceBean")
public class DepartmentServiceImpl extends AbstractBaseService<Department, DepartmentExample> implements DepartmentService {

    @Autowired
    private DepartmentMapper departmentMapper;

    @Autowired
    private UserCompanyMapper userCompayMapper;

    @Override
    public void delete(int id) {
        UserCompany userCompany = new UserCompany();
        userCompany.setDepartmentId(id);
        List<UserCompany> useCompanyList = userCompayMapper.selectByExample(new UserCompanyExample(userCompany));
        // 删除分组的话，此分组内所有成员的分组设为null
        for (UserCompany item : useCompanyList) {
            item.setDepartmentId(null);
            userCompayMapper.updateByExample(item, new UserCompanyExample(item));
        }
        departmentMapper.deleteByPrimaryKey(id);
    }

    @Override
    public void updateDepartmentOfUser(UserCompany userCompany) {
        Integer departmentId = userCompany.getDepartmentId();
        userCompany.setDepartmentId(null);
        UserCompanyExample example = new UserCompanyExample(userCompany);
        userCompany.setDepartmentId(departmentId);
        userCompany.setId(userCompayMapper.selectByExample(example).get(0).getId());
        userCompayMapper.updateByExample(userCompany, example);
    }

    @Override
    public void sortDepartment(List<Integer> departmentIds) {
        Department department = new Department();
        for (int i = 0; i < departmentIds.size(); i++) {
            department.setId(departmentIds.get(i));
            department.setCustomOrder(i);
            departmentMapper.updateByPrimaryKeySelective(department);
        }
    }

    @Override
    public Department getDepartmentByCompanyIdByUserId(int companyId, int userId) {
        UserCompany userCompany = new UserCompany();
        userCompany.setCompanyId(companyId);
        userCompany.setUserId(userId);
        List<UserCompany> result = userCompayMapper.selectByExample(new UserCompanyExample(userCompany));

        return result.get(0).getDepartmentId() == null ? null : departmentMapper.selectByPrimaryKey(result.get(0).getDepartmentId());
    }

    @Override
    public void fillUserDepartmentInCompany(User user, int companyId) {
        Department department = this.getDepartmentByCompanyIdByUserId(companyId, user.getId());
        user.setDepartmentId(department == null ? 0 : department.getId());
    }

    @Override
    public void fillUsersDepartmentInCompany(List<User> users, int companyId) {
        for (User user : users) {
            this.fillUserDepartmentInCompany(user, companyId);
        }
    }

    @Override
    protected BaseMapper<Department, DepartmentExample> getBaseMapper() {
        return departmentMapper;
    }

    @Override
    public Department newItem() {
        return new Department();
    }

    @Override
    public DepartmentExample newExample() {
        return new DepartmentExample();
    }

    @Override
    public DepartmentExample newExample(Department item) {
        return new DepartmentExample(item);
    }

}
