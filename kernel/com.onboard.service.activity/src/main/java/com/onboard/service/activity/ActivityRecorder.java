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
package com.onboard.service.activity;

import org.aspectj.lang.ProceedingJoinPoint;

import com.onboard.domain.model.Activity;
import com.onboard.domain.model.type.BaseProjectItem;

/**
 * 用于记录{@link Activity}信息，可作为Aspect织入到对象创建和更新的方法
 * 
 * Kernel中的活动类型包括：
 * <ul>
 * <li>项目 （创建，删除，更新，移动，恢复，归档，激活）</li>
 * <li>任务列表（创建，删除，更新，移动，恢复）</li>
 * <li>任务 （创建，删除，更新，移动，恢复）</li>
 * <li>讨论 （发起，删除，移动，恢复）</li>
 * <li>评论 （回复，删除）</li>
 * <li>用户 （加入，退出项目）</li>
 * </ul>
 * 
 * @author yewei
 * 
 */
public interface ActivityRecorder {

    /**
     * 记录创建活动
     * 
     * @param item
     *            创建的对象
     */
    void recordCreationActivity(BaseProjectItem item);

    /**
     * 记录更新活动
     * 
     * @param joinpoint
     */
    Object recordUpdateActivity(ProceedingJoinPoint joinpoint);

    /**
     * 刪除活动
     * 
     * @param joinpoint
     */
    Object deleteActivity(ProceedingJoinPoint joinpoint);
}
