<?php
namespace ns_b6143df9_a5cd_424c_9f17_8e24616b2c3f;

class ZReport extends \ReportingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "ZReport";
    }

    public function render() {
        $this->includefile("zreport");
    }
    
    public function showReport() {
        
    }
}
?>
