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

import com.onboard.domain.mapper.model.IterationExample;
import com.onboard.domain.model.Iteration;
import com.onboard.domain.model.IterationAttach;
import com.onboard.domain.model.Project;
import com.onboard.domain.model.type.Iterable;
import com.onboard.service.base.BaseService;

/**
 * {@link Iteration} Service Interface
 * 
 * @generated_by_elevenframework
 * 
 */
public interface IterationService extends BaseService<Iteration, IterationExample> {

    /**
     * 获取project下的当前迭代
     * 
     * @param projectId
     * @return
     */
    Iteration getCurrentIterationByProjectId(int projectId);

    /**
     * 分页获取所有完成的迭代
     * 
     * @param projectId
     * @param start
     * @param limit
     * @return
     */
    List<Iteration> getCompleteIterationsByProjectId(int projectId, int start, int limit);
    
    /**
     * @param project
     * @return
     */
    Iteration addNewIterationForProject(Project project);
    
    /**
     * 根据迭代获取进入迭代的对象
     * @param iterationId
     * @return
     */
    List<Iterable> getIterablesByIteration(Integer iterationId);
    
    /**
     * 获取进入迭代以及看板的对象
     * @param iterationId
     * @return
     */
    List<Iterable> getIterablesWithBoardablesByIteration(Integer iterationId);
    
    /**
     * 添加进迭代
     * @param iterationId
     * @param modelType
     * @param modelId
     */
    void addIterable(IterationAttach iterationAttach);
    void addIterable(Iterable iterable);
    
    /**
     * 从迭代中移除
     * @param iterationId
     * @param modelType
     * @param modelId
     */
    void removeIterable(IterationAttach iterationAttach);
    
    /**
     * Remove an iterable from its iteration
     * 
     * @param iterable - the iterable to remove
     * @param iterationId - the id of its iteration
     */
    void removeIterable(Iterable iterable, Integer iterationId);
    
    /**
     * Get all completed iterations by projectId
     * 
     * @param projectId
     * @return
     */
    List<Iteration> getCompleteIterationsByProjectId(int projectId);

}
