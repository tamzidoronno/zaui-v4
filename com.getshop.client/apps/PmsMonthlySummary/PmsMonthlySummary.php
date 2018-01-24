<?php
namespace ns_54e85e9b_cbeb_4ddd_abf9_7a2ddf1c259f;

class PmsMonthlySummary extends \WebshopApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "PmsMonthlySummary";
    }

    public function render() {
        echo "<div style='max-width:1500px; margin:auto;'>";
        $this->includefile("overview");
        echo "</div>";
        
    }
}
?>
