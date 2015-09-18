package com.onboard.frontend.model;

import java.util.Date;
import java.util.List;

public class Document {
    private Integer id;

    private Integer projectId;

    private String title;

    private Integer creatorId;

    private Boolean deleted;

    private Date created;

    private Date updated;

    private String creatorName;

    private Integer companyId;

    private Boolean isHomePage;

    private String content;

    private List<Comment> comments;

    private List<User> subscribers;

    public Document() {

    }

    public Document (Activity activity) {
        setId(activity.getAttachId());
        setCreated(activity.getCreated());
        setContent(activity.getContent());
        setCompanyId(activity.getCompanyId());
        setProjectId(activity.getProjectId());
        setCreatorId(activity.getCreatorId());
        setCreatorName(activity.getCreatorName());
        setTitle(activity.getTarget());
        setUpdated(activity.getCreated());
    }


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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Integer creatorId) {
        this.creatorId = creatorId;
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

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public Integer getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Integer companyId) {
        this.companyId = companyId;
    }

    public Boolean getIsHomePage() {
        return isHomePage;
    }

    public void setIsHomePage(Boolean isHomePage) {
        this.isHomePage = isHomePage;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public List<User> getSubscribers() {
        return subscribers;
    }

    public void setSubscribers(List<User> subscribers) {
        this.subscribers = subscribers;
    }

}
