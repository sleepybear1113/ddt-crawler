package com.xjx.ddtcrawler.controller;

import com.xjx.ddtcrawler.cookie.UserPrivilege;
import com.xjx.ddtcrawler.exception.MyException;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
public class PrivilegeController {

    @Resource
    private UserPrivilege userPrivilege;

    @RequestMapping("/privilege/addCommonUserId")
    public List<Long> addCommonUserId(Long id) {
        if (id == null) {
            throw new MyException("错误");
        }

        for (Long commonUserId : userPrivilege.getCommonUserIds()) {
            if (commonUserId.equals(id)) {
                throw new MyException("重复");
            }
        }
        userPrivilege.getCommonUserIds().add(id);
        return userPrivilege.getCommonUserIds();
    }

    @RequestMapping("/privilege/deleteCommonUserId")
    public Object deleteCommonUserId(Long id) {
        if (id == null) {
            throw new MyException("错误");
        }

        userPrivilege.getCommonUserIds().removeIf(u -> u.equals(id));
        return userPrivilege.getCommonUserIds();
    }
}
