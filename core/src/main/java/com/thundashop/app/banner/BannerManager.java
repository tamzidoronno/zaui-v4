package com.thundashop.app.banner;

import com.getshop.scope.GetShopSession;
import com.thundashop.app.bannermanager.data.Banner;
import com.thundashop.app.bannermanager.data.BannerSet;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.databasemanager.data.DataRetreived;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
@GetShopSession
public class BannerManager extends ManagerBase implements IBannerManager {
    public HashMap<String, BannerSet> banners = new HashMap();
    private String id;
   
    @Override
    public void dataFromDatabase(DataRetreived data) {
        for (DataCommon retData : data.data)
            banners.put(retData.id, (BannerSet)retData);
    }

    private BannerSet saveBannerSet(String id, BannerSet bannerSet) throws ErrorException {
        if (bannerSet.storeId == null || bannerSet.storeId.equals(""))
            bannerSet.storeId = storeId;
        
        bannerSet.id = id;
        saveObject(bannerSet);
        banners.put(id, bannerSet);
        return bannerSet;
    }

    @Override
    public BannerSet saveSet(BannerSet set) throws ErrorException {
        BannerSet savedSet = findBannerSet(set.id);
        
        savedSet.height = set.height;
        savedSet.width = set.width;
        savedSet.interval = set.interval;
        savedSet.showDots = set.showDots;
        savedSet.banners = set.banners;
      
        saveObject(savedSet);
        return savedSet;
    }

    @Override
    public BannerSet deleteSet(String id) throws ErrorException {
        BannerSet set = findBannerSet(id);
        removeBannerSet(id);
        return set;
    }

    @Override
    public BannerSet addImage(String bannerSetId, String fileId) throws ErrorException {
        BannerSet set = findBannerSet(bannerSetId);
        if(set == null) {
           throw new ErrorException(857574848);
        }
        addImage(set, fileId);
        return set;
    }

    @Override
    public BannerSet removeImage(String bannerSetId, String fileId) throws ErrorException {
        BannerSet set = findBannerSet(bannerSetId);
        removeFromBannerSet(bannerSetId, fileId);
        return set;
    }

    @Override
    public BannerSet createSet(int width, int height, String id) throws ErrorException {
        BannerSet set = new BannerSet();
        set.height = height;
        set.width = width;
        if(id.trim().length() == 0) {
            id = UUID.randomUUID().toString();
        }
        return saveBannerSet(id, set);
    }

    @Override
    public BannerSet getSet(String id) throws ErrorException {
        return findBannerSet(id);
    }

    private BannerSet findBannerSet(String id) {
        if(banners == null) {
            banners = new HashMap();
        }
        BannerSet set = banners.get(id);
        if(set == null) {
            set = new BannerSet();
            set.storeId = storeId;
            banners.put(id, set);
            set.id = id;
        }
        
        
        return set;
    }

    private void addImage(BannerSet set, String fileId) throws ErrorException {
        if(set.banners == null) {
            set.banners = new ArrayList();
        }
        
        Banner banner = new Banner();
        banner.imageId = fileId;
        set.banners.add(banner);
        set.storeId = storeId;
        saveObject(set);
    }

    private void removeBannerSet(String id) throws ErrorException {
        BannerSet set = findBannerSet(id);
        banners.remove(set.id);
        
        deleteObject(set);
    }

    private void removeFromBannerSet(String bannerSetId, String fileId) throws ErrorException {
        BannerSet set = findBannerSet(bannerSetId);
        
        Banner toRemove = null;
        for(Banner banner : set.banners) {
            if(banner.imageId.equals(fileId)) {
                toRemove = banner;
            }
        }
        
        if(toRemove != null) {
            set.banners.remove(toRemove);
        }
        
        saveObject(set);
    }

    @Override
    public BannerSet linkProductToImage(String bannerSetId, String imageId, String productId) throws ErrorException {
        BannerSet bannerSet = findBannerSet(bannerSetId);
        for(Banner banner : bannerSet.banners) {
            if(banner.imageId.equals(imageId)) {
                banner.productId = productId;
                saveBannerSet(bannerSet.id, bannerSet);
                return bannerSet;
            }
        }
        throw new ErrorException(1010);
    }
}