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

import com.onboard.domain.model.type.ProjectItem;

public interface IdInProjectService {
    
	/**
	 * Get next available IdInProject of a project
	 * 
	 * @param projectId - the project
	 * @return an available IdInProject
	 */
    Integer getNextIdByProjectId(Integer projectId);
    
    /**
     * Get next available IdInProject with step of a project
     * 
     * @param projectId - the project
     * @param step - step
     * @return an available IdInProject
     */
    Integer getNextIdByProjectIdWithStep(Integer projectId, Integer step); 
    
    /**
     * Get a projectItem by its projectId and IdInProject
     * 
     * @param projectId
     * @param idInProject
     * @return a projectItem fits the requirements
     */
    ProjectItem get(Integer projectId, Integer idInProject);

}
