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

import java.io.Serializable;
import java.util.List;

import com.onboard.domain.mapper.model.TodoObject;
import com.onboard.domain.model.type.Commentable;
import com.onboard.domain.model.type.Indexable;
import com.onboard.domain.model.type.ProjectItem;
import com.onboard.domain.model.type.Recommendable;
import com.onboard.domain.model.type.Subscribable;

/**
 * 领域模型：Todo
 * 
 * @author yewei, ruici
 * 
 */
public class Todo extends TodoObject implements Commentable, Subscribable, Indexable, Serializable, ProjectItem, Recommendable {

    private static final long serialVersionUID = 1L;

    private List<Comment> comments;

    private List<User> subscribers;

    private Todolist todolist;

    private User creator;

    private User assignee;

    private Project project;

    private String todolistName;

    public Todo() {
        super();
    }

    public Todo(int id) {
        super(id);
    }

    public Todo(boolean deleted) {
        super(deleted);
    }

    public Todo(int id, boolean deleted) {
        super(id, deleted);
    }

    public Todo(TodoObject obj) {
        super(obj);
    }

    public String getTodolistName() {
        return todolistName;
    }

    public void setTodolistName(String todolistName) {
        this.todolistName = todolistName;
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
    public List<User> getSubscribers() {
        return subscribers;
    }

    @Override
    public void setSubscribers(List<User> subscribers) {
        this.subscribers = subscribers;
    }

    @Override
    public String getType() {
        return "todo";
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public User getAssignee() {
        return assignee;
    }

    public void setAssignee(User assignee) {
        this.assignee = assignee;
    }

    public Todolist getTodolist() {
        return todolist;
    }

    public void setTodolist(Todolist todolist) {
        this.todolist = todolist;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    @Override
    public String getCommentSubject() {
        return getContent();
    }

    @Override
    public String getSubscribableType() {
        return "todo";
    }

    @Override
    public Integer getSubscribableId() {
        return getId();
    }

    @Override
    public String getSubscribableSubject() {
        return getContent();
    }

    @Override
    public String generateText() {
        String conent = getContent();
        conent = conent != null ? conent : "";
        String description = getDescription();
        description = description != null ? description : "";
        return String.format("%s %s", conent, description);
    }

    @Override
    public Integer getIdInProject() {
        return getProjectTodoId();
    }

    @Override
    public void setIdInProject(Integer id) {
        setProjectTodoId(id);
    }

    @Override
    public boolean trashRequried() {
        return true;
    }

}
