<?php
namespace ns_92e3b7d6_c3ac_463e_90ba_4f966613f8dd;

class SedoxFileUpload extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "SedoxFileUpload";
    }

    public function render() {
        $this->includefile("uploadmodal");
        $this->includefile("uploadform");
    }
    
    public function doesProductExists() {
        echo "false";
    }
    
    // TODO : select product based on data['md5']
    public function selectProductBaseOnMd5() {
        echo "false";
    }
    
    public function uploadFile() {
//        echo "TEST";
    }
}
?>
