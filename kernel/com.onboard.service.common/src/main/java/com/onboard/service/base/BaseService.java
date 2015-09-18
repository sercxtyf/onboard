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
package com.onboard.service.base;

import java.util.List;

import com.onboard.domain.mapper.model.common.BaseExample;
import com.onboard.domain.model.type.BaseOperateItem;

public interface BaseService<I extends BaseOperateItem, E extends BaseExample> {
    
    /**
     * 根据主键获取对象
     * @param id 目标对象的主键
     * @return 按要求从数据库中获取出的对象，当不存在时返回null
     */
    I getById(int id);
    
    /**
     * 获取所有对象的列表
     * @return 按要求从数据库中获取出的对象组成的列表
     */
    List<I> getAll();

    /**
     * 获取一段范围内的对象的列表
     * @param start 范围的起始位置
     * @param limit 范围的长度
     * @return 按要求从数据库中获取出的对象组成的列表
     */
    List<I> getAll(int start, int limit);

    /**
     * 根据样例对象获取符合条件的对象的列表
     * @param item 样例对象
     * @return 按要求从数据库中获取出的对象组成的列表
     */
    List<I> getBySample(I item);
    
    /**
     * 根据样例对象获取一定范围内符合条件的对象列表
     * @param item 样例对象
     * @param start 范围的起始位置
     * @param limit 范围的最大长度
     * @return 按要求从数据库中获取出的对象组成的列表
     */
    List<I> getBySample(I item, int start, int limit);

    /**
     * 根据样例对象获取符合条件的对象的数量
     * @param item 样例对象
     * @return 按要求从数据库中获取出的对象的数量
     */
    int countBySample(I item);

    /**
     * 在数据库中创建一个对象
     * @param item 需要被添加进数据库的对象
     * @return 创建好的对象，包括其在数据库中的主键
     */
    I create(I item);

    /**
     * 在数据库中更新一个对象，在更新过程中忽略值为null的域
     * @param item 需要被更新进数据库的对象
     * @return 更新好的对象
     */
    I updateSelective(I item);
    
    /**
     * 在数据库中更新一个对象
     * @param item 需要被更新进数据库的对象
     * @return 更新好的对象
     */
    I update(I item);

    /**
     * 在数据库中删除一个对象
     * @param id 需要被删除的对象的主键
     */
    void delete(int id);
    
    /**
     * 在数据库中删除一个对象
     * @param id 需要被恢复的对象的主键
     */
    void recover(int id);
    
    /**
     * TODO: 重新整理名称
     * 在数据库中彻底删除一个对象
     * @param id 需要被彻底删除的对象的主键
     */
    void deleteFromTrash(int id);
    
    /**
     * 返回该对象的类型标识符
     * @return
     */
    String getModelType();
    
    /**
     * 返回一个与该对象相同类型的新对象
     * @return
     */
    I newItem();
    
    /**
     * TODO: delete this method
     * @param id
     * @return
     */
    I getByIdWithDetail(int id);
    
    /**
     * TODO: delete this method
     * Get item list by example
     * 
     * @param item
     * @param start
     * @param limit
     * @return the item list
     */
    List<I> getByExample(E example);

    /**
     * TODO: delete this method
     * Get item count by example
     * 
     * @param item
     * @return the count
     */
    int countByExample(E example);
    
    /**
     * 新建example
     * @return
     */
    E newExample();
    
    /**
     * 根据item创建example
     * @param item
     * @return
     */
    E newExample(I item);
    
}
