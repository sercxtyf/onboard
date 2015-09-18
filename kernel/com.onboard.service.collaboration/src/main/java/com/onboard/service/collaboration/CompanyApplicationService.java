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
package com.onboard.service.collaboration;

import com.onboard.domain.model.CompanyApplication;

import java.util.List;

/**
 * {@link CompanyApplication} Service Interface
 * 
 * @generated_by_elevenframework
 * 
 */
public interface CompanyApplicationService {

    /**
     * Get item by id
     * 
     * @param id
     * @return item
     */
    CompanyApplication getCompanyApplicationById(int id);


    CompanyApplication getCompanyApplicationByToken(String token);

    /**
     * Get item list
     * 
     * @param start
     * @param limit
     * @return the item list
     */
    List<CompanyApplication> getCompanyApplications(int start, int limit);

    /**
     * Create
     * 
     * @param item
     * @return the created CompanyApplication
     */
    CompanyApplication createCompanyApplication(CompanyApplication item);

    /**
     * Delete
     * 
     * @param id
     */
    void deleteCompanyApplication(int id);

    /**
     * Disable a token
     * 
     * @param id
     */
    void disableCompanyApplicationToken(int id);
}
