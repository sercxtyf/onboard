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
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.onboard.domain.mapper.StepMapper;
import com.onboard.domain.mapper.base.BaseMapper;
import com.onboard.domain.mapper.model.StepExample;
import com.onboard.domain.model.IterationItemStatus;
import com.onboard.domain.model.Step;
import com.onboard.domain.model.type.ProjectItem;
import com.onboard.service.account.UserService;
import com.onboard.service.base.AbstractBaseService;
import com.onboard.service.collaboration.IdInProjectService;
import com.onboard.service.collaboration.IterationService;
import com.onboard.service.collaboration.KeywordService;
import com.onboard.service.collaboration.ProjectItemService;
import com.onboard.service.collaboration.ProjectService;
import com.onboard.service.collaboration.StepService;
import com.onboard.service.web.SessionService;

/**
 * {@link com.onboard.service.collaboration.StepService} Service implementation
 * 
 * @generated_by_elevenframework
 * 
 */
@Transactional
@Service("stepServiceBean")
public class StepServiceImpl extends AbstractBaseService<Step, StepExample> implements StepService, ProjectItemService {

    @Autowired
    private StepMapper stepMapper;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private UserService userService;

    @Autowired
    private IdInProjectService idInProjectService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private IterationService iterationService;

    @Autowired
    private KeywordService keywordService;

    @Override
    public Step getById(int id) {
        Step step = stepMapper.selectByPrimaryKey(id);
        if (step == null) {
            return null;
        }
        if (step.getAssigneeId() != null) {
            step.setAssignee(userService.getById(step.getAssigneeId()));
        }
        return step;
    }

    @Override
    public Step create(Step item) {
        item.setCreatedTime(new Date());
        item.setCreatorId(sessionService.getCurrentUser().getId());
        item.setCreatorName(sessionService.getCurrentUser().getName());
        item.setCreatorAvatar(sessionService.getCurrentUser().getAvatar());
        item.setIdInProject(idInProjectService.getNextIdByProjectId(item.getProjectId()));
        if (item.getAssigneeId() == null) {
            item.setAssigneeId(sessionService.getCurrentUser().getId());
        }
        if (item.getDueDate() == null) {
            item.setDueDate(DateTime.now().withTimeAtStartOfDay().plusDays(1).plusSeconds(-1).toDate());
        }
        item.setDeleted(false);
        stepMapper.insert(item);
        iterationService.addIterable(item);
        return item;
    }

    @Override
    public Step updateSelective(Step item) {
        Step old = getById(item.getId());
        Date now = new Date();
        item.setUpdatedTime(now);
        if (!item.getDueDate().equals(old.getDueDate())) {
            DateTime dt = new DateTime(item.getDueDate());
            item.setDueDate(dt.withTimeAtStartOfDay().plusDays(1).plusSeconds(-1).toDate());
        }
        if (item.getStatus() != null && !item.getStatus().equals(old.getStatus())) {
            if (item.getStatus().equals(IterationItemStatus.CLOSED.getValue())) {
                item.setCompletedTime(now);
                item.setCompleterId(sessionService.getCurrentUser().getId());
                if (item.getAssigneeId() != null) {
                    keywordService.addKeywordToUser(item, item.getAssigneeId());
                }
            }
        }
        stepMapper.updateByPrimaryKeySelective(item);
        return item;
    }

    @Override
    public List<Step> getByAttachTypeAndId(String attachType, Integer attachId) {
        Step sample = new Step();
        sample.setAttachType(attachType);
        sample.setAttachId(attachId);
        List<Step> steps = stepMapper.selectByExample(new StepExample(sample));
        for (Step step : steps) {
            step.setAssignee(userService.getById(step.getAssigneeId()));
        }
        return steps;
    }

    @Override
    public ProjectItem getItemByIdInProject(Integer projectId, Integer idInProject) {
        Step sample = new Step();
        sample.setProjectId(projectId);
        sample.setIdInProject(idInProject);
        List<Step> steps = stepMapper.selectByExample(new StepExample(sample));
        if (steps.isEmpty()) {
            return null;
        }
        assert steps.size() == 1;
        return steps.get(0);
    }

    @Override
    public List<Step> getCompletedStepsBetweenDates(Integer companyId, Date since, Date until) {
        Step sample = new Step();
        sample.setCompanyId(companyId);
        sample.setStatus(IterationItemStatus.CLOSED.getValue());
        StepExample example = new StepExample(sample);

        DateTime dt = new DateTime(since);
        since = dt.withTimeAtStartOfDay().toDate();
        dt = new DateTime(until);
        until = dt.withTimeAtStartOfDay().plusDays(1).toDate();

        example.getOredCriteria().get(0).andCompletedTimeGreaterThanOrEqualTo(since).andCompletedTimeLessThan(until);
        List<Step> results = stepMapper.selectByExample(example);

        for (Step step : results) {
            step.setProject(projectService.getById(step.getProjectId()));
        }
        return results;
    }

    @Override
    public List<Step> getOpenStepsBetweenDatesByUser(int companyId, Integer userId, Date since, Date until) {
        Step sample = new Step();
        sample.setCompanyId(companyId);
        sample.setAssigneeId(userId);
        StepExample example = new StepExample(sample);
        example.getOredCriteria().get(0).andDueDateGreaterThanOrEqualTo(since).andDueDateLessThan(until);
        example.getOredCriteria().get(0).andStatusNotEqualTo(IterationItemStatus.CLOSED.getValue());
        List<Step> results = stepMapper.selectByExample(example);
        return results;
    }

