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
package com.onboard.service.collaboration.impl.test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.onboard.domain.mapper.ProjectMapper;
import com.onboard.domain.mapper.ProjectPrivilegeMapper;
import com.onboard.domain.mapper.UserProjectMapper;
import com.onboard.domain.mapper.model.ProjectExample;
import com.onboard.domain.mapper.model.ProjectPrivilegeExample;
import com.onboard.domain.mapper.model.UserProjectExample;
import com.onboard.domain.mapper.model.common.BaseExample;
import com.onboard.domain.model.Activity;
import com.onboard.domain.model.Attachment;
import com.onboard.domain.model.Company;
import com.onboard.domain.model.Project;
import com.onboard.domain.model.Todo;
import com.onboard.domain.model.Topic;
import com.onboard.domain.model.UserProject;
import com.onboard.dto.ProjectDTO;
import com.onboard.service.account.CompanyService;
import com.onboard.service.account.UserService;
import com.onboard.service.activity.ActivityService;
import com.onboard.service.collaboration.AttachmentService;
import com.onboard.service.collaboration.IterationService;
import com.onboard.service.collaboration.ProjectService;
import com.onboard.service.collaboration.TodoService;
import com.onboard.service.collaboration.TodolistService;
import com.onboard.service.collaboration.TopicService;
import com.onboard.service.collaboration.activity.ActivityRecorderHelper;
import com.onboard.service.collaboration.impl.ProjectMemberService;
import com.onboard.service.collaboration.impl.ProjectServiceImpl;
import com.onboard.service.web.SessionService;
import com.onboard.test.exampleutils.CriterionVerifier;
import com.onboard.test.exampleutils.ExampleMatcher;
import com.onboard.test.exampleutils.ObjectMatcher;
import com.onboard.test.moduleutils.ModuleHelper;

@RunWith(MockitoJUnitRunner.class)
public class ProjectServiceImplTest {

    @Mock
    private ProjectService projectService;

    @Mock
    private UserService userService;

    @Mock
    private SessionService sessionService;

    @Mock
    private ProjectMapper projectMapper;

    @Mock
    private UserProjectMapper userProjectMapper;

    @Mock
    private TopicService topicService;

    @Mock
    private AttachmentService attachmentService;

    @Mock
    private TodolistService todolistService;

    @Mock
    private TodoService todoService;

    @Mock
    private ActivityService activityService;

    @Mock
    private CompanyService companyService;

    @Mock
    IterationService iterationService;

    @Mock
    private ProjectMemberService projectMemberService;

    @Mock
    private ProjectPrivilegeMapper projectPrivilegeMapper;

    @InjectMocks
    private ProjectServiceImpl projectServiceImpl;

    private static int id = 1;
    private static int companyId = 3;
    private static int projectId = 4;

    private static int userId = 5;

    private Project project;
    private ProjectDTO projectDTO;
    private List<Project> projects;

    private Company company;

    private Project getASampleProject() {
        return ModuleHelper.getASampleProject();
    }

    private List<Project> getASampleProjectList() {
        List<Project> projects = new ArrayList<Project>();
        projects.add(project);
        projects.add(project);
        return projects;
    }

    @Before
    public void setUpBefore() throws Exception {
        project = getASampleProject();
        projects = getASampleProjectList();
        company = ModuleHelper.getASampleCompany();

        projectDTO = new ProjectDTO();
        projectDTO.setId(ModuleHelper.projectId);
        projectDTO.setName(ModuleHelper.projectName);
        projectDTO.setCreated(ModuleHelper.created);
        projectDTO.setCompanyId(ModuleHelper.companyId);
        projectDTO.setCreatorId(ModuleHelper.creatorId);
        projectDTO.setDeleted(false);
        projectDTO.setUpdated(ModuleHelper.created);
        projectDTO.setArchived(false);

        when(projectMapper.selectByExample(any(ProjectExample.class))).thenReturn(projects);
        when(projectMapper.selectByPrimaryKey(anyInt())).thenReturn(project);

        when(companyService.getById(anyInt())).thenReturn(company);

        Mockito.doNothing().when(projectMemberService).add(anyInt(), anyInt(), any(int[].class));

        ActivityRecorderHelper activityRecorderHelper = new ActivityRecorderHelper();
        activityRecorderHelper.setProjectService(projectService);
        activityRecorderHelper.setSession(sessionService);
        activityRecorderHelper.setUserService(userService);

        when(projectService.getById(anyInt())).thenReturn(project);
        when(userService.getById(anyInt())).thenReturn(ModuleHelper.getASampleUser());
        when(sessionService.getCurrentUser()).thenReturn(ModuleHelper.getASampleUser());

        // when(companyService.getById)

    }

