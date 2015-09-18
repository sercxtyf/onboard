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
package com.onboard.service.collaboration.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.primitives.Ints;
import com.onboard.domain.mapper.ProjectMapper;
import com.onboard.domain.mapper.ProjectPrivilegeMapper;
import com.onboard.domain.mapper.ProjectTodoStatusMapper;
import com.onboard.domain.mapper.UserProjectMapper;
import com.onboard.domain.mapper.base.BaseMapper;
import com.onboard.domain.mapper.model.ProjectExample;
import com.onboard.domain.mapper.model.ProjectPrivilegeExample;
import com.onboard.domain.mapper.model.ProjectTodoStatusExample;
import com.onboard.domain.mapper.model.UserProjectExample;
import com.onboard.domain.model.Activity;
import com.onboard.domain.model.Attachment;
import com.onboard.domain.model.Company;
import com.onboard.domain.model.IterationItemStatus;
import com.onboard.domain.model.Project;
import com.onboard.domain.model.ProjectPrivilege;
import com.onboard.domain.model.ProjectTodoStatus;
import com.onboard.domain.model.Todo;
import com.onboard.domain.model.Topic;
import com.onboard.domain.model.User;
import com.onboard.domain.model.UserProject;
import com.onboard.domain.transform.ProjectTransform;
import com.onboard.dto.ProjectDTO;
import com.onboard.service.account.CompanyService;
import com.onboard.service.activity.ActivityService;
import com.onboard.service.base.AbstractBaseService;
import com.onboard.service.collaboration.AttachmentService;
import com.onboard.service.collaboration.IterationService;
import com.onboard.service.collaboration.ProjectService;
import com.onboard.service.collaboration.TodoService;
import com.onboard.service.collaboration.TodolistService;
import com.onboard.service.collaboration.TopicService;
import com.onboard.service.collaboration.activity.ActivityRecorderHelper;
import com.onboard.service.web.SessionService;

/**
 * {@link ProjectService}接口实现
 * 
 * @author yewei, ruici
 * 
 */
@Transactional
@Service("projectServiceBean")
public class ProjectServiceImpl extends AbstractBaseService<Project, ProjectExample> implements ProjectService {

    public static final int MAX_ACTIVITY_ITEM_NO = 5;
    public static final int MAX_TOPIC_ITEM_NO = 5;
    public static final int MAX_TODOLIST_ITEM_NO = 10000;
    public static final int MAX_ATTACHMENTS_ITEM_NO = 4;

    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private ProjectMemberService projectMemberService;

    @Autowired
    private UserProjectMapper userProjectMapper;

    @Autowired
    private ProjectPrivilegeMapper projectPrivilegeMapper;

    @Autowired
    private ProjectTodoStatusMapper projectTodoStatusMapper;

    @Autowired
    private TopicService topicService;

    @Autowired
    private AttachmentService attachmentService;

    @Autowired
    private TodolistService todolistService;

    @Autowired
    private TodoService todoService;

    @Autowired
    private ActivityService activityService;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private IterationService iterationService;

    @Override
    public List<Project> getActiveProjectsByCompany(int companyId, int start, int limit) {

        Project project = new Project();
        project.setCompanyId(companyId);
        project.setDeleted(false);
        ProjectExample projectExample = new ProjectExample(project);
        projectExample.setLimit(start, limit);

        return projectMapper.selectByExample(projectExample);
    }

    @Override
    public List<Project> getArchivedProjecsByCompany(int companyId, int start, int limit) {
        Project project = new Project();
        project.setCompanyId(companyId);
        project.setArchived(true);
        ProjectExample projectExample = new ProjectExample(project);
        projectExample.setLimit(start, limit);

        return projectMapper.selectByExample(projectExample);
    }

    @Override
    public List<Project> getProjectsByCompany(int companyId, int start, int limit) {
        Project project = new Project();
        project.setCompanyId(companyId);
        ProjectExample projectExample = new ProjectExample(project);
        projectExample.setLimit(start, limit);
        return projectMapper.selectByExample(projectExample);
    }

    @Override
    public List<Project> getProjectListByOwnerByCompany(int userId, int companyId, int start, int limit) {
        Project project = new Project();
        project.setCompanyId(companyId);
        project.setCreatorId(userId);

        ProjectExample projectExample = new ProjectExample(project);
        projectExample.setLimit(start, limit);

        return projectMapper.selectByExample(projectExample);
    }

    @Override
    public List<Project> getActiveProjectListByUserByCompany(int userId, int companyId, int start, int limit) {
        return getProjectListByArchivedByUserByCompany(userId, companyId, start, limit, false);
    }

