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

import com.onboard.domain.model.Activity;
import com.onboard.domain.model.User;
import com.onboard.domain.model.type.BaseProjectItem;

/**
 * 创建完{@link Activity}进行回调的接口，相关的回调是异步完成的
 * 
 * @author yewei
 * 
 */
public interface ActivityHook {

    /**
     * 创建完{@link Activity}时, {@link ActivityRecorder}会调用ActivityHook
     * 
     * @param owner
     *            产生Activity的用户
     * @param activity
     *            被创建出来的Activity
     * @param item
     *            Activity关联的新创建的对象
     * @throws Throwable
     */
    void whenCreationActivityCreated(User owner, Activity activity, BaseProjectItem item) throws Throwable;

    /**
     * 创建完{@link Activity}时, {@link ActivityRecorder}会调用ActivityHook
     * 
     * @param owner
     *            产生Activity的用户
     * @param activity
     *            被创建出来的Activity
     * @param item
     *            Activity关联的更新前的对象，已填充字段
     * @param updatedItem
     *            Activity关联的更新后的对象，已填充字段
     * @throws Throwable
     */
    void whenUpdateActivityCreated(User owner, Activity activity, BaseProjectItem item, BaseProjectItem updatedItem) throws Throwable;
}
