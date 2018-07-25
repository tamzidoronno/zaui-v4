<?php
namespace ns_afbe1ef5_6c62_45c7_a5a0_fd16d380d7cb;

class GeneralSettingsOther extends \WebshopApplication implements \Application {
    private $storeSettingsInstance;
    public function getDescription() {
        
    }

    public function getName() {
        return "GeneralSettingsOther";
    }

    public function render() {
        $this->includefile("settings");
    }
    
    public function removeDomain() {
        $domain = $_POST['data']['domain'];
        $this->getApi()->getStoreManager()->removeDomainName($domain);
    }
    
    public function saveWebAdress() {
        $domain = $_POST['data']['domain'];
        $this->getApi()->getStoreManager()->setPrimaryDomainName($domain);
    }
    
    public function getStoreSettingsApp() {
        if (!$this->storeSettingsInstance) {
            $app = $this->getApi()->getStoreApplicationPool()->getApplication("d755efca-9e02-4e88-92c2-37a3413f3f41");
            $this->storeSettingsInstance = $this->getFactory()->getApplicationPool()->createInstace($app);
        }
        
        return $this->storeSettingsInstance;
    }
    
    public function save() {
        $this->getApi()->getStoreManager()->changeTimeZone($_POST['data']['timezone']);
        $this->getStoreSettingsApp()->setConfigurationSetting("currencycode", $_POST['data']['currency']);

        $languages = $this->getStoreSettingsApp()->getConfigurationSetting("languages");
        $saveLanagues =  [];
        $states = $this->getFactory()->getLanguageReadable();
        foreach($states as $key => $lang) {
            if($_POST['data']['additionallanguage_'.$key] == "true") {
                $saveLanagues[] = $key;
            }
        }
        
        $languages = json_encode($saveLanagues);
        $this->getStoreSettingsApp()->setConfigurationSetting("languages", $languages);
        $this->getStoreSettingsApp()->setConfigurationSetting("language", $_POST['data']['mainlanguage']);
        
    }
    
   public function getCurrencyCode() {
        return \ns_9de54ce1_f7a0_4729_b128_b062dc70dcce\ECommerceSettings::fetchCurrencyCode();
    }

    public function isCurrency($currency) {
        $selected = $this->getCurrencyCode() == $currency ? "selected='true'" : "";
        echo $selected;
    }
    
    public function saveMasterStoreId() {
        $storeId = $_POST['data']['masterstoreid'];
        $this->getApi()->getStoreManager()->setMasterStoreId($storeId);
    }

    public function disconnectMaster() {
        $storeId = $_POST['data']['masterstoreid'];
        $this->getApi()->getStoreManager()->setMasterStoreId("");
    }
    
    public function acceptSlave() {
        $this->getApi()->getStoreManager()->acceptSlave($_POST['data']['slavestoreid']);
    }
}
?>