    @Override
    public List<Project> getArchivedProjectListByUserByCompany(int userId, int companyId, int start, int limit) {
        return getProjectListByArchivedByUserByCompany(userId, companyId, start, limit, true);
    }

    public List<Project> getProjectListByArchivedByUserByCompany(int userId, int companyId, int start, int limit, Boolean archived) {
        List<Project> projects = new ArrayList<Project>();

        UserProject userProject = new UserProject();
        userProject.setUserId(userId);
        userProject.setCompanyId(companyId);

        UserProjectExample userProjectExample = new UserProjectExample(userProject);
        userProjectExample.setOrderByClause("customOrder");
        List<UserProject> userProjectList = userProjectMapper.selectByExample(userProjectExample);

        for (UserProject up : userProjectList) {
            Project project = new Project(projectMapper.selectByPrimaryKey(up.getProjectId()));
            if (project.getDeleted() == false && project.getArchived() == archived) {
                projects.add(project);
            }
        }
        return projects;
    }

    @Override
    public List<Integer> getProjectIdListByUserByCompany(int userId, int companyId, int start, int limit) {

        List<Integer> projects = new ArrayList<Integer>();

        UserProject userProject = new UserProject();
        userProject.setUserId(userId);
        userProject.setCompanyId(companyId);

        List<UserProject> userProjectList = userProjectMapper.selectByExample(new UserProjectExample(userProject));

        for (UserProject up : userProjectList) {
            Project project = new Project(projectMapper.selectByPrimaryKey(up.getProjectId()));
            if (project.getDeleted() == false) {
                projects.add(project.getId());
            }
        }

        return projects;
    }

    @Override
    public List<Integer> getActiveProjectIdListByUserByCompany(int userId, int companyId, int start, int limit) {
        List<Integer> projects = new ArrayList<Integer>();
        UserProject userProject = new UserProject();
        userProject.setUserId(userId);
        userProject.setCompanyId(companyId);
        List<UserProject> userProjectList = userProjectMapper.selectByExample(new UserProjectExample(userProject));
        for (UserProject up : userProjectList) {
            Project project = new Project(projectMapper.selectByPrimaryKey(up.getProjectId()));
            if (project.getDeleted() == false && project.getArchived() == false) {
                projects.add(project.getId());
            }
        }
        return projects;
    }

    @Override
    public List<Project> getDiscardedProjectListByCompany(int companyId, int start, int limit) {
        Project project = new Project(true);
        project.setCompanyId(companyId);
        ProjectExample projectExample = new ProjectExample(project);
        projectExample.setLimit(start, limit);

        return projectMapper.selectByExample(projectExample);
    }

    @Override
    public void grantAccessToExistingPeople(Project project, List<User> users) {

    }

    @Override
    public void grantAccessViaEmailAddress(Project project, List<String> emailAddresses) {
    }

    @Override
    public Project createProject(ProjectDTO projectDTO) {
        Project project = ProjectTransform.projectDTOtoProject(projectDTO);
        project.setCreated(new Date());
        project.setUpdated(project.getCreated());
        project.setCreatorAvatar(sessionService.getCurrentUser().getAvatar());
        projectMapper.insertSelective(project);

        Company company = companyService.getById(project.getCompanyId());
        projectMemberService.add(project.getCompanyId(), project.getId(), project.getCreatorId(), company.getCreatorId());
        if (projectDTO.getMembers() != null) {
            projectMemberService.add(project.getCompanyId(), project.getId(), Ints.toArray(projectDTO.getMembers()));
        }
        if (projectDTO.getEmails() != null) {
            projectMemberService.invite(project.getCompanyId(), project.getId(), projectDTO.getEmails().toArray(new String[0]));
        }
        iterationService.addNewIterationForProject(project);
        return project;
    }

    @Override
    public Project updateProject(ProjectDTO projectDTO) {
        Project project = ProjectTransform.projectDTOtoProject(projectDTO);
        project.setUpdated(new Date());
        projectMapper.updateByPrimaryKeySelective(project);

        if (projectDTO.getMembers() != null && projectDTO.getMembers().size() > 0) {

            Set<Integer> existing = Sets.newHashSet(projectMemberService.get(project.getId()));
            Set<Integer> upcoming = Sets.newHashSet(projectDTO.getMembers());

            // TEMP FIX: we will not remove any body as some bug in the page
            projectMemberService.remove(project.getId(), Ints.toArray(Sets.difference(existing, upcoming)));
            projectMemberService.add(project.getCompanyId(), project.getId(), Ints.toArray(Sets.difference(upcoming, existing)));
        }
        if (projectDTO.getEmails() != null) {
            projectMemberService.invite(project.getCompanyId(), project.getId(), projectDTO.getEmails().toArray(new String[0]));
        }

        return project;
    }

