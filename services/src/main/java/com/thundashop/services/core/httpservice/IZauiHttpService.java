package com.thundashop.services.core.httpservice;

import com.thundashop.core.webmanager.ZauiHttpRequest;
import com.thundashop.core.webmanager.OkHttpResponse;

public interface IZauiHttpService {
    OkHttpResponse get(ZauiHttpRequest httpRequest);
    OkHttpResponse post(ZauiHttpRequest httpRequest);
    
}
