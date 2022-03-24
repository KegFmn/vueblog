package com.likc.common.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
public class LoginDto implements Serializable {

    @NotBlank(message = "昵称不能为空")
    private String userName;

    @NotBlank(message = "密码不能为空")
    private String passWord;
}
