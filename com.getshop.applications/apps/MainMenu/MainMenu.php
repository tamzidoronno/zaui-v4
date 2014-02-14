<?php
namespace ns_bf35979f_6965_4fec_9cc4_c42afd3efdd7;

class MainMenu extends \SystemApplication implements \Application {
    
    public function getDescription() {
    }

    public function getName() {
    }
    
    public function postProcess() {
    }
    
    public function preProcess() {
    }
    
    public function render() {
        if($this->getFactory()->isExtendedMode()) {
            $this->includefile("mainmenu");
        }
    }
    
    public function changeLayout() {
        $this->getApi()->getPageManager()->changePageLayout($_POST['data']['pageId'], $_POST['data']['type']);
        $this->getFactory()->clearCachedPageData();
        $this->getFactory()->initPage();
    }
}

?>
