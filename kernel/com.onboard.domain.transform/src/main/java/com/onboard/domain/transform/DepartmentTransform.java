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
package com.onboard.domain.transform;

import java.util.List;

import org.springframework.beans.BeanUtils;

import com.google.common.collect.Lists;
import com.onboard.domain.model.Department;
import com.onboard.domain.model.User;
import com.onboard.dto.DepartmentDTO;

public class DepartmentTransform {
    public static DepartmentDTO departmentAndUsersToDepartmentDTO(Department department, List<User> users) {
        DepartmentDTO departmentDTO = new DepartmentDTO();
        BeanUtils.copyProperties(department, departmentDTO);
        departmentDTO.setUsers(Lists.transform(users, UserTransform.USER_TO_USERDTO_FUNCTION));
        return departmentDTO;
    }

}
