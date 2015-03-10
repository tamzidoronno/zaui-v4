/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.informationscreenmanager;

import com.thundashop.core.common.Administrator;
import com.thundashop.core.common.Customer;
import com.thundashop.core.common.GetShopApi;
import com.thundashop.core.informationscreen.CustomerInfoScreenHolder;
import com.thundashop.core.informationscreen.Feed;
import com.thundashop.core.informationscreen.InfoScreen;
import com.thundashop.core.informationscreen.Slider;
import com.thundashop.core.informationscreen.SliderType;
import java.util.List;

/**
 *
 * @author ktonder
 */
@GetShopApi
interface IInformationScreenManager {
    List<InfoScreen> getInformationScreens();
    
    @Administrator
    public void registerTv(String customerId);
    
    @Administrator
    public List<CustomerInfoScreenHolder> getHolders();
    
    List<SliderType> getTypes();
    
    @Customer
    public void addSlider(Slider slider, String tvId);
    
    @Customer
    public void deleteSlider(String sliderId, String tvId);
    
    public InfoScreen getScreen(String id);
    
    public void saveTv(InfoScreen tv);
    
    public Feed getNews();
}
