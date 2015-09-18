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
import java.util.List;

public class BugDTO implements DTO {

    private List<UserDTO> subscribers;
    private UserDTO bugAssigneeDTO;

    private Integer id;
    private Integer companyId;
    private Integer projectId;
    private String projectName;
    private Integer creatorId;
    private String creatorName;
    private String title;
    private Date createdTime;
    private Date completedTime;
    private Integer status;
    private Integer priority;
    private Integer assigneeId;
    private Boolean deleted;
    private Date dueTime;
    private String description;
    private String iterationStatus;
    private Integer bugType;
    private Integer idInProject;

    private Boolean iterationCompleted;
    private Date iterationCompletedTime;

    public List<UserDTO> getSubscribers() {
        return subscribers;
    }

    public void setSubscribers(List<UserDTO> subscribers) {
        this.subscribers = subscribers;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Integer companyId) {
        this.companyId = companyId;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public Integer getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Integer creatorId) {
        this.creatorId = creatorId;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public Date getCompletedTime() {
        return completedTime;
    }

    public void setCompletedTime(Date completedTime) {
        this.completedTime = completedTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Integer getAssigneeId() {
        return assigneeId;
    }

    public void setAssigneeId(Integer assigneeId) {
        this.assigneeId = assigneeId;
    }

    public Integer getBugType() {
        return bugType;
    }

    public void setBugType(Integer bugType) {
        this.bugType = bugType;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public Date getDueTime() {
        return dueTime;
    }

    public void setDueTime(Date dueTime) {
        this.dueTime = dueTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public UserDTO getBugAssigneeDTO() {
        return bugAssigneeDTO;
    }

    public void setBugAssigneeDTO(UserDTO bugAssigneeDTO) {
        this.bugAssigneeDTO = bugAssigneeDTO;
    }

    public String getType() {
        return "bug";
    }

    public String getIterationStatus() {
        return iterationStatus;
    }

    public void setIterationStatus(String iterationStatus) {
        this.iterationStatus = iterationStatus;
    }

    public Boolean getIterationCompleted() {
        return iterationCompleted;
    }

    public void setIterationCompleted(Boolean iterationCompleted) {
        this.iterationCompleted = iterationCompleted;
    }

    public Date getIterationCompletedTime() {
        return iterationCompletedTime;
    }

    public void setIterationCompletedTime(Date iterationCompletedTime) {
        this.iterationCompletedTime = iterationCompletedTime;
    }

    public Integer getIdInProject() {
        return idInProject;
    }

    public void setIdInProject(Integer idInProject) {
        this.idInProject = idInProject;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

}
