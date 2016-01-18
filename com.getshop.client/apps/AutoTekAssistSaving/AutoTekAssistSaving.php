<?php
namespace ns_497d3093_47cf_4e38_bd2c_5117476f8409;

class AutoTekAssistSaving extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "AutoTekAssistSaving";
    }

    public function render() {
        $this->includefile("saving");
    }
}
?>
