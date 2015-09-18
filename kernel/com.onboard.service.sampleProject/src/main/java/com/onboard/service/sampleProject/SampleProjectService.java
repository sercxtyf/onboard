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
package com.onboard.service.sampleProject;

import com.onboard.domain.model.User;

/**
 * 创建示例项目服务
 * 
 * @author xingliang
 * 
 */
public interface SampleProjectService {

    /**
     * 在一个团队中创建一个用于示例的演示项目
     * @param companyId 需要创建演示项目的团队的主键
     * @param creator 所有操作的执行人，通常即为该团队的创建者
     */
    void createSampleProjectByCompanyId(Integer companyId, User creator);
}
