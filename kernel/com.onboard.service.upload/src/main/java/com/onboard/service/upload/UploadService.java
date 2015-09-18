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
package com.onboard.service.upload;

import java.util.List;

import com.onboard.domain.mapper.model.UploadExample;
import com.onboard.domain.model.Upload;
import com.onboard.service.base.BaseService;

/**
 * {@link Upload}服务接口
 * 
 * @author yewei
 * 
 */
public interface UploadService extends BaseService<Upload, UploadExample> {

    /**
     * 获取一个项目中指定范围内的文件列表
     * @param projectId
     * @param start 列表的起始位置
     * @param limit 列表的最大长度
     * @return 按要求从数据库中获取出的Upload列表
     */
    List<Upload> getUploadsByProject(int projectId, int start, int limit);

    /**
     * 将一个文件移动到指定的项目下
     * @param upload 需要被移动的文件
     * @param projectId 目标项目的主键
     */
    void moveUpload(Upload upload, int projectId);

}
