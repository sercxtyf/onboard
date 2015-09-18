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

import com.onboard.service.index.model.SearchQuery;
import com.onboard.service.index.model.SearchResult;

public interface SearchService {
    
	/**
	 * 在Solr中搜索指定关键字，并返回一段范围内的结果
	 * @param key 需要搜索的结果
	 * @param searchQuery 搜索队列
	 * @param start 范围的开始位置
	 * @param limit 范围的最大长度
	 * @return 按照要求从Solr中获取的搜索结果
	 */
    SearchResult search(String key, SearchQuery searchQuery, int start, int limit);
}
