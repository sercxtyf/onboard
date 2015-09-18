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
package com.onboard.service.activity;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.onboard.domain.mapper.model.ActivityExample;
import com.onboard.domain.model.Activity;

/**
 * {@link Activity}服务接口
 * 
 * @author yewei
 * 
 */
public interface ActivityService {

    /**
     * 创建一个Activity对象
     * 
     * @param activity
     * @return 返回创建的Activity对象，包括数据库中的id
     */
    Activity create(Activity activity);

    /**
     * 更新Activity
     * 
     * @param activity
     * @return
     */
    Activity update(Activity activity);

    /**
     * 删除Activity
     * 
     * @param id
     */
    void delete(int id);

    /**
     * 根据id获取Activity对象
     * 
     * @param id
     *            Activity id
     * @return
     */
    Activity getById(int id);

    /**
     * 获取一个项目内的Activity
     * 
     * @param projectId
     * @param start
     * @param limit
     * @return
     */
    List<Activity> getByProject(int projectId, int start, int limit);

    /**
     * 获取一个项目的Activity，截止到endTime为止， 试取limit条数据，如果最前一条数据的当天还有其它Activity数据，则取完该天的所有数据， 返回的Activity数据最终可能大于limit条。
     * 
     * @param projectId
     * @param endTime
     *            截止时间
     * @param limit
     * @return
     */
    List<Activity> getByProjectTillDay(int projectId, Date endTime, int limit);

    /**
     * 获取一个公司的Activity
     * 
     * @param projectId
     * @param start
     * @param limit
     * @return
     */
    List<Activity> getByCompany(int projectId, int start, int limit);

    /**
     * 获取一个公司的Activity，截止到endTime为止， 试取limit条数据，如果最前一条数据的当天还有其它Activity数据，则取完该天的所有数据， 返回的Activity数据最终可能大于limit条。
     * 
     * @param projectId
     * @param start
     * @param limit
     * @return
     */
    List<Activity> getByCompanyTillDay(int companyId, Date endTime, int limit);

    /**
     * 获取用户的最新Activity列表
     * 
     * @param companyId
     * @param userId
     * @param limit
     * @param projectList
     *            如果为空，表示用户参与的所有项目
     * @return
     */
    List<Activity> getLatestByUser(int companyId, int userId, int limit, List<Integer> projectList);

    /**
     * 分页获取一个用户在一个公司的活动
     * 
     * @param companyId
     * @param userId
     * @param start
     * @param limit
     * @param projectList
     * @return
     */
    List<Activity> getLatestByUserByPage(int companyId, int userId, int start, int limit, List<Integer> projectList);

    /**
     * 获取一组项目用户相关的活动，基于日期进行分组
     * 
     * @param companyId
     * @param userId
     * @param projectList
     *            如果为空，表示用户参与的所有项目
     * @return
     */
    TreeMap<Date, List<Activity>> getByUserGroupByDate(int companyId, int userId, int limit, List<Integer> projectList, Date until);

    /**
     * 获取用户可以查看的Activity信息
     * 
     * @param companyId
     * @param filterUserId
     * @param filterProjectId
     * @param endTime
     * @param limit
     * @return
     */
    List<Activity> getUserVisibleTillDay(int companyId, Integer filterUserId, Integer filterProjectId, Date endTime, String type,
            int limit);

    /**
     * 获取用户可以查看的Activity信息
     * 
     * @param companyId
     * @param userId
     * @return
     */
    List<Activity> getUserVisibleBySampleTillDay(int companyId, Activity sample, Date endTime, int limit);

    /**
     * 获取一个任务的相关活动信息,其中删除了回复相关的活动信息
     * 
     * @param todoId
     * @param start
     * @param limit
     * @return
     */
    List<Activity> getByTodo(int todoId, int start, int limit);

    /**
     * 获取某项目某天的activity
     * 
     * @param projectId
     * @param date
     * @return
     */
    List<Activity> getByProjectByDate(int projectId, Date date);

    /**
     * 根据某对象的类型和id来获取activity
     * 
     * @param type
     * @param id
     * @return
     */
    List<Activity> getByAttachTypeAndId(String type, int id);

    /**
     * 分页获取一个用户在一个公司的活动
     * 
     * @param companyId
     * @param userId
     * @param start
     * @param limit
     * @param projectList
     * @return
     */
    List<Activity> getLatestByBySampleCompanyByPage(Activity sample, int companyId, int start, int limit,
            List<Integer> projectList);

    /**
     * 根据example获取activity
     * 
     * @param example
     * @return
     */
    List<Activity> getByActivityExample(ActivityExample example);

    /**
     * 根据attachType和id删除activity
     * 
     * @param type
     * @param id
     */
    void deleteByAttachTypeAndId(String type, int id);

    /**
     * 获取某一日期之后所有的该用户的活动信息
     * 
     * @author Chenlong
     * @param companyId
     * @param userId
     * @param projectList
     * @param until
     * @return
     */
    List<Activity> getLatestByUserSince(int companyId, int userId, List<Integer> projectList, Date until);

    /**
     * 获取一个用户在某个项目中某时间范围内的活动
     * 
     * @param projectId
     * @param start
     * @param end
     * @return
     */
    List<Activity> getByProjectBetweenDates(int projectId, Date start, Date end);

    List<Activity> getByUserGroupByDateReturnList(int companyId, int userId, int limit, List<Integer> projectList, Date until);

    /**
     * 获取一个用户在某个项目中某时间范围内的活动
     * 
     * @param projectId
     * @param userId
     * @param start
     * @param end
     * @return
     */
    List<Activity> getByProjectUserBetweenDates(int projectId, int userId, Date start, Date end);

    /**
     * 获取团队成员某段时间的活动数
     * 
     * @param companyId
     * @param since
     * @param until
     * @return
     */
    Map<Integer, Integer> getActivityCountForUsers(Integer companyId, Date since, Date until);

    /**
     * 获取团队成员某段时间内每天的活动数
     * 
     * @param companyId
     * @param since
     * @param until
     * @return
     */
    List<Map<String, ?>> getActivityCountForUsersGroupByDate(Integer companyId, Date since, Date until);

    List<Activity> getActivitiesByCompanyAndDates(Integer companyId, Date since, Date until);
}
