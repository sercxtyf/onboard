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
package com.onboard.domain.model.type;

import com.onboard.domain.model.User;

/**
 * 可以进看板的对象
 * 
 * @author xr
 * 
 */
public interface Boardable {

    /**
     * 在看板内的状态
     * 
     * @return
     */
    String getIterationStatus();

    /**
     * 责任人
     * 
     * @return
     */
    User getAssignee();

    /**
     * 设置责任人
     * 
     * @param user
     */
    void setAssignee(User user);

}
