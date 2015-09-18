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
package com.onboard.service.activity.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.onboard.domain.mapper.ActivityMapper;
import com.onboard.domain.mapper.NotificationMapper;
import com.onboard.domain.mapper.ProjectMapper;
import com.onboard.domain.mapper.UserProjectMapper;
import com.onboard.domain.mapper.model.ActivityExample;
import com.onboard.domain.mapper.model.NotificationExample;
import com.onboard.domain.mapper.model.UserProjectExample;
import com.onboard.domain.model.Activity;
import com.onboard.domain.model.Notification;
import com.onboard.domain.model.Project;
import com.onboard.domain.model.Todo;
import com.onboard.domain.model.User;
import com.onboard.domain.model.UserProject;
import com.onboard.service.account.UserService;
import com.onboard.service.activity.ActivityActionType;
import com.onboard.service.activity.ActivityService;
import com.onboard.service.web.SessionService;

/**
 * {@link ActivityService}接口实现
 * 
 * @author yewei
 * 
 */
@Transactional
@Service("activityServiceBean")
public class ActivityServiceImpl implements ActivityService {

    @Autowired
    private ActivityMapper activityMapper;

    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private UserProjectMapper userProjectMapper;

    @Autowired
    private NotificationMapper notificationMapper;

    @Autowired
    private SessionService session;

    @Autowired
    private UserService userService;

    private void addProjectConstraint(ActivityExample example, List<Integer> projectList) {
        if (projectList != null) {
            example.getOredCriteria().get(0).andProjectIdIn(projectList);
        }
    }

    /**
     * 获取截止到某个时间的活动，该活动属于用户参与的一组项目
     * 
     * @param activity
     *            与activity具有相同属性的活动才会被选取
     * @param endTime
     *            截止时间
     * @param limit
     *            分页参数，最终取出来的活动数可能大于limit，取的原则是一旦某天的一些活动被取出来，则会将改天所有活动取出
     * @param projectList
     *            项目集合
     * @return
     */
    private List<Activity> getActivitiesTillDay(Activity activity, Date endTime, int limit, List<Integer> projectList) {

        if (projectList != null && projectList.isEmpty()) {
            return new ArrayList<Activity>();
        }

        ActivityExample example = new ActivityExample(activity);
        example.setLimit(0, limit);
        example.setOrderByClause("id desc");
        example.getOredCriteria().get(0).andCreatedLessThan(endTime);

        addProjectConstraint(example, projectList);

        List<Activity> activities = activityMapper.selectByExample(example);

        if (activities.size() == limit) {
            Activity end = activities.get(limit - 1);
            Date startTime = new DateTime(end.getCreated()).withTimeAtStartOfDay().toDate();

            example = new ActivityExample(activity);
            example.setOrderByClause("id desc");
            example.getOredCriteria().get(0).andCreatedGreaterThanOrEqualTo(startTime).andIdLessThan(end.getId());

            addProjectConstraint(example, projectList);

            activities.addAll(activityMapper.selectByExample(example));
        }

        return activities;
    }

    /**
     * 创建一个按id降序排列，设置好分页的example
     * 
     * @param activity
     * @param start
     * @param limit
     * @return
     */
    private ActivityExample buildBasicExample(Activity activity, int start, int limit) {
        ActivityExample activityExample = new ActivityExample(activity);
        activityExample.setLimit(start, limit);
        activityExample.setOrderByClause("id desc");

        return activityExample;
    }

    private TreeMap<Date, List<Activity>> groupActivitiesByDate(List<Activity> activities) {

        TreeMap<Date, List<Activity>> map = new TreeMap<Date, List<Activity>>(new Comparator<Date>() {

            @Override
            public int compare(Date o1, Date o2) {
                return o2.compareTo(o1);
            }

        });

        for (Activity ac : activities) {
            Date d = new DateTime(ac.getCreated()).withTimeAtStartOfDay().toDate();
            if (!map.containsKey(d)) {
                List<Activity> list = new ArrayList<Activity>();
                list.add(ac);
                map.put(d, list);
            } else {
                map.get(d).add(ac);
            }
        }

        return map;
    }

