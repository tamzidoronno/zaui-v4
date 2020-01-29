<?php
namespace ns_c20ea6e2_bc0b_4fe1_b92a_0c73b67aead7;

class SalesPointReports extends \ns_57db782b_5fe7_478f_956a_ab9eb3575855\SalesPointCommon implements \Application {
    private $users = array();
    
    private $cachedPaymentNames = array();
    
    public function getDescription() {
        
    }

    public function getName() {
        return "SalesPointReports";
    }

    public function showSalesReport() {
        $_SESSION['ns_c20ea6e2_bc0b_4fe1_b92a_0c73b67aead7_activetab'] = "salesreport";
        $this->includefile("salesreport");
    }
    
    public function showStockReport() {
        $_SESSION['ns_c20ea6e2_bc0b_4fe1_b92a_0c73b67aead7_activetab'] = "stock";
        $this->includefile("stock");
    }
    
    public function loadReport() {
        $_SESSION['ns_c20ea6e2_bc0b_4fe1_b92a_0c73b67aead7_start'] = $_POST['data']['startdate'];
        $_SESSION['ns_c20ea6e2_bc0b_4fe1_b92a_0c73b67aead7_end'] = $_POST['data']['enddate'];
    }
    
    public function render() {
        if ($this->preRender()) {
            return;
        }
        
        $this->addOrderToReport();
        $this->includefile("leftmenu");
        
        $tab = isset($_SESSION['ns_c20ea6e2_bc0b_4fe1_b92a_0c73b67aead7_activetab']) ? $_SESSION['ns_c20ea6e2_bc0b_4fe1_b92a_0c73b67aead7_activetab'] : "";
        echo "<div class='reportarea'>";
            if ($tab == "reportlist") {
                $this->showReportList(true);
            } else if ($tab == "masterreport") {
                $this->includefile("masterreport");
            } else if ($tab == "salesreport") {
                $this->includefile("salesreport");
            } else if ($tab == "stock") {
                $this->includefile("stock");
            } else {
                $this->includefile("xreport");
            }
        echo "</div>";
    }
    
    public function isCurrentZReportASavedReport() {
        return isset($_SESSION['ns_c20ea6e2_bc0b_4fe1_b92a_0c73b67aead7_selected_z_report']) && $_SESSION['ns_c20ea6e2_bc0b_4fe1_b92a_0c73b67aead7_selected_z_report'];
    }

    public function getCurrentZReport() {
        if ($this->isCurrentZReportASavedReport()) {
            return $this->getApi()->getPosManager()->getZReport($_SESSION['ns_c20ea6e2_bc0b_4fe1_b92a_0c73b67aead7_selected_z_report'], $this->getSelectedCashPointId());
        } else {
            return $this->getApi()->getPosManager()->getZReport("", $this->getSelectedCashPointId());
        }
    }
    
    public function cancelCurrentZReport() {
        unset($_SESSION['ns_c20ea6e2_bc0b_4fe1_b92a_0c73b67aead7_selected_z_report']);
    }

    /**
     * 
     * @param \core_ordermanager_data_Order $order
     * @return \core_usermanager_data_User
     */
    public function getUserById($userId) {
        if (!array_key_exists($userId, $this->users)) {
            $user = $this->getApi()->getUserManager()->getUserById($userId);
            $this->users[$userId] = $user;
        }
        
        return $this->users[$userId];
    }
    
    public function createZReport() {
        $this->getApi()->getPosManager()->createZReport($this->getSelectedCashPointId());
    }
    
    public function formatRowCreatedDate($row) {
        return $this->formatDate($row->rowCreatedDate);
    }
    
    public function formatCashPointName($row) {
        $point = $this->getApi()->getPosManager()->getCashPoint($row->cashPointId);
        if(!$point) {
            return "";
        }
        return $point->cashPointName;
    }
    
    public function formatDate($date) {
        $sortkey = strtotime($date);
        return "<span getshop_sorting='$sortkey'>" . date('d.m.Y H:i:s', strtotime($date)) . "</span>";
    }
    
    public function downloadPdfZreport() {
        $this->includefile('pdfxreport');
        die();
    }
    
    public function formatActivateButton($row) {
        $retString = "";
        
        if (strstr(\ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::getUserObject()->emailAddress, "getshop.com")) {
            $retString = '<i zreportid="'.$row->id.'" gs_precheck="app.SalesPointReports.getpassword" gsclick="deleteZReport" class="gs_shop_small_icon fa fa-trash"></i> ';
        }
        
        $retString .= "<div class='gs_shop_small_icon' gsclick='selectZReport' zreportid='$row->id'>".$this->__f("show")."</div>";
        
        $point = $this->getApi()->getPosManager()->getCashPoint($row->cashPointId);
        $filename = "unknown";
        if($point) {
            $filename = date('d.m.Y h.i', @strtotime($row))." - ".$point->cashPointName;
        }
        
        
        
        $retString .= "<div class='gs_shop_small_icon' gstype='downloadpdf' method='downloadPdfZreport' filename='$filename.pdf' zreportid='$row->id'><i class='fa fa-download'></i></div>";
        
        return $retString;
    }
    
