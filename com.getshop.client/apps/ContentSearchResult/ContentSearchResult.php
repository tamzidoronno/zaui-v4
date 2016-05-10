<?php
namespace ns_da5ba9de_c58d_4f52_8c4b_cbf2fb00191b;

class ContentSearchResult extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "ContentSearchResult";
    }

    public function render() {
        $this->includefile("result");
    }
}
?>
