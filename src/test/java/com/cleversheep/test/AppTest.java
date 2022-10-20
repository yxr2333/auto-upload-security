package com.cleversheep.test;

import com.clevesheep.security.dto.LoginParam;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created By IntelliJ IDEA
 *
 * @author IceCreamQAQ
 * @datetime 2022/10/20 星期四
 * Happy Every Coding Time~
 */
public class AppTest {

    @Test
    public void test() {
        LoginParam param1 = new LoginParam("username", "456");
        LoginParam param2 = new LoginParam("yxr", "123");

        List<LoginParam> list = Arrays.asList(param1, param2);
        List<LoginParam> collect = list.stream().map(param -> {
            LoginParam loginParam = param.copy();
            loginParam.setPassword(loginParam.getPassword() + "hello");
            return loginParam;
        }).collect(Collectors.toList());
        collect.forEach(System.out::println);
    }
}
