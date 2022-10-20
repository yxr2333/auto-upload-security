package com.clevesheep.security.cron;

import cn.hutool.db.Db;
import cn.hutool.db.Entity;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.clevesheep.security.common.CommonUtils;
import com.clevesheep.security.dto.LoginParam;

import java.sql.SQLException;
import java.util.List;

/**
 * Created By Intellij IDEA
 *
 * @author Xinrui Yu
 * @date 2022/3/31 13:23 星期四
 */
public class AutoSubmitCronTask {
    private static final Log log = LogFactory.get(AutoSubmitCronTask.class);
    public void submit(){
        log.info("开始给数据库中的所有用户自动上报！");
        try {
            List<Entity> users = Db.use().findAll("t_users");
            for (Entity user : users) {
                String id = user.getStr("id");
                String username = user.getStr("username");
                String password = user.getStr("password");
                log.info("当前上报用户信息：");
                log.info("编号：" + id);
                log.info("用户名：" + username);
                log.info("密码：" + password);
                LoginParam loginParam = new LoginParam(username, password);
                String s = CommonUtils.doLogin(loginParam);
                log.info("上报结果" + s);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
