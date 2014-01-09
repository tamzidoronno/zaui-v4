package com.getshop.javaapi;

public class GetShopApi {

      public Transporter transport;
      public GetShopApi(int port, String host, String sessionId, String webaddress) throws Exception {
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
      * @return APIMobileManager
      */
      public APIMobileManager getMobileManager() {
           return new APIMobileManager(transport);
      }
     /**
      * @return APIOrderManager
      */
      public APIOrderManager getOrderManager() {
           return new APIOrderManager(transport);
      }
     /**
      * @return APIGalleryManager
      */
      public APIGalleryManager getGalleryManager() {
           return new APIGalleryManager(transport);
      }
     /**
      * @return APIStoreManager
      */
      public APIStoreManager getStoreManager() {
           return new APIStoreManager(transport);
      }
     /**
      * @return APIPageManager
      */
      public APIPageManager getPageManager() {
           return new APIPageManager(transport);
      }
     /**
      * @return APIUtilManager
      */
      public APIUtilManager getUtilManager() {
           return new APIUtilManager(transport);
      }
     /**
      * @return APIListManager
      */
      public APIListManager getListManager() {
           return new APIListManager(transport);
      }
     /**
      * @return APIMessageManager
      */
      public APIMessageManager getMessageManager() {
           return new APIMessageManager(transport);
      }
     /**
      * @return APICalendarManager
      */
      public APICalendarManager getCalendarManager() {
           return new APICalendarManager(transport);
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
      * @return APIAppManager
      */
      public APIAppManager getAppManager() {
           return new APIAppManager(transport);
      }
     /**
      * @return APIGetShop
      */
      public APIGetShop getGetShop() {
           return new APIGetShop(transport);
      }
     /**
      * @return APIChatManager
      */
      public APIChatManager getChatManager() {
           return new APIChatManager(transport);
      }
     /**
      * @return APIReportingManager
      */
      public APIReportingManager getReportingManager() {
           return new APIReportingManager(transport);
      }
     /**
      * @return APIInvoiceManager
      */
      public APIInvoiceManager getInvoiceManager() {
           return new APIInvoiceManager(transport);
      }
     /**
      * @return APIYouTubeManager
      */
      public APIYouTubeManager getYouTubeManager() {
           return new APIYouTubeManager(transport);
      }
     /**
      * @return APIProductManager
      */
      public APIProductManager getProductManager() {
           return new APIProductManager(transport);
      }
     /**
      * @return APIContentManager
      */
      public APIContentManager getContentManager() {
           return new APIContentManager(transport);
      }
     /**
      * @return APIBannerManager
      */
      public APIBannerManager getBannerManager() {
           return new APIBannerManager(transport);
      }
     /**
      * @return APILogoManager
      */
      public APILogoManager getLogoManager() {
           return new APILogoManager(transport);
      }
     /**
      * @return APIFooterManager
      */
      public APIFooterManager getFooterManager() {
           return new APIFooterManager(transport);
      }
     /**
      * @return APINewsManager
      */
      public APINewsManager getNewsManager() {
           return new APINewsManager(transport);
      }
}
