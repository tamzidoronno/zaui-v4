/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.thundashop.core.cartuning;

import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.GetShopApi;
import java.util.List;

/**
 *
 * @author boggi
 */
@GetShopApi
public interface ICarTuningManager {
    public List<CarTuningData> getCarTuningData(String id) throws ErrorException; 
}
