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
    
    public function downloadHistory() {
        
        $filter = $this->createFilterData();
        $filter->pageNumber = 1;
        $filter->pageSize = 9999999;
        
        $products = $this->creditHistory = $this->getApi()
                ->getSedoxProductManager()
                ->getProductsFirstUploadedByCurrentUser($filter);
        
        $rows = [];
        
        foreach ($products as $product) {
            $productName = $product->printableName;

            $time = $this->formatJavaDateToTime($product->rowCreatedDate);
            $date = date("d M Y", $time);

            $row = [];
            $row[] = $product->id;
            $row[] = $date;
            $row[] = $productName;
            $rows[] = $row;
        }
        
        echo json_encode($rows);
    }
}
?>