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
package com.onboard.web.api.departmnet;

/**
 * @author xuchen
 *
 */

import org.elevenframework.web.exception.ResourceNotFoundException;
import org.elevenframework.web.interceptor.Interceptors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.onboard.domain.model.Department;
import com.onboard.domain.model.UserCompany;
import com.onboard.service.account.DepartmentService;
import com.onboard.service.security.exception.NoPermissionException;
import com.onboard.service.security.interceptors.CompanyAdminRequired;

@RequestMapping("/{companyId}/group")
@Controller
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

    /**
     * 创建分组
     */
    @RequestMapping(value = "", method = RequestMethod.POST)
    @Interceptors({ CompanyAdminRequired.class })
    @ResponseBody
    public ResponseEntity<Department> createDepartment(@PathVariable("companyId") int companyId,
            @RequestBody Department department) {
        department.setCompanyId(companyId);

        return new ResponseEntity<Department>(departmentService.create(department), HttpStatus.CREATED);
    }

    /**
     * 
     * @param companyId
     * @param groupId
     * @param group
     * @param sort
     *            为true时仅仅对分组进行排序
     * @return
     */
    @RequestMapping(value = "/{groupId}", method = RequestMethod.PUT)
    @Interceptors({ CompanyAdminRequired.class })
    public ResponseEntity<Department> updateDepartment(@PathVariable("companyId") int companyId,
            @PathVariable("groupId") int groupId, @RequestBody Department group) {

        groupFilter(groupId, companyId);
        group.setId(groupId);
        group.setCompanyId(companyId);

        departmentService.updateSelective(group);
        return new ResponseEntity<Department>(HttpStatus.OK);
    }

    /**
     * 删除分组
     * 
     */
    @RequestMapping(value = "/{groupId}", method = RequestMethod.DELETE)
    @Interceptors({ CompanyAdminRequired.class })
    public ResponseEntity<Department> deleteDepartment(@PathVariable("companyId") int companyId,
            @PathVariable("groupId") int groupId) {
        groupFilter(groupId, companyId);
        departmentService.delete(groupId);
        return new ResponseEntity<Department>(HttpStatus.OK);
    }

    /**
     * 更新用户的分组
     * 
     */
    @RequestMapping(value = "/groupuser", method = RequestMethod.PUT)
    @Interceptors({ CompanyAdminRequired.class })
    public ResponseEntity<Department> updateUserDepartment(@RequestBody UserCompany userCompany) {
        departmentService.updateDepartmentOfUser(userCompany);
        return new ResponseEntity<Department>(HttpStatus.OK);
    }

    private void groupFilter(int groupId, int companyId) {
        Department department = departmentService.getById(groupId);
        if (department == null) {
            throw new NoPermissionException(companyId);
        } else if (department.getCompanyId() != companyId) {
            throw new ResourceNotFoundException();
        }
    }

}
