<?php
namespace ns_13e03ff7_8334_45eb_a280_c772c3262325;

class MecaFleetCars extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }
    
    public static function sortByDate($car1, $car2) {
        $time1 = strtotime($car1->rowCreatedDate);
        $time2 = strtotime($car2->rowCreatedDate);
        
        return $time1 < $time2;
    }

    public function getName() {
        return "MecaFleetCars";
    }

    public function render() {
        $this->includefile('cars');
    }
    
    public function deleteCar() {
        $this->getApi()->getMecaManager()->deleteCar($_POST['data']['carid']);
    }
}
?>
