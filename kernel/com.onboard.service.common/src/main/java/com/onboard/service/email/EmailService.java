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
package com.onboard.service.email;

import java.util.concurrent.Future;


public interface EmailService {

    /**
     * 简单的邮件发送服务
     * @author Gou Rui 
     * @param to 收件人邮件地址
     * @param cc 抄送邮件地址
     * @param bcc 密送邮件地址
     * @param subject 主题
     * @param 邮件正文，由于设置的contentType是text/html，所以content可以是html格式的
     */
    Future<Boolean> sendEmail(String to, String[] cc, String[] bcc, String subject,
                              String content, String replyTo);

    /**
     * 简单的邮件发送服务
     * @author Gou Rui 
     * @param to 收件人邮件地址
     * @param cc 抄送邮件地址
     * @param bcc 密送邮件地址
     * @param subject 主题
     * @param 邮件正文，由于设置的contentType是text/html，所以content可以是html格式的
     */
    Future<Boolean> sendEmail(String from, String to, String[] cc, String[] bcc, String subject,
                              String content, String replyTo);
}
