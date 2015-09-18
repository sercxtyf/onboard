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

import com.onboard.domain.mapper.model.UserObject;
import com.onboard.domain.model.type.BaseOperateItem;
import com.onboard.domain.model.type.Typeable;

/**
 * 领域模型：User
 * 
 * @author ruici
 * 
 */
public class User extends UserObject implements Typeable, BaseOperateItem {

    private static final long serialVersionUID = 8649426265472715988L;

    public User() {
        super();
    }

    public User(int id) {
        super(id);
    }

    public User(User obj) {
        super(obj);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj.getClass().equals(User.class))) {
            return false;
        }
        User user = (User) obj;

        return this.getId().equals(user.getId());
    }

    @Override
    public int hashCode() {
        return this.getId() == null ? super.hashCode() : this.getId();
    }

    private Integer departmentId;

    public Integer getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Integer departmentId) {
        this.departmentId = departmentId;
    }

    @Override
    public String getType() {
        return "user";
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
        return true;
    }

}
