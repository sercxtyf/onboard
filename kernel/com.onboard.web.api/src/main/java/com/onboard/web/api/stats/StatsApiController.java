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
package com.onboard.web.api.stats;

import java.util.Date;
import java.util.List;

import org.elevenframework.web.interceptor.Interceptors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Lists;
import com.onboard.domain.model.Activity;
import com.onboard.domain.model.Bug;
import com.onboard.domain.model.Comment;
import com.onboard.domain.model.Discussion;
import com.onboard.domain.model.Step;
import com.onboard.domain.model.Todo;
import com.onboard.domain.transform.ActivityTransForm;
import com.onboard.domain.transform.BugTransForm;
import com.onboard.domain.transform.CommentTransform;
import com.onboard.domain.transform.DiscussionTransform;
import com.onboard.domain.transform.StepTransform;
import com.onboard.domain.transform.TodoTransform;
import com.onboard.dto.ActivityDTO;
import com.onboard.dto.BugDTO;
import com.onboard.dto.CommentDTO;
import com.onboard.dto.DiscussionDTO;
import com.onboard.dto.StepDTO;
import com.onboard.dto.TodoDTO;
import com.onboard.service.account.UserService;
import com.onboard.service.activity.ActivityService;
import com.onboard.service.collaboration.BugService;
import com.onboard.service.collaboration.CommentService;
import com.onboard.service.collaboration.DiscussionService;
import com.onboard.service.collaboration.ProjectService;
import com.onboard.service.collaboration.StepService;
import com.onboard.service.collaboration.TodoService;
import com.onboard.service.security.interceptors.CompanyMemberRequired;

@RequestMapping("/{companyId}/stats")
@Controller
public class StatsApiController {

    @Autowired
    private ActivityService activityService;

    @Autowired
    private TodoService todoService;

    @Autowired
    private BugService bugService;

    @Autowired
    private StepService stepService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private UserService userService;

    @Autowired
    private DiscussionService discussionService;

    @Autowired
    private CommentService commentService;

    private static Logger logger = LoggerFactory.getLogger(StatsApiController.class);

    @RequestMapping(value = "/completedTodos", method = RequestMethod.GET)
    @Interceptors({ CompanyMemberRequired.class })
    @ResponseBody
    public List<TodoDTO> getCompanyTodoStats(@PathVariable int companyId, @RequestParam(value = "start") long start,
            @RequestParam(value = "end") long end) {
        Date since = new Date(start);
        Date until = new Date(end);
        List<Todo> todos = todoService.getCompletedTodosBetweenDates(companyId, since, until);
        logger.info("There are {} todos found", todos.size());
        return Lists.transform(todos, TodoTransform.TODO_DTO_FUNCTION);
    }

    @RequestMapping(value = "/completedBugs", method = RequestMethod.GET)
    @Interceptors({ CompanyMemberRequired.class })
    @ResponseBody
    public List<BugDTO> getCompanyBugStats(@PathVariable int companyId, @RequestParam(value = "start") long start,
            @RequestParam(value = "end") long end) {
        Date since = new Date(start);
        Date until = new Date(end);
        List<Bug> bugs = bugService.getCompletedBugsBetweenDates(companyId, since, until);
        logger.info("There are {} bugs found", bugs.size());
        for (Bug bug : bugs) {
            if (bug.getAssigneeId() != null) {
                bug.setAssignee(userService.getById(bug.getAssigneeId()));
            }
        }
        return Lists.transform(bugs, BugTransForm.BUG_TO_BUGDTO_FUNCTION);
    }

    @RequestMapping(value = "/completedSteps", method = RequestMethod.GET)
    @Interceptors({ CompanyMemberRequired.class })
    @ResponseBody
    public List<StepDTO> getCompanyStepStats(@PathVariable int companyId, @RequestParam(value = "start") long start,
            @RequestParam(value = "end") long end) {
        Date since = new Date(start);
        Date until = new Date(end);
        List<Step> steps = stepService.getCompletedStepsBetweenDates(companyId, since, until);
        logger.info("There are {} steps found", steps.size());
        for (Step step : steps) {
            if (step.getAssigneeId() != null) {
                step.setAssignee(userService.getById(step.getAssigneeId()));
            }
        }
        return Lists.transform(steps, StepTransform.STEP_DTO_FUNCTION);
    }

    @RequestMapping(value = "/activities", method = RequestMethod.GET)
    @Interceptors({ CompanyMemberRequired.class })
    @ResponseBody
    public List<ActivityDTO> getCompanyActivityStats(@PathVariable int companyId, @RequestParam(value = "start") long start,
            @RequestParam(value = "end") long end) {
        Date since = new Date(start);
        Date until = new Date(end);
        List<Activity> activities = activityService.getActivitiesByCompanyAndDates(companyId, since, until);
        return Lists.transform(activities, ActivityTransForm.ACTIVITY_TO_ACTIVITYDTO_FUNCTION);
    }

    @RequestMapping(value = "/discussions", method = RequestMethod.GET)
    @Interceptors({ CompanyMemberRequired.class })
    @ResponseBody
    public List<DiscussionDTO> getCompanyDiscussions(@PathVariable int companyId, @RequestParam(value = "start") long start,
            @RequestParam(value = "end") long end) {
        Date since = new Date(start);
        Date until = new Date(end);
        List<Discussion> discussions = discussionService.getDiscussionsByCompanyIdBetweenDates(companyId, since, until);
        return Lists.transform(discussions, DiscussionTransform.DISCUSSION_DTO_FUNCTION);
    }

    @RequestMapping(value = "/comments", method = RequestMethod.GET)
    @Interceptors({ CompanyMemberRequired.class })
    @ResponseBody
    public List<CommentDTO> getCompanyComments(@PathVariable int companyId, @RequestParam(value = "start") long start,
            @RequestParam(value = "end") long end) {
        Date since = new Date(start);
        Date until = new Date(end);
        List<Comment> comments = commentService.getCommentsByCompanyIdBetweenDates(companyId, since, until);
        return Lists.transform(comments, CommentTransform.COMMENT_TO_DTO_FUNCTION);
    }
}
