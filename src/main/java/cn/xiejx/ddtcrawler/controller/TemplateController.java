package cn.xiejx.ddtcrawler.controller;

import cn.xiejx.ddtcrawler.cookie.WebUser;
import cn.xiejx.ddtcrawler.dto.TemplateDto;
import cn.xiejx.ddtcrawler.exception.MyException;
import cn.xiejx.ddtcrawler.logic.TemplateLogic;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author XJX
 * @date 2021/8/14 18:33
 */
@RestController
public class TemplateController {
    @Resource
    private TemplateLogic templateLogic;

    @RequestMapping("/template/getTemplateById")
    public TemplateDto getTemplateById(Long id) throws MyException {
        WebUser webUser = WebUser.getSafeWebUser();
        if (webUser.notAdmin()) {
            throw new MyException("无权操作该接口");
        }
        return templateLogic.getTemplateById(id);
    }

    @RequestMapping("/template/saveTemplate")
    public Boolean saveTemplate(Long templateId, String templateName) throws MyException {
        WebUser webUser = WebUser.getSafeWebUser();
        if (webUser.notAdmin()) {
            throw new MyException("无权操作该接口");
        }
        return templateLogic.saveTemplate(templateId, templateName);
    }

    @RequestMapping("/template/listAllTemplates")
    public List<TemplateDto> listAllTemplates() {
        return templateLogic.listAllTemplates();
    }

    @RequestMapping("/template/listCommonSlv4")
    public List<TemplateDto> listCommonSlv4() {
        return templateLogic.listCommonSlv4();
    }
}
