<?php
namespace ns_da9c6dfe_98a2_489c_89a6_57915064d7ae;

class AccountingDownloadFilter extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "AccountingDownloadFilter";
    }

    public function render() {
        $this->includefile("select");
    }
    
    public function setEndDate() {
        $downloadApp = new \ns_1674e92d_feb5_4a78_9dba_1e5ba05a6a31\AccountingDownload();
        $downloadApp->setEndDate();
    }
}
?>
