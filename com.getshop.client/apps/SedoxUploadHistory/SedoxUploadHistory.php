<?php
namespace ns_783ecee4_382b_4803_ba54_046ddd1ecf7e;

class SedoxUploadHistory extends \ns_5278fb21_3c0a_4ea1_b282_be1b76896a4b\SedoxCommon implements \Application {
    private $uploadedFiles;
    
    public function getDescription() {
        
    }

    public function getName() {
        return "SedoxUploadHistory";
    }

    public function render() {
        $this->includefile("uploadhistory");
    }
    
    public function getCurrentUploadedFiles() {
        $this->loadFiles();
        return $this->uploadedFiles;
    }

    public function loadFiles() {
        if (!$this->uploadedFiles) {
            $this->uploadedFiles = $this->getApi()
                    ->getSedoxProductManager()
                    ->getProductsFirstUploadedByCurrentUser($this->createFilterData());
        }
    }
    
    public function defaultSorting() {
        return "";
    }
    
    public function getTotalPages() {
        return $this->getApi()
                ->getSedoxProductManager()
                ->getProductsFirstUploadedByCurrentUserTotalPages($this->createFilterData());
    }
}
?>
