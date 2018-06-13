<?php
namespace ns_81edf29e_38e8_4811_a2c7_bc86ad5ab948;

class GslBooking extends \MarketingApplication implements \Application {
    public function getDescription() {
        return "New bookingview";
    }

    public function getName() {
        return "GslBooking";
    }

    public function render() {
        $lang = $this->getFactory()->getCurrentLanguage();
        echo "<script>";
        echo 'sessionStorage.setItem("getshop_language","'.$lang.'");';
        echo "</script>";
        
        ob_start();
        $this->includefile("gslfront_1");
        $content = ob_get_contents();
        ob_end_clean();
        
        $content = str_replace("{getshop_endpoint}", "", $content);
        echo $content;
    }
}
?>
