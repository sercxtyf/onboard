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
package com.onboard.domain.transform;

import org.springframework.beans.BeanUtils;

import com.google.common.base.Function;
import com.onboard.domain.model.Activity;
import com.onboard.domain.model.User;
import com.onboard.dto.ActivityDTO;

public class ActivityTransForm {

    public static final Function<Activity, ActivityDTO> ACTIVITY_TO_ACTIVITYDTO_FUNCTION = new Function<Activity, ActivityDTO>() {
        @Override
        public ActivityDTO apply(Activity input) {
            ActivityDTO activityDTO = new ActivityDTO();
            BeanUtils.copyProperties(input, activityDTO);
            return activityDTO;
        }
    };

    public static ActivityDTO ActivityAndUserToActivityDTO(Activity activity, User user) {
        ActivityDTO activityDTO = new ActivityDTO();
        BeanUtils.copyProperties(activity, activityDTO);
        if (user != null && activityDTO.getCreatorId().equals(user.getId())) {
            activityDTO.setCreator(UserTransform.userToUserDTO(user));
        }
        return activityDTO;
    }

    public static ActivityDTO activityToActivityDTO(Activity activity) {
        ActivityDTO activityDTO = new ActivityDTO();
        BeanUtils.copyProperties(activity, activityDTO);
        return activityDTO;
    }

    public static Activity activityDTOToActivity(ActivityDTO activityDTO) {
        Activity activity = new Activity();
        BeanUtils.copyProperties(activityDTO, activity);
        if (activityDTO.getCreator() != null) {
            activity.setCreator(UserTransform.userDTOToUser(activityDTO.getCreator()));
        }
        return activity;
    }
}
