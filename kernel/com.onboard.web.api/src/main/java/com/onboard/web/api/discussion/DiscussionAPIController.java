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
package com.onboard.web.api.discussion;

import java.util.List;
import java.util.Map;

import javax.naming.NoPermissionException;
import javax.validation.Valid;

import org.elevenframework.web.exception.BadRequestException;
import org.elevenframework.web.interceptor.Interceptors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.onboard.domain.model.Discussion;
import com.onboard.domain.model.Topic;
import com.onboard.domain.transform.DiscussionTransform;
import com.onboard.domain.transform.TopicTransform;
import com.onboard.dto.DiscussionDTO;
import com.onboard.dto.TopicDTO;
import com.onboard.service.collaboration.CommentService;
import com.onboard.service.collaboration.DiscussionService;
import com.onboard.service.collaboration.TopicService;
import com.onboard.service.security.interceptors.DiscussionCreatorRequired;
import com.onboard.service.security.interceptors.ProjectMemberRequired;
import com.onboard.service.security.interceptors.ProjectNotArchivedRequired;
import com.onboard.service.web.SessionService;
import com.onboard.web.api.form.DiscussionForm;

@RequestMapping(value = "/{companyId}/projects/{projectId}/discussions")
@Controller
public class DiscussionAPIController {

    private static final int TOPICS_PER_PAGE = 30;
    public static final Logger logger = LoggerFactory.getLogger(DiscussionAPIController.class);

    @Autowired
    private TopicService topicService;

    @Autowired
    private DiscussionService discussionService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private SessionService session;

    @Autowired
    private LocalValidatorFactoryBean validator;

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.setValidator(validator);
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    @Interceptors({ ProjectMemberRequired.class })
    @ResponseBody
    public Map<String, ?> showTopicList(@PathVariable("projectId") int projectId, @RequestParam(value = "page",
            required = false,
            defaultValue = "1") Integer page) {
        if (page <= 0) {
            throw new BadRequestException("parameter page shoud be positive integer.");
        }
        // get total pages of project's topics
        Topic sample = new Topic(false);
        sample.setProjectId(projectId);
        int total = topicService.countByExample(sample);
        Integer pages = (total - 1) / TOPICS_PER_PAGE + 1;
        // get topics of one page
        List<Topic> topics = topicService.getTopicListByProjectId(projectId, (page - 1) * TOPICS_PER_PAGE,
                TOPICS_PER_PAGE);
        List<TopicDTO> topicDtos = Lists.transform(topics, TopicTransform.TOPIC_DTO_FUNCTION);
        topicDtos = Lists.transform(topicDtos, TopicDTO_COUNTCOMMENT_FUNCTION);
        logger.info(String.format("%d topics found in project %d", topicDtos.size(), projectId));
        return ImmutableMap.of("topics", topicDtos, "page", page, "totalPage", pages);
    }

    private void addCommentCount(TopicDTO topic) {
        String attachType = topic.getRefType();
        int attachId = topic.getRefId();
        int count = commentService.getCountOfCommentsByTopic(attachType, attachId);
        if (attachType.equals(new Discussion().getType())) {
            count = count + 1;
        }
        topic.setNumOfComment(count);
    }

    private final Function<TopicDTO, TopicDTO> TopicDTO_COUNTCOMMENT_FUNCTION = new Function<TopicDTO, TopicDTO>() {
        @Override
        public TopicDTO apply(TopicDTO topic) {
            addCommentCount(topic);
            return topic;
        }
    };

