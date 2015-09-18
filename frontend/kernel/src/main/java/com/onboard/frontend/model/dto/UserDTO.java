package com.onboard.frontend.model.dto;

import java.util.Date;

import org.springframework.beans.BeanUtils;

import com.google.common.base.Function;
import com.onboard.frontend.model.User;

public class UserDTO {

    public static final Function<User, UserDTO> USER_DTO_FUNCTION = new Function<User, UserDTO>() {
        public UserDTO apply(User input) {
            return new UserDTO(input);
        }
    };

    public UserDTO() {

    }

    public UserDTO(User user) {
        BeanUtils.copyProperties(user, this);
    }

    public User toUser() {
        User user = new User();
        BeanUtils.copyProperties(this, user);
        return user;
    }

    public static final Function<UserDTO, User> USERDTO_TO_USER_FUNCTION = new Function<UserDTO, User>() {
        public User apply(UserDTO input) {
            return input.toUser();
        }
    };

    private Integer id;

    private String name;

    private String description;

    private String email;

    private Boolean activated;

    private String avatar;

    private Date created;

    private Date updated;

    private String username;

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getActivated() {
        return activated;
    }

    public void setActivated(Boolean activated) {
        this.activated = activated;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
