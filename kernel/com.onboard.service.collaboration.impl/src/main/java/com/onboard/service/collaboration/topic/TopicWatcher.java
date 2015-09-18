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
package com.onboard.service.collaboration.topic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.onboard.domain.model.Activity;
import com.onboard.domain.model.Topic;
import com.onboard.domain.model.type.Commentable;
import com.onboard.domain.model.type.BaseProjectItem;
import com.onboard.service.activity.ActivityHook;
import com.onboard.service.activity.SynchronizedActivityHook;
import com.onboard.service.collaboration.TopicService;

/**
 * Topic关联的对象修改后，Topic的title可能需要发生变化。{@link TopicWatcher}注册为{@link ActivityHook}，当相关更新发生时，将根据需要根性Topic的title
 * 
 * @author yewei
 * 
 */
@Service("topicWatcherBean")
public class TopicWatcher implements SynchronizedActivityHook {
    
    @Autowired
    private TopicService topicService;

    @Override
    public void whenCreationActivityCreated(Activity activity, BaseProjectItem item) throws Throwable {
        // do nothing

    }

    @Override
    public void whenUpdateActivityCreated(Activity activity, BaseProjectItem item, BaseProjectItem updatedItem) throws Throwable {
        if (item instanceof Commentable) {
            Commentable c1 = (Commentable) item;
            Commentable c2 = (Commentable) updatedItem;
            if (c2.getCommentSubject() != null && !c2.getCommentSubject().equals(c1.getCommentSubject())) {
                Topic topic = topicService.getTopicByTypeAndId(c1.getType(), c1.getId());
                if (topic != null) {
                    Topic t = new Topic(topic.getId());
                    t.setTitle(c2.getCommentSubject());
                    topicService.updateTopic(t);
                }
            }
        }

    }

}
