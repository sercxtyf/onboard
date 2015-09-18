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
package com.onboard.service.common.attach.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.onboard.domain.model.type.BaseProjectItem;
import com.onboard.domain.model.type.IdentifiableOperator;
import com.onboard.service.common.attach.IdentifiableAttachManager;
import com.onboard.service.common.attach.IdentifiableAttachService;

@Service("identifiableAttachManagerBean")
public class IdentifiableAttachManagerImpl implements IdentifiableAttachManager {

    /**
     * like this : {type : { attachType : @link{ IdentifiableAttachService }}}
     */
    private final Map<String, Map<String, IdentifiableAttachService>> identifiableAttachServiceMap = Collections
            .synchronizedMap(new HashMap<String, Map<String, IdentifiableAttachService>>());

    public synchronized void addIdentifiablAttacheService(IdentifiableAttachService identifiableAttachService) {
        if (identifiableAttachService != null) {
            String modelType = identifiableAttachService.modelType();
            String attachType = identifiableAttachService.attachType();
            if (identifiableAttachServiceMap.containsKey(modelType)) {
                identifiableAttachServiceMap.get(modelType).put(attachType, identifiableAttachService);
            } else {
                Map<String, IdentifiableAttachService> serviceMap = Maps.newHashMap();
                serviceMap.put(attachType, identifiableAttachService);
                identifiableAttachServiceMap.put(modelType, serviceMap);
            }
        }
    }

    public synchronized void removeIdentifiableService(IdentifiableAttachService identifiableAttachService) {
        if (identifiableAttachService != null) {
            String modelType = identifiableAttachService.modelType();
            String attachType = identifiableAttachService.attachType();
            if (identifiableAttachServiceMap.containsKey(modelType)
                    && identifiableAttachServiceMap.get(modelType).containsKey(attachType)) {
                Map<String, IdentifiableAttachService> map = identifiableAttachServiceMap.get(modelType);
                map.remove(attachType);
                if (0 == map.size()) {
                    identifiableAttachServiceMap.remove(modelType);
                }
            }
        }
    }

    @Override
    public List<? extends BaseProjectItem> getIdentifiablesByTypeAndAttachTypeAndId(String type, String attachType, Integer attachId) {
        IdentifiableAttachService identifialbeAttachService = null;
        if (identifiableAttachServiceMap.containsKey(type)) {
            if (identifiableAttachServiceMap.get(type).containsKey(attachType)) {
                identifialbeAttachService = identifiableAttachServiceMap.get(type).get(attachType);
            } else if (identifiableAttachServiceMap.get(type).containsKey(IdentifiableOperator.NONE_TYPE)) {
                identifialbeAttachService = identifiableAttachServiceMap.get(type).get(IdentifiableOperator.NONE_TYPE);
            }
        }
        if (identifialbeAttachService == null) {
            return Lists.newArrayList();
        }
        return identifialbeAttachService.getIdentifiablesByAttachId(attachType, attachId);
    }
}
