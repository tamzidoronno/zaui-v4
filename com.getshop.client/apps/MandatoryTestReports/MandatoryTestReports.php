<?php
namespace ns_a83eedf7_c26d_44e6_91bd_31cc4e4a75ee;

class MandatoryTestReports extends \WebshopApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "MandatoryTestReports";
    }

    public function render() {
        $this->includefile("report");
        $this->includefile("companies");
    }
    
    public function loadList() {
        
    }

    public function getIds() {
        $retArray = array();
        if (isset($_POST['data'])) {
            foreach ($_POST['data'] as $key => $value) {
                if (strstr($key, "package") && $value === "true") {
                    $splits = explode("_", $key);
                    $id = $splits[1];
                    $retArray[] = $id;
                }
            }
        }
        
        return $retArray;
    }

}
?>
