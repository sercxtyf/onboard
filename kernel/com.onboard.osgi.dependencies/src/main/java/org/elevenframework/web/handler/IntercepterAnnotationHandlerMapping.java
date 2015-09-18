package org.elevenframework.web.handler;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.elevenframework.web.interceptor.Interceptors;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

public class IntercepterAnnotationHandlerMapping extends RequestMappingHandlerMapping {

    @Override
    protected HandlerExecutionChain getHandlerExecutionChain(Object handler, HttpServletRequest request) {
        HandlerExecutionChain chain = super.getHandlerExecutionChain(handler, request);
        HandlerMethod hm = (HandlerMethod) handler;
        HandlerInterceptor[] interceptors = detectInterceptors(hm.getMethod());
        chain.addInterceptors(interceptors);
        return chain;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private List<HandlerInterceptor> getHandlerInterceptors(Class<? extends Object> clazz,
            Interceptors interceptorAnnotation) {
        List<HandlerInterceptor> interceptors = new ArrayList<HandlerInterceptor>();

        if (interceptorAnnotation != null) {
            Class[] interceptorClasses = interceptorAnnotation.value();
            if (interceptorClasses != null) {
                for (Class interceptorClass : interceptorClasses) {
                    if (!HandlerInterceptor.class.isAssignableFrom(interceptorClass)) {
                        raiseIllegalInterceptorValue(clazz, interceptorClass);
                    }
                    interceptors.add((HandlerInterceptor) getApplicationContext().getBean(interceptorClass));
                }
            }
        }

        return interceptors;
    }

    private HandlerInterceptor[] detectInterceptors(Method method) {
        Class<? extends Object> clazz = method.getDeclaringClass();
        Interceptors classAnnotations = AnnotationUtils.findAnnotation(clazz, Interceptors.class);
        Interceptors methodAnnotations = AnnotationUtils.findAnnotation(method, Interceptors.class);
        List<HandlerInterceptor> interceptors = getHandlerInterceptors(clazz, classAnnotations);
        interceptors.addAll(getHandlerInterceptors(clazz, methodAnnotations));
        return interceptors.toArray(new HandlerInterceptor[0]);
    }

    @SuppressWarnings("rawtypes")
    private void raiseIllegalInterceptorValue(Class<? extends Object> clazz, Class interceptorClass) {
        throw new IllegalArgumentException(interceptorClass + " specified on " + clazz + " does not implement "
                + HandlerInterceptor.class.getName());
    }

}
