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
package com.onboard.web.api.upload;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.elevenframework.web.interceptor.Interceptors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.onboard.domain.model.Attachment;
import com.onboard.domain.model.Comment;
import com.onboard.domain.model.Project;
import com.onboard.domain.model.Upload;
import com.onboard.domain.model.User;
import com.onboard.domain.transform.AttachmentTransform;
import com.onboard.domain.transform.UploadTransForm;
import com.onboard.dto.AttachmentDTO;
import com.onboard.dto.UploadDTO;
import com.onboard.service.account.UserService;
import com.onboard.service.collaboration.AttachmentService;
import com.onboard.service.collaboration.CommentService;
import com.onboard.service.collaboration.ProjectService;
import com.onboard.service.security.interceptors.ProjectMemberRequired;
import com.onboard.service.security.interceptors.ProjectNotArchivedRequired;
import com.onboard.service.security.interceptors.UploadCreatorRequired;
import com.onboard.service.upload.UploadService;
import com.onboard.service.web.SessionService;
import com.onboard.web.api.form.UploadForm;

@RequestMapping("/{companyId}/projects/{projectId}/uploads")
@Controller
public class UploadApiController {
    public static final Logger logger = LoggerFactory.getLogger(UploadApiController.class);

    @Autowired
    private UploadService uploadService;

    @Autowired
    private UserService userService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private AttachmentService attachmentService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private SessionService session;

    @RequestMapping(value = "", method = RequestMethod.POST)
    @Interceptors({ ProjectMemberRequired.class, ProjectNotArchivedRequired.class })
    @ResponseBody
    public List<AttachmentDTO> createUpload(@PathVariable("companyId") int companyId,
            @PathVariable("projectId") int projectId, @RequestBody UploadForm form) {
        if (form.getAttachmentIds() == null || form.getAttachmentIds().size() == 0) {
            return null;
        }
        List<AttachmentDTO> attachmentDTOs = Lists.transform(
                createUpload(companyId, projectId, session.getCurrentUser(), form.getAttachmentIds(),
                        form.getSubscribers()), AttachmentTransform.ATTACHMENT_TO_ATTACHMENTDTO_FUNCTION);
        return attachmentDTOs;
    }

    private List<Attachment> createUpload(int companyId, int projectId, User creator, List<Integer> attachmentIds,
            List<User> subs) {
        List<Attachment> attachments = new ArrayList<Attachment>();
        for (Integer id : attachmentIds) {
            Attachment a = attachmentService.getById(id);
            Upload upload = new Upload(false);
            upload.setCompanyId(companyId);
            upload.setProjectId(projectId);
            upload.setContent(a.getName());
            upload.setCreatorId(creator.getId());
            upload.setCreatorName(creator.getName());
            upload.setSubscribers(subs);
            upload.setAttachments(new ArrayList<Attachment>(Arrays.asList(a)));
            attachments.add(uploadService.create(upload).getAttachments().get(0));
        }
        return attachments;
    }

    @RequestMapping(value = "/{uploadId}", method = RequestMethod.GET)
    @Interceptors({ ProjectMemberRequired.class, ProjectNotArchivedRequired.class })
    @ResponseBody
    public ImmutableMap<String, ?> getUpload(@PathVariable("companyId") int companyId,
            @PathVariable("projectId") int projectId, @PathVariable("uploadId") int uploadId) throws Exception {
        Upload upload = uploadService.getByIdWithDetail(uploadId);
        UploadDTO uploadDto = UploadTransForm.uploadToUploadDTO(upload);
        // List<Attachment> attachmentAttachUploads =
        // attachmentService.getAttachmentsByTypeAndId(type, id, start, limit)
        List<User> users = userService.getUserByProjectId(projectId);
        Project project = projectService.getById(projectId);
        List<Attachment> attachments = attachmentService.getAttachmentsByTypeAndId("upload", uploadId, 0, -1);
        List<AttachmentDTO> attachmentDTOs = Lists.transform(attachments,
                AttachmentTransform.ATTACHMENT_TO_ATTACHMENTDTO_FUNCTION);
        AttachmentDTO upLoadAttachmentDTO = attachmentDTOs.get(0);
        // addDepartmentInfoHelper.addDepartmentInfo(model, users, companyId);

        return ImmutableMap.of("uploadDTO", uploadDto, "usersInProject", users, "project", project,
                "uploadAttachmentDTO", upLoadAttachmentDTO);
    }

    @RequestMapping(value = "/{uploadId}/delete", method = RequestMethod.DELETE)
    @Interceptors({ ProjectMemberRequired.class, ProjectNotArchivedRequired.class })
    @ResponseBody
    public void discardUpload(@PathVariable("companyId") int companyId, @PathVariable("projectId") int projectId,
            @PathVariable("uploadId") int uploadId) {
        String type = new Upload().getType();
        Upload upload = new Upload(uploadId, true);
        attachmentService.discardAttachment(type, uploadId);
        List<Comment> comments = commentService.getCommentsByTopic(type, uploadId, 0, -1);
        for (Comment comment : comments) {
            commentService.delete(comment.getId());
        }
        uploadService.updateSelective(upload);
    }

    @RequestMapping(value = "/{uploadId}/recover", method = RequestMethod.POST)
    @Interceptors({ UploadCreatorRequired.class, ProjectNotArchivedRequired.class })
    @ResponseBody
    public void recoverUpload(@PathVariable("uploadId") int uploadId) {
        Upload upload = new Upload(uploadId, false);
        uploadService.updateSelective(upload);
        // uploadService.recoverUpload(uploadId);
    }
}
