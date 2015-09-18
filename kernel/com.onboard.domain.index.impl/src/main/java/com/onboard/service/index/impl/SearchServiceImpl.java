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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.onboard.service.index.SearchService;
import com.onboard.service.index.model.Page;
import com.onboard.service.index.model.SearchQuery;
import com.onboard.service.index.model.SearchResult;

/**
 * 基于{@link SearchItemBuilder}的{@link SearchService}实现
 * 
 * @author yewei
 * 
 */
@Service("searchServiceBean")
public class SearchServiceImpl implements SearchService {

    @Autowired
    private IndexServices indexServices;

    @Override
    public SearchResult search(String key, SearchQuery searchQuery, int start, int limit) {
        Page page = new Page(limit);
        page.setCurrentPageNumber(start / limit);
        return indexServices.getIndexService().search(key, searchQuery, page);
    }

}
