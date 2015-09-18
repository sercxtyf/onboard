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

import com.onboard.domain.model.Collection;

public interface CollectionService {

	/**
	 * Get a collection by its id
	 * @param id - id of the collection
	 * @return the collection
	 */
    public Collection getCollectionById(int id);

    /**
     * Create a collection of a attach-able
     * 
     * @param userId - the id of its owner
     * @param attachId - the id of the attach-able
     * @param attachType - the type of the attach-able
     * @return the created collection
     */
    public Collection createCollection(int userId, int attachId, String attachType);

    /**
     * Filter collection by its attachType and attachId
     * 
     * @param userId - the id of its owner
     * @param attachId
     * @param attachType
     * @return a list of collections fits the requirements
     */
    public List<Collection> getCollectionsByAttachTypeAndId(int userId, int attachId, String attachType);

    /**
     * Filter collection by its owner
     * @param userId the id of its owner
     * @return a list of collections fits the requirements
     */
    public List<Collection> getCollectionsByUserId(int userId);

    /**
     * Delete collection by its id
     * @param id - id of the collection
     */
    public void deleteCollection(int id);

}
