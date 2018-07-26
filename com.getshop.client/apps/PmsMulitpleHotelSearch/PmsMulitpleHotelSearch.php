<?php
namespace ns_08c6fa4f_1044_4aa7_b31b_37a633ce6a3a;

class PmsMulitpleHotelSearch extends \WebshopApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "PmsMulitpleHotelSearch";
    }

    public function render() {
        $this->includefile("searchfield");
        $this->includefile("searchresult");
    }
}
?>
