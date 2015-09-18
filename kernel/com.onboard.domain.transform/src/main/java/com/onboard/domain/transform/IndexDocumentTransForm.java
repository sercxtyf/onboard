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
package com.onboard.domain.transform;

import com.google.common.base.Function;
import com.onboard.dto.IndexDocumentDTO;
import com.onboard.service.index.model.IndexDocument;

public class IndexDocumentTransForm {
    public static final Function<IndexDocument, IndexDocumentDTO> INDEXDOCUMENT_DTO_FUNCTION = new Function<IndexDocument, IndexDocumentDTO>() {
        @Override
        public IndexDocumentDTO apply(IndexDocument input) {
            return indexDocumentToIndexDocumentDTO(input);
        }
    };

    public static IndexDocumentDTO indexDocumentToIndexDocumentDTO(IndexDocument indexDocument) {
        IndexDocumentDTO indexDocumentDTO = new IndexDocumentDTO();
        indexDocumentDTO.setCompanyId(indexDocument.getCompanyId());
        indexDocumentDTO.setContent(indexDocument.getContent());
        indexDocumentDTO.setCreatedTime(indexDocument.getCreatedTime());
        indexDocumentDTO.setCreatorAvatar(indexDocument.getCreatorAvatar());
        indexDocumentDTO.setCreatorId(indexDocument.getCreatorId());
        indexDocumentDTO.setCreatorName(indexDocument.getCreatorName());
        indexDocumentDTO.setModelId(indexDocument.getModelId());
        indexDocumentDTO.setModelType(indexDocument.getModelType());
        indexDocumentDTO.setProjectId(indexDocument.getProjectId());
        indexDocumentDTO.setProjectName(indexDocument.getProjectName());
        indexDocumentDTO.setRelatorIds(indexDocument.getRelatorIds());
        indexDocumentDTO.setTitle(indexDocument.getTitle());
        indexDocumentDTO.setExtendIndexFileds(indexDocument.getExtendIndexFields());
        return indexDocumentDTO;
    }

}
