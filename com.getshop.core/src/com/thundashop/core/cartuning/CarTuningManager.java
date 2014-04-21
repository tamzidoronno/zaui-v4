/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.thundashop.core.cartuning;

import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.DatabaseSaver;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.Logger;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.databasemanager.data.DataRetreived;
import java.util.List;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 *
 * @author boggi
 */
@Component
@Scope("prototype")
public class CarTuningManager extends ManagerBase implements ICarTuningManager {
    private CarTuningCollection carTuningCollection = new CarTuningCollection();

    @Autowired
    public CarTuningManager(Logger log, DatabaseSaver databaseSaver) {
        super(log, databaseSaver);
    }

    @Override
    public void dataFromDatabase(DataRetreived data) {
        for (DataCommon dataCommon : data.data) {
            if (dataCommon instanceof CarTuningCollection) {
                this.carTuningCollection = (CarTuningCollection)dataCommon;
            }
        }
    }
    
    @Override
    public List<CarTuningData> getCarTuningData(String id) throws ErrorException {
        return carTuningCollection.entries;
    }

    @Override
    public void saveCarTuningData(List<CarTuningData> data) throws ErrorException {
        carTuningCollection.entries = data;
        carTuningCollection.storeId = storeId;
        databaseSaver.saveObject(carTuningCollection, credentials);
    }
}
