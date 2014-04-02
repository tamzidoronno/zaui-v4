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
    
    @PostConstruct
    public void setupTestData() {
        CarTuningData personbiler = new CarTuningData();
        personbiler.name = "Personbiler";
        
        CarTuningData audi = new CarTuningData();
        audi.name = "Audi";
        
        CarTuningData vw = new CarTuningData();
        vw.name = "VW";
        
        personbiler.subEntries.add(audi);
        personbiler.subEntries.add(vw);
        
        CarTuningData trucks = new CarTuningData();
        trucks.name = "Lastebiler";
        
        CarTuningData boats = new CarTuningData();
        boats.name = "Boats";
        
        carTuningCollection.entries.add(personbiler);
        carTuningCollection.entries.add(trucks);
        carTuningCollection.entries.add(boats);
        carTuningCollection.entries.add(boats);
    }
    
    @Override
    public List<CarTuningData> getCarTuningData(String id) throws ErrorException {
        return carTuningCollection.entries;
    }
    
}
