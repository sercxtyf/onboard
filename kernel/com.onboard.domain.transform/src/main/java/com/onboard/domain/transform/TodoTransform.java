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
import com.google.common.collect.Lists;
import com.onboard.domain.model.Project;
import com.onboard.domain.model.Todo;
import com.onboard.dto.TodoDTO;

public class TodoTransform {

    /**
     * only basic datas
     */
    public static final Function<Todo, TodoDTO> TODO_DTO_FUNCTION = new Function<Todo, TodoDTO>() {
        @Override
        public TodoDTO apply(Todo input) {
            TodoDTO result = todoToTodoDTO(input);
            Project project = input.getProject();
            if (project != null) {
                result.projectName = project.getName();
            }
            return result;
        }
    };

    public static TodoDTO todoToTodoDTO(Todo todo) {
        TodoDTO todoDTO = new TodoDTO();
        BeanUtils.copyProperties(todo, todoDTO);
        return todoDTO;
    }

    public static TodoDTO todoToTodoDTOWithComments(Todo todo) {
        TodoDTO todoDTO = new TodoDTO();
        BeanUtils.copyProperties(todo, todoDTO);
        if (todo.getComments() != null) {
            todoDTO.setComments(Lists.transform(todo.getComments(), CommentTransform.COMMENT_TO_DTO_FUNCTION));
        }
        if (todo.getSubscribers() != null) {
            todoDTO.setSubscribers(Lists.transform(todo.getSubscribers(), UserTransform.USER_TO_USERDTO_FUNCTION));
        }
        return todoDTO;
    }

}
