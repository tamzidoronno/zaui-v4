<?php
namespace ns_2399034c_bdc3_4dd6_87c4_df297d55bb2d;

class PmsSegmentation extends \WebshopApplication implements \Application {
    var $loadSegment = false;
    
    public function getDescription() {
        
    }

    public function getName() {
        return "PmsSegmentation";
    }

    public function render() {
        if($this->loadSegment) {
            $this->includefile("editsegment");
        } else {        
            $this->includefile("configpanel");
        }
    }
    
    public function recalculateSegment() {
        $segmentId = $_POST['data']['segmentid'];
        $this->getApi()->getPmsCoverageAndIncomeReportManager()->recalculateSegments($this->getSelectedMultilevelDomainName(), $segmentId);
    }
    
    public function PmsSegmentation_previewSegment() {
        $pmsSegment = new PmsSegmentation();
        $pmsSegment->loadSegment = true;
        $pmsSegment->renderApplication(true, $this);
    }
    
    public function deletesegment() {
        $this->getApi()->getPmsCoverageAndIncomeReportManager()->deleteSegment($this->getSelectedMultilevelDomainName(), $_POST['data']['gsvalue']);
    }
    
    public function createSegment() {
        $name = $_POST['data']['name'];
        $code = $_POST['data']['code'];
        if(!$name) {
            return;
        }
        if(!$code) {
            return;
        }
        $segment = new \core_pmsmanager_PmsSegment();
        $segment->code = $code;
        $segment->name = $name;
        $segment->comment = $_POST['data']['comment'];
        $this->getApi()->getPmsCoverageAndIncomeReportManager()->saveSegments($this->getSelectedMultilevelDomainName(), $segment);
    }
    
    public function saveSegment() {
        $id = $_POST['data']['id'];
        $segment = $this->getApi()->getPmsCoverageAndIncomeReportManager()->getSegment($this->getSelectedMultilevelDomainName(), $id);
        $segment->code = $_POST['data']['code'];
        $segment->name = $_POST['data']['name'];
        $segment->comment = $_POST['data']['comment'];
        $segment->types = array();
        foreach($_POST['data'] as $key => $val) {
            if($val == "true" && stristr($key, "bookingtype_")) {
                $type = str_replace("bookingtype_", "", $key);
                $segment->types[] = $type;
            }
        }
        $this->getApi()->getPmsCoverageAndIncomeReportManager()->saveSegments($this->getSelectedMultilevelDomainName(), $segment);
    }
    
}
?>
