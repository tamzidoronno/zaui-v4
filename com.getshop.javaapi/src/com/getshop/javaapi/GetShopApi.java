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
      * @return APIMecaApi
      */
      public APIMecaApi getMecaApi() {
           return new APIMecaApi(transport);
      }
     /**
      * @return APIOrderManager
      */
      public APIOrderManager getOrderManager() {
           return new APIOrderManager(transport);
      }
     /**
      * @return APIGetShop
      */
      public APIGetShop getGetShop() {
           return new APIGetShop(transport);
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
      * @return APIArxManager
      */
      public APIArxManager getArxManager() {
           return new APIArxManager(transport);
      }
     /**
      * @return APIUserManager
      */
      public APIUserManager getUserManager() {
           return new APIUserManager(transport);
      }
     /**
      * @return APIInformationScreenManager
      */
      public APIInformationScreenManager getInformationScreenManager() {
           return new APIInformationScreenManager(transport);
      }
     /**
      * @return APIMessageManager
      */
      public APIMessageManager getMessageManager() {
           return new APIMessageManager(transport);
      }
     /**
      * @return APINewsLetterManager
      */
      public APINewsLetterManager getNewsLetterManager() {
           return new APINewsLetterManager(transport);
      }
     /**
      * @return APICalendarManager
      */
      public APICalendarManager getCalendarManager() {
           return new APICalendarManager(transport);
      }
     /**
      * @return APIBigStock
      */
      public APIBigStock getBigStock() {
           return new APIBigStock(transport);
      }
     /**
      * @return APIStoreApplicationPool
      */
      public APIStoreApplicationPool getStoreApplicationPool() {
           return new APIStoreApplicationPool(transport);
      }
     /**
      * @return APIStoreApplicationInstancePool
      */
      public APIStoreApplicationInstancePool getStoreApplicationInstancePool() {
           return new APIStoreApplicationInstancePool(transport);
      }
     /**
      * @return APIGetShopApplicationPool
      */
      public APIGetShopApplicationPool getGetShopApplicationPool() {
           return new APIGetShopApplicationPool(transport);
      }
     /**
      * @return APIBrainTreeManager
      */
      public APIBrainTreeManager getBrainTreeManager() {
           return new APIBrainTreeManager(transport);
      }
     /**
      * @return APICarTuningManager
      */
      public APICarTuningManager getCarTuningManager() {
           return new APICarTuningManager(transport);
      }
     /**
      * @return APICartManager
      */
      public APICartManager getCartManager() {
           return new APICartManager(transport);
      }
     /**
      * @return APIHotelBookingManager
      */
      public APIHotelBookingManager getHotelBookingManager() {
           return new APIHotelBookingManager(transport);
      }
     /**
      * @return APIProductManager
      */
      public APIProductManager getProductManager() {
           return new APIProductManager(transport);
      }
     /**
      * @return APIMobileManager
      */
      public APIMobileManager getMobileManager() {
           return new APIMobileManager(transport);
      }
     /**
      * @return APIStoreManager
      */
      public APIStoreManager getStoreManager() {
           return new APIStoreManager(transport);
      }
     /**
      * @return APIGalleryManager
      */
      public APIGalleryManager getGalleryManager() {
           return new APIGalleryManager(transport);
      }
     /**
      * @return APICertegoManager
      */
      public APICertegoManager getCertegoManager() {
           return new APICertegoManager(transport);
      }
     /**
      * @return APIListManager
      */
      public APIListManager getListManager() {
           return new APIListManager(transport);
      }
     /**
      * @return APIChatManager
      */
      public APIChatManager getChatManager() {
           return new APIChatManager(transport);
      }
     /**
      * @return APIBannerManager
      */
      public APIBannerManager getBannerManager() {
           return new APIBannerManager(transport);
      }
     /**
      * @return APINewsManager
      */
      public APINewsManager getNewsManager() {
           return new APINewsManager(transport);
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
      * @return APIContentManager
      */
      public APIContentManager getContentManager() {
           return new APIContentManager(transport);
      }
}
