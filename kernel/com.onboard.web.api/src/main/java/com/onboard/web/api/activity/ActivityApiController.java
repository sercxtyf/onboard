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
package com.onboard.web.api.activity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.elevenframework.web.interceptor.Interceptors;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Lists;
import com.onboard.domain.model.Activity;
import com.onboard.domain.transform.ActivityTransForm;
import com.onboard.dto.ActivityDTO;
import com.onboard.service.activity.ActivityService;
import com.onboard.service.security.interceptors.CompanyMemberRequired;
import com.onboard.web.api.utils.ActivityHelper;

@RequestMapping(value = "/{companyId}")
@Controller
public class ActivityApiController {

    public static final Logger logger = LoggerFactory.getLogger(ActivityApiController.class);

    public static final int LIMIT = 30;

    private static DateTimeFormatter dtf = org.joda.time.format.DateTimeFormat.forPattern("yyyy-MM-dd");

    @Autowired
    private ActivityService activityService;

    @RequestMapping(value = "/projects/{projectId}/activities", method = RequestMethod.GET)
    @Interceptors({ CompanyMemberRequired.class })
    @ResponseBody
    public ResponseEntity<?> getTabView(@PathVariable Integer companyId, @PathVariable Integer projectId,
            @RequestParam(value = "attachType", required = false) String attachType, @RequestParam(value = "attachId",
                    required = false) Integer attachId,
            @RequestParam(required = false, defaultValue = "false") Boolean stat,
            @RequestParam(required = false) Long start, @RequestParam(required = false) Long end) {
        if (stat) {
            logger.info("in");
            if (start == 0 || end == 0) {
                return new ResponseEntity<Object>(HttpStatus.BAD_REQUEST);
            } else {
                List<ActivityDTO> activityDTOs = getUserActivitiesByProjectAndTimeForStat(companyId, projectId, start,
                        end);
                return new ResponseEntity<List<ActivityDTO>>(activityDTOs, HttpStatus.OK);
            }
        }
        return new ResponseEntity<List<ActivityDTO>>(Lists.transform(
                activityService.getByAttachTypeAndId(attachType, attachId),
                ActivityTransForm.ACTIVITY_TO_ACTIVITYDTO_FUNCTION), HttpStatus.OK);
    }

    @RequestMapping(value = "/activities", method = RequestMethod.GET)
    @Interceptors({ CompanyMemberRequired.class })
    @ResponseBody
    public CompanyActivities showActivities(@PathVariable int companyId, @RequestParam(value = "until",
            required = false) @DateTimeFormat(iso = ISO.DATE) Date until,
            @RequestParam(required = false) Integer userId, @RequestParam(required = false) Integer projectId,
            @RequestParam(required = false) String type) {
        DateTime dt = until == null ? new DateTime() : new DateTime(until);
        until = dt.withTime(0, 0, 0, 0).plusDays(1).plusMillis(-1).toDate();
        boolean hasNext = false;
        Date nextDay = null;

        List<Activity> activities = activityService.getUserVisibleTillDay(companyId, userId, projectId, until, type,
                LIMIT);

        if (activities.size() > 0) {

            Activity activity = activities.get(activities.size() - 1);
            nextDay = new DateTime(activity.getCreated()).withTime(0, 0, 0, 0).plusMillis(-1).toDate();

            // 如果取到nextDay仍然有数据，则hasNext为true
            List<Activity> nextActivities = activityService.getUserVisibleTillDay(companyId, userId, projectId,
                    nextDay, type, LIMIT);

            if (nextActivities.size() > 0) {
                hasNext = true;
            }
        }
        return new CompanyActivities(activities, hasNext, nextDay);
    }

    private List<ActivityDTO> getUserActivitiesByProjectAndTimeForStat(int companyId, int projectId, Long start,
            Long end) {
        Date since = new Date(start);
        Date until = new Date(end);
        Date limit = new DateTime(until).plusMonths(-6).toDate();
        if (since.after(until) || since.before(limit)) {
            return null;
        }
        List<Activity> activities = activityService.getByProjectBetweenDates(projectId, since, until);
        return Lists.transform(activities, ActivityTransForm.ACTIVITY_TO_ACTIVITYDTO_FUNCTION);
    }

    class CompanyActivities {

        public CompanyActivities(List<Activity> activities, boolean hasNext, Date nextDay) {
            this.activities = ActivityHelper.arrangeActivitiesWithDateWithProject(Lists.transform(activities,
                    ActivityTransForm.ACTIVITY_TO_ACTIVITYDTO_FUNCTION));
            this.hasNext = hasNext;
            if (hasNext) {
                this.nextDay = dtf.print(new DateTime(nextDay));
            }
        }

        List<ArrayList<ArrayList<ActivityDTO>>> activities;
        boolean hasNext;
        String nextDay;

        public List<ArrayList<ArrayList<ActivityDTO>>> getActivities() {
            return activities;
        }

        public boolean isHasNext() {
            return hasNext;
        }

        public String getNextDay() {
            return nextDay;
        }
    }
}
