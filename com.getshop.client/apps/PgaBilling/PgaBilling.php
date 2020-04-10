<?php
namespace ns_d049425e_1718_48a0_b61b_950492638a14;

class PgaBilling extends \ns_752aee89_0abc_43cf_9067_5aeadfe07cc1\PgaCommon implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "PgaBilling";
    }

    public function render() {
        $this->checkForceGuestInformation();
        $this->includefile("billing");
    }
}
?>
