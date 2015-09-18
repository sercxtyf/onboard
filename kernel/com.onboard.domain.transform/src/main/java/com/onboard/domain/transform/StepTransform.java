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
import com.onboard.domain.model.Project;
import com.onboard.domain.model.Step;
import com.onboard.dto.StepDTO;

public class StepTransform {

    public static final Function<Step, StepDTO> STEP_DTO_FUNCTION = new Function<Step, StepDTO>() {
        @Override
        public StepDTO apply(Step input) {
            StepDTO result = stepToStepDTO(input);
            Project project = input.getProject();
            if (project != null) {
                result.setProjectName(project.getName());
            }
            return result;
        }
    };

    public static StepDTO stepToStepDTO(Step step) {
        StepDTO stepDTO = new StepDTO();
        BeanUtils.copyProperties(step, stepDTO);
        if (step.getAssignee() != null) {
            stepDTO.setAssigneeDTO(UserTransform.userToUserDTO(step.getAssignee()));
        }
        return stepDTO;

    }
}
