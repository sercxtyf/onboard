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
package com.onboard.domain.model;

import com.onboard.domain.mapper.model.SubscriberObject;
import com.onboard.domain.model.type.Typeable;

/**
 * 领域模型：Subscriber
 * 
 * @author yewei
 * 
 */
public class Subscriber extends SubscriberObject implements Typeable {

    public Subscriber() {
        super();
    }

    public Subscriber(int id) {
        super(id);
    }

    public Subscriber(SubscriberObject obj) {
        super(obj);
    }

    @Override
    public String getType() {
        return "subscriber";
    }

}