    private List<Integer> getProjectIdListByUserByCompany(int userId, int companyId, int start, int limit) {
        List<Integer> projects = new ArrayList<Integer>();

        UserProject userProject = new UserProject();
        userProject.setUserId(userId);
        userProject.setCompanyId(companyId);
        UserProjectExample example = new UserProjectExample(userProject);
        example.setLimit(start, limit);

        List<UserProject> userProjectList = userProjectMapper.selectByExample(example);

        for (UserProject up : userProjectList) {
            Project project = new Project(projectMapper.selectByPrimaryKey(up.getProjectId()));
            if (project.getDeleted() == false) {
                projects.add(project.getId());
            }
        }

        return projects;
    }

    @Override
    public Activity create(Activity activity) {
        activityMapper.insertSelective(activity);
        return activity;
    }

    @Override
    public Activity update(Activity activity) {
        activityMapper.updateByPrimaryKey(activity);
        return activity;
    }

    @Override
    public void delete(int id) {
        Notification sample = new Notification();
        sample.setActivityId(id);
        notificationMapper.deleteByExample(new NotificationExample(sample));
        activityMapper.deleteByPrimaryKey(id);
    }

    @Override
    public Activity getById(int id) {
        return activityMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<Activity> getByProject(int projectId, int start, int limit) {
        Activity activity = new Activity();
        activity.setProjectId(projectId);
        ActivityExample activityExample = buildBasicExample(activity, start, limit);
        return activityMapper.selectByExample(activityExample);
    }

    @Override
    public List<Activity> getByProjectTillDay(int projectId, Date endTime, int limit) {

        Activity activity = new Activity();
        activity.setProjectId(projectId);

        return getActivitiesTillDay(activity, endTime, limit, null);
    }

    @Override
    public List<Activity> getByCompany(int companyId, int start, int limit) {
        Activity activity = new Activity();
        activity.setCompanyId(companyId);
        ActivityExample activityExample = buildBasicExample(activity, start, limit);

        return activityMapper.selectByExample(activityExample);
    }

    @Override
    public List<Activity> getByCompanyTillDay(int companyId, Date endTime, int limit) {
        Activity activity = new Activity();
        activity.setCompanyId(companyId);

        return getActivitiesTillDay(activity, endTime, limit, null);
    }

    @Override
    public List<Activity> getLatestByUser(int companyId, int userId, int limit, List<Integer> projectList) {
        Activity activity = new Activity();
        activity.setCreatorId(userId);
        return getLatestByBySampleCompanyByPage(activity, companyId, 0, limit, projectList);
    }

    @Override
    public List<Activity> getLatestByUserByPage(int companyId, int userId, int start, int limit, List<Integer> projectList) {
        if (projectList != null && projectList.isEmpty()) {
            return new ArrayList<Activity>();
        }
        Activity sample = new Activity();
        sample.setCreatorId(userId);
        sample.setCompanyId(companyId);

        ActivityExample example = buildBasicExample(sample, start, limit);
        addProjectConstraint(example, projectList);

        return activityMapper.selectByExample(example);
    }

    private List<Activity> appendActivitiesOfLastDay(List<Activity> activities, ActivityExample example) {

        if (activities != null && !activities.isEmpty()) {
            Activity lastActivity = activities.get(activities.size() - 1);
            Date since = new DateTime(lastActivity.getCreated()).withTimeAtStartOfDay().toDate();
            example.getOredCriteria().get(0).andCreatedGreaterThanOrEqualTo(since).andCreatedLessThan(lastActivity.getCreated());
            activities.addAll(activityMapper.selectByExample(example));
        }

        return activities;
    }

    @Override
    public List<Activity> getByUserGroupByDateReturnList(int companyId, int userId, int limit, List<Integer> projectList,
            Date until) {

        if (projectList != null && projectList.isEmpty()) {
            return new ArrayList<Activity>();
        }

        Activity sample = new Activity();
        sample.setCreatorId(userId);
        sample.setCompanyId(companyId);
        ActivityExample example = new ActivityExample(sample);

        addProjectConstraint(example, projectList);

        example.getOredCriteria().get(0).andCreatedLessThanOrEqualTo(until);
        example.setLimit(limit);
        example.setOrderByClause("id desc");

        List<Activity> originActivities = activityMapper.selectByExample(example);

        if (originActivities == null || originActivities.isEmpty()) {
            return new ArrayList<Activity>();
        }

        // 将最后一天剩余的所有activity都取出来，最终activity的数目可能大于limit
        List<Activity> activities = appendActivitiesOfLastDay(originActivities, example);

        return activities;

    }

    @Override
    public TreeMap<Date, List<Activity>> getByUserGroupByDate(int companyId, int userId, int limit, List<Integer> projectList,
            Date until) {

        List<Activity> activities = this.getByUserGroupByDateReturnList(companyId, userId, limit, projectList, until);
        return groupActivitiesByDate(activities);
    }

    @Override
    public List<Activity> getUserVisibleTillDay(int companyId, Integer filterUserId, Integer filterProjectId, Date endTime,
            String filterType, int limit) {

        Activity sample = new Activity();

        if (filterProjectId != null) {
            sample.setProjectId(filterProjectId);
        }
        if (filterUserId != null) {
            sample.setCreatorId(filterUserId);
        }
        if (filterType != null) {
            sample.setAttachType(filterType);
        }

        return this.getUserVisibleBySampleTillDay(companyId, sample, endTime, limit);

    }

    @Override
    public List<Activity> getUserVisibleBySampleTillDay(int companyId, Activity sample, Date endTime, int limit) {
        if (sample == null) {
            return new ArrayList<Activity>();
        }
        sample.setCompanyId(companyId);
        List<Integer> projectList = getProjectIdListByUserByCompany(session.getCurrentUser().getId(), companyId, 0, -1);

        return getActivitiesTillDay(sample, endTime, limit, projectList);
    }

    @Override
    public List<Activity> getByTodo(int todoId, int start, int limit) {

        Activity activity = new Activity();
        activity.setAttachId(todoId);
        activity.setAttachType(new Todo().getType());

        ActivityExample activityExample = buildBasicExample(activity, start, limit);

        // TODO hard-coding of subject, subject should be moved to config files
        activityExample.getOredCriteria().get(0).andActionNotEqualTo(ActivityActionType.REPLY);

        return activityMapper.selectByExample(activityExample);
    }

    @Override
    public List<Activity> getByProjectByDate(int projectId, Date date) {

        Activity sample = new Activity();
        sample.setProjectId(projectId);

        ActivityExample example = new ActivityExample(sample);

        DateTime dt = new DateTime(date);
        Date start = dt.withTimeAtStartOfDay().toDate();
        Date end = dt.plusDays(1).toDate();
        example.getOredCriteria().get(0).andCreatedGreaterThanOrEqualTo(start).andCreatedLessThan(end);

        return activityMapper.selectByExample(example);
    }

    @Override
    public List<Activity> getByAttachTypeAndId(String type, int id) {

        Activity activity = new Activity();
        activity.setAttachId(id);
        activity.setAttachType(type);

        ActivityExample example = new ActivityExample(activity);
        example.setOrderByClause("id desc");

        return activityMapper.selectByExample(example);
    }

    @Override
    public List<Activity> getLatestByBySampleCompanyByPage(Activity sample, int companyId, int start, int limit,
            List<Integer> projectList) {

        if (projectList != null && projectList.isEmpty()) {
            return new ArrayList<Activity>();
        }

        sample.setCompanyId(companyId);
        ActivityExample example = buildBasicExample(sample, start, limit);

        addProjectConstraint(example, projectList);

        return activityMapper.selectByExample(example);
    }

    @Override
    public List<Activity> getByActivityExample(ActivityExample example) {
        return activityMapper.selectByExample(example);
    }

    @Override
    public void deleteByAttachTypeAndId(String type, int id) {
        Activity deletingActivity = new Activity();
        deletingActivity.setAttachType(type);
        deletingActivity.setAttachId(id);
        List<Activity> activities = activityMapper.selectByExample(new ActivityExample(deletingActivity));
        for (Activity activity : activities) {
            this.delete(activity.getId());
        }
    }

    @Override
    public List<Activity> getLatestByUserSince(int companyId, int userId, List<Integer> projectList, Date since) {
        Activity sample = new Activity();
        sample.setCreatorId(userId);
        sample.setCompanyId(companyId);
        ActivityExample example = new ActivityExample(sample);
        addProjectConstraint(example, projectList);
        example.getOredCriteria().get(0).andCreatedGreaterThanOrEqualTo(since);
        example.setOrderByClause("created desc");
        List<Activity> originActivities = activityMapper.selectByExample(example);
        return originActivities;
    }

    @Override
    public List<Activity> getByProjectBetweenDates(int projectId, Date start, Date end) {

        Activity sample = new Activity();
        sample.setProjectId(projectId);

        ActivityExample example = new ActivityExample(sample);

        DateTime dt = new DateTime(start);
        start = dt.withTimeAtStartOfDay().toDate();
        dt = new DateTime(end);
        end = dt.withTimeAtStartOfDay().plusDays(1).toDate();
        example.getOredCriteria().get(0).andCreatedGreaterThanOrEqualTo(start).andCreatedLessThan(end);
        example.setOrderByClause("id desc");
        return activityMapper.selectByExample(example);
    }

    @Override
    public List<Activity> getByProjectUserBetweenDates(int projectId, int userId, Date start, Date end) {

        Activity sample = new Activity();
        sample.setProjectId(projectId);
        sample.setCreatorId(userId);

        ActivityExample example = new ActivityExample(sample);

        DateTime dt = new DateTime(start);
        start = dt.withTimeAtStartOfDay().toDate();
        dt = new DateTime(end);
        end = dt.withTimeAtStartOfDay().plusDays(1).toDate();
        example.getOredCriteria().get(0).andCreatedGreaterThanOrEqualTo(start).andCreatedLessThan(end);
        example.setOrderByClause("id desc");
        return activityMapper.selectByExample(example);
    }

    private Integer getCountByCompanyUserBetweenDates(int companyId, int userId, Date start, Date end) {
        Activity sample = new Activity();
        sample.setCompanyId(companyId);
        sample.setCreatorId(userId);

        ActivityExample example = new ActivityExample(sample);

        DateTime dt = new DateTime(start);
        start = dt.withTimeAtStartOfDay().toDate();
        dt = new DateTime(end);
        end = dt.withTimeAtStartOfDay().plusDays(1).toDate();
        example.getOredCriteria().get(0).andCreatedGreaterThanOrEqualTo(start).andCreatedLessThan(end);
        example.setOrderByClause("id desc");
        return activityMapper.countByExample(example);
    }

    @Override
    public Map<Integer, Integer> getActivityCountForUsers(Integer companyId, Date since, Date until) {
        List<User> companyUsers = userService.getUserByCompanyId(companyId);
        Map<Integer, Integer> activityCountMap = Maps.newHashMap();
        for (User user : companyUsers) {
            activityCountMap.put(user.getId(), getCountByCompanyUserBetweenDates(companyId, user.getId(), since, until));
        }
        return activityCountMap;
    }

    @Override
    public List<Map<String, ?>> getActivityCountForUsersGroupByDate(Integer companyId, Date since, Date until) {
        List<User> companyUsers = userService.getUserByCompanyId(companyId);
        List<Map<String, ?>> result = Lists.newArrayList();
        Map<Integer, Integer> activityCountMap;
        DateTime dt = new DateTime(since);
        dt = dt.withTimeAtStartOfDay();
        DateTime end = new DateTime(until);
        end = end.withTimeAtStartOfDay().plusDays(1).minusMinutes(1);
        while (dt.toDate().before(end.toDate())) {
            activityCountMap = Maps.newHashMap();
            for (User user : companyUsers) {
                activityCountMap.put(user.getId(),
                        getCountByCompanyUserBetweenDates(companyId, user.getId(), dt.toDate(), dt.toDate()));
            }
            result.add(ImmutableMap.of("time", dt.toDate().getTime(), "counts", activityCountMap));
            dt = dt.plusDays(1);
        }
        return result;
    }

    @Override
    public List<Activity> getActivitiesByCompanyAndDates(Integer companyId, Date since, Date until) {
        Activity activity = new Activity();
        activity.setCompanyId(companyId);
        ActivityExample example = new ActivityExample(activity);

        DateTime dt = new DateTime(since);
        since = dt.withTimeAtStartOfDay().toDate();
        dt = new DateTime(until);
        until = dt.withTimeAtStartOfDay().plusDays(1).toDate();
        example.getOredCriteria().get(0).andCreatedGreaterThanOrEqualTo(since).andCreatedLessThan(until);
        example.setOrderByClause("id desc");

        return activityMapper.selectByExample(example);
    }
}
