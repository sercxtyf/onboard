package com.onboard.frontend.model;

import java.util.Date;

public class Topic {
    private Integer id;

    private Integer projectId;

    private String title;

    private String excerpt;

    private Integer lastUpdatorId;

    private String lastUpdatorName;

    private Integer refId;

    private String refType;

    private Boolean deleted;

    private Date created;

    private Date updated;

    private Integer companyId;

    private User lastUpdator;

    public Topic() {

    }

    public Topic (Activity activity) {
        setId(activity.getAttachId());
        setRefId(activity.getAttachId());
        setRefType(activity.getAttachType());
        setLastUpdator(activity.getCreator());
        setCreated(activity.getCreated());
        setProjectId(activity.getProjectId());
        setTitle(activity.getTarget());
        setExcerpt(activity.getContent());
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

    public String getExcerpt() {
        return excerpt;
    }

    public void setExcerpt(String excerpt) {
        this.excerpt = excerpt;
    }

    public Integer getLastUpdatorId() {
        return lastUpdatorId;
    }

    public void setLastUpdatorId(Integer lastUpdatorId) {
        this.lastUpdatorId = lastUpdatorId;
    }

    public String getLastUpdatorName() {
        return lastUpdatorName;
    }

    public void setLastUpdatorName(String lastUpdatorName) {
        this.lastUpdatorName = lastUpdatorName;
    }

    public Integer getRefId() {
        return refId;
    }

    public void setRefId(Integer refId) {
        this.refId = refId;
    }

    public String getRefType() {
        return refType;
    }

    public void setRefType(String refType) {
        this.refType = refType;
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

    public Integer getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Integer companyId) {
        this.companyId = companyId;
    }

    public User getLastUpdator() {
        return lastUpdator;
    }

    public void setLastUpdator(User lastUpdator) {
        this.lastUpdator = lastUpdator;
    }

}
