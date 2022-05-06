package cn.xiejx.ddtcrawler.controller;

import cn.xiejx.ddtcrawler.exception.MyException;
import cn.xiejx.ddtcrawler.config.ConfigBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author xjx
 */
@Slf4j
@RestController
public class PrivilegeController {

    @RequestMapping("/privilege/addCommonUserId")
    public List<Long> addCommonUserId(Long id) {
        if (id == null) {
            throw new MyException("错误");
        }

        for (Long commonUserId : ConfigBean.getCommonUserIds()) {
            if (commonUserId.equals(id)) {
                throw new MyException("重复");
            }
        }
        ConfigBean.getCommonUserIds().add(id);
        return ConfigBean.getCommonUserIds();
    }

    @RequestMapping("/privilege/deleteCommonUserId")
    public List<Long> deleteCommonUserId(Long id) {
        if (id != null) {
            ConfigBean.getCommonUserIds().removeIf(u -> u.equals(id));
        }
        return ConfigBean.getCommonUserIds();
    }
}
