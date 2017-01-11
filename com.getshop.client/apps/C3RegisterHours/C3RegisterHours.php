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
    
    public function downloadSfiReportTotal() {
        $periodes = $this->getApi()->getC3Manager()->getPeriodes();
        foreach ($periodes as $periode) {
            if ($periode->isActive) {
                $companyId = \ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::getUserObject()->companyObject->id;
                echo $this->getApi()->getC3Manager()->getBase64SFIExcelReportTotal($companyId, $periode->from, $periode->to);
                die();
            }
        }
    }

    public function setSummaryView() {
        $_SESSION['C3RegisterHours_viewmode'] = "true";
    }
    
    public function setNormalView() {
        unset($_SESSION['C3RegisterHours_viewmode']);
    }
    
    public function getViewMode() {
        if (isset($_SESSION['C3RegisterHours_viewmode'])) {
            return true;
        }
        
        return false;
    }
    
    public static function sortHours($a,$b) {
        return strcmp($a->costType, $b->costType);
    }

    public function sortAndGroup($hours) {

        if ($hours)
            uasort($hours, array('\ns_f1f2c4f4_fc7d_4bec_89ec_973ff192ff6d\C3RegisterHours', 'sortHours'));
        
        return $hours;
    }

}
?>
