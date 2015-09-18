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
import com.onboard.domain.model.Discussion;
import com.onboard.domain.model.type.BaseProjectItem;
import com.onboard.domain.model.utils.HtmlTextParser;
import com.onboard.service.activity.ActivityActionType;
import com.onboard.service.activity.ActivityGenerator;
import com.onboard.service.activity.util.ActivityHelper;
import com.onboard.service.collaboration.DiscussionService;

/**
 * 生成讨论相关活动信息的辅助类
 * 
 * @author yewei
 * 
 */
@Service("discussionActivityGeneratorBean")
public class DiscussionActivityGenerator implements ActivityGenerator {

    @Autowired
    private DiscussionService discussionService;

    public static final String CREATE_SUBJECT = "发起了讨论";
    public static final String DISCARD_SUBJECT = "删除了讨论";
    public static final String RECOVER_SUBJECT = "从回收站还原了讨论";
    public static final String MOVE_SUBJECT = "移动了讨论";
    public static final String UPDATE_SUBJECT = "更新了讨论";

    private Activity generateActivityByActionType(String actionType, String subject, Discussion discussion) {

        Activity activity = ActivityRecorderHelper.generateActivityByActionType(actionType, subject, discussion);

        activity.setTarget(discussion.getSubject());
        activity.setContent(ActivityHelper.cutoffActivityContent(HtmlTextParser.getPlainText(discussion.getContent())));

        activity.setProjectId(discussion.getProjectId());
        activity.setCompanyId(discussion.getCompanyId());

        return ActivityRecorderHelper.enrichActivity(activity);

    }

    private Activity generateUpdateActivity(Discussion discussion, Discussion modifiedDicussion) {
        if (!discussion.getProjectId().equals(modifiedDicussion.getProjectId())) {
            Activity activity = this.generateActivityByActionType(ActivityActionType.MOVE, MOVE_SUBJECT, discussion);

            ActivityRecorderHelper.setupMoveInformation(discussion.getProjectId(), modifiedDicussion.getProjectId(), activity);

            return activity;
        }

        Activity activity = this.generateActivityByActionType(ActivityActionType.UPDATE, UPDATE_SUBJECT, discussion);

        return activity;
    }

    @Override
    public String modelType() {
        return new Discussion().getType();
    }

    @Override
    public Activity generateCreateActivity(BaseProjectItem item) {

        Discussion discussion = (Discussion) item;

        return this.generateActivityByActionType(ActivityActionType.CREATE, CREATE_SUBJECT, discussion);
    }

    @Override
    public Activity generateUpdateActivity(BaseProjectItem item, BaseProjectItem modifiedItem) {
        Discussion d1 = (Discussion) item;
        Discussion d2 = (Discussion) modifiedItem;

        if (d2.getDeleted() != null && d1.getDeleted() != d2.getDeleted()) {
            if (d2.getDeleted()) {
                return this.generateActivityByActionType(ActivityActionType.DISCARD, DISCARD_SUBJECT, d1);
            } else {
                return this.generateActivityByActionType(ActivityActionType.RECOVER, RECOVER_SUBJECT, d1);
            }
        } else {
            return this.generateUpdateActivity(d1, d2);
        }
    }

    @Override
    public BaseProjectItem enrichModel(BaseProjectItem identifiable) {
        return new Discussion(discussionService.getById(identifiable.getId()));
    }

    @Override
    public String modelService() {
        return DiscussionService.class.getName();
    }

}
