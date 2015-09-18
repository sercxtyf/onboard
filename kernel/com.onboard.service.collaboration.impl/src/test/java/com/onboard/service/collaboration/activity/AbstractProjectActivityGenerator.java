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

import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.onboard.domain.model.Project;
import com.onboard.service.collaboration.ProjectService;
import com.onboard.test.moduleutils.ModuleHelper;

@RunWith(MockitoJUnitRunner.class)
public abstract class AbstractProjectActivityGenerator {
    @Mock
    protected ProjectService mockedProjectService;
    
    protected Project project, projectDeleted, projectArchive, projectName, projectDescription, projectNameAndDescription;
    
    @Before
    public void setupTest() {
        project = ModuleHelper.getASampleProject();
        project.setArchived(false);
        projectDeleted = ModuleHelper.getASampleProject();
        projectDeleted.setDeleted(true);
        projectArchive = ModuleHelper.getASampleProject();
        projectArchive.setArchived(true);
        projectName = ModuleHelper.getASampleProject();
        projectName.setName(ModuleHelper.name + "1");
        projectDescription = ModuleHelper.getASampleProject();
        projectDescription.setDescription(ModuleHelper.description + "1");
        projectNameAndDescription = ModuleHelper.getASampleProject();
        projectNameAndDescription.setName(ModuleHelper.name + "1");
        projectNameAndDescription.setDescription(ModuleHelper.description + "1");
        initProjectService();
    }
    
    /** initProjectService **/
    private void initProjectService() {
        when(mockedProjectService.getById(anyInt())).thenReturn(project);
    }
}