    public function formatUser($row) {
        $user = $this->getUserById($row->createdByUserId);
        return $user ? $user->fullName : "N/A";
    }
    
    public function showXReport($fromRender=false) {
        $_SESSION['ns_c20ea6e2_bc0b_4fe1_b92a_0c73b67aead7_activetab'] = "xreport";
        $this->includefile('xreport');
        die();
    }
    
    public function showMasterReport($fromRender=false) {
        $_SESSION['ns_c20ea6e2_bc0b_4fe1_b92a_0c73b67aead7_activetab'] = "masterreport";
        $this->includefile('masterreport');
        $this->includefile('xreport');
        die();
    }
    
    public function showReportList($fromRender=false) {
        $_SESSION['ns_c20ea6e2_bc0b_4fe1_b92a_0c73b67aead7_activetab'] = "reportlist";
        
        echo "<div class='reportheader'>".$this->__f("You have the following reports")."</div>";
        $filterOptions = new \core_common_FilterOptions();
        $args = array($filterOptions);

        $attributes = array(
            array('rowaction', '', '', 'formatActivateButton'),
            array('id', 'gs_hidden', 'id'),
            array('rowCreatedDate', 'CREATED DATE', '', 'formatRowCreatedDate'),
            array('user', 'Created by', '', 'formatUser'),
            array('totalAmount', 'TOTAL', 'totalAmount'),
            array('cashPointName', 'CASHPOINT', '', 'formatCashPointName')
        );
        
        $table = new \GetShopModuleTable($this, 'PosManager', 'getZReportsUnfinalized', $args, $attributes);
        $sortingArray= array();
        $sortingArray[] = "rowCreatedDate";
        $sortingArray[] = "user";
        $sortingArray[] = "totalAmount";
        $sortingArray[] = "cashPointName";
        
        $table->setSorting($sortingArray);
        $table->renderPagedTable();
        if (!$fromRender) 
            die();
    }

    public function selectZReport() {
        $_SESSION['ns_c20ea6e2_bc0b_4fe1_b92a_0c73b67aead7_activetab'] = "xreport";
        $_SESSION['ns_c20ea6e2_bc0b_4fe1_b92a_0c73b67aead7_selected_z_report'] = $_POST['data']['zreportid'];
    }

    public function addOrderToReport() {
        if (isset($_POST['data']['addorder']) && isset($_POST['data']['password']) && isset($_SESSION['ns_c20ea6e2_bc0b_4fe1_b92a_0c73b67aead7_selected_z_report']) && $_SESSION['ns_c20ea6e2_bc0b_4fe1_b92a_0c73b67aead7_selected_z_report']) {
            $this->getApi()->getPosManager()->addOrderIdToZReport($_POST['data']['addorder'], $_SESSION['ns_c20ea6e2_bc0b_4fe1_b92a_0c73b67aead7_selected_z_report'], $_POST['data']['password']);
        }
    }

    public function getPaymentMethodName($paymentNameSpaceId) {
        if (isset($this->cachedPaymentNames[$paymentNameSpaceId])) {
            return $this->cachedPaymentNames[$paymentNameSpaceId];
        }
        
        $paymentAppId = str_replace("ns_", "", $paymentNameSpaceId);
        $paymentAppId = str_replace("_", "-", $paymentAppId);
        $app = $this->getApi()->getStoreApplicationPool()->getApplication($paymentAppId);
        $instance = $this->getFactory()->getApplicationPool()->createInstace($app);
        if($instance) {
            $name = $instance->getName();
            $this->cachedPaymentNames[$paymentNameSpaceId] = $name;
        } else {
          echo "Instance on app: ". $app->id . " does not exists (" . $paymentNameSpaceId . ")"; 
          exit(0);
        }
        return $name;
    }

    /**
     * @return \core_pos_CanCloseZReport
     */
    public function getCloseResult() {
        return $this->closeResult;
    }
    
    public function canMakeZReport() {
        $this->closeResult = $this->getApi()->getPosManager()->canCreateZReport($this->getSelectedMultilevelDomainName(), $this->getSelectedCashPointId());
        $this->includefile("closeerrors");
        return $this->closeResult->canClose;
    }
    
    public function deleteZReport() {
        
    }
    
