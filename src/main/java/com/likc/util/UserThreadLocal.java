package com.likc.util;

import com.likc.entity.User;

/**
 * @author lkc
 */
public class UserThreadLocal {
    private static ThreadLocal<User> opUserThread = new ThreadLocal<>();

    public static void set(User user) {
        opUserThread.set(user);
    }

    public static Long getId() {
        User user = opUserThread.get();
        if (user != null) {
            return user.getId();
        }
        return null;
    }

    public static User get() {
        return opUserThread.get();
    }

    // 防止内存泄漏
    public static void remove() {
        opUserThread.remove();
    }
}
