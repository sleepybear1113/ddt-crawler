package com.xjx.ddtcrawler.controller;

import com.xjx.ddtcrawler.cookie.UserPrivilege;
import com.xjx.ddtcrawler.cookie.WebUser;
import com.xjx.ddtcrawler.domain.Template;
import com.xjx.ddtcrawler.exception.MyException;
import com.xjx.ddtcrawler.logic.TemplateLogic;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author XieJiaxing
 * @date 2021/8/14 18:33
 */
@RestController
public class TemplateController {
    @Resource
    private TemplateLogic templateLogic;
    @Resource
    private UserPrivilege userPrivilege;

    @RequestMapping("/template/getTemplateById")
    public Template getTemplateById(Long id) throws MyException {
        WebUser webUser = WebUser.getSafeWebUser();
        if (userPrivilege.notAdmin(webUser)) {
            throw new MyException("无权操作该接口");
        }
        return templateLogic.getTemplateById(id);
    }

    @RequestMapping("/template/saveTemplate")
    public Boolean saveTemplate(Long templateId, String templateName) throws MyException {
        WebUser webUser = WebUser.getSafeWebUser();
        if (userPrivilege.notAdmin(webUser)) {
            throw new MyException("无权操作该接口");
        }
        return templateLogic.saveTemplate(templateId, templateName);
    }

    @RequestMapping("/template/listAllTemplates")
    public List<Template> listAllTemplates() {
        return templateLogic.listAllTemplates();
    }

    @RequestMapping("/template/listCommonSlv4")
    public List<Template> listCommonSlv4() {
        return templateLogic.listCommonSlv4();
    }
}
