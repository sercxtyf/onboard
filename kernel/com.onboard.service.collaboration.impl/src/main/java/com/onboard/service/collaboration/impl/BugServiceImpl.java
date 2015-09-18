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
package com.onboard.service.collaboration.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.onboard.domain.mapper.BugMapper;
import com.onboard.domain.mapper.base.BaseMapper;
import com.onboard.domain.mapper.model.BugExample;
import com.onboard.domain.model.Bug;
import com.onboard.domain.model.Bug.BugStatus;
import com.onboard.domain.model.User;
import com.onboard.domain.model.type.ProjectItem;
import com.onboard.service.account.UserService;
import com.onboard.service.base.AbstractBaseService;
import com.onboard.service.collaboration.BugService;
import com.onboard.service.collaboration.CommentService;
import com.onboard.service.collaboration.IdInProjectService;
import com.onboard.service.collaboration.IterationService;
import com.onboard.service.collaboration.ProjectItemService;
import com.onboard.service.collaboration.ProjectService;
import com.onboard.service.common.subscrible.SubscriberService;
import com.onboard.service.web.SessionService;

/**
 * {@link com.onboard.service.collaboration.BugService} Service implementation
 * 
 * @generated_by_elevenframework
 * 
 */
@Transactional
@Service("bugServiceBean")
public class BugServiceImpl extends AbstractBaseService<Bug, BugExample> implements BugService, ProjectItemService {
    public static final int DEFAULT_LIMIT = -1;

    @Autowired
    BugMapper bugMapper;
    @Autowired
    SubscriberService subscriberService;
    @Autowired
    CommentService commentService;
    @Autowired
    UserService userService;
    @Autowired
    IdInProjectService idInProjectService;
    @Autowired
    IterationService iterationService;
    @Autowired
    ProjectService projectService;
    @Autowired
    SessionService sessionService;

    private static int MIN_BUG_NUMBER = 10;

    @Override
    public Bug getById(int id) {
        Bug bug = bugMapper.selectByPrimaryKey(id);
        if (bug == null) {
            return null;
        }
        if (bug.getAssigneeId() != null) {
            bug.setAssignee(userService.getById(bug.getAssigneeId()));
        }
        return bug;
    }

    @Override
    public Bug getBugByIdWithCommentAndSubscriable(int id) {
        Bug bug = bugMapper.selectByPrimaryKey(id);

        if (bug == null) {
            return bug;
        }
        Bug newBug = new Bug(bug);
        commentService.fillCommentable(newBug, 0, DEFAULT_LIMIT);
        subscriberService.fillSubcribers(newBug);

        User assignee = userService.getById(bug.getAssigneeId());
        if (assignee != null) {
            newBug.setAssignee(assignee);
        }

        return newBug;
    }

    @Override
    public Bug create(Bug item) {
        item.setIdInProject(idInProjectService.getNextIdByProjectId(item.getProjectId()));
        item.setCreated(new Date());
        item.setCreatorAvatar(sessionService.getCurrentUser().getAvatar());
        item.setCreatorId(sessionService.getCurrentUser().getId());
        item.setCreatorName(sessionService.getCurrentUser().getName());
        bugMapper.insert(item);
        iterationService.addIterable(item);
        return item;
    }

    @Override
    public Bug updateSelective(Bug item) {
        Bug bug = bugMapper.selectByPrimaryKey(item.getId());
        if (!item.getDueTime().equals(bug.getDueTime())) {
            DateTime dt = new DateTime(item.getDueTime());
            item.setDueTime(dt.withTimeAtStartOfDay().plusDays(1).plusSeconds(-1).toDate());
        }
        if (bug.getStatus() != 0 && item.getStatus() == 0) {
            item.setCompletedTime(new Date());
        }
        bugMapper.updateByPrimaryKeySelective(item);
        return item;
    }

    @Override
    public List<Bug> getAllBugsByProject(int projectId) {
        Bug bug = new Bug();
        bug.setProjectId(projectId);
        BugExample example = new BugExample(bug);
        return bugMapper.selectByExample(example);
    }

    @Override
    public List<Bug> getBugsByStatusByProject(int projectId, int status) {
        Bug bug = new Bug();
        bug.setProjectId(projectId);
        bug.setStatus(status);
        BugExample example = new BugExample(bug);
        return bugMapper.selectByExample(example);
    }

    @Override
    public List<Bug> getOpenedBugsByProject(int projectId, int start, int limit) {
        Bug bug = new Bug();
        bug.setProjectId(projectId);
        bug.setDeleted(false);
        BugExample example = new BugExample(bug);

        example.setStart(start);
        example.setLimit(limit);

        example.getOredCriteria().get(0).andStatusGreaterThan(0);
        example.setOrderByClause("id desc");

        return bugMapper.selectByExample(example);
    }

