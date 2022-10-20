package com.clevesheep.security.common;

import cn.hutool.core.util.ReUtil;
import cn.hutool.db.Db;
import cn.hutool.db.Entity;
import cn.hutool.db.handler.EntityHandler;
import cn.hutool.db.sql.SqlExecutor;
import cn.hutool.http.ContentType;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.clevesheep.security.Data;
import com.clevesheep.security.dto.LoginParam;
import com.clevesheep.security.dto.User;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created By Intellij IDEA
 *
 * @author Xinrui Yu
 * @date 2022/3/31 10:09 星期四
 */
public class CommonUtils {

//    private static final Logger log = LoggerFactory.getLogger(CommonUtils.class);

    private static final Log log = LogFactory.get(CommonUtils.class);
    private static final String UA = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/106.0.0.0 Safari/537.36";

    public static void doRemoteUsersUpload() {
        log.info("开始远程上报");
        HttpResponse response = HttpRequest.get("http://120.25.3.27/api/user/list")
                .execute();
        System.out.println(response);
        if (response.isOk()) {
            JSONObject res = JSONUtil.parseObj(response.body());
            System.out.println(res);
            if (res.getInt("code").equals(20000)) {
                JSONArray data = res.getJSONArray("data");
                System.out.println(data);
                List<User> users = data.stream().map(obj -> JSONUtil.parseObj(obj).toBean(User.class)).collect(Collectors.toList());
                System.out.println(users);
                users.forEach(user -> {
                    LoginParam loginParam = new LoginParam(user);
                    System.out.println(loginParam);
                    doLogin(loginParam);
                    System.out.println("**************分割************");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        }
    }


    public static String getTodaySubmitForm() {
        HttpResponse getFormRes = HttpRequest.get("http://yiqing.ctgu.edu.cn/wx/health/toApply.do")
                .header("User-Agent", UA)
                .execute();
        if (getFormRes.isOk() && getFormRes.body() != null && !Objects.equals(getFormRes.body(), "")) {
            log.info("获取当日上报表单成功！");
            log.info("表单数据：{}", getFormRes.body());
            return getFormRes.body();
        } else {
            log.error("获取上报表单失败！状态码：[" + getFormRes.getStatus() + "]");
            log.info("尝试进行更新当天上报信息...");
            return null;
        }
    }

    public static String getToken(String body) {
        String token = ReUtil.get("<input type=\"hidden\" name=\"ttoken\" value=\"(.*?)\"/", body, 1);
        if (token != null) {
            log.info("自动上报token抓取成功！");
            log.info("随机token：" + token);
            log.info("随机动态token：" + token);
        } else {
            log.error("自动上报时抓取token失败");
        }
        return token;
    }

    public static Boolean doSubmit(String token) {
        Data data = new Data(token);
        HashMap<String, Object> requestData = data.getRequestData();
        HttpResponse applyRes = HttpRequest.post("http://yiqing.ctgu.edu.cn/wx/health/saveApply.do")
                .header("User-Agent", UA)
                .form(requestData)
                .execute();
        if (applyRes.isOk()) {
            log.info("自动上报成功！");
            System.out.println(applyRes.getStatus());
            return true;
        } else {
            log.error("自动上报失败！");
            return false;
        }
    }

    public static void getUserData(HashMap<String, Object> requestData, JSONArray list) {
        JSONObject obj = JSONUtil.parseObj(list.get(5));
        requestData.put("province", obj.getStr("province"));
        requestData.put("city", obj.getStr("city"));
        requestData.put("district", obj.getStr("district"));
        requestData.put("adcode", obj.getStr("adcode"));
        requestData.put("longitude", obj.getStr("longitude"));
        requestData.put("latitude", obj.getStr("latitude"));
        requestData.put("sfqz", obj.getStr("sfqz"));
        requestData.put("sfys", obj.getStr("sfys"));
        requestData.put("sfzy", obj.getStr("sfzy"));
        requestData.put("sfgl", obj.getStr("sfgl"));
        requestData.put("status", obj.getStr("status"));
        requestData.put("szdz", obj.getStr("szdz"));
        requestData.put("sjh", obj.getStr("sjh"));
        requestData.put("lxrxm", obj.getStr("lxrxm"));
        requestData.put("lxrsjh", obj.getStr("lxrsjh"));
        requestData.put("sffr", obj.getStr("sffr"));
        requestData.put("sffrAm", obj.getStr("sffrAm"));
        requestData.put("sffrNoon", obj.getStr("sffrNoon"));
        requestData.put("sffrPm", obj.getStr("sffrPm"));
        requestData.put("sffy", obj.getStr("sffy"));
        requestData.put("sfgr", obj.getStr("sfgr"));
        requestData.put("qzglsj", obj.getStr("qzglsj"));
        requestData.put("qzgldd", obj.getStr("qzgldd"));
        requestData.put("glyy", obj.getStr("glyy"));
        requestData.put("mqzz", obj.getStr("mqzz"));
        requestData.put("sffx", obj.getStr("sffx"));
        requestData.put("qt", obj.getStr("qt"));
    }

    public static Boolean getAllSubmitList() {
        boolean flag = false;
        HttpResponse getListRes = HttpRequest.post("http://yiqing.ctgu.edu.cn/wx/health/studentHis.do")
                .header("User-Agent", UA)
                .execute();
        if (getListRes.isOk()) {
            log.info("获取已上报信息列表成功");
            JSONArray list = JSONUtil.parseArray(getListRes.body());
            Data userData = new Data();
            // 获取用户之前填的个人信息
            getUserData(userData.getRequestData(), list);
            System.out.println(userData.getRequestData());
            JSONObject first = JSONUtil.parseObj(list.get(0));
            String applyid = first.getStr("applyid");
            log.info("获取当天上报信息applyid成功！applyid:" + applyid);
            HttpResponse viewRes = HttpRequest.get("http://yiqing.ctgu.edu.cn/wx/health/viewApply.do?id=" + applyid)
                    .header("User-Agent", UA)
                    .execute();
            if (viewRes.isOk()) {
                log.info("获取上报信息视图成功！");
                HttpResponse editViewRes = HttpRequest.get("http://yiqing.ctgu.edu.cn/wx/health/editApply.do")
                        .header("User-Agent", UA)
                        .execute();
                if (editViewRes.isOk()) {
                    log.info("获取修改信息视图成功！");
                    String body = editViewRes.body();
                    String token = ReUtil.get("<input type=\"hidden\" name=\"ttoken\" value=\"(.*?)\"/", body, 1);
                    if (token != null) {
                        log.info("更新上报信息时token抓取成功！");
                        log.info("随机token：" + token);
                        userData.getRequestData().put("ttoken", token);
                        HttpResponse applyRes = HttpRequest.post("http://yiqing.ctgu.edu.cn/wx/health/saveApply.do")
                                .header("User-Agent", UA)
                                .form(userData.getRequestData())
                                .execute();
                        if (applyRes.isOk()) {
                            log.info("自动更新上报成功！");
                            flag = true;
                            System.out.println(applyRes.getStatus());
                        } else {
                            log.error("更新上报失败！");
                        }
                    } else {
                        log.error("更新上报时抓取token失败！");
                    }
                } else {
                    log.info("获取修改信息视图失败！错误码：[" + editViewRes.getStatus() + "]");
                }

            } else {
                log.info("获取上报信息视图失败，状态码：[" + viewRes.getStatus() + "]");
            }
        } else {
            log.error("获取上报信息列表失败！更新当天上报信息失败");
        }
        return flag;
    }

    public static String doLogin(LoginParam loginParam) {
        HashMap<String, Object> map = new HashMap<>();
        String username = loginParam.getUsername();
        String password = loginParam.getPassword();
        map.put("username", username);
        map.put("password", password);
        HttpRequest request = HttpRequest.post("http://yiqing.ctgu.edu.cn/wx/index/loginSubmit.do")
                .header("User-Agent", UA)
                .contentType(ContentType.MULTIPART.getValue())
                .form(map)
                .timeout(30000);
        HttpResponse loginResponse = request.execute();
        if (loginResponse.isOk()) {
            String loginStatus = loginResponse.body();
            if ("success".equals(loginStatus)) {
                log.info("登录成功！用户名为:" + username);
                String body = getTodaySubmitForm();
                System.out.println(body);
                Optional<Boolean> optional = Optional.ofNullable(checkIsUpload());
                if (optional.isPresent()) {
                    Boolean flag = optional.get();
                    if (flag) {
                        return getAllSubmitList() ? "当日已上报，更新上报信息成功！" : "上报失败！";
                    } else {
                        String token = getToken(body);
                        if (token != null) {
                            return doSubmit(token) ? "上报成功" : "上报失败！";
                        } else {
                            return "随机token抓取失败！";
                        }
                    }
                } else {
                    return "未知错误";
                }
            } else {
                log.error("登陆失败！请检查用户名密码是否配置正确！");
                return "登陆失败！请检查用户名密码是否配置正确！";
            }
        } else {
            return "登陆失败！请检查网络连接！";
        }

    }

    public static Boolean checkIsUpload() {
        String url = "http://yiqing.ctgu.edu.cn/wx/health/main.do";
        HttpResponse response = HttpRequest.get(url).execute();
        if (response.isOk()) {
            log.info("进入首页成功");
            String body = response.body();
            // <span class="normal-sm-tip green-warn fn-ml10">今日已上报</span>
            String s = ReUtil.get("<span class=\"normal-sm-tip green-warn fn-ml10\">(.*?)</span>", body, 1);
            log.info("上报状态：{}", s);
            if ("今日已上报".equals(s)) {
                log.info("今日已上报");
                return true;
            }
            return false;
        } else {
            log.info("进入首页失败");
            return null;
        }
    }

    public static void saveUser(String username, String password) {
        try {
            Connection conn = Db.use().getConnection();
            String sql = "select count(*) `sum` from t_users where username = ?";
            Entity entity = SqlExecutor.query(conn, sql, new EntityHandler(), username);
            Integer sum = entity.getInt("sum");
            // 如果数据库中不存在，那么就保存到数据库中
            if (sum == 0) {
                Db.use().insertForGeneratedKey(
                        Entity.create("t_users")
                                .set("username", username)
                                .set("password", password)
                                .set("create_time", LocalDateTime.now())
                );
                log.info("*****数据保存完毕*****");
            } else {
                log.info("*****该信息已存在，无需保存*****");
                Db.use().update(
                        Entity.create().set("password", password),
                        Entity.create("t_users").set("username", username)
                );
                log.info("*****更新信息成功*****");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
