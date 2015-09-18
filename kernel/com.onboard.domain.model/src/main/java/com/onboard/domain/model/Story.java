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
import com.onboard.domain.mapper.model.StoryObject;
import com.onboard.domain.model.type.Boardable;
import com.onboard.domain.model.type.Commentable;
import com.onboard.domain.model.type.Iterable;
import com.onboard.domain.model.type.Recommendable;

/**
 * Domain model: Story
 * 
 * 需求
 * 
 */
public class Story extends StoryObject implements Commentable, Iterable, Recommendable {

    private static final long serialVersionUID = -7918493617476160113L;

    private List<Comment> comments;

    private List<Story> childStories;

    private List<User> subscribers;

    private List<Step> steps;

    private List<Boardable> boardables;

    private Integer todoCount;

    private Integer finishedTodoCount;

    private Integer completedChildStoryCount;
    private Integer uncompletedChildStoryCount;

    public Story() {
        super();
    }

    public Story(int id) {
        super(id);
    }

    public Story(boolean deleted) {
        super();
        setDeleted(deleted);
    }

    public Story(int id, boolean deleted) {
        super(id);
        setDeleted(deleted);
    }

    public Story(StoryObject obj) {
        super(obj);
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
        return getDescription();
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
        return "story";
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
        return getDescription();
    }

    public List<Story> getChildStories() {
        return childStories;
    }

    public void setChildStories(List<Story> childStories) {
        this.childStories = childStories;
    }

    public List<Step> getSteps() {
        return steps;
    }

    public void setSteps(List<Step> steps) {
        this.steps = steps;
    }

    @Override
    public List<Boardable> getBoardables() {
        if (boardables != null) {
            List<Boardable> boardables = Lists.newArrayList();
            boardables.addAll(steps);
        }
        return boardables;
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
        return String.format("%s %s %s", getPre(), getDescription(), getPost());
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

    @Override
    public boolean trashRequried() {
        return true;
    }

}
