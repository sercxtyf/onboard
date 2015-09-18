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

import org.springframework.beans.BeanUtils;

import com.google.common.base.Function;
import com.onboard.domain.model.Project;
import com.onboard.dto.ProjectDTO;

public class ProjectTransform {

    public static final Function<Project, ProjectDTO> PROJECT_DTO_FUNCTION = new Function<Project, ProjectDTO>() {
        @Override
        public ProjectDTO apply(Project input) {
            return projectToProjectDTO(input);
        }
    };

    public static ProjectDTO projectToProjectDTO(Project project) {
        ProjectDTO projectDTO = new ProjectDTO();
        BeanUtils.copyProperties(project, projectDTO);
        return projectDTO;
    }

    public static Project projectDTOtoProject(ProjectDTO projectDTO) {
        Project project = new Project();
        BeanUtils.copyProperties(projectDTO, project);

        return project;
    }
}
