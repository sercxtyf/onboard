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
package com.onboard.web.api.form;

import java.util.List;

import com.onboard.domain.model.Project;
import com.onboard.domain.model.User;

public class ProjectForm extends Project {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private List<User> admins;

    @Override
    public List<User> getAdmins() {
        return admins;
    }

    @Override
    public void setAdmins(List<User> admins) {
        this.admins = admins;
    }
}
