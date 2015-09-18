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
package com.onboard.service.collaboration.activity;

import java.text.SimpleDateFormat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.onboard.domain.model.Activity;
import com.onboard.domain.model.Iteration;
import com.onboard.domain.model.Iteration.IterationStatus;
import com.onboard.domain.model.type.BaseProjectItem;
import com.onboard.service.activity.ActivityActionType;
import com.onboard.service.activity.ActivityGenerator;
import com.onboard.service.activity.util.ActivityHelper;
import com.onboard.service.collaboration.IterationService;
import com.onboard.service.web.SessionService;

/**
 * 
 * @author R
 * 
 */

@Service("iterationActivityGeneratorBean")
public class IterationActivityGenerator implements ActivityGenerator {

    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("MM月dd日 ");

    public static final String START_ITERATION_ACTION = "start";
    public static final String COMPLETE_ITERATION_ACTION = "complete";
    public static final String CREATE_ITERATION_SUBJECT = "创建了迭代";
    public static final String UPDATE_ITERATION_SUBJECT = "更新了迭代";
    public static final String START_ITERATION_SUBJECT = "开始了迭代";
    public static final String COMPLETE_ITERATION_SUBJECT = "完成了迭代";

    public static final String UPDATE_ITERATION_NAME_CONTENT = "名称由“%s”变为“%s”";
    public static final String UPDATE_ITERATION_STARTTIME_CONTENT = "开始时间由“%s”变为“%s”";
    public static final String UPDATE_ITERATION_ENDTIME_CONTENT = "截止时间由“%s”变为“%s”";
    public static final String START_ITERATION_CONTENT = "开始时间为“%s”，截止时间为“%s”";

    @Autowired
    private IterationService iterationService;

    @Autowired
    private SessionService sessionService;

    @Override
    public String modelType() {
        return new Iteration().getType();
    }

    @Override
    public String modelService() {
        return IterationService.class.getName();
    }

    @Override
    public BaseProjectItem enrichModel(BaseProjectItem identifiable) {
        return new Iteration(iterationService.getById(identifiable.getId()));
    }

    private Activity generateActivityByActionType(String actionType, String subject, Iteration iteration) {
        Activity activity = ActivityHelper.generateActivityByActionType(actionType, subject, iteration);
        activity.setProjectId(iteration.getProjectId());
        activity.setCompanyId(iteration.getCompanyId());
        activity.setTarget(iteration.getName());
        return ActivityRecorderHelper.enrichActivity(activity);
    }

    @Override
    public Activity generateCreateActivity(BaseProjectItem item) {
        return null;
        // Iteration iteration = (Iteration) item;
        // Activity activity = generateActivityByActionType(ActivityActionType.CREATE, CREATE_ITERATION_SUBJECT, iteration);
        // return activity;
    }

    @Override
    public Activity generateUpdateActivity(BaseProjectItem item, BaseProjectItem modifiedItem) {
        Iteration original = (Iteration) item;
        Iteration updated = (Iteration) modifiedItem;
        Activity activity = null;
        if (isStartIterration(original, updated)) {
            activity = generateActivityByActionType(START_ITERATION_ACTION, START_ITERATION_SUBJECT, updated);
            activity.setContent(String.format(START_ITERATION_CONTENT, dateFormat.format(updated.getStartTime()),
                    dateFormat.format(updated.getEndTime())));
        } else if (isCompleteIterration(original, updated)) {
            activity = generateActivityByActionType(COMPLETE_ITERATION_ACTION, COMPLETE_ITERATION_SUBJECT, updated);
        } else {
            String content = getUpdateIterationInfoContent(original, updated);
            if (!(content == null || content.length() == 0)) {
                activity = generateActivityByActionType(ActivityActionType.UPDATE, UPDATE_ITERATION_SUBJECT, updated);
                activity.setContent(content);
            }
        }
        return activity;
    }

    private boolean isStartIterration(Iteration item, Iteration modifiedItem) {
        return modifiedItem.getStatus() != null && modifiedItem.getStatus().equals(IterationStatus.ACTIVE.getValue())
                && item.getStatus().equals(IterationStatus.CREATED.getValue());
    }

    private boolean isCompleteIterration(Iteration item, Iteration modifiedItem) {
        return modifiedItem.getStatus() != null && modifiedItem.getStatus().equals(IterationStatus.COMPLETED.getValue())
                && item.getStatus().equals(IterationStatus.ACTIVE.getValue());
    }

    private String getUpdateIterationInfoContent(Iteration item, Iteration modifiedItem) {
        StringBuffer contentBuffer = new StringBuffer();
        if (modifiedItem.getName() != null && !modifiedItem.getName().equals(item.getName())) {
            contentBuffer.append(String.format(UPDATE_ITERATION_NAME_CONTENT, item.getName(), modifiedItem.getName()));
            return contentBuffer.toString();
        }
        boolean isFirstContent = true;
        if (modifiedItem.getStartTime() != null && !modifiedItem.getStartTime().equals(item.getStartTime())) {
            contentBuffer.append(String.format(UPDATE_ITERATION_STARTTIME_CONTENT, dateFormat.format(item.getStartTime()),
                    dateFormat.format(modifiedItem.getStartTime())));
            isFirstContent = false;
        }
        if (modifiedItem.getEndTime() != null && !modifiedItem.getEndTime().equals(item.getEndTime())) {
            if (!isFirstContent) {
                contentBuffer.append("，");
            }
            contentBuffer.append(String.format(UPDATE_ITERATION_ENDTIME_CONTENT, dateFormat.format(item.getEndTime()),
                    dateFormat.format(modifiedItem.getEndTime())));
        }
        return contentBuffer.toString();

    }
}
