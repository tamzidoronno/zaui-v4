package com.thundashop.core.utils;

import com.getshop.scope.GetShopSessionScope;
import com.thundashop.core.common.AppContext;

public class GetshopAsyncTask implements Runnable {
    protected GetShopSessionScope scope;
    long threadId;

    public GetshopAsyncTask() {
        scope = AppContext.appContext.getBean(GetShopSessionScope.class);
        this.threadId = Thread.currentThread().getId();
    }

    @Override
    public void run() {
        long currentThreadId = Thread.currentThread().getId();
        scope.shareThreadContext(threadId, currentThreadId);
    }
}
