<?php
namespace ns_b904a342_9321_4f1f_a69f_2c7a87b10258;

class C3EsaReport extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "C3EsaReport";
    }

    public function render() {
        $this->includefile("selectworkpackage");
    }
    
    public function downloadEsaReport() {
        $startDate = $this->convertToJavaDate(strtotime($_POST['data']['from']));
        $endDate = $this->convertToJavaDate(strtotime($_POST['data']['to']));
        
        echo $this->getApi()->getC3Manager()->getBase64ESAExcelReport($startDate, $endDate);
        die();
    }
}
?>
