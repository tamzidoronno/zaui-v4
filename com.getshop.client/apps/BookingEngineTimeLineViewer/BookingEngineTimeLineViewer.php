<?php
namespace ns_e098f200_ea77_4367_9b8a_a760cc4c1818;

class BookingEngineTimeLineViewer extends \ns_83df5ae3_ee55_47cf_b289_f88ca201be6e\EngineCommon implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "BookingEngineTimeLineViewer";
    }
    
    public function showReport() {
        
    }
    
    public function getSelectedName() {
        return $this->getConfigurationSetting("pmsname");
    }
    

    public function render() {
         if (!$this->getBookingEgineName()) {
            $this->printNotConnectedWarning();
        } else {
            $this->includefile("timelineview");
        }
      
    }

    public function formatTime($date) {
        return date('d/m-Y H:i:s', strtotime($date));
    }

}
?>
