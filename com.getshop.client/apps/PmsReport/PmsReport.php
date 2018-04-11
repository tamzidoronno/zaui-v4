<?php
namespace ns_39fd9a07_94ea_4297_b6e8_01e052e3b8b9;

class PmsReport extends \MarketingApplication implements \Application {
    var $data;
    
    public function getDescription() {
        
    }

    public function getName() {
        return "PmsReport";
    }

    public function getReport() {
        return array();
    }
    /* @var $input \core_pmsmanager_StatisticsEntry */
    public function formatDate($input) {
        if(!$input->date) {
            return "Total";
        }
        return date("d.m.Y", strtotime($input->date));
    }
    public function formatRevPar($input) {
        return round($input->revPar);
    }
    
    public function render() {
        if($this->getApi()->getPmsManager()->hasNoBookings($this->getSelectedMultilevelDomainName())) {
            $this->includefile("nobookingsyet");
            return;
        }
        
        $this->includefile("reportfilter");
        
        $selectedFilter = $this->getSelectedFilter();
        
        echo "<br><br>";
        echo "<div class='reportview'>";
        if(strstr($selectedFilter->type, "coverage")) {
            $this->printCoverageReport();
        } else {
            $this->printIncomeReport();
        }
        echo "</div>";
        
    }

    public function getSelectedFilter() {
        if(isset($_SESSION['savedfilter'])) {
            $filter = json_decode($_SESSION['savedfilter']);
        } else {
            $filter = new \stdClass();
            $filter->type = "coverage";
            $filter->start = date("d.mY", time());
            $filter->end = date("d.mY", time());
            $filter->includeNonBookableRooms = false;
            $filter->view = "daily";
        }
        
        $filter->start = date("d.m.Y 00:00", strtotime($filter->start));
        $filter->end = date("d.m.Y 23:59", strtotime($filter->end));
        
        return $filter;
    }

    public function setReportFilter() {
        $filter = $this->getSelectedFilter();
        $filter->type = $_POST['data']['type'];
        $filter->start = $_POST['data']['start'];
        $filter->end = $_POST['data']['end'];
        $filter->includeNonBookableRooms = $_POST['data']['includenonbookable'] == "true";
        $filter->view = $_POST['data']['view'];
        
        $_SESSION['savedfilter'] = json_encode($filter);
    }

    public function printCoverageReport() {
        $filter = $this->getCoverageFilter();
        $data = $this->getApi()->getPmsManager()->getStatistics($this->getSelectedMultilevelDomainName(), $filter);
        $attributes = array(
            array('date', 'Date', null, 'formatDate'),
            array('avilable', 'Available rooms', 'spearRooms', null),
            array('rentedout', 'Rented out', 'roomsRentedOut', null),
            array('guests', 'Guests', 'guestCount', null),
            array('avprice', 'Avg. price', "avgPrice", null),
            array('revpar', 'RevPar', 'revPar', "formatRevPar"),
            array('total', 'Total', 'totalPrice', null),
            array('budget', 'Budget', 'bugdet', null),
            array('Coverage', 'Coverage', 'coverage', null)
        );
        
        $table = new \GetShopModuleTable($this, 'PmsManager', 'loadCoverageResult', null, $attributes);
        $table->setData($data->entries);
        $table->render();
        $_SESSION['latestpmscoverageresult'] = json_encode($data);
    }
    
    public function PmsManager_loadCoverageResult() {
        $this->includefile("coveragedropdownresult");
    }
    
    
    /**
     * @param \core_pmsmanager_PmsOrderStatsFilter $filter
     */
    public function addPaymentTypeToFilter($filter) {
        
        $paymentMethods = $this->getApi()->getStoreApplicationPool()->getActivatedPaymentApplications();

        foreach ($paymentMethods as $key => $method) {
            if($filter->savedPaymentMethod != "allmethods" && $method->id != $filter->savedPaymentMethod) {
                continue;
            }
            
            $id = $method->id;
            if($id == "70ace3f0-3981-11e3-aa6e-0800200c9a66" || $id == "cbe3bb0f-e54d-4896-8c70-e08a0d6e55ba") {
                //Invoices and samlefaktura needs to include everything
                $method = new \core_pmsmanager_PmsPaymentMethods();
                $method->paymentMethod = $id;
                $method->paymentStatus = 0;
                $filter->methods[] = $method;
                continue;
            }
            $method = new \core_pmsmanager_PmsPaymentMethods();
            $method->paymentMethod = $id;
            $method->paymentStatus = 7;
            $filter->methods[] = $method;
            
            $method = new \core_pmsmanager_PmsPaymentMethods();
            $method->paymentMethod = $id;
            $method->paymentStatus = -9;
            $filter->methods[] = $method;
        }
        
        if($filter->savedPaymentMethod == "transferredtoaccounting") {
            $method = new \core_pmsmanager_PmsPaymentMethods();
            $method->paymentMethod = "";
            $method->paymentStatus = -10;
            $filter->methods[] = $method;
        }
        
        return $filter;
    }

    

