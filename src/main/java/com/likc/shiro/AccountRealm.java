package com.likc.shiro;

import com.likc.entity.User;
import com.likc.service.UserService;
import com.likc.util.JwtUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AccountRealm extends AuthorizingRealm {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserService userService;

    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof JwtToken;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {

        JwtToken jwtToken = (JwtToken)token;

        String userId = jwtUtils.getClaimByToken((String) jwtToken.getPrincipal()).getSubject();

        User user = userService.getById(Long.parseLong(userId));

        if (user == null){
            throw new UnknownAccountException("账户不存在");
        }

        if (user.getStatus() == -1){
            throw new LockedAccountException("账户已被锁定");
        }

        AccountProfile profile = new AccountProfile();
        BeanUtils.copyProperties(user,profile);

        //profile消息体、getCredentials获取密钥、getName()其实就是这个自定义类的名字，校验成功后放行到Controller校验权限
        return new SimpleAuthenticationInfo(profile, jwtToken.getCredentials(), getName());
    }

    /**
     * 授权
     * 只有当需要检测用户权限的时候才会调用此方法，例如checkRole,checkPermission之类的
     * @param principalCollection
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        return null;
    }
}
