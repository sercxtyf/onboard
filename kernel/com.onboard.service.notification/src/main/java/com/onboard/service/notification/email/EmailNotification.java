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

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;

import com.onboard.domain.model.Activity;
import com.onboard.domain.model.User;
import com.onboard.domain.model.type.Subscribable;
import com.onboard.service.account.UserService;
import com.onboard.service.common.subscrible.SubscriberService;
import com.onboard.service.email.EmailService;
import com.onboard.service.notification.SimpleNotificationMethod;
import com.onboard.utils.DataCipher;

/**
 * Email通知
 * 
 * @author yewei, XR
 * 
 */
public abstract class EmailNotification extends SimpleNotificationMethod {

    @Value("${mailgun.domain}")
    private String domain;

    @Autowired
    protected EmailService emailService;

    @Autowired
    protected SubscriberService subscriberService;

    @Autowired
    protected UserService userService;

    protected abstract String getEmailContent(Activity activity, Subscribable item);

    protected abstract String getEmailSubject(Activity activity, Subscribable item);

    protected abstract String subjectInSpecificProject(Activity activity, Subscribable item, String subject);

    protected User getOwner(Activity activity) {
        return userService.getById(activity.getCreatorId());
    }

    protected Subscribable enrichSubscribable(Subscribable item) {
        return item;
    }

    /**
     * 获取发送通知的对象
     * 
     * @param item
     * @return
     */
    protected List<User> getSubsribers(Subscribable item) {
        return item.getSubscribers();
    }

    @Override
    public void notifySubsribers(Activity activity, Subscribable item) {

        User currentUser = getOwner(activity);

        // 若未填充Subscribers，填充之
        if (item.getSubscribers() == null) {
            subscriberService.fillSubcribers(item);
        }

        // 获取content和subject
        String content = getEmailContent(activity, item);
        String subject = getEmailSubject(activity, item);

        // 为每个subscriber发送邮件
        for (User subscriber : getSubsribers(item)) {
            if (subscriber != null && !subscriber.getId().equals(currentUser.getId())) {
                if (subscriber.getEmail() == null) {
                    subscriber = userService.getById(subscriber.getId());
                }
                String replyTo = StringUtils.arrayToDelimitedString(
                        new String[] { item.getSubscribableType(), String.valueOf(item.getSubscribableId()),
                                DataCipher.encode(subscriber.getEmail()) }, "-");

                emailService.sendEmail(currentUser.getName(), subscriber.getEmail(), null, null, subject, content,
                        String.format("%s@%s", replyTo, domain));
            }
        }
    }
}
