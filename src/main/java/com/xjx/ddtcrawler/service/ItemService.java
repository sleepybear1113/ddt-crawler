package com.xjx.ddtcrawler.service;

import com.xjx.ddtcrawler.domain.Item;
import com.xjx.ddtcrawler.domain.Result;
import com.xjx.ddtcrawler.mapper.ItemMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author XJX
 * @date 2021/8/7 16:25
 */
@Service
public class ItemService {
    @Autowired
    private ItemMapper itemMapper;

    public int deleteById(Long id) {
        return itemMapper.deleteById(id);
    }

    public int insert(List<Item> list) {
        return itemMapper.insertBatch(list);
    }

    public int insert(Result result) {
        if (result == null) {
            return 0;
        }

        List<Item> items = result.getItems();
        if (CollectionUtils.isEmpty(items)) {
            return 0;
        }

        return insert(items);
    }

    public int insert(Item record) {
        return itemMapper.insert(record);
    }

    public int insertSelective(Item record) {
        return itemMapper.insertSelective(record);
    }

    public Item getById(Long id) {
        return itemMapper.getById(id);
    }

    public int updateByIdSelective(Item record) {
        return itemMapper.updateByIdSelective(record);
    }

    public int updateById(Item record) {
        return itemMapper.updateById(record);
    }

    public long getMaxAuctionId() {
        return itemMapper.getMaxAuctionId();
    }
}
