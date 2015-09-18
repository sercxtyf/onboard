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
package com.onboard.service.collaboration.attach;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.onboard.domain.model.Activity;
import com.onboard.domain.model.Todo;
import com.onboard.domain.model.type.BaseProjectItem;
import com.onboard.service.activity.ActivityService;
import com.onboard.service.common.attach.AbstractAttachService;
import com.onboard.service.common.attach.IdentifiableAttachService;

/**
 * {@link IdentifiableAttachService} Todo与Activity关联服务实现
 * 
 * @author XingLiang
 * 
 */
@Service("todoActivityAttachServiceBean")
public class TodoActivityAttachServiceImpl extends AbstractAttachService {

    @Autowired
    ActivityService activityService;

    @Override
    public String attachType() {
        return new Todo().getType();
    }

    @Override
    public List<? extends BaseProjectItem> getIdentifiablesByAttachId(int attachId) {
        return activityService.getByTodo(attachId, 0, -1);
    }

    @Override
    public String modelType() {
        return new Activity().getType();
    }

}
