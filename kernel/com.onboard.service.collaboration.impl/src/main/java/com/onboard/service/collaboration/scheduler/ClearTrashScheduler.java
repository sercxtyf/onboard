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
package com.onboard.service.collaboration.scheduler;

import com.onboard.domain.mapper.model.ActivityExample;
import com.onboard.domain.model.Activity;
import com.onboard.service.activity.ActivityActionType;
import com.onboard.service.activity.ActivityService;
import com.onboard.service.common.identifiable.IdentifiableManager;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ClearTrashScheduler {

    @Autowired
    private ActivityService activityService;

    @Autowired
    private IdentifiableManager identifiableManager;

    @Scheduled(cron = "0 1 0 * * ?")
    public void clearTrash() {
        Activity sample = new Activity();
        sample.setAction(ActivityActionType.DISCARD);
        ActivityExample example = new ActivityExample(sample);
        example.getOredCriteria().get(0).andTrashIsNull();
        Date untilDate = new DateTime().plusDays(-30).withTimeAtStartOfDay().toDate();
        example.getOredCriteria().get(0).andCreatedLessThan(untilDate);
        List<Activity> activities = activityService.getByActivityExample(example);
        for (Activity activity : activities) {
            try {
                identifiableManager.deleteIdentifiableByTypeAndId(activity.getAttachType(), activity.getAttachId());
                /**
                 * TODO : temp fix : invoke activityService because aop can not find deleteIdentifiableById
                 */
                activityService.deleteByAttachTypeAndId(activity.getAttachType(), activity.getAttachId());
            } catch (Exception e) {
                // TODO: handle exception
            }

        }
    }
}
