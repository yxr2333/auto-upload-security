package com.clevesheep.security;


import cn.hutool.core.io.file.FileAppender;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.cron.CronUtil;
import cn.hutool.db.Db;
import cn.hutool.db.Entity;
import cn.hutool.http.ContentType;
import cn.hutool.http.HttpUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.clevesheep.security.common.CommonUtils;
import com.clevesheep.security.dto.LoginParam;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

/**
 * Created By Intellij IDEA
 *
 * @author Xinrui Yu
 * @date 2022/3/19 10:09 星期六
 */
public class Application {
    private static final Log log = LogFactory.get(Application.class);
    public static void main(String[] args) {
        CronUtil.start(true);
        HttpUtil.createServer(8888)
                .setRoot("src/main/resources/web")
                .addAction("/submit",(request,response) -> {
                    // 解决跨域的问题
                    response.setHeader("Access-Control-Allow-Origin", "*");
                    response.setHeader("Access-Control-Allow-Methods", "*");
                    response.setHeader("Access-Control-Max-Age", "3600");
                    response.setHeader("Access-Control-Allow-Headers", "*");
                    response.setHeader("Access-Control-Allow-Credentials", "true");
                    log.info("收到客户端发送的请求");
                    log.info("客户端IP：" + request.getClientIP());
                    log.info("User-Agent：" + request.getUserAgentStr());
                    String username = request.getParam("username");
                    String password = request.getParam("password");
                    log.info("username：" + username);
                    log.info("password：" + password);
                    log.info("*****保存数据中*****");
                        CommonUtils.saveUser(username,password);
                    log.info("*****开始上报*****");
                    LoginParam user = new LoginParam();
                    user.setUsername(username);
                    user.setPassword(password);
                    String result = CommonUtils.doLogin(user);
                    log.info("上报结果：" + result);
                    log.info("*****上报结束*****");
                    response.write(result);
                })
                .addAction("/all", (request,response) -> {
                    response.setHeader("Access-Control-Allow-Origin", "*");
                    response.setHeader("Access-Control-Allow-Methods", "*");
                    response.setHeader("Access-Control-Max-Age", "3600");
                    response.setHeader("Access-Control-Allow-Headers", "*");
                    response.setHeader("Access-Control-Allow-Credentials", "true");
                    List<Entity> users = null;
                    try {
                        users = Db.use().findAll("t_users");

                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    if (users == null) {
                        response.write("暂无数据");
                    } else {
                        // 判断是否全部成功上报
                        int sum = 0;
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
                            if (!s.contains("成功")) {
                                sum += 1;
                            }
                        }
                        if (sum == users.size()) {
                            log.info("全部上报成功");
                            response.write(new String("全部上报成功".getBytes(), StandardCharsets.UTF_8), ContentType.build("text/html", StandardCharsets.UTF_8));
                        } else {
                            log.info("部分上报成功");
                            response.write("部分上报失败");
                        }
                    }

                })
                .start();

    }
}
