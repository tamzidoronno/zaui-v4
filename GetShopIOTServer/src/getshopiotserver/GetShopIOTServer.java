package getshopiotserver;

import getshop.nets.GetShopNetsApp;
import getshop.nets.GetShopNetsController;

public class GetShopIOTServer {

    public static void main(String[] args) {
        System.out.println("Starting GetShop IOT server, taking part of the fourth industrial revolution and helping out on upgrading society into version 5.0");
        System.out.println("Server version: 0.0.1");
        
        try {
            GetShopIOTOperator reader = new GetShopIOTOperator();
            boolean setup = reader.setup();
            if(!setup) {
                reader.readConfiguration();
                reader.setup();
            }
            reader.getPaymentOperator();
            reader.start();
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
}
