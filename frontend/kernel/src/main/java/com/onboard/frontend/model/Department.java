package com.onboard.frontend.model;

public class Department {
    private Integer id;

    private String name;

    private Integer companyId;

    private Integer customOrder;

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

    public Integer getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Integer companyId) {
        this.companyId = companyId;
    }

    public Integer getCustomOrder() {
        return customOrder;
    }

    public void setCustomOrder(Integer customOrder) {
        this.customOrder = customOrder;
    }

}