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
package com.onboard.web.api.attachment;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.elevenframework.web.exception.BadRequestException;
import org.elevenframework.web.exception.ResourceNotFoundException;
import org.elevenframework.web.interceptor.Interceptors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.onboard.domain.model.Attachment;
import com.onboard.domain.model.Upload;
import com.onboard.domain.transform.AttachmentTransform;
import com.onboard.dto.AttachmentDTO;
import com.onboard.service.collaboration.AttachmentService;
import com.onboard.service.security.interceptors.AttachmentCreatorRequired;
import com.onboard.service.security.interceptors.ProjectMemberRequired;
import com.onboard.service.security.interceptors.ProjectNotArchivedRequired;
import com.onboard.service.upload.UploadService;

@RequestMapping(value = "/{companyId}/projects/{projectId}/attachments")
@Controller
public class AttachmentApiController {

    public static final Logger logger = LoggerFactory.getLogger(AttachmentApiController.class);

    private static final int ATTACHMENT_PER_PAGE = 30;

    @Autowired
    private AttachmentService attachmentService;

    @Autowired
    private UploadService uploadService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    @Interceptors({ ProjectMemberRequired.class })
    @ResponseBody
    public ImmutableMap<String, ?> getAttachments(@PathVariable("companyId") int companyId,
            @PathVariable("projectId") int projectId,
            @RequestParam(value = "start", required = false, defaultValue = "0") Integer start) {
        // long attachmentsAllSize = getAttachmentsSize(projectId);
        if (start < 0) {
            throw new BadRequestException("parameter page shoud be positive integer.");
        }
        List<Attachment> attachments = attachmentService.getAttachmentsByProjectId(projectId, start, ATTACHMENT_PER_PAGE);
        Attachment sample = new Attachment(false);
        sample.setProjectId(projectId);
        int total = attachmentService.countBySample(sample);
        int totalPage = (total - 1) / ATTACHMENT_PER_PAGE + 1;
        return ImmutableMap.of("attachmentDTOs",
                Lists.transform(attachments, AttachmentTransform.ATTACHMENT_TO_ATTACHMENTDTO_FUNCTION), "totalPage", totalPage,
                "total", total);
    }

    @RequestMapping(value = "/stage", method = RequestMethod.POST)
    @Interceptors({ ProjectMemberRequired.class, ProjectNotArchivedRequired.class })
    @ResponseBody
    public AttachmentDTO stageAttachment(@PathVariable("companyId") int companyId, @PathVariable("projectId") int projectId,
            @RequestParam(value = "file", required = true) MultipartFile multipart) throws IOException {
        AttachmentDTO attachmentDTO = AttachmentTransform
                .attachmentToAttachmentDTO(attachmentService.stageAttachment(companyId, projectId,
                        multipart.getOriginalFilename(), multipart.getSize(), multipart.getContentType(), multipart.getBytes()));
        return attachmentDTO;
    }

    @RequestMapping(value = "/stageUploadedImage", method = RequestMethod.POST)
    @Interceptors({ ProjectMemberRequired.class, ProjectNotArchivedRequired.class })
    @ResponseBody
    public AttachmentDTO stageUploadedImage(@PathVariable("companyId") int companyId, @PathVariable("projectId") int projectId,
            @RequestParam(value = "file", required = true) MultipartFile multipart) throws IOException {
        String filename = multipart.getOriginalFilename();
        logger.debug("file name is " + filename);
        if (filename == null || filename.isEmpty() || filename.equals("undefined")) {
            filename = System.currentTimeMillis() + "";
        }

        Attachment attachment = attachmentService.stageAttachment(companyId, projectId, filename, multipart.getSize(),
                multipart.getContentType(), multipart.getBytes());
        attachmentService.moveStageAttachment(attachment.getId(), projectId);

        AttachmentDTO attachmentDTO = AttachmentTransform.attachmentToAttachmentDTO(attachment);
        return attachmentDTO;
    }

    @RequestMapping(value = "/{attachmentId}", method = RequestMethod.GET)
    @Interceptors({ ProjectMemberRequired.class })
    @ResponseBody
    public AttachmentDTO getAttachment(@PathVariable("companyId") int companyId, @PathVariable("projectId") int projectId,
            @PathVariable("attachmentId") int attachmentId) {
        Attachment attachment = attachmentService.getById(attachmentId);
        if (attachment == null) {
            throw new ResourceNotFoundException();
        }

        return AttachmentTransform.attachmentToAttachmentDTO(attachment);
    }

    @RequestMapping(value = "/byte/{attachmentId}", method = RequestMethod.GET)
    @Interceptors({ ProjectMemberRequired.class })
    @ResponseBody
    public byte[] getAttachmentByte(@PathVariable("companyId") int companyId, @PathVariable("projectId") int projectId,
            @PathVariable("attachmentId") int attachmentId) {
        Attachment attachment = attachmentService.getById(attachmentId);
        if (attachment == null) {
            throw new ResourceNotFoundException();
        }

        return attachmentService.getAttachmentContentById(projectId, attachmentId);
    }

