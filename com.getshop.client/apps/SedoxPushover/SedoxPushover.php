<?php
namespace ns_2ccaebd8_12f0_4c00_99be_feb5f4da9d79;

class SedoxPushover extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "SedoxPushover";
    }

    public function render() {
        $this->includefile("pushover");
    }
    
    public function saveId() {
        $userId = \ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::getUserObject()->id;
        
        $this->getApi()
                ->getSedoxProductManager()
                ->setPushoverId($_POST['data']['id']);
    }
    
    public function getPushoverId() {
        $userId = \ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::getUserObject()->id;
        $user = $this->getApi()->getSedoxProductManager()->getSedoxUserAccountById($userId);
        return $user->pushoverId;
    }
}
?>