    @Override
    public List<Bug> getFinishedBugsByProject(int projectId, int start, int limit) {
        Bug bug = new Bug();
        bug.setProjectId(projectId);
        bug.setDeleted(false);
        BugExample example = new BugExample(bug);

        example.setStart(start);
        example.setLimit(limit);

        example.getOredCriteria().get(0).andStatusEqualTo(0);
        example.setOrderByClause("id desc");

        return bugMapper.selectByExample(example);
    }

    @Override
    public ProjectItem getItemByIdInProject(Integer projectId, Integer idInProject) {
        Bug sample = new Bug();
        sample.setProjectId(projectId);
        sample.setIdInProject(idInProject);
        List<Bug> bugs = bugMapper.selectByExample(new BugExample(sample));
        if (bugs.isEmpty()) {
            return null;
        }
        assert bugs.size() == 1;
        return bugs.get(0);
    }

    @Override
    public List<Bug> getCompletedBugsBetweenDates(Integer companyId, Date since, Date until) {
        Bug sample = new Bug();
        sample.setCompanyId(companyId);
        sample.setStatus(0);
        BugExample example = new BugExample(sample);

        DateTime dt = new DateTime(since);
        since = dt.withTimeAtStartOfDay().toDate();
        dt = new DateTime(until);
        until = dt.withTimeAtStartOfDay().plusDays(1).toDate();

        example.getOredCriteria().get(0).andCompletedTimeGreaterThanOrEqualTo(since).andCompletedTimeLessThan(until);
        List<Bug> results = bugMapper.selectByExample(example);

        for (Bug bug : results) {
            bug.setProject(projectService.getById(bug.getProjectId()));
        }
        return results;
    }

    @Override
    public TreeMap<Date, Map<Integer, List<Bug>>> getCompletedBugsGroupByDateByUser(int companyId, int userId,
            List<Integer> projectList, Date until, int limit) {
        Bug sample = new Bug();
        sample.setAssigneeId(userId);
        sample.setDeleted(false);
        sample.setStatus(BugStatus.CLOSED.getValue());
        sample.setCompanyId(companyId);
        BugExample example = new BugExample(sample);
        if (projectList != null) {
            if (projectList.size() > 0) {
                example.getOredCriteria().get(0).andProjectIdIn(projectList);
            } else {
                return new TreeMap<Date, Map<Integer, List<Bug>>>();
            }
        }
        example.getOredCriteria().get(0).andCompletedTimeLessThanOrEqualTo(until);
        example.setLimit(limit);
        example.setOrderByClause("completedTime desc");
        List<Bug> originBugs = bugMapper.selectByExample(example);
        if (originBugs == null || originBugs.size() == 0) {
            return new TreeMap<Date, Map<Integer, List<Bug>>>();
        }
        List<Bug> completedBugs = appendBugsOfLastDay(originBugs, until, example);

        return getBugsGroupByDate(completedBugs);

    }

    private List<Bug> appendBugsOfLastDay(List<Bug> bugs, Date until, BugExample example) {
        if (bugs != null && bugs.size() > 0) {
            Bug lastBug = bugs.get(bugs.size() - 1);
            Date newUntil = new DateTime(lastBug.getCompletedTime()).withTimeAtStartOfDay().toDate();
            example.getOredCriteria().get(0).andCompletedTimeBetween(newUntil, until);
            bugs = bugMapper.selectByExample(example);
        }

        return bugs;
    }

    private TreeMap<Date, Map<Integer, List<Bug>>> getBugsGroupByDate(List<Bug> bugs) {
        TreeMap<Date, Map<Integer, List<Bug>>> map = new TreeMap<Date, Map<Integer, List<Bug>>>();
        Map<Date, List<Bug>> bugsGroupByDateMap = new TreeMap<Date, List<Bug>>();
        for (Bug bug : bugs) {
            Date d = new DateTime(bug.getCompletedTime()).withTimeAtStartOfDay().toDate();
            if (!bugsGroupByDateMap.containsKey(d)) {
                List<Bug> list = Lists.newArrayList();
                list.add(bug);
                bugsGroupByDateMap.put(d, list);
            } else {
                bugsGroupByDateMap.get(d).add(bug);
            }
        }
        List<Date> dateKey = new ArrayList<Date>(bugsGroupByDateMap.keySet());
        for (Date date : dateKey) {
            Map<Integer, List<Bug>> bugsGroupByProjectIdMap = new TreeMap<Integer, List<Bug>>();
            for (Bug bug : bugsGroupByDateMap.get(date)) {
                Integer projectId = bug.getProjectId();
                if (!bugsGroupByProjectIdMap.containsKey(projectId)) {
                    List<Bug> list = new ArrayList<Bug>();
                    list.add(bug);
                    bugsGroupByProjectIdMap.put(projectId, list);
                } else {
                    bugsGroupByProjectIdMap.get(projectId).add(bug);
                }
            }
            map.put(date, bugsGroupByProjectIdMap);

        }

        return map;
    }

