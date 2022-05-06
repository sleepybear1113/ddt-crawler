package cn.xiejx.ddtcrawler.logic;

import cn.xiejx.ddtcrawler.dto.TemplateDto;
import cn.xiejx.ddtcrawler.exception.MyException;
import cn.xiejx.ddtcrawler.service.TemplateService;
import jakarta.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author XJX
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

    @Resource
    private TemplateService templateService;

    public TemplateDto getTemplateById(Long id) {
        return templateService.getById(id);
    }

    public Boolean saveTemplate(Long templateId, String templateName) throws MyException {
        if (templateId == null || StringUtils.isBlank(templateName)) {
            throw new MyException("template 参数有误");
        }
        TemplateDto templateDto = new TemplateDto();
        templateDto.setId(templateId);
        templateDto.setName(templateName);
        return templateService.save(templateDto);
    }

    public List<TemplateDto> listAllTemplates() {
        List<TemplateDto> templates = templateService.getAll();
        templates.forEach(TemplateDto::encryptId);
        return templates;
    }

    public List<TemplateDto> listCommonSlv4() {
        List<TemplateDto> templates = templateService.getByIds(COMMON_SLV_4);
        if (CollectionUtils.isEmpty(templates)) {
            return new ArrayList<>();
        }

        templates.forEach(TemplateDto::encryptId);
        return templates;
    }
}
