/*
 */
package com.thundashop.app.bannermanager.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ktonder
 */
public class Banner implements Serializable {
    public String imageId;
    public String productId;
    public String link;
    public int rotation;
    public String crop_cordinates;
    public boolean isDefault = false;
    public List<BannerText> imagetext = new ArrayList();
}
