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
package com.onboard.web.api.tag;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.elevenframework.web.exception.ResourceNotFoundException;
import org.elevenframework.web.interceptor.Interceptors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.ImmutableMap;
import com.onboard.domain.model.Attachment;
import com.onboard.domain.model.Tag;
import com.onboard.domain.model.TagAndTagAttach;
import com.onboard.domain.model.TagAttach;
import com.onboard.domain.model.type.Taggable;
import com.onboard.domain.transform.TagTransform;
import com.onboard.dto.TagDTO;
import com.onboard.service.collaboration.TagService;
import com.onboard.service.security.interceptors.AttachmentCreatorRequired;
import com.onboard.service.security.interceptors.ProjectMemberRequired;
import com.onboard.service.security.interceptors.ProjectNotArchivedRequired;

@RequestMapping("/{companyId}/projects/{projectId}/tags")
@Controller
public class TagApiController {

    private static final int ATTACHMENT_PER_PAGE = 20;

    @Autowired
    private TagService tagService;

    @RequestMapping(value = "", method = RequestMethod.POST)
    @Interceptors({ ProjectMemberRequired.class })
    @ResponseBody
    public TagDTO createTag(@PathVariable("projectId") int projectId, @PathVariable("companyId") int companyId,
            HttpServletResponse response, @RequestBody TagAndTagAttach tagAndTagAttachDTO) throws IOException {
        tagAndTagAttachDTO.getTag().setProjectId(projectId);
        Tag tag = tagService.getOrCreateTag(tagAndTagAttachDTO.getTag());
        if (!tagService.addTagWithAttacTypeAndId(tag, tagAndTagAttachDTO.getTagAttach().getAttachType(),
                tagAndTagAttachDTO.getTagAttach().getAttachId())) {
            response.sendError(HttpServletResponse.SC_CONFLICT);
            return null;
        }
        TagDTO tagDto = TagTransform.tagToTagDTO(tag);
        return tagDto;
    }

    @RequestMapping(value = "/{tagId}/{attachType}/{attachId}", method = RequestMethod.DELETE)
    @Interceptors({ AttachmentCreatorRequired.class, ProjectNotArchivedRequired.class })
    @ResponseBody
    public String deleteTag(@PathVariable("tagId") int tagId, @PathVariable("attachType") String attachType,
            @PathVariable("attachId") int attachId) {
        TagAttach tagAttach = new TagAttach();
        tagAttach.setAttachId(attachId);
        tagAttach.setAttachType(attachType);
        tagAttach.setTagId(tagId);
        if (tagService.deleteAttachmentTag(tagAttach)) {
            return "success";
        }
        return "failed";
    }

    @RequestMapping(value = "/{tagId}", method = RequestMethod.POST)
    @Interceptors({ AttachmentCreatorRequired.class, ProjectNotArchivedRequired.class })
    @ResponseBody
    public void editTag(@RequestBody Tag tag, @PathVariable("projectId") int projectId, @PathVariable("tagId") int tagId) {
        tag.setProjectId(projectId);
        tag.setId(tagId);
        tagService.updateTag(tag);
    }

    @RequestMapping(value = "/{tagId}", method = RequestMethod.GET)
    @Interceptors({ ProjectMemberRequired.class })
    @ResponseBody
    public ImmutableMap<String, ?> showAttachmentsWithTag(@PathVariable("projectId") int projectId,
            @PathVariable("companyId") int companyId, @PathVariable("tagId") int tagId, @RequestParam(value = "page",
                    required = false,
                    defaultValue = "1") Integer page) {
        Tag tag = tagService.getTagById(tagId);
        if (tag == null) {
            throw new ResourceNotFoundException(String.format("tag id = %d not found!", tagId));
        }

        List<Taggable> attachments = tagService.getTagTarget(tag, new Attachment().getType(), (page - 1)
                * ATTACHMENT_PER_PAGE, ATTACHMENT_PER_PAGE);
        TagDTO tagDto = TagTransform.tagToTagDTO(tag);
        return ImmutableMap.of("tagDto", tagDto, "attachments", attachments);

    }
}
