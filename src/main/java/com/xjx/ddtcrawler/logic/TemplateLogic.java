package com.xjx.ddtcrawler.logic;

import com.xjx.ddtcrawler.domain.Template;
import com.xjx.ddtcrawler.exception.MyException;
import com.xjx.ddtcrawler.service.TemplateService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author XieJiaxing
 * @date 2021/8/14 18:34
 */
@Component
public class TemplateLogic {
    @Autowired
    private TemplateService templateService;

    public Template getTemplateById(Long id) {
        return templateService.getById(id);
    }

    public Boolean saveTemplate(Long templateId, String templateName) throws MyException {
        if (templateId == null || StringUtils.isBlank(templateName)) {
            throw new MyException("template 参数有误");
        }
        Template template = new Template();
        template.setId(templateId);
        template.setName(templateName);
        return templateService.save(template);
    }
}