    @RequestMapping(value = "/{discussionId}", method = RequestMethod.GET)
    @Interceptors({ ProjectMemberRequired.class })
    @ResponseBody
    public DiscussionDTO showDiscussion(@PathVariable("companyId") int companyId,
            @PathVariable("projectId") int projectId, @PathVariable("discussionId") int discussionId) {

        Discussion discussion = discussionService.getByIdWithDetail(discussionId);
        if (null == discussion || !discussion.getProjectId().equals(projectId)) {
            logger.error("Discussion " + discussionId + " doesn't exist in project " + projectId);
            return null;
        } else {
            DiscussionDTO discussionDto = DiscussionTransform.discussionToDiscussionDTOWithDetail(discussion);
            return discussionDto;
        }
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    @Interceptors({ ProjectMemberRequired.class, ProjectNotArchivedRequired.class })
    @ResponseBody
    public TopicDTO newDiscussion(@PathVariable("companyId") int companyId, @PathVariable("projectId") int projectId,
            @Valid @RequestBody DiscussionForm form) {
        logger.info("Creating new discussion...");
        form.setProjectId(projectId);
        form.setCompanyId(companyId);
        form.setCreatorId(session.getCurrentUser().getId());
        form.setCreatorName(session.getCurrentUser().getName());
        logger.info("" + form.getSubscribers().size());
        Discussion newDiscussion = discussionService.create(form);
        logger.info("Retrieving newly created topic...");
        Topic topic = topicService.getTopicByTypeAndId(newDiscussion.getType(), newDiscussion.getId());
        TopicDTO result = TopicTransform.topicToTopicDTO(topic);
        logger.info("add comment count...");
        addCommentCount(result);
        logger.info("Finishing...");
        return result;
    }

    @RequestMapping(value = "/{discussionId}", method = RequestMethod.DELETE)
    @Interceptors({ DiscussionCreatorRequired.class, ProjectNotArchivedRequired.class })
    @ResponseBody
    public void deleteDiscussion(@PathVariable("discussionId") int discussionId) {
        logger.info("deleting " + discussionId);
        Discussion discussion = new Discussion(discussionId);
        discussion.setDeleted(true);
        discussionService.updateSelective(discussion);
        // discussionService.discardDiscussion(discussionId);
    }

    @RequestMapping(value = "/{discussionId}", method = RequestMethod.PUT)
    @Interceptors({ ProjectMemberRequired.class, ProjectNotArchivedRequired.class })
    @ResponseBody
    public Object updateDiscussion(@PathVariable("companyId") int companyId, @PathVariable("projectId") int projectId,
            @PathVariable("discussionId") int discussionId, @Valid @RequestBody DiscussionForm form,
            @RequestParam(value = "recover", defaultValue = "false", required = true) Boolean recover)
            throws NoPermissionException {
        if (recover) {
            return recoverDiscussion(discussionId);
        }
        logger.info("Updating discussion " + discussionId);
        logger.info("attachments " + form.getAttachments().size());
        form.setProjectId(projectId);
        form.setCompanyId(companyId);
        discussionService.updateSelective(form);
        Topic topic = topicService.getTopicByTypeAndId(form.getType(), form.getId());
        TopicDTO result = TopicTransform.topicToTopicDTO(topic);
        logger.info("add comment count...");
        addCommentCount(result);
        logger.info("Finishing...");
        return result;
    }

    private Map<String, ?> recoverDiscussion(@PathVariable("discussionId") int discussionId)
            throws NoPermissionException {
        logger.info("recovering " + discussionId);
        Discussion discussion = discussionService.getById(discussionId);
        if (discussion.getId().equals(session.getCurrentUser().getId())) {
            throw new NoPermissionException();
        }
        discussion.setDeleted(false);
        discussionService.updateSelective(discussion);
        // discussionService.discardDiscussion(discussionId);
        discussion = discussionService.getByIdWithDetail(discussionId);
        DiscussionDTO discussionDto = DiscussionTransform.discussionToDiscussionDTOWithDetail(discussion);
        Topic topic = topicService.getTopicByTypeAndId(discussion.getType(), discussion.getId());
        TopicDTO topicDTO = TopicTransform.topicToTopicDTO(topic);
        addCommentCount(topicDTO);
        return ImmutableMap.of("discussion", discussionDto, "topic", topicDTO);

    }

    @RequestMapping(value = "/{discussionId}/topic", method = RequestMethod.GET)
    @Interceptors({ ProjectMemberRequired.class, ProjectNotArchivedRequired.class })
    @ResponseBody
    public TopicDTO getTopicByDiscussion(@PathVariable("companyId") int companyId,
            @PathVariable("projectId") int projectId, @PathVariable("discussionId") int discussionId) {
        Topic topic = topicService.getTopicByTypeAndId(new Discussion().getType(), discussionId);
        TopicDTO result = TopicTransform.topicToTopicDTO(topic);
        addCommentCount(result);
        return result;
    }

}
