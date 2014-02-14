<?php
namespace ns_2ebd7c69_eba3_4b7e_85c6_0e0bd274aad0;

class SedoxCarList extends \WebshopApplication implements \Application {
    
    public function getDescription() {
        return "This application displays tuning data for car, boats, trucks, tractors, etc";
    }

    public function getName() {
        return "Sedox tuning car list";
    }

    public function render() {
        $this->includefile("sedoxcarlist");
    }    
    
    public function getStarted() {
        echo $this->__f("Just add it");
    }
    
    public function loadManufacturer() {
        $target = $_POST['data']['target'];
        
        $soapClient = new \SoapClient("http://www.sedox.com/carlistapi/Service.php?wsdl");
        
        switch($target) {
            case "t1":
                $data = $soapClient->getCarBrands();
                break;
            case "t2":
                $data = $soapClient->getTruckBrands();
                break;
            case "t3":
                $data = $soapClient->getTractorBrands();
                break;
            case "t4":
                $data = $soapClient->getBoatBrands();
                break;
        }
        
        echo json_encode($data);
    }
    
    public function loadChildren() {
        $target = $_POST['data']['target'];
        $soapClient = new \SoapClient("http://www.sedox.com/carlistapi/Service.php?wsdl");
        $data = $soapClient->getChildren($target);
        echo json_encode($data);
    }

    public function loadTuningData() {
        $target = $_POST['data']['target'];
        $soapClient = new \SoapClient("http://www.sedox.com/carlistapi/Service.php?wsdl");
        $data = $soapClient->getTuningData($target);
        echo json_encode($data);
    }
    
    public function showTuningData() {
        if (!isset($_POST['data']['stage1_nm']))
            $_POST['data']['stage1_nm'] = "-";
        
        if (!isset($_POST['data']['stage1_hp']))
            $_POST['data']['stage1_hp'] = "-";
        
        $this->includefile("tuningdata");
    }
    
}
?>
