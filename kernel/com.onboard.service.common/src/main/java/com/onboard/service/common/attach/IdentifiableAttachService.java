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
package com.onboard.service.common.attach;

import java.util.List;

import com.onboard.domain.model.type.BaseProjectItem;
import com.onboard.domain.model.type.IdentifiableOperator;

/**
 * 获取关联类型集合的服务，可在其中配置关联类型与主体类型的属性
 * 
 * @author XingLiang
 * 
 */
public interface IdentifiableAttachService extends IdentifiableOperator {

    /**
     * 关联类型
     * 
     * @return
     */
    String attachType();

    /**
     * 通过主体类型对象id获取与之相关的所有attachType类型对象
     * 
     * @param attachId
     * @return
     */
    public abstract List<? extends BaseProjectItem> getIdentifiablesByAttachId(int attachId);

    /**
     * 通过主体类型对象id获取与之相关的所有attachType类型对象
     * 
     * @param attachType
     * @param attachId
     * @return
     */
    public abstract List<? extends BaseProjectItem> getIdentifiablesByAttachId(String attachType, int attachId);
}
