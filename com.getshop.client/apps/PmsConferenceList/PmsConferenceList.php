<?php
namespace ns_dbe8930f_05d9_44f7_b399_4e683389f5cc;

class PmsConferenceList extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "PmsConferenceList";
    }

    public function render() {
        $this->includefile("conferencelist");
    }

}
?>
