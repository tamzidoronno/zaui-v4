<?php
namespace ns_66112a2c_92c9_47c6_ae5b_bb35121e6654;

class HotelPriceCalculator extends \ApplicationBase implements \Application {
    public function getDescription() {
        return "Price calculator for hotel view";
    }

    public function getName() {
        return "HotelPriceCalcuator";
    }
    
    public function retry() {
        $this->includefile("calculator");
    }

    public function render() {
        echo '<div class="pricecalcarea">';
        $this->includefile("calculator");
        echo '</div>';
        echo "<div class='pricecalcarearesult'>";
        echo "</div>";
        echo "<div style='clear:both;'></div>";
    }
    
    public function generatePrice() {
        $this->includefile("generatedprice");
    }

}

?>