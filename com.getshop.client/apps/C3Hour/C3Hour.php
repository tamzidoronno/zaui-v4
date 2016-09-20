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
        
        $this->validate($c3Hour);
        $this->getApi()->getC3Manager()->addHour($c3Hour);
        
    }
    
    public function validate($hour) {
        if (!ctype_digit($hour->hours)) {
            $obj = $this->getStdErrorObject();
            $obj->fields->errorMessageTimer = "Kun hele timer er tillatt i dette feltet";
            $obj->gsfield->hours = 1;
            $this->doError($obj);
        }
        
        $result = $this->getApi()->getC3Manager()->canAdd($hour);
        
        if ($result && $result == "OUTSIDE_OF_OPEN_PERIODE") {
            $obj = $this->getStdErrorObject();
            $obj->fields->errorMessage = "Timer kan føres på prosjektets åpen periode";
            $obj->gsfield->from = 1;
            $obj->gsfield->to = 1;
            $this->doError($obj);
        }
        
        if ($result && $result == "PROJECT_PERIODE_INVALIDE") {
            $obj = $this->getStdErrorObject();
            $obj->fields->errorMessage = "Kan ikke føre timer utenfor prosjektets varighet";
            $obj->gsfield->from = 1;
            $obj->gsfield->to = 1;
            $this->doError($obj);
        }
    }
}
?>
