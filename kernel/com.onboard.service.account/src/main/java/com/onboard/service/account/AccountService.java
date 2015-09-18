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

import com.onboard.domain.model.Invitation;
import com.onboard.domain.model.Project;
import com.onboard.domain.model.User;

/**
 * This service is about {@link User} ,{@link Invitation}.
 * 
 */
public interface AccountService {

	/**
	 * Send an invitation of the given company to the given email
	 * 
	 * @param companyId
	 *            The id of the company which this invitation related to
	 * @param email
	 *            The target email address
	 */
	void sendInvitation(int companyId, String email);

	/**
	 * Send an invitation of the given company(and some of its projects) to the
	 * given email
	 * 
	 * @param companyId
	 *            The id of the company which this invitation related to
	 * @param email
	 *            The target email address
	 * @param projects
	 *            The list of the projects which this invitation related to
	 */
	void sendInvitation(int companyId, String email, List<Project> projects);

	/**
	 * Authenticate a token to see if it's the token that was sent to some
	 * user's email before
	 * 
	 * @param companyId
	 *            The id of the company which this invitation related to
	 * @param token
	 *            The token need to be authenticated
	 * @return The user's email address if the token is valid, null if not.
	 */
	String authenticateInvitation(int companyId, String token);

	/**
	 * Complete an invitation (Create user, add the user to company and projects
	 * and delete the token)
	 * 
	 * @param companyId
	 *            The id of the company which this invitation related to
	 * @param token
	 *            The token of the invitation that need to be complete
	 * @param user
	 *            The basic info user provides for registration
	 * 
	 */
	void completeInvitation(int companyId, User user, String token);

	/**
	 * Get all invitations of the given company
	 * 
	 * @param companyId
	 *            The id of the company
	 * @return a list of invitations that meets the restriction
	 */
	List<Invitation> getAllInvitations(int companyId);

	/**
	 * Get an invitation by its id
	 * 
	 * @param id
	 *            The id of the invitation
	 * @return an object of the invitation that meets the restriction
	 */
	Invitation getInvitationById(int id);

	/**
	 * Delete an invitation by its id
	 * 
	 * @param id
	 *            The id of the invitation
	 */
	void deleteInvitationById(int id);

	/**
	 * Generate the activities of an user's join
	 * 
	 * @param user
	 *            The user that just joined
	 * @param projectId
	 *            The id of the project that the activities related to
	 */
	// TODO: to delete
	void addActivityInfo(User user, int projectId);

}
