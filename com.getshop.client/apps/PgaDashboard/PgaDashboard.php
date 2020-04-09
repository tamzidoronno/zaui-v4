<?php
namespace ns_2f998ecd_72e2_4b44_8529_cc8d6e5b2d15;

class PgaDashboard extends \ns_752aee89_0abc_43cf_9067_5aeadfe07cc1\PgaCommon implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "PgaDashboard";
    }

    public function render() {
        $this->checkForceGuestInformation();
        $this->includefile("head");
    }
}
?>
