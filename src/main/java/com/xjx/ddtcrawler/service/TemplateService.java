package com.xjx.ddtcrawler.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xjx.ddtcrawler.domain.Template;
import com.xjx.ddtcrawler.mapper.TemplateMapper;
import jakarta.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
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

    public Template getById(Long id) {
        if (id == null) {
            return null;
        }
        return templateMapper.selectById(id);
    }

    public List<Template> getByIds(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return new ArrayList<>();
        }

        return templateMapper.selectBatchIds(new ArrayList<>(new HashSet<>(ids)));
    }

    public Map<Long, Template> getMapByIds(List<Long> ids) {
        List<Template> list = getByIds(ids);
        if (CollectionUtils.isEmpty(list)) {
            return new HashMap<>();
        }

        Map<Long, Template> map = new HashMap<>();
        for (Template template : list) {
            Long id = template.getId();
            map.put(id, template);
        }
        return map;
    }

    public boolean save(Template template) {
        if (template == null || template.getId() == null) {
            return false;
        }
        template.setModifyTime(System.currentTimeMillis());

        Long id = template.getId();
        Template existTemplate = templateMapper.selectById(id);
        if (existTemplate == null) {
            if (StringUtils.isBlank(template.getName())) {
                return false;
            }

            templateMapper.insert(template);
        } else {
            templateMapper.updateById(template);
        }

        return true;
    }

    public List<Template> getAll() {
        return templateMapper.selectList(new QueryWrapper<>());
    }
}
