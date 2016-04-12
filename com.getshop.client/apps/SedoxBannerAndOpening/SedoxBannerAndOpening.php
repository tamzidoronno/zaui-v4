<?php
namespace ns_cf3f46d9_0073_4966_977d_8e202dc5abbb;

class SedoxBannerAndOpening extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "SedoxBannerAndOpening";
    }

    public function render() {
        $this->includefile("bannerandopening");
    }
}
?>
