/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.mecamanager;

import com.getshop.scope.GetShopSession;
import com.thundashop.core.common.ApplicationInstance;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.listmanager.ListBadgetAware;
import com.thundashop.core.listmanager.data.Entry;
import com.thundashop.core.messagemanager.MessageManager;
import com.thundashop.core.mobilemanager.MobileManager;
import com.thundashop.core.pagemanager.PageManager;
import com.thundashop.core.storemanager.StoreManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
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
public class MecaManager extends ManagerBase implements IMecaManager, ListBadgetAware {

    @Autowired
    public PageManager pageManager;
    
    @Autowired
    public MessageManager messageManager;
    
    @Autowired
    public MobileManager mobileManager;
    
    @Autowired
    public StoreManager storeManager;

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
        
        car.finalizeCar();
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
        
        finalize(car);
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
        
        allCars.stream().forEach(o -> finalize(o));
        
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
        
        allCars.stream().forEach(o -> finalize(o));
        
        return allCars;
    }

    @Override
    public List<MecaCar> getCarsByCellphone(String cellPhone) {
        List<MecaCar> retCars = cars.values().stream()
                .filter(car -> car.cellPhone != null && car.cellPhone.equals(cellPhone))
                .collect(Collectors.toList());
        
        retCars.stream().forEach(o -> finalize(o));
        
        return retCars;
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
        currentCar.dateRequestedKilomters = null;
        for (String token : currentCar.tokens) {
            mobileManager.clearBadged(token);
        }
        saveObject(currentCar);
    }

    @Override
    public void registerDeviceToCar(String tokenId, String cellPhone) {
        MecaCar currentCar = getCarByCellphone(cellPhone);
        
        if (currentCar != null) {
            currentCar.tokens.add(tokenId);
            saveObject(currentCar);    
        }
    }

    @Override
    public void notifyByPush(String phoneNumber, String message) {
        MecaCar car = getCarByCellphone(phoneNumber);
        if (car != null) {
            for (String token : car.tokens) {
                mobileManager.sendMessage(token, message);
            }
        }
    }

    private MecaCar getCarByCellphone(String phoneNumber) {
        List<MecaCar> cars = getCarsByCellphone(phoneNumber);
        
        if (cars.size() > 0) {
            finalize(cars.get(0));
            return cars.get(0);
        } 
        
        return null;
    }

    @Override
    public void sendInvite(String mecaCarId) {
        MecaCar car = cars.get(mecaCarId);
        if (car != null) {
            String title = storeManager.getMyStore().identifier;
            String message = "Hei, du er nå meldt inn i MECA Fleet. Last ned appen på AppStore/Google play. Logg inn med verksted: " + title + " og telefonnret ditt som passord.";
            messageManager.sendSms("nexmo", car.cellPhone, message, getStoreDefaultPrefix());
            car.inivitationSent = new Date();
            saveObject(car);
        }
    }

    @Override
    public MecaCar getCar(String id) {
        MecaCar car = cars.get(id);
        if (car != null) {
            finalize(car);
            return car;
        }
        
        return null;
    }

    @Override
    public void requestNextService(String carId, Date date) {
        MecaCar car = getCar(carId);
        if (car != null) {
            car.nextServiceAgreed = date;
            car.nextServiceAcceptedByCarOwner = null;
            car.newSuggestedDate = null;

            saveObject(car);
            
            notifyByPush(car.cellPhone, "Din bil skal inn på service.");
        }
    }

    @Override
    public MecaCar answerServiceRequest(String carId, boolean answer) {
        MecaCar car = getCar(carId);
        if (car != null) {
            car.nextServiceAcceptedByCarOwner = answer;
            
            String subject = "Statusoppdatering..";
            String content = "Bil med registreringsnr " + car.licensePlate + " kunne ";
            
            if (car.agreeDate) {
                content += " møte opp til foreslått dato og tid";
                messageManager.sendMessageToStoreOwner(content, subject);
            } else {
                content += " <b>ikke</b> møte opp til foreslått dato og tid";
                messageManager.sendMessageToStoreOwner(content, subject);
            }
            
            for (String token : car.tokens) {
                mobileManager.clearBadged(token);
            }
            
            finalize(car);
            saveObject(car);
            return car;
        }
        
        return null;
    }
    
    @Override
    public MecaCar answerControlRequest(String carId, boolean answer) {
        MecaCar car = getCar(carId);
        if (car != null) {
            car.nextControlAcceptedByCarOwner = answer;
            car.newSuggestedDate = null;
            
            String subject = "Statusoppdatering..";
            String content = "Bil med registreringsnr " + car.licensePlate + " kunne ";
            
            if (car.agreeDateControl) {
                content += " møte opp til foreslått dato og tid for EU kontroll";
                messageManager.sendMessageToStoreOwner(content, subject);
            } else {
                content += " <b>ikke</b> møte opp til foreslått dato og tid for EU kontroll";
                messageManager.sendMessageToStoreOwner(content, subject);
            }
            
            for (String token : car.tokens) {
                mobileManager.clearBadged(token);
            }
            
            finalize(car);
            saveObject(car);
            return car;
        }
        
        return null;
    }

    @Override
    public void resetServiceInterval(String carId, Date date, int kilometers) {
        MecaCar car = getCar(carId);
        if (car != null) {
            car.resetService(date, kilometers);
            saveObject(car);
        }
    }

    @Override
    public void requestNextControl(String carId, Date date) {
        MecaCar car = getCar(carId);
        if (car != null) {
            car.nextControlAgreed = date;
            car.nextControlAcceptedByCarOwner = null;
            car.newSuggestedDate = null;
            saveObject(car);
            
            notifyByPush(car.cellPhone, "Din bil skal inn på EU Kontroll.");
        }
    }

    @Override
    public void markControlAsCompleted(String carId) {
        MecaCar car = getCar(carId);
        if (car != null) {
            car.markControlAsCompleted();
            saveObject(car);
        }
    }

    @Override
    public void sendKilometerRequest(String carId) {
        MecaCar car = getCar(carId);
        if (car != null) {
            car.dateRequestedKilomters = new Date();
            saveObject(car);
            notifyByPush(car.cellPhone, "Vi trenger din kilometerstand");
        }
    }

    @Override
    public int getBadges(Entry entry) {
        List<ApplicationInstance> apps = pageManager.getApplicationsForPage(entry.pageId);
        ApplicationInstance mecaFleetService = apps.stream().filter(app -> app.appSettingsId.equals("e4a506de-4702-4d82-8224-f30e5fdb1d2e"))
                .findAny()
                .orElse(null);

        ApplicationInstance mecaControl = apps.stream().filter(app -> app.appSettingsId.equals("b48c3e14-676d-4c9e-acfc-60591c711c57"))
                .findAny()
                .orElse(null);
        
        int i = 0;
        
        if (mecaFleetService != null) {
            for (MecaCar car : getCarsServiceList()) {
                finalize(car);
                if (car.needAttentionToService || car.serviceDateRejected) {
                    i++;
                }
            }
        }
        
        if (mecaControl != null) {
            for (MecaCar car : getCarsServiceList()) {
                finalize(car);
                if (car.canAgreeControlDate && car.nextControlAgreed == null) {
                    i++;
                }
            }
        }
        
        return i;
    }

    @Override
    public MecaCar suggestDate(String carId, Date date) {
        MecaCar car = getCar(carId);
        
        if (car != null) {
            car.newSuggestedDate = date;
            saveObject(car);
        }
        
        finalize(car);
        return car;
    }
}