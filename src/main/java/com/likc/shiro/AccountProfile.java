package com.likc.shiro;

import lombok.Data;

import java.io.Serializable;

@Data
public class AccountProfile implements Serializable {

    private Long uid;

    private String userName;

    private String avatar;

    private String email;

}
