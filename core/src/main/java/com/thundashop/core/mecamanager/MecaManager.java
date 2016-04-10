/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.mecamanager;

import com.getshop.scope.GetShopSession;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.messagemanager.MessageManager;
import com.thundashop.core.pagemanager.PageManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author emil
 */
@Component
@GetShopSession
public class MecaManager extends ManagerBase implements IMecaManager {

    @Autowired
    public PageManager pageManager;
    
    @Autowired
    public MessageManager messageManager;
    
    private Map<String, MecaFleet> fleets = new HashMap();
    private Map<String, MecaCar> cars = new HashMap();
    
    @Override
    public void dataFromDatabase(DataRetreived data) {
        
        for (DataCommon obj : data.data) {
            if (obj instanceof MecaFleet) {
                MecaFleet fleet = (MecaFleet) obj;
                fleets.put(fleet.id, fleet);
            }    
        }
        
        for (DataCommon obj : data.data) {
            // load workshops
           
            
            if (obj instanceof MecaCar) {
                MecaCar car = (MecaCar) obj;
                MecaFleet fleet = getFleetByCar(car);
                
                if (fleet == null) {
                    deleteCar(car.id);
                } else {
                    cars.put(car.id, car);
                }
            }
        }
    }

    @Override
    public MecaFleet createFleet(MecaFleet fleet) {
        saveObject(fleet);
        fleets.put(fleet.id, fleet);
        return fleet;
    }

    @Override
    public List<MecaFleet> getFleets() {
        fleets.values().stream().forEach(o -> finalize(o));
        return new ArrayList(fleets.values());
    }

    private void finalize(MecaFleet o) {
        if (o == null) {
            return;
        }
        
        if (o.pageId == null || o.pageId.isEmpty()) {
            o.pageId = pageManager.createPageFromTemplatePage("mecafleet_template_page").id;
            saveObject(o);
        }
        
        o.cars.removeIf(carId -> cars.get(carId) == null);
        checkAccess(o);
    }

    @Override
    public MecaFleet getFleetPageId(String pageId) {
        
        MecaFleet fleet = fleets.values().stream()
                .filter(o -> o.pageId.equals(pageId))
                .findFirst()
                .orElse(null);
        
        finalize(fleet);
        
        return fleet;
    }

    private void checkAccess(MecaFleet fleet) {
        // TODO
    }

    @Override
    public void saveFleetCar(String pageId, MecaCar car) {
        MecaFleet fleet = getFleetPageId(pageId);
        
        if (fleet == null) {
            fleet = getFleetThatCarBelongsTo(car);
        }
        
        finalize(fleet);
        
        if (fleet == null) {
            throw new NullPointerException("Why is there cars created without connections to a fleet?");
        }
        
        saveObject(car);
        finalize(car);
        cars.put(car.id, car);
        
        if (!fleet.cars.contains(car.id)) {
            fleet.cars.add(car.id);
            saveObject(fleet);
        }
        
        
    }

    @Override
    public List<MecaCar> getCarsForMecaFleet(String pageId) {
        MecaFleet fleet = getFleetPageId(pageId);
        finalize(fleet);
        
        if (fleet == null) {
            return new ArrayList();
        }
        
        List<MecaCar> retCars = fleet.cars.stream()
                .map(o -> cars.get(o))
                .filter(o -> o != null)
                .collect(Collectors.toList());
        
        retCars.forEach(o -> finalize(o));
        return retCars;
    }
    
    @Override
    public void deleteCar(String carId) {
        fleets.values().stream()
                .forEach(o -> removeCarFromList(o, carId));
        
        MecaCar car = cars.remove(carId);
        if (car != null) {
            deleteObject(car);
        }
    }

    private void removeCarFromList(MecaFleet fleet, String carId) {
        boolean removed = fleet.cars.remove(carId);
        if (removed) {
            saveObject(fleet);
        }
    }

    private void finalize(MecaCar car) {
        if (car == null) {
            return;
        }
        
        if (car.pageId == null || car.pageId.isEmpty()) {
            car.pageId = pageManager.createPageFromTemplatePage("meca_car_template").id;
            saveObject(car);
        }
        
        car.calculateNextValues();
        
    }

