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
import com.google.common.collect.Lists;
import com.onboard.domain.model.Story;
import com.onboard.dto.StoryDTO;

public class StoryTransform {

    public static final Function<Story, StoryDTO> STORY_DTO_FUNCTION = new Function<Story, StoryDTO>() {
        @Override
        public StoryDTO apply(Story input) {
            return storyToStoryDTO(input);
        }
    };

    public static StoryDTO storyToStoryDTO(Story story) {
        StoryDTO storyDTO = new StoryDTO();
        BeanUtils.copyProperties(story, storyDTO);
        if (story.getChildStories() != null) {
            storyDTO.setChildStoryDTOs(Lists.newArrayList(Lists.transform(story.getChildStories(),
                    StoryTransform.STORY_DTO_FUNCTION)));
        }
        return storyDTO;
    }

    public static Function<Story, StoryDTO> getStoryDtoFunction() {
        return StoryTransform.STORY_DTO_FUNCTION;
    }
}
