package com.thundashop.services.core.httpservice;

import com.thundashop.core.webmanager.OkHttpRequest;
import com.thundashop.core.webmanager.OkHttpResponse;

public interface IZauiHttpService {
    OkHttpResponse get(OkHttpRequest httpRequest);
    OkHttpResponse post(OkHttpRequest httpRequest);
    
}
