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
package com.onboard.service.activity.impl.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.onboard.domain.model.Activity;
import com.onboard.domain.model.User;
import com.onboard.domain.model.type.BaseProjectItem;
import com.onboard.service.activity.ActivityHook;
import com.onboard.service.activity.SynchronizedActivityHook;

/**
 * 调用ActivityHook的辅助类
 * 
 * @author yewei
 * 
 */
@Component("activityHookHelper")
public class ActivityHookHelper {

    private static final Logger logger = LoggerFactory.getLogger(ActivityHookHelper.class);

    public void callOneSynchronizedActivityHook(SynchronizedActivityHook synchronizedActivityHook, Activity activity,
            BaseProjectItem item, BaseProjectItem updatedItem) {

        try {
            if (updatedItem == null) {
                synchronizedActivityHook.whenCreationActivityCreated(activity, item);
            } else {
                synchronizedActivityHook.whenUpdateActivityCreated(activity, item, updatedItem);
            }
        } catch (Throwable e) {
            // do not throw out exceptions from synchronized activity hook
            logger.error("Fail to call sychronized activity hook: ", e);
        }
    }

    @Async
    public void callOneAsynchronizedActivityHook(ActivityHook activityHook, User owner, Activity activity, BaseProjectItem item,
            BaseProjectItem updatedItem) {
        try {
            if (updatedItem == null) {
                activityHook.whenCreationActivityCreated(owner, activity, item);
            } else {
                activityHook.whenUpdateActivityCreated(owner, activity, item, updatedItem);
            }
        } catch (Throwable e) {
            // do not throw out exceptions from activity hook
            logger.error("Fail to call activity hook: ", e);
        }
    }
}
