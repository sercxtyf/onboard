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
package com.onboard.service.collaboration.activity;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.mockito.InjectMocks;

import com.onboard.domain.model.Activity;
import com.onboard.domain.model.type.BaseProjectItem;
import com.onboard.service.activity.ActivityActionType;
import com.onboard.test.moduleutils.ModuleHelper;

public class ProjectActivityGeneratorTest extends AbstractProjectActivityGenerator {
    @InjectMocks
    private ProjectActivityGenerator projectActivityGenerator;
    
    @Test
    public void testGenerateCreateActivity() {
        Activity ret = projectActivityGenerator.generateCreateActivity(project);

        assertEquals(ret.getAction(), ActivityActionType.CREATE);
        assertEquals(ret.getCompanyId(), new Integer(ModuleHelper.companyId));
        assertEquals(ret.getProjectId(), new Integer(ModuleHelper.projectId));
        assertEquals(ret.getProjectName(), ModuleHelper.projectName);
    }
    
    @Test
    public void testGenerateUpdateActivity() {
        Activity ret;
        ret = projectActivityGenerator.generateUpdateActivity(project, projectDeleted);
        assertEquals(ret.getAction(), ActivityActionType.DISCARD);
        ret = projectActivityGenerator.generateUpdateActivity(projectDeleted, project);
        assertEquals(ret.getAction(), ActivityActionType.RECOVER);
        ret = projectActivityGenerator.generateUpdateActivity(project, projectArchive);
        assertEquals(ret.getAction(), ActivityActionType.ARCHIVE);
        ret = projectActivityGenerator.generateUpdateActivity(projectArchive, project);
        assertEquals(ret.getAction(), ActivityActionType.ACTIVATE);
        ret = projectActivityGenerator.generateUpdateActivity(project, project);
        assertEquals(ret.getAction(), ActivityActionType.UPDATE);
        ret = projectActivityGenerator.generateUpdateActivity(project, projectName);
        assertEquals(ret.getAction(), ActivityActionType.UPDATE);
        assertEquals(ret.getContent(), String.format(ProjectActivityGenerator.NAME_UPDATE, project.getName(), projectName.getName()));
        ret = projectActivityGenerator.generateUpdateActivity(project, projectDescription);
        assertEquals(ret.getAction(), ActivityActionType.UPDATE);
        assertEquals(ret.getContent(), String.format(ProjectActivityGenerator.DESCRIPTION_UPDATE, project.getDescription(), projectDescription.getDescription()));
        ret = projectActivityGenerator.generateUpdateActivity(project, projectNameAndDescription);
        assertEquals(ret.getAction(), ActivityActionType.UPDATE);
        assertEquals(ret.getContent(), String.format(ProjectActivityGenerator.NAME_AND_DESCRIPTION_UPDATE, project.getName(), projectNameAndDescription.getName(), project.getDescription(),projectNameAndDescription.getDescription()));
    }
    
    @Test
    public void testModelType() {
        String ret = projectActivityGenerator.modelType();
        assertEquals(ret, "project");
    }
    
    @Test 
    public void testEnrichModel() {
        BaseProjectItem ret = projectActivityGenerator.enrichModel(project);
        verify(mockedProjectService, times(1)).getById(anyInt());
        assertEquals(ret.getId(), new Integer(ModuleHelper.projectId));
        assertEquals(ret.getCompanyId(), new Integer(ModuleHelper.companyId));
        assertEquals(ret.getCreatorId(), new Integer(ModuleHelper.creatorId));
        assertEquals(ret.getProjectId(), new Integer(ModuleHelper.projectId));
    }
}
