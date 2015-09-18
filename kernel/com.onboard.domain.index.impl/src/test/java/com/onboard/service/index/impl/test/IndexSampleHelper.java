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
package com.onboard.service.index.impl.test;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.onboard.domain.mapper.model.common.BaseCriteria;
import com.onboard.domain.mapper.model.common.BaseExample;
import com.onboard.domain.mapper.model.common.BaseItem;
import com.onboard.domain.model.type.Indexable;
import com.onboard.service.index.custom.IndexableService;
import com.onboard.service.index.model.IndexDocument;
import com.onboard.test.moduleutils.ModuleHelper;

public class IndexSampleHelper {

    public static IndexableService getASampleIndexableService() {
        return new IndexableService() {

            @Override
            public String modelType() {
                return ModuleHelper.type;
            }

            @Override
            public IndexDocument indexableToIndexDocument(Indexable indexable) {
                return getASampleIndexDocument();
            }

            @Override
            public List<Indexable> getIndexablesByExample(com.onboard.domain.mapper.model.common.BaseExample baseExample) {
                return Lists.newArrayList();
            }
        };
    }

    public static IndexDocument getASampleIndexDocument() {
        return new IndexDocument() {

            @Override
            public void setTitle(String title) {

            }

            @Override
            public void setContent(String content) {

            }

            @Override
            public boolean needIndex() {
                return true;
            }

            @Override
            public String getTitle() {
                return ModuleHelper.title;
            }

            @Override
            public List<Integer> getRelatorIds() {
                return Lists.newArrayList();
            }

            @Override
            public String getProjectName() {
                return ModuleHelper.projectName;
            }

            @Override
            public Integer getProjectId() {
                return ModuleHelper.projectId;
            }

            @Override
            public String getModelType() {
                return ModuleHelper.modelType;
            }

            @Override
            public Integer getModelId() {
                return ModuleHelper.id;
            }

            @Override
            public Indexable getIndexable() {
                return getASampleIndexable();
            }

            @Override
            public String getId() {
                return ModuleHelper.content;
            }

            @Override
            public Map<String, Object> getExtendIndexFields() {
                return Maps.newHashMap();
            }

            @Override
            public String getCreatorName() {
                return ModuleHelper.creatorName;
            }

            @Override
            public Integer getCreatorId() {
                return ModuleHelper.creatorId;
            }

            @Override
            public String getCreatorAvatar() {
                return ModuleHelper.creatorAvatar;
            }

            @Override
            public Date getCreatedTime() {
                return ModuleHelper.created;
            }

            @Override
            public String getContent() {
                return ModuleHelper.content;
            }

            @Override
            public Integer getCompanyId() {
                return ModuleHelper.companyId;
            }

            @Override
            public String getAttachTitle() {
                return ModuleHelper.title;
            }
        };
    }

    public static Indexable getASampleIndexable() {
        return new Indexable() {

            @Override
            public Integer getId() {
                return ModuleHelper.id;
            }

            @Override
            public String getType() {
                return ModuleHelper.type;
            }

            @Override
            public Integer getProjectId() {
                return ModuleHelper.projectId;
            }

            @Override
            public Boolean getDeleted() {
                return false;
            }

            @Override
            public String getCreatorName() {
                return ModuleHelper.creatorName;
            }

            @Override
            public Integer getCreatorId() {
                return ModuleHelper.creatorId;
            }

            @Override
            public Integer getCompanyId() {
                return ModuleHelper.companyId;
            }

            @Override
            public void setProjectId(Integer projectId) {
                // TODO Auto-generated method stub

            }

            @Override
            public void setCompanyId(Integer companyId) {
                // TODO Auto-generated method stub

            }

            @Override
            public void setCreatorId(Integer creatorId) {
                // TODO Auto-generated method stub

            }

            @Override
            public void setCreatorName(String creatorName) {
                // TODO Auto-generated method stub

            }

            @Override
            public String getCreatorAvatar() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public void setCreatorAvatar(String creatorAvatar) {
                // TODO Auto-generated method stub

            }

            @Override
            public Date getCreated() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public void setCreated(Date created) {
                // TODO Auto-generated method stub

            }

            @Override
            public Date getUpdated() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public void setUpdated(Date updated) {
                // TODO Auto-generated method stub

            }

            @Override
            public void setDeleted(Boolean deleted) {
                // TODO Auto-generated method stub

            }

            @Override
            public void setId(Integer id) {
                // TODO Auto-generated method stub

            }

            @Override
            public BaseItem copy() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public boolean trashRequried() {
                // TODO Auto-generated method stub
                return false;
            }
        };
    }

    public static BaseExample getASampleBaseExample() {
        return new BaseExample() {

            @Override
            public boolean isDistinct() {
                return false;
            }

            @Override
            public int getStart() {
                return ModuleHelper.start;
            }

            @Override
            public List<BaseCriteria> getOredBaseCriteria() {
                return Lists.newArrayList();
            }

            @Override
            public String getOrderByClause() {
                return null;
            }

            @Override
            public int getLimit() {
                return ModuleHelper.limit;
            }

            @Override
            public void setOrderByClause(String orderByClause) {
                // TODO Auto-generated method stub

            }

            @Override
            public void setDistinct(boolean distinct) {
                // TODO Auto-generated method stub

            }

            @Override
            public void setStart(int start) {
                // TODO Auto-generated method stub

            }

            @Override
            public void setLimit(int limit) {
                // TODO Auto-generated method stub

            }

            @Override
            public void setLimit(int start, int limit) {
                // TODO Auto-generated method stub

            }

            @Override
            public void clear() {
                // TODO Auto-generated method stub

            }
        };
    }

    public static List<Indexable> getASampleIndexableList() {
        return Lists.newArrayList(getASampleIndexable());
    }
}
