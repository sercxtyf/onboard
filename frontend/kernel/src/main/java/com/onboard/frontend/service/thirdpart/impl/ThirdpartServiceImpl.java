package com.onboard.frontend.service.thirdpart.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.onboard.frontend.model.ResponseMap;
import com.onboard.frontend.service.net.impl.NetServiceImpl;
import com.onboard.frontend.service.thirdpart.ThirdpartService;

@Service
public class ThirdpartServiceImpl implements ThirdpartService {
    @Autowired
    private NetServiceImpl netService;

    public Map<String, String> thirdPartAuthenticateRepository(int companyId, int projectId, int userId, String code) {
        return netService.getForObject(String.format(GITHUBCALLBACK, companyId, projectId, userId, code), ResponseMap.class);
    }
}
