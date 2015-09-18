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
package com.onboard.service.common.test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.Lists;
import com.onboard.domain.mapper.SubscriberMapper;
import com.onboard.domain.mapper.UserMapper;
import com.onboard.domain.mapper.model.SubscriberExample;
import com.onboard.domain.mapper.model.common.BaseExample;
import com.onboard.domain.model.Subscriber;
import com.onboard.domain.model.User;
import com.onboard.domain.model.type.Subscribable;
import com.onboard.service.common.identifiable.IdentifiableManager;
import com.onboard.service.common.subscrible.impl.SubscriberServiceImpl;
import com.onboard.test.exampleutils.CriterionVerifier;
import com.onboard.test.exampleutils.ExampleMatcher;
import com.onboard.test.exampleutils.ObjectMatcher;
import com.onboard.test.moduleutils.ModuleHelper;

@RunWith(MockitoJUnitRunner.class)
public class SubscriberServiceImplTest {

    private Subscriber subscriber;

    @Mock
    private SubscriberMapper mockedSubscriberMapper;

    @Mock
    private UserMapper mockedUserMapper;

    @Mock
    private IdentifiableManager mockedIdentifiableManager;

    @Mock
    private Subscribable subscribable1;

    @Mock
    private Subscribable subscribable2;

    @Mock
    private Subscribable subscribable3;

    @InjectMocks
    private SubscriberServiceImpl subscriberServiceImpl;

    public static Subscriber getASampleSubscriber() {
        Subscriber subscriber = new Subscriber();
        subscriber.setCompanyId(ModuleHelper.companyId);
        subscriber.setId(ModuleHelper.id);
        subscriber.setSubscribeId(ModuleHelper.id);
        subscriber.setSubscribeType(ModuleHelper.type);
        subscriber.setUserId(ModuleHelper.userId);
        return subscriber;
    }

    public static User getASampleUser() {
        User user = new User();
        user.setId(ModuleHelper.id);
        return user;
    }

    public static List<User> getASampleUsers() {
        return Lists.newArrayList(getASampleUser());
    }

    public static List<Subscriber> getASampleSubscribers() {
        List<Subscriber> subscriberList = new ArrayList<Subscriber>();
        subscriberList.add(getASampleSubscriber());
        return subscriberList;
    }

    @Before
    public void setup() {
        subscriber = getASampleSubscriber();
        when(mockedSubscriberMapper.selectByPrimaryKey(Mockito.anyInt())).thenReturn(subscriber);
        when(mockedSubscriberMapper.selectByExample(Mockito.any(SubscriberExample.class))).thenReturn(getASampleSubscribers());
        when(mockedSubscriberMapper.countByExample(Mockito.any(SubscriberExample.class))).thenReturn(ModuleHelper.count);
        when(mockedUserMapper.selectByPrimaryKey(Mockito.anyInt())).thenReturn(getASampleUser());
        when(subscribable1.getSubscribers()).thenReturn(getASampleUsers());
        when(subscribable1.getSubscribableId()).thenReturn(ModuleHelper.id);
        when(subscribable1.getSubscribableType()).thenReturn(ModuleHelper.type);
        when(subscribable1.getId()).thenReturn(ModuleHelper.userId);
        when(subscribable2.getSubscribers()).thenReturn(null);
        when(subscribable3.getSubscribers()).thenReturn(Lists.newArrayList(new User(ModuleHelper.userId)));
        when(subscribable3.getSubscribableId()).thenReturn(ModuleHelper.id);
        when(subscribable3.getSubscribableType()).thenReturn(ModuleHelper.type);
        when(subscribable3.getId()).thenReturn(ModuleHelper.userId);
        when(mockedIdentifiableManager.getIdentifiableByTypeAndId(Mockito.anyString(), Mockito.anyInt())).thenReturn(
                subscribable1);
    }

    private void runAssert(Subscriber subscriber) {
        assertEquals(ModuleHelper.companyId, (int) subscriber.getCompanyId());
        assertEquals(ModuleHelper.id, (int) subscriber.getId());
        assertEquals(ModuleHelper.id, (int) subscriber.getSubscribeId());
        assertEquals(ModuleHelper.type, subscriber.getSubscribeType());
        assertEquals(ModuleHelper.userId, (int) subscriber.getUserId());
    }

    @Test
    public void getSusbcriberByIdTest() {
        Subscriber result = subscriberServiceImpl.getSubscriberById(ModuleHelper.id);
        verify(mockedSubscriberMapper, Mockito.times(1)).selectByPrimaryKey(Mockito.anyInt());
        runAssert(result);
    }

