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
import com.onboard.domain.model.Tag;
import com.onboard.dto.TagDTO;

public class TagTransform {

    public static final Function<Tag, TagDTO> TAG_TO_TAGDTO_FUNCTION = new Function<Tag, TagDTO>() {
        @Override
        public TagDTO apply(Tag input) {
            return tagToTagDTO(input);
        }
    };
    public static final Function<TagDTO, Tag> TAGDTO_TO_TAG_FUNCTION = new Function<TagDTO, Tag>() {
        @Override
        public Tag apply(TagDTO input) {
            return tagDTOtoTag(input);
        }
    };

    public static TagDTO tagToTagDTO(Tag tag) {
        TagDTO tagDTO = new TagDTO();
        BeanUtils.copyProperties(tag, tagDTO);
        return tagDTO;
    }

    public static Tag tagDTOtoTag(TagDTO tagDTO) {
        Tag tag = new Tag();
        BeanUtils.copyProperties(tagDTO, tag);
        return tag;
    }

}
