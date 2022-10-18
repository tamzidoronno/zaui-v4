package com.thundashop.services.core.httpservice;

import com.thundashop.core.webmanager.ZauiHttpRequest;
import com.thundashop.core.webmanager.ZauiHttpResponse;

public interface IZauiHttpService {
    ZauiHttpResponse get(ZauiHttpRequest httpRequest);
    ZauiHttpResponse post(ZauiHttpRequest httpRequest);
    
}
