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
package com.onboard.service.security.impl.test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.onboard.domain.mapper.ProjectPrivilegeMapper;
import com.onboard.domain.mapper.model.ProjectPrivilegeExample;
import com.onboard.domain.mapper.model.common.BaseExample;
import com.onboard.domain.model.ProjectPrivilege;
import com.onboard.service.account.UserService;
import com.onboard.service.security.impl.ProjectPrivilegeServiceImpl;
import com.onboard.test.exampleutils.AbstractMatcher;
import com.onboard.test.exampleutils.CriterionVerifier;
import com.onboard.test.exampleutils.ExampleMatcher;
import com.onboard.test.exampleutils.ObjectMatcher;
import com.onboard.test.moduleutils.ModuleHelper;

@RunWith(MockitoJUnitRunner.class)
public class ProjectPrivilegeServiceImplTest {
    ProjectPrivilege projectPrivilege1;

    ProjectPrivilege projectPrivilege2;

    List<ProjectPrivilege> projectPrivileges, projectPrivilegesSingle;

    @InjectMocks
    private ProjectPrivilegeServiceImpl projectPrivilegeServiceImpl;

    @Mock
    private ProjectPrivilegeMapper projectPrivilegeMapper;

    @Mock
    private UserService userService;

    @Before
    public void setUpBefore() throws Exception {
        initUploadMapperOPerations();
    }

    private void initUploadMapperOPerations() {
        projectPrivilege1 = getASampleProjectPrivilege1(false);
        projectPrivilege2 = getASampleProjectPrivilege2(true);
        projectPrivileges = getProjectPrivilegeList();
        List<ProjectPrivilege> p = new ArrayList<ProjectPrivilege>();
        p.add(projectPrivilege1);
        projectPrivilegesSingle = p;
        
        when(projectPrivilegeMapper.selectByPrimaryKey(ModuleHelper.id)).thenReturn(projectPrivilege1);
//        when(projectPrivilegeMapper.selectByExample( Mockito.any(ProjectPrivilegeExample.class ))).thenReturn(projectPrivilegesSingle);
//        when(projectPrivilegeMapper.selectByExample( Mockito.any(ProjectPrivilegeExample.class ))).thenReturn(getProjectPrivilegeList());
        when(projectPrivilegeMapper.countByExample(any(ProjectPrivilegeExample.class))).thenReturn(
                getProjectPrivilegeList().size());
    }

    private ProjectPrivilege getASampleProjectPrivilege1(Boolean isAdmin) {
        ProjectPrivilege projectPrivilege = new ProjectPrivilege();
        projectPrivilege.setId(ModuleHelper.id);
        projectPrivilege.setUserId(ModuleHelper.userId);
        projectPrivilege.setProjectId(ModuleHelper.projectId);
        projectPrivilege.setIsAdmin(isAdmin);
        return projectPrivilege;
    }

    private ProjectPrivilege getASampleProjectPrivilege2(Boolean isAdmin) {
        ProjectPrivilege projectPrivilege = new ProjectPrivilege();
        projectPrivilege.setId(ModuleHelper.id + 1);
        projectPrivilege.setUserId(ModuleHelper.userId + 1);
        projectPrivilege.setProjectId(ModuleHelper.projectId + 1);
        projectPrivilege.setIsAdmin(isAdmin);
        return projectPrivilege;
    }

    private List<ProjectPrivilege> getProjectPrivilegeList() {
        List<ProjectPrivilege> projectPrivileges = new ArrayList<ProjectPrivilege>();
        projectPrivileges.add(projectPrivilege1);
        projectPrivileges.add(projectPrivilege2);
        return projectPrivileges;

    }

    private void runCommonEquals(ProjectPrivilege projectPrivilege, Boolean isAdmin) {
        assertEquals((int) projectPrivilege.getId(), ModuleHelper.id);
        assertEquals((int) projectPrivilege.getProjectId(), ModuleHelper.projectId);
        assertEquals((int) projectPrivilege.getUserId(), ModuleHelper.userId);
        assertEquals(projectPrivilege.getIsAdmin(), isAdmin);
    }

