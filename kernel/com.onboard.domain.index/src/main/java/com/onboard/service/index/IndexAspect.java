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
package com.onboard.service.index;

import org.aspectj.lang.ProceedingJoinPoint;

import com.onboard.domain.mapper.model.common.BaseExample;
import com.onboard.domain.model.type.Indexable;

/**
 * 更新索引的切面
 * @author yewei
 */
public interface IndexAspect {
    /**
     * 使用切面添加文档
     * @param item 文档
     */
    public void insert(Indexable item);

    /**
     * 使用切面添加文档
     * @param item 文档
     */
    public void insertSelective(Indexable item);

    /**
     * 使用切面更新文档
     * @param item 文档
     */
    public void updateByPrimaryKey(Indexable item);

    /**
     * 使用切面更新文档
     * @param item 文档
     */
    public void updateByPrimaryKeySelective(Indexable item);

    /**
     * 使用切面更新文档
     * @param item 文档
     */
    public void updateByExample(Indexable item, BaseExample itemExample);

    /**
     * 使用切面更新文档
     * @param item 文档
     */
    public void updateByExampleSelective(Indexable item, BaseExample itemExample);

    /**
     * 使用切面删除文档
     * @param item 文档
     */
    public Object deleteByPrimaryKey(ProceedingJoinPoint joinpoint);

    /**
     * 使用切面删除文档
     * @param item 文档
     */
    public Object deleteByExample(ProceedingJoinPoint joinpoint);

}
