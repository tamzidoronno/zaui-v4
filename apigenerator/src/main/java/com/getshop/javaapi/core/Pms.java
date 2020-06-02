package com.getshop.javaapi.core;

import com.thundashop.core.common.Communicator;
public class Pms {

      public Communicator transport;
      public Pms(Communicator transporter) throws Exception {
           this.transport = transporter;
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
      * @return APIPullServerManager
      */
      public APIPullServerManager getPullServerManager() {
           return new APIPullServerManager(transport);
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
      * @return APIEpayManager
      */
      public APIEpayManager getEpayManager() {
           return new APIEpayManager(transport);
      }
     /**
      * @return APIBrainTreeManager
      */
      public APIBrainTreeManager getBrainTreeManager() {
           return new APIBrainTreeManager(transport);
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
      * @return APICertegoManager
      */
      public APICertegoManager getCertegoManager() {
           return new APICertegoManager(transport);
      }
     /**
      * @return APISystemManager
      */
      public APISystemManager getSystemManager() {
           return new APISystemManager(transport);
      }
     /**
      * @return APITrackerManager
      */
      public APITrackerManager getTrackerManager() {
           return new APITrackerManager(transport);
      }
     /**
      * @return APIDibsManager
      */
      public APIDibsManager getDibsManager() {
           return new APIDibsManager(transport);
      }
     /**
      * @return APIOAuthManager
      */
      public APIOAuthManager getOAuthManager() {
           return new APIOAuthManager(transport);
      }
     /**
      * @return APIFileManager
      */
      public APIFileManager getFileManager() {
           return new APIFileManager(transport);
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
      * @return APIMecaManager
      */
      public APIMecaManager getMecaManager() {
           return new APIMecaManager(transport);
      }
     /**
      * @return APIScormManager
      */
      public APIScormManager getScormManager() {
           return new APIScormManager(transport);
      }
     /**
      * @return APISearchManager
      */
      public APISearchManager getSearchManager() {
           return new APISearchManager(transport);
      }
     /**
      * @return APIStoreApplicationPool
      */
      public APIStoreApplicationPool getStoreApplicationPool() {
           return new APIStoreApplicationPool(transport);
      }
     /**
      * @return APIGetShopApplicationPool
      */
      public APIGetShopApplicationPool getGetShopApplicationPool() {
           return new APIGetShopApplicationPool(transport);
      }
     /**
      * @return APIStoreApplicationInstancePool
      */
      public APIStoreApplicationInstancePool getStoreApplicationInstancePool() {
           return new APIStoreApplicationInstancePool(transport);
      }
     /**
      * @return APISupportManager
      */
      public APISupportManager getSupportManager() {
           return new APISupportManager(transport);
      }
     /**
      * @return APIGiftCardManager
      */
      public APIGiftCardManager getGiftCardManager() {
           return new APIGiftCardManager(transport);
      }
     /**
      * @return APIPgaManager
      */
      public APIPgaManager getPgaManager() {
           return new APIPgaManager(transport);
      }
     /**
      * @return APIBamboraManager
      */
      public APIBamboraManager getBamboraManager() {
           return new APIBamboraManager(transport);
      }
     /**
      * @return APISalesManager
      */
      public APISalesManager getSalesManager() {
           return new APISalesManager(transport);
      }
     /**
      * @return APIGetShopLockSystemManager
      */
      public APIGetShopLockSystemManager getGetShopLockSystemManager() {
           return new APIGetShopLockSystemManager(transport);
      }
     /**
      * @return APIGetShopLockManager
      */
      public APIGetShopLockManager getGetShopLockManager() {
           return new APIGetShopLockManager(transport);
      }
     /**
      * @return APIDBBackupManager
      */
      public APIDBBackupManager getDBBackupManager() {
           return new APIDBBackupManager(transport);
      }
     /**
      * @return APIStoreOcrManager
      */
      public APIStoreOcrManager getStoreOcrManager() {
           return new APIStoreOcrManager(transport);
      }
     /**
      * @return APIOcrManager
      */
      public APIOcrManager getOcrManager() {
           return new APIOcrManager(transport);
      }
     /**
      * @return APIExcelManager
      */
      public APIExcelManager getExcelManager() {
           return new APIExcelManager(transport);
      }
     /**
      * @return APIVippsManager
      */
      public APIVippsManager getVippsManager() {
           return new APIVippsManager(transport);
      }
     /**
      * @return APIQuestBackManager
      */
      public APIQuestBackManager getQuestBackManager() {
           return new APIQuestBackManager(transport);
      }
     /**
      * @return APIAmestoManager
      */
      public APIAmestoManager getAmestoManager() {
           return new APIAmestoManager(transport);
      }
     /**
      * @return APIOrderManager
      */
      public APIOrderManager getOrderManager() {
           return new APIOrderManager(transport);
      }
     /**
      * @return APIEhfXmlGenerator
      */
      public APIEhfXmlGenerator getEhfXmlGenerator() {
           return new APIEhfXmlGenerator(transport);
      }
     /**
      * @return APIGetShopCentral
      */
      public APIGetShopCentral getGetShopCentral() {
           return new APIGetShopCentral(transport);
      }
     /**
      * @return APISimpleEventManager
      */
      public APISimpleEventManager getSimpleEventManager() {
           return new APISimpleEventManager(transport);
      }
     /**
      * @return APIEventBookingManager
      */
      public APIEventBookingManager getEventBookingManager() {
           return new APIEventBookingManager(transport);
      }
     /**
      * @return APIAccountingManager
      */
      public APIAccountingManager getAccountingManager() {
           return new APIAccountingManager(transport);
      }
     /**
      * @return APIGdsManager
      */
      public APIGdsManager getGdsManager() {
           return new APIGdsManager(transport);
      }
     /**
      * @return APIGalleryManager
      */
      public APIGalleryManager getGalleryManager() {
           return new APIGalleryManager(transport);
      }
     /**
      * @return APIYouTubeManager
      */
      public APIYouTubeManager getYouTubeManager() {
           return new APIYouTubeManager(transport);
      }
     /**
      * @return APIExternalPosManager
      */
      public APIExternalPosManager getExternalPosManager() {
           return new APIExternalPosManager(transport);
      }
     /**
      * @return APIPosManager
      */
      public APIPosManager getPosManager() {
           return new APIPosManager(transport);
      }
     /**
      * @return APIFtpManager
      */
      public APIFtpManager getFtpManager() {
           return new APIFtpManager(transport);
      }
     /**
      * @return APIMekonomenManager
      */
      public APIMekonomenManager getMekonomenManager() {
           return new APIMekonomenManager(transport);
      }
     /**
      * @return APIGmailApiManager
      */
      public APIGmailApiManager getGmailApiManager() {
           return new APIGmailApiManager(transport);
      }
     /**
      * @return APIPkkControlManager
      */
      public APIPkkControlManager getPkkControlManager() {
           return new APIPkkControlManager(transport);
      }
     /**
      * @return APIChecklistManager
      */
      public APIChecklistManager getChecklistManager() {
           return new APIChecklistManager(transport);
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
      * @return APIComfortManager
      */
      public APIComfortManager getComfortManager() {
           return new APIComfortManager(transport);
      }
     /**
      * @return APIVerifoneManager
      */
      public APIVerifoneManager getVerifoneManager() {
           return new APIVerifoneManager(transport);
      }
     /**
      * @return APITicketManager
      */
      public APITicketManager getTicketManager() {
           return new APITicketManager(transport);
      }
     /**
      * @return APICustomerTicketManager
      */
      public APICustomerTicketManager getCustomerTicketManager() {
           return new APICustomerTicketManager(transport);
      }
     /**
      * @return APIPmsEventManager
      */
      public APIPmsEventManager getPmsEventManager() {
           return new APIPmsEventManager(transport);
      }
     /**
      * @return APIReportingManager
      */
      public APIReportingManager getReportingManager() {
           return new APIReportingManager(transport);
      }
     /**
      * @return APIUUIDSecurityManager
      */
      public APIUUIDSecurityManager getUUIDSecurityManager() {
           return new APIUUIDSecurityManager(transport);
      }
     /**
      * @return APIPmsBookingProcess
      */
      public APIPmsBookingProcess getPmsBookingProcess() {
           return new APIPmsBookingProcess(transport);
      }
     /**
      * @return APIProductManager
      */
      public APIProductManager getProductManager() {
           return new APIProductManager(transport);
      }
     /**
      * @return APISendRegningManager
      */
      public APISendRegningManager getSendRegningManager() {
           return new APISendRegningManager(transport);
      }
     /**
      * @return APIWebManager
      */
      public APIWebManager getWebManager() {
           return new APIWebManager(transport);
      }
     /**
      * @return APIBackupManager
      */
      public APIBackupManager getBackupManager() {
           return new APIBackupManager(transport);
      }
     /**
      * @return APIDepartmentManager
      */
      public APIDepartmentManager getDepartmentManager() {
           return new APIDepartmentManager(transport);
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
      * @return APIInformationScreenManager
      */
      public APIInformationScreenManager getInformationScreenManager() {
           return new APIInformationScreenManager(transport);
      }
     /**
      * @return APIPmsAddonManager
      */
      public APIPmsAddonManager getPmsAddonManager() {
           return new APIPmsAddonManager(transport);
      }
     /**
      * @return APIPmsGetShopOverView
      */
      public APIPmsGetShopOverView getPmsGetShopOverView() {
           return new APIPmsGetShopOverView(transport);
      }
     /**
      * @return APIPmsWebBookingManager
      */
      public APIPmsWebBookingManager getPmsWebBookingManager() {
           return new APIPmsWebBookingManager(transport);
      }
     /**
      * @return APIPmsManagerProcessor
      */
      public APIPmsManagerProcessor getPmsManagerProcessor() {
           return new APIPmsManagerProcessor(transport);
      }
     /**
      * @return APISmsHistoryManager
      */
      public APISmsHistoryManager getSmsHistoryManager() {
           return new APISmsHistoryManager(transport);
      }
     /**
      * @return APIPmsSelfManagement
      */
      public APIPmsSelfManagement getPmsSelfManagement() {
           return new APIPmsSelfManagement(transport);
      }
     /**
      * @return APIPmsInvoiceManager
      */
      public APIPmsInvoiceManager getPmsInvoiceManager() {
           return new APIPmsInvoiceManager(transport);
      }
     /**
      * @return APIPmsCoverageAndIncomeReportManager
      */
      public APIPmsCoverageAndIncomeReportManager getPmsCoverageAndIncomeReportManager() {
           return new APIPmsCoverageAndIncomeReportManager(transport);
      }
     /**
      * @return APIPmsManager
      */
      public APIPmsManager getPmsManager() {
           return new APIPmsManager(transport);
      }
     /**
      * @return APIPmsConferenceManager
      */
      public APIPmsConferenceManager getPmsConferenceManager() {
           return new APIPmsConferenceManager(transport);
      }
     /**
      * @return APIPmsNotificationManager
      */
      public APIPmsNotificationManager getPmsNotificationManager() {
           return new APIPmsNotificationManager(transport);
      }
     /**
      * @return APIPmsPaymentTerminal
      */
      public APIPmsPaymentTerminal getPmsPaymentTerminal() {
           return new APIPmsPaymentTerminal(transport);
      }
     /**
      * @return APIPmsReportManager
      */
      public APIPmsReportManager getPmsReportManager() {
           return new APIPmsReportManager(transport);
      }
     /**
      * @return APICareTakerManager
      */
      public APICareTakerManager getCareTakerManager() {
           return new APICareTakerManager(transport);
      }
     /**
      * @return APITimeRegisteringManager
      */
      public APITimeRegisteringManager getTimeRegisteringManager() {
           return new APITimeRegisteringManager(transport);
      }
     /**
      * @return APIStripeManager
      */
      public APIStripeManager getStripeManager() {
           return new APIStripeManager(transport);
      }
     /**
      * @return APIGetShopAccountingManager
      */
      public APIGetShopAccountingManager getGetShopAccountingManager() {
           return new APIGetShopAccountingManager(transport);
      }
     /**
      * @return APIGetShop
      */
      public APIGetShop getGetShop() {
           return new APIGetShop(transport);
      }
     /**
      * @return APITrackAndTraceManager
      */
      public APITrackAndTraceManager getTrackAndTraceManager() {
           return new APITrackAndTraceManager(transport);
      }
     /**
      * @return APIDirectorManager
      */
      public APIDirectorManager getDirectorManager() {
           return new APIDirectorManager(transport);
      }
     /**
      * @return APIBigStock
      */
      public APIBigStock getBigStock() {
           return new APIBigStock(transport);
      }
     /**
      * @return APIDoorManager
      */
      public APIDoorManager getDoorManager() {
           return new APIDoorManager(transport);
      }
     /**
      * @return APISedoxProductManager
      */
      public APISedoxProductManager getSedoxProductManager() {
           return new APISedoxProductManager(transport);
      }
     /**
      * @return APIBookingEngine
      */
      public APIBookingEngine getBookingEngine() {
           return new APIBookingEngine(transport);
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
     /**
      * @return APICalendarManager
      */
      public APICalendarManager getCalendarManager() {
           return new APICalendarManager(transport);
      }
     /**
      * @return APIApacManager
      */
      public APIApacManager getApacManager() {
           return new APIApacManager(transport);
      }
     /**
      * @return APIWubookManager
      */
      public APIWubookManager getWubookManager() {
           return new APIWubookManager(transport);
      }
     /**
      * @return APICarTuningManager
      */
      public APICarTuningManager getCarTuningManager() {
           return new APICarTuningManager(transport);
      }
     /**
      * @return APIPageManager
      */
      public APIPageManager getPageManager() {
           return new APIPageManager(transport);
      }
     /**
      * @return APILasGruppenPDFGenerator
      */
      public APILasGruppenPDFGenerator getLasGruppenPDFGenerator() {
           return new APILasGruppenPDFGenerator(transport);
      }
     /**
      * @return APIInvoiceManager
      */
      public APIInvoiceManager getInvoiceManager() {
           return new APIInvoiceManager(transport);
      }
     /**
      * @return APIResturantManager
      */
      public APIResturantManager getResturantManager() {
           return new APIResturantManager(transport);
      }
     /**
      * @return APIBookingComRateManagerManager
      */
      public APIBookingComRateManagerManager getBookingComRateManagerManager() {
           return new APIBookingComRateManagerManager(transport);
      }
     /**
      * @return APIC3Manager
      */
      public APIC3Manager getC3Manager() {
           return new APIC3Manager(transport);
      }
}
