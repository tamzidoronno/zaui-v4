<?php
namespace ns_238d74e7_4c06_4429_85bd_40648121d1cb;

class SedoxSearchHeader extends \WebshopApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "SedoxSearchHeader";
    }

    public function render() {
        $this->includefile("searchfield");
    }
    
    public function getSearchString() {
        if(isset($_SESSION['sedox_search_value'])) {
            return $_SESSION['sedox_search_value'];
        }
        
        return "";
    }
}
?>
