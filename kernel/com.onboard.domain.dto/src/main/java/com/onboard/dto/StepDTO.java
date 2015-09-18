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

public class StepDTO implements DTO {

    private Integer id;
    private String attachType;
    private Integer attachId;
    private String content;
    private Date dueDate;
    private Date createdTime;
    private Date updatedTime;
    private Integer creatorId;
    private String creatorName;
    private Integer assigneeId;
    private String status;
    private Date startTime;
    private Date completedTime;
    private Integer completerId;
    private Integer idInProject;
    private Integer projectId;
    private String projectName;
    private Integer companyId;
    private String attachName;

    private UserDTO assigneeDTO;
    private String iterationStatus;
    private Boolean iterationCompleted;
    private Date iterationCompletedTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAttachType() {
        return attachType;
    }

    public void setAttachType(String attachType) {
        this.attachType = attachType;
    }

    public Integer getAttachId() {
        return attachId;
    }

    public void setAttachId(Integer attachId) {
        this.attachId = attachId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public Date getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(Date updatedTime) {
        this.updatedTime = updatedTime;
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

    public Integer getAssigneeId() {
        return assigneeId;
    }

    public void setAssigneeId(Integer assigneeId) {
        this.assigneeId = assigneeId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getCompletedTime() {
        return completedTime;
    }

    public void setCompletedTime(Date completedTime) {
        this.completedTime = completedTime;
    }

    public Integer getCompleterId() {
        return completerId;
    }

    public void setCompleterId(Integer completerId) {
        this.completerId = completerId;
    }

    public UserDTO getAssigneeDTO() {
        return assigneeDTO;
    }

    public void setAssigneeDTO(UserDTO assigneeDTO) {
        this.assigneeDTO = assigneeDTO;
    }

    public String getType() {
        return "step";
    }

    public String getIterationStatus() {
        return iterationStatus;
    }

    public void setIterationStatus(String iterationStatus) {
        this.iterationStatus = iterationStatus;
    }

    public Integer getIdInProject() {
        return idInProject;
    }

    public void setIdInProject(Integer idInProject) {
        this.idInProject = idInProject;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public Integer getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Integer companyId) {
        this.companyId = companyId;
    }

    public String getAttachName() {
        return attachName;
    }

    public void setAttachName(String attachName) {
        this.attachName = attachName;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
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

}
