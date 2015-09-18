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

import com.onboard.domain.mapper.model.TodolistObject;
import com.onboard.domain.model.type.Commentable;
import com.onboard.domain.model.type.Indexable;
import com.onboard.domain.model.type.Recommendable;
import com.onboard.domain.model.type.Subscribable;

/**
 * 领域模型：Todolist
 * 
 * @author yewei, ruici
 * 
 */
public class Todolist extends TodolistObject implements Commentable, Subscribable, Indexable, Serializable, Recommendable {

    private static final long serialVersionUID = 1L;

    private List<Todo> todos;

    private List<Todo> dicardTodos;

    private List<Comment> comments;

    private List<User> subscribers;

    private Boolean completed;

    private Project project;

    public List<Todo> getDicardTodos() {
        return dicardTodos;
    }

    public void setDicardTodos(List<Todo> dicardTodos) {
        this.dicardTodos = dicardTodos;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Todolist() {
        super();
    }

    public Todolist(int id) {
        super(id);
    }

    public Todolist(boolean deleted) {
        super(deleted);
    }

    public Todolist(int id, boolean deleted) {
        super(id, deleted);
    }

    public Todolist(TodolistObject obj) {
        super(obj);
    }

    public List<Todo> getTodos() {
        return todos;
    }

    public void setTodos(List<Todo> todos) {
        this.todos = todos;
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
        return "todolist";
    }

    public Boolean getCompleted() {
        if (completed != null && completed) {
            return true;
        }
        if (this.todos == null || this.todos.size() == 0) {
            return false;
        }
        for (Todo todo : this.todos) {
            if (!todo.getStatus().equals(IterationItemStatus.CLOSED.getValue())) {
                return false;
            }
        }

        return true;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    @Override
    public String getCommentSubject() {
        return this.getName();
    }

    @Override
    public String getSubscribableType() {
        return "todolist";
    }

    @Override
    public Integer getSubscribableId() {
        return this.getId();
    }

    @Override
    public String getSubscribableSubject() {
        return this.getName();
    }

    @Override
    public String generateText() {
        return String.format("%s %s", getName(), getDescription());
    }

    @Override
    public boolean trashRequried() {
        return true;
    }

}
