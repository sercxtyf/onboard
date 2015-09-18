/*******************************************************************************
 * Copyright [2015] [Onboard team of SERC, Peking University]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.onboard.domain.model;

import java.util.Date;

import com.onboard.domain.mapper.model.DepartmentObject;
import com.onboard.domain.model.type.BaseOperateItem;

/**
 * Domain model: Group
 * 
 * @generated_by_elevenframework
 * 
 */
public class Department extends DepartmentObject implements BaseOperateItem {

    private static final long serialVersionUID = -4840010149566893768L;

    public Department() {
        super();
    }

    public Department(int id) {
        super(id);
    }

    public Department(DepartmentObject obj) {
        super(obj);
    }

    @Override
    public String getType() {
        return "department";
    }

    @Override
    public Date getCreated() {
        return null;
    }

    @Override
    public void setCreated(Date created) {
    }

    @Override
    public Date getUpdated() {
        return null;
    }

    @Override
    public void setUpdated(Date updated) {
    }

    @Override
    public Boolean getDeleted() {
        return false;
    }

    @Override
    public void setDeleted(Boolean deleted) {
    }

    @Override
    public boolean trashRequried() {
        return false;
    }

}

