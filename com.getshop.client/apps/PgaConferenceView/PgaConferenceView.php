<?php
namespace ns_b01782d0_5181_4b12_bec8_ee2e844bcae5;

class PgaConferenceView extends \ns_752aee89_0abc_43cf_9067_5aeadfe07cc1\PgaCommon implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "PgaConferenceView";
    }

    public function render() {
        $this->checkForceGuestInformation();
        $this->includefile("conferenceview");
    }

    

}
?>
