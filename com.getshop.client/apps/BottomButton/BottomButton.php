<?php
namespace ns_1081551b_1ecc_46cd_a192_63567ce129ab;

class BottomButton extends \WebshopApplication implements \Application {
    
    public function getDescription() {
        
    }

    public function getName() {
        return $this->__f("Normal button");
    }

    public function render() {
        // Not in use
    }
    
    public function renderBottomApplicationArea() {
        $name = $this->getConfigurationSetting("buttonName") ? $this->getConfigurationSetting("buttonName") : $this->__w("Button");
        $pageId = $this->getConfigurationSetting("buttonLink");
         
        $button = "<div class='gs_web_button'>$name</div>";
        if ($pageId) {
            echo "<a href='?page=$pageId'>$button</a>";
        } else {
            echo $button;
        }
    }
    
    public function edit() {
        $this->includeFile('config');
    }
}
?>
