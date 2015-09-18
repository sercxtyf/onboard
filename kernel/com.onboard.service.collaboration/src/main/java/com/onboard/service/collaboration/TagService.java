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
package com.onboard.service.collaboration;

import java.util.List;

import com.onboard.domain.model.Tag;
import com.onboard.domain.model.TagAttach;
import com.onboard.domain.model.type.Taggable;

/**
 * {@link Tag} Service Interface
 * 
 * @author XR, yewi
 * 
 */
public interface TagService {
    /**
     * Get item by id
     * 
     * @param id
     * @return item
     */
    Tag getTagById(int id);

    /**
     * Get item list
     * 
     * @param start
     * @param limit
     * @return the item list
     */
    List<Tag> getTags(int start, int limit);

    /**
     * Get item list by example
     * 
     * @param item
     * @param start
     * @param limit
     * @return the item list
     */
    List<Tag> getTagsByExample(Tag item, int start, int limit);

    /**
     * Get item count by example
     * 
     * @param item
     * @return the count
     */
    int countByExample(Tag item);

    /**
     * Create
     * 
     * @param item
     * @return the created Tag
     */
    Tag createTag(Tag item);

    /**
     * Update
     * 
     * @param item
     * @return the updated item
     */
    Tag updateTag(Tag item);

    /**
     * Delete
     * 
     * @param id
     */
    void deleteTag(int id);

    /**
     * 获取或创建tag
     * 
     * @param item
     * @return
     */
    Tag getOrCreateTag(Tag item);

    /**
     * 添加附件标签
     * 
     * @param tag
     * @param attachmentId
     */
    boolean addTagWithAttacTypeAndId(Tag tag, String type, int id);

    /**
     * 删除附件标签
     * 
     * @param tag
     * @param attachmentId
     */
    void deleteTagWithAttachTypeAndId(Tag tag, String type, int id);

    /**
     * 获取attachment的tags
     * 
     * @param attachment
     * @return
     */
    List<Tag> getTags(Taggable taggable);

    /**
     * 获取attachments的tags
     * 
     * @param attachments
     * @return
     */
    void setTags(List<Taggable> taggables);

    /**
     * 删除attachment tag
     * @param ta
     * @return 是否将该标签删除（若有其他对象引用该标签，则只删除引用关系，不删除该标签）
     */
    boolean deleteAttachmentTag(TagAttach ta);

    /**
     * 通过tag获取{@link Taggable}列表
     * 
     * @param tag
     * @param type
     * @param start
     * @param limit
     * @return
     */
    List<Taggable> getTagTarget(Tag tag, String type, int start, int limit);

    /**
     * 通过projectId获取tags
     * 
     * @param projectId
     * @return
     */
    List<Tag> getTagsByProjectId(int projectId);

    /**
     * 根据tagId获取该标签下文件的数量
     * 
     * @param tagId
     * @return
     */
    Integer countByTagId(int tagId);

    /**
     * 填充tags
     * 
     * @param tagable
     */
    void fillTags(Taggable tagable);

}