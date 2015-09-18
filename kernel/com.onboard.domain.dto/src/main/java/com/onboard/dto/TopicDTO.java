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
package com.onboard.dto;

import java.util.Date;

public class TopicDTO implements DTO {

    private Integer id;
    private Integer projectId;
    private String title;
    private String excerpt;
    private Integer lastUpdatorId;
    private String lastUpdatorName;
    private Integer refId;
    private String refType;
    private Boolean deleted;
    private Date created;
    private Date updated;
    private Integer companyId;
    private UserDTO updator;
    private Integer numOfComment;
    private UserDTO creator;
    private Boolean stick;

    public Boolean getStick() {
        return stick;
    }

    public void setStick(Boolean stick) {
        this.stick = stick;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getExcerpt() {
        return excerpt;
    }

    public void setExcerpt(String excerpt) {
        this.excerpt = excerpt;
    }

    public Integer getLastUpdatorId() {
        return lastUpdatorId;
    }

    public void setLastUpdatorId(Integer lastUpdatorId) {
        this.lastUpdatorId = lastUpdatorId;
    }

    public String getLastUpdatorName() {
        return lastUpdatorName;
    }

    public void setLastUpdatorName(String lastUpdatorName) {
        this.lastUpdatorName = lastUpdatorName;
    }

    public Integer getRefId() {
        return refId;
    }

    public void setRefId(Integer refId) {
        this.refId = refId;
    }

    public String getRefType() {
        return refType;
    }

    public void setRefType(String refType) {
        this.refType = refType;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public Integer getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Integer companyId) {
        this.companyId = companyId;
    }

    public Integer getNumOfComment() {
        return numOfComment;
    }

    public void setNumOfComment(Integer numOfComment) {
        this.numOfComment = numOfComment;
    }

    public UserDTO getUpdator() {
        return updator;
    }

    public void setUpdator(UserDTO updator) {
        this.updator = updator;
    }

    public UserDTO getCreator() {
        return creator;
    }

    public void setCreator(UserDTO creator) {
        this.creator = creator;
    }

}
