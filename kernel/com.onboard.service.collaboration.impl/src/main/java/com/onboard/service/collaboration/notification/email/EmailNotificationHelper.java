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
package com.onboard.service.collaboration.notification.email;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.onboard.domain.model.Attachment;
import com.onboard.service.email.TemplateEngineService;
import com.onboard.service.notification.email.EmailNotification;

/**
 * 生成{@link EmailNotification}內容的辅助类
 * 
 * @author XR
 * 
 */
@Service("emailNotificationHelper")
public class EmailNotificationHelper {

    private static final String VM_ATTACHMENT_LIST = "attachment-list.vm";

    public static final String VM_PATH = "templates/";

    @Autowired
    private TemplateEngineService templateEngineService;

    private String protocol = "https://";

    @Value("${site.host}")
    private String host;

    /**
     * 获取附件的邮件正文
     * 
     * @param attachments
     * @param companyId
     * @param projectId
     * @return
     */
    public String getAttachementListEmailContent(List<Attachment> attachments, int companyId, int projectId) {
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
