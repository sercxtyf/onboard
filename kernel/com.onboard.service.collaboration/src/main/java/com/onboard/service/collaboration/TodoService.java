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

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.onboard.domain.mapper.model.TodoExample;
import com.onboard.domain.model.AttachTodo;
import com.onboard.domain.model.Todo;
import com.onboard.domain.model.Todolist;
import com.onboard.domain.model.type.BaseOperateItem;
import com.onboard.service.base.BaseService;

/**
 * {@link Todo} 相关的服务
 * 
 * @author ruici, yewei
 */
public interface TodoService extends BaseService<Todo, TodoExample> {

    /**
     * 根据项目以及todo在项目中的id获取todo
     * 
     * @param projectId
     * @param projectTodoId
     * @return
     */
    Todo getTodoByProjectIdAndProjectTodoId(int projectId, int projectTodoId);

    List<Todo> getTodosByTodoListWithoutComments(int todolistId);

    /**
     * 获取todolist下的所有todo,不填充comments和subscribers信息
     * 
     * @param todolistId
     * @return
     */
    List<Todo> getOpenTodosByTodoListWithoutCommentsAndSubscribers(int todolistId);

    /**
     * 获取todolist下的所有todo，不需要填充详细信息
     * 
     * @param todolistId
     * @return
     */
    List<Todo> getTodosByTodoList(int todolistId);

    /**
     * 获取todolist下的所有在回收站的todo
     * 
     * @param todolistId
     * @return
     */
    List<Todo> getTodosByTodoListWithDiscard(int todolistId);

    /**
     * 复制一个Todo对象
     * 
     * @param todo
     * @return 返回创建的Todo对象，包括数据库中的id
     */
    Todo copyTodo(Todo todo, Integer projectId, Integer todolistId);


    /**
     * 移动Todo到新的Todolist下
     * 
     * @param todo
     * @param todolistId
     */
    void moveTodo(Todo todo, int todolistId);

    /**
     * 获取日历上展示的todo
     * 
     * @param startTime
     * @param endTime
     * @param companyId
     * @return
     */
    List<Todo> getByTimeRangeByCompany(Date startTime, Date endTime, int companyId, int userId);

    /**
     * 获取项目中已完成Todo的数量
     * 
     * @param projectId
     * @return
     */
    int getCompletedTodosCountByProject(int projectId);

    /**
     * 获取项目已完成的Todo列表，并按日期排序分类
     * 
     * @param projectId
     * @param until
     *            完成日期在until之前
     * @param limit
     *            限制数量，返回结果可能大于limit
     * @return
     */
    TreeMap<Date, List<Todolist>> getCompletedTodolistGroupByDateByProject(int projectId, Date until, int limit);

    /**
     * 获取一组项目用户负责的已完成的任务，基于日期进行分组
     * 
     * @param companyId
     * @param userId
     * @param projectList
     *            如果为空，表示用户参与的所有项目
     * @return
     */
    TreeMap<Date, List<Todolist>> getCompletedTodolistsGroupByDateByUser(int companyId, int userId, List<Integer> projectList,
            Date until, int limit);

    /**
     * 获取一组项目内一个用户的未完成任务
     * 
     * @param userId
     *            为null的时候 ,取出一个项目的所有未完成任务
     * @param projectList
     *            如果为空，表示用户参与的所有项目
     * @return
     */
    List<Todolist> getOpenTodosByUser(Integer userId, List<Integer> projectList);

    /**
     * 更新Todo的日期和负责人
     * 
     * @param todo
     * @return
     */
    Todo updateTodoAssigneeAndDueDate(Todo todo);

    /**
     * 根据截止时间获取todos
     * 
     * @param startTime
     * @param endTime
     * @return
     */
    List<Todo> getByTimeRange(Date startTime, Date endTime);

    /**
     * 获取任务的详细信息
     * 
     * @param id
     * @return
     */
    Todo getTodoByIdWithCommentAndSubscriable(int id);

    /**
     * 获取任务列表下所有已删除的任务
     * 
     * @param todolistId
     * @return
     */
    List<Todo> getDeletedTodosByTodoList(int todolistId);

    List<Todo> getUncompletedTodoByProject(int projectId);

    /**
     * 创建todo关联对象
     * 
     * @param attachType
     * @param attachId
     * @param todoId
     * @return 如果attachTodo已存在，则返回null
     */
    AttachTodo attachToTodo(String attachType, int attachId, int todoId);

    /**
     * 删除关联对象
     * 
     * @param attachType
     * @param attachId
     * @param todoId
     * @return
     */
    void removeAttachToTodo(String attachType, int attachId, int todoId);

    /**
     * 是否已关联上
     * 
     * @param attachType
     * @param attachId
     * @param todoId
     * @return
     */
    boolean isAttachTodoRecorded(String attachType, int attachId, int todoId);

    /**
     * 获取todo相关的关联对象
     * 
     * @param todoId
     * @param attachType
     * @return
     */
    List<BaseOperateItem> getAttachesByTodoId(int todoId, String attachType);

    /**
     * 获取某对象绑定的所有任务
     * 
     * @param attachType
     * @param attachId
     * @return
     */
    List<Todo> getTodosByAttachTypeAndId(String attachType, int attachId);

    /**
     * 将两个todo关联起来，没有顺序关系
     * 
     * @param fromTodo
     * @param toTodo
     * @return
     */
    void associateTodos(Integer fromTodoId, Integer toTodoId);

    /**
     * 获取某todo关联的所有todo，可根据type做筛选，若type为none，则返回所有关联todo
     * 
     * @param todo
     * @param type
     * @return
     */
    List<Todolist> getAssociateTodosByType(Integer todoId, String todoType);

    /**
     * @author Chenlong
     * @param startTime
     * @param endTime
     * @param projectId
     * @return
     */
    List<Todo> getCompletedTodoByTimeRangeProject(Date startTime, Date endTime, int projectId);

    /**
     * 任务完成数统计
     * 
     * @param companyId
     * @param since
     * @param until
     * @return
     */
    Map<Integer, Integer> countForCompanyUsers(Integer companyId, Date since, Date until);

    /**
     * 完成的任务统计
     * 
     * @author Chenlong
     * @param companyId
     * @param since
     * @param until
     * @return
     */
    List<Todo> getCompletedTodosBetweenDates(Integer companyId, Date since, Date until);

    /**
     * 迁移历史任务关键词
     */
    void postgenerateTodoKeywords();

    /***
     * 日历中展示当前用户未完成的Todo
     * 
     * @param companyId
     * @param userId
     * @param since
     * @param until
     * @return
     */
    List<Todo> getOpenTodosBetweenDatesByUser(Integer companyId, Integer userId, Date since, Date until);
}
