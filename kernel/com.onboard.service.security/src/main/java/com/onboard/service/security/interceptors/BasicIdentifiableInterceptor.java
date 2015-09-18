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
package com.onboard.service.security.interceptors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.elevenframework.web.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.onboard.domain.model.User;
import com.onboard.domain.model.type.BaseProjectItem;
import com.onboard.service.common.identifiable.IdentifiableManager;
import com.onboard.service.security.exception.NoPermissionException;
import com.onboard.service.security.utils.SecurityUtils;
import com.onboard.service.web.SessionService;

/**
 * 对{@link BaseProjectItem}对象进行基本验证的Interceptor
 * 
 * @author yewei
 * 
 */
public class BasicIdentifiableInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    protected SessionService session;

    @Autowired
    protected IdentifiableManager identifiableManager;

    public static final Logger logger = LoggerFactory.getLogger(BasicIdentifiableInterceptor.class);

    /**
     * 子类覆盖该方法进行特定的验证
     * 
     * @param identifiable
     * @return
     */
    public boolean modelCheck(BaseProjectItem identifiable, User user) {
        return true;
    }

    /**
     * 子类覆盖该方法进行特定的验证
     * 
     * @param identifiable
     * @return
     */
    public boolean roleCheck(Integer companyId, Integer projectId, User user) {
        return true;
    }

    private BaseProjectItem modelExisted(HttpServletRequest request) throws ResourceNotFoundException {
        BaseProjectItem identifiable = null;

        String modelType = extractIdentifiableType(request);
        if (modelType != null && identifiableManager.identifiableRegistered(modelType)) {
            Integer id = SecurityUtils.getIntegerValueOfPathVariable(request, modelType + "Id");
            // identifiable 的id存在
            if (id != null) {
                // identifiable 的id值非法或者id值不存在
                if (id < 0 || getIdentifiable(request, modelType, id) == null) {
                    throw new ResourceNotFoundException();
                } else {
                    // TODO: mod BaseProjectItem to BaseOperateItem
                    identifiable = (BaseProjectItem) identifiableManager.getIdentifiableByTypeAndId(modelType, id);
                }
            }
        }

        return identifiable;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // logger.info("Prehandle...");
        if (session.getCurrentUser() == null) {
            return false;
        }

        // 如果是特定identifiable相关的操作，则会返回该identifiable实例
        BaseProjectItem identifiable = modelExisted(request);

        Integer companyId = SecurityUtils.getIntegerValueOfPathVariable(request, "companyId");
        if (identifiable != null && !modelCheck(identifiable, session.getCurrentUser())) {
            throw new NoPermissionException(companyId);
        }

        if (!roleCheck(request)) {
            throw new NoPermissionException(companyId);
        }

        return super.preHandle(request, response, handler);
    }

    /**
     * 提取对象类型，URL为/{companyId}/{projects}/{projectId}/{domainType}
     * 
     * @param request
     * @return
     */
    private String extractIdentifiableType(HttpServletRequest request) {
        String path = request.getRequestURI();

        String[] url = path.split("/");

        if (url.length < 5) {
            return null;
        }
        /**
         * TODO: 复数的处理
         */
        String modelType = url[4];
        if (modelType.length() == 0) {
            throw new ResourceNotFoundException();
        }
        if (modelType.charAt(modelType.length() - 1) == 's') {
            modelType = modelType.substring(0, modelType.length() - 1);
        }
        return modelType;
    }

    protected boolean roleCheck(HttpServletRequest request) {
        Integer companyId = SecurityUtils.getIntegerValueOfPathVariable(request, "companyId");
        Integer projectId = SecurityUtils.getIntegerValueOfPathVariable(request, "projectId");
        return roleCheck(companyId, projectId, session.getCurrentUser());
    }

    /**
     * 验证对象是否存在于请求所在的团队和项目中
     * 
     * @param request
     * @return
     */
    private BaseProjectItem getIdentifiable(HttpServletRequest request, String modelType, Integer id) {

        Integer companyId = SecurityUtils.getIntegerValueOfPathVariable(request, "companyId");
        Integer projectId = SecurityUtils.getIntegerValueOfPathVariable(request, "projectId");

        if (companyId == null || projectId == null) {
            return null;
        }

        // TODO: mod BaseProjectItem to BaseOperateItem
        BaseProjectItem identifiable = (BaseProjectItem) identifiableManager.getIdentifiableByTypeAndId(modelType, id);

        if (identifiable != null && identifiable.getCompanyId().equals(companyId)
                && identifiable.getProjectId().equals(projectId)) {
            return identifiable;
        }

        return null;

    }
}
