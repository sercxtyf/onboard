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

import com.onboard.domain.mapper.model.CommentExample;
import com.onboard.domain.model.Comment;
import com.onboard.domain.model.type.Commentable;
import com.onboard.service.base.BaseService;

/**
 * {@link Comment}服务接口
 * 
 * @author yewei
 * 
 */
public interface CommentService extends BaseService<Comment, CommentExample> {

    /**
     * 获取某一对象的评论数量
     * 
     * @param attachType
     * @param attachId
     * @return
     */
    public int getCountOfCommentsByTopic(String attachType, int attachId);

    /**
     * 获取某一个对象的所有评论
     * 
     * @param attachType
     * @param attachId
     * @param start
     * @param limit
     * @return
     */
    public List<Comment> getCommentsByTopic(String attachType, int attachId, int start, int limit);

    /**
     * 获取单个评论的对象信息
     * 
     * @param targetType
     * @param id
     * @return
     */
    public Commentable getCommentTarget(String targetType, int id);

    /**
     * 获取单个评论的对象名称
     * 
     * @param targetType
     * @param id
     * @return
     */
    public String getCommentTargetName(String targetType, int id);

    /**
     * 获取某一个对象的所有评论
     * 
     * @param commentable
     * @param start
     * @param limit
     */
    void fillCommentable(Commentable commentable, int start, int limit);


    /**
     * 复制评论
     * 
     */
    public void copyComments(Commentable oldItem, Commentable newItem);

    /**
     * 根据type和id删除comment
     * 
     * @param type
     * @param id
     */
    public void deleteCommentByAttachTypeAndId(String type, int id);

    public void relocateComment(Commentable item, int projectId);

    /***
     * @author Chenlong
     * @param companyId
     * @param since
     * @param until
     * @return
     */
    List<Comment> getCommentsByCompanyIdBetweenDates(int companyId, Date since, Date until);
}
