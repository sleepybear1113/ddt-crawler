package com.xjx.ddtcrawler.mapper;

import com.xjx.ddtcrawler.domain.Item;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemMapper {
    int deleteById(Long id);

    int insert(Item record);

    int insertBatch(List<Item> records);

    int insertSelective(Item record);

    Item getById(Long id);

    int updateByIdSelective(Item record);

    int updateById(Item record);

    long getMaxAuctionId();
}