    @RequestMapping(value = "/{attachmentId}/download", method = RequestMethod.GET)
    @Interceptors({ ProjectMemberRequired.class })
    @ResponseBody
    public HttpEntity<byte[]> downloadAttachment(@PathVariable("companyId") int companyId,
            @PathVariable("projectId") int projectId, @PathVariable("attachmentId") int attachmentId)
            throws UnsupportedEncodingException {
        Attachment attachment = attachmentService.getById(attachmentId);

        if (attachment == null) {
            throw new ResourceNotFoundException();
        }

        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        String filename = new String(attachment.getName().getBytes("GB2312"), "ISO_8859_1");
        header.setContentDispositionFormData("attachment", filename);
        header.add("X-Accel-Redirect", String.format("/attachments/%d/%d", projectId, attachmentId));
        header.add("X-Accel-Charset", "utf-8");

        return new HttpEntity<byte[]>(null, header);
    }

    @RequestMapping(value = "/{attachmentId}", method = RequestMethod.DELETE)
    @Interceptors({ AttachmentCreatorRequired.class, ProjectNotArchivedRequired.class })
    @ResponseStatus(HttpStatus.OK)
    public void deleteAttachment(@PathVariable("companyId") int companyId, @PathVariable("projectId") int projectId,
            @PathVariable("attachmentId") int attachmentId) {
        Attachment attachment = attachmentService.getById(attachmentId);
        if (attachment.getAttachType().equals("upload")) {
            Upload upload = new Upload(attachment.getAttachId(), true);
            uploadService.updateSelective(upload);
        }
        attachmentService.delete(attachmentId);
    }

    private MediaType getContentType(String contentType) {
        if (contentType.equalsIgnoreCase(MediaType.IMAGE_GIF_VALUE)) {
            return MediaType.IMAGE_GIF;
        } else if (contentType.equalsIgnoreCase(MediaType.IMAGE_PNG_VALUE)) {
            return MediaType.IMAGE_PNG;
        } else if (contentType.equalsIgnoreCase(MediaType.IMAGE_JPEG_VALUE)) {
            return MediaType.IMAGE_JPEG;
        } else if (contentType.equalsIgnoreCase(MediaType.TEXT_PLAIN_VALUE)) {
            return MediaType.TEXT_PLAIN;
        } else if (contentType.equalsIgnoreCase(MediaType.TEXT_XML_VALUE)) {
            return MediaType.TEXT_XML;
        } else if (contentType.equalsIgnoreCase(MediaType.TEXT_HTML_VALUE)) {
            return MediaType.TEXT_HTML;
        }
        return MediaType.APPLICATION_OCTET_STREAM;
    }

    @RequestMapping(value = "/image/{attachmentId}", method = RequestMethod.GET)
    @Interceptors({ ProjectMemberRequired.class })
    @ResponseBody
    public HttpEntity<byte[]> renderImgInPage(@PathVariable("companyId") int companyId, @PathVariable("projectId") int projectId,
            @PathVariable("attachmentId") int attachmentId) {
        Attachment attachment = attachmentService.getById(attachmentId);
        byte[] bytes = attachmentService.getAttachmentContentById(projectId, attachmentId);
        if (attachment == null || bytes == null) {
            throw new ResourceNotFoundException();
        }
        HttpHeaders header = new HttpHeaders();
        header.setContentType(getContentType(attachment.getContentType()));
        return new HttpEntity<byte[]>(bytes, header);
    }

    @RequestMapping(value = "/text/{attachmentId}", method = RequestMethod.GET)
    @Interceptors({ ProjectMemberRequired.class })
    @ResponseBody
    public HttpEntity<byte[]> renderTxtInPage(@PathVariable("companyId") int companyId, @PathVariable("projectId") int projectId,
            @PathVariable("attachmentId") int attachmentId) {
        Attachment attachment = attachmentService.getById(attachmentId);
        byte[] bytes = attachmentService.getAttachmentContentById(projectId, attachmentId);
        if (attachment == null || bytes == null) {
            throw new ResourceNotFoundException();
        }
        HttpHeaders header = new HttpHeaders();
        header.setContentType(getContentType(attachment.getContentType()));
        return new HttpEntity<byte[]>(bytes, header);
    }

    @RequestMapping(value = "/pdf/{attachmentId}", method = RequestMethod.GET)
    @Interceptors({ ProjectMemberRequired.class })
    @ResponseBody
    public HttpEntity<byte[]> renderPDFInPage(@PathVariable("companyId") int companyId, @PathVariable("projectId") int projectId,
            @PathVariable("attachmentId") int attachmentId) throws UnsupportedEncodingException {
        Attachment attachment = attachmentService.getById(attachmentId);
        byte[] bytes = attachmentService.getAttachmentContentById(projectId, attachmentId);
        if (attachment == null || bytes == null) {
            throw new ResourceNotFoundException();
        }
        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.parseMediaType("application/pdf"));
        String filename = new String(attachment.getName().getBytes("GB2312"), "ISO_8859_1");
        header.setContentDispositionFormData("attachment", filename);
        header.add("X-Accel-Redirect", String.format("/attachments/%d/%d", projectId, attachmentId));
        header.add("X-Accel-Charset", "utf-8");
        return new HttpEntity<byte[]>(bytes, header);
    }
}
