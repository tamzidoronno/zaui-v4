package com.thundashop.core.databasemanager.updatescripts;

import com.thundashop.app.content.ContentManager;
import com.thundashop.app.contentmanager.data.ContentData;
import com.thundashop.core.common.AppConfiguration;
import com.thundashop.core.common.Setting;
import com.thundashop.core.pagemanager.data.Page;
import com.thundashop.core.productmanager.data.Product;
import com.thundashop.core.productmanager.data.ProductImage;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;

public class ConvertToNewProductType extends UpgradeBase {

    public static void main(String[] args) throws UnknownHostException {
        ConvertToNewProductType converter = new ConvertToNewProductType();
        converter.doConvert();
    }

    public void doConvert() throws UnknownHostException {
        HashMap<String, Product> products = getAllProducts();
        HashMap<String, Page> pages = getAllPages();

        for (Product prod : products.values()) {
            Page page = pages.get(prod.pageId);
            if (page != null) {
                if(page.pageType == 2) {
                    continue;
                }
                
                System.out.println(page.id);
                page.pageType = 2;
                page.type = 31;
                page.title = prod.name;
                page.clear();

                AppConfiguration app = createApp("Product", "06f9d235-9dd3-4971-9b91-88231ae0436b");
                app.storeId = page.storeId;
                saveObject(app, "PageManager");
                addApplicationToPage(page, "product", app.id);
                saveObject(page, "PageManager");

                app = createApp("ImageDisplayer", "831647b5-6a63-4c46-a3a3-1b4a7c36710a");

                for (ProductImage img : prod.images.values()) {
                    if (img.type == 0) {
                        Setting setting = new Setting();
                        setting.type = "image";
                        setting.value = img.fileId;

                        Setting setting_orig = new Setting();
                        setting_orig.type = "original_image";
                        setting_orig.value = img.fileId;

                        app.settings = new HashMap();
                        app.settings.put("image", setting);
                        app.settings.put("original_image", setting);
                    }
                }


                app.storeId = page.storeId;
                saveObject(app, "PageManager");
                addApplicationToPage(page, "col_1", app.id);
                saveObject(page, "PageManager");


                app = createApp("ContentManager", "320ada5b-a53a-46d2-99b2-9b0b26a7105a");
                app.storeId = page.storeId;
                saveObject(app, "PageManager");
                addApplicationToPage(page, "col_2", app.id);

                ContentData content = new ContentData();
                content.storeId = page.storeId;
                content.content = prod.shortDescription;
                content.appId = app.id;
                saveObject(content, "ContentManager");

                saveObject(page, "PageManager");

            }
        }

        //Convert applications
        HashMap<String, AppConfiguration> apps = getAppAplications();
        for(AppConfiguration app : apps.values()) {
            if(app.appName.equals("ProductList")) {
                app.appName = "ProductLister";
                app.appSettingsId = "962ce2bb-1684-41e4-8896-54b5d24392bf";
                saveObject(app, "PageManager");
            }
            if(app.appName.equals("ProductManager")) {
                app.appName = "ProductWidget";
                app.appSettingsId = "b741283d-920d-460b-8c08-fad5ef4294cb";
                saveObject(app, "PageManager");
            }
        }
    }
}
