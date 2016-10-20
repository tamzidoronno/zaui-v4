<?php
namespace ns_fe5ceb57_c8ea_4032_a5e2_04b8b86dc38c;

class C3OtherCosts extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "C3OtherCosts";
    }

    public function render() {
        $this->includefile("othercost");
    }
    
    public function saveCost() {
        $otherCost = new \core_c3_C3OtherCosts();
        
        if ($this->getModalVariable("otherid")) {
            $otherCost = $this->getApi()->getC3Manager()->getOtherCost($this->getModalVariable("otherid"));
        }

        $otherCost->from = $this->convertToJavaDate(strtotime($_POST['data']['from']));
        $otherCost->to = $this->convertToJavaDate(strtotime($_POST['data']['to']));
        $otherCost->projectId = $this->getModalVariable("projectid");
        $otherCost->cost = $_POST['data']['cost'];
        $otherCost->type = $_POST['data']['type'];
        $otherCost->comment = $_POST['data']['comment'];
        $otherCost->nfr = isset($_POST['data']['nfr']) ? $_POST['data']['nfr'] : false;
        
        $this->validate($otherCost);
        $this->getApi()->getC3Manager()->saveOtherCosts($otherCost);
        
    }
    
    public function validate($hour) {
        if (!ctype_digit($hour->cost)) {
            $obj = $this->getStdErrorObject();
            $obj->fields->errorMessageTimer = "Kun hele kroner tilatt i dette feltet";
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
