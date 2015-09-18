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

import com.onboard.domain.model.ProjectPrivilege;
import com.onboard.domain.model.User;

/**
 * {@link ProjectPrivilege} Service Interface
 * 
 * @author XR, yewei
 * 
 */
public interface ProjectPrivilegeService {
    /**
     * 根据主键获取项目权限对象
     * @param id 主键
     * @return 按要求从数据库中获取出的项目权限对象
     */
    ProjectPrivilege getProjectPrivilegeById(int id);

    /**
     * 获取一定范围内的项目权限列表
     * @param start 列表的起始位置
     * @param limit 列表的最大长度
     * @return 按要求从数据库中获取出的项目权限列表
     */
    List<ProjectPrivilege> getProjectPrivileges(int start, int limit);

    /**
     * 根据样例对象获取一定范围内的项目权限列表
     * @param item 样例对象
     * @param start 列表的起始位置
     * @param limit 列表的最大长度
     * @return 按要求从数据库中获取出的项目权限列表
     */
    List<ProjectPrivilege> getProjectPrivilegesByExample(ProjectPrivilege item, int start, int limit);

    /**
     * 根据样例对象获取符合条件的项目权限数量
     * @param item 样例对象
     * @return 按要求从数据库中获取出的项目权限的数量
     */
    int countByExample(ProjectPrivilege item);

    /**
     * 在数据库中创建一个项目权限对象
     * @param item 需要被添加进数据库的项目权限对象
     * @return 创建好的项目权限对象，包括其在数据库中的主键
     */
    ProjectPrivilege createProjectPrivilege(ProjectPrivilege item);

    /**
     * 在数据库中更新一个项目权限对象
     * @param item 需要被更新的项目权限对象
     * @return 更新好的项目权限对象
     */
    ProjectPrivilege updateProjectPrivilege(ProjectPrivilege item);

    /**
     * 在数据库中删除一个项目权限对象
     * @param id 需要被删除的项目权限对象的主键
     */
    void deleteProjectPrivilege(int id);

    /**
     * 获取特定用户在特定项目中的权限，如果权限尚不存在，则创建一个
     * @param projectId 项目主键
     * @param userId 用户主键
     * @return 已经存在或者刚刚创建好的项目权限对象
     */
    ProjectPrivilege getOrCreateProjectPrivilegeByUserId(int projectId, int userId);

    /**
     * 根据用户主键获取其所有关联的项目权限
     * @param userId 用户主键
     * @return 按要求从数据库中获取出的项目权限列表
     */
    List<ProjectPrivilege> getProjectPrivilegesByUserId(int userId);

    /**
     * 根据项目主键获取其所有的管理员
     * @param projectId 项目主键
     * @return 按要求从数据库中获取出的用户列表
     */
    List<User> getProjectAdminsByProject(int projectId);

    /**
     * 添加特定用户为特定项目的管理员
     * @param userId 用户主键
     * @param projectId 项目主键
     */
    void addProjectAdmin(int userId, int projectId);

    /**
     * 从特定项目的管理员中删除特定用户
     * @param userId 用户主键
     * @param projectId 项目主键
     */
    void removeProjectAdmin(int userId, int projectId);
}
