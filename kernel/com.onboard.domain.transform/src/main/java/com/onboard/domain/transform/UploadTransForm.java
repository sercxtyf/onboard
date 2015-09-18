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

import org.springframework.beans.BeanUtils;

import com.google.common.base.Function;
import com.onboard.domain.model.Upload;
import com.onboard.dto.UploadDTO;

public class UploadTransForm {

    public static final Function<Upload, UploadDTO> HELPTIP_DTO_FUNCTION = new Function<Upload, UploadDTO>() {
        @Override
        public UploadDTO apply(Upload input) {
            return uploadToUploadDTO(input);
        }
    };

    public static UploadDTO uploadToUploadDTO(Upload upload) {
        UploadDTO uploadDTO = new UploadDTO();
        BeanUtils.copyProperties(upload, uploadDTO);
        return uploadDTO;
    }

}
