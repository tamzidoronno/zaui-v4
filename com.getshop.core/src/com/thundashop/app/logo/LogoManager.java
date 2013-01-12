package com.thundashop.app.logo;

import com.thundashop.app.logomanager.data.SavedLogo;
import com.thundashop.core.common.*;
import com.thundashop.core.databasemanager.data.DataRetreived;
import java.util.HashMap;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 *
 * @author boggi
 */
@Component
@Scope("prototype")
public class LogoManager extends ManagerBase implements ILogoManager {

    HashMap<String, SavedLogo> logos = new HashMap();
    
    @Override
    public void dataFromDatabase(DataRetreived data) {
        List<DataCommon> theData = data.data;
        for (int i = 0; i < theData.size(); i++) {
            if (theData.get(i) instanceof SavedLogo) {
                SavedLogo logo = (SavedLogo) theData.get(i);
                SavedLogo curLogo = logos.get(logo.storeId);
                if (curLogo == null || (curLogo.rowCreatedDate.getTime() < logo.rowCreatedDate.getTime())) {
                    logos.put(logo.storeId, logo);
                }
            }
        }
    }

    @Autowired
    public LogoManager(DatabaseSaver databaseSaver, Logger logger) throws ErrorException {
        super(logger, databaseSaver);
    }

    @Override
    public void setLogo(String fileId) throws ErrorException {
        SavedLogo logo = saveLogo(fileId);
    }

    @Override
    public void deleteLogo() throws ErrorException {
        SavedLogo logo = getSavedLogo();
        if(logo == null) {
            throw new ErrorException(1000006);
        }
        databaseSaver.deleteObject(logo, credentials);
        logos.remove(storeId);
    }

    @Override
    public SavedLogo getLogo() throws ErrorException {
        SavedLogo logo = getSavedLogo();
        if(logo != null) {
            return logo;
        }
        return new SavedLogo();
    }

    private SavedLogo saveLogo(String fileId) throws ErrorException {
        SavedLogo savedLogo = new SavedLogo();
        savedLogo.LogoId = fileId;
        savedLogo.storeId = storeId;
        
        databaseSaver.saveObject(savedLogo, credentials);
        logos.put(storeId, savedLogo);
        
        return savedLogo;
    }

    private SavedLogo getSavedLogo() throws ErrorException {
        SavedLogo logo = logos.get(storeId);
        
        return logo;
    }
}
