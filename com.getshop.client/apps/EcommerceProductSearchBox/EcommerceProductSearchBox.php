<?php
namespace ns_c282cfba_2873_46fd_876b_c44269eb0dfb;

class EcommerceProductSearchBox extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "EcommerceProductSearchBox";
    }

    public function render() {
        $this->includefile("searchbox");
    }
    
    public function doSearch() {
        $_SESSION['ns_c282cfba_2873_46fd_876b_c44269eb0dfb_searchword'] = $_POST['data']['search'];
    }
    
    public function gsAlsoUpdate() {
        $alsoUpdate = array();
        $alsoUpdate[] = '0c6398b0-c301-481a-b4e7-faea0376e822';
        return $alsoUpdate;
    }
}
?>