    public function printIncomeReport() {
        $selectedFilter = $this->getSelectedFilter();
        $filter = new \core_pmsmanager_PmsOrderStatsFilter();
        $filter->start = $this->convertToJavaDate(strtotime($selectedFilter->start));
        $filter->end = $this->convertToJavaDate(strtotime($selectedFilter->end));
        $filter->includeVirtual = false;
        $filter->savedPaymentMethod = "allmethods";
        $filter = $this->addPaymentTypeToFilter($filter);
        $filter->displayType = "dayslept";
        $filter->priceType = "extaxes";

        if(stristr($selectedFilter->type, "forecasted")) {
            $filter->includeVirtual = true;
        }
        if(!$this->data) {
            $data = $this->getApi()->getPmsInvoiceManager()->generateStatistics($this->getSelectedMultilevelDomainName(), $filter);
        } else {
            $data = $this->data;
        }
        
        $rows = array();
        $products = array();
        $allProducts = $this->indexList($this->getApi()->getProductManager()->getAllProductsLight());
        
        $total = 0;
        
        //Creating product array.
        foreach($data->entries as $entry) {
            foreach($entry->priceEx as $productId => $val) {
                if(!isset($products[$productId])) {
                    $products[$productId] = 0;
                }
                $products[$productId] += $val;
                $total += $val;
            }
        }
        $products["total"] = $total;
        foreach($products as $productId => $val) {
            $products[$productId] = round($val);
        }
        $idx = 0;
        $prodsInUse = array();
        foreach($products as $productId => $val) {
            $prodsInUse["product".$idx] = $productId;
            $idx++;
        }
        $_SESSION['pmsreport_productsinuse'] = json_encode($prodsInUse);
        $_SESSION['pmsreport_dataresult'] = json_encode($data);
        
        //Creating daily rows.
        foreach($data->entries as $entry) {
            $row = new \stdClass();
            $row->date = date("d.m.Y", strtotime($entry->day));
            $index = 0;
            $total = 0;
            foreach($products as $productId => $val) {
                if(isset($entry->priceEx->{$productId})) {
                    $total += $entry->priceEx->{$productId};
                    $row->{"product".$index} = round($entry->priceEx->{$productId});
                } else {
                    $row->{"product".$index} = "0";
                }
                $index++;
            }
            $row->{"total"} = round($total);
            $rows[] = $row;
        }
        
        //Creating attributes.
        $attributes = array(array('date', 'Date', 'date',null));
        $index = 0;
        foreach($products as $productId => $total) {
            if($productId == "total") { continue; }
            $attributes[] = array("product".$index, @$allProducts[$productId]->name, "product". $index, null);
            $index++;
        }
        
        //Adding total bottom row.
        $row = new \stdClass();
        $totalAllDays = 0;
        $totalAllDays += $total;
        $row->date = "Total";
        $index = 0;
        foreach($products as $productId => $total) {
            if($productId == "total") { continue; }
            $row->{"product".$index} = $total;
            $index++;
        }
        $row->{"total"} = $totalAllDays;
        $rows[] = $row;
        $attributes[] = array('total', 'Total', 'total',null);
        
        $table = new \GetShopModuleTable($this, 'PmsManager', 'loadIncomeReportCell', null, $attributes);
        $table->setData($rows);
        $table->render();
        
        echo "<style>";
        echo ".PmsReport .GetShopModuleTable .col { width: " . (85/sizeof($attributes)). "% !important; }";
        echo "</style>";
    }

    public function PmsManager_loadIncomeReportCell() {
        
        $data = json_decode($_SESSION['pmsreport_dataresult']);
        $products = json_decode($_SESSION['pmsreport_productsinuse'], true);
        $date = $_POST['data']['date'];
        $productId = $products[$_POST['data']['gscolumn']];
        
        echo "<table>";
        echo "<tr>";
        echo "<th>Order id</th>";
        echo "<th>User</th>";
        echo "<th>Amount</th>";
        echo "<th>Created date</th>";
        echo "</tr>";
        
        $total = 0;
        foreach($data->entries as $entry) {
            if(date("d.m.Y", strtotime($entry->day)) == $date) {
               foreach($entry->priceExOrders->{$productId} as $orderId => $val) {
                   $order = $this->getApi()->getOrderManager()->getOrder($orderId);
                   $user = $this->getApi()->getUserManager()->getUserById($order->userId);
                   echo "<tr>";
                   echo "<td>" . $order->incrementOrderId . "</td><td>" . $user->fullName . "</td><td align='center'>" . round($val) . "</td>";
                   echo "<td>" . date("d.m.Y H:i", strtotime($order->rowCreatedDate)) . "</td>";
                   echo "</td>";
                   $total += $val;
               }
            }
        }
        echo "<tr><td colspan='10'>Total : " . round($total) . "</td></tr>";;
        echo "</table>";

    }
    
    public function getCoverageFilter() {
        $selectedFilter = $this->getSelectedFilter();
        $filter = new \core_pmsmanager_PmsBookingFilter();
        $filter->startDate = $this->convertToJavaDate(strtotime($selectedFilter->start));
        $filter->endDate = $this->convertToJavaDate(strtotime($selectedFilter->end));
        $filter->timeInterval = $selectedFilter->view;
        $filter->includeVirtual = false;
        
        if(stristr($selectedFilter->type, "forecasted")) {
            $filter->includeVirtual = true;
        }
        $filter->includeNonBookable = $selectedFilter->includeNonBookableRooms;
        return $filter;
    }

}
?>
