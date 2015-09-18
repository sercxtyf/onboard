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
package com.onboard.test.moduleutils;

import java.util.Date;

import com.onboard.domain.model.type.BaseProjectItem;
import com.onboard.test.model.BaseProjectItemImpl;

public class BaseProjectItemBuilder {
    
    private final BaseProjectItemImpl baseProjectItem;

    public BaseProjectItemBuilder() {
        super();
        baseProjectItem = new BaseProjectItemImpl();
    }
    
    public static BaseProjectItemBuilder getBuilder(){
        return new BaseProjectItemBuilder();
    }
    
    public static BaseProjectItemBuilder getBuilder(Integer id, String type){
        return new BaseProjectItemBuilder();
    }
    
    public BaseProjectItem build(){
        return baseProjectItem;
    }
    
    public BaseProjectItemBuilder setId(Integer id) {
        baseProjectItem.setId(id);
        return this;
    }
    public BaseProjectItemBuilder setProjectId(Integer projectId) {
        baseProjectItem.setProjectId(projectId);
        return this;
    }
    public BaseProjectItemBuilder setType(String type) {
        baseProjectItem.setType(type);
        return this;
    }
    public BaseProjectItemBuilder setCompanyId(Integer companyId) {
        baseProjectItem.setCompanyId(companyId);
        return this;
    }
    public BaseProjectItemBuilder setCreatorId(Integer creatorId) {
        baseProjectItem.setCreatorId(creatorId);
        return this;
    }
    public BaseProjectItemBuilder setCreatorName(String creatorName) {
        baseProjectItem.setCreatorName(creatorName);
        return this;
    }
    public BaseProjectItemBuilder setCreatorAvatar(String creatorAvatar) {
        baseProjectItem.setCreatorAvatar(creatorAvatar);
        return this;
    }
    public BaseProjectItemBuilder setCreated(Date created) {
        baseProjectItem.setCreated(created);
        return this;
    }
    public BaseProjectItemBuilder setUpdated(Date updated) {
        baseProjectItem.setUpdated(updated);
        return this;
    }
    public BaseProjectItemBuilder setDeleted(Boolean deleted) {
        baseProjectItem.setDeleted(deleted);
        return this;
    }
    public BaseProjectItemBuilder setTrashRequired(boolean trashRequired) {
        baseProjectItem.setTrashRequired(trashRequired);
        return this;
    }

}
