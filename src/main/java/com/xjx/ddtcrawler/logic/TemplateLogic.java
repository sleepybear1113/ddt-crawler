package com.xjx.ddtcrawler.logic;

import com.xjx.ddtcrawler.domain.Template;
import com.xjx.ddtcrawler.exception.MyException;
import com.xjx.ddtcrawler.service.TemplateService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author XieJiaxing
 * @date 2021/8/14 18:34
 */
@Component
public class TemplateLogic {
    private static final List<Long> COMMON_SLV_4 = new ArrayList<>();

    static {
        Long[] commonAttributeSlv4 = {311418L, 311420L, 311421L, 311422L, 311423L, 311424L, 311426L, 311427L, 311428L, 311429L, 311430L, 311431L, 311432L, 311433L, 311434L};
        Long[] commonAttackSlv4 = {312409L, 312410L, 312411L, 312412L, 312413L, 312415L};
        Long[] commonDefenceSlv4 = {313407L, 313408L, 313409L, 313410L, 313412L};
        COMMON_SLV_4.addAll(Arrays.asList(commonAttributeSlv4));
        COMMON_SLV_4.addAll(Arrays.asList(commonAttackSlv4));
        COMMON_SLV_4.addAll(Arrays.asList(commonDefenceSlv4));
    }

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

    public List<Template> listAllTemplates() {
        List<Template> templates = templateService.getAll();
        templates.forEach(Template::encryptId);
        return templates;
    }

    public List<Template> listCommonSlv4() {
        List<Template> templates = templateService.getByIds(COMMON_SLV_4);
        if (CollectionUtils.isEmpty(templates)) {
            return new ArrayList<>();
        }

        templates.forEach(Template::encryptId);
        return templates;
    }
}
