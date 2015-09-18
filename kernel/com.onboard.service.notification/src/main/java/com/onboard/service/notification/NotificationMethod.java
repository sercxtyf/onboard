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

import com.onboard.domain.model.Activity;
import com.onboard.domain.model.type.IdentifiableOperator;
import com.onboard.domain.model.type.Subscribable;

/**
 * 代表一类提醒方法，如站内提醒或者邮件提醒
 * 
 * @author yewei
 * 
 */
public interface NotificationMethod extends IdentifiableOperator {

	/**
	 * 获取该通知方法的通知规则，用以判断什么时候应当发送通知
	 * @return 一个通知规则对象
	 */
    NotificationRule getNotificationRule();

    /**
     * 根据活动对象和可订阅对象发送通知
     * @param activity 活动对象
     * @param item 可订阅对象
     */
    void notifySubsribers(Activity activity, Subscribable item);

    /**
     * 根据活动对象和可订阅对象发送通知
     * @param activity 活动对象
     * @param original 更新前的可订阅对象
     * @param updated 更新后的可订阅对象
     */
    void notifySubsribers(Activity activity, Subscribable original, Subscribable updated);
}
