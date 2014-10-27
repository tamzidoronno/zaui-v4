<?php
namespace ns_ee1f3649_cfd8_41d5_aa5b_682216f376b6;

use \ReportingApplication;
use \Application;

class Translation extends ReportingApplication implements Application {
     public $singleton = true;

     public function getDescription() {
        return $this->__f("Not satisfied with the text \"hardcoded\" on your page?<br>Add this application and tune the text yourself.");
    }

    public function renderStandalone() {
    }
    
    public function getName() {
        return $this->__f("Translation");
    }

    public function postProcess() {
        
    }

    public function preProcess() {
        
    }
    
    public function saveTranslation() {
        $newArray = array();
        $baseTranslation = $this->getFactory()->getWebShopTranslation();
        foreach($_POST['data'] as $key => $value) {
            $value = base64_decode($_POST['data'][$key]);
            $key = base64_decode($key);
            
            if(strlen(trim($value)) == 0) {
                continue;
            }
            
            if($baseTranslation[$key] == $value) {
                //No need to save user translated data which is the same.
                continue;
            }
            
            if($key == $value) {
                //No need to save keys as values, since they are defaults.
                continue;
            }
            
            $translationObject = $this->getApiObject()->core_storemanager_data_TranslationObject();
            $translationObject->key = $key;
            $translationObject->value = htmlentities($value, ENT_QUOTES, "UTF-8");
            
            $newArray[] = $translationObject;
        }
        
        $store = $this->getApi()->getStoreManager()->getMyStore();
        $store->configuration->translationMatrix = $newArray;
        $this->getApi()->getStoreManager()->saveStore($store->configuration);
        $this->getFactory()->reloadStoreObject();
        $this->getFactory()->loadLanguage();
    }
    

    public function render() {
        $this->includefile("translationview");
    }
    
    public function renderConfig() {
        echo $this->__f("Click on the chat icon in the top menu and start translating.");
    }
    
}
?>
