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
package com.onboard.web.api.notification;

import java.util.ArrayList;
import java.util.List;

import org.elevenframework.web.interceptor.Interceptors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.onboard.domain.model.Notification;
import com.onboard.service.notification.NotificationService;
import com.onboard.service.security.interceptors.CompanyMemberRequired;
import com.onboard.service.web.SessionService;

@RequestMapping(value = "/{companyId}")
@Controller
public class NotificationApiController {

    @Autowired
    NotificationService notificationService;

    @Autowired
    SessionService sessionService;

    @RequestMapping(value = "/notificatioins", method = RequestMethod.GET)
    @Interceptors({ CompanyMemberRequired.class })
    @ResponseBody
    public List<Notification> getNotifications(@PathVariable int companyId,
            @RequestParam(required = false) Boolean isRead, @RequestParam(required = true) Integer start,
            @RequestParam(required = true) Integer limit) {
        List<Notification> notifications = new ArrayList<Notification>();
        Notification notification = new Notification();
        notification.setCompanyId(companyId);
        notification.setUserId(sessionService.getCurrentUser().getId());
        notification.setIsRead(isRead);
        notifications = notificationService.getNotificationsBySample(notification, start, limit);
        return notifications;
    }

    @RequestMapping(value = "/notificatioins/{notificationId}", method = RequestMethod.GET)
    @Interceptors({ CompanyMemberRequired.class })
    @ResponseBody
    public Notification getNotificationById(@PathVariable int companyId, @PathVariable int notificationId) {
        return notificationService.getNotificationById(notificationId);
    }

    @RequestMapping(value = "/notificatioins/{notificationId}", method = RequestMethod.PUT)
    @Interceptors({ CompanyMemberRequired.class })
    @ResponseBody
    public Notification updateNotification(@PathVariable int companyId, @PathVariable int notificationId,
            @RequestBody Notification notification) {
        return notificationService.updateNotification(notification);
    }

    @RequestMapping(value = "/notificatioins/batchread", method = RequestMethod.PUT)
    @Interceptors({ CompanyMemberRequired.class })
    @ResponseStatus(HttpStatus.OK)
    public void batchReadNotification(@PathVariable int companyId) {
        List<Notification> notifications = notificationService.getNotificationsByCompanyIdAndUserId(companyId,
                sessionService.getCurrentUser().getId(), 0, -1, false);
        for (Notification notification : notifications) {
            notification.setIsRead(true);
            notificationService.updateNotification(notification);
        }
    }

    @RequestMapping(value = "/notificatioins/{notificationId}", method = RequestMethod.DELETE)
    @Interceptors({ CompanyMemberRequired.class })
    @ResponseStatus(HttpStatus.OK)
    public void deleteNotification(@PathVariable int companyId, @PathVariable int notificationId) {
        notificationService.deleteNotification(notificationId);
    }

}
