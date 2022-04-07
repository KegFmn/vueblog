package com.likc.common.lang;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Result<T> implements Serializable{

    private Integer code;
    private String msg;
    private T data;

    public Result(Integer code, String msg) {
        this(code, msg, null);
    }

}
