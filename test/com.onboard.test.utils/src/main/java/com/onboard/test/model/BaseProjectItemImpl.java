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
package com.onboard.test.model;

import java.util.Date;

import com.onboard.domain.mapper.model.common.BaseItem;
import com.onboard.domain.model.type.BaseProjectItem;
import com.onboard.test.moduleutils.ModuleHelper;

public class BaseProjectItemImpl implements BaseProjectItem {
    
    private static final long serialVersionUID = 2098285158250422435L;
    private Integer id;
    private String type;
    private Integer projectId = ModuleHelper.projectId;
    private Integer companyId = ModuleHelper.companyId;
    private Integer creatorId = ModuleHelper.creatorId;
    private String creatorName = ModuleHelper.creatorName;
    private String creatorAvatar = ModuleHelper.creatorAvatar;
    private Date created = ModuleHelper.created;
    private Date updated = ModuleHelper.updated;
    private Boolean deleted = false;
    private boolean trashRequired = false;
    
    public BaseProjectItemImpl() {
        super();
    }
    
    public BaseProjectItemImpl(Integer id, String type){
        super();
        this.id = id;
        this.type = type;
    }
    
    @Override
    public Integer getId() {
        return id;
    }
    @Override
    public void setId(Integer id) {
        this.id = id;
    }
    @Override
    public Integer getProjectId() {
        return projectId;
    }
    @Override
    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }
    @Override
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    @Override
    public Integer getCompanyId() {
        return companyId;
    }
    @Override
    public void setCompanyId(Integer companyId) {
        this.companyId = companyId;
    }
    @Override
    public Integer getCreatorId() {
        return creatorId;
    }
    @Override
    public void setCreatorId(Integer creatorId) {
        this.creatorId = creatorId;
    }
    @Override
    public String getCreatorName() {
        return creatorName;
    }
    @Override
    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }
    @Override
    public String getCreatorAvatar() {
        return creatorAvatar;
    }
    @Override
    public void setCreatorAvatar(String creatorAvatar) {
        this.creatorAvatar = creatorAvatar;
    }
    @Override
    public Date getCreated() {
        return created;
    }
    @Override
    public void setCreated(Date created) {
        this.created = created;
    }
    @Override
    public Date getUpdated() {
        return updated;
    }
    @Override
    public void setUpdated(Date updated) {
        this.updated = updated;
    }
    @Override
    public Boolean getDeleted() {
        return deleted;
    }
    @Override
    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }
    public boolean isTrashRequired() {
        return trashRequired;
    }
    public void setTrashRequired(boolean trashRequired) {
        this.trashRequired = trashRequired;
    }
    @Override
    public boolean trashRequried() {
        return trashRequired;
    }
    @Override
    public BaseItem copy() {
        return this;
    }

}
