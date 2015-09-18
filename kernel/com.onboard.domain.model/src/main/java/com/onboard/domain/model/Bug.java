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
import com.onboard.domain.mapper.model.BugObject;
import com.onboard.domain.model.type.Boardable;
import com.onboard.domain.model.type.Commentable;
import com.onboard.domain.model.type.Iterable;
import com.onboard.domain.model.type.ProjectItem;
import com.onboard.domain.model.type.Recommendable;

/**
 * Domain model: Bug
 * 
 * @generated_by_elevenframework
 * 
 */
public class Bug extends BugObject implements Commentable, Iterable, Boardable, ProjectItem, Recommendable {

    public enum BugPriorities {
        BLOCKER(1), CRITICAL(2), MAJOR(3), NORMAL(4), MINOR(5);

        private int priority;

        private BugPriorities(int priority) {
            this.priority = priority;
        }

        public int getValue() {
            return this.priority;
        }

        public static int getDefaultBugPriority() {
            return MAJOR.getValue();
        }

        public static List<Integer> getAllBugPriorities() {
            return Lists.newArrayList(BLOCKER.getValue(), CRITICAL.getValue(), MAJOR.getValue(), NORMAL.getValue(),
                    MINOR.getValue());
        }
    }

    public enum BugStatus {
        TODO(1), INPROGESS(2), FIXED(3), APPROVED(4), REVIEWED(5), VERIFIED(6), CLOSED(0);

        private int status;

        private BugStatus(int status) {
            this.status = status;
        }

        public int getValue() {
            return this.status;
        }

        public static List<Integer> getDefaultBugStatus() {
            return Lists.newArrayList(TODO.getValue(), INPROGESS.getValue(), CLOSED.getValue());
        }

        public static List<Integer> getAllBugStatus() {
            return Lists.newArrayList(TODO.getValue(), INPROGESS.getValue(), FIXED.getValue(), APPROVED.getValue(),
                    REVIEWED.getValue(), VERIFIED.getValue(), CLOSED.getValue());
        }
    }

    private static final long serialVersionUID = -3380782637989646470L;

    private List<Comment> comments;

    private List<User> subscribers;

    private User assignee;

    private Project project;

    public Bug() {
        super();
    }

    public Bug(int id) {
        super(id);
    }

    public Bug(BugObject obj) {
        super(obj);
    }

    public Bug(Boolean deleted) {
        super(deleted);
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
    public String getSubscribableType() {
        return getType();
    }

    @Override
    public Integer getSubscribableId() {
        return getId();
    }

    @Override
    public String getSubscribableSubject() {
        return getTitle();
    }

    @Override
    public List<User> getSubscribers() {
        return subscribers;
    }

    @Override
    public void setSubscribers(List<User> users) {
        subscribers = users;

    }

    @Override
    public String getType() {
        return "bug";
    }

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
        return getTitle();
    }

    @Override
    public Boolean getCompleted() {
        return getStatus().equals(0);
    }

    @Override
    public void setCompleted(Boolean completed) {
        if (completed) {
            setStatus(0);
        } else {
            setStatus(1);
        }
    }

    @Override
    public List<Boardable> getBoardables() {
        return Lists.newArrayList((Boardable) this);
    }

    @Override
    public String getIterationStatus() {

        String retVal = "";
        switch (getStatus()) {
        case 0:
            retVal = IterationItemStatus.CLOSED.getValue();
            break;
        case 1:
            retVal = IterationItemStatus.TODO.getValue();
            break;
        case 2:
            retVal = IterationItemStatus.INPROGESS.getValue();
            break;
        case 3:
            retVal = IterationItemStatus.FIXED.getValue();
            break;
        case 4:
            retVal = IterationItemStatus.APPROVED.getValue();
            break;
        case 5:
            retVal = IterationItemStatus.REVIEWED.getValue();
            break;
        case 6:
            retVal = IterationItemStatus.VERIFIED.getValue();
        default:
            retVal = IterationItemStatus.TODO.getValue();
            break;
        }
        return retVal;
    }

    private Boolean iterationCompleted;
    private Date iterationCompletedTime;

    @Override
    public Boolean getIterationCompleted() {
        return iterationCompleted;
    }

    @Override
    public void setIterationCompleted(Boolean iterationCompleted) {
        this.iterationCompleted = iterationCompleted;
    }

    @Override
    public Date getIterationCompletedTime() {
        return iterationCompletedTime;
    }

    @Override
    public void setIterationCompletedTime(Date iterationCompletedTime) {
        this.iterationCompletedTime = iterationCompletedTime;
    }

    @Override
    public String generateText() {
        return getTitle();
    }

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
        return getCreatedTime();
    }

    @Override
    public void setUpdated(Date updated) {
    }

    @Override
    public boolean trashRequried() {
        return true;
    }

}
