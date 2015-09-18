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

import java.util.Date;
import java.util.List;
import java.util.TreeMap;

import com.onboard.domain.mapper.model.AttachmentExample;
import com.onboard.domain.model.Attachment;
import com.onboard.domain.model.type.Attachable;
import com.onboard.service.base.BaseService;

/**
 * {@link Attachment}服务接口
 * 
 * @author yewei
 * 
 */
public interface AttachmentService extends BaseService<Attachment, AttachmentExample> {

    static int NONE_ATTACH_ID = 0;
    static String NONE_ATTACH_TYPE = "";

    /**
     * 根据对象类型和id获取Attachment对象
     * 
     * @param type
     * @param id
     * @param start
     * @param limit
     * @return
     */
    List<Attachment> getAttachmentsByTypeAndId(String type, int id, int start, int limit);

    /**
     * 根据对象类型和id获取Attachment对象
     * 
     * @param type
     * @param id
     * @param start
     * @param limit
     * @return
     */
    List<Attachment> getAttachmentsByTypeAndIdWithNotDiscard(String type, int id, int start, int limit);

    /**
     * 根据对象类型和id获取Attachment对象
     * 
     * @param type
     * @param id
     * @param start
     * @param limit
     * @return
     */
    List<Attachment> getAttachmentsByTypeAndIdWithDiscard(String type, int id, int start, int limit);

    /**
     * 填充Attachable对象
     * 
     * @param attachable
     * @param start
     * @param limit
     * @return
     */
    void fillAttachments(Attachable attachable, int start, int limit);

    /**
     * 填充Attachable对象
     * 
     * @param attachable
     * @param start
     * @param limit
     * @return
     */
    void fillAttachmentsWithNotDiscard(Attachable attachable, int start, int limit);

    /**
     * 填充Attachable对象
     * 
     * @param attachable
     * @param start
     * @param limit
     * @return
     */
    void fillAttachmentsWithDiscard(Attachable attachable, int start, int limit);

    /**
     * 获取项目下的attachments
     * 
     * @param projectId
     *            项目id
     * @param start
     * @param limit
     * @return
     */
    List<Attachment> getAttachmentsByProjectId(int projectId, int start, int limit);

    /**
     * 获取项目下的attachments
     * 
     * @param projectId
     *            项目id
     * @param start
     * @param limit
     * @return
     */
    List<Attachment> getAttachmentsByProjectIdWithNotDiscard(int projectId, int start, int limit);
    List<Attachment> getAttachmentsByCompanyIdWithNotDiscard(int companyId, int start, int limit);

    /**
     * 获取一组项目里某个用户相关的的attachments
     * 
     * @param companyId
     * @param userId
     * @param start
     * @param limit
     * @param projectList
     *            如果为空，表示用户参与的所有项目
     * @return
     */
    List<Attachment> getAttachmentsByUserId(int companyId, int userId, int start, int limit, List<Integer> projectList);

    /**
     * 获取一组项目用户相关的附件，基于日期进行分组
     * 
     * @param companyId
     * @param userId
     * @param projectList
     *            如果为空，表示用户参与的所有项目
     * @return
     */
    TreeMap<Date, List<Attachment>> getAttachmentsByUserGroupByDate(int companyId, int userId, List<Integer> projectList,
            Date until, int limit);

    /**
     * 将附属在对象上的Attachment放入回收站
     * 
     * @param attachType
     * @param attachId
     */
    void discardAttachment(String attachType, int attachId);

    /**
     * 将附属在对象上的Attachment恢复删除
     * 
     * @param attachType
     * @param attachId
     */
    void recoverAttachment(String attachType, int attachId);

    /**
     * 将附属在对象上的Attachment放入回收站
     * 
     * @param attachType
     * @param attachId
     */
    void deleteAttachmentByAttachTypeAndId(String attachType, int attachId);

    /**
     * 暂存临时上传的文件
     * 
     * @param file
     * @return 返回用于临时标识该上传文件的token
     */

    Attachment stageAttachment(int companyId, int projectId, String name, long size, String contentType, byte[] file);

    /**
     * 获取指定attachment对象的文件内容
     * 
     * @param projectId
     *            所属项目id
     * @param id
     *            attachment id
     * @return
     */
    byte[] getAttachmentContentById(int projectId, int id);

    /**
     * 将Stage文件移动至正式文件夹
     * 
     * @param attachmentId
     * @param projectId
     */
    boolean moveStageAttachment(int attachmentId, int projectId);

    /**
     * 对两个attachment列表进行diff 获取newest相对于origin里新添加的Attachment列表
     * 
     * @param origin
     * @param newest
     */
    List<Attachment> diffIncomingOfAttachable(Attachable origin, Attachable newest);

    /**
     * 对两个attachment列表进行diff 获取origin相对于newest中不存在的Attachment列表
     * 
     * @param origin
     * @param newest
     * @return
     */
    List<Attachment> diffDeletedOfAttachable(Attachable origin, Attachable newest);

    /**
     * 根据id获取带url的attachment
     * 
     * @param id
     * @return
     */
    Attachment getAttachmentByIdWithUrl(int id);

    /**
     * 为attachable添加附件
     * 
     * @param attachable
     * @param attachment
     * @return 添加后的附件
     */
    Attachment addAttachmentForAttachable(Attachable attachable, Attachment attachment);

    /**
     * 为attachable添加附件
     * 
     * @param attachable
     * @param attachments
     * @return 添加后的附件列表
     */
    List<Attachment> addAttachmentsForAttachable(Attachable attachable, List<Attachment> attachments);

    /**
     * 为attachable添加附件
     * 
     * @param attachable
     * @param attachments
     * @return 更新后的附件列表
     */
    void appendAttachmentsForAttachable(Attachable attachable);

    /**
     * 将附件移动到新的项目
     * 
     * @author Chenlong
     * @param item
     * @param projectId
     */
    void relocateAttachment(Attachable item, int projectId);

    /**
     * 将附件复制到新的项目
     * 
     * @author Chenlong
     * @param item
     * @param projectId
     */
    void copyAttachments(Attachable oldItem, Attachable newItem);
}
