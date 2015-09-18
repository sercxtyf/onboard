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
package com.onboard.web.api.comment;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.elevenframework.web.exception.ResourceNotFoundException;
import org.elevenframework.web.interceptor.Interceptors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.onboard.domain.model.Attachment;
import com.onboard.domain.model.Comment;
import com.onboard.domain.model.User;
import com.onboard.domain.transform.CommentTransform;
import com.onboard.domain.transform.UserTransform;
import com.onboard.dto.CommentDTO;
import com.onboard.dto.UserDTO;
import com.onboard.service.collaboration.AttachmentService;
import com.onboard.service.collaboration.CommentService;
import com.onboard.service.common.attach.IdentifiableAttachManager;
import com.onboard.service.common.subscrible.SubscriberService;
import com.onboard.service.security.interceptors.CommentCreatorRequired;
import com.onboard.service.security.interceptors.ProjectMemberRequired;
import com.onboard.service.security.interceptors.ProjectNotArchivedRequired;
import com.onboard.service.web.SessionService;
import com.onboard.web.api.form.CommentForm;

@RequestMapping(value = "/{companyId}/projects/{projectId}/comments")
@Controller
public class CommentAPIController {

    public static final Logger logger = LoggerFactory.getLogger(CommentAPIController.class);

    @Autowired
    private CommentService commentService;

    @Autowired
    private SubscriberService subscriberService;

    @Autowired
    private IdentifiableAttachManager identifiableAttachManager;

    @Autowired
    private SessionService session;

    @Autowired
    private AttachmentService attachmentService;

    @RequestMapping(value = "", method = RequestMethod.POST)
    @Interceptors({ ProjectMemberRequired.class, ProjectNotArchivedRequired.class })
    @ResponseBody
    public Map<String, ?> createComment(@PathVariable("companyId") int companyId,
            @PathVariable("projectId") int projectId, @RequestBody CommentForm form) {
        form.setProjectId(projectId);
        form.setCompanyId(companyId);
        form.setCreatorId(session.getCurrentUser().getId());
        form.setCreatorName(session.getCurrentUser().getName());

        Comment comment = commentService.create(CommentTransform.commentDTOtoComment(form));
        CommentDTO newCommentDTO = CommentTransform.commentToCommentDTO(comment);

        List<User> existSubscribers = subscriberService.getSubscribeUsersByTopic(form.getAttachType(),
                form.getAttachId());
        List<UserDTO> existSubscriberDTOs = Lists.transform(existSubscribers, UserTransform.USER_TO_USERDTO_FUNCTION);
        form.setSubscriberDTOs(existSubscriberDTOs);
        return ImmutableMap.of("newComment", newCommentDTO, "existSubscribers", existSubscriberDTOs);
    }

    @SuppressWarnings("unchecked")
    @RequestMapping(value = "", method = RequestMethod.GET)
    @Interceptors({ ProjectMemberRequired.class })
    @ResponseBody
    public Map<String, ?> getTabView(@PathVariable("companyId") int companyId,
            @PathVariable("projectId") int projectId, @RequestParam(value = "attachType") String attachType,
            @RequestParam(value = "attachId") int attachId) {

        List<Comment> comments = (List<Comment>) identifiableAttachManager.getIdentifiablesByTypeAndAttachTypeAndId(
                new Comment().getType(), attachType, attachId);

        Collections.sort(comments, new Comparator<Comment>() {
            @Override
            public int compare(Comment o1, Comment o2) {
                return o1.getCreated().compareTo(o2.getCreated());
            }
        });

        List<Comment> commentsDetails = Lists.newArrayList();
        for (Comment comment : comments) {
            commentsDetails.add(commentService.getByIdWithDetail(comment.getId()));
        }
        List<CommentDTO> commentDTOs = Lists.transform(commentsDetails, CommentTransform.COMMENT_TO_DTO_FUNCTION);

        List<User> subscribers = subscriberService.getSubscribeUsersByTopic(attachType, attachId);
        List<UserDTO> subscriberDTOs = Lists.transform(subscribers, UserTransform.USER_TO_USERDTO_FUNCTION);
        return ImmutableMap.of("comments", commentDTOs, "subscribers", subscriberDTOs);
    }

    @RequestMapping(value = "/{commentId}", method = RequestMethod.DELETE)
    @Interceptors({ ProjectMemberRequired.class, ProjectNotArchivedRequired.class, CommentCreatorRequired.class })
    @ResponseBody
    public Map<String, ?> deleteComment(@PathVariable("commentId") int commentId) {
        Comment comment = commentService.getById(commentId);
        long capacity = 0;
        if (comment == null) {
            return ImmutableMap.of("capacity", 0, "status", new ResponseEntity<String>(HttpStatus.NOT_FOUND));
        }
        if (!session.getCurrentUser().getId().equals(comment.getCreatorId())) {
            return ImmutableMap.of("capacity", 0, "status", new ResponseEntity<String>(HttpStatus.UNAUTHORIZED));
        }
        List<Attachment> attachments = attachmentService.getAttachmentsByTypeAndIdWithNotDiscard(comment.getType(),
                comment.getId(), 0, -1);
        for (Attachment attachment : attachments) {
            capacity += attachment.getSize();
        }
        commentService.delete(commentId);

        return ImmutableMap.of("capacity", capacity, "status", new ResponseEntity<String>(HttpStatus.NO_CONTENT));
    }

    @RequestMapping(value = "/{commentId}", method = RequestMethod.GET)
    @Interceptors({ ProjectMemberRequired.class, ProjectNotArchivedRequired.class })
    @ResponseBody
    public CommentDTO getComment(@PathVariable("commentId") int commentId) {
        Comment comment = commentService.getById(commentId);
        if (comment == null) {
            throw new ResourceNotFoundException("comment not found");
        }
        return CommentTransform.commentToCommentDTO(comment);
    }

    @RequestMapping(value = "/{commentId}", method = RequestMethod.PUT)
    @Interceptors({ ProjectMemberRequired.class, ProjectNotArchivedRequired.class, CommentCreatorRequired.class })
    @ResponseBody
    public CommentDTO updateComment(@PathVariable("companyId") int companyId, @PathVariable("projectId") int projectId,
            @RequestBody CommentDTO form) {
        Comment comment = commentService.updateSelective(CommentTransform.commentDTOtoComment(form));
        return CommentTransform.commentToCommentDTO(comment);
    }

}
