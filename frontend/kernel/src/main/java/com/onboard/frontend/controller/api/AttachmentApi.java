package com.onboard.frontend.controller.api;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.onboard.frontend.exception.InternalException;
import com.onboard.frontend.exception.ResourceNotFoundException;
import com.onboard.frontend.model.dto.AttachmentDTO;
import com.onboard.frontend.service.net.NetService;

@RestController
public class AttachmentApi {

    private final static String ATTACHMENT_HEADER = "/%d/projects/%d/attachments";

    private final static String ATTACHMENT_ID = ATTACHMENT_HEADER + "/%d";

    private final static String ATTACHMENT_DOWNLOAD = ATTACHMENT_HEADER + "/%d/download";

    private final static String ATTACHMENT_IMAGE = ATTACHMENT_HEADER + "/image/%d";

    private final static String ATTACHMENT_PDF = ATTACHMENT_HEADER + "/pdf/%d";

    private final static String ATTACHMENT_TXT = ATTACHMENT_HEADER + "/text/%d";

    private final static String ATTACHMENT_BYTE = ATTACHMENT_HEADER + "/byte/%d";

    @Autowired
    private NetService netService;

    @RequestMapping(value = "/api/{companyId}/projects/{projectId}/attachments/{attachmentId}/download", method = RequestMethod.GET)
    @ResponseBody
    public HttpEntity<byte[]> downloadAttachment(@PathVariable("companyId") int companyId,
            @PathVariable("projectId") int projectId, @PathVariable("attachmentId") int attachmentId, HttpServletRequest request)
            throws IOException {
        String byteUri = String.format(ATTACHMENT_BYTE, companyId, projectId, attachmentId);
        String attachmentUri = String.format(ATTACHMENT_ID, companyId, projectId, attachmentId);
        byte[] bytes = netService.getForObject(byteUri, byte[].class);
        AttachmentDTO attachment = netService.getForObject(attachmentUri, AttachmentDTO.class);
        if (attachment == null || bytes == null) {
            throw new com.onboard.frontend.exception.ResourceNotFoundException();
        }
        HttpHeaders header = new HttpHeaders();
        String filename = new String(attachment.getName().getBytes("GB2312"), "ISO_8859_1");
        header.setContentDispositionFormData("attachment", filename);
        return new HttpEntity<byte[]>(bytes, header);
    }

    @RequestMapping(value = "/api/{companyId}/projects/{projectId}/attachments/text/{attachmentId}", method = RequestMethod.GET)
    @ResponseBody
    public HttpEntity<byte[]> renderTxtInPage(@PathVariable("companyId") int companyId, @PathVariable("projectId") int projectId,
            @PathVariable("attachmentId") int attachmentId) throws InternalException {
        String byteUri = String.format(ATTACHMENT_BYTE, companyId, projectId, attachmentId);
        String attachmentUri = String.format(ATTACHMENT_ID, companyId, projectId, attachmentId);
        byte[] bytes = netService.getForObject(byteUri, byte[].class);
        AttachmentDTO attachment = netService.getForObject(attachmentUri, AttachmentDTO.class);
        if (attachment == null || bytes == null) {
            throw new ResourceNotFoundException();
        }
        HttpHeaders header = new HttpHeaders();
        header.setContentType(getContentType(attachment.getContentType()));
        return new HttpEntity<byte[]>(bytes, header);
    }

    @RequestMapping(value = "/api/{companyId}/projects/{projectId}/attachments/image/{attachmentId}", method = RequestMethod.GET)
    @ResponseBody
    public HttpEntity<byte[]> renderImgInPage(@PathVariable("companyId") int companyId, @PathVariable("projectId") int projectId,
            @PathVariable("attachmentId") int attachmentId) throws InternalException {
        String byteUri = String.format(ATTACHMENT_IMAGE, companyId, projectId, attachmentId);
        String attachmentUri = String.format(ATTACHMENT_ID, companyId, projectId, attachmentId);
        byte[] bytes = netService.getForObject(byteUri, byte[].class);
        AttachmentDTO attachment = netService.getForObject(attachmentUri, AttachmentDTO.class);
        if (attachment == null || bytes == null) {
            throw new ResourceNotFoundException();
        }
        HttpHeaders header = new HttpHeaders();
        header.setContentType(getContentType(attachment.getContentType()));
        return new HttpEntity<byte[]>(bytes, header);
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

    @RequestMapping(value = "/api/{companyId}/projects/{projectId}/attachments/pdf/{attachmentId}", method = RequestMethod.GET)
    @ResponseBody
    public HttpEntity<byte[]> renderPDFInPage(@PathVariable("companyId") int companyId, @PathVariable("projectId") int projectId,
            @PathVariable("attachmentId") int attachmentId) throws InternalException {
        String byteUri = String.format(ATTACHMENT_BYTE, companyId, projectId, attachmentId);
        String attachmentUri = String.format(ATTACHMENT_ID, companyId, projectId, attachmentId);
        byte[] bytes = netService.getForObject(byteUri, byte[].class);
        AttachmentDTO attachment = netService.getForObject(attachmentUri, AttachmentDTO.class);
        if (attachment == null || bytes == null) {
            throw new ResourceNotFoundException();
        }
        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.parseMediaType("application/pdf"));
        return new HttpEntity<byte[]>(bytes, header);
    }

    @RequestMapping(value = "/api/{companyId}/projects/{projectId}/attachments/{attachmentId}", method = RequestMethod.GET)
    @ResponseBody
    public AttachmentDTO getAttachment(@PathVariable("companyId") int companyId, @PathVariable("projectId") int projectId,
            @PathVariable("attachmentId") int attachmentId) {
        String attachmentUri = String.format(ATTACHMENT_ID, companyId, projectId, attachmentId);
        AttachmentDTO attachmentDTO = netService.getForObject(attachmentUri, AttachmentDTO.class);

        return attachmentDTO;
    }

}
