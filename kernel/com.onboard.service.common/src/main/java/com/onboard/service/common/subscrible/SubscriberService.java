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
package com.onboard.service.common.subscrible;

import java.util.List;

import com.onboard.domain.model.Subscriber;
import com.onboard.domain.model.User;
import com.onboard.domain.model.type.Subscribable;

/**
 * {@link Subscriber}服务接口
 * 
 * @author yewei
 * 
 */
public interface SubscriberService {
    /**
     * 根据主键获取订阅对象
     * @param id 目标订阅对象的主键
     * @return 按要求从数据库中获取出的订阅对象
     */
    Subscriber getSubscriberById(int id);

    /**
     * 获取一个可订阅对象的所有订阅用户
     * @param subscribeType 可订阅对象的的类型
     * @param subscribeId 可订阅对象的的主键
     * @return
     */
    List<User> getSubscribeUsersByTopic(String subscribeType, int subscribeId);

    /**
     * 根据样例对象获取符合条件的订阅对象的数量
     * @param item 样例对象
     * @return 按要求从数据库中获取出的订阅对象的数量
     */
    int countByExample(Subscriber item);

    /**
     * 填充一个可订阅对象的订阅者列表域
     * @param subscribable 需要被填充的可订阅对象域
     */
    void fillSubcribers(Subscribable subscribable);

    /**
     * 为一个可订阅对象在数据库中添加订阅者
     * @param subscribable 包含了订阅者信息的可订阅对象
     */
    void addSubscribers(Subscribable subscribable);

    /**
     * 获取一个可订阅对象的所有订阅的列表
     * @param subscribeType 可订阅对象的的类型
     * @param subscribeId 话可订阅对象的的主键
     * @return 按要求从数据库中获取出的订阅的列表
     */
    List<Subscriber> getSubscribersByTopic(String subscribeType, int subscribeId);

    /**
     * 在数据库中创建一个订阅对象
     * @param bubscriber  需要被添加进数据库的订阅对象
     * @return 返回创建的订阅对象，包括数据库中的id
     */
    Subscriber createSubscriber(Subscriber bubscriber);

    /**
     * 在数据库中更新一个订阅对象
     * @param bubscriber 需要被更新的订阅对象
     * @return 更新好的订阅对象
     */
    Subscriber updateSubscriber(Subscriber bubscriber);

    /**
     * 在数据库中删除一个订阅对象
     * @param id 需要被删除的订阅对象的主键
     */
    void deleteSubscriber(int id);

    /**
     * 在数据库中删除一个订阅对象
     * @param subscriber 需要被删除的订阅对象
     */
    void deleteSubscriberByExample(Subscriber subscriber);

    /**
     * 填充一个可订阅对象的订阅者列表，同时需要包含当前用户
     * @param subscribable 需要被填充的可订阅对象域
     * @param defaultUser 当前用户
     */
    void generateSubscribers(Subscribable subscribable, User defaultUser);

    /**
     * 根据类型和主键获取一个可订阅对象
     * @param type 可订阅对象的类型
     * @param id 可订阅对象的主键
     * @return 按要求从数据库中获取出可订阅对象
     */
    Subscribable getSubscribleByTypeAndId(String type, Integer id);

    /**
     * 为一个可订阅对象在数据库中更新订阅者
     * @param subscribable 包含了更新后订阅者信息的可订阅对象
     */
    void updateSubscribers(Subscribable subscribable);

}
