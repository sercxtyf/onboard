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

public class StoryDTO implements DTO {

    private Integer id;
    private Integer projectId;
    private Integer companyId;
    private String pre;
    private String post;
    private Double position;
    private Boolean deleted;
    private Date created;
    private Date update;
    private Date completedTime;
    private Boolean completed;
    private Integer creatorId;
    private String creatorName;
    private String description;
    private String acceptanceLevel;
    private Integer priority;
    private Integer parentStoryId;
    private Boolean completable;
    private Integer completedChildStoryCount;
    private Integer uncompletedChildStoryCount;
    private List<CommentDTO> comments;
    private List<UserDTO> subscribers;
    private List<StoryDTO> childStoryDTOs;
    private List<StepDTO> stepsDTOs;

    private Boolean iterationCompleted;
    private Date iterationCompletedTime;
    private Integer todoCount;
    private Integer finishedTodoCount;

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

    public Integer getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Integer companyId) {
        this.companyId = companyId;
    }

    public String getPre() {
        return pre;
    }

    public void setPre(String pre) {
        this.pre = pre;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public Double getPosition() {
        return position;
    }

    public void setPosition(Double position) {
        this.position = position;
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

    public Date getUpdate() {
        return update;
    }

    public void setUpdate(Date update) {
        this.update = update;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAcceptanceLevel() {
        return acceptanceLevel;
    }

    public void setAcceptanceLevel(String acceptanceLevel) {
        this.acceptanceLevel = acceptanceLevel;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Integer getParentStoryId() {
        return parentStoryId;
    }

    public void setParentStoryId(Integer parentStoryId) {
        this.parentStoryId = parentStoryId;
    }

    public Boolean getCompletable() {
        return completable;
    }

    public void setCompletable(Boolean completable) {
        this.completable = completable;
    }

    public List<CommentDTO> getComments() {
        return comments;
    }

    public void setComments(List<CommentDTO> comments) {
        this.comments = comments;
    }

    public List<UserDTO> getSubscribers() {
        return subscribers;
    }

    public void setSubscribers(List<UserDTO> subscribers) {
        this.subscribers = subscribers;
    }

    public Date getCompletedTime() {
        return completedTime;
    }

    public void setCompletedTime(Date completeTime) {
        completedTime = completeTime;
    }

    public String getType() {
        return "story";
    }

    public List<StoryDTO> getChildStoryDTOs() {
        return childStoryDTOs;
    }

    public void setChildStoryDTOs(List<StoryDTO> childStoryDTOs) {
        this.childStoryDTOs = childStoryDTOs;
    }

    public List<StepDTO> getStepsDTOs() {
        return stepsDTOs;
    }

    public List<StepDTO> getBoardableDTOs() {
        return stepsDTOs;
    }

    public void setStepsDTOs(List<StepDTO> stepsDTOs) {
        this.stepsDTOs = stepsDTOs;
    }

    public Integer getCompletedChildStoryCount() {
        return completedChildStoryCount;
    }

    public void setCompletedChildStoryCount(Integer completedChildStoryCount) {
        this.completedChildStoryCount = completedChildStoryCount;
    }

    public Integer getUncompletedChildStoryCount() {
        return uncompletedChildStoryCount;
    }

    public void setUncompletedChildStoryCount(Integer uncompletedChildStoryCount) {
        this.uncompletedChildStoryCount = uncompletedChildStoryCount;
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

    public Integer getTodoCount() {
        return todoCount;
    }

    public void setTodoCount(Integer todoCount) {
        this.todoCount = todoCount;
    }

    public Integer getFinishedTodoCount() {
        return finishedTodoCount;
    }

    public void setFinishedTodoCount(Integer finishedTodoCount) {
        this.finishedTodoCount = finishedTodoCount;
    }
}
