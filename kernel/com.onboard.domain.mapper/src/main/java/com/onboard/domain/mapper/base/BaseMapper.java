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
package com.onboard.domain.mapper.base;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.onboard.domain.mapper.model.common.BaseExample;
import com.onboard.domain.mapper.model.common.BaseItem;

public interface BaseMapper<R extends BaseItem, E extends BaseExample> {

    int countByExample(E example);

    int deleteByExample(E example);

    int deleteByPrimaryKey(Integer id);

    int insert(R record);

    int insertSelective(R record);

    List<R> selectByExample(E example);

    R selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") R record, @Param("example") E example);

    int updateByExample(@Param("record") R record, @Param("example") E example);

    int updateByPrimaryKeySelective(R record);

    int updateByPrimaryKey(R record);

}