    @After
    public void tearDownAfter() throws Exception {
    }

    @Test
    public void testGetActiveProjectsByCompany() {
        List<Project> projectList = projectServiceImpl.getActiveProjectsByCompany(companyId, ModuleHelper.start,
                ModuleHelper.limit);

        verify(projectMapper).selectByExample(argThat(new ExampleMatcher<ProjectExample>() {
            @Override
            public boolean matches(BaseExample example) {
                Boolean companyIdMatchBoolean = CriterionVerifier.verifyEqualTo(example, "companyId", companyId);
                Boolean startMatchBoolean = CriterionVerifier.verifyStart(example, ModuleHelper.start);
                Boolean limitMatchBoolean = CriterionVerifier.verifyLimit(example, ModuleHelper.limit);
                return companyIdMatchBoolean && startMatchBoolean && limitMatchBoolean;
            }
        }));
        assertEquals(projects, projectList);
    }

    @Test
    public void testGetArchivedProjecsByCompany() {
        List<Project> projectList = projectServiceImpl.getArchivedProjecsByCompany(companyId, ModuleHelper.start,
                ModuleHelper.limit);

        verify(projectMapper).selectByExample(argThat(new ExampleMatcher<ProjectExample>() {
            @Override
            public boolean matches(BaseExample example) {
                Boolean companyIdMatchBoolean = CriterionVerifier.verifyEqualTo(example, "companyId", companyId);
                Boolean archivedMatchBoolean = CriterionVerifier.verifyEqualTo(example, "archived", true);
                return companyIdMatchBoolean && archivedMatchBoolean;
            }
        }));
        assertEquals(projects, projectList);

    }

    @Test
    public void testGetActiveProjectListByUserByCompany() {
        List<Project> projects = projectServiceImpl.getArchivedProjectListByUserByCompany(ModuleHelper.companyId,
                ModuleHelper.companyId, ModuleHelper.start, ModuleHelper.limit);
        verify(userProjectMapper).selectByExample(argThat(new ExampleMatcher<UserProjectExample>() {
            @Override
            public boolean matches(BaseExample example) {
                Boolean companyIdMatchBoolean = CriterionVerifier.verifyEqualTo(example, "companyId", ModuleHelper.companyId);
                Boolean archivedMatchBoolean = CriterionVerifier.verifyEqualTo(example, "userId", ModuleHelper.companyId);
                return companyIdMatchBoolean && archivedMatchBoolean;
            }
        }));
        assertEquals(projects.size(), 0);
    }

    @Test
    public void testGetArchivedProjectListByUserByCompany() {
        List<Project> projects = projectServiceImpl.getArchivedProjectListByUserByCompany(ModuleHelper.companyId,
                ModuleHelper.companyId, ModuleHelper.start, ModuleHelper.limit);
        verify(userProjectMapper).selectByExample(argThat(new ExampleMatcher<UserProjectExample>() {
            @Override
            public boolean matches(BaseExample example) {
                Boolean companyIdMatchBoolean = CriterionVerifier.verifyEqualTo(example, "companyId", ModuleHelper.companyId);
                Boolean archivedMatchBoolean = CriterionVerifier.verifyEqualTo(example, "userId", ModuleHelper.companyId);
                return companyIdMatchBoolean && archivedMatchBoolean;
            }
        }));
        assertEquals(projects.size(), 0);
    }

