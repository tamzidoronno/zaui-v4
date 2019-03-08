<?php
namespace ns_8f7198af_bd49_415a_8c39_9d6762ef1440;

class ComfortDashBoard extends \WebshopApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "ComfortDashBoard";
    }

    public function render() {
        echo "<div style='max-width:1600px; margin: auto;margin-top: 30px;'>";
        $this->includefile("dashboardintroduction");
        $this->includefile("dashboardoverview");
        echo "</div>";
    }
}
?>
