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
package com.onboard.web.api.topic;

import org.elevenframework.web.interceptor.Interceptors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.onboard.domain.model.Topic;
import com.onboard.service.collaboration.TopicService;
import com.onboard.service.security.exception.NoPermissionException;
import com.onboard.service.security.interceptors.ProjectMemberRequired;

@RequestMapping(value = "/{companyId}/projects/{projectId}")
@Controller
public class TopicAPIController {

    @Autowired
    private TopicService topicService;

    @RequestMapping(value = "/topics/{topicId}/stick", method = RequestMethod.PUT)
    @Interceptors({ ProjectMemberRequired.class })
    @ResponseBody
    public Topic stickTopic(@PathVariable int topicId, @PathVariable int projectId) {
        int stickCount = topicService.getTopicCount(projectId);
        if (stickCount > 10) {
            throw new NoPermissionException();
        }
        return topicService.stickTopic(topicId);

    }

    @RequestMapping(value = "/topics/{topicId}/unstick", method = RequestMethod.PUT)
    @Interceptors({ ProjectMemberRequired.class })
    @ResponseBody
    public Topic unstickTopic(@PathVariable int topicId) {

        return topicService.unstickTopic(topicId);

    }

    @RequestMapping(value = "/topics-stick-count", method = RequestMethod.GET)
    @Interceptors({ ProjectMemberRequired.class })
    @ResponseBody
    public Integer getStickTopicCount(@PathVariable int projectId) {

        return topicService.getTopicCount(projectId);

    }
}
