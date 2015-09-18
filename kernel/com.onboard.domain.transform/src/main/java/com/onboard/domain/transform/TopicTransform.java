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
package com.onboard.domain.transform;

import org.springframework.beans.BeanUtils;

import com.google.common.base.Function;
import com.onboard.domain.model.Topic;
import com.onboard.dto.TopicDTO;

public class TopicTransform {

    public static final Function<Topic, TopicDTO> TOPIC_DTO_FUNCTION = new Function<Topic, TopicDTO>() {
        @Override
        public TopicDTO apply(Topic topic) {
            return topicToTopicDTO(topic);
        }
    };

    public static TopicDTO topicToTopicDTO(Topic topic) {
        TopicDTO topicDTO = new TopicDTO();
        BeanUtils.copyProperties(topic, topicDTO);
        if (topic.getLastUpdator() != null) {
            topicDTO.setUpdator(UserTransform.userToUserDTO(topic.getLastUpdator()));
        }
        if (topic.getTargetCreator() != null) {
            topicDTO.setUpdator(UserTransform.userToUserDTO(topic.getLastUpdator()));
        }
        return topicDTO;
    }

}
