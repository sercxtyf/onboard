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
package com.onboard.domain.model;

import java.util.Date;
import java.util.List;

import com.google.common.collect.Lists;
import com.onboard.domain.mapper.model.StepObject;
import com.onboard.domain.model.type.Boardable;
import com.onboard.domain.model.type.Commentable;
import com.onboard.domain.model.type.Iterable;
import com.onboard.domain.model.type.ProjectItem;
import com.onboard.domain.model.type.Recommendable;

/**
 * Domain model: Step
 * 
 * @generated_by_elevenframework
 * 
 */
public class Step extends StepObject implements Boardable, Commentable, ProjectItem, Iterable, Recommendable {

    private static final long serialVersionUID = 1576626819949586075L;

    private User assignee;

    private Project project;

    public Step() {
        super();
    }

    public Step(int id) {
        super(id);
    }

    public Step(StepObject obj) {
        super(obj);
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    @Override
    public User getAssignee() {
        return assignee;
    }

    @Override
    public void setAssignee(User assignee) {
        this.assignee = assignee;
    }

    @Override
    public String getType() {
        return "step";
    }

    @Override
    public String getIterationStatus() {
        return getStatus();
    }

    @Override
    public String getSubscribableType() {
        return getType();
    }

    @Override
    public Integer getSubscribableId() {
        return getId();
    }

    @Override
    public String getSubscribableSubject() {
        return getContent();
    }

    private List<User> subscribers;

    @Override
    public List<User> getSubscribers() {
        return subscribers;
    }

    @Override
    public void setSubscribers(List<User> users) {
        subscribers = users;
    }

    private List<Comment> comments;

    @Override
    public List<Comment> getComments() {
        return comments;
    }

    @Override
    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    @Override
    public String getCommentSubject() {
        return getContent();
    }

    @Override
    public String generateText() {
        return getContent();
    }

    @Override
    public Boolean getCompleted() {
        return getStatus().equals(IterationItemStatus.CLOSED.getValue());
    }

    @Override
    public void setCompleted(Boolean completed) {
        if (completed) {
            setStatus(IterationItemStatus.CLOSED.getValue());
        } else {
            if (getCompleted()) {
                setStatus(IterationItemStatus.INPROGESS.getValue());
            }
        }
    }

    private Boolean iterationCompleted;
    private Date iterationCompletedTime;

    @Override
    public Boolean getIterationCompleted() {
        return iterationCompleted;
    }

    @Override
    public void setIterationCompleted(Boolean completed) {
        iterationCompleted = completed;
    }

    @Override
    public Date getIterationCompletedTime() {
        return iterationCompletedTime;
    }

    @Override
    public void setIterationCompletedTime(Date completedTime) {
        iterationCompletedTime = completedTime;
    }

    @Override
    public List<Boardable> getBoardables() {
        return Lists.newArrayList((Boardable) this);
    }

    /**
     * TODO: mode created time to created
     */
    @Override
    public Date getCreated() {
        return getCreatedTime();
    }

    @Override
    public void setCreated(Date created) {
        setCreatedTime(created);
    }

    @Override
    public Date getUpdated() {
        return getUpdatedTime();
    }

    @Override
    public void setUpdated(Date updated) {
        setUpdatedTime(updated);
    }

    @Override
    public boolean trashRequried() {
        return true;
    }

}
