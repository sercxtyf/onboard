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
package com.onboard.service.activity.impl.test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.onboard.domain.mapper.ActivityMapper;
import com.onboard.domain.mapper.NotificationMapper;
import com.onboard.domain.mapper.ProjectMapper;
import com.onboard.domain.mapper.UserProjectMapper;
import com.onboard.domain.mapper.model.ActivityExample;
import com.onboard.domain.mapper.model.NotificationExample;
import com.onboard.domain.mapper.model.UserProjectExample;
import com.onboard.domain.mapper.model.common.BaseExample;
import com.onboard.domain.model.Activity;
import com.onboard.domain.model.Project;
import com.onboard.domain.model.Todo;
import com.onboard.domain.model.User;
import com.onboard.domain.model.UserProject;
import com.onboard.service.activity.ActivityActionType;
import com.onboard.service.activity.impl.ActivityServiceImpl;
import com.onboard.service.web.SessionService;
import com.onboard.test.exampleutils.CriterionVerifier;
import com.onboard.test.exampleutils.ExampleMatcher;

@RunWith(MockitoJUnitRunner.class)
public class ActivityServiceImplTest {

    @Mock
    private ActivityMapper mockActivityMapper;

    @Mock
    private ProjectMapper mockProjectMapper;

    @Mock
    private UserProjectMapper mockUserProjectMapper;

    @Mock
    private SessionService mockSessionService;

    @Mock
    private NotificationMapper notificationMapper;

    @InjectMocks
    private ActivityServiceImpl activityService;

    private static int start = 0;
    private static int limit = 5;

    private static int id = 1;
    private static String attachType = (new Todo()).getType();
    private static int attachId = 2;
    private static String subject = "创建了任务";
    private static String target = "todo_title";
    private static String content = "todo_content";

    private static int companyId = 3;
    private static int projectId = 4;
    private static String projectName = "test_project";

    private static String dateFormatPattern = "yyyy-MM-dd hh:mm";
    private static String strDate = "2014-03-04 00:00";
    private static Date time = getDateByString(strDate);

    private static int userId = 5;
    private static String userName = "test_user";

    private Activity activity;
    private List<Activity> activityList;
    private List<Integer> projectIdList;
    private User user;

    private static Date getDateByString(String strDate) {
        DateFormat fmt = new SimpleDateFormat(dateFormatPattern);
        Date date = null;
        try {
            date = fmt.parse(strDate);
        } catch (ParseException e) {
            return null;
        }
        return date;
    }

    private Activity getASampleActivity() {
        Activity a = new Activity();
        a.setAction(ActivityActionType.CREATE);
        a.setAttachId(attachId);
        a.setAttachType(attachType);
        a.setCompanyId(companyId);
        a.setContent(content);
        a.setCreated(time);
        a.setCreatorId(userId);
        a.setCreatorName(userName);
        a.setId(id);
        a.setProjectId(projectId);
        a.setProjectName(projectName);
        a.setSubject(subject);
        a.setTarget(target);
        return a;
    }

    private List<Activity> getASampleActivityList() {
        List<Activity> activities = new ArrayList<Activity>();
        activities.add(this.activity);
        return activities;
    }

    private User getASampleUser() {
        User user = new User();
        user.setId(userId);
        user.setName(userName);
        return user;
    }

    @Before
    public void setUpBefore() throws Exception {
        this.activity = getASampleActivity();
        this.activityList = getASampleActivityList();
        this.projectIdList = getProjectIds();
        this.user = getASampleUser();

        when(mockActivityMapper.selectByExample(any(ActivityExample.class))).thenReturn(activityList);
        when(mockActivityMapper.insert(any(Activity.class))).thenReturn(id);
        when(notificationMapper.deleteByExample(any(NotificationExample.class))).thenReturn(1);
    }

    @After
    public void tearDownAfter() throws Exception {
    }