    @Override
    public MecaCar getCarByPageId(String pageId) {
        MecaCar car = cars.values().stream()
                .filter(o -> o.pageId != null && o.pageId.equals(pageId))
                .findFirst()
                .orElse(null);
        
        if (car == null) {
            return null;
        }
        
        MecaFleet fleet = getFleetThatCarBelongsTo(car);
        if (fleet == null) {
            throw new ErrorException(26);
        }
        
        checkAccess(fleet);
        
        return car;
    }

    private MecaFleet getFleetThatCarBelongsTo(MecaCar car) {
        return fleets.values().stream()
                .filter(o -> o.cars.contains(car.id))
                .findAny()
                .orElse(null);
    }

    @Override
    public List<MecaCar> getCarsPKKList() {
        cars.values().stream()
                .forEach(car -> finalize(car));
        
        List<MecaCar> allCars = new ArrayList(cars.values());
        Collections.sort(allCars, (MecaCar car1, MecaCar car2) -> {
            if (car1 == null || car2 == null || car1.nextControll == null || car2.nextControll == null) {
                return 0;
            }
            
            return car1.nextControll.compareTo(car2.nextControll);
        });
        
        return allCars;
    }

    @Override
    public MecaFleet getFleetByCar(MecaCar car) {
        MecaFleet fleet = getFleetThatCarBelongsTo(car);
        return fleet;
    }

    @Override
    public List<MecaCar> getCarsServiceList() {
        cars.values().stream()
                .forEach(car -> finalize(car));
        
        List<MecaCar> allCars = new ArrayList(cars.values());
        
        Collections.sort(allCars, (MecaCar car1, MecaCar car2) -> {
            if (car1 == null || car2 == null || car1.nextService == null || car2.nextService == null) {
                return 0;
            }
            
            return car1.nextService.compareTo(car2.nextService);
            
        });
        
        return allCars;
    }

    @Override
    public List<MecaCar> getCarsByCellphone(String cellPhone) {
        return cars.values().stream()
                .filter(car -> car.cellPhone != null && car.cellPhone.equals(cellPhone))
                .collect(Collectors.toList());
    }

    @Override
    public void callMe(String cellPhone) {
        String content = "Hei";
        content += "<br/>";
        content += "<br/> Jeg ønsker å bli oppringt på telefonnr: " + cellPhone;
        content += "<br/>";
        content += "<br/> Melding fra Meca Fleet APP";
        
        String subject = "Ønsker å bli oppringt";
        
        String storeName = getStoreName();
        String storeEmail = getStoreEmailAddress();
        
        messageManager.sendMail(storeEmail, storeName, subject, content, storeEmail, storeName);
    }

    public String nl2br(String text) {
        return text.replace("\n\n", "<p>").replace("\n", "<br>");
    }
    
    @Override
    public void sendEmail(String cellPhone, String message) {
        List<MecaCar> cars = getCarsByCellphone(cellPhone);
        
        MecaCar currentCar = null;
        if (cars.size() > 0) {
            currentCar = cars.get(0);
        }
        
        String content = "Fra: Kontaktskjema i MECA Fleet APP.";
        content += "<br/>";
        content += "<br/> =================================================";
        content += "<br/>" + nl2br(message);
        content += "<br/>" + nl2br(message);
        content += "<br/> =================================================";
        
        if (currentCar != null) {
            content += "<br/>";
            content += "<br/> Regnr: " + currentCar.licensePlate;
            content += "<br/> Telefonr: " + currentCar.cellPhone;
            content += "<br/>";
            content += "<br/> Melding fra MECA Fleet APP system";
        }
        
        String subject = "Melding fra MECA Fleet APP";
        
        String storeName = getStoreName();
        String storeEmail = getStoreEmailAddress();
        
        messageManager.sendMail(storeEmail, storeName, subject, content, storeEmail, storeName);
    }

    @Override
    public void sendKilometers(String cellPhone, int kilometers) {
        List<MecaCar> cars = getCarsByCellphone(cellPhone);
        
        MecaCar currentCar = null;
        if (cars.size() > 0) {
            currentCar = cars.get(0);
        } else {
            return;
        }
        
        currentCar.kilometers = kilometers;
        saveObject(currentCar);
    }

}
