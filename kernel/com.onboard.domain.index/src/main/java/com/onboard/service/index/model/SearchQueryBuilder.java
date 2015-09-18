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
package com.onboard.service.index.model;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;

public class SearchQueryBuilder {
    
    private final SearchQuery searchQuery = new SearchQuery();
    
    public static SearchQueryBuilder getBuilder(){
        return new SearchQueryBuilder();
    }
    
    public SearchQuery build(){
        return searchQuery;
    }

    public SearchQueryBuilder modelTypes(List<String> modelTypes){
        searchQuery.setModelTypes(modelTypes);
        return this;
    }
    public SearchQueryBuilder projectIds(List<Integer> projectIds){
        searchQuery.setProjectIds(projectIds);
        return this;
    }
    public SearchQueryBuilder creatorIds(List<Integer> creatorIds){
        searchQuery.setCreatorIds(creatorIds);
        return this;
    }
    public SearchQueryBuilder companyIds(List<Integer> companyIds){
        searchQuery.setCompanyIds(companyIds);
        return this;
    }
    public SearchQueryBuilder relatorIds(List<Integer> relatorIds){
        searchQuery.setRelatorIds(relatorIds);
        return this;
    }
    
    public SearchQueryBuilder otherQueryStrings(Map<String, List<Object>> queryStrings) {
        Map<String, List<Object>> originQueryStrings = searchQuery.getQueryStrings();
        if(originQueryStrings == null){
            searchQuery.setQueryStrings(queryStrings);
        }else {
            originQueryStrings.putAll(queryStrings);
        }
        return this;
    }
    
    public SearchQueryBuilder otherQueryString(String fieldName, List<Object> queryStrings) {
        Map<String, List<Object>> originQueryStrings = searchQuery.getQueryStrings();
        if(originQueryStrings == null){
            originQueryStrings = Maps.newHashMap();
            searchQuery.setQueryStrings(originQueryStrings);
        }
        originQueryStrings.put(fieldName, queryStrings);
        return this;
    }

}