    private TreeMap<Integer, List<Bug>> getBugsGroupByProjectId(List<Bug> bugs) {
        TreeMap<Integer, List<Bug>> map = new TreeMap<Integer, List<Bug>>();
        for (Bug bug : bugs) {
            int projectId = bug.getProjectId();
            if (!map.containsKey(projectId)) {
                List<Bug> list = Lists.newArrayList();
                list.add(bug);
                map.put(projectId, list);
            } else {
                map.get(projectId).add(bug);
            }
        }

        return map;
    }

    @Override
    public TreeMap<Integer, List<Bug>> getOpenBugsByUser(Integer userId, List<Integer> projectList) {
        Bug sample = new Bug(false);
        sample.setAssigneeId(userId);
        BugExample example = new BugExample(sample);
        example.getOredCriteria().get(0).andStatusNotEqualTo(BugStatus.CLOSED.getValue());

        if (projectList != null) {
            if (projectList.size() > 0) {
                example.getOredCriteria().get(0).andProjectIdIn(projectList);
            } else {
                return new TreeMap<Integer, List<Bug>>();
            }
        }
        example.setOrderByClause("createdTime desc");
        List<Bug> originBugs = bugMapper.selectByExample(example);
        if (originBugs == null || originBugs.size() == 0) {
            return new TreeMap<Integer, List<Bug>>();
        }

        return getBugsGroupByProjectId(originBugs);
    }

    @Override
    public List<Bug> getOpenBugsBetweenDatesByUser(int companyId, Integer userId, Date since, Date until) {
        Bug sample = new Bug();
        sample.setCompanyId(companyId);
        sample.setAssigneeId(userId);
        BugExample example = new BugExample(sample);
        example.getOredCriteria().get(0).andDueTimeGreaterThanOrEqualTo(since).andDueTimeLessThan(until);
        example.getOredCriteria().get(0).andStatusNotEqualTo(0);
        List<Bug> results = bugMapper.selectByExample(example);
        return results;
    }

    @Override
    public Long getCompletedBugAveDurationByProjectIdDateBackByMonth(Integer projectId, Integer months) {
        Bug sample = new Bug();
        sample.setProjectId(projectId);
        sample.setStatus(0);
        BugExample example = new BugExample(sample);

        DateTime dt = new DateTime(new Date());
        Date since = dt.withTimeAtStartOfDay().plusMonths(-months).toDate();
        Date until = dt.withTimeAtStartOfDay().plusDays(1).toDate();

        example.getOredCriteria().get(0).andCompletedTimeGreaterThanOrEqualTo(since).andCompletedTimeLessThan(until);
        List<Bug> results = bugMapper.selectByExample(example);
        int length = results.size();
        if (length < MIN_BUG_NUMBER)
            return 0L;
        else {
            Long result = 0L;
            for (Bug bug : results) {
                result += bug.getCompletedTime().getTime() - bug.getCreatedTime().getTime();
            }
            return result / length;
        }
    }

    @Override
    public Long getCompletedBugThirdQuarterDurationByProjectIdDateBackByMonth(Integer projectId, Integer months) {
        Bug sample = new Bug();
        sample.setProjectId(projectId);
        sample.setStatus(0);
        BugExample example = new BugExample(sample);

        DateTime dt = new DateTime(new Date());
        Date since = dt.withTimeAtStartOfDay().plusMonths(-months).toDate();
        Date until = dt.withTimeAtStartOfDay().plusDays(1).toDate();

        example.getOredCriteria().get(0).andCompletedTimeGreaterThanOrEqualTo(since).andCompletedTimeLessThan(until);
        List<Bug> results = bugMapper.selectByExample(example);
        int length = results.size();
        if (length < MIN_BUG_NUMBER)
            return 0L;
        else {
            int k = length * 3 / 4 - 1;
            long[] arr = new long[length];
            int i = 0;
            for (Bug bug : results) {
                arr[i] += bug.getCompletedTime().getTime() - bug.getCreatedTime().getTime();
                i++;
            }
            Arrays.sort(arr);
            return arr[k];
        }
    }

    public void recoverIdentifiableById(int id) {
        Bug bug = new Bug(id);
        bug.setDeleted(false);
        bugMapper.updateByPrimaryKeySelective(bug);
    }

    @Override
    protected BaseMapper<Bug, BugExample> getBaseMapper() {
        return bugMapper;
    }

    @Override
    public Bug newItem() {
        return new Bug();
    }

    @Override
    public BugExample newExample() {
        return new BugExample();
    }

    @Override
    public BugExample newExample(Bug item) {
        return new BugExample(item);
    }
}
