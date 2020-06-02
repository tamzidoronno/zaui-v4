package com.getshop.javaapi.pos;

import com.thundashop.core.common.Communicator;
public class Pos {

      public Communicator transport;
      public Pos(Communicator transporter) throws Exception {
           this.transport = transporter;
      }
     /**
      * @return APIPaymentTerminalManager
      */
      public APIPaymentTerminalManager getPaymentTerminalManager() {
           return new APIPaymentTerminalManager(transport);
      }
     /**
      * @return APIWareHouseManager
      */
      public APIWareHouseManager getWareHouseManager() {
           return new APIWareHouseManager(transport);
      }
     /**
      * @return APIStoreManager
      */
      public APIStoreManager getStoreManager() {
           return new APIStoreManager(transport);
      }
     /**
      * @return APICartManager
      */
      public APICartManager getCartManager() {
           return new APICartManager(transport);
      }
     /**
      * @return APIUserManager
      */
      public APIUserManager getUserManager() {
           return new APIUserManager(transport);
      }
     /**
      * @return APIOrderManager
      */
      public APIOrderManager getOrderManager() {
           return new APIOrderManager(transport);
      }
     /**
      * @return APIGetShopCentral
      */
      public APIGetShopCentral getGetShopCentral() {
           return new APIGetShopCentral(transport);
      }
     /**
      * @return APIGdsManager
      */
      public APIGdsManager getGdsManager() {
           return new APIGdsManager(transport);
      }
     /**
      * @return APIPmsPosManager
      */
      public APIPmsPosManager getPmsPosManager() {
           return new APIPmsPosManager(transport);
      }
     /**
      * @return APIPosManager
      */
      public APIPosManager getPosManager() {
           return new APIPosManager(transport);
      }
     /**
      * @return APIPaymentManager
      */
      public APIPaymentManager getPaymentManager() {
           return new APIPaymentManager(transport);
      }
     /**
      * @return APIListManager
      */
      public APIListManager getListManager() {
           return new APIListManager(transport);
      }
     /**
      * @return APIImageManager
      */
      public APIImageManager getImageManager() {
           return new APIImageManager(transport);
      }
     /**
      * @return APIUtilManager
      */
      public APIUtilManager getUtilManager() {
           return new APIUtilManager(transport);
      }
     /**
      * @return APIProductManager
      */
      public APIProductManager getProductManager() {
           return new APIProductManager(transport);
      }
     /**
      * @return APINewsLetterManager
      */
      public APINewsLetterManager getNewsLetterManager() {
           return new APINewsLetterManager(transport);
      }
     /**
      * @return APIMessageManager
      */
      public APIMessageManager getMessageManager() {
           return new APIMessageManager(transport);
      }
     /**
      * @return APIGetShop
      */
      public APIGetShop getGetShop() {
           return new APIGetShop(transport);
      }
     /**
      * @return APIPrintManager
      */
      public APIPrintManager getPrintManager() {
           return new APIPrintManager(transport);
      }
     /**
      * @return APIStorePrintManager
      */
      public APIStorePrintManager getStorePrintManager() {
           return new APIStorePrintManager(transport);
      }
}
