<?php
namespace ns_f1f2c4f4_fc7d_4bec_89ec_973ff192ff6d;

class C3RegisterHours extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "C3RegisterHours";
    }

    public function render() {
//        $this->includefile("overview");
        $this->includefile("summary");
    }
    
    public function downloadSfiReport() {
        $periodes = $this->getApi()->getC3Manager()->getPeriodes();
        foreach ($periodes as $periode) {
            if ($periode->isActive) {
                $companyId = \ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::getUserObject()->companyObject->id;
                echo $this->getApi()->getC3Manager()->getBase64SFIExcelReport($companyId, $periode->from, $periode->to);
                die();
            }
        }
    }
}
?>
