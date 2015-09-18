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
package com.onboard.web.api.collection;

import java.util.List;

import javax.validation.Valid;

import org.elevenframework.web.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.google.common.collect.Lists;
import com.onboard.domain.model.Collection;
import com.onboard.domain.model.User;
import com.onboard.domain.model.type.Recommendable;
import com.onboard.domain.transform.CollectionTransform;
import com.onboard.dto.CollectionDTO;
import com.onboard.service.collaboration.CollectionService;
import com.onboard.service.common.identifiable.IdentifiableManager;
import com.onboard.service.security.RoleService;
import com.onboard.service.security.exception.NoPermissionException;
import com.onboard.service.web.SessionService;
import com.onboard.web.api.form.CollectionForm;

@Controller
@RequestMapping(value = "/collections")
public class CollectionController {

    @Autowired
    private CollectionService collectionService;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private IdentifiableManager identifiableManager;

    @Autowired
    private RoleService roleService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    @ResponseBody
    public List<CollectionDTO> getCollections(
            @RequestParam(required = false, value = "attachType", defaultValue = "") String attachType,
            @RequestParam(required = false, value = "attachId", defaultValue = "0") Integer attachId) {

        User user = sessionService.getCurrentUser();
        if (attachType != null && attachType.length() > 0) {
            return Lists.transform(collectionService.getCollectionsByAttachTypeAndId(user.getId(), attachId, attachType),
                    CollectionTransform.COLLECTION_DTO_FUNCTION);
        }
        return Lists.transform(collectionService.getCollectionsByUserId(user.getId()),
                CollectionTransform.COLLECTION_DTO_FUNCTION);
    }

    @RequestMapping(value = "/{collectionId}", method = RequestMethod.GET)
    @ResponseBody
    public CollectionDTO getCollectionById(@PathVariable Integer collectionId) {
        Collection collection = collectionService.getCollectionById(collectionId);
        if (collection.getUserId() != sessionService.getCurrentUser().getId()) {
            throw new NoPermissionException();
        }
        return CollectionTransform.collectionToCollectionDTO(collection);
    }

    @RequestMapping(value = "/{collectionId}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.OK)
    public void deleteCollectionById(@PathVariable Integer collectionId) {
        collectionService.deleteCollection(collectionId);
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    @ResponseBody
    public CollectionDTO createCollection(@Valid @RequestBody CollectionForm collection) {
        int userId = sessionService.getCurrentUser().getId();
        Recommendable recommendable = (Recommendable) identifiableManager.getIdentifiableByTypeAndId(collection.getAttachType(),
                collection.getAttachId());
        if (recommendable == null
                || !roleService.projectMember(userId, recommendable.getCompanyId(), recommendable.getProjectId())) {
            throw new NoPermissionException();
        }
        Collection newCollection = collectionService.createCollection(userId, collection.getAttachId(),
                collection.getAttachType());
        if (null == newCollection) {
            throw new ResourceNotFoundException();
        }
        return CollectionTransform.collectionToCollectionDTO(newCollection);
    }
}
