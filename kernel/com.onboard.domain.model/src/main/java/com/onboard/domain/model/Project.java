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

import java.util.List;

import com.onboard.domain.mapper.model.ProjectObject;
import com.onboard.domain.model.type.BaseProjectItem;
import com.onboard.domain.model.type.Recommendable;

/**
 * 领域模型：Project
 * 
 * @author yewei, ruici
 * 
 */
public class Project extends ProjectObject implements BaseProjectItem, Recommendable {

    private static final long serialVersionUID = 1L;

    private List<Activity> activities;

    private List<Todolist> todolists;

    private List<Topic> topics;

    private List<Attachment> attachments;

    private List<User> users;

    private User creator;

    private List<User> admins;

    private Iteration activeIteration;

    public Iteration getActiveIteration() {
        return activeIteration;
    }

    public void setActiveIteration(Iteration activeIteration) {
        this.activeIteration = activeIteration;
    }

    public Project() {
        super();
    }

    public Project(int id) {
        super(id);
    }

    public Project(boolean deleted) {
        super(deleted);
    }

    public Project(int id, boolean deleted) {
        super(id, deleted);
    }

    public Project(ProjectObject obj) {
        super(obj);
    }

    public List<Todolist> getTodolists() {
        return todolists;
    }

    public void setTodolists(List<Todolist> todolists) {
        this.todolists = todolists;
    }

    public List<Topic> getTopics() {
        return topics;
    }

    public List<Activity> getActivities() {
        return activities;
    }

    public void setActivities(List<Activity> activities) {
        this.activities = activities;
    }

    public void setTopics(List<Topic> topics) {
        this.topics = topics;
    }

    @Override
    public String getType() {
        return "project";
    }

    public List<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public List<User> getAdmins() {
        return admins;
    }

    public void setAdmins(List<User> admins) {
        this.admins = admins;
    }

    @Override
    public String getCreatorName() {
        if (getCreator() != null) {
            return getCreator().getName();
        }
        return null;
    }

    @Override
    public String generateText() {
        return String.format("%s %s", getName(), getDescription());
    }

    // TODO .... I don't wanna say a word
    @Override
    public Integer getProjectId() {
        return getId();
    }

    @Override
    public void setProjectId(Integer projectId) {
    }

    @Override
    public boolean trashRequried() {
        return true;
    }

}
