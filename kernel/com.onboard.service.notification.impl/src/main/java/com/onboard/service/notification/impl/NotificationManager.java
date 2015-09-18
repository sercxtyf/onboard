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
package com.onboard.service.notification.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.onboard.domain.model.Activity;
import com.onboard.domain.model.User;
import com.onboard.domain.model.type.BaseProjectItem;
import com.onboard.domain.model.type.Subscribable;
import com.onboard.service.activity.ActivityHook;
import com.onboard.service.notification.NotificationMethod;

/**
 * 
 * 管理{@link NotificationMethod},将相关提醒发送给订阅者，实现{@link ActivityHook}接口
 * 
 * @author yewei
 * 
 */
@Service("notificationManagerBean")
public class NotificationManager implements ActivityHook {

    public static final Logger logger = LoggerFactory.getLogger(NotificationManager.class);

    private static final Map<String, ArrayList<NotificationMethod>> notificationMethods = Collections
            .synchronizedMap(new HashMap<String, ArrayList<NotificationMethod>>());

    public synchronized void addNotificationMethod(NotificationMethod method) {
        if (method != null) {
            ArrayList<NotificationMethod> methods = notificationMethods.get(method.modelType());
            if (methods == null) {
                methods = new ArrayList<NotificationMethod>();
                notificationMethods.put(method.modelType(), methods);
            }
            methods.add(method);
        }
    }

    public synchronized void removeNotificationMethod(NotificationMethod method) {
        if (method != null) {
            ArrayList<NotificationMethod> methods = notificationMethods.get(method.modelType());
            if (methods != null) {
                methods.remove(method);
            }
        }
    }

    /**
     * 判断是否需要通过NotificationMethod发送通知，若需要，则发送
     * 
     * @param method
     * @param activity
     * @param item
     * @param updatedItem
     */
    private void notify(NotificationMethod method, Activity activity, BaseProjectItem item, BaseProjectItem updatedItem) {
        if (updatedItem == null) {
            if (method.getNotificationRule().ifNotify(activity, (Subscribable) item)) {
                method.notifySubsribers(activity, (Subscribable) item);
            }
        } else {
            if (method.getNotificationRule().ifNotify(activity, (Subscribable) item, (Subscribable) updatedItem)) {
                method.notifySubsribers(activity, (Subscribable) item, (Subscribable) updatedItem);
            }
        }
    }

    /**
     * 找出满足ModelType条件的NotificationMethod，发送通知
     * 
     * @param activity
     * @param item
     * @param updatedItem
     */
    private void doNotifySubsribers(User owner, Activity activity, BaseProjectItem item, BaseProjectItem updatedItem) {

        ArrayList<NotificationMethod> methods = notificationMethods.get(item.getType());
        if (methods != null) {
            for (NotificationMethod method : methods) {
                this.notify(method, activity, item, updatedItem);
            }
        }

        methods = notificationMethods.get(NotificationMethod.NONE_TYPE);
        if (methods != null) {
            if (notificationMethods.keySet().contains(item.getType())) {
                for (NotificationMethod method : methods) {
                    this.notify(method, activity, item, updatedItem);
                }
            }
        }
    }

    @Override
    public void whenCreationActivityCreated(User owner, Activity activity, BaseProjectItem item) throws Throwable {
        this.doNotifySubsribers(owner, activity, item, null);
    }

    @Override
    public void whenUpdateActivityCreated(User owner, Activity activity, BaseProjectItem item, BaseProjectItem updatedItem) throws Throwable {
        this.doNotifySubsribers(owner, activity, item, updatedItem);
    }
}
