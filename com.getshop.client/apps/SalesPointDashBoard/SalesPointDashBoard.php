<?php
namespace ns_bf312f0d_d204_45e9_9519_a139064ee2a7;

class SalesPointDashBoard extends \ns_57db782b_5fe7_478f_956a_ab9eb3575855\SalesPointCommon implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "SalesPointDashBoard";
    }

    public function render() {
        if ($this->preRender()) {
            return;
        }
        
        $this->includefile("dashboard");
    }
}
?>
