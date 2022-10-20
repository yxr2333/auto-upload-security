package com.clevesheep.security.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created By Intellij IDEA
 *
 * @author Xinrui Yu
 * @date 2022/3/31 10:15 星期四
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginParam {
    private String username;
    private String password;
}
