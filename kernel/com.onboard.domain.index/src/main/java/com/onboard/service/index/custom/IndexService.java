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
package com.onboard.service.index.custom;

import java.util.List;

import com.onboard.domain.model.type.Indexable;
import com.onboard.service.index.model.Page;
import com.onboard.service.index.model.SearchQuery;
import com.onboard.service.index.model.SearchResult;

/**
 * 全文检索写入接口
 *
 * @author lvyiqiang, yewei
 *
 */
public interface IndexService {
    
    /**
     * 在全局索引中创建一个可索引对象
     * @param indexable 需要被添加进全文索引的索引对象
     */
    public void addIndex(Indexable indexable);

    /**
     * 在全局索引中更新一个可索引对象
     * @param modifiedIndexable 需要被更新的索引对象
     */
    public void updateIndex(Indexable modifiedIndexable);

    /**
     * 在全局索引中根据文档主键删除对应的可索引对象
     * @param id 文档主键
     */
    public void deleteIndexById(String id);

    /**
     * 在全局索引中根据文档主键列表删除对应的可索引对象
     * @param idList 文档主键的列表
     */
    public void deleteIndexByIdList(List<String> idList);

    /**
     * 在全局索引中，根据关键词，项目列表和分页信息对特定的对象进行查询
     * @param key 索引关键词
     * @param projectIdList 项目列表
     * @param page 分页信息
     * @param modelType 查询对象的类型
     * @return {@link SearchResult}对象
     */
    public SearchResult search(String key, SearchQuery searchQuery, Page page);
    

    /**
     * 关键词推荐
     * @param key
     * @return 相关的关键词列表
     */
    public List<String> suggest(String key);
}
