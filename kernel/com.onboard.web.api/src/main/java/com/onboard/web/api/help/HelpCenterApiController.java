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
package com.onboard.web.api.help;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.Valid;

import org.elevenframework.web.interceptor.Interceptors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.Lists;
import com.onboard.domain.model.HelpTip;
import com.onboard.domain.transform.HelpTransForm;
import com.onboard.dto.HelpTipDTO;
import com.onboard.service.help.HelpTipService;
import com.onboard.service.security.interceptors.ManagerRequired;
import com.onboard.web.api.exception.BadRequestException;
import com.onboard.web.api.form.HelpTipForm;
import com.onboard.web.api.form.UpdateHelpTipForm;

@RequestMapping(value = "/helpTips")
@Controller
public class HelpCenterApiController {

    @Autowired
    private HelpTipService helpTipService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, ?> getHelpTips() {
        Builder<String, Object> builder = ImmutableMap.builder();

        List<HelpTip> helpTips = helpTipService.getHelpTips(0, -1);

        Map<String, List<HelpTip>> helpTipsMap = helpTipService.groupHelpTipsByTitle(helpTips);
        Map<String, List<HelpTipDTO>> helpTipDTOsMap = makeHelpTipsMapSerilizable(helpTipsMap);

        builder.put("helpTipDTOsMap", helpTipDTOsMap);
        return builder.build();

    }

    private Map<String, List<HelpTipDTO>> makeHelpTipsMapSerilizable(Map<String, List<HelpTip>> map) {
        Map<String, List<HelpTipDTO>> mapDTO = new HashMap<String, List<HelpTipDTO>>();

        Set<String> titles = map.keySet();
        for (String title : titles) {
            List<HelpTip> helpTips = map.get(title);
            mapDTO.put(title, Lists.transform(helpTips, HelpTransForm.HELPTIP_DTO_FUNCTION));
        }
        return mapDTO;
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    @Interceptors(ManagerRequired.class)
    @ResponseBody
    public HelpTipDTO newHelpTip(@Valid @RequestBody HelpTipForm helpTipForm) {

        HelpTip helpTip = new HelpTip();
        helpTip.setTitle(helpTipForm.getTitle());
        helpTip.setQuestion(helpTipForm.getQuestion());
        helpTip.setAnswer(helpTipForm.getAnswer());

        return HelpTransForm.helpToHelpDTO(helpTipService.createHelpTip(helpTip));

    }

    @RequestMapping(value = "/{helpTipId}", method = RequestMethod.PUT)
    @Interceptors(ManagerRequired.class)
    @ResponseBody
    public HelpTipDTO updateHelpTip(@PathVariable int helpTipId, @Valid @RequestBody HelpTipForm helpTipForm) {

        HelpTip helpTip = helpTipService.getHelpTipById(helpTipId);
        helpTip.setAnswer(helpTipForm.getAnswer());
        helpTip.setQuestion(helpTipForm.getQuestion());
        helpTip.setTitle(helpTipForm.getTitle());

        return HelpTransForm.helpToHelpDTO(helpTipService.updateHelpTip(helpTip));

    }

    @RequestMapping(value = "/{helpTipId}", method = RequestMethod.DELETE)
    @Interceptors(ManagerRequired.class)
    @ResponseStatus(HttpStatus.OK)
    public void deleteHelpTip(@PathVariable int helpTipId) {

        helpTipService.discardHelpTip(helpTipId);
    }

    @RequestMapping(value = "", method = RequestMethod.PUT)
    @Interceptors(ManagerRequired.class)
    @ResponseStatus(HttpStatus.OK)
    public void updateHelpTips(@Valid @RequestBody UpdateHelpTipForm updateHelpTipForm) {

        helpTipService.updateHelpTipByGroupTitle(updateHelpTipForm.getTitle(), updateHelpTipForm.getNewTitle());

    }

    @RequestMapping(value = "", method = RequestMethod.DELETE)
    @Interceptors(ManagerRequired.class)
    @ResponseStatus(HttpStatus.OK)
    public void deleteHelpTips(@RequestParam(value = "title", required = true) String title) {
        if (title != null) {
            helpTipService.discardHelpTipsByGroupTitle(title);
        } else {
            throw new BadRequestException(HttpStatus.BAD_REQUEST.toString());
        }
    }
}
