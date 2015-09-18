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
package com.onboard.web.api.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;

import com.google.common.collect.Lists;
import com.onboard.domain.model.Activity;
import com.onboard.dto.ActivityDTO;

public class ActivityHelper {

    public static List<ArrayList<ArrayList<ActivityDTO>>> arrangeActivitiesWithDateWithProject(List<ActivityDTO> activities) {
        List<ArrayList<ArrayList<ActivityDTO>>> activitiesModel = Lists.newArrayList();

        int dayCount = 0;

        if (activities != null && activities.size() > 0) {
            ArrayList<ActivityDTO> listByProject = Lists.newArrayList();
            listByProject.add(activities.get(0));

            ArrayList<ArrayList<ActivityDTO>> listByDay = Lists.newArrayList();
            listByDay.add(listByProject);

            activitiesModel.add(listByDay);
            dayCount++;
        }

        int projectCount = 1;

        for (int i = 1; i < activities.size(); i++) {

            if (isSameDay(activities.get(i).getCreated(), activities.get(i - 1).getCreated())) {

                ArrayList<ArrayList<ActivityDTO>> listByDay = activitiesModel.get(dayCount - 1);

                if (activities.get(i).getProjectId().intValue() == activities.get(i - 1).getProjectId().intValue()) {
                    ArrayList<ActivityDTO> listByProject = listByDay.get(projectCount - 1);
                    listByProject.add(activities.get(i));
                } else {
                    ArrayList<ActivityDTO> listByProject = Lists.newArrayList();
                    listByProject.add(activities.get(i));
                    listByDay.add(listByProject);
                    projectCount++;
                }

            } else {
                ArrayList<ActivityDTO> listByProject = Lists.newArrayList();
                listByProject.add(activities.get(i));

                ArrayList<ArrayList<ActivityDTO>> listByDay = Lists.newArrayList();
                listByDay.add(listByProject);

                activitiesModel.add(listByDay);
                dayCount++;
                projectCount = 1;
            }
        }

        return activitiesModel;
    }

    public static List<ArrayList<Activity>> arrangeActivitiesWithDate(List<Activity> activities) {
        List<ArrayList<Activity>> activitiesModel = new ArrayList<ArrayList<Activity>>();

        int dayCount = 0;

        if (activities != null && activities.size() > 0) {

            ArrayList<Activity> listByDay = new ArrayList<Activity>();
            listByDay.add(activities.get(0));
            activitiesModel.add(listByDay);
            dayCount++;
        }

        for (int i = 1; i < activities.size(); i++) {
            if (isSameDay(activities.get(i).getCreated(), activities.get(i - 1).getCreated())) {
                activitiesModel.get(dayCount - 1).add(activities.get(i));
            } else {
                ArrayList<Activity> listByDay = new ArrayList<Activity>();
                listByDay.add(activities.get(i));
                activitiesModel.add(listByDay);
                dayCount++;
            }
        }

        return activitiesModel;
    }

    private static boolean isSameDay(Date d1, Date d2) {
        return new DateTime(d1).toLocalDate().isEqual(new DateTime(d2).toLocalDate());
    }
}