    @Test
    public void testGrantAccessToExsitingPeople() {

        // for (User user : users) {
        // UserProject userProject = new UserProject();
        // userProject.setProjectId(project.getId());
        // userProject.setUserId(user.getId());
        // userProject.setCompanyId(project.getCompanyId());
        // userProjectMapper.insert(userProject);
        // accountService.addActivityInfo(user, project.getId());
        // }

    }

    @Test
    public void testGrantAccessViaEmailAddress() {
        // for (String email : emailAddresses) {
        // accountService.sendInvitation(project.getCompanyId(), email,
        // Arrays.asList(project));
        // }
    }

    @Test
    public void testGetProjectIdListByUserByCompany() {

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

    }

    @Test
    public void testGetActiveProjectIdListByUserByCompany() {
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
    }

    @Test
    public void testGetDiscardedProjectListByCompany() {
        projectServiceImpl.getDiscardedProjectListByCompany(ModuleHelper.companyId, ModuleHelper.start, ModuleHelper.limit);
        verify(projectMapper).selectByExample(argThat(new ExampleMatcher<ProjectExample>() {
            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "companyId", companyId)
                        && CriterionVerifier.verifyEqualTo(example, "deleted", true);
            }
        }));
    }

    @Test
    public void testGetProjectById() {
        Project result = projectServiceImpl.getById(id);
        verify(projectMapper).selectByPrimaryKey(id);
        assertEquals(project, result);
    }

    @Test
    public void testGetProjectsByCompany() {
        List<Project> projectList = projectServiceImpl.getProjectsByCompany(companyId, ModuleHelper.start, ModuleHelper.limit);
        verify(projectMapper).selectByExample(argThat(new ExampleMatcher<ProjectExample>() {
            @Override
            public boolean matches(BaseExample example) {
                Boolean companyIdMatchBoolean = CriterionVerifier.verifyEqualTo(example, "companyId", companyId);
                return companyIdMatchBoolean;
            }
        }));
        assertEquals(projects, projectList);
    }

    @Test
    public void testGetProjectListByOwnerByCompany() {
        List<Project> projectList = projectServiceImpl.getProjectListByOwnerByCompany(userId, companyId, ModuleHelper.start,
                ModuleHelper.limit);
        verify(projectMapper).selectByExample(argThat(new ExampleMatcher<ProjectExample>() {
            @Override
            public boolean matches(BaseExample example) {
                Boolean companyIdMatchBoolean = CriterionVerifier.verifyEqualTo(example, "companyId", companyId);
                Boolean userIdMatchBoolean = CriterionVerifier.verifyEqualTo(example, "creatorId", userId);

                return companyIdMatchBoolean && userIdMatchBoolean;
            }
        }));
        assertEquals(projects, projectList);
    }

    @Test
    public void testCreateProject() {

        projectServiceImpl.createProject(projectDTO);

        verify(projectMapper).insertSelective(argThat(new ObjectMatcher<Project>() {
            @Override
            public boolean verifymatches(Project p) {
                return p.getCreated() != null && p.getUpdated() != null && p.getName().equals(project.getName())
                        && p.getCompanyId().equals(project.getCompanyId());
            }
        }));

        // verify(projectMemberService).add(anyInt(), anyInt(), any(int[].class));
    }

    @Test
    public void testUpdateProject() {
        projectServiceImpl.updateProject(projectDTO);
        verify(projectMapper).updateByPrimaryKeySelective(argThat(new ObjectMatcher<Project>() {
            @Override
            public boolean verifymatches(Project p) {
                return p.getUpdated() != null && p.getName().equals(project.getName())
                        && p.getCompanyId().equals(project.getCompanyId());
            }
        }));
    }

    @Test
    public void testRevokeAccess() {
        projectServiceImpl.revokeAccess(project.getId(), userId);
        verify(userProjectMapper).deleteByExample(argThat(new ExampleMatcher<UserProjectExample>() {
            @Override
            public boolean matches(BaseExample example) {
                Boolean companyIdMatchBoolean = CriterionVerifier.verifyEqualTo(example, "projectId", project.getId());
                Boolean userIdMatchBoolean = CriterionVerifier.verifyEqualTo(example, "userId", userId);
                return companyIdMatchBoolean && userIdMatchBoolean;
            }
        }));
        verify(projectPrivilegeMapper).deleteByExample(argThat(new ExampleMatcher<ProjectPrivilegeExample>() {
            @Override
            public boolean matches(BaseExample example) {
                Boolean companyIdMatchBoolean = CriterionVerifier.verifyEqualTo(example, "projectId", project.getId());
                Boolean userIdMatchBoolean = CriterionVerifier.verifyEqualTo(example, "userId", userId);
                return companyIdMatchBoolean && userIdMatchBoolean;
            }
        }));
        verify(activityService).create(any(Activity.class));
    }

    @Test
    public void testArchiveProject() {
        projectServiceImpl.archiveProject(id);
        verify(projectMapper).updateByPrimaryKeySelective(argThat(new ObjectMatcher<Project>() {
            @Override
            public boolean verifymatches(Project item) {
                return item.getArchived().equals(true) && item.getId().equals(id);
            }

        }));

    }

    @Test
    public void testActivateProject() {
        projectServiceImpl.activateProject(id);
        verify(projectMapper).updateByPrimaryKeySelective(argThat(new ObjectMatcher<Project>() {

            @Override
            public boolean verifymatches(Project item) {
                return item.getDeleted().equals(false) && item.getId().equals(id);
            }

        }));
    }

    @Test
    public void testDeleteProject() {
        projectServiceImpl.deleteFromTrash(id);
        verify(projectMapper).deleteByPrimaryKey(id);

    }

    @Test
    public void testDiscardProject() {
        projectServiceImpl.delete(id);

        verify(projectMapper).updateByPrimaryKeySelective(argThat(new ObjectMatcher<Project>() {

            @Override
            public boolean verifymatches(Project item) {
                return item.getDeleted() == true && item.getId().equals(id);
            }

        }));
    }

    @Test
    public void testRecoverProject() {
        projectServiceImpl.recover(id);
        verify(projectMapper).updateByPrimaryKeySelective(argThat(new ObjectMatcher<Project>() {

            @Override
            public boolean verifymatches(Project item) {
                return item.getDeleted() == false && item.getId().equals(id);
            }

        }));
    }

    @Test
    public void testGetTopicCount() {
        projectServiceImpl.getTopicCount(projectId);
        verify(topicService).countByExample(argThat(new ObjectMatcher<Topic>() {

            @Override
            public boolean verifymatches(Topic item) {
                return item.getProjectId().equals(projectId);
            }

        }));

    }

    @Test
    public void testGetAttachmentCount() {
        projectServiceImpl.getAttachmentCount(projectId);
        verify(attachmentService).countBySample(argThat(new ObjectMatcher<Attachment>() {

            @Override
            public boolean verifymatches(Attachment item) {
                return item.getProjectId().equals(projectId);
            }

        }));
    }

    @Test
    public void testGetTodoCount() {
        projectServiceImpl.getTodoCount(projectId);
        verify(todoService).countBySample(argThat(new ObjectMatcher<Todo>() {

            @Override
            public boolean verifymatches(Todo item) {
                return item.getProjectId().equals(projectId);
            }

        }));
    }

    @Test
    public void testGetUserCount() {
        projectServiceImpl.getUserCount(projectId);
        verify(userProjectMapper).countByExample(argThat(new ExampleMatcher<UserProjectExample>() {

            @Override
            public boolean matches(BaseExample example) {
                Boolean projectIdMatchBoolean = CriterionVerifier.verifyEqualTo(example, "projectId", projectId);
                return projectIdMatchBoolean;
            }
        }));
    }

}
