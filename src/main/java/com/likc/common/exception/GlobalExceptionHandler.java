package com.likc.common.exception;

import com.likc.common.lang.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.ShiroException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {


    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(value = ShiroException.class)
    public Result<Void> handler(ShiroException e){
        log.error("运行时异常: ======================={}",e.getMessage());
        return new Result<>(401, e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = RuntimeException.class)
     public Result<Void> handler(RuntimeException e){
         log.error("运行时异常: ======================={}",e.getMessage());
         return new Result<>(400, e.getMessage());
     }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = IllegalArgumentException.class)
    public Result<Void> handler(IllegalArgumentException e){
        log.error("Assert异常: ======================={}",e.getMessage());
        return new Result<>(400, e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public Result<Void> handler(MethodArgumentNotValidException e){
        log.error("实体校验异常: ======================={}",e.getMessage());
        BindingResult bindingResult = e.getBindingResult();
        ObjectError objectError = bindingResult.getAllErrors().stream().findFirst().get();

        return new Result<>(400, objectError.getDefaultMessage());
    }

}
