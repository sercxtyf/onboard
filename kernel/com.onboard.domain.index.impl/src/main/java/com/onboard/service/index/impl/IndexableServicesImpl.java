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
package com.onboard.service.index.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.onboard.domain.model.type.Indexable;
import com.onboard.service.index.custom.IndexableService;
import com.onboard.service.index.custom.IndexableServices;

@Service("indexableServicesBean")
public class IndexableServicesImpl implements IndexableServices {

    private static final Map<String, IndexableService> indexableServices = Collections
            .synchronizedMap(new HashMap<String, IndexableService>());

    public synchronized void addIndexableService(IndexableService indexableService) {
        if (indexableService != null) {
            indexableServices.put(indexableService.modelType(), indexableService);
        }
    }

    public synchronized void removeIndexableService(IndexableService indexableService) {
        if (indexableService != null) {
            indexableServices.remove(indexableService.modelType());
        }
    }

    @Override
    public IndexableService getIndexableService(Indexable indexable) {
        if (indexable == null) {
            return null;
        }
        return indexableServices.get(indexable.getType());
    }

    @Override
    public IndexableService getIndexableService(String modelType) {
        if (modelType == null) {
            return null;
        }
        return indexableServices.get(modelType);
    }

}
