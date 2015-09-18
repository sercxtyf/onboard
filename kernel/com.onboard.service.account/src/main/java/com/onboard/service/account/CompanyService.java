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
package com.onboard.service.account;

import java.util.List;

import com.onboard.domain.mapper.model.CompanyExample;
import com.onboard.domain.model.Company;
import com.onboard.service.base.BaseService;

/**
 * This service is about {@link Company}.
 * 
 * @author huangsz
 * 
 */
public interface CompanyService extends BaseService<Company, CompanyExample> {
	/**
	 * Get all companies which contains the given user
	 * 
	 * @param userId
	 *            The id of the user
	 * @return a list of companies that meets the restriction
	 */
	List<Company> getCompaniesByUserId(int userId);

	/**
	 * Remove the given user from the given company
	 * 
	 * @param companyId
	 *            The id of the company
	 * @param userId
	 *            The id of the user
	 */
	void removeUser(Integer companyId, Integer userId);

	/**
	 * Check if the given company contains the given user
	 * 
	 * @param companyId
	 *            The id of the company
	 * @param userId
	 *            The id of the user
	 * @return the result of the check
	 */
	boolean containsUser(Integer companyId, Integer userId);

}
