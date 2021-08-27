package com.thundashop.core.config;

import com.thundashop.core.common.GetShopLogHandler;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;

import java.lang.reflect.Method;

public class AsyncExceptionHandler implements AsyncUncaughtExceptionHandler {

    @Override
    public void handleUncaughtException(Throwable throwable, Method method, Object... objects) {
        GetShopLogHandler.logStack((Exception) throwable, null);
    }

}
