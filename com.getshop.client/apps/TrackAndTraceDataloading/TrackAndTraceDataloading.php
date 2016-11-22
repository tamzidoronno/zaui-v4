<?php
namespace ns_4b03ba65_0fcf_479d_83b9_1306e1bbedb3;

class TrackAndTraceDataloading extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "TrackAndTraceDataloading";
    }

    public function render() {

        if (isset($_FILES['data']['tmp_name'])) {
            $fileContent = file_get_contents($_FILES['data']['tmp_name']);
            $this->getApi()->getTrackAndTraceManager()->loadData($fileContent, $_FILES['data']['name']);
        }
        $this->includefile("dataloading");
    }
    
    public function start() {
        
        
    }
}
?>
