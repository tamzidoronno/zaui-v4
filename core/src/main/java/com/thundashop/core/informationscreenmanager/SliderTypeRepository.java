/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.informationscreenmanager;

import com.getshop.scope.GetShopSession;
import com.thundashop.core.informationscreen.SliderType;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
@GetShopSession
public class SliderTypeRepository {
    public List<SliderType> types = new ArrayList();
    
    public SliderTypeRepository() {
        SliderType type = new SliderType();
        type.name = "slider1";
        types.add(type);
    }
    
    
    public List<SliderType> getTypes() {
        return types;
    }
}
