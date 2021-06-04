package getshopiotserver;

import getshop.nets.GetShopNetsApp;
import getshop.nets.GetShopNetsController;

public class GetShopIOTServer {

    public static void stop(String[] args) {
        System.exit(0);
    }

    public static void main(String[] args) {
        System.out.println("Starting GetShop IOT server, taking part of the fourth industrial revolution and helping out on upgrading society into version 5.0");
        System.out.println("Server version: 0.0.1");

        if (args.length == 0 || args[0].isEmpty()) {
            System.out.println("Please specify config file");
            return;
        }
        
        try {

            GetShopIOTOperator operator = new GetShopIOTOperator(args[0]);
                        
            Thread toDo = new Thread(operator);
            toDo.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
        
        while(true) {
            try { Thread.sleep(1000); } catch (Exception ex) {}
        }
    }
}
