package com.xjx.ddtcrawler.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xjx.ddtcrawler.domain.Template;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author XJX
 * @date 2021/8/1 20:40
 */
@Repository
public interface TemplateMapper extends BaseMapper<Template> {

    List<Template> getAll();
}
