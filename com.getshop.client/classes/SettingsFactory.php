<?php

class SettingsFactory extends FactoryBase {
    
    public function renderContent() {
        $dashBoard = new ns_b81bfb16_8066_4bea_a3c6_c155fa7119f8\DashBoard();
        $dashBoard->renderConfig();
    }
    
    public function includefile($filename, $overrideappname = NULL, $printError = true) {
       
    }
}

?>