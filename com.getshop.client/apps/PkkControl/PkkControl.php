<?php
namespace ns_2b06f7a7_8301_4582_a9ea_1005e9248257; 

class PkkControl extends \MarketingApplication implements \Application {
    public function getDescription() {
        return $this->__f("Connects to the norwegian offices for road safety and check when the next TUV control is due");
    }

    public function getName() {
        return $this->__f("PkkControl");
    }

    public function render() {
        $this->includefile("control");
    }
    
    public function searchForControl() {    
        $this->includefile("searchpkkcontrol");
        die();
    }
    
    public function signUp() {
        $data = new \core_pkkcontrol_PkkControlData();
        $data->modelAndBrand = $_POST['data']['brand'];
        $data->licensePlate = $_POST['data']['licenseplate'];
        $data->vineNumber = $_POST['data']['vinenumber'];
        $data->registedYear = $_POST['data']['registredyear'];
        $data->lastControl = $_POST['data']['lastcontrol'];
        $data->nextControl = $_POST['data']['nextcontrol'];
        $data->name = $_POST['data']['pkk_name_of_user'];
        $data->cellphone= $_POST['data']['pkk_cellphone_number'];
        $data->email = $_POST['data']['pkk_email_address'];
        $this->getApi()->getPkkControlManager()->registerPkkControl($data);
    }
    
    public function removePkk() {
        $this->getApi()->getPkkControlManager()->removePkkControl($_POST['data']['id']);
    }

}