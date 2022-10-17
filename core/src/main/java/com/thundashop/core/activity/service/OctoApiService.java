package com.thundashop.core.activity.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.thundashop.core.activity.dto.OctoProduct;
import com.thundashop.core.webmanager.WebManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OctoApiService {
    @Autowired
    private WebManager webManager;

    public List<OctoProduct> getProducts(Integer supplierId) throws IOException {
        Map<String,String> headers = new HashMap<>();
        headers.put(Constants.OCTO_CONTENT.getLeft(), Constants.OCTO_CONTENT.getRight());
        String result = webManager.getResponseWithHeaders(Constants.OCTO_API_ENDPOINT +"/suppliers/"+supplierId+"/products",Constants.OCTO_API_KEY,headers,null, null, "GET");
        Type listType = new TypeToken<List<OctoProduct>>(){}.getType();

        return new Gson().fromJson(result, listType);
    }
}
