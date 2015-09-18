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
package com.onboard.service.collaboration.notification.email;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.onboard.domain.model.Activity;
import com.onboard.domain.model.Comment;
import com.onboard.domain.model.Project;
import com.onboard.domain.model.type.Subscribable;
import com.onboard.service.collaboration.ProjectService;
import com.onboard.service.common.identifiable.IdentifiableManager;
import com.onboard.service.common.subscrible.SubscriberService;
import com.onboard.service.notification.NotificationMethod;
import com.onboard.service.notification.NotificationRule;
import com.onboard.service.notification.email.AbstractEmailNotification;

/**
 * 针对{@link Comment}实现的{@link NotificationMethod}
 * 
 * @author XR, yewei
 * 
 */
@Service("commentEmailNotificationBean")
public class CommentEmailNotification extends AbstractEmailNotification {

    private static final String VM_NAME = "comment-created.vm";

    @Autowired
    @Qualifier("commentNotificationRuleBean")
    private NotificationRule notificationRule;

    @Autowired
    private EmailNotificationHelper emailNotificationHelper;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private IdentifiableManager identifiableManager;

    @Autowired
    protected SubscriberService subscriberService;

    @Override
    public String modelType() {
        return new Comment().getType();
    }

    @Override
    protected Subscribable enrichSubscribable(Subscribable item) {
        subscriberService.fillSubcribers(item);
        Comment comment = ((Comment) item);
        Subscribable attachment = (Subscribable) identifiableManager.getIdentifiableByTypeAndId(comment.getAttachType(),
                comment.getAttachId());
        comment.setAttach(attachment);
        return comment;
    }

    @Override
    public Map<String, Object> getModel(Activity activity, Subscribable item, Map<String, Object> model) {
        ;
        Comment comment = (Comment) enrichSubscribable(item);
        Project project = this.projectService.getById(item.getProjectId());
        model.put("userName", this.getOwner(activity).getName());
        model.put("projectName", project.getName());
        model.put("subject", item.getSubscribableSubject());
        model.put("content", comment.getContent());
        model.put(
                "attachmentList",
                emailNotificationHelper.getAttachementListEmailContent(comment.getAttachments(), comment.getCompanyId(),
                        comment.getProjectId()));

        return model;
    }

    @Override
    public NotificationRule getNotificationRule() {
        return this.notificationRule;
    }

    @Override
    protected String getEmailSubject(Activity activity, Subscribable item) {
        return subjectInSpecificProject(activity, item, item.getSubscribableSubject());
    }

    @Override
    public String getTemplatePath() {
        return VM_PATH + VM_NAME;
    }

}
