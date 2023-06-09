package com.likc.Interceptor;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.likc.entity.User;
import com.likc.util.JwtUtils;
import com.likc.util.UserThreadLocal;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author lkc
 */
@Slf4j
@Component
public class VerifyInterceptor implements HandlerInterceptor {

    @Resource
    private JwtUtils jwtUtils;

    /**
     * 预处理，在业务处理器处理请求之前被调用，可以进行登录拦截，编码处理、安全控制、权限校验等处理
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String jwt = request.getHeader("Authorization");
        if (StringUtils.isEmpty(jwt)){
            throw new JWTVerificationException("非法请求");
        } else {
            String id;
            try {
                DecodedJWT verify = jwtUtils.verify(jwt);
                id = verify.getClaim("id").asString();
            } catch (Exception e) {
                throw new JWTVerificationException("token校验失败");
            }
            User user = new User();
            user.setId(Long.parseLong(id));
            UserThreadLocal.set(user);
        }
        return true;
    }

    /**
     * 预处理，在业务处理器处理请求之前被调用，可以进行登录拦截，编码处理、安全控制、权限校验等处理
     * @param request
     * @param response
     * @param handler
     * @param modelAndView
     * @throws Exception
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    /**
     * 返回处理，在DispatcherServlet完全处理完请求后被调用，可用于清理资源等。已经渲染了页面。
     * @param request
     * @param response
     * @param handler
     * @param ex
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        UserThreadLocal.remove();
    }
}
