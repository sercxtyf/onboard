package com.onboard.frontend.controller.api;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.onboard.frontend.exception.InternalException;
import com.onboard.frontend.service.net.impl.NetServiceImpl;

/**
 * Rest API for mobile APP.
 * 
 * @author XingLiang
 * 
 */
@RestController
public class MobileProxyController {

    private final Logger logger = LoggerFactory.getLogger(MobileProxyController.class);

    private final static String KERNEL_ERROR_FORMAT = "kernel response status:500, detail message is:\n%s";
    @Autowired
    private NetServiceImpl netService;

    @RequestMapping(value = "/api/mobile/**", method = RequestMethod.GET)
    @ResponseBody
    public String doGetProxy(HttpServletRequest request, HttpServletResponse response) {
        try {
            return netService.getForJson(request);
        } catch (InternalException e) {
            logger.error(String.format(KERNEL_ERROR_FORMAT, e.getMessage()));
            response.setStatus(500);
        }
        return null;
    }

    @RequestMapping(value = "/api/mobile/**", method = RequestMethod.POST)
    @ResponseBody
    public String doPostProxy(HttpServletRequest request, HttpServletResponse response) {
        try {
            return netService.postForJson(request);
        } catch (InternalException e) {
            logger.error(String.format(KERNEL_ERROR_FORMAT, e.getMessage()));
            response.setStatus(500);
        }
        return null;
    }

    @RequestMapping(value = "/api/mobile/**", method = RequestMethod.PUT)
    @ResponseBody
    public String doPutProxy(HttpServletRequest request, HttpServletResponse response) {
        try {
            return netService.putForJson(request);
        } catch (InternalException e) {
            logger.error(String.format(KERNEL_ERROR_FORMAT, e.getMessage()));
            response.setStatus(500);
        }
        return null;
    }

    @RequestMapping(value = "/api/mobile/**", method = RequestMethod.DELETE)
    @ResponseBody
    public void doDeleteProxy(HttpServletRequest request, HttpServletResponse response) {
        try {
            netService.deleteForJson(request);
        } catch (InternalException e) {
            logger.error(String.format(KERNEL_ERROR_FORMAT, e.getMessage()));
            response.setStatus(500);
        }
    }
}
