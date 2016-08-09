package com.thundashop.core.apigenerator;

import com.thundashop.core.common.GetShopLogHandler;
import com.thundashop.core.common.ManagerSubBase;
import java.io.IOException;

/**
 *
 * @author ktonder
 */
public class GenerateApiLocalPal {
    public static void main(String[] args) throws ClassNotFoundException, IOException, Exception {
        GetShopLogHandler.logPrintStatic("Generating local api", null);
        GenerateApi ga = new GenerateApi("/home/boggi/projects/3.0.0/GetShop Core/", "/home/boggi/projects/3.0.0/GetShop Messages/", "/source/getshop/3.0.0/com.getshop.client/", "/source/getshop/3.0.0/core", false);
        ga.generate();

        ga = new GenerateApi("/home/boggi/projects/3.0.0/GetShop Core/", "/home/boggi/projects/3.0.0/GetShop Messages/", "/source/getshop/3.0.0/com.getshop.pullserver/", "/source/getshop/3.0.0/core", false);
        ga.generate();
    }
}
