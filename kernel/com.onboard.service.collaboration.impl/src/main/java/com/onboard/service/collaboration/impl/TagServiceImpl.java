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
package com.onboard.service.collaboration.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.onboard.domain.mapper.TagAttachMapper;
import com.onboard.domain.mapper.TagMapper;
import com.onboard.domain.mapper.model.TagAttachExample;
import com.onboard.domain.mapper.model.TagExample;
import com.onboard.domain.model.Tag;
import com.onboard.domain.model.TagAttach;
import com.onboard.domain.model.type.Taggable;
import com.onboard.service.collaboration.TagService;
import com.onboard.service.common.identifiable.IdentifiableManager;

/**
 * {@link com.onboard.service.collaboration.TagService} Service implementation
 * 
 * @XR, yewei
 * 
 */
@Transactional
@Service("tagServiceBean")
public class TagServiceImpl implements TagService {

    @Autowired
    private TagMapper tagMapper;

    @Autowired
    private TagAttachMapper tagAttachMapper;

    @Autowired
    IdentifiableManager identifiableManager;

    @Override
    public Tag getTagById(int id) {
        return tagMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<Tag> getTags(int start, int limit) {
        TagExample example = new TagExample(new Tag());
        example.setLimit(start, limit);
        return tagMapper.selectByExample(example);
    }

    @Override
    public List<Tag> getTagsByExample(Tag item, int start, int limit) {
        TagExample example = new TagExample(item);
        example.setLimit(start, limit);
        return tagMapper.selectByExample(example);
    }

    @Override
    public int countByExample(Tag item) {
        TagExample example = new TagExample(item);
        return tagMapper.countByExample(example);
    }

    @Override
    public Tag createTag(Tag item) {
        tagMapper.insert(item);
        return item;
    }

    @Override
    public Tag updateTag(Tag item) {
        tagMapper.updateByPrimaryKey(item);
        return item;
    }

    @Override
    public void deleteTag(int id) {
        tagMapper.deleteByPrimaryKey(id);
    }

    @Override
    public Tag getOrCreateTag(Tag item) {
        List<Tag> tags = tagMapper.selectByExample(new TagExample(item));
        if (tags == null || tags.size() == 0) {
            tagMapper.insert(item);
            return item;
        } else {
            return tags.get(0);
        }
    }

    @Override
    public List<Tag> getTags(Taggable taggable) {
        TagAttach tagAttach = new TagAttach();
        tagAttach.setAttachId(taggable.getId());
        tagAttach.setAttachType(taggable.getType());
        List<TagAttach> tagAttachs = tagAttachMapper.selectByExample(new TagAttachExample(tagAttach));

        List<Tag> tags = new ArrayList<Tag>();
        for (TagAttach ta : tagAttachs) {
            tags.add(tagMapper.selectByPrimaryKey(ta.getTagId()));
        }
        return tags;
    }

    @Override
    public void setTags(List<Taggable> taggables) {
        if (taggables != null) {
            for (Taggable taggable : taggables) {
                taggable.setTags(this.getTags(taggable));
            }
        }
    }

    @Override
    public boolean deleteAttachmentTag(TagAttach at) {

        tagAttachMapper.deleteByExample(new TagAttachExample(at));

        // 判断该标签下是否有文件，若无，则删除该标签
        TagAttach ta = new TagAttach();
        ta.setTagId(at.getTagId());
        List<TagAttach> tagAttachs = tagAttachMapper.selectByExample(new TagAttachExample(ta));
        if (tagAttachs == null || tagAttachs.size() == 0) {
            this.deleteTag(at.getTagId());
            return true;
        }
        return false;
    }

    @Override
    public List<Taggable> getTagTarget(Tag tag, String modelType, int start, int limit) {

        TagAttach tagAttach = new TagAttach();
        tagAttach.setTagId(tag.getId());
        tagAttach.setAttachType(modelType);
        TagAttachExample example = new TagAttachExample(tagAttach);
        example.setLimit(start, limit);
        example.setOrderByClause("id desc");
        List<TagAttach> tagAttachs = tagAttachMapper.selectByExample(example);

        List<Taggable> taggables = new ArrayList<Taggable>();
        for (TagAttach ta : tagAttachs) {
            Taggable t = (Taggable) identifiableManager.getIdentifiableByTypeAndId(modelType, ta.getAttachId());
            if (t != null) {
                t.setTags(this.getTags(t));
                taggables.add(t);
            }
        }
        return taggables;
    }

    @Override
    public List<Tag> getTagsByProjectId(int projectId) {
        Tag tag = new Tag();
        tag.setProjectId(projectId);
        return tagMapper.selectByExample(new TagExample(tag));
    }

    @Override
    public Integer countByTagId(int tagId) {
        TagAttach tagAttach = new TagAttach();
        tagAttach.setTagId(tagId);
        return tagAttachMapper.countByExample(new TagAttachExample(tagAttach));
    }

    @Override
    public boolean addTagWithAttacTypeAndId(Tag tag, String type, int id) {
        TagAttach tagAttach = new TagAttach();
        tagAttach.setAttachId(id);
        tagAttach.setAttachType(type);
        tagAttach.setTagId(tag.getId());
        List<TagAttach> tas = tagAttachMapper.selectByExample(new TagAttachExample(tagAttach));
        if (tas == null || tas.size() == 0) {
            tagAttachMapper.insert(tagAttach);
            return true;
        }
        return false;
    }

    @Override
    public void deleteTagWithAttachTypeAndId(Tag tag, String type, int id) {
        TagAttach tagAttach = new TagAttach();
        tagAttach.setAttachId(id);
        tagAttach.setAttachType(type);
        tagAttach.setTagId(tag.getId());
        tagAttachMapper.deleteByExample(new TagAttachExample(tagAttach));

    }

    @Override
    public void fillTags(Taggable tagable) {
        TagAttach tagAttach = new TagAttach();
        tagAttach.setAttachId(tagable.getId());
        tagAttach.setAttachType(tagable.getType());
        List<TagAttach> tagAttachs = tagAttachMapper.selectByExample(new TagAttachExample(tagAttach));
        List<Tag> tags = new ArrayList<Tag>();
        for (TagAttach ta : tagAttachs) {
            tags.add(tagMapper.selectByPrimaryKey(ta.getTagId()));
        }
        tagable.setTags(tags);
    }

}
