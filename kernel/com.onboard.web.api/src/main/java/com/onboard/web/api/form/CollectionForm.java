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
package com.onboard.web.api.form;

import javax.validation.constraints.Min;

import org.hibernate.validator.constraints.NotBlank;

import com.onboard.domain.model.Collection;

public class CollectionForm extends Collection {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @NotBlank
    private String attachType;

    @Min(1)
    private Integer attachId;

    @Override
    public String getAttachType() {
        return attachType;
    }

    @Override
    public void setAttachType(String attachType) {
        this.attachType = attachType;
    }

    @Override
    public Integer getAttachId() {
        return attachId;
    }

    @Override
    public void setAttachId(Integer attachId) {
        this.attachId = attachId;
    }
}
