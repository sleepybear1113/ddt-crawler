package com.xjx.ddtcrawler.controller;

import com.xjx.ddtcrawler.cookie.WebUser;
import com.xjx.ddtcrawler.exception.MyException;
import com.xjx.ddtcrawler.task.ItemTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author XieJiaxing
 * @date 2021/8/14 18:40
 */
@RestController
public class TaskController {
    @Autowired
    private ItemTask itemTask;

    @RequestMapping("/task/startAuction")
    public String startAuction() throws MyException {
        WebUser safeWebUser = WebUser.getSafeWebUser();
        return itemTask.startTask(safeWebUser);
    }
}
