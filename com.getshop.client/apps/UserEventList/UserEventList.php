<?php
namespace ns_138a1b9a_d8e3_4fec_8f3f_1cae11bca54f;

class UserEventList extends \ns_d5444395_4535_4854_9dc1_81b769f5a0c3\EventCommon implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "UserEventList";
    }

    public function render() {
        $this->includefile("eventlist");
    }
}
?>
