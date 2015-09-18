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

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.when;

import java.util.ArrayList;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;

import com.onboard.domain.mapper.model.ProjectTodoIdExample;
import com.onboard.domain.model.ProjectTodoId;
import com.onboard.domain.model.type.ProjectItem;
import com.onboard.service.collaboration.impl.IdInProjectServiceImpl;
import com.onboard.test.moduleutils.ModuleHelper;

public class IdInProjectServiceImplTest extends AbstractIdInProjectServiceTest{
    
    @InjectMocks
    private IdInProjectServiceImpl idInProjectServiceImpl;
    
    @Test
    public void testGetNextIdByProjectIdWithEmptyList() {
        when(mockedProjectTodoIdMapper.selectByExample(Mockito.any(ProjectTodoIdExample.class))).thenReturn(new ArrayList<ProjectTodoId>());
        
        Integer retInt = idInProjectServiceImpl.getNextIdByProjectId(ModuleHelper.projectId);
        
        assertSame(retInt, 1);
    }
    
    @Test
    public void testGetNextIdByProjectIdWithList() {
        when(mockedProjectTodoIdMapper.selectByExample(Mockito.any(ProjectTodoIdExample.class))).thenReturn(projectTodoIdListWithOne);
        
        Integer retInt = idInProjectServiceImpl.getNextIdByProjectId(ModuleHelper.projectId);
        
        assertSame(retInt, ModuleHelper.todoId);
    }
    
    /*
    @Test
    public void testGetNextIdByProjectIdWithListMoreThanOneElement() {
        when(mockedProjectTodoIdMapper.selectByExample(Mockito.any(ProjectTodoIdExample.class))).thenReturn(projectTodoIdListWithMore);
        
        Integer retInt = idInProjectServiceImpl.getNextIdByProjectId(ModuleHelper.projectId);
        
        assertSame(retInt, 1);
    }
    */
    
    @Test
    public void testGetWithNull() {
        idInProjectServiceImpl.setProjectItemServices(projectItemServices);
        
        when(mockedProjectItemService.getItemByIdInProject(Mockito.any(Integer.class), Mockito.any(Integer.class))).thenReturn(null);
        
        ProjectItem ret = idInProjectServiceImpl.get(ModuleHelper.projectId, ModuleHelper.id);
        
        assertSame(ret, null);
    }
    
    @Test
    public void testGet() {
        idInProjectServiceImpl.setProjectItemServices(projectItemServices);
        
        when(mockedProjectItemService.getItemByIdInProject(Mockito.any(Integer.class), Mockito.any(Integer.class))).thenReturn(projectItem);
        
        ProjectItem ret = idInProjectServiceImpl.get(ModuleHelper.projectId, ModuleHelper.id);
        
        assertSame(ret, projectItem);
    }

}
