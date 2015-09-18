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
package com.onboard.service.index.model;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.onboard.domain.model.type.Indexable;

/**
 * 检索输出的结果文档
 * 
 * @author yewei
 * 
 */
public interface IndexDocument {
    
    public static final String ID_TEMLATE = "%s_%s";
    
    String getId();
    String getModelType();
    Integer getModelId();
    
    Integer getProjectId();
    String getProjectName();
    Integer getCompanyId();
    
    Date getCreatedTime();
    Integer getCreatorId();
    String getCreatorName();
    String getCreatorAvatar();
    List<Integer> getRelatorIds();
    
    String getTitle();
    void setTitle(String title);
    String getContent();
    void setContent(String content);
    String getAttachTitle();
    
    Indexable getIndexable();
    
    Map<String, Object> getExtendIndexFields();
    
    boolean needIndex();
    

}
