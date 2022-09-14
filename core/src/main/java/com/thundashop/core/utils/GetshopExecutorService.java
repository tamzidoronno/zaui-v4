package com.thundashop.core.utils;

import com.getshop.scope.GetShopSessionScope;
import com.thundashop.core.common.AppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GetshopExecutorService implements  Runnable{
    protected GetShopSessionScope scope;
    protected static final Logger logger = LoggerFactory.getLogger(GetshopExecutorService.class);
    long threadId;

    public GetshopExecutorService(){
        scope = AppContext.appContext.getBean(GetShopSessionScope.class);
        this.threadId = Thread.currentThread().getId();
    }

    @Override
    public void run(){
        long currentThreadId = Thread.currentThread().getId();
        scope.shareThreadContext(threadId, currentThreadId);
    }
}

