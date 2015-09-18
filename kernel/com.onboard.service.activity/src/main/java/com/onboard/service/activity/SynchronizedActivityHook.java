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
import com.onboard.domain.model.type.BaseProjectItem;

/**
 * 创建完{@link Activity}进行同步回调的接口
 * 
 * 与{@link ActivityHook}不同的, {@link SynchronizedActivityHook}的回调是同步的
 * 
 * 大部分情况下，应该使用{@link ActivityHook}
 * 
 * 如果使用{@link SynchronizedActivityHook}可能造成请求相应的速度问题
 * 
 * @author yewei
 * 
 */

public interface SynchronizedActivityHook {
    /**
     * 创建完{@link Activity}时, {@link ActivityRecorder}会调用ActivityHook
     * 
     * @param activity
     *            被创建出来的Activity
     * @param item
     *            Activity关联的新创建的对象
     * @throws Throwable
     */
    void whenCreationActivityCreated(Activity activity, BaseProjectItem item) throws Throwable;

    /**
     * 创建完{@link Activity}时, {@link ActivityRecorder}会调用ActivityHook
     * 
     * @param activity
     *            被创建出来的Activity
     * @param item
     *            Activity关联的更新前的对象，已填充字段
     * @param updatedItem
     *            Activity关联的更新后的对象，已填充字段
     * @throws Throwable
     */
    void whenUpdateActivityCreated(Activity activity, BaseProjectItem item, BaseProjectItem updatedItem) throws Throwable;

}
