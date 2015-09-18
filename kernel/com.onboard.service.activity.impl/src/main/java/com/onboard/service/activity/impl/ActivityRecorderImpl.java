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
package com.onboard.service.activity.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.onboard.domain.mapper.ActivityMapper;
import com.onboard.domain.mapper.ProjectMapper;
import com.onboard.domain.mapper.model.ActivityExample;
import com.onboard.domain.model.Activity;
import com.onboard.domain.model.Project;
import com.onboard.domain.model.type.BaseProjectItem;
import com.onboard.service.activity.ActivityGenerator;
import com.onboard.service.activity.ActivityHook;
import com.onboard.service.activity.ActivityRecorder;
import com.onboard.service.activity.SynchronizedActivityHook;
import com.onboard.service.activity.exception.ActivityRecorderException;
import com.onboard.service.activity.impl.util.ActivityHookHelper;
import com.onboard.service.activity.util.ActivityHelper;
import com.onboard.service.web.SessionService;

/**
 * {@link ActivityRecorder}的实现
 * 
 * @author yewei
 * 
 */
@Service("activityRecorderBean")
public class ActivityRecorderImpl implements ActivityRecorder {

    private static final Logger logger = LoggerFactory.getLogger(ActivityRecorderImpl.class);

    @Autowired
    private ActivityMapper activityMapper;

    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    ActivityHookHelper activityHookHelper;

    @Autowired
    private SessionService session;

    private static final Map<String, ActivityGenerator> activityGenerators = Collections
            .synchronizedMap(new HashMap<String, ActivityGenerator>());

    private static final Map<String, String> service2ModelMap = Collections.synchronizedMap(new HashMap<String, String>());

    private static final List<ActivityHook> activityHooks = Collections.synchronizedList(new ArrayList<ActivityHook>());

    private static final List<SynchronizedActivityHook> synchronizedActivityHooks = Collections
            .synchronizedList(new ArrayList<SynchronizedActivityHook>());

    public synchronized void addActivityGenerator(ActivityGenerator activityGenerator) {
        if (activityGenerator != null) {
            activityGenerators.put(activityGenerator.modelType(), activityGenerator);
            service2ModelMap.put(activityGenerator.modelService(), activityGenerator.modelType());
        }
    }

    public synchronized void removeActivityGenerator(ActivityGenerator activityGenerator) {
        if (activityGenerator != null) {
            activityGenerators.remove(activityGenerator.modelType());
            service2ModelMap.remove(activityGenerator.modelService());
        }
    }

    public synchronized void addSynchronizedActivityHook(SynchronizedActivityHook synchronizedActivityHook) {
        synchronizedActivityHooks.add(synchronizedActivityHook);
    }

    public synchronized void removeSynchronizedActivityHook(SynchronizedActivityHook synchronizedActivityHook) {
        synchronizedActivityHooks.remove(synchronizedActivityHook);
    }

    public synchronized void addActivityHook(ActivityHook activityHook) {
        activityHooks.add(activityHook);
    }

    public synchronized void removeActivityHook(ActivityHook activityHook) {
        activityHooks.remove(activityHook);
    }

    private void callActivityHook(Activity activity, BaseProjectItem item, BaseProjectItem updatedItem) {

        for (SynchronizedActivityHook synchronizedActivityHook : synchronizedActivityHooks) {
            activityHookHelper.callOneSynchronizedActivityHook(synchronizedActivityHook, activity, item, updatedItem);
        }

        for (ActivityHook activityHook : activityHooks) {
            activityHookHelper.callOneAsynchronizedActivityHook(activityHook, session.getCurrentUser(), activity, item,
                    updatedItem);
        }

    }

    /**
     * 检查UserSession里的值是否正常，赋给传入的Activity并返回
     * 
     * @return Activity
     * @throws ActivityRecorderException
     */
    private Activity enrichActivity(Activity activity) throws ActivityRecorderException {

        Integer userId = session.getCurrentUser().getId();

        if (userId == null || activity.getProjectId() == null) {
            logger.warn("Fail to generate activity");
            throw new ActivityRecorderException("User ID or Project ID is null");
        }

        activity.setCreatorName(session.getCurrentUser().getName());
        activity.setCreatorId(userId);
        if (activity.getCreatorAvatar() == null || activity.getCreatorAvatar().length() < 1) {
            activity.setCreatorAvatar("/avatar/default.png");
        }
        activity.setCreated(new Date());
        activity.setTarget(ActivityHelper.cutoffActivityTitle(activity.getTarget()));

        if (activity.getProjectName() == null) {
            Project project = projectMapper.selectByPrimaryKey(activity.getProjectId());
            if (project != null) {
                activity.setProjectName(project.getName());
            }
        }

        return activity;
    }

    /**
     * 记录创建活动
     * 
     * @param item
     *            创建的对象
     */
    @Override
    public void recordCreationActivity(BaseProjectItem item) {
        try {
            ActivityGenerator activityGenerator = activityGenerators.get(item.getType());

            if (activityGenerator != null) {
                Activity activity = activityGenerator.generateCreateActivity(item);
                if (activity != null) {
                    activity = this.enrichActivity(activity);
                    activityMapper.insert(activity);
                    this.callActivityHook(activity, item, null);
                }
            }
        } catch (ActivityRecorderException e) {
            logger.error("Fail to log activity:", e);
        }
    }

    /**
     * 记录更新活动
     * 
     * @param item
     *            更新的对象
     */
    @Override
    public Object recordUpdateActivity(ProceedingJoinPoint joinpoint) {
        BaseProjectItem original = (BaseProjectItem) joinpoint.getArgs()[0];
        if (original == null) {
            return null;
        }
        ActivityGenerator activityGenerator = activityGenerators.get(original.getType());
        if (activityGenerator != null) {
            original = activityGenerator.enrichModel(original);
        }

        try {
            BaseProjectItem updated = (BaseProjectItem) joinpoint.proceed();
            if (activityGenerator != null) {
                // 填充updated
                updated = activityGenerator.enrichModel(updated);
                Activity activity = activityGenerator.generateUpdateActivity(original, updated);
                if (activity != null) {
                    activity = this.enrichActivity(activity);
                    activityMapper.insert(activity);
                    this.callActivityHook(activity, original, updated);
                }
            }

            return updated;
        } catch (ActivityRecorderException e) {
            logger.error("Fail to log activity:", e);
        } catch (Throwable t) {
            logger.error("Fail to log activity:", t);
        }
        return original;
    }

    @Override
    public Object deleteActivity(ProceedingJoinPoint joinpoint) {
        Object returnVal = null;
        String modelType = null;

        Class<?>[] serviceTypes = joinpoint.getTarget().getClass().getInterfaces();
        for (Class<?> clazz : serviceTypes) {
            if (service2ModelMap.get(clazz.getName()) != null) {
                modelType = service2ModelMap.get(clazz.getName());
                break;
            }
        }

        if (modelType != null) {
            Activity activity = new Activity();
            activity.setAttachType(modelType);
            activity.setAttachId((Integer) joinpoint.getArgs()[0]);
            activityMapper.deleteByExample(new ActivityExample(activity));
            /**
             * TODO delete notifications which reference the activity
             */
        }

        try {
            returnVal = joinpoint.proceed();
        } catch (Throwable e) {
            logger.error("fail to delete item: ", e);
        }

        return returnVal;
    }
}
