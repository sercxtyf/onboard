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
package com.onboard.service.common.subscrible.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.onboard.domain.mapper.SubscriberMapper;
import com.onboard.domain.mapper.UserMapper;
import com.onboard.domain.mapper.model.SubscriberExample;
import com.onboard.domain.model.Subscriber;
import com.onboard.domain.model.User;
import com.onboard.domain.model.type.Subscribable;
import com.onboard.service.common.identifiable.IdentifiableManager;
import com.onboard.service.common.subscrible.SubscriberService;

/**
 * {@link SubscriberService}接口实现
 * 
 * @author yewei
 * 
 */
@Transactional
@Service("subscriberServiceBean")
public class SubscriberServiceImpl implements SubscriberService {

    public static final Logger logger = LoggerFactory.getLogger(SubscriberServiceImpl.class);

    @Autowired
    private SubscriberMapper subscriberMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private IdentifiableManager identifiableManager;

    @Override
    public Subscriber getSubscriberById(int id) {
        return subscriberMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<Subscriber> getSubscribersByTopic(String subscribeType, int subscribeId) {

        Subscriber subscriber = new Subscriber();
        subscriber.setSubscribeType(subscribeType);
        subscriber.setSubscribeId(subscribeId);

        return subscriberMapper.selectByExample(new SubscriberExample(subscriber));

    }

    @Override
    public void fillSubcribers(Subscribable subscribable) {
        if (subscribable != null) {
            subscribable.setSubscribers(this.getSubscribeUsersByTopic(subscribable.getSubscribableType(),
                    subscribable.getSubscribableId()));
        }
    }

    @Override
    public List<User> getSubscribeUsersByTopic(String subscribeType, int subscribeId) {

        List<User> users = new ArrayList<User>();

        Subscriber subscriber = new Subscriber();
        subscriber.setSubscribeType(subscribeType);
        subscriber.setSubscribeId(subscribeId);

        List<Subscriber> subscribers = subscriberMapper.selectByExample(new SubscriberExample(subscriber));

        for (Subscriber s : subscribers) {
            User user = userMapper.selectByPrimaryKey(s.getUserId());
            if (user != null) {
                users.add(new User(user));
            }
        }

        return users;
    }

    @Override
    public int countByExample(Subscriber item) {
        SubscriberExample example = new SubscriberExample(item);
        return subscriberMapper.countByExample(example);
    }

    @Override
    public Subscriber createSubscriber(Subscriber subscriber) {
        try {
            subscriberMapper.insert(subscriber);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
        return subscriber;
    }

    @Override
    public Subscriber updateSubscriber(Subscriber subscriber) {
        subscriberMapper.updateByPrimaryKey(subscriber);
        return subscriber;
    }

    @Override
    public void deleteSubscriber(int id) {
        subscriberMapper.deleteByPrimaryKey(id);
    }

    @Override
    public void deleteSubscriberByExample(Subscriber subscriber) {
        subscriberMapper.deleteByExample(new SubscriberExample(subscriber));
    }

    @Override
    public void addSubscribers(Subscribable subscribable) {
        if (subscribable.getSubscribers() == null || subscribable.getSubscribers().size() == 0) {
            return;
        }
        for (User user : subscribable.getSubscribers()) {
            try {
                Subscriber subscriber = new Subscriber();
                subscriber.setSubscribeId(subscribable.getSubscribableId());
                subscriber.setSubscribeType(subscribable.getSubscribableType());
                subscriber.setUserId(user.getId());
                subscriberMapper.insert(subscriber);
            } catch (DuplicateKeyException e) {
                // do nothing
            }
        }

    }

    @Override
    public void generateSubscribers(Subscribable subscribable, User defaultUser) {
        List<User> subscribers = subscribable.getSubscribers();
        List<User> ret = new ArrayList<User>();
        if (defaultUser != null) {
            ret.add(userMapper.selectByPrimaryKey(defaultUser.getId()));
        }
        if (defaultUser != null && subscribers != null) {
            for (User subscriber : subscribers) {
                if (subscriber != null && subscriber.getId() != null && !subscriber.getId().equals(defaultUser.getId())) {
                    ret.add(userMapper.selectByPrimaryKey(subscriber.getId()));
                }
            }
        }
        subscribable.setSubscribers(ret);
    }

    @Override
    public Subscribable getSubscribleByTypeAndId(String type, Integer id) {
        return (Subscribable) identifiableManager.getIdentifiableByTypeAndId(type, id);
    }

    @Override
    public void updateSubscribers(Subscribable subscribable) {
        List<Subscriber> origin = getSubscribersByTopic(subscribable.getType(), subscribable.getSubscribableId());
        if (origin == null) {
            addSubscribers(subscribable);
        } else {
            List<User> update = subscribable.getSubscribers();
            if (update != null) {
                User example = new User();
                for (Subscriber subscriber : origin) {
                    example.setId(subscriber.getUserId());
                    if (!update.contains(example))
                        deleteSubscriber(subscriber.getId());
                    else
                        update.remove(example);
                }
                subscribable.setSubscribers(update);
                addSubscribers(subscribable);
            }
        }
    }
}
