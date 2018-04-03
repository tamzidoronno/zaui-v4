<?php
namespace ns_e4a506de_4702_4d82_8224_f30e5fdb1d2e;

class MecaFleetServiceList extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "MecaFleetServiceList";
    }

    public function render() {
        $this->mode = "attentioncars";
        $this->includefile("servicelist");
        $this->mode = "othercars";
        $this->includefile("servicelist");
    }
    
    public function serviceCompleted() {
        $this->checkError();
        $date = $this->convertToJavaDate(time());
        $this->getApi()->getMecaManager()->resetServiceInterval($_POST['data']['carid'], $date, $_POST['data']['currentkilometers']);
        
    }
    
    public function pkkCompleted() {
        $this->checkError();
        $date = $this->convertToJavaDate(time());
        $this->getApi()->getMecaManager()->markControlAsCompleted($_POST['data']['carid']);
    }
    
    public function serviceAndCompleted() {
        $this->checkError();
        $date = $this->convertToJavaDate(time());
        $this->getApi()->getMecaManager()->resetServiceInterval($_POST['data']['carid'], $date, $_POST['data']['currentkilometers'] );
        $this->getApi()->getMecaManager()->markControlAsCompleted($_POST['data']['carid']);
    }
    
    public function noShow() {
        $this->getApi()->getMecaManager()->noShowService($_POST['data']['carid']);
    }

    public function getCarList() {
        if ($this->mode == "attentioncars") {
            return $this->getApi()->getMecaManager()->getCarsServiceList(true);
        }
        
        if ($this->mode == "othercars") {
            return $this->getApi()->getMecaManager()->getCarsServiceList(false);
        }
        
    }

    public function checkError() {
        if (!isset($_POST['data']['currentkilometers']) || !$_POST['data']['currentkilometers']) {
            $obj = $this->getStdErrorObject(); // Get a default error message
            $obj->fields->errorMessage = "<i class='fa fa-warning'></i> Kilometerstanden må være oppgitt"; // The message you wish to display in the gserrorfield
            $obj->gsfield->hours = 1; // Will highlight the field that has gsname "hours"
            $this->doError($obj); // Code will stop here.
        }

    }
    
    public function saveComment() {
        $this->getApi()->getMecaManager()->setCommentOnCar($_POST['data']['carid'], $_POST['data']['comment']);
    }

}
?>
