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
package com.onboard.service.common.identifiable.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.onboard.domain.mapper.model.common.BaseExample;
import com.onboard.domain.model.type.BaseOperateItem;
import com.onboard.domain.model.type.BaseProjectItem;
import com.onboard.service.base.BaseService;
import com.onboard.service.common.identifiable.IdentifiableManager;

/**
 * 实现为{@link BaseProjectItem}的对象对应的访问服务，需要注册到该类中
 * 
 * @author yewei
 * 
 */
@Service("identifiableManagerBean")
public class IdentifiableManagerImpl implements IdentifiableManager {
    
    public static Logger logger = LoggerFactory.getLogger(IdentifiableManagerImpl.class);

    private final Map<String, BaseService<? extends BaseOperateItem, ? extends BaseExample>> identifialbeMap = Collections
            .synchronizedMap(new HashMap<String, BaseService<? extends BaseOperateItem, ? extends BaseExample>>());

    public synchronized void addIdentifiableService(
            BaseService<? extends BaseOperateItem, ? extends BaseExample> baseService) {
        if (baseService != null) {
            identifialbeMap.put(baseService.getModelType(), baseService);
        }
    }

    public synchronized void removeIdentifiableService(
            BaseService<? extends BaseOperateItem, ? extends BaseExample> baseService) {
        if (baseService != null) {
            identifialbeMap.remove(baseService.getModelType());
        }
    }

    @Override
    public BaseOperateItem getIdentifiableByTypeAndId(String type, Integer id) {
        BaseService<? extends BaseOperateItem, ? extends BaseExample> identifiableService = identifialbeMap.get(type);
        if (identifiableService == null) {
            return null;
        }
        return identifiableService.getById(id);
    }

    @Override
    public boolean identifiableRegistered(String type) {
        return identifialbeMap.get(type) != null;
    }

    @Override
    public BaseOperateItem getIdentifiableWithDetailByTypeAndId(String type, Integer id) {
        BaseService<? extends BaseOperateItem, ? extends BaseExample> identifiableService = identifialbeMap.get(type);
        if (identifiableService == null) {
            return null;
        }
        return identifiableService.getByIdWithDetail(id);
    }

    @Override
    public void deleteIdentifiableByTypeAndId(String type, int id) {
        BaseService<? extends BaseOperateItem, ? extends BaseExample> identifiableService = identifialbeMap.get(type);
        if (identifiableService == null) {
            return;
        }
        identifiableService.delete(id);
    }

    @Override
    public BaseService<? extends BaseOperateItem, ? extends BaseExample> getIdentifiableService(String type) {
        return identifialbeMap.get(type);
    }
}
