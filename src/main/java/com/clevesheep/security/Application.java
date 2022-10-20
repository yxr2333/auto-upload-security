package com.clevesheep.security;


import cn.hutool.cron.CronUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.clevesheep.security.common.CommonUtils;
import com.clevesheep.security.dto.LoginParam;

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
                .addAction("/submit", (request, response) -> {
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
                    CommonUtils.saveUser(username, password);
                    log.info("*****开始上报*****");
                    LoginParam user = new LoginParam();
                    user.setUsername(username);
                    user.setPassword(password);
                    String result = CommonUtils.doLogin(user);
                    log.info("上报结果：" + result);
                    log.info("*****上报结束*****");
                    response.write(result);
                })
                .addAction("/test", (request, response) -> {
                    response.setHeader("Access-Control-Allow-Origin", "*");
                    response.setHeader("Access-Control-Allow-Methods", "*");
                    response.setHeader("Access-Control-Max-Age", "3600");
                    response.setHeader("Access-Control-Allow-Headers", "*");
                    response.setHeader("Access-Control-Allow-Credentials", "true");
                    log.info("收到客户端发送的请求");
                    CommonUtils.doRemoteUsersUpload();
                    response.write("远程上报成功");
                })
                .start();

    }
}
