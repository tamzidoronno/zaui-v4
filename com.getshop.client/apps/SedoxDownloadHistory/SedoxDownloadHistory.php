<?php
namespace ns_5278fb21_3c0a_4ea1_b282_be1b76896a4b;

class SedoxDownloadHistory extends SedoxCommon implements \Application {
    private $orders = null;

    public function getDescription() {   
    }

    public function getName() {
        return "SedoxDownloadHistory";
    }

    public function render() {
        $this->includefile("downloadhistory");
    }

    public function loadOrders() {
        if ($this->orders) {
            return;
        }
        
        $this->orders = $this->getApi()
                ->getSedoxProductManager()
                ->getOrders($this->createFilterData());
        
    }
    
    public function getCurrentOrders() {
        $this->loadOrders();
        return $this->orders;
    }
    
    public function getTotalPages() {
        return $this->getApi()
                ->getSedoxProductManager()
                ->getOrdersPageCount($this->createFilterData());
    }
    
    public function downloadHistory() {
        
        $filter = $this->createFilterData();
        $filter->pageNumber = 1;
        $filter->pageSize = 9999999;
        
        $orders =  $this->getApi()
                ->getSedoxProductManager($filter)
                ->getOrders($filter);
        
        
        $rows = [];
        
        foreach ($orders as $order) {
            $time = $this->formatJavaDateToTime($order->dateCreated);
            $date = date("d M Y", $time);
        
            $product = $this->getApi()->getSedoxProductManager()->getProductById($order->productId);
            if (!$product)
                continue;
            
            $productName = $product->printableName;
            $row = [];
            $row[] = $order->productId;
            $row[] = $date;
            $row[] = $productName;
            $rows[] = $row;
        }
        
        echo json_encode($rows);
    }
 
}
?>