    @Test
    public void testGetProjectPrivilegeById() {
        ProjectPrivilege projectPrivilege = projectPrivilegeServiceImpl.getProjectPrivilegeById(ModuleHelper.id);
        verify(projectPrivilegeMapper, times(1)).selectByPrimaryKey(ModuleHelper.id);
        Mockito.verifyNoMoreInteractions(projectPrivilegeMapper);
        runCommonEquals(projectPrivilege, false);
    }

    @Test
    public void testGetProjectPrivilegesByExample() {

        when(projectPrivilegeMapper.selectByExample( Mockito.any(ProjectPrivilegeExample.class ))).thenReturn(getProjectPrivilegeList());
        
        List<ProjectPrivilege> projectPrivileges = projectPrivilegeServiceImpl.getProjectPrivilegesByExample(
                projectPrivilege1, ModuleHelper.start, ModuleHelper.limit);
        verify(projectPrivilegeMapper).selectByExample(Matchers.argThat(new ExampleMatcher<ProjectPrivilegeExample>() {

            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "projectId", ModuleHelper.projectId);
            }
        }));
        assertEquals(projectPrivileges.size(), 2);
        assertEquals(projectPrivileges.get(0), projectPrivilege1);
    }

    @Test
    public void testCountByExample() {
        int count = projectPrivilegeServiceImpl.countByExample(projectPrivilege1);
        verify(projectPrivilegeMapper).countByExample(Matchers.argThat(new ExampleMatcher<ProjectPrivilegeExample>() {
            @Override
            public boolean matches(BaseExample example) {
                return //CriterionVerifier.verifyEqualTo(example, "id", ModuleHelper.id)
                       CriterionVerifier.verifyEqualTo(example, "userId", ModuleHelper.userId)
                       && CriterionVerifier.verifyEqualTo(example, "projectId", ModuleHelper.projectId)
                       //&& CriterionVerifier.verifyEqualTo(example, "canCreateProject", true);
                       && CriterionVerifier.verifyEqualTo(example, "isAdmin", false);
            }
        }));
        assertEquals(count, 2);
    }

    @Test
    public void testCreateProjectPrivilege() {
        ProjectPrivilege newProjectPrivilege = getASampleProjectPrivilege1(false);
        newProjectPrivilege.setId(null);
        newProjectPrivilege = projectPrivilegeServiceImpl.createProjectPrivilege(newProjectPrivilege);
        verify(projectPrivilegeMapper).insert(argThat(new AbstractMatcher<ProjectPrivilege>() {
            @Override
            public boolean matches(Object arg0) {
                ProjectPrivilege c = (ProjectPrivilege) arg0;
                return c.getIsAdmin() != null && c.getUserId().equals(projectPrivilege1.getUserId())
                        && c.getProjectId().equals(projectPrivilege1.getProjectId()) && c.getIsAdmin().equals(false);
            }
        }));
    }

    @Test
    public void testUpdateProjectPrivilege() {
        ProjectPrivilege c = new ProjectPrivilege(projectPrivilege1);
        final Integer newUserId = 2;
        c.setUserId(newUserId);

        projectPrivilegeServiceImpl.updateProjectPrivilege(c);
        verify(projectPrivilegeMapper).updateByPrimaryKey(argThat(new ObjectMatcher<ProjectPrivilege>() {

            @Override
            public boolean verifymatches(ProjectPrivilege item) {
                return item.getId().equals(projectPrivilege1.getId()) && item.getUserId().equals(newUserId);
            }
        }));
    }

    @Test
    public void testSetProjectPrivilege() {
        ProjectPrivilege c = new ProjectPrivilege(projectPrivilege1);
        final Integer newUserId = 2;
        c.setUserId(newUserId);
        projectPrivilegeServiceImpl.updateProjectPrivilege(c);
        verify(projectPrivilegeMapper).updateByPrimaryKey(argThat(new ObjectMatcher<ProjectPrivilege>() {
            @Override
            public boolean verifymatches(ProjectPrivilege item) {
                return item.getId().equals(projectPrivilege1.getId()) && item.getUserId().equals(newUserId);
            }
        }));
    }

    @Test
    public void testGetOrCreateProjectPrivilegeByUserId() {
        projectPrivilegeServiceImpl.getOrCreateProjectPrivilegeByUserId(ModuleHelper.projectId, ModuleHelper.userId);
        verify(projectPrivilegeMapper).selectByExample(Matchers.argThat(new ExampleMatcher<ProjectPrivilegeExample>() {

            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "projectId", ModuleHelper.projectId)
                        && CriterionVerifier.verifyEqualTo(example, "userId", ModuleHelper.userId);
            }
        }));
    }

    @Test
    public void testDeleteProjectPrivilege() {
        projectPrivilegeServiceImpl.deleteProjectPrivilege(ModuleHelper.id);
        verify(projectPrivilegeMapper, times(1)).deleteByPrimaryKey(Matchers.argThat(new ObjectMatcher<Integer>() {
            @Override
            public boolean verifymatches(Integer id) {
                return id.equals(ModuleHelper.id);
            }
        }));
        Mockito.verifyNoMoreInteractions(projectPrivilegeMapper);
    }

    @Test
    public void testGetProjectPrivilegesByUserId() {
        when(projectPrivilegeMapper.selectByExample( Mockito.any(ProjectPrivilegeExample.class ))).thenReturn(projectPrivilegesSingle);
        
        List<ProjectPrivilege> projectPrivileges = projectPrivilegeServiceImpl
                .getProjectPrivilegesByUserId(ModuleHelper.userId);
        verify(projectPrivilegeMapper).selectByExample(Matchers.argThat(new ExampleMatcher<ProjectPrivilegeExample>() {
            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "userId", ModuleHelper.userId);
            }
        }));
        assertEquals(projectPrivileges.size(), 1);
    }

    @Test
    public void testGetProjectAdminsByProject() {

        when(projectPrivilegeMapper.selectByExample( Mockito.any(ProjectPrivilegeExample.class ))).thenReturn(projectPrivilegesSingle);
        
        projectPrivilegeServiceImpl.getProjectAdminsByProject(ModuleHelper.projectId);
        verify(projectPrivilegeMapper).selectByExample(Matchers.argThat(new ExampleMatcher<ProjectPrivilegeExample>() {
            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "projectId", ModuleHelper.projectId)
                        && CriterionVerifier.verifyEqualTo(example, "isAdmin", true);
            }
        }));
        verify(userService, times(1)).getById(any(Integer.class));
    }

    @Test
    public void testAddProjectAdmin() {
        when(projectPrivilegeMapper.selectByExample( Mockito.any(ProjectPrivilegeExample.class ))).thenReturn(projectPrivileges);
        
        projectPrivilegeServiceImpl.addProjectAdmin(ModuleHelper.projectId, ModuleHelper.userId);
        
//        verify(projectPrivilegeMapper).selectByExample(Matchers.argThat(new ExampleMatcher<ProjectPrivilegeExample>() {
//            @Override
//            public boolean matches(BaseExample example) {
//                return CriterionVerifier.verifyEqualTo(example, "userId", ModuleHelper.userId);
//            }
//        }));
    }

    @Test
    public void testRemoveAdmin() {
        when(projectPrivilegeMapper.selectByExample( Mockito.any(ProjectPrivilegeExample.class ))).thenReturn(projectPrivileges);
        
        projectPrivilegeServiceImpl.removeProjectAdmin(ModuleHelper.projectId, ModuleHelper.userId);
//        
//        verify(projectPrivilegeMapper).updateByPrimaryKey(Matchers.argThat(new ObjectMatcher<ProjectPrivilege>() {
//            @Override
//            public boolean verifymatches(ProjectPrivilege privilege) {
//                return privilege.getIsAdmin().equals(false) && privilege.getProjectId().equals(ModuleHelper.projectId)
//                        && privilege.getUserId().equals(ModuleHelper.userId);
//            }
//        }));
    }
}
