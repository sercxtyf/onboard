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
import com.onboard.domain.model.CompanyApplication;
import com.onboard.dto.CompanyApplicationDTO;

public class CompanyApplicationTransform {

    public static final Function<CompanyApplication, CompanyApplicationDTO> TO_DTO_FUNCTION = new Function<CompanyApplication, CompanyApplicationDTO>() {
        @Override
        public CompanyApplicationDTO apply(CompanyApplication input) {
            return companyApplicationToCompanyApplicationDTO(input);
        }
    };

    public static CompanyApplicationDTO companyApplicationToCompanyApplicationDTO(CompanyApplication companyApplication) {
        CompanyApplicationDTO companyApplicationDTO = new CompanyApplicationDTO();
        BeanUtils.copyProperties(companyApplication, companyApplicationDTO);
        return companyApplicationDTO;
    }

    public static CompanyApplication companyApplicationDTOtoCompanyApplication(
            CompanyApplicationDTO companyApplicationDTO) {
        CompanyApplication companyApplication = new CompanyApplication();
        BeanUtils.copyProperties(companyApplicationDTO, companyApplication);
        return companyApplication;
    }

}
