<?php
namespace ns_ba885f72_f571_4a2e_8770_e91cbb16b4ad;

class Facebook extends \WebshopApplication implements \Application {

    //put your code here
    public function getDescription() {
        return $this->__("Tightly integrates facebook into your webshop, give users options to comment product, like products, share discounts, etc");
    }

    public function getName() {
        return "Facebook";
    }
    

    public function postProcess() {
        
    }

    public function preProcess() {
        
    }

    public function saveAddress() {
        $this->setConfigurationSetting("address", $_POST['data']['address']);
    }
    
    public function render() {
        echo "<div class='news_area'>";
        $address = $this->getConfigurationSetting("address");
        if(!$address && $this->hasWriteAccess()) {
            $this->includefile("activation");
        }
        echo "</div>";
        echo "<script>";
        echo "var fb_" . str_replace("-","_", $this->getConfiguration()->id) . " = new gs_app.Facebook('" . $this->getConfiguration()->id . "','$address');";
        echo "</script>";
    }
    
    /**
     * Should display you configuration page.
     */
    public function renderConfig() {
        $this->includefile("facebookconfig");
    }
    
}

?>
