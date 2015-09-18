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
package com.onboard.service.security;

import java.util.List;

import com.onboard.domain.model.CompanyPrivilege;

/**
 * {@link CompanyPrivilege} Service Interface
 * 
 * @author XR
 * 
 */
public interface CompanyPrivilegeService {
    /**
     * 根据主键获取团队权限对象
     * @param id 主键
     * @return 按要求从数据库中获取出的团队权限对象
     */
    CompanyPrivilege getCompanyPrivilegeById(int id);

    /**
     * 获取一定范围内的团队权限列表
     * @param start 列表的起始位置
     * @param limit 列表的最大长度
     * @return 按要求从数据库中获取出的团队权限列表
     */
    List<CompanyPrivilege> getCompanyPrivileges(int start, int limit);

    /**
     * TODO 需要和updateCompanyPrivilege统一
     * 在数据库中更新一个团队权限对象
     * @param item 需要被更新的团队权限对象
     * @return 更新好的团队权限对象
     */
    CompanyPrivilege setCompanyPrivilege(CompanyPrivilege item);

    /**
     * 根据样例对象获取一定范围内的团队权限列表
     * @param item 样例对象
     * @param start 列表的起始位置
     * @param limit 列表的最大长度
     * @return 按要求从数据库中获取出的团队权限列表
     */
    List<CompanyPrivilege> getCompanyPrivilegesByExample(CompanyPrivilege item, int start, int limit);

    /**
     * 根据样例对象获取符合条件的团队权限数量
     * @param item 样例对象
     * @return 按要求从数据库中获取出的团队权限的数量
     */
    int countByExample(CompanyPrivilege item);

    /**
     * 在数据库中创建一个团队权限对象
     * @param item 需要被添加进数据库的团队权限对象
     * @return 创建好的团队权限对象，包括其在数据库中的主键
     */
    CompanyPrivilege createCompanyPrivilege(CompanyPrivilege item);

    /**
     * 在数据库中更新一个团队权限对象
     * @param item 需要被更新的团队权限对象
     * @return 更新好的团队权限对象
     */
    CompanyPrivilege updateCompanyPrivilege(CompanyPrivilege item);

    /**
     * 在数据库中删除一个团队权限对象
     * @param id 需要被删除的团队权限对象的主键
     */
    void deleteCompanyPrivilege(int id);

    /**
     * 获取特定用户在特定团队中的权限，如果权限尚不存在，则创建一个
     * @param companyId 团队主键
     * @param userId 用户主键
     * @return 已经存在或者刚刚创建好的团队权限对象
     */
    CompanyPrivilege getOrCreateCompanyPrivilegeByUserId(int companyId, int userId);
}
