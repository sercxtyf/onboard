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
import com.onboard.domain.model.User;
import com.onboard.dto.UserDTO;

public class UserTransform {

    public static final Function<User, UserDTO> USER_TO_USERDTO_FUNCTION = new Function<User, UserDTO>() {
        @Override
        public UserDTO apply(User input) {
            return userToUserDTO(input);
        }
    };
    public static final Function<UserDTO, User> USERDTO_TO_USER_FUNCTION = new Function<UserDTO, User>() {
        @Override
        public User apply(UserDTO input) {
            return userDTOToUser(input);
        }
    };

    public static UserDTO userToUserDTO(User user) {
        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(user, userDTO);
        return userDTO;
    }

    public static User userDTOToUser(UserDTO userDTO) {
        User user = new User();
        BeanUtils.copyProperties(userDTO, user);
        return user;
    }

}
