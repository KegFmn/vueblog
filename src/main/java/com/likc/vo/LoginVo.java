package com.likc.vo;

import lombok.*;

/**
 * @author likc
 * @date 2022/3/16
 * @description
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginVo {

    private Long id;

    private String userName;

    private String avatar;

    private String email;
}
