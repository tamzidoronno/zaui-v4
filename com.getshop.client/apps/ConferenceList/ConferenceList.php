<?php
namespace ns_d3e168b2_c10e_4750_a330_527d98906aa8;

class ConferenceList extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "ConferenceList";
    }

    public function render() {
        $this->includefile("conferencelist");
    }
}
?>
