<?php
namespace ns_ae288b19_9934_4e34_ade7_e8abba43b477;

class SedoxFiluploadCount extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "SedoxFiluploadCount";
    }

    public function render() {
        $this->includefile("uploadcount");
    }

    public function getDownloadCount() {
        return $this->getApi()->getSedoxProductManager()->getUserFileDownloadCount();
    }

    public function getUploadCount() {
        return $this->getApi()->getSedoxProductManager()->getUserFileUploadCount();
    }

}
?>
