package com.xjx.ddtcrawler.mapper;

import com.xjx.ddtcrawler.domain.Template;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author XieJiaxing
 * @date 2021/8/1 20:40
 */
@Repository
public interface TemplateMapper {
    Template get(Long id);

    List<Template> getByIds(List<Long> ids);

    List<Template> getAll();

    void add(Template template);

    void update(Template template);
}
