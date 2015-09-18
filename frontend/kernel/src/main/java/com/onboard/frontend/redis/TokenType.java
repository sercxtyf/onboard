package com.onboard.frontend.redis;

public enum TokenType {
    REMEMBER_ME("remember_me"), FORGET_PASSWORD("forget_password"), CONFIRMATION(
            "confirmation");

    private String name;

    private TokenType(String name) {
        this.setName(name);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
