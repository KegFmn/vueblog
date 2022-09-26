package com.likc.Interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.likc.annotation.AccessLimit;
import com.likc.common.lang.Result;
import com.likc.util.RedisUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author likc
 * @date 2022/9/26
 * @description
 */
@Component
public class AccessLimtInterceptor implements HandlerInterceptor {

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {

            HandlerMethod hm = (HandlerMethod) handler;
            //获取方法中的注解,看是否有该注解
            AccessLimit accessLimit = hm.getMethodAnnotation(AccessLimit.class);
            if (accessLimit == null) {
                return true;
            }
            int seconds = accessLimit.seconds();
            int maxCount = accessLimit.maxCount();
            boolean needFingerprint = accessLimit.needFingerprint();
            String fingerprint = null;
            if (needFingerprint) {
                fingerprint = request.getHeader("fingerprint");
                if (StringUtils.isEmpty(fingerprint)) {
                    render(response, "请刷新后再使用");
                    return false;
                }
            }
            // 拼接Key
            String key = fingerprint + "::" + request.getServletPath();

            ReentrantLock lock = new ReentrantLock();
            lock.lock();
            try {
                Integer count = (Integer)redisUtils.get(key);
                if (count == null) {
                    redisUtils.set(key, 1, seconds);
                } else if (count < maxCount) {
                    redisUtils.incr(key, 1);
                } else {
                    render(response, "访问过于频繁");
                    return false;
                }
            } finally {
                lock.unlock();
            }
        }
        return true;
    }

    private void render(HttpServletResponse response, String msg)throws Exception {
        response.setContentType("application/json;charset=UTF-8");
        OutputStream out = response.getOutputStream();
        String str  = objectMapper.writeValueAsString(new Result<>(400, msg));
        out.write(str.getBytes(StandardCharsets.UTF_8));
        out.flush();
        out.close();
    }
}
