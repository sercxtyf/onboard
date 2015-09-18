package com.onboard.frontend.model;

import java.io.Serializable;

public class GithubInfo implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 4033121468380236921L;
    private Integer id;
    private Integer userId;
    private String code;
    private String token;
    private String userName;
    private String userEmail;
    private Boolean deleted;
    private Integer onboardUserId;
    private String type;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public Integer getOnboardUserId() {
        return onboardUserId;
    }

    public void setOnboardUserId(Integer onboardUserId) {
        this.onboardUserId = onboardUserId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
