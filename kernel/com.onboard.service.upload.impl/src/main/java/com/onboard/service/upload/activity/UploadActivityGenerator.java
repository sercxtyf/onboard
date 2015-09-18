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
package com.onboard.service.upload.activity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.onboard.domain.model.Activity;
import com.onboard.domain.model.Upload;
import com.onboard.domain.model.type.BaseProjectItem;
import com.onboard.service.activity.ActivityActionType;
import com.onboard.service.activity.ActivityGenerator;
import com.onboard.service.upload.UploadService;

/**
 * 生成文件相关活动信息的辅助类
 * 
 * @author yewei
 * 
 */
@Service("uploadActivityGeneratorBean")
public class UploadActivityGenerator implements ActivityGenerator {

    @Autowired
    private UploadService uploadService;

    public static final String CREATE_SUBJECT = "上传了文件";
    public static final String DISCARD_SUBJECT = "删除了文件";
    public static final String RECOVER_SUBJECT = "从回收站还原了文件";
    public static final String MOVE_SUBJECT = "移动了文件";

    private Activity generateActivityByActionType(String actionType, String subject, Upload upload) {

        Activity activity = new Activity();

        activity.setAttachId(upload.getId());
        activity.setAttachType(upload.getType());
        activity.setSubject(subject);
        activity.setAction(actionType);
        activity.setTarget(upload.getContent());
        activity.setProjectId(upload.getProjectId());
        activity.setCompanyId(upload.getCompanyId());

        return activity;
    }

    @Override
    public Activity generateCreateActivity(BaseProjectItem item) {

        Upload upload = (Upload) item;

        Activity activity = this.generateActivityByActionType(ActivityActionType.CREATE, CREATE_SUBJECT, upload);

        return activity;
    }

    @Override
    public Activity generateUpdateActivity(BaseProjectItem item, BaseProjectItem modifiedItem) {
        Upload u1 = (Upload) item;
        Upload u2 = (Upload) modifiedItem;

        if (u2.getDeleted() != null && u1.getDeleted() != u2.getDeleted()) {
            if (u2.getDeleted()) {
                return this.generateActivityByActionType(ActivityActionType.DISCARD, DISCARD_SUBJECT, u1);
            } else {
                return this.generateActivityByActionType(ActivityActionType.RECOVER, RECOVER_SUBJECT, u1);
            }
        } else if (u2.getProjectId() != null && !u1.getProjectId().equals(u2.getProjectId())) {
            Activity activity = this.generateActivityByActionType(ActivityActionType.MOVE, MOVE_SUBJECT, u2);
            // TODO set up move information
            return activity;
        }
        return null;
    }

    @Override
    public String modelType() {
        return new Upload().getType();
    }

    @Override
    public String modelService() {
        return UploadService.class.getName();
    }

    @Override
    public BaseProjectItem enrichModel(BaseProjectItem identifiable) {
        return new Upload(uploadService.getById(identifiable.getId()));
    }

}
