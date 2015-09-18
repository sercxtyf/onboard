package com.onboard.web.admin.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Lists;
import com.onboard.domain.transform.CompanyApplicationTransform;
import com.onboard.dto.CompanyApplicationDTO;
import com.onboard.service.collaboration.CompanyApplicationService;

@Controller
@RequestMapping("/api/trials")
public class TrialApiController {

    private static final int NUM_PER_PAGE = 20;

    @Autowired
    private CompanyApplicationService companyApplicationService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    @ResponseBody
    public List<CompanyApplicationDTO> companyApplications(@RequestParam(value = "page",
            required = false,
            defaultValue = "1") int page) {
        return Lists.transform(
                companyApplicationService.getCompanyApplications((page - 1) * NUM_PER_PAGE, NUM_PER_PAGE),
                CompanyApplicationTransform.TO_DTO_FUNCTION);
    }
}
