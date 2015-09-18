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

public class SearchQuery {
    
    private List<String> modelTypes;
    private List<Integer> projectIds;
    private List<Integer> companyIds;
    private List<Integer> creatorIds;
    private List<Integer> relatorIds;
    private Map<String, List<Object>> queryStrings;
    
    public List<String> getModelTypes() {
        return modelTypes;
    }
    public void setModelTypes(List<String> modelTypes) {
        this.modelTypes = modelTypes;
    }
    public List<Integer> getProjectIds() {
        return projectIds;
    }
    public void setProjectIds(List<Integer> projectIds) {
        this.projectIds = projectIds;
    }
    public List<Integer> getCompanyIds() {
        return companyIds;
    }
    public void setCompanyIds(List<Integer> companyIds) {
        this.companyIds = companyIds;
    }
    public List<Integer> getCreatorIds() {
        return creatorIds;
    }
    public void setCreatorIds(List<Integer> creatorIds) {
        this.creatorIds = creatorIds;
    }
    public List<Integer> getRelatorIds() {
        return relatorIds;
    }
    public void setRelatorIds(List<Integer> relatorIds) {
        this.relatorIds = relatorIds;
    }
    public Map<String, List<Object>> getQueryStrings() {
        return queryStrings;
    }
    public void setQueryStrings(Map<String, List<Object>> queryStrings) {
        this.queryStrings = queryStrings;
    }

}
