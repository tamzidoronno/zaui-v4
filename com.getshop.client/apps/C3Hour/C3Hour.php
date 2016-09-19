<?php
namespace ns_4a4d17cc_7691_4851_998a_10fae57efcee;

class C3Hour extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "C3Hour";
    }

    public function render() {
        $this->includefile("edithour");
    }
    
    public function saveHours() {
        if ($this->getModalVariable("hourid")) {
            $c3Hour = $this->getApi()->getC3Manager()->getHourById($this->getModalVariable("hourid"));
        } else {
            $c3Hour = new \core_c3_C3Hour();
        }
        
        $c3Hour->from = $this->convertToJavaDate(strtotime($_POST['data']['from']));
        $c3Hour->to = $this->convertToJavaDate(strtotime($_POST['data']['to']));
        $c3Hour->projectId = $this->getModalVariable("projectid");
        $c3Hour->hours = $_POST['data']['hours'];
        $c3Hour->bidragstype = $_POST['data']['bidragstype'];
        
        $this->getApi()->getC3Manager()->addHour($c3Hour);
    }
}
?>
