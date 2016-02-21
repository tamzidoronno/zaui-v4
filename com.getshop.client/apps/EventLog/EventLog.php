<?php
namespace ns_c2730dd0_cffc_4e9d_bcd7_338623dc24ad;

class EventLog extends \ns_d5444395_4535_4854_9dc1_81b769f5a0c3\EventCommon implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "EventLog";
    }

    public function render() {
        $this->includefile("eventlog");
    }
}
?>
