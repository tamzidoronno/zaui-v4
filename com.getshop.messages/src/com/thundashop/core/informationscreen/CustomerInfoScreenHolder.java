/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.informationscreen;

import com.thundashop.core.common.DataCommon;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 *
 * @author ktonder
 */
public class CustomerInfoScreenHolder extends DataCommon  {
    private String customerId = "";
    private int tvCounter = 0;
    private List<InfoScreen> screens = new ArrayList();
    
    public List<InfoScreen> getScreens() {
        return screens;
    }

    public CustomerInfoScreenHolder() {
    }
    
    public CustomerInfoScreenHolder(String customerId) {
        this.customerId = customerId;
    }
    
    public String getCustomerId() {
        return customerId;
    }

    public void registerInformationScreen(String infoScreenId) {
        tvCounter++;
        InfoScreen screen = new InfoScreen();
        screen.infoScreenId = infoScreenId;
        screen.name = ""+tvCounter;
        screens.add(screen);
    }

    public void addSlider(Slider slider, String tvId) {
        if (slider.id == null || slider.id.isEmpty()) {
            slider.id = UUID.randomUUID().toString();
        }
        
        for (InfoScreen screen : screens) {
            if (screen.infoScreenId.equals(tvId)) {
                screen.sliders.put(slider.id, slider);
            }
        }
    }

    public void deleteSlider(String sliderId) {
        for (InfoScreen screen : screens) {
            screen.sliders.remove(sliderId);
        }
    }
}
