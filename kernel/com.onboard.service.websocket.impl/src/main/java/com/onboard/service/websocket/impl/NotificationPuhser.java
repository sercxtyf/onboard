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
package com.onboard.service.websocket.impl;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.onboard.domain.model.Activity;
import com.onboard.domain.model.Notification;
import com.onboard.domain.model.User;
import com.onboard.domain.model.type.BaseProjectItem;
import com.onboard.domain.model.type.Subscribable;
import com.onboard.service.activity.ActivityHook;
import com.onboard.service.common.identifiable.IdentifiableManager;
import com.onboard.service.common.subscrible.SubscriberService;
import com.onboard.service.notification.NotificationService;
import com.onboard.service.web.SessionService;
import com.onboard.service.websocket.WebSocketService;

/**
 * @author Gourui
 * 
 */
@Service("notificationPuhserBean")
public class NotificationPuhser implements ActivityHook {

    public static final Logger logger = LoggerFactory.getLogger(NotificationPuhser.class);

    @Autowired
    private WebSocketService webSocketService;

    @Autowired
    private IdentifiableManager identifiableManager;

    @Autowired
    private SubscriberService subscriberService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private SessionService sessionService;

    private void broadcastByActivity(User owner, Activity activity, BaseProjectItem originalItem, BaseProjectItem updatedItem)
            throws IOException {
        /**
         * push web page notifications to all subscribers
         */
        activity.setAttachObject((BaseProjectItem)identifiableManager.getIdentifiableByTypeAndId(activity.getAttachType(),
                activity.getAttachId()));
        subscriberService.fillSubcribers((Subscribable) updatedItem);
        Notification notification = new Notification();
        notification.setCompanyId(activity.getCompanyId());
        notification.setActivityId(activity.getId());
        notification.setUserId(sessionService.getCurrentUser().getId());
        List<Notification> notifications = notificationService.getNotificationsBySample(notification, 0, -1);
        if (notifications != null && notifications.size() > 0) {
            notification = notifications.get(0);
        }
        if (notification.getId() != null) {
            notification = notificationService.getNotificationById(notification.getId());
            for (User user : ((Subscribable) updatedItem).getSubscribers()) {
                if (user.getId() != null && user.getId() != activity.getCreatorId()) {
                    webSocketService.broadcastOne(user.getEmail(), notification);
                }
            }
        }

    }

    @Override
    public void whenCreationActivityCreated(User owner, Activity activity, BaseProjectItem item) throws Throwable {
        broadcastByActivity(owner, activity, item, item);
    }

    @Override
    public void whenUpdateActivityCreated(User owner, Activity activity, BaseProjectItem item, BaseProjectItem updatedItem)
            throws Throwable {
        broadcastByActivity(owner, activity, item, updatedItem);
    }
}
