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
 * 更新操作和新建操作处理逻辑完全一样的{@link ActivityHook}
 * 
 * @author yewei
 *
 */
public abstract class SimpleActivityHook implements ActivityHook {

    @Override
    public abstract void whenCreationActivityCreated(User owner, Activity activity, BaseProjectItem item) throws Throwable;

    @Override
    public void whenUpdateActivityCreated(User owner, Activity activity, BaseProjectItem item, BaseProjectItem updatedItem) throws Throwable {
        whenCreationActivityCreated(owner, activity, updatedItem);
    }

}

