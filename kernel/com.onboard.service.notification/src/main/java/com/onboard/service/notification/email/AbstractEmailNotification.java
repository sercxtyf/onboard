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
package com.onboard.service.notification.email;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.onboard.domain.model.Activity;
import com.onboard.domain.model.type.Subscribable;
import com.onboard.service.common.identifiable.IdentifiableManager;
import com.onboard.service.email.TemplateEngineService;

/**
 * 利用VelocityEngine实现的{@link EmailNotification} 子类实现抽象方法用以填充邮件内容
 * 
 * @author XR， yewei
 * 
 */
public abstract class AbstractEmailNotification extends EmailNotification {

    public static final String VM_PATH = "templates/";

    @Autowired
    protected TemplateEngineService templateEngineService;

    @Autowired
    private IdentifiableManager identifiableManager;

    protected String protocol = "https://";

    @Value("${site.host}")
    protected String host;

    @Override
    public String getEmailContent(Activity activity, Subscribable item) {
        return templateEngineService.process(getClass(), getTemplatePath(), getModel(activity, item));
    }

    @Override
    public String subjectInSpecificProject(Activity activity, Subscribable item, String subject) {
        String projectName = activity.getProjectName();
        if (projectName == null) {
            return subject;
        } else {
            return String.format("[%s]%s", projectName, subject);
        }
    }

    /**
     * 将基本信息填入model内,
     * 
     * @param activity
     * @param item
     * @return
     */
    private Map<String, Object> getModel(Activity activity, Subscribable item) {
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("host", protocol + this.host);
//        model.put("url", identifiableManager.getIdentifiableURL(item));
        model.put("subscribers", this.getSubsribers(item));
        return this.getModel(activity, item, model);
    }

    /**
     * 根据activity和Subscribable信息填充model并返回
     * 
     * @param activity
     * @param item
     * @param model
     * @return
     */
    public abstract Map<String, Object> getModel(Activity activity, Subscribable item, Map<String, Object> model);

    public abstract String getTemplatePath();

}