    @Override
    public TreeMap<Date, Map<Integer, List<Step>>> getCompletedStepsGroupByDateByUser(int companyId, int userId,
            List<Integer> projectList, Date until, int limit) {
        Step sample = new Step();
        sample.setAssigneeId(userId);
        sample.setDeleted(false);
        sample.setStatus(IterationItemStatus.CLOSED.getValue());
        sample.setCompanyId(companyId);
        StepExample example = new StepExample(sample);
        if (projectList != null) {
            if (projectList.size() > 0) {
                example.getOredCriteria().get(0).andProjectIdIn(projectList);
            } else {
                return new TreeMap<Date, Map<Integer, List<Step>>>();
            }
        }
        example.getOredCriteria().get(0).andCompletedTimeLessThanOrEqualTo(until);
        example.setLimit(limit);
        example.setOrderByClause("completedTime desc");
        List<Step> originSteps = stepMapper.selectByExample(example);
        if (originSteps == null || originSteps.size() == 0) {
            return new TreeMap<Date, Map<Integer, List<Step>>>();
        }
        List<Step> completedSteps = appendStepsOfLastDay(originSteps, until, example);

        return getStepsGroupByDate(completedSteps);

    }

    private List<Step> appendStepsOfLastDay(List<Step> steps, Date until, StepExample example) {
        if (steps != null && steps.size() > 0) {
            Step lastStep = steps.get(steps.size() - 1);
            Date newUntil = new DateTime(lastStep.getCompletedTime()).withTimeAtStartOfDay().toDate();
            example.getOredCriteria().get(0).andCompletedTimeBetween(newUntil, until);
            steps = stepMapper.selectByExample(example);
        }
        return steps;
    }

    private TreeMap<Date, Map<Integer, List<Step>>> getStepsGroupByDate(List<Step> steps) {
        TreeMap<Date, Map<Integer, List<Step>>> map = new TreeMap<Date, Map<Integer, List<Step>>>();
        Map<Date, List<Step>> stepsGroupByDateMap = new TreeMap<Date, List<Step>>();
        for (Step step : steps) {
            Date d = new DateTime(step.getCompletedTime()).withTimeAtStartOfDay().toDate();
            if (!stepsGroupByDateMap.containsKey(d)) {
                List<Step> list = Lists.newArrayList();
                list.add(step);
                stepsGroupByDateMap.put(d, list);
            } else {
                stepsGroupByDateMap.get(d).add(step);
            }
        }
        List<Date> dateKey = new ArrayList<Date>(stepsGroupByDateMap.keySet());
        for (Date date : dateKey) {
            Map<Integer, List<Step>> stepsGroupByProjectIdMap = new TreeMap<Integer, List<Step>>();
            for (Step step : stepsGroupByDateMap.get(date)) {
                Integer projectId = step.getProjectId();
                if (!stepsGroupByProjectIdMap.containsKey(projectId)) {
                    List<Step> list = new ArrayList<Step>();
                    list.add(step);
                    stepsGroupByProjectIdMap.put(projectId, list);
                } else {
                    stepsGroupByProjectIdMap.get(projectId).add(step);
                }
            }
            map.put(date, stepsGroupByProjectIdMap);

        }

        return map;
    }

    @Override
    public Map<Integer, List<Step>> getOpenStepsByUser(Integer userId, List<Integer> projectList) {
        Step sample = new Step();
        sample.setAssigneeId(userId);
        sample.setDeleted(false);
        StepExample example = new StepExample(sample);
        example.getOredCriteria().get(0).andStatusNotEqualTo(IterationItemStatus.CLOSED.getValue());

        if (projectList != null) {
            if (projectList.size() > 0) {
                example.getOredCriteria().get(0).andProjectIdIn(projectList);
            } else {
                return new TreeMap<Integer, List<Step>>();
            }
        }
        example.setOrderByClause("createdTime desc");
        List<Step> steps = stepMapper.selectByExample(example);
        if (steps == null || steps.size() == 0) {
            return new TreeMap<Integer, List<Step>>();
        }

        return getStepsGroupByProjectId(steps);
    }

    private Map<Integer, List<Step>> getStepsGroupByProjectId(List<Step> steps) {
        TreeMap<Integer, List<Step>> map = new TreeMap<Integer, List<Step>>();
        for (Step step : steps) {
            int projectId = step.getProjectId();
            if (!map.containsKey(projectId)) {
                List<Step> list = Lists.newArrayList();
                list.add(step);
                map.put(projectId, list);
            } else {
                map.get(projectId).add(step);
            }
        }

        return map;
    }

    @Override
    protected BaseMapper<Step, StepExample> getBaseMapper() {
        return stepMapper;
    }

    @Override
    public Step newItem() {
        return new Step();
    }

    @Override
    public StepExample newExample() {
        return new StepExample();
    }

    @Override
    public StepExample newExample(Step item) {
        return new StepExample(item);
    }

    @Override
    public List<Step> getBySample(Step item, int start, int limit) {
        StepExample example = newExample(item);
        example.setLimit(start, limit);
        example.setOrderByClause("createdTime desc");
        return getBaseMapper().selectByExample(example);
    }
}
