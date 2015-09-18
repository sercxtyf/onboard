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

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.onboard.domain.mapper.model.BugExample;
import com.onboard.domain.model.Bug;
import com.onboard.service.base.BaseService;

/**
 * {@link Bug} Service Interface
 * 
 * @generated_by_elevenframework
 * 
 */
public interface BugService extends BaseService<Bug, BugExample> {

    /**
     * Get all bugs in a project
     * 
     * @param projectId - the id of project
     * @return a list of all bugs fits the requirements
     */
    List<Bug> getAllBugsByProject(int projectId);

    /**
     * Get all bugs of some status in a project
     * 
     * @param projectId - the id of project
     * @param status - the status filtered
     * @return a list of all bugs fits the requirements
     */
    List<Bug> getBugsByStatusByProject(int projectId, int status);

    /**
     * Get a bug with its comments and subscribers
     * 
     * @param id - the id of bug
     * @return an object of bug with extra info
     */
    Bug getBugByIdWithCommentAndSubscriable(int id);

    /**
     * Get opened bugs in a range by its project
     *  
     * @param projectId - the id of project
     * @param start - the start of range
     * @param limit - the length limit of range
     * @return a list of bugs fits the requirements
     */
    List<Bug> getOpenedBugsByProject(int projectId, int start, int limit);

    /**
     * Get finished bugs in a range by its project
     *  
     * @param projectId - the id of project
     * @param start - the start of range
     * @param limit - the length limit of range
     * @return a list of bugs fits the requirements
     */
    List<Bug> getFinishedBugsByProject(int projectId, int start, int limit);

    /**
     * Get completed bugs which date is in a range by its companyId
     * 
     * @param companyId - the id of company
     * @param since - the start of date
     * @param until - the end of date
     * @return a list of bus fits the requirements
     */
    List<Bug> getCompletedBugsBetweenDates(Integer companyId, Date since, Date until);

    /**
     * Get completed bugs organized by its date and projectId
     * 
     * @param companyId - the id of company
     * @param userId - the id of assignee
     * @param projectList - a list of project
     * @param until - the end of date
     * @param limit - the maximum number of bugs
     * @return an organized list of bugs fits the requirements 
     */
    TreeMap<Date, Map<Integer, List<Bug>>> getCompletedBugsGroupByDateByUser(int companyId, int userId,
            List<Integer> projectList, Date until, int limit);

    /**
     * Get opened bugs organized by its projectId
     * 
     * @param userId - the id of assignee
     * @param projectList - a list of project
     * @return an organized list of bugs fits the requirements
     */
    TreeMap<Integer, List<Bug>> getOpenBugsByUser(Integer userId, List<Integer> projectList);

    /***
     * Get opened bugs with requirements of company, assignee and dueTime
     * 
     * @param companyId - the id of its company
     * @param userId - the id of its assignee
     * @param since - the earliest date of its dueTime
     * @param until - the latest date of its dueTime
     * @return a list of bugs fits the requirements
     */
    List<Bug> getOpenBugsBetweenDatesByUser(int companyId, Integer userId, Date since, Date until);

    /**
     * TODO: No idea what's this
     * @param projectId
     * @param months
     * @return
     */
    Long getCompletedBugAveDurationByProjectIdDateBackByMonth(Integer projectId, Integer months);

    /**
     * TODO: No idea what's this
     * @param projectId
     * @param months
     * @return
     */
    Long getCompletedBugThirdQuarterDurationByProjectIdDateBackByMonth(Integer projectId, Integer months);
}
