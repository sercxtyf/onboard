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

import com.onboard.domain.mapper.model.UserCompanyObject;


/**
 * 领域模型：UserCompany
 * 
 * @author ruici
 * 
 */
public class UserCompany extends UserCompanyObject {

    public UserCompany() {
        super();
    }

    public UserCompany(UserCompany obj) {
        super(obj);
    }
}
