package com.thundashop.core.activity.service;

import com.thundashop.core.activity.dto.OctoProduct;
import com.thundashop.core.webmanager.WebManager;
import okhttp3.Request;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OctoApiService {
    @Autowired
    private WebManager webManager;

//    public List<OctoProduct> getProducts(){
//        Map<String,String> headers = new HashMap<>();
//        headers.put(Constants.OCTO_CONTENT.getLeft(), Constants.OCTO_CONTENT.getRight());
//        Request request = webManager.httpBearerTokenRequest(Constants.OCTO_API_ENDPOINT,Constants.OCTO_API_KEY,headers,null,"GET");
//    }
}
