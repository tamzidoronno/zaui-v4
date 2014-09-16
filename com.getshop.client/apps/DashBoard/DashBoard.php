<?php

namespace ns_b81bfb16_8066_4bea_a3c6_c155fa7119f8;

class DashBoard extends \ApplicationBase implements \Application {
    
    public function getDescription() {
        return $this->__f("A dashboad application");
    }

    public function getName() {
        return $this->__f("DashBoard");
    }

    public function render() {
        // Nothing to render
    }    
    
    public function renderConfig() {
        $this->includefile("dashboard");
    }
}
?>
