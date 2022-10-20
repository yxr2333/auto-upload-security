package com.clevesheep.security;

import java.util.HashMap;

/**
 * Created By Intellij IDEA
 *
 * @author Xinrui Yu
 * @date 2022/3/19 10:13 星期六
 */

public class Data {
    private HashMap<String,Object> requestData;
    public Data(){
        requestData = new HashMap<>();
    }
    public Data(String token) {
        requestData = new HashMap<>();
        requestData.put("ttoken",token);
        requestData.put("province","湖北省");
        requestData.put("city","宜昌市");
        requestData.put("district","西陵区");
        requestData.put("adcode","421102");
        requestData.put("longitude","115");
        requestData.put("latitude",30);
        requestData.put("sfqz","否");
        requestData.put("sfys","否");
        requestData.put("sfzy","否");
        requestData.put("sfgl","否");
        requestData.put("status",1);
        requestData.put("szdz","湖北省 宜昌市 西陵区");
        requestData.put("sjh","18771758601");
        requestData.put("lxrxm","羊羊羊");
        requestData.put("lxrsjh","18771758601");
        requestData.put("sffr","否");
        requestData.put("sffrAm","否");
        requestData.put("sffrNoon","否");
        requestData.put("sffrPm","否");
        requestData.put("sffy","否");
        requestData.put("sfgr","否");
        requestData.put("qzglsj","");
        requestData.put("qzgldd","");
        requestData.put("glyy","");
        requestData.put("mqzz","");
        requestData.put("sffx","否");
        requestData.put("qt","");
    }

    public HashMap<String, Object> getRequestData() {
        return requestData;
    }
}
