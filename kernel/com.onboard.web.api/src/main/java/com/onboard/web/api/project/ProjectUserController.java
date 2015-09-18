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
package com.onboard.web.api.project;

import java.util.Date;
import java.util.List;

import org.elevenframework.web.interceptor.Interceptors;
import org.joda.time.DateTime;
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
import com.onboard.domain.model.Project;
import com.onboard.domain.transform.ActivityTransForm;
import com.onboard.dto.ActivityDTO;
import com.onboard.service.activity.ActivityService;
import com.onboard.service.collaboration.ProjectService;
import com.onboard.service.security.interceptors.ProjectMemberRequired;
import com.onboard.service.security.interceptors.UserChecking;

@RequestMapping(value = "")
@Controller
public class ProjectUserController {
    public static final Logger logger = LoggerFactory.getLogger(ProjectUserController.class);
    @Autowired
    private ProjectService projectService;

    @Autowired
    private ActivityService activityService;

    @RequestMapping(value = "/{companyId}/projects/{projectId}/users/{userId}/activities", method = RequestMethod.GET)
    @Interceptors({ ProjectMemberRequired.class, UserChecking.class })
    @ResponseBody
    public List<ActivityDTO> getUserActivitiesByProjectAndTime(@PathVariable int companyId, @PathVariable int projectId,
            @PathVariable int userId, @RequestParam(value = "start", required = true) Long start,
            @RequestParam(value = "end", required = true) Long end) {
        Date since = new Date(start);
        Date until = new Date(end);
        Date limit = new DateTime(until).plusMonths(-6).toDate();
        if (since.after(until) || since.before(limit)) {
            return null;
        }
        List<Activity> activities = activityService.getByProjectUserBetweenDates(projectId, userId, since, until);
        return Lists.transform(activities, ActivityTransForm.ACTIVITY_TO_ACTIVITYDTO_FUNCTION);
    }

    @RequestMapping(value = "/{companyId}/users/{userId}", method = RequestMethod.GET)
    @ResponseBody
    public Boolean currentUserHasProject(@PathVariable int companyId, @PathVariable int userId) {
        List<Project> projects = projectService.getActiveProjectListByUserByCompany(userId, companyId, 0, -1);
        if (projects != null && projects.size() > 0) {
            return true;
        } else {
            return false;
        }
    }

}