    @Override
    public void archiveProject(int id) {
        Project project = new Project(id);
        project.setArchived(true);
        projectMapper.updateByPrimaryKeySelective(project);
    }

    @Override
    public void activateProject(int id) {
        Project project = new Project(id, false);
        projectMapper.updateByPrimaryKeySelective(project);
    }

    @Override
    public void revokeAccess(int projectId, int userId) {
        UserProject userProject = new UserProject();
        userProject.setProjectId(projectId);
        userProject.setUserId(userId);
        userProjectMapper.deleteByExample(new UserProjectExample(userProject));
        // 删除该用户在该项目的权限
        ProjectPrivilege sample = new ProjectPrivilege();
        sample.setProjectId(projectId);
        sample.setUserId(userId);
        projectPrivilegeMapper.deleteByExample(new ProjectPrivilegeExample(sample));

        Activity activity = ActivityRecorderHelper.generateActivityOfRemoveUser(projectId, userId);
        activityService.create(activity);
    }

    @Override
    public int getTopicCount(int projectId) {
        Topic topic = new Topic(false);
        topic.setProjectId(projectId);

        return topicService.countByExample(topic);
    }

    @Override
    public int getAttachmentCount(int projectId) {
        Attachment attachment = new Attachment();
        attachment.setProjectId(projectId);
        attachment.setDeleted(false);
        return attachmentService.countBySample(attachment);
    }

    @Override
    public int getTodoCount(int projectId) {
        Todo todo = new Todo(false);
        todo.setProjectId(projectId);
        return todoService.countBySample(todo);
    }

    @Override
    public int getUserCount(int projectId) {
        UserProject userProject = new UserProject();
        userProject.setProjectId(projectId);
        return userProjectMapper.countByExample(new UserProjectExample(userProject));
    }

    @Override
    public void sortProject(List<Integer> projectIds) {
        UserProject userProject = new UserProject();
        for (int i = 0; i < projectIds.size(); i++) {
            userProject.setProjectId(projectIds.get(i));
            userProject.setUserId(sessionService.getCurrentUser().getId());
            userProject.setCustomOrder(null);
            UserProjectExample userProjectExample = new UserProjectExample(userProject);
            userProject.setCustomOrder(i);
            userProjectMapper.updateByExampleSelective(userProject, userProjectExample);
        }
    }

    @Override
    public List<String> getTodoStatusByProjectId(Integer projectId) {
        ProjectTodoStatus sample = new ProjectTodoStatus();
        sample.setProjectId(projectId);
        List<ProjectTodoStatus> statuses = projectTodoStatusMapper.selectByExample(new ProjectTodoStatusExample(sample));
        List<String> result;
        // 如果已经有数据，直接返回
        if (!statuses.isEmpty()) {
            result = Lists.newArrayList();
            for (ProjectTodoStatus status : statuses) {
                result.add(status.getStatus());
            }
        }
        // 如果还没有数据，默认三种状态，并存进数据库
        else {
            result = IterationItemStatus.getDefaultTodoStatus();
            for (String status : result) {
                ProjectTodoStatus projectTodoStatus = new ProjectTodoStatus();
                projectTodoStatus.setProjectId(projectId);
                projectTodoStatus.setStatus(status);
                projectTodoStatusMapper.insert(projectTodoStatus);
            }
        }
        return result;
    }

    @Override
    public boolean addTodoStatusToProject(Integer projectId, String todoStatus) {
        ProjectTodoStatus sample = new ProjectTodoStatus();
        sample.setProjectId(projectId);
        sample.setStatus(todoStatus);
        List<ProjectTodoStatus> statuses = projectTodoStatusMapper.selectByExample(new ProjectTodoStatusExample(sample));
        if (!statuses.isEmpty()) {
            return false;
        }
        projectTodoStatusMapper.insert(sample);
        return true;
    }

    @Override
    public boolean removeTodoStatusFromProject(Integer projectId, String todoStatus) {
        if (IterationItemStatus.getDefaultTodoStatus().contains(todoStatus)) {
            return false;
        }
        ProjectTodoStatus sample = new ProjectTodoStatus();
        sample.setProjectId(projectId);
        sample.setStatus(todoStatus);
        projectTodoStatusMapper.deleteByExample(new ProjectTodoStatusExample(sample));
        return true;
    }

    @Override
    protected BaseMapper<Project, ProjectExample> getBaseMapper() {
        return projectMapper;
    }

    @Override
    public Project newItem() {
        return new Project();
    }

    @Override
    public ProjectExample newExample() {
        return new ProjectExample();
    }

    @Override
    public ProjectExample newExample(Project item) {
        return new ProjectExample(item);
    }
}
