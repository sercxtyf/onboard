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
package com.onboard.service.websocket;

import java.io.IOException;

import org.apache.catalina.websocket.MessageInbound;

import com.onboard.domain.model.Activity;
import com.onboard.domain.model.Notification;
import com.onboard.domain.model.User;

/**
 * 提供WebSocket相关的服务
 * 
 * @author Gourui
 * 
 */
public interface WebSocketService {

	/**
	 * 注册一个用户页面的WebSocket客户端
	 * @param user 用户对象
	 * @param inbound
	 */
    public void registerClient(String user, MessageInbound inbound);

    /**
     * 注销一个用户页面的WebSocket客户端
     * @param user 用户端想
     * @param inbound
     */
    public void unregisterClient(String user, MessageInbound inbound);

    /**
     * 将活动对象发送给某个特定用户的所有页面
     * @param user 目标用户对象
     * @param activity 需要发送的活动对象
     */
    public void broadcastOne(String user, Activity activity);
    
    /**
     * 将文本信息发送给某个特定用户的所有页面
     * @param user 目标用户对象
     * @param message 需要发送的文本信息
     */
    public void broadcastOne(String user, String message);

    /**
     * 将对象发送给某个特定用户的所有页面
     * @param user 目标用户对象
     * @param object 需要发送的对象
     */
    public void broadcastOne(User user, Object object);

    /**
     * 将活动对象发送给所有连接了WebSocket的用户的所有页面
     * @param activity 需要发送的活动对象
     */
    public void broadcastAll(Activity activity);

    /**
     * 将文本信息发送给某个特定页面
     * @param inbound 页面信息
     * @param message 需要发送的文本信息
     * @throws IOException
     */
    public void sendToPage(MessageInbound inbound, String message) throws IOException;

    /**
     * 将通知对象发送给某个特定用户的所有页面
     * @param user 目标用户对象
     * @param notification 需要发送的通知对象
     */
    void broadcastOne(String user, Notification notification);

}
