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
import java.util.Map;

import com.onboard.domain.mapper.model.UserExample;
import com.onboard.domain.model.Department;
import com.onboard.domain.model.User;
import com.onboard.service.base.BaseService;

/**
 * This service is about registration and authenticate of {@link User}
 * 
 * @author ruici, huangsz
 * 
 */
public interface UserService extends BaseService<User, UserExample> {

	/**
	 * Register an user using given user object and a name of company
	 * 
	 * @param user
	 *            An object of user contains basic information
	 * @param companyName
	 *            The name of company that the user want's to create when
	 *            registered
	 */
	User signUp(User user, String companyName);

	/**
	 * Send an confirmation email to an user, which contains a token related to
	 * this user, the user can use this token to confirm his/her email address
	 * then finish the registration
	 * 
	 * @param user
	 *            The user that the email sent to
	 */
	void sendConfirmationEmail(User user);

	/**
	 * Authenticate the given token to see if the given user register using the
	 * right email address(To see if the token user claims to get equals the
	 * token we sent)
	 * 
	 * @param token
	 *            The token user claims to get from the email
	 */
	boolean confirmRegisteredUser(int uid, String token);

	/**
	 * Authenticate the given identity and the given password to see if the user
	 * can login with these information, the identity here can be either email
	 * or username, which means we need to check both possibilities
	 * 
	 * @param emailOrUsername
	 *            The identity that the user claims, may be either email or
	 *            username
	 * @param password
	 *            The password that the user claims
	 * @return an object of the user if the information is valid, otherwise null
	 */
	User login(String emailOrUsername, String password);

	/**
	 * Try to help user reset their password, usually we sent an email with
	 * token to their email, then they can use the token(contains in an URL) to
	 * reset their password
	 * 
	 * @param email
	 *            The email address that the user claims
	 */
	void forgetPassword(String email);

	/**
	 * Try to reset some user's password to the given password, but we need to
	 * authenticate the given token first to ensure the account security and
	 * figure out which user wants to reset his/her password
	 * 
	 * @param password
	 *            The password that the user wants to change to
	 * @param token
	 *            The token that the user claims to get from the email(It's
	 *            possible to be a fake one)
	 * @return true if the token is valid, otherwise false
	 */
	boolean resetPassword(int uid, String password, String token);

	/**
	 * Get user by the given email address
	 * 
	 * @param email
	 *            The email that the user should have
	 * @return an object of user that meets the restriction, null if none
	 *         exists.
	 */
	User getUserByEmail(String email);

	/**
	 * Get user by the given email address, and don't remove the password
	 * information
	 * 
	 * @param email
	 *            The email that the user should have
	 * @return an object of user that meets the restriction, null if none
	 *         exists.
	 */
	public User getUserWithPasswordByEmail(String email);

	/**
	 * Get user by the given identity(can be either email of username), and
	 * don't remove the password information
	 * 
	 * @param emailOrUsername
	 *            The identity that the user should have, can be either email or
	 *            username
	 * @return an object of user that meets the restriction, null if none
	 *         exists.
	 */
	public User getUserByEmailOrUsernameWithPassword(String emailOrUsername);

	/**
	 * Get user by the given identity, the identity here can be either email or
	 * username
	 * 
	 * @param emailOrUsername
	 *            The identity that the user should have, can be either email or
	 *            username
	 * @return an object of user that meets the restriction, null if not exists.
	 */
	User getUserByEmailOrUsername(String emailOrUsername);

	/**
	 * Get all users in the given project
	 * 
	 * @param projectId
	 *            The id of the project
	 * @return a list of users that meets the restriction
	 */
	List<User> getUserByProjectId(int projectId);

	/**
	 * Get all users in the given company
	 * 
	 * @param companyId
	 *            The id of the company
	 * @return a list of users that meets the restriction
	 */
	List<User> getUserByCompanyId(int companyId);

	/**
	 * Get all users in the given department
	 * 
	 * @param groupId
	 *            The id of the department
	 * @param companyId
	 *            The id of the company
	 * @return a list of users that meets the restriction
	 */
	List<User> getUserByCompanyIdByDepartmentId(int groupId, int companyId);

	/**
	 * Get all users in the given company, then organized them by project
	 * 
	 * @param companyId
	 *            The id of the company
	 * @return a organized list of users that meets the restriction
	 */
	Map<Integer, List<User>> getAllProjectUsersInCompany(int companyId);

	/**
	 * Get all users that has department in the given company, then organized
	 * them by department
	 * 
	 * @param companyId
	 *            The id of the company
	 * @return a organized list of users that meets the restriction
	 */
	Map<Department, List<User>> getDepartmentedUserByCompanyId(Integer companyId);

	/**
	 * Get all users that doesn't have department in the given company
	 * 
	 * @param companyId
	 *            The id of the company
	 * @return a list of users that meets the restriction
	 */
	List<User> getUnDepartmentedUsersByCompanyId(Integer companyId);

	/**
	 * Update an user, without updating its password
	 * 
	 * @param user
	 *            An object of user contains its new information
	 * @param avatar
	 *            A file contains the new avatar, null if doesn't change
	 * @param filename
	 *            The filename that the avatar should save as, null if doesn't
	 *            change
	 */
	void updateUser(User user, byte[] avatar, String filename);

	/**
	 * Check if the given user is belong to the given company
	 * 
	 * @param userId
	 *            The id of the user
	 * @param companyId
	 *            The id of the company
	 * @return result of the check
	 */
	boolean isUserInCompany(int userId, int companyId);

	/**
	 * Check if the given user is belong to the given project
	 * 
	 * @param userId
	 *            The id of the user
	 * @param companyId
	 *            the id of the company
	 * @param projectId
	 *            the id of the project
	 * @return result of the check
	 */
	boolean isUserInProject(int userId, int companyId, int projectId);

	/**
	 * Check if an email address is already used
	 * 
	 * @param email
	 *            The email address need to be checked
	 * @return result of the check
	 */
	boolean isEmailRegistered(String email);

	/**
	 * Check if the given username is already used
	 * 
	 * @param username
	 *            The username need to be checked
	 * @return result of the check
	 */
	public Boolean containUsername(String username);

	/**
	 * Encoding the given password
	 * 
	 * @author Chenlong
	 * @param password
	 *            Then password need to be encode
	 * @param salt
	 *            No idea what's this..
	 * @return a string of encoded password
	 */
	// TODO: the salt here is never used actually
	public String createPassword(String password, String salt);

	/**
	 * Filter users that belongs to the given project from the given list of
	 * users
	 * 
	 * @author Chenlong
	 * @param users
	 *            The origin list of users
	 * @param projectId
	 *            The id of the project that used for filter
	 * @return a filtered list of users that meets the restriction
	 */
	List<User> filterProjectMembers(List<User> users, int projectId);

}
