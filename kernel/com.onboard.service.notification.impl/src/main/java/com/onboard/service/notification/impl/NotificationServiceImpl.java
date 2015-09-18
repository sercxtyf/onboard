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

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.onboard.domain.mapper.NotificationMapper;
import com.onboard.domain.mapper.model.NotificationExample;
import com.onboard.domain.model.Activity;
import com.onboard.domain.model.Notification;
import com.onboard.domain.model.Project;
import com.onboard.service.activity.ActivityService;
import com.onboard.service.collaboration.ProjectService;
import com.onboard.service.notification.NotificationService;

/**
 * {@link com.onboard.service.notification.NotificationService} Service
 * implementation
 * 
 * @generated_by_elevenframework
 */
@Transactional
@Service("notificationServiceBean")
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationMapper notificationMapper;

    @Autowired
    private ActivityService activityService;

    @Autowired
    private ProjectService projectService;

    @Override
    public Notification getNotificationById(int id) {
        return fillNotification(notificationMapper.selectByPrimaryKey(id));
    }

    private Notification fillNotification(Notification notification) {
        if (notification == null) {
            return null;
        }
        Activity activity = activityService.getById(notification.getActivityId());
        notification.setActivity(activity);
        Project project = projectService.getById(notification.getActivity().getProjectId());
        notification.getActivity().setProjectColorId(project.getColorId());
        return notification;
    }

    @Override
    public List<Notification> getNotifications(int start, int limit) {
        NotificationExample example = new NotificationExample(new Notification());
        example.setLimit(start, limit);
        List<Notification> notifications = notificationMapper.selectByExample(example);
        for (Notification notification : notifications) {
            fillNotification(notification);
        }
        return notifications;
    }

    @Override
    public List<Notification> getNotificationsBySample(Notification item, int start, int limit) {
        NotificationExample example = new NotificationExample(item);
        example.setLimit(start, limit);
        List<Notification> notifications = notificationMapper.selectByExample(example);
        for (Notification notification : notifications) {
            fillNotification(notification);
        }
        Collections.reverse(notifications);
        return notifications;
    }

    @Override
    public List<Notification> getNotificationsByExample(NotificationExample example) {
        List<Notification> notifications = notificationMapper.selectByExample(example);
        for (Notification notification : notifications) {
            fillNotification(notification);
        }
        return notifications;
    }

    @Override
    public int countByExample(Notification item) {
        NotificationExample example = new NotificationExample(item);
        return notificationMapper.countByExample(example);
    }

    @Override
    public Notification createNotification(Notification item) {
        notificationMapper.insert(item);
        return item;
    }

    @Override
    public Notification updateNotification(Notification item) {
        notificationMapper.updateByPrimaryKeySelective(item);
        return item;
    }

    @Override
    public void deleteNotification(int id) {
        notificationMapper.deleteByPrimaryKey(id);
    }

    @Override
    public int countNotificationsByUserIdAndCompanyId(int userId, int companyId, boolean isRead) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setIsRead(isRead);
        notification.setCompanyId(companyId);
        NotificationExample example = new NotificationExample(notification);
        return notificationMapper.countByExample(example);
    }

    @Override
    public List<Notification> getNotificationsByCompanyIdAndUserId(int companyId, int userId, int start, int limit,
            boolean isRead) {
        Notification sample = new Notification();
        sample.setCompanyId(companyId);
        sample.setUserId(userId);
        sample.setIsRead(isRead);
        NotificationExample example = new NotificationExample(sample);
        example.setLimit(start, limit);
        example.setOrderByClause("id desc");
        List<Notification> notifications = notificationMapper.selectByExample(example);
        for (Notification notification : notifications) {
            fillNotification(notification);
        }
        return notifications;
    }

}
