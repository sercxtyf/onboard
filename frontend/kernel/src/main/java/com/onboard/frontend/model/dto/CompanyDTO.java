package com.onboard.frontend.model.dto;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.onboard.frontend.model.Company;
import com.onboard.frontend.model.Project;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.util.Date;
import java.util.List;

public class CompanyDTO {
    
    public static final Logger logger = LoggerFactory.getLogger(CompanyDTO.class);

    private Integer id;
    private String name;
    private String description;
    private Integer creatorId;
    private UserDTO creator;
    private Date created;
    private Date updated;
    private List<ProjectDTO> projects;

    public CompanyDTO() {
    }

    public CompanyDTO(Company company) {
        BeanUtils.copyProperties(company, this);
    }

    public CompanyDTO(Company company, List<Project> projects) {
        BeanUtils.copyProperties(company, this);
        this.projects = ImmutableList.copyOf(Lists.transform(projects, ProjectDTO.PROJECT_DTO_FUNCTION));
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public Integer getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Integer creatorId) {
        this.creatorId = creatorId;
    }

    public UserDTO getCreator() {
        return creator;
    }

    public void setCreator(UserDTO creator) {
        this.creator = creator;
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

    public List<ProjectDTO> getProjects() {
        logger.debug("get = {} {}", this.projects, projects);
        return projects;
    }

    public void setProjects(List<ProjectDTO> projects) {
        logger.debug("set = {}", this.projects);
        this.projects = projects;
    }

    public Company toCompany() {
        Company company = new Company();
        BeanUtils.copyProperties(this, company);

        return company;
    }
}
