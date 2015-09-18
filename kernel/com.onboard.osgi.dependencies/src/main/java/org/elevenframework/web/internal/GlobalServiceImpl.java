package org.elevenframework.web.internal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.elevenframework.web.GlobalService;
import org.springframework.stereotype.Service;

@Service("globalServiceBean")
public class GlobalServiceImpl implements GlobalService {

    public static final String SESSION_KEY = "session";

    private static InheritableThreadLocal<ThreadVariables> THREAD_VARIABLES = new InheritableThreadLocal<ThreadVariables>() {

        @Override
        protected ThreadVariables initialValue() {
            return new ThreadVariables();
        }

    };

    public static void setUpHttpSession(HttpServletRequest request) {
        THREAD_VARIABLES.get().put(SESSION_KEY, request.getSession());
    }

    public static void destroy() {
        THREAD_VARIABLES.remove();
    }

    @Override
    public void set(String key, Object object) {
        if (key.equals(SESSION_KEY)) {
            throw new IllegalArgumentException("the key of global service should not be session");
        }
        THREAD_VARIABLES.get().put(key, object);
    }

    @Override
    public Object get(String key) {
        return THREAD_VARIABLES.get().get(key);
    }

    @Override
    public HttpSession getSession() {
        return (HttpSession) THREAD_VARIABLES.get().get(SESSION_KEY);
    }

}
