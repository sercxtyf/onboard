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
package com.onboard.service.collaboration;

import java.util.List;

import com.onboard.domain.mapper.model.ProjectExample;
import com.onboard.domain.model.Project;
import com.onboard.domain.model.User;
import com.onboard.dto.ProjectDTO;
import com.onboard.service.base.BaseService;

/**
 * {@link Project}服务接口
 * 
 * @author ruici,yewei
 * 
 */
public interface ProjectService extends BaseService<Project, ProjectExample> {

    /**
     * 获取指定公司中所有活动(非archived)项目列表
     * 
     * @param companyId
     *            公司id
     * @return
     */
    List<Project> getActiveProjectsByCompany(int companyId, int start, int limit);

    /**
     * 获取指定公司中所有归档(archived)项目列表
     * 
     * @param companyId
     *            公司id
     * @return
     */
    List<Project> getArchivedProjecsByCompany(int companyId, int start, int limit);

    /**
     * 获取Company下的所有Project
     * 
     * @param companyId
     * @return
     */
    List<Project> getProjectsByCompany(int companyId, int start, int limit);

    /**
     * 获取某个Company下某用户创建的所有Project
     * 
     * @param userId
     * @return
     */
    List<Project> getProjectListByOwnerByCompany(int userId, int companyId, int start, int limit);

    /**
     * 获取某个Company下某用户参与的所有Project
     * 
     * @param userId
     * @param companyId
     * @param start
     * @param limit
     * @return
     */
    List<Project> getActiveProjectListByUserByCompany(int userId, int companyId, int start, int limit);

    /**
     * 获取某个Company下某用户参与的所有Project的Id
     * 
     * @param userId
     * @param companyId
     * @param start
     * @param limit
     * @return
     */
    List<Integer> getProjectIdListByUserByCompany(int userId, int companyId, int start, int limit);

    /**
     * 获取所有回收站中的Project
     * 
     * @return
     */
    List<Project> getDiscardedProjectListByCompany(int companyId, int start, int limit);

    /**
     * 归档项目
     * 
     * @param id
     *            项目id
     */
    void archiveProject(int id);

    /**
     * 激活项目
     * 
     * @param id
     *            项目id
     */
    void activateProject(int id);

    /**
     * 为团队成员授予项目权限
     * 
     * @param id
     *            项目id
     * @param userIds
     *            成员id列表
     */
    void grantAccessToExistingPeople(Project project, List<User> users);

    /**
     * 通过邮件邀请新成员加入项目
     * 
     * @param id
     *            项目id
     * @param emailAddresses
     *            新成员邮件列表
     */
    void grantAccessViaEmailAddress(Project project, List<String> emailAddresses);

    /**
     * 解除某成员项目访问权限
     * 
     * @param id
     *            项目id
     * @param userId
     *            成员id
     */
    void revokeAccess(int projectId, int userId);

    /**
     * TODO: move this to database 获取项目Topic数量
     * 
     * @param projectId
     * @return
     */
    int getTopicCount(int projectId);

    /**
     * TODO: move this to database 获取Attachment数量
     * 
     * @param projectId
     * @return
     */
    int getAttachmentCount(int projectId);

    /**
     * TODO: move this to database 获取Todo数量
     * 
     * @param projectId
     * @return
     */
    int getTodoCount(int projectId);

    /**
     * TODO: move this to database 获取Document数量
     * 
     * @param projectId
     * @return
     */
    // int getDocumentCount(int projectId);

    /**
     * TODO: move this to database 获取User数量
     * 
     * @param projectId
     * @return
     */
    int getUserCount(int projectId);

    /**
     * 在项目页面对项目进行拖动排序
     * 
     * @param projectIds
     */
    void sortProject(List<Integer> projectIds);

    /**
     * @param userId
     * @param companyId
     * @param start
     * @param limit
     * @return
     */
    List<Project> getArchivedProjectListByUserByCompany(int userId, int companyId, int start, int limit);

    /**
     * 根据用户和组织获得没有删除和归档的项目列表Id
     * 
     * @param userId
     * @param companyId
     * @param start
     * @param limit
     * @return
     */
    List<Integer> getActiveProjectIdListByUserByCompany(int userId, int companyId, int start, int limit);

    /**
     * 获取项目下的任务状态
     * 
     * @param projectId
     * @return
     */
    List<String> getTodoStatusByProjectId(Integer projectId);

    /**
     * 为项目添加任务状态
     * 
     * @param projectId
     * @param todoStatus
     * @return
     */
    boolean addTodoStatusToProject(Integer projectId, String todoStatus);

    /**
     * 将状态从某个项目中删除
     * 
     * @param projectId
     * @param todoStatus
     * @return
     */
    boolean removeTodoStatusFromProject(Integer projectId, String todoStatus);

    /**
     * Create a project according to a DTO
     * 
     * @param projectDTO
     * @return created project
     */
    Project createProject(ProjectDTO projectDTO);

    /**
     * Update a project according to a DTO
     * 
     * @param projectDTO
     * @return updated project
     */
    Project updateProject(ProjectDTO projectDTO);
}
