package org.elevenframework.web.listener;

import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.http.HttpServletRequest;

import org.elevenframework.web.internal.GlobalServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestContextListener implements ServletRequestListener {

    private static final Logger logger = LoggerFactory.getLogger(RequestContextListener.class);

    @Override
    public void requestDestroyed(ServletRequestEvent requestEvent) {
        GlobalServiceImpl.destroy();
    }

    @Override
    public void requestInitialized(ServletRequestEvent requestEvent) {
        if (!(requestEvent.getServletRequest() instanceof HttpServletRequest)) {
            logger.debug("error type: " + requestEvent.getServletRequest().getClass().getName());
            throw new IllegalArgumentException("Request is not an HttpServletRequest: " + requestEvent.getServletContext());
        }

        HttpServletRequest request = (HttpServletRequest) requestEvent.getServletRequest();
        GlobalServiceImpl.setUpHttpSession(request);
    }

}
