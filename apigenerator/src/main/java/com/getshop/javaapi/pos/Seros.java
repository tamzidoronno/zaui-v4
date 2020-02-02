package com.getshop.javaapi.pos;

import com.thundashop.core.common.Communicator;
public class Seros {

      public Communicator transport;
      public Seros(Communicator transporter) throws Exception {
           this.transport = transporter;
      }
     /**
      * @return APIStoreManager
      */
      public APIStoreManager getStoreManager() {
           return new APIStoreManager(transport);
      }
     /**
      * @return APIUserManager
      */
      public APIUserManager getUserManager() {
           return new APIUserManager(transport);
      }
     /**
      * @return APIApiManager
      */
      public APIApiManager getApiManager() {
           return new APIApiManager(transport);
      }
     /**
      * @return APIGdsManager
      */
      public APIGdsManager getGdsManager() {
           return new APIGdsManager(transport);
      }
     /**
      * @return APISerosLockSystem
      */
      public APISerosLockSystem getSerosLockSystem() {
           return new APISerosLockSystem(transport);
      }
     /**
      * @return APIKeyManager
      */
      public APIKeyManager getKeyManager() {
           return new APIKeyManager(transport);
      }
     /**
      * @return APIUtilManager
      */
      public APIUtilManager getUtilManager() {
           return new APIUtilManager(transport);
      }
     /**
      * @return APIMessageManager
      */
      public APIMessageManager getMessageManager() {
           return new APIMessageManager(transport);
      }
     /**
      * @return APISmartHubManager
      */
      public APISmartHubManager getSmartHubManager() {
           return new APISmartHubManager(transport);
      }
}
