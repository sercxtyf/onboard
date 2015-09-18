package com.onboard.frontend.model.dto;

import java.util.Date;
import java.util.List;

import org.springframework.beans.BeanUtils;

import com.google.common.base.Function;
import com.onboard.frontend.model.Project;

public class ProjectDTO {

    public static final Function<Project, ProjectDTO> PROJECT_DTO_FUNCTION = new Function<Project, ProjectDTO>() {
        public ProjectDTO apply(Project input) {
            return null;
        }
    };

    private Integer id;
    private Integer companyId;
    private String name;
    private String description;
    private Boolean archived;
    private Integer creatorId;
    private Boolean deleted;
    private Date created;
    private Date updated;
    private Integer colorId;

    private Integer topicCount;
    private Integer attachmentCount;
    private Integer todoCount;
    private Integer userCount;
    private Integer documentCount;
    private Boolean isCurrentUserAdmin;

    private List<Integer> members;

    private List<String> emails;

    public ProjectDTO() {
    }

    public ProjectDTO(Project project) {
        BeanUtils.copyProperties(project, this);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getArchived() {
        return archived;
    }

    public void setArchived(Boolean archived) {
        this.archived = archived;
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

    public Integer getColorId() {
        return colorId;
    }

    public void setColorId(Integer colorId) {
        this.colorId = colorId;
    }

    public List<Integer> getMembers() {
        return members;
    }

    public void setMembers(List<Integer> members) {
        this.members = members;
    }

    public List<String> getEmails() {
        return emails;
    }

    public void setEmails(List<String> emails) {
        this.emails = emails;
    }

    public Integer getTopicCount() {
        return topicCount;
    }

    public void setTopicCount(Integer topicCount) {
        this.topicCount = topicCount;
    }

    public Integer getAttachmentCount() {
        return attachmentCount;
    }

    public void setAttachmentCount(Integer attachmentCount) {
        this.attachmentCount = attachmentCount;
    }

    public Integer getTodoCount() {
        return todoCount;
    }

    public void setTodoCount(Integer todoCount) {
        this.todoCount = todoCount;
    }

    public Integer getUserCount() {
        return userCount;
    }

    public void setUserCount(Integer userCount) {
        this.userCount = userCount;
    }

    public Integer getDocumentCount() {
        return documentCount;
    }

    public void setDocumentCount(Integer documentCount) {
        this.documentCount = documentCount;
    }

    public Boolean getIsCurrentUserAdmin() {
        return isCurrentUserAdmin;
    }

    public void setIsCurrentUserAdmin(Boolean isCurrentUserAdmin) {
        this.isCurrentUserAdmin = isCurrentUserAdmin;
    }

    public Project toProject() {
        Project project = new Project();
        BeanUtils.copyProperties(this, project);

        return project;
    }

}
