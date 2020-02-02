package com.getshop.javaapi.pos;

import com.thundashop.core.common.Communicator;
public class Central {

      public Communicator transport;
      public Central(Communicator transporter) throws Exception {
           this.transport = transporter;
      }
     /**
      * @return APIStoreManager
      */
      public APIStoreManager getStoreManager() {
           return new APIStoreManager(transport);
      }
     /**
      * @return APISystemManager
      */
      public APISystemManager getSystemManager() {
           return new APISystemManager(transport);
      }
     /**
      * @return APIUserManager
      */
      public APIUserManager getUserManager() {
           return new APIUserManager(transport);
      }
     /**
      * @return APIGdsManager
      */
      public APIGdsManager getGdsManager() {
           return new APIGdsManager(transport);
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
}