    @Test
    public void getSubsciberByTopicTest() {
        List<Subscriber> result = subscriberServiceImpl.getSubscribersByTopic(ModuleHelper.type, ModuleHelper.id);
        verify(mockedSubscriberMapper).selectByExample(Mockito.argThat(new ExampleMatcher<SubscriberExample>() {

            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "subscribeType", ModuleHelper.type)
                        && CriterionVerifier.verifyEqualTo(example, "subscribeId", ModuleHelper.id);
            }
        }));
        for (Subscriber subscriber : result) {
            runAssert(subscriber);
        }
    }

    @Test
    public void getSubscribeUserByTopicTest() {
        List<User> result = subscriberServiceImpl.getSubscribeUsersByTopic(ModuleHelper.type, ModuleHelper.id);
        verify(mockedSubscriberMapper).selectByExample(Mockito.argThat(new ExampleMatcher<SubscriberExample>() {

            @Override
            public boolean matches(BaseExample example) {
                return CriterionVerifier.verifyEqualTo(example, "subscribeType", ModuleHelper.type)
                        && CriterionVerifier.verifyEqualTo(example, "subscribeId", ModuleHelper.id);
            }
        }));
        verify(mockedUserMapper, Mockito.times(result.size())).selectByPrimaryKey(ModuleHelper.userId);
        assertEquals(1, result.size());
        for (User user : result) {
            assertEquals(ModuleHelper.id, (int) user.getId());
        }
    }

    @Test
    public void countByExampleTest() {
        int result = subscriberServiceImpl.countByExample(getASampleSubscriber());
        verify(mockedSubscriberMapper).countByExample(Mockito.any(SubscriberExample.class));
        assertEquals(ModuleHelper.count, result);
    }

    @Test
    public void createSubscriberTest() {
        Subscriber result = subscriberServiceImpl.createSubscriber(getASampleSubscriber());
        verify(mockedSubscriberMapper).insert(Mockito.any(Subscriber.class));
        runAssert(result);
    }

    @Test
    public void updateSubscriberTest() {
        Subscriber result = subscriberServiceImpl.updateSubscriber(getASampleSubscriber());
        verify(mockedSubscriberMapper).updateByPrimaryKey(Mockito.any(Subscriber.class));
        runAssert(result);
    }

    @Test
    public void deleteSubscriberTest() {
        subscriberServiceImpl.deleteSubscriber(ModuleHelper.id);
        verify(mockedSubscriberMapper).deleteByPrimaryKey(ModuleHelper.id);
    }

    @Test
    public void deleteSubscriberByExampleTest() {
        subscriberServiceImpl.deleteSubscriberByExample(getASampleSubscriber());
        verify(mockedSubscriberMapper).deleteByExample(Mockito.any(SubscriberExample.class));
    }

    @Test
    public void addSubscriberTest() {
        subscriberServiceImpl.addSubscribers(subscribable2);
        verify(subscribable2, Mockito.times(0)).getSubscribableType();
        subscriberServiceImpl.addSubscribers(subscribable1);
        verify(mockedSubscriberMapper).insert(Mockito.argThat(new ObjectMatcher<Subscriber>() {

            public boolean verifymatches(Subscriber item) {
                return item.getSubscribeId().equals(ModuleHelper.id) && item.getSubscribeType().equals(ModuleHelper.type)
                        && item.getUserId().equals(ModuleHelper.id);
            }
        }));
    }

    @Test
    public void generateSubscribersTest() {
        subscriberServiceImpl.generateSubscribers(subscribable2, null);
        verify(mockedUserMapper, Mockito.times(0)).selectByPrimaryKey(Mockito.anyInt());
        subscriberServiceImpl.generateSubscribers(subscribable2, getASampleUser());
        verify(mockedUserMapper, Mockito.times(1)).selectByPrimaryKey(Mockito.anyInt());
        subscriberServiceImpl.generateSubscribers(subscribable1, getASampleUser());
        verify(mockedUserMapper, Mockito.times(2)).selectByPrimaryKey(Mockito.anyInt());
        subscriberServiceImpl.generateSubscribers(subscribable3, getASampleUser());
        verify(mockedUserMapper, Mockito.times(4)).selectByPrimaryKey(Mockito.anyInt());
    }

    @Test
    public void getSubscribleByTypeAndId() {
        Subscribable result = subscriberServiceImpl.getSubscribleByTypeAndId(ModuleHelper.type, ModuleHelper.id);
        verify(mockedIdentifiableManager).getIdentifiableByTypeAndId(ModuleHelper.type, ModuleHelper.id);
        assertEquals(ModuleHelper.userId, (int) result.getId());
        assertEquals(ModuleHelper.type, result.getSubscribableType());
        assertEquals(ModuleHelper.id, (int) result.getSubscribableId());
    }

    @Test
    public void updateSubscribersTest() {
        subscriberServiceImpl.updateSubscribers(subscribable1);
        verify(mockedSubscriberMapper).insert(Mockito.any(Subscriber.class));
    }
}
