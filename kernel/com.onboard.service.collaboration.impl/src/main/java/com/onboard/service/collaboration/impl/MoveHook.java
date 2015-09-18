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
package com.onboard.service.collaboration.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.onboard.domain.mapper.ActivityMapper;
import com.onboard.domain.mapper.AttachmentMapper;
import com.onboard.domain.mapper.TopicMapper;
import com.onboard.domain.mapper.model.ActivityExample;
import com.onboard.domain.mapper.model.AttachmentExample;
import com.onboard.domain.mapper.model.TopicExample;
import com.onboard.domain.model.Activity;
import com.onboard.domain.model.Attachment;
import com.onboard.domain.model.Project;
import com.onboard.domain.model.Topic;
import com.onboard.domain.model.type.BaseProjectItem;
import com.onboard.service.collaboration.ProjectService;

/**
 * Move操作的钩子操作，用于AOP
 * 
 * @author yewei
 * 
 */
@Service("moveHook")
public class MoveHook {

    @Autowired
    private TopicMapper topicMapper;

    @Autowired
    private AttachmentMapper attachmentMapper;

    @Autowired
    private ActivityMapper activityMapper;

    @Autowired
    private ProjectService projectService;

    private void updateTopic(BaseProjectItem item, int projectId) {
        Topic topic = new Topic();
        topic.setProjectId(projectId);

        Topic example = new Topic();
        example.setRefId(item.getId());
        example.setRefType(item.getType());

        topicMapper.updateByExampleSelective(topic, new TopicExample(example));
    }

    private void updateAttachment(BaseProjectItem item, int projectId) {
        Attachment attachment = new Attachment();
        attachment.setProjectId(projectId);

        Attachment example = new Attachment();
        example.setAttachId(item.getId());
        example.setAttachType(item.getType());

        attachmentMapper.updateByExampleSelective(attachment,
                new AttachmentExample(example));
    }

    private void updateActivity(BaseProjectItem item, int projectId) {
        Activity activity = new Activity();
        activity.setProjectId(projectId);

        Activity example = new Activity();
        example.setAttachId(item.getId());
        example.setAttachType(item.getType());

        activityMapper.updateByExampleSelective(activity, new ActivityExample(
                example));
    }

    public void afterMove(BaseProjectItem item, int projectId) {

        Project project = projectService.getById(projectId);
        if (project != null) {
            this.updateTopic(item, projectId);
            this.updateAttachment(item, projectId);
            this.updateActivity(item, projectId);
        }
    }

}
