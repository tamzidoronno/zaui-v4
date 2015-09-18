package com.getshop.pullserver;

public class GetShopApiPullserver {

      public Transporter transport;
      public GetShopApiPullserver(int port, String host, String sessionId, String webaddress) throws Exception {
           this.transport = new Transporter();
           this.transport.port = port;
           this.transport.host = host;
           this.transport.sessionId = sessionId;
           this.transport.webaddress = webaddress;
           this.transport.api = this;
           this.transport.connect();
           
           getStoreManager().initializeStore(webaddress, sessionId);
      }
    /**
      * @return APIPullServerManager
      */
      public APIPullServerManager getPullServerManager() {
           return new APIPullServerManager(transport);
      }
      /**
      * @return APIStoreManager
      */
      public APIStoreManager getStoreManager() {
           return new APIStoreManager(transport);
      }
     
}
