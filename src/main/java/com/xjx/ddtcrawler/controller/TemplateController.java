package com.xjx.ddtcrawler.controller;

import com.xjx.ddtcrawler.cookie.WebUser;
import com.xjx.ddtcrawler.domain.Template;
import com.xjx.ddtcrawler.exception.MyException;
import com.xjx.ddtcrawler.logic.TemplateLogic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author XieJiaxing
 * @date 2021/8/14 18:33
 */
@RestController
public class TemplateController {
    @Autowired
    private TemplateLogic templateLogic;

    @RequestMapping("/template/getTemplateById")
    public Template getTemplateById(Long id) throws MyException {
        WebUser webUser = WebUser.getSafeWebUser();
        if (!webUser.isAdmin()) {
            throw new MyException("无权操作该接口");
        }
        return templateLogic.getTemplateById(id);
    }


    @RequestMapping("/template/saveTemplate")
    public Boolean saveTemplate(Long templateId, String templateName) throws MyException {
        WebUser webUser = WebUser.getSafeWebUser();
        if (!webUser.isAdmin()) {
            throw new MyException("无权操作该接口");
        }
        return templateLogic.saveTemplate(templateId, templateName);
    }
}