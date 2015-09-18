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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.onboard.domain.model.Activity;
import com.onboard.domain.model.Bug;
import com.onboard.domain.model.type.BaseProjectItem;
import com.onboard.service.activity.ActivityActionType;
import com.onboard.service.activity.ActivityGenerator;
import com.onboard.service.activity.util.ActivityHelper;
import com.onboard.service.collaboration.BugService;
import com.onboard.service.web.SessionService;

/**
 * 生成故事相关活动信息的辅助类
 * 
 * @author 邢亮
 * 
 */

@Service("bugActivityGeneratorBean")
public class BugActivityGenerator implements ActivityGenerator {

    public static final String CREATE_BUG_SUBJECT = "创建了Bug";
    public static final String RECOVER_BUG_SUBJECT = "重新打开了Bug";
    public static final String UPDATE_BUG_SUBJECT = "更新了Bug";
    public static final String DELETE_BUG_SUBJECT = "删除了Bug";
    public static final String COMPLETE_BUG_SUBJECT = "完成了Bug";

    @Autowired
    private BugService bugService;

    @Autowired
    private SessionService sessionService;

    @Override
    public String modelType() {
        return new Bug().getType();
    }

    @Override
    public String modelService() {
        return BugService.class.getName();
    }

    @Override
    public BaseProjectItem enrichModel(BaseProjectItem identifiable) {
        return new Bug(bugService.getById(identifiable.getId()));
    }

    private Activity generateActivityByActionType(String actionType, String subject, Bug bug) {
        Activity activity = ActivityHelper.generateActivityByActionType(actionType, subject, bug);
        activity.setProjectId(bug.getProjectId());
        activity.setCompanyId(bug.getCompanyId());
        activity.setTarget(bug.getTitle());
        return ActivityRecorderHelper.enrichActivity(activity);
    }

    @Override
    public Activity generateCreateActivity(BaseProjectItem item) {
        Bug bug = (Bug) item;
        Activity activity = generateActivityByActionType(ActivityActionType.CREATE, CREATE_BUG_SUBJECT, bug);
        return activity;
    }

    @Override
    public Activity generateUpdateActivity(BaseProjectItem item, BaseProjectItem modifiedItem) {
        Bug bug = (Bug) modifiedItem;
        Bug originalBug = (Bug) item;
        Activity activity = null;
        if (bug.getDeleted() == true && originalBug.getDeleted() == false) {
            activity = generateActivityByActionType(ActivityActionType.DISCARD, DELETE_BUG_SUBJECT, bug);
        } else if (bug.getDeleted() == false && originalBug.getDeleted() == true) {
            activity = generateActivityByActionType(ActivityActionType.RECOVER, RECOVER_BUG_SUBJECT, bug);
        } else if (bug.getCompleted() == true && originalBug.getCompleted() == false) {
            activity = generateActivityByActionType(ActivityActionType.COMPLETE, COMPLETE_BUG_SUBJECT, bug);
        } else {
            activity = generateActivityByActionType(ActivityActionType.UPDATE, UPDATE_BUG_SUBJECT, bug);
        }
        return activity;
    }

}
