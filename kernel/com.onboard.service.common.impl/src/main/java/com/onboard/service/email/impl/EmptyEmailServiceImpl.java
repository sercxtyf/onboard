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
package com.onboard.service.email.impl;

import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import com.onboard.service.email.InternalEmailService;

@Service("emptyEmailServiceBean")
public class EmptyEmailServiceImpl implements InternalEmailService {

    public static final Logger logger = LoggerFactory.getLogger(EmptyEmailServiceImpl.class);

    @Override
    public Future<Boolean> sendEmail(String to, String[] cc, String[] bcc, String subject, String content, String replyTo) {
        logger.debug("mock email service");
        return new AsyncResult<Boolean>(true);
    }

    @Override
    public Future<Boolean> sendEmail(String from, String to, String[] cc, String[] bcc, String subject, String content,
            String replyTo) {
        logger.debug("mock email service");
        return new AsyncResult<Boolean>(true);
    }
}
