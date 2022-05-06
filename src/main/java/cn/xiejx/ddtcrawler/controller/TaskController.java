package cn.xiejx.ddtcrawler.controller;

import cn.xiejx.ddtcrawler.cookie.WebUser;
import cn.xiejx.ddtcrawler.exception.MyException;
import cn.xiejx.ddtcrawler.task.ItemTask;
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
