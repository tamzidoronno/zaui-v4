/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.informationscreenmanager;

import com.getshop.scope.GetShopSession;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.informationscreen.CustomerInfoScreenHolder;
import com.thundashop.core.informationscreen.Feed;
import com.thundashop.core.informationscreen.InfoScreen;
import com.thundashop.core.informationscreen.Slider;
import com.thundashop.core.informationscreen.SliderType;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
@GetShopSession
public class InformationScreenManager extends ManagerBase implements IInformationScreenManager {

    @Override
    public void dataFromDatabase(DataRetreived data) {
    }
    
    
    @Override
    public List<InfoScreen> getInformationScreens() {
        return null;
    }

    private CustomerInfoScreenHolder getHolder() {
        return null;
    }
    
    private CustomerInfoScreenHolder getByCustomerId(String customerId) {
        return null;
    }

    @Override
    public void registerTv(String customerId) {}

    @Override
    public List<CustomerInfoScreenHolder> getHolders() {
        return null;
    }

    @Override
    public List<SliderType> getTypes() {
        return null;
    }   

    @Override
    public void addSlider(Slider slider, String tvId) {}

    private CustomerInfoScreenHolder getByCustomerHolderByTvId(String tvId) {
        return null;
    }

    @Override
    public InfoScreen getScreen(String id) {
        return null;
    }

    @Override
    public void deleteSlider(String sliderId, String tvId) {}

    @Override
    public void saveTv(InfoScreen tv) {}

    @Override
    public Feed getNews() {
        return null;
    }
}