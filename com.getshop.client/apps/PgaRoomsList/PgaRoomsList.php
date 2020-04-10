<?php
namespace ns_96ee60e7_4f5d_4084_a2c7_ac6aa7e53bc0;

class PgaRoomsList extends \ns_752aee89_0abc_43cf_9067_5aeadfe07cc1\PgaCommon implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "PgaRoomsList";
    }

    public function render() {
        $this->checkForceGuestInformation();
        $this->includefile("roomlist");
    }
}
?>