    @Test
    public void getByProject() {
        List<Activity> resultList = activityService.getByProject(projectId, start, limit);
        verify(mockActivityMapper).selectByExample(argThat(new ExampleMatcher<ActivityExample>() {
            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyStart(example, start) && CriterionVerifier.verifyLimit(example, limit)
                        && CriterionVerifier.verifyEqualTo(example, "projectId", projectId)
                        && CriterionVerifier.verifyOrderByClause(example, "id desc");
            }
        }));
        assertEquals(activityList, resultList);
    }

    @Test
    public void getByTodo() {
        List<Activity> resultList = activityService.getByTodo(attachId, start, limit);
        verify(mockActivityMapper).selectByExample(argThat(new ExampleMatcher<ActivityExample>() {
            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyStart(example, start) && CriterionVerifier.verifyLimit(example, limit)
                        && CriterionVerifier.verifyEqualTo(example, "attachId", attachId)
                        && CriterionVerifier.verifyEqualTo(example, "attachType", new Todo().getType())
                        && CriterionVerifier.verifyNotEqualTo(example, "action", ActivityActionType.REPLY);
            }
        }));
        assertEquals(activityList, resultList);
    }

    @Test
    public void getActivitiesByCompany() {
        List<Activity> resultList = activityService.getByCompany(companyId, start, limit);
        verify(mockActivityMapper).selectByExample(argThat(new ExampleMatcher<ActivityExample>() {
            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyStart(example, start) && CriterionVerifier.verifyLimit(example, limit)
                        && CriterionVerifier.verifyEqualTo(example, "companyId", companyId)
                        && CriterionVerifier.verifyOrderByClause(example, "id desc");
            }
        }));
        assertEquals(activityList, resultList);
    }

    @Test
    public void createActivity() {
        activityService.create(activity);
        verify(mockActivityMapper).insert(activity);
    }

    private List<Integer> getProjectIds() {
        List<Integer> projectList = new ArrayList<Integer>();
        projectList.add(projectId);
        return projectList;
    }

    private void mockGetProjectIdListByUserByCompany(int userId, int companyId, int start, int limit) {
        UserProject userProject = new UserProject();
        userProject.setUserId(userId);
        userProject.setCompanyId(companyId);
        userProject.setProjectId(projectId);
        List<UserProject> userProjectList = new ArrayList<UserProject>();
        userProjectList.add(userProject);
        userProjectList.add(userProject);
        when(mockUserProjectMapper.selectByExample(any(UserProjectExample.class))).thenReturn(userProjectList);

        Project project1 = new Project();
        project1.setId(projectId);
        project1.setDeleted(false);
        Project project2 = new Project(project1);
        project2.setDeleted(true);
        when(mockProjectMapper.selectByPrimaryKey(projectId)).thenReturn(project1).thenReturn(project2);
    }

    private void verifyGetProjectIdListByUserByCompany(final int userId, final int companyId, final int start, final int limit) {
        // verify
        verify(mockUserProjectMapper).selectByExample(argThat(new ExampleMatcher<UserProjectExample>() {
            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "companyId", companyId)
                        && CriterionVerifier.verifyEqualTo(example, "userId", userId)
                        && CriterionVerifier.verifyStart(example, start) && CriterionVerifier.verifyLimit(example, limit);
            }
        }));
        verify(mockProjectMapper, times(2)).selectByPrimaryKey(projectId);
    }

    private void mockGetProjectIdListByUserByCompanyReturnEmpty(int userId, int companyId, int start, int limit) {
        List<UserProject> userProjectList = new ArrayList<UserProject>();
        when(mockUserProjectMapper.selectByExample(any(UserProjectExample.class))).thenReturn(userProjectList);
    }

    private void verifyGetProjectIdListByUserByCompanyReturnEmpty(final int userId, final int companyId, final int start,
            final int limit) {
        // verify
        verify(mockUserProjectMapper).selectByExample(argThat(new ExampleMatcher<UserProjectExample>() {
            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "companyId", companyId)
                        && CriterionVerifier.verifyEqualTo(example, "userId", userId)
                        && CriterionVerifier.verifyStart(example, start) && CriterionVerifier.verifyLimit(example, limit);
            }
        }));
        verify(mockProjectMapper, times(0)).selectByPrimaryKey(projectId);
    }

    private void mockGetActivitiesTillDayWithProjectIds(Activity activity, Date endTime, int limit, List<Integer> projectList) {
        when(mockActivityMapper.selectByExample(any(ActivityExample.class))).thenReturn(activityList).thenReturn(activityList);
    }

    @SuppressWarnings("rawtypes")
    private void verifyGetActivitiesTillDayWithProjectIds(final ExampleMatcher macher, final Date endTime, final int limit,
            final List<Integer> projectIds) {
        verify(mockActivityMapper).selectByExample(argThat(new ExampleMatcher<ActivityExample>() {
            @Override
            public boolean matches(BaseExample example) {
                return macher.matches(example) && CriterionVerifier.verifyStart(example, 0)
                        && CriterionVerifier.verifyLimit(example, limit)
                        && CriterionVerifier.verifyOrderByClause(example, "id desc")
                        && CriterionVerifier.verifyLessThan(example, "created", endTime)
                        && (projectIds == null || CriterionVerifier.verifyIn(example, "projectId", projectIds));
            }
        }));
        // when limit = 1, it means activities.size() == limit
        if (limit == 1) {
            verify(mockActivityMapper).selectByExample(argThat(new ExampleMatcher<ActivityExample>() {
                @Override
                public boolean matches(BaseExample example) {
                    return macher.matches(example) && CriterionVerifier.verifyOrderByClause(example, "id desc")
                            && CriterionVerifier.verifyGraterThanOrEqualTo(example, "created", time)
                            && CriterionVerifier.verifyLessThan(example, "id", id)
                            && (projectIds == null || CriterionVerifier.verifyIn(example, "projectId", projectIds));
                }
            }));
        }
    }

    @Test
    public void getByProjectTillDay() {
        // mock
        this.mockGetActivitiesTillDayWithProjectIds(activity, time, limit, projectIdList);
        // run
        List<Activity> resultList = activityService.getByProjectTillDay(projectId, time, limit);
        // verify
        // set limit to 5, so we would get one activity
        ExampleMatcher<ActivityExample> macher = new ExampleMatcher<ActivityExample>() {
            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "projectId", projectId);
            }
        };
        this.verifyGetActivitiesTillDayWithProjectIds(macher, time, limit, null);
        // result depend on value of limit
        assertEquals(resultList.size(), 1);
        assertEquals(resultList.get(0), activity);
    }

    @Test
    public void getActivitiesByCompanyTillDay() {
        int tmpLimit = 1;
        // mock
        this.mockGetActivitiesTillDayWithProjectIds(activity, time, tmpLimit, projectIdList);
        // run
        // set limit to 1, so we would just get two activity
        List<Activity> resultList = activityService.getByCompanyTillDay(companyId, time, tmpLimit);
        // verify
        ExampleMatcher<ActivityExample> macher = new ExampleMatcher<ActivityExample>() {
            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "companyId", companyId);
            }
        };
        this.verifyGetActivitiesTillDayWithProjectIds(macher, time, tmpLimit, null);
        // result depend on value of limit
        assertEquals(resultList.size(), 2);
        assertEquals(resultList.get(0), activity);
        assertEquals(resultList.get(1), activity);
    }

    @Test
    public void getUserVisibleBySampleTillDay() {
        // mock
        int tmpLimit = 1;
        when(mockSessionService.getCurrentUser()).thenReturn(user);
        this.mockGetProjectIdListByUserByCompany(user.getId(), companyId, 0, -1);
        this.mockGetActivitiesTillDayWithProjectIds(activity, time, tmpLimit, projectIdList);
        // run
        List<Activity> resultList = activityService.getUserVisibleBySampleTillDay(companyId, activity, time, tmpLimit);
        // verify
        this.verifyGetProjectIdListByUserByCompany(userId, companyId, 0, -1);
        ExampleMatcher<ActivityExample> macher = new ExampleMatcher<ActivityExample>() {
            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "companyId", companyId);
            }
        };
        this.verifyGetProjectIdListByUserByCompany(userId, companyId, 0, -1);
        this.verifyGetActivitiesTillDayWithProjectIds(macher, time, tmpLimit, null);
        // result depend on value of limit
        assertEquals(resultList.size(), 2);
        assertEquals(resultList.get(0), activity);

    }

    @Test
    public void getUserVisibleBySampleTillDayWithPeojectIdsEmpty() {
        // mock
        when(mockSessionService.getCurrentUser()).thenReturn(user);
        this.mockGetProjectIdListByUserByCompanyReturnEmpty(user.getId(), companyId, 0, -1);
        this.mockGetActivitiesTillDayWithProjectIds(activity, time, limit, projectIdList);
        // run
        List<Activity> resultList = activityService.getUserVisibleBySampleTillDay(companyId, activity, time, limit);
        // verify
        this.verifyGetProjectIdListByUserByCompanyReturnEmpty(userId, companyId, 0, -1);
        verify(mockActivityMapper, times(0)).selectByExample(argThat(new ExampleMatcher<ActivityExample>() {
            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "companyId", companyId)
                        && CriterionVerifier.verifyStart(example, 0) && CriterionVerifier.verifyLimit(example, limit)
                        && CriterionVerifier.verifyOrderByClause(example, "id desc")
                        && CriterionVerifier.verifyLessThan(example, "created", time);
            }
        }));
        // result depend on value of limit
        assertEquals(resultList.size(), 0);

    }

    @Test
    public void getUserVisibleTillDay() {
        // mock
        when(mockSessionService.getCurrentUser()).thenReturn(user);
        this.mockGetProjectIdListByUserByCompany(user.getId(), companyId, 0, -1);
        this.mockGetActivitiesTillDayWithProjectIds(activity, time, limit, projectIdList);
        // run
        activityService.getUserVisibleTillDay(companyId, userId, projectId, time, null, limit);
        // verify
        // getActivitiesCanBeViewedByUserBySampleTillDay has been veirfied, just
        // verify projectId and userId
        verify(mockActivityMapper).selectByExample(argThat(new ExampleMatcher<ActivityExample>() {
            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "projectId", projectId)
                        && CriterionVerifier.verifyEqualTo(example, "creatorId", userId);
            }
        }));
    }

    @Test
    public void getUserVisibleTillDayWithNullParamter() {
        // mock
        when(mockSessionService.getCurrentUser()).thenReturn(user);
        this.mockGetProjectIdListByUserByCompany(user.getId(), companyId, 0, -1);
        this.mockGetActivitiesTillDayWithProjectIds(activity, time, limit, projectIdList);
        // run
        activityService.getUserVisibleTillDay(companyId, null, null, time, null, limit);
        // verify
        // getActivitiesCanBeViewedByUserBySampleTillDay has been veirfied, just
        // verify projectId and userId
        verify(mockActivityMapper, times(0)).selectByExample(argThat(new ExampleMatcher<ActivityExample>() {
            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "projectId", projectId)
                        && CriterionVerifier.verifyEqualTo(example, "userId", userId);
            }
        }));
    }

    @Test
    public void getLatestByUserWithProjectIdList() {
        List<Activity> resultList = activityService.getLatestByUser(companyId, userId, limit, projectIdList);
        verify(mockActivityMapper).selectByExample(argThat(new ExampleMatcher<ActivityExample>() {
            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "creatorId", userId)
                        && CriterionVerifier.verifyEqualTo(example, "companyId", companyId)
                        && CriterionVerifier.verifyLimit(example, limit)
                        && CriterionVerifier.verifyIn(example, "projectId", projectIdList);
            }
        }));
        assertEquals(activityList, resultList);
    }

    @Test
    public void getLatestByUserWithProjectIdListEmpty() {
        List<Integer> ids = new ArrayList<Integer>();
        List<Activity> resultList = activityService.getLatestByUser(companyId, userId, limit, ids);
        verify(mockActivityMapper, times(0)).selectByExample(any(ActivityExample.class));
        assertEquals(resultList, new ArrayList<Activity>());
    }

    @Test
    public void getLatestByUserWithoutProjectIdList() {
        List<Activity> resultList = activityService.getLatestByUser(companyId, userId, limit, null);
        verify(mockActivityMapper).selectByExample(argThat(new ExampleMatcher<ActivityExample>() {
            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "creatorId", userId)
                        && CriterionVerifier.verifyEqualTo(example, "companyId", companyId)
                        && CriterionVerifier.verifyLimit(example, limit)
                        && CriterionVerifier.verifyOrderByClause(example, "id desc");
            }
        }));
        assertEquals(activityList, resultList);
    }

    @Test
    public void getByUserGroupByDateWithReturnNull() {

        when(mockActivityMapper.selectByExample(any(ActivityExample.class))).thenReturn(null);
        TreeMap<Date, List<Activity>> result = activityService
                .getByUserGroupByDate(companyId, userId, limit, projectIdList, time);
        assertEquals(true, result.isEmpty());

        when(mockActivityMapper.selectByExample(any(ActivityExample.class))).thenReturn(new ArrayList<Activity>());
        result = activityService.getByUserGroupByDate(companyId, userId, limit, projectIdList, time);
        assertEquals(true, result.isEmpty());

    }

    @Test
    public void getByUserGroupByDateWithProjectIdListEmpty() {
        TreeMap<Date, List<Activity>> result = activityService.getByUserGroupByDate(companyId, userId, limit,
                new ArrayList<Integer>(), time);
        verify(mockActivityMapper, times(0)).selectByExample(any(ActivityExample.class));
        assertEquals(result, new TreeMap<Date, List<Activity>>());
    }

    @Test
    public void getByUserGroupByDateWithProjectIdList() {
        // mock
        this.mockAppendActivitiesOfLastDay();
        // run
        TreeMap<Date, List<Activity>> result = activityService
                .getByUserGroupByDate(companyId, userId, limit, projectIdList, time);
        // verify
        verify(mockActivityMapper, times(2)).selectByExample(argThat(new ExampleMatcher<ActivityExample>() {
            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "creatorId", userId)
                        && CriterionVerifier.verifyEqualTo(example, "companyId", companyId)
                        && CriterionVerifier.verifyOrderByClause(example, "id desc")
                        && CriterionVerifier.verifyIn(example, "projectId", projectIdList)
                        && CriterionVerifier.verifyLessThanOrEqualTo(example, "created", time)
                        && CriterionVerifier.verifyLimit(example, limit);
            }
        }));
        this.verifyAppendActivitiesOfLastDay();
        Date date = new DateTime(time).withTimeAtStartOfDay().toDate();
        assertEquals(true, result.containsKey(date));
        assertEquals(2, result.get(date).size());
        assertEquals(activity, result.get(date).get(0));
        assertEquals(activity, result.get(date).get(1));
    }

    @Test
    public void getByUserGroupByDateWithoutProjectIdList() {
        // mock
        this.mockAppendActivitiesOfLastDay();
        // run
        TreeMap<Date, List<Activity>> result = activityService.getByUserGroupByDate(companyId, userId, limit, null, time);
        // verify
        verify(mockActivityMapper, times(2)).selectByExample(argThat(new ExampleMatcher<ActivityExample>() {
            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "creatorId", userId)
                        && CriterionVerifier.verifyEqualTo(example, "companyId", companyId)
                        && CriterionVerifier.verifyLessThanOrEqualTo(example, "created", time)
                        && CriterionVerifier.verifyLimit(example, limit)
                        && CriterionVerifier.verifyOrderByClause(example, "id desc");
            }
        }));
        this.verifyAppendActivitiesOfLastDay();
        Date date = new DateTime(time).withTimeAtStartOfDay().toDate();
        assertEquals(true, result.containsKey(date));
        assertEquals(2, result.get(date).size());
        assertEquals(activity, result.get(date).get(0));
        assertEquals(activity, result.get(date).get(1));
    }

    private void mockAppendActivitiesOfLastDay() {
        List<Activity> activities = new ArrayList<Activity>();
        activities.add(activity);
        Activity tmpActivity1 = new Activity(activity);
        tmpActivity1.setCreated(getDateByString("2014-04-04 00:00"));
        activities.add(tmpActivity1);
        when(mockActivityMapper.selectByExample(any(ActivityExample.class))).thenReturn(activityList).thenReturn(activities);
    }

    private void verifyAppendActivitiesOfLastDay() {
        verify(mockActivityMapper, times(2)).selectByExample(argThat(new ExampleMatcher<ActivityExample>() {
            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyGraterThanOrEqualTo(example, "created", new DateTime(activity.getCreated())
                        .withTimeAtStartOfDay().toDate())
                        && CriterionVerifier.verifyLessThan(example, "created", activity.getCreated());
            }
        }));
    }

    @Test
    public void getByProjectByDate() {
        List<Activity> resultList = activityService.getByProjectByDate(projectId, time);
        verify(mockActivityMapper).selectByExample(argThat(new ExampleMatcher<ActivityExample>() {
            @Override
            public boolean matches(BaseExample example) {
                Date start = new DateTime(time).withTimeAtStartOfDay().toDate();
                Date end = new DateTime(time).plusDays(1).withTimeAtStartOfDay().toDate();
                return CriterionVerifier.verifyEqualTo(example, "projectId", projectId)
                        && CriterionVerifier.verifyGraterThanOrEqualTo(example, "created", start)
                        && CriterionVerifier.verifyLessThan(example, "created", end);
            }
        }));
        assertEquals(activityList, resultList);
    }

    @Test
    public void getByAttachTypeAndId() {
        List<Activity> resultList = activityService.getByAttachTypeAndId(attachType, attachId);
        verify(mockActivityMapper).selectByExample(argThat(new ExampleMatcher<ActivityExample>() {
            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "attachId", attachId)
                        && CriterionVerifier.verifyEqualTo(example, "attachType", attachType);
            }
        }));
        assertEquals(activityList, resultList);
    }

    @Test
    public void getById() {
        when(mockActivityMapper.selectByPrimaryKey(anyInt())).thenReturn(activity);

        Activity result = activityService.getById(id);

        verify(mockActivityMapper).selectByPrimaryKey(id);
        assertEquals(activity, result);
    }

    @Test
    public void update() {

        Activity result = activityService.update(activity);

        verify(mockActivityMapper).updateByPrimaryKey(activity);
        assertEquals(activity, result);
    }

    @Test
    public void delete() {

        activityService.delete(id);

        verify(mockActivityMapper).deleteByPrimaryKey(id);
    }

    @Test
    public void getLatestByUserByPage() {
        List<Activity> resultList = activityService.getLatestByUserByPage(companyId, userId, start, limit, projectIdList);
        verify(mockActivityMapper).selectByExample(argThat(new ExampleMatcher<ActivityExample>() {
            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "companyId", companyId)
                        && CriterionVerifier.verifyStart(example, start) && CriterionVerifier.verifyLimit(example, limit)
                        && CriterionVerifier.verifyIn(example, "projectId", projectIdList);
            }
        }));
        assertEquals(activityList, resultList);

    }
}
