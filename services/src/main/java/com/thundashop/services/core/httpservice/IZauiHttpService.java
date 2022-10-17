package com.thundashop.services.core.httpservice;

public interface IZauiHttpService {
    OkHttpResponse get(OkHttpRequest httpRequest);
    OkHttpResponse post(OkHttpRequest httpRequest);
    
}
