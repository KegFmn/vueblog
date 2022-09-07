package com.likc.shiro;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: likc
 * @Date: 2022/09/07/21:46
 * @Description:
 */
@Data
public class AccountProfile implements Serializable {

    private Long id;

    private String userName;

    private String avatar;

    private String email;

}
