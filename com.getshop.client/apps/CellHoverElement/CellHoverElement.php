<?php
namespace ns_6bafb644_5276_4101_90d3_acc5c9a0f710;

class CellHoverElement extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "CellHoverElement";
    }

    public function render() {
        echo "<div style='text-align:center;'>";
        echo "<span class='hovercell'></span>";
        echo "</div>";
    }
}
?>
