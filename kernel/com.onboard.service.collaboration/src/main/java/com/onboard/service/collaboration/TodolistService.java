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

import com.onboard.domain.mapper.model.TodolistExample;
import com.onboard.domain.model.Todo;
import com.onboard.domain.model.Todolist;
import com.onboard.service.base.BaseService;

/**
 * Todolist服務接口
 * 
 * @author ruici, yewei
 * 
 */
public interface TodolistService extends BaseService<Todolist, TodolistExample> {

    /**
     * 获取指定项目下的所有TodoList
     * 
     * @param projectId
     * @param start
     * @param limit
     * @return
     */
    List<Todolist> getTodolistsByProject(int projectId, int start, int limit);

    /**
     * 获取指定项目下的所有TodoList,填充了所有todo
     * 
     * @param projectId
     * @param start
     * @param limit
     * @return
     */
    List<Todolist> getTodolistDetailsByProject(int projectId, int start, int limit);

    /**
     * 获取指定项目下的所有未归档的TodoList,并填充了所有未完成的todo
     * 
     * @param projectId
     * @return
     */
    List<Todolist> getOpenTodolistsWithUncompletedTodosByProject(int projectId);

    /**
     * 获取用户在一组项目中尚未完成的todolist列表
     * 
     * @param userId
     * @param projectList
     *            如果为null，则表示用户参与的所有项目
     * @return
     */
    List<Todolist> getOpenTodolistByUser(Integer userId, List<Integer> projectList);

    /**
     * 获取用户在一组项目内已经完成的todolist列表
     * 
     * @param companyId
     * @param userId
     * @param limit
     * @param projectList
     *            如果为空，表示用户参与的所有项目
     * @return
     */
    List<Todolist> getCompletedTodolistByUser(int companyId, int userId, int limit, List<Integer> projectList);

    /**
     * 移动Todolist到新的项目下
     * 
     * @param todolist
     * @param projectId
     */
    void moveTodolist(Todolist todolist, int projectId);

    /**
     * 对todo列表根据todolist进行分组
     * 
     * @param todos
     * @return
     */
    List<Todolist> getTodolistAccordingToTodos(List<Todo> todos);

    /**
     * 获取Todolist详细信息，包括todo, subscriber和comment
     * 
     * @param id
     * @return
     */
    Todolist getTodolistByIdWithExtraInfo(int id);

    /**
     * 获取项目清单，包含所有已完成和未完成的任务
     * 
     * @param id
     * @return
     */
    Todolist getTodolistWithClosedTodos(Integer id);

    /**
     * 复制一份Todolist
     * 
     * @author Chenlong
     * @param todolist
     *            , projectId
     * @return
     */
    Todolist copyTodolist(Todolist todolist, Integer projectId, boolean includeCompletedTodos);

    /**
     * 获取已归档任务清单列表
     * 
     * @param projectId
     * @return
     */
    List<Todolist> getAcrivedTodolists(Integer projectId);

}
