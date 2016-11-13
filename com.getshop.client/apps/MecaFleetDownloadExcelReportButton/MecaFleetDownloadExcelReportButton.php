<?php
namespace ns_f9992c24_10e8_4d88_b5d0_bee85956041f;

class MecaFleetDownloadExcelReportButton extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "MecaFleetDownloadExcelReportButton";
    }

    public function render() {
        $this->includefile("downloadexcel");
    }
    
    public function downloadReport() {
        echo $this->getApi()->getMecaManager()->getBase64ExcelReport($this->getPage()->getId());
        die();
    }
}
?>
