package com.onboard.frontend.model;

/**
 * Created by XingLiang on 2014/12/17.
 */
public class AttachTodo {
    private Integer id;
    private String attachType;
    private Integer attachId;
    private Integer todoId;

    public AttachTodo() {

    }

    public AttachTodo(Integer id, String attachType, Integer attachId, Integer todoId) {
        this.id = id;
        this.attachType = attachType;
        this.attachId = attachId;
        this.todoId = todoId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAttachType() {
        return attachType;
    }

    public void setAttachType(String attachType) {
        this.attachType = attachType;
    }

    public Integer getAttachId() {
        return attachId;
    }

    public void setAttachId(Integer attachId) {
        this.attachId = attachId;
    }

    public Integer getTodoId() {
        return todoId;
    }

    public void setTodoId(Integer todoId) {
        this.todoId = todoId;
    }
}
