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
package com.onboard.service.activity.util;

import org.jsoup.Jsoup;

import com.onboard.domain.model.Activity;
import com.onboard.domain.model.type.BaseProjectItem;
import com.onboard.service.activity.ActivityGenerator;

public class ActivityHelper {

    public static Activity generateActivityByActionType(String actionType,
            String subject, BaseProjectItem item) {
        Activity activity = new Activity();
        activity.setAttachId(item.getId());
        activity.setAttachType(item.getType());

        activity.setSubject(subject);
        activity.setAction(actionType);

        return activity;
    }

    /**
     * 截断Activity内容描述字段
     * 
     * @param str
     * @return
     */
    public static String cutoffActivityContent(String str) {
        if (str != null) {
            return str.substring(0, Math.min(ActivityGenerator.MAX_ACTIVITY_CONTENT_LENGTH, str.length()));
        }
        return null;
    }
 
    /**
     * 截断Activity标题描述字段
     * 
     * @param str
     * @return
     */
    public static String cutoffActivityTitle(String str) {
        if (str != null) {
            return str.substring(0, Math.min(ActivityGenerator.MAX_ACTIVITY_TITLE_LENGTH, str.length()));
        }
        return null;
    }
    
    public static String soup(String content){
        return content == null ? "" : Jsoup.parse(content).text();
    }

}
