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
        $date = $this->convertToJavaDate(time());
        $this->getApi()->getMecaManager()->resetServiceInterval($_POST['data']['carid'], $date, 125000);
    }
    
    public function pkkCompleted() {
        $date = $this->convertToJavaDate(time());
        $this->getApi()->getMecaManager()->markControlAsCompleted($_POST['data']['carid']);
    }
    
    public function serviceAndCompleted() {
        $date = $this->convertToJavaDate(time());
        $this->getApi()->getMecaManager()->resetServiceInterval($_POST['data']['carid'], $date, 125000);
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

}
?>
