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
package com.onboard.service.common.identifiable;

import com.onboard.domain.mapper.model.common.BaseExample;
import com.onboard.domain.model.type.BaseOperateItem;
import com.onboard.service.base.BaseService;

/**
 * 根据类型和Id获取对象实例的服务，对象需要在Kernel中经过注册
 * 
 * @author yewei
 * 
 */

public interface IdentifiableManager {
    
    BaseService<? extends BaseOperateItem, ? extends BaseExample> getIdentifiableService(String type);

    public BaseOperateItem getIdentifiableByTypeAndId(String type, Integer id);

    public BaseOperateItem getIdentifiableWithDetailByTypeAndId(String type, Integer id);

    public boolean identifiableRegistered(String type);

    public void deleteIdentifiableByTypeAndId(String type, int id);
}
