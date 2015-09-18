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
package com.onboard.web.api.form;

import java.util.Date;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import com.onboard.domain.model.Discussion;

public class DiscussionForm extends Discussion {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Length(min = 1, max = 50, message = "标题长度必须在1-50之间")
    @NotNull
    @NotBlank
    private String subject;

    @Override
    public String getSubject() {
        return subject;
    }

    @Override
    public void setSubject(String subject) {
        this.subject = subject;
    }

    public DiscussionForm() {
        super();
    }

    public DiscussionForm(Discussion discussion) {
        super(discussion);
        setSubject(discussion.getSubject());
        setComments(discussion.getComments());
        setAttachments(discussion.getAttachments());
        setSubscribers(discussion.getSubscribers());
        setCreated(new Date());
    }
}
