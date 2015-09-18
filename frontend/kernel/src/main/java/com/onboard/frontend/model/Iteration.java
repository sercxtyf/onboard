package com.onboard.frontend.model;

import java.util.Date;
import java.util.List;

/**
 * Created by XingLiang on 2014/12/19.
 */
public class Iteration {
    public enum IterationStatus {
        CREATED("created"), ACTIVE("active"), COMPLETED("completed");

        private String status;

        private IterationStatus(String status) {
            this.status = status;
        }

        public String getValue() {
            return this.status;
        }
    }
    private Integer id;
    private Date created;
    private String status;
    private Date startTime;
    private Date endTime;
    private Integer todoCount;
    private Integer indexId;
    private Integer creatorId;
    private Integer projectId;
    private Integer companyId;
    private String name;
    private List<Todo> todos;

    public List<Todo> getTodos() {
        return todos;
    }

    public void setTodos(List<Todo> todos) {
        this.todos = todos;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
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

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Integer getTodoCount() {
        return todoCount;
    }

    public void setTodoCount(Integer todoCount) {
        this.todoCount = todoCount;
    }

    public Integer getIndexId() {
        return indexId;
    }

    public void setIndexId(Integer indexId) {
        this.indexId = indexId;
    }

    public Integer getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Integer creatorId) {
        this.creatorId = creatorId;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
