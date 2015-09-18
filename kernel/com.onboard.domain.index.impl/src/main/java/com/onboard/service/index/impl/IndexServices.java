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

import java.util.List;

import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.onboard.domain.model.type.Indexable;
import com.onboard.service.index.custom.IndexService;
import com.onboard.service.index.model.Page;
import com.onboard.service.index.model.SearchQuery;
import com.onboard.service.index.model.SearchResult;

@Service("indexServicesBean")
public class IndexServices {
    
    private final List<IndexService> indexServices = Lists.newArrayList();
    private final IndexService nullIndexService = new NullIndexService();
    
    public synchronized void addIndexService(IndexService indexService) {
        if (indexService != null && !indexServices.contains(indexService)) {
            indexServices.add(indexService);
        }
    }

    public synchronized void removeIndexService(IndexService indexService) {
        if (indexService != null && indexServices.contains(indexService)) {
            indexServices.remove(indexService);
        }
    }
    
    public IndexService getIndexService(){
        if(indexServices.isEmpty()){
            return nullIndexService;
        }
        return indexServices.get(0);
    }
    
    public class NullIndexService implements IndexService{

        @Override
        public void addIndex(Indexable indexable) {
        }

        @Override
        public void deleteIndexById(String id) {
        }

        @Override
        public void deleteIndexByIdList(List<String> idList) {
        }

        @Override
        public SearchResult search(String key, SearchQuery searchQuery, Page page) {
            return new SearchResult();
        }


        @Override
        public List<String> suggest(String key) {
            return Lists.newArrayList();
        }

        @Override
        public void updateIndex(Indexable modifiedIndexable) {
        }
        
    }

}
