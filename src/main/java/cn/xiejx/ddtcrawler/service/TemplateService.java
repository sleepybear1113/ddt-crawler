package cn.xiejx.ddtcrawler.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import cn.xiejx.ddtcrawler.domain.Template;
import cn.xiejx.ddtcrawler.dto.TemplateDto;
import cn.xiejx.ddtcrawler.mapper.TemplateMapper;
import jakarta.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author XJX
 * @date 2021/8/1 20:56
 */
@Service
public class TemplateService {
    @Resource
    private TemplateMapper templateMapper;

    public TemplateDto getById(Long id) {
        if (id == null) {
            return null;
        }
        Template template = templateMapper.selectById(id);
        if (template == null) {
            return null;
        }
        TemplateDto templateDto = new TemplateDto();
        BeanUtils.copyProperties(template, templateDto);
        return templateDto;
    }

    public List<TemplateDto> getByIds(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return new ArrayList<>();
        }

        List<Template> templates = templateMapper.selectBatchIds(new ArrayList<>(new HashSet<>(ids)));
        if (CollectionUtils.isEmpty(templates)) {
            return new ArrayList<>();
        }

        ArrayList<TemplateDto> templateDtoList = new ArrayList<>();
        for (Template template : templates) {
            TemplateDto t = new TemplateDto();
            BeanUtils.copyProperties(template, t);
            templateDtoList.add(t);
        }

        return templateDtoList;
    }

    public Map<Long, TemplateDto> getMapByIds(List<Long> ids) {
        List<TemplateDto> list = getByIds(ids);
        if (CollectionUtils.isEmpty(list)) {
            return new HashMap<>();
        }

        Map<Long, TemplateDto> map = new HashMap<>();
        for (TemplateDto templateDto : list) {
            Long id = templateDto.getId();
            map.put(id, templateDto);
        }
        return map;
    }

    public boolean save(TemplateDto templateDto) {
        if (templateDto == null || templateDto.getId() == null) {
            return false;
        }
        Template template = new Template();
        BeanUtils.copyProperties(templateDto, template);
        templateDto.setModifyTime(System.currentTimeMillis());

        Long id = templateDto.getId();
        Template existTemplate = templateMapper.selectById(id);
        if (existTemplate == null) {
            if (StringUtils.isBlank(templateDto.getName())) {
                return false;
            }

            templateMapper.insert(template);
        } else {
            templateMapper.updateById(template);
        }

        return true;
    }

    public List<TemplateDto> getAll() {
        List<Template> templates = templateMapper.selectList(new QueryWrapper<>());
        ArrayList<TemplateDto> templateDtoList = new ArrayList<>();
        for (Template template : templates) {
            TemplateDto t = new TemplateDto();
            BeanUtils.copyProperties(template, t);
            templateDtoList.add(t);
        }

        return templateDtoList;
    }
}
