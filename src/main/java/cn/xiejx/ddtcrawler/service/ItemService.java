package cn.xiejx.ddtcrawler.service;

import cn.xiejx.ddtcrawler.domain.Item;
import cn.xiejx.ddtcrawler.domain.Result;
import cn.xiejx.ddtcrawler.mapper.ItemMapper;
import jakarta.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author XJX
 * @date 2021/8/7 16:25
 */
@Service
public class ItemService {
    @Resource
    private ItemMapper itemMapper;

    public int deleteById(Long id) {
        return itemMapper.deleteById(id);
    }

    public int insert(List<Item> list) {
        if (CollectionUtils.isEmpty(list)) {
            return 0;
        }
        for (Item item : list) {
            itemMapper.insert(item);
        }
        return list.size();
    }

    public int insert(Result result) {
        if (result == null) {
            return 0;
        }

        return insert(result.getItems());
    }

    public long getMaxAuctionId() {
        return 0;
    }
}
