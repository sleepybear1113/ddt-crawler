package com.xjx.ddtcrawler.controller;

import com.xjx.ddtcrawler.cookie.WebUser;
import com.xjx.ddtcrawler.exception.MyException;
import com.xjx.ddtcrawler.task.ItemTask;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author XJX
 * @date 2021/8/14 18:40
 */
@RestController
public class TaskController {
    @Resource
    private ItemTask itemTask;

    public String startAuction() throws MyException {
        WebUser safeWebUser = WebUser.getSafeWebUser();
        return itemTask.startTask(safeWebUser);
    }
}
