/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.informationscreenmanager;

import com.getshop.scope.GetShopSession;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.informationscreen.CustomerInfoScreenHolder;
import com.thundashop.core.informationscreen.Feed;
import com.thundashop.core.informationscreen.InfoScreen;
import com.thundashop.core.informationscreen.Slider;
import com.thundashop.core.informationscreen.SliderType;
import com.thundashop.core.usermanager.UserManager;
import com.thundashop.core.usermanager.data.User;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
@GetShopSession
public class InformationScreenManager extends ManagerBase implements IInformationScreenManager {
    @Autowired
    private UserManager userManager;
    
    @Autowired
    private SliderTypeRepository sliderRepository;
    
    public Map<String, CustomerInfoScreenHolder> holders = new HashMap();

    @Override
    public void dataFromDatabase(DataRetreived data) {
        for (DataCommon dataCommon : data.data) {
            if (dataCommon instanceof CustomerInfoScreenHolder) {
                CustomerInfoScreenHolder holder = (CustomerInfoScreenHolder)dataCommon;
                holders.put(holder.getCustomerId(), holder);
            }
        }
    }
    
    
    @Override
    public List<InfoScreen> getInformationScreens() {
        CustomerInfoScreenHolder holder = getHolder();
        if (holder != null) {
            return holder.getScreens();
        } 
        
        return null;
    }

    private CustomerInfoScreenHolder getHolder() {
        if (getSession() == null || getSession().currentUser == null) {
            return null;
        }
        
        return getByCustomerId(getSession().currentUser.id);
    }
    
    private CustomerInfoScreenHolder getByCustomerId(String customerId) {
        CustomerInfoScreenHolder holder = holders.get(customerId);
        if (holder == null) {
            holder = new CustomerInfoScreenHolder(customerId);
            holders.put(customerId, holder);
        }
        
        return holder;
    }

    @Override
    public void registerTv(String customerId) {
        String infoScreenId = UUID.randomUUID().toString();
        CustomerInfoScreenHolder holder = getByCustomerId(customerId);
        holder.registerInformationScreen(infoScreenId);
        saveObject(holder);
    }

    @Override
    public List<CustomerInfoScreenHolder> getHolders() {
        addAllCustomerHolders();
        return new ArrayList(holders.values());
    }

    private void addAllCustomerHolders() {
        for (User user : userManager.getAllUsers()) {
            getByCustomerId(user.id);
        }
    }

    @Override
    public List<SliderType> getTypes() {
        return sliderRepository.getTypes();
    }   

    @Override
    public void addSlider(Slider slider, String tvId) {
        CustomerInfoScreenHolder holder = getByCustomerHolderByTvId(tvId);
        if (holder != null) {
            holder.addSlider(slider, tvId);
            saveObject(holder);
        }
    }

    private CustomerInfoScreenHolder getByCustomerHolderByTvId(String tvId) {
        if (getSession() != null && getSession().currentUser != null && getSession().currentUser.type == 100) {
            for (CustomerInfoScreenHolder holder : getHolders()) {
                for (InfoScreen screen : holder.getScreens()) {
                    if (screen.infoScreenId != null && screen.infoScreenId.equals(tvId)) {
                        return holder;
                    }
                }
            }
        } else {
            return getHolder();
        }
        
        return null;
    }

    @Override
    public InfoScreen getScreen(String id) {
        for (CustomerInfoScreenHolder holder : getHolders()) {
            for (InfoScreen screen : holder.getScreens()) {
                if (screen.infoScreenId != null && screen.infoScreenId.equals(id)) {
                    return screen;
                }
            }
        }
        
        return null;
    }

    @Override
    public void deleteSlider(String sliderId, String tvId) {
        CustomerInfoScreenHolder holder = getByCustomerHolderByTvId(tvId);
        
        if (holder != null) {
            holder.deleteSlider(sliderId);
            saveObject(holder);
        }
    }

    @Override
    public void saveTv(InfoScreen tv) {
        CustomerInfoScreenHolder holder = getByCustomerHolderByTvId(tv.infoScreenId);
        if (holder != null) {
            holder.saveTv(tv);
            saveObject(holder);
        }
    }

    @Override
    public Feed getNews() {
        RSSFeedParser parser = new RSSFeedParser("http://www.nrk.no/verden/toppsaker.rss");
        return parser.readFeed();
    }
}