    public function recalculateSegments() {
        $segments = $this->getApi()->getPmsCoverageAndIncomeReportManager()->getSegments($this->getSelectedMultilevelDomainName());
        foreach ($segments as $segment) {
            $this->getApi()->getPmsCoverageAndIncomeReportManager()->recalculateSegments($this->getSelectedMultilevelDomainName(), $segment->id);
        }
    }
    
    public function deleteOrder() {
        $this->getApi()->getOrderManager()->deleteOrder($_POST['data']['orderid']);
    }

    public function getSumTotalOfItems($items, $includeTaxes) {
        $total = 0;
        foreach ($items as $item) {
            if ($includeTaxes) {
                $total += $item->count * $item->product->price;
            } else {
                $total += $item->count * $item->product->priceExTaxes;
            }
        }
        return $total;
    }

    /**
     * 
     * @param type $taxGroupNumber
     * @param \core_productmanager_data_TaxGroup[] $taxGroups
     * @return \core_productmanager_data_TaxGroup TaxGroup
     */
    public function getTaxGroup($taxGroupNumber, $taxGroups) {
        foreach ($taxGroups as $taxGroup) {
            if ($taxGroup->groupNumber == $taxGroupNumber) {
                return $taxGroup;
            }
        }
        
        return null;
    }

    public function getCount($items) {
        $total = 0;
        foreach ($items as $item) {
            $total += $item->count;
        }
        return $total;
    }
    public function generateExcelReport() {
        $header = array();
        $header[] = "Product";
        $header[] = "Count";
        $header[] = "Ex taxes";
        $header[] = "Avg price";
        $header[] = "Taxes";
        
        $matrix = $this->createSalesPosReportMatrix();
        
        array_unshift($matrix, $header);
        
//        $newMatrix = array();
//        $newMatrix[] = $header;
//        foreach($matrix as $row) {
//            $newMatrix[] = $row;
//        }
        
        echo json_encode($matrix);
    }

    public function getStart() {
        if(isset($_SESSION['ns_c20ea6e2_bc0b_4fe1_b92a_0c73b67aead7_start'])) {
            return $_SESSION['ns_c20ea6e2_bc0b_4fe1_b92a_0c73b67aead7_start'];
        }
        return date("d.m.Y", time()-(86400*7));
    }

    public function getEnd() {
        if(isset($_SESSION['ns_c20ea6e2_bc0b_4fe1_b92a_0c73b67aead7_end'])) {
            return $_SESSION['ns_c20ea6e2_bc0b_4fe1_b92a_0c73b67aead7_end'];
        }
        return date("d.m.Y", time());
    }

    public function createSalesPosReportMatrix() {
        
        $matrix = array();
        $allProducts = $this->getApi()->getProductManager()->getAllProductsLight();
        $sorter = array();
        foreach($allProducts as $prod) {
            $sorter[$prod->id] = $prod->name;
        }

        $filter = new \core_pos_SalesReportFilter();
        $filter->start = $this->convertToJavaDate(strtotime($this->getStart()));
        $filter->end = $this->convertToJavaDate(strtotime($this->getEnd()));
        $result = $this->getApi()->getPosManager()->getSalesReport($filter);
        $totalCounter = 0;
        $totalValue = 0;
        $totalTaxes = 0;
        foreach($result->entries as $entry) {
            foreach($sorter as $prodId => $productName) {
                $prodCounter = (array)$entry->productCounter;
                $prodValue = (array)$entry->productValue;
                $prodTaxes = (array)$entry->productTaxes;
                if(!array_key_exists($prodId, $prodCounter)) {
                    continue;
                }
                $counter = $prodCounter[$prodId];
                $value = $prodValue[$prodId];
                $taxes = $prodTaxes[$prodId];
                $row = array();
                $row[] .= $productName;
                $row[] .= $counter;
                $row[] .= round($value, 2);
                $row[] .= round($value/$counter, 2);
                $row[] .= round($taxes, 2);
                $matrix[] = $row;
                $totalCounter += $counter;
                $totalValue += $value;
                $totalTaxes += $taxes;
            }
            
            $sum = array();
            $sum[] = "Total";
            $sum[] = $totalCounter;
            $sum[] = round($totalValue,2);
            $sum[] = round($totalValue/$totalCounter,2);
            $sum[] = round($totalTaxes,2);
            $matrix[] = $sum;
        }
        
        return $matrix;

    }

    public function setNewMonthForStock() {
        $data = explode(".", $_POST['data']['gsvalue']);
        $_SESSION['ns_c20ea6e2_bc0b_4fe1_b92a_0c73b67aead7_stock_year'] = $data[1];
        $_SESSION['ns_c20ea6e2_bc0b_4fe1_b92a_0c73b67aead7_stock_month'] = $data[0];
    }
}
?>

