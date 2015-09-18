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
package com.onboard.service.notification;

import java.util.List;

import com.onboard.domain.mapper.model.NotificationExample;
import com.onboard.domain.model.Notification;

/**
 * {@link Notification} Service Interface
 * 
 * @author SERC
 * 
 */
public interface NotificationService {
    /**
     * Get item by id
     * 
     * @param id
     * @return item
     */
    Notification getNotificationById(int id);

    /**
     * Get item list
     * 
     * @param start
     * @param limit
     * @return the item list
     */
    List<Notification> getNotifications(int start, int limit);

    /**
     * 通过companyId和userId获取通知
     * 
     * @param companyId
     * @param userId
     * @param start
     * @param limit
     * @return
     */
    List<Notification> getNotificationsByCompanyIdAndUserId(int companyId, int userId, int start, int limit,
            boolean isRead);

    /**
     * Get item list by example
     * 
     * @param item
     * @param start
     * @param limit
     * @return the item list
     */
    List<Notification> getNotificationsBySample(Notification item, int start, int limit);

    /**
     * 通过NotificationExample获取Notification，并填充activity
     * 
     * @param example
     * @return
     */
    public List<Notification> getNotificationsByExample(NotificationExample example);

    /**
     * Get item count by example
     * 
     * @param item
     * @return the count
     */
    int countByExample(Notification item);

    /**
     * Create
     * 
     * @param item
     * @return the created Notification
     */
    Notification createNotification(Notification item);

    /**
     * Update
     * 
     * @param item
     * @return the updated item
     */
    Notification updateNotification(Notification item);

    /**
     * Delete
     * 
     * @param id
     */
    void deleteNotification(int id);

    /**
     * 获取notification的数量
     * 
     * @param userId
     * @return
     */
    int countNotificationsByUserIdAndCompanyId(int userId, int companyId, boolean isRead);
}
