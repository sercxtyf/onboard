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
package com.onboard.service.upload.notification;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.onboard.domain.model.Activity;
import com.onboard.domain.model.Attachment;
import com.onboard.domain.model.Upload;
import com.onboard.domain.model.type.Subscribable;
import com.onboard.service.collaboration.ProjectService;
import com.onboard.service.notification.NotificationRule;
import com.onboard.service.notification.email.AbstractEmailNotification;

@Service("createUploadEmailNotificationBean")
public class CreateUploadEmailNotification extends AbstractEmailNotification {
    private static final String VM_NAME = "upload-created.vm";

    private static final String VM_ATTACHMENT_LIST = "attachment-list.vm";

    @Autowired
    @Qualifier("createUploadNotificationRuleBean")
    private NotificationRule notificationRule;

    @Autowired
    private ProjectService projectService;

    @Override
    public String modelType() {
        return new Upload().getType();
    }

    @Override
    public Map<String, Object> getModel(Activity activity, Subscribable item, Map<String, Object> model) {
        Upload upload = (Upload) item;
        model.put("userName", this.getOwner(activity).getName());
        model.put("projectName", projectService.getById(upload.getProjectId()).getName());
        model.put("attachmentList",
                this.getAttachementListEmail(upload.getAttachments(), upload.getCompanyId(), upload.getProjectId()));
        return model;
    }

    @Override
    public String getTemplatePath() {
        return VM_PATH + VM_NAME;
    }

    @Override
    public NotificationRule getNotificationRule() {
        return notificationRule;
    }

    @Override
    protected String getEmailSubject(Activity activity, Subscribable item) {
        return String.format("%s在%s上上传了一个文件：%s", activity.getCreatorName(), activity.getProjectName(), this.getOwner(activity)
                .getName());
    }

    private String getAttachementListEmail(List<Attachment> attachments, int companyId, int projectId) {
        if (attachments == null) {
            return "";
        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("attachmentList", attachments);
        map.put("companyId", companyId);
        map.put("projectId", projectId);
        map.put("host", protocol + this.host);
        return templateEngineService.process(getClass(), VM_PATH + VM_ATTACHMENT_LIST, map);
    }

}
