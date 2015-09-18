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

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.onboard.domain.model.type.Indexable;

public class IndexDocumentImpl implements IndexDocument {
    
    private String id;
    private String modelType;
    private Integer modelId;
    
    private Integer projectId;
    private String projectName;
    private Integer companyId;
    
    private Date createdTime;
    private Integer creatorId;
    private String creatorName;
    private String creatorAvatar;
    private List<Integer> relatorIds;
    
    private String title;
    private String attachTitle;
    private String content;
    
    private Indexable indexable;
    private Map<String, Object> extendIndexFileds;

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getModelType() {
        return modelType;
    }

    public void setModelType(String modelType) {
        this.modelType = modelType;
    }

    @Override
    public Integer getModelId() {
        return modelId;
    }

    public void setModelId(Integer modelId) {
        this.modelId = modelId;
    }

    @Override
    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    @Override
    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    @Override
    public Integer getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Integer companyId) {
        this.companyId = companyId;
    }

    @Override
    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    @Override
    public Integer getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Integer creatorId) {
        this.creatorId = creatorId;
    }

    @Override
    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    @Override
    public String getCreatorAvatar() {
        return creatorAvatar;
    }

    public void setCreatorAvatar(String creatorAvatar) {
        this.creatorAvatar = creatorAvatar;
    }

    @Override
    public List<Integer> getRelatorIds() {
        return relatorIds;
    }

    public void setRelatorIds(List<Integer> relatorIds) {
        this.relatorIds = relatorIds;
    }

    @Override
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public Indexable getIndexable() {
        return indexable;
    }

    public void setIndexable(Indexable indexable) {
        this.indexable = indexable;
    }

    @Override
    public Map<String, Object> getExtendIndexFields() {
        return extendIndexFileds;
    }

    public void setExtendIndexFileds(Map<String, Object> extendIndexFileds) {
        this.extendIndexFileds = extendIndexFileds;
    }

    @Override
    public boolean needIndex() {
        return !(getTitle() == null && getContent() == null && getAttachTitle() == null);
    }

    @Override
    public String getAttachTitle() {
        return attachTitle;
    }

    public void setAttachTitle(String attachTitle) {
        this.attachTitle = attachTitle;
    }

    public Map<String, Object> getExtendIndexFileds() {
        return extendIndexFileds;
    }

}
