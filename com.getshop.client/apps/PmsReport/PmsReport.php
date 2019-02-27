<?php
namespace ns_39fd9a07_94ea_4297_b6e8_01e052e3b8b9;

class PmsReport extends \MarketingApplication implements \Application {
    var $data;
    var $types;
    var $orderIds;
    
    public function getDescription() {
        
    }
    
    public function getTotalAddons($saleStats) {
        $addonsResult = array();
        foreach($saleStats->entries as $entries) {
            foreach($entries->addonsCount as $addonId => $val) {
                @$addonsResult[$addonId]['count'] += $val;
            }
            foreach($entries->addonsPrice as $addonId => $val) {
                @$addonsResult[$addonId]['price'] += $val;
            }
        }
        
        return $addonsResult;
    }
    
    public function getTotalSlept($saleStats) {
        $total = 0;
        foreach($saleStats->entries as $entry) {
            $total = $entry->{'totalPrice'};
        }
        return $total;
    }

    /**
     * @return core_bookingengine_data_BookingItemType[]
     */
    public function getTypes() {
        if($this->types) {
            return $this->types;
        }
        $types = $this->getApi()->getBookingEngine()->getBookingItemTypesWithSystemType($this->getSelectedMultilevelDomainName(), null);
        $this->types = $this->indexList($types);
        return $this->types;
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
        $today = "";
        if(date("dmy", strtotime($input->date)) == date("dmy", time())) {
            $today = "<i class='fa fa-arrow-left' title='Todays date'></i>";
        }
        return "<span getshop_sorting='".strtotime($input->date)."'>" . date("d.m.Y", strtotime($input->date)) . " $today</span>";
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
        if($selectedFilter->type == "cleaning_report") {
            $this->includefile("cleaning_report");
        } else if($selectedFilter->type == "geographical_report") {
            $this->includefile("geographical_report");
        } else if($selectedFilter->type == "accounting_report") {
            $this->includefile("accounting_report");
        } else if(strstr($selectedFilter->type, "coverage")) {
            $this->printCoverageReport();
        } else {
            if($this->useNewIncomeReport()) {
                $this->includefile("incomereport");
            } else {
                $this->printIncomeReport();
            }
        }
        echo "</div>";
    }

    public function getSelectedFilter() {
        if(isset($_SESSION['savedfilter'])) {
            $filter = json_decode($_SESSION['savedfilter']);
        } else {
            $filter = new \stdClass();
            $filter->type = "coverage";
            $filter->start = date("d.m.Y", time());
            $filter->end = date("d.m.Y", time());
            $filter->includeNonBookableRooms = false;
            $filter->view = "daily";
            $filter->segment = "";
        }
        
        if(!$filter->start) {
            $filter->start = time();
        }
        
        if(!$filter->end) {
            $filter->end = time();
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
        $filter->channel = $_POST['data']['channel'];
        $filter->view = $_POST['data']['view'];
        $filter->departmentIds = array();
        $filter->segment = $_POST['data']['segment'];
        
        if (isset($_POST['data']['departmentid']) && $_POST['data']['departmentid']) {
            $filter->departmentIds[] = $_POST['data']['departmentid'];
        }
        
        $filter->typeFilter = array();
        foreach($_POST['data'] as $key => $val) {
            if(stristr($key, "typeselected_") && $val == "true") {
                $filter->typeFilter[] = str_replace("typeselected_", "", $key);
            }
        }
        
        $_SESSION['savedfilter'] = json_encode($filter);
    }

    public function downloadCoverageReport() {
        $channels = $this->getApi()->getPmsManager()->getChannelMatrix($this->getSelectedMultilevelDomainName());
        $sheets = array();
        $sheets['useMultipleSheets'] = "true";
        
        $newChans = array();
        $newChans[''] = "Total";
        foreach($channels as $chan => $text) {
            $newChans[$chan] = $text;
        }
        $channels = $newChans;
        

        $pmssearchbox = new \ns_961efe75_e13b_4c9a_a0ce_8d3906b4bd73\PmsSearchBookingColumnFormatters(null);
        $chnls = $pmssearchbox->getChannels();        
        $chnls[""] = "Total";
        
        $haederSet = false;
        $filter = $this->getCoverageFilter();
        foreach($channels as $chan => $text) {
            $chanName = "unkown";
            if(isset($chnls[$chan])) {
                $chanName = $chnls[$chan];
            } else {
                continue;
            }

            
            $filter->channel = $chan;
            $data = $this->getApi()->getPmsManager()->getStatistics($this->getSelectedMultilevelDomainName(), $filter);
            $rows = array();
            $header = array();
            $header[] = "Date";
            $header[] = "Available";
            $header[] = "Rented out";
            $header[] = "Arrivals";
            $header[] = "Departures";
            $header[] = "Guests";
            $header[] = "Avg. Price total";
            $header[] = "Avg. Price billed";
            $header[] = "Revpar";
            $header[] = "Total";
            $header[] = "Billed";
            $header[] = "Remaining";
            $header[] = "Coverage";
            $rows[] = $header;

            foreach($data->entries as $d) {
                $row = array();
                if($d->date) {
                    $row[] = date("d/m/Y", strtotime($d->date));
                } else {
                    $row[] = "";
                }
                $row[] = $d->spearRooms;
                $row[] = $d->roomsRentedOut;
                $row[] = $d->arrivals;
                $row[] = $d->departures;
                $row[] = $d->guestCount;
                $row[] = $d->avgPriceForcasted;
                $row[] = $d->avgPrice;
                $row[] = $d->revPar;
                $row[] = $d->totalForcasted;
                $row[] = $d->totalPrice;
                $row[] = $d->totalRemaining;
                $row[] = $d->coverage;
                $rows[] = $row;
            }
            
            
            $sheets[$chanName] = $rows;
        }
         echo json_encode($sheets);
    }
    
    public function downloadAccountingReport() {
        $filter = $this->getIncomeReportFilter();
        $data = $this->getApi()->getPmsInvoiceManager()->getAccountingStatistics($this->getSelectedMultilevelDomainName(), $filter);
        $matrix = $this->createMatrix($data);
        $attrs = $this->createAccountingAttributes($matrix);
        $rows = array();
        $header = array();
        foreach($attrs as $r) {
            $header[] = $r[1];
        }
        $rows[] = $header;
        foreach($matrix as $r) {
            $rows[] = $r;
        }
        echo json_encode($rows);
    }
    
    public function formatAvgPrice($row) {
        return "<span title='Average from billed'>" . $row->avgPrice . "</span> / <span title='Average from total'>" . $row->avgPriceForcasted . "</span>";
    }
    
    public function roundTotalPrice($row) {
        return round($row->totalPrice, 0);
    }
    
    public function formatRemaining($in) {
        $rest = $in->totalForcasted - $in->totalPrice;
        return round($rest, 0);
    }
    
    public function printCoverageReport() {
        $date = date("d-m-Y", time());
        echo "<div style='text-align:right;'>";
        echo "<span style='color:blue; cursor:pointer;' gs_downloadExcelReport='downloadCoverageReport' gs_fileName='coveragereport-$date'>Download this report to excel</span>";
        echo "</div>";
        $filter = $this->getCoverageFilter();
        $data = $this->getApi()->getPmsManager()->getStatistics($this->getSelectedMultilevelDomainName(), $filter);
        $attributes = array(
            array('rowdate', 'gs_hidden', 'date'),
            array('date', 'Date', null, 'formatDate'),
            array('avilable', 'Available', 'spearRooms', null),
            array('rentedout', 'Rented out', 'roomsRentedOut', null),
            array('arrivals', 'Arrivals', 'arrivals', null),
            array('departures', 'Departures', 'departures', null),
            array('guests', 'Guests', 'guestCount', null),
            array('avprice', 'Avg. price', "avgPrice", "formatAvgPrice"),
            array('revpar', 'RevPar', 'revPar', "formatRevPar"),
            array('total', 'Total', 'totalForcasted', null),
            array('totalbilled', 'Billed', 'totalPrice', 'roundTotalPrice'),
            array('totalremaining', 'Remaining', 'totalRemaining', 'formatRemaining'),
            array('Coverage', 'Coverage', 'coverage', null)
        );
        
        $rowsToHighLight = $this->getWeekendRows($data->entries);
        
        $table = new \GetShopModuleTable($this, 'PmsManager', 'loadCoverageResult', null, $attributes);
        $table->setSorting(array("date","total","totalbilled", "totalremaining","arrivals", "departures","avilable","rentedout","guests","avprice","revpar","total","budget","Coverage"));
        $table->setData($data->entries);
        $table->appendClassToRowNumbers($rowsToHighLight, "weekenddate");
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
            if($id == "cbe3bb0f-e54d-4896-8c70-e08a0d6e55ba") {
                continue;
            }
            if($id == "70ace3f0-3981-11e3-aa6e-0800200c9a66") {
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

    public function downloadIncomeReportExTaxes() {
        $this->downloadIncomeReport(true);
    }

    public function downloadIncomeReportIncTaxes() {
        $this->downloadIncomeReport(false);
    }
    
    public function downloadIncomeReport($exTaxes) {
        $res = $this->setIncomeReportData();
        $rows = array();
        $header = array();
        $header[] = "Date";
        $header[] = "Orderid";
        
        $products = array();
        foreach($res->entries as $entry) {
            foreach($entry->priceExOrders as $prodId => $values) {
                $products[$prodId] = 1;
            }
        }
        $idx = 2;
        foreach($products as $prodId => $val) {
            $header[] = $this->getApi()->getProductManager()->getProduct($prodId)->name;
            $prodIdx[$prodId] = $idx;
            $idx++;
        }
        
        $rows[] = $header;
        
        
        foreach($res->entries as $entry) {
            $dayRows = $this->createDayRows($entry, $header);
            $array = $entry->priceExOrders;
            if(!$exTaxes) {
                $array = $entry->priceIncOrders;
            }
            foreach($array as $prodId => $orders) {
                foreach($orders as $orderId => $amount) {
                    if(isset($dayRows[$orderId])) {
                        $dayRows[$orderId][$prodIdx[$prodId]] = round($amount,2);
                    } else {
                        echo "Not created";
                    }
                }
            }
            foreach($dayRows as $r) { $rows[] = $r; }
        }
        echo json_encode($rows);
    }
    
    public function downloadReportRaw() {
        $rows = $this->getIncomeReportRows(true);
        $products = $this->getProductsArray();
        
        $headerrow = array();
        $headerrow[] = "Date";
        
        foreach($products as $prodId => $total) {
            if($prodId == "total") {
                $headerrow[] = "Total";
            } else {
                $headerrow[] = $this->getApi()->getProductManager()->getProduct($prodId)->name;
            }
        }
        
        $rows = array_reverse($rows);
        $rows[] = $headerrow;
        $rows = array_reverse($rows);
        
        echo json_encode($rows);
    }
    
    public function downloadReportRawNew() {
        echo json_encode($this->getIncomeReportData(true));
    }
    
    public function downloadIncomeReportExTaxesNew() {
        echo json_encode($this->getIncomeReportData(true));
    }
    
    public function downloadIncomeReportIncTaxesNew() {
        echo json_encode($this->getIncomeReportData(true));
    }
    
    
    public function printIncomeReport() {
        $this->setIncomeReportData();
        $data = $this->data;
        
        $usersoverview = (array)$data->usersTotal;
        arsort($usersoverview);
        
        $i = 0;
        echo "<div style=' width: 1500px; margin: auto;text-align:right; color:blue; cursor:pointer;'>";
        echo "<span gs_downloadExcelReport='downloadReportRaw' gs_fileName='incomereport'>Download this report to excel</span> - ";
        echo "<span gs_downloadExcelReport='downloadIncomeReportExTaxes' gs_fileName='incomereportraw'>Download to excel ex taxes with </span> - ";
        echo "<span gs_downloadExcelReport='downloadIncomeReportIncTaxes' gs_fileName='incomereport'>Download to excel inc taxes</span>";
        echo "</div>";
        
        echo "<table style='width:1500px; margin:auto;'>";
        foreach($usersoverview as $userId => $val) {
            $user = $this->getApi()->getUserManager()->getUserById($userId);
            if($user->type > 50) {
                continue;
            }
            $display = "";
            if($i > 0) {
                $display = "display:none";
            }
            echo "<tr style='$display' class='profitablecustomers'>";
            if($display) {
                echo "<td>" . $user->fullName . "</td><td>" . round($val) . "</td>";
            } else {
                echo "<td onclick='$(\".profitablecustomers\").toggle()' style='cursor:pointer;'>The most profitable customer is ".$user->fullName." (" .round($val) .") , click here to load top 30 most profitable customers</td>";
            }
            echo "</tr>";
            $i++;
            if($i > 30) {
                break;
            }
        }
        echo "</table>";
 
        $sortAttributes = array("date");
        $index = 0;
        $allProducts = $this->indexList($this->getApi()->getProductManager()->getAllProductsLight());
        $products = $this->getProductsArray();
        foreach($products as $productId => $total) {
            $sortAttributes[] = "product" . $index;
            $index++;
        }
               
        $rows = $this->getIncomeReportRows();

        //Creating attributes.
        $attributes = array(array('date', 'Date', 'date',null));
        $index = 0;
        foreach($products as $productId => $total) {
            if($productId == "total") { continue; }
            $attributes[] = array("product".$index, @$allProducts[$productId]->name, "product". $index, null);
            $index++;
        }
        $attributes[] = array('total', 'Total', 'total',null);
        echo "<div class='reportoverview'>";
        $table = new \GetShopModuleTable($this, 'PmsManager', 'loadIncomeReportCell', null, $attributes);
        $table->setSorting($sortAttributes);
        $table->setData($rows);
        $table->render();
        echo "</div>";
        
        echo "<style>";
        echo ".reportoverview {  overflow-x: scroll; max-width: 1500px; margin:auto; }";
        echo ".PmsReport .GetShopModuleTable .col { width: 100px !important; }";
        echo ".PmsReport .GetShopModuleTable { width: " . ((110*sizeof($attributes))+50). "px !important; }";
        echo ".PmsReport .GetShopModuleTable { max-width: " . ((110*sizeof($attributes))+50). "px !important; }";
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
    
    public function searchCustomerToIncludeInFilter() {
        $input = $_POST['data']['input'];
        $users = $this->getApi()->getUserManager()->findUsers($input);
        if(sizeof($users) == 0) {
            echo "No customers / companies found.";
        } else {
            foreach($users as $usr) {
                echo "<div class='customerselectionrow'>";
                echo "<span class='shop_button includecustomerinfilter' style='float:right;' userid='".$usr->id."'>Select</span>";
                echo $usr->fullName. "<br>";
                echo "</div>";
            }
        }
    }
    
    public function includeCustomerToFilter() {
        $userid = $_POST['data']['userid'];
        $filter = $this->getSelectedFilter();
        if(!isset($filter->customers)) {
            $filter->customers = array();
        } else {
            $filter->customers = (array)$filter->customers;
        }
        $filter->customers[$userid] = "";
        $_SESSION['savedfilter'] = json_encode($filter);
        $this->printAddedCustomers();
     }

     public function removeCustomer() {
        $userid = $_POST['data']['userid'];
        $filter = $this->getSelectedFilter();
        unset($filter->customers->{$userid});
        $_SESSION['savedfilter'] = json_encode($filter);
     }
     
    public function getCoverageFilter() {
        $selectedFilter = $this->getSelectedFilter();
        $typeFilter = array();
        if(isset($selectedFilter->typeFilter)) {
            $typeFilter = $selectedFilter->typeFilter;
        }
        $filter = new \core_pmsmanager_PmsBookingFilter();
        $filter->startDate = $this->convertToJavaDate(strtotime($selectedFilter->start));
        $filter->endDate = $this->convertToJavaDate(strtotime($selectedFilter->end));
        $filter->timeInterval = $selectedFilter->view;
        $filter->includeVirtual = false;
        $filter->fromPms = true;
        $filter->removeAddonsIncludedInRoomPrice = true;
        $filter->typeFilter = $typeFilter;
        $filter->segments = array();
        if($selectedFilter->segment) {
            $filter->segments[] = $selectedFilter->segment;
        }
        
        if(stristr($selectedFilter->type, "forecasted")) {
            $filter->includeVirtual = true;
        }
        $filter->includeNonBookable = $selectedFilter->includeNonBookableRooms;
        $filter->channel = $selectedFilter->channel;
        $filter->customers = array();
        if(isset($selectedFilter->customers)) {
            foreach($selectedFilter->customers as $id => $val) {
                $filter->customers[] = $id;
            }
        }
        return $filter;
    }

    /**
     * 
     * @return \core_pmsmanager_PmsOrderStatistics
     */
    public function setIncomeReportData() {
        $filter = $this->getIncomeReportFilter();
        if(!$this->data) {
            $data = $this->getApi()->getPmsInvoiceManager()->generateStatistics($this->getSelectedMultilevelDomainName(), $filter);
        } else {
            $data = $this->data;
        }
        $this->data = $data;
        return $data;
    }

    public function createEmptyRow($size) {
        $row = array();
        for($i = 0; $i < $size;$i++) {
            $row[] = "";
        }
        return $row;
    }

    public function createDayRows($entry, $header) {
        $max = 0;
        $ordersRow = array();
        foreach($entry->priceExOrders as $orders) {
            foreach($orders as $orderId => $val) {
                $ordersRow[$orderId] = 1;
            }
        }
        $rows = array();
        foreach($ordersRow as $orderId => $val) {
            $rows[$orderId] = $this->createEmptyRow(sizeof($header));
            $rows[$orderId][0] = date("d.m.Y", strtotime($entry->day));
            $rows[$orderId][1] = $this->getIncOrderOnOrder($orderId);
        }
        return $rows;
    }

    public function getIncOrderOnOrder($orderId) {
        if(isset($this->orderIds[$orderId])) {
            return $this->orderIds[$orderId];
        }
        
        $order = $this->getApi()->getOrderManager()->getOrder($orderId);
        $this->orderIds[$orderId] = $order->incrementOrderId;
        return $order->incrementOrderId;
    }

    public function getIncomeReportRows($convertToDateToExcel = false) {
        $this->setIncomeReportData();
        $data = $this->data;

        $rows = array();
        $products = $this->getProductsArray();
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
            if($convertToDateToExcel) {
                $row->date = date("d/m/Y", strtotime($entry->day));
            } else {
                $row->date = date("d.m.Y", strtotime($entry->day));
            }
            $index = 0;
            $total = 0;
            foreach($products as $productId => $val) {
                if($productId == "total") {
                    continue;
                }
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
        
        //Adding total bottom row.
        $row = new \stdClass();
        $totalAllDays = 0;
        $row->date = "Total";
        $index = 0;
        foreach($products as $productId => $total) {
            if($productId == "total") { continue; }
            $row->{"product".$index} = $total;
            $totalAllDays += $total;
            $index++;
        }
        $row->{"total"} = $totalAllDays;
        $rows[] = $row;
        
        return $rows;        
    }

    public function getProductsArray() {
        $this->setIncomeReportData();
        $data = $this->data;

        $products = array();
        
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
        return $products;
    }

    public function getIncomeReportFilter() {
        $selectedFilter = $this->getSelectedFilter();
        $filter = new \core_pmsmanager_PmsOrderStatsFilter();
        $filter->start = $this->convertToJavaDate(strtotime($selectedFilter->start));
        $filter->end = $this->convertToJavaDate(strtotime($selectedFilter->end));
        $filter->includeVirtual = false;
        $filter->savedPaymentMethod = "allmethods";
        $filter->fromPmsModule = true;
        $filter = $this->addPaymentTypeToFilter($filter);
        $filter->displayType = "dayslept";
        $filter->priceType = "extaxes";
        $filter->customers = array(); 
        if(isset($selectedFilter->customers)) {
            foreach($selectedFilter->customers as $id => $val) {
                $filter->customers[] = $id;
            }
        }

        if(stristr($selectedFilter->type, "forecasted")) {
            $filter->includeVirtual = true;
        }
        return $filter;
    }

    public function createMatrix($data) {
        $allProducts = $this->indexList($this->getApi()->getProductManager()->getAllProductsLight());
        $products = $data->productsIncluded;
        $rows = array();
        foreach($data->dayresult as $dayres) {
            /* @var $dayres core_accountingmanager_AccountingSystemStatistics */
            $row = new \stdClass();
            $row->date = date("d.m.Y", strtotime($dayres->date));
            foreach($products as $prodId) {
                if(isset($dayres->productPrices->{$prodId})) {
                    $prodres = $dayres->productPrices->{$prodId};
                    $row->{$prodId} = round($prodres->totalValue); 
                } else {
                   $row->{$prodId} = 0.0; 
                }
            }
            $row->total = round($dayres->total);
            $rows[] = $row;
        }
        
        $summaryRow = new \stdClass();
        $summaryRow->date = "Total";
        $total = 0;
        foreach($data->productsIncluded as $prodInc) {
            $summaryRow->{$prodInc} = round($data->allResult->{$prodInc});
            $total += $data->allResult->{$prodInc};
        }
        $summaryRow->total = round($total);
        $rows[] = $summaryRow;
        return $rows;

    }

    public function createAccountingAttributes($matrix) {
        $attributes = array(array('date', 'Date', 'date',null));
        $allProducts = $this->indexList($this->getApi()->getProductManager()->getAllProductsLight());
        $index = 0;
        foreach($matrix[0] as $productId => $total) {
            if($productId == "date") { continue; }
            if($productId == "total") { continue; }
            $attributes[] = array("product".$index, $allProducts[$productId]->name, $productId, null);
            $index++;
        }
        $date = date("d-m-Y", time());
        $attributes[] = array('total', 'Total', 'total',null);
        return $attributes;
    }

    public function getWeekendRows($rows) {
        $rowcounter = 1;
        $result = array();
        foreach($rows as $row) {
            if((date('N', strtotime($row->date)) >= 6)) {
                $result[] = $rowcounter;
            }
            $rowcounter++;
        }
        return $result;
    }

    public function printAddedCustomers() {
        $selectedfilter = $this->getSelectedFilter();
        if(isset($selectedfilter->customers)) {
            foreach($selectedfilter->customers as $userId => $val) {
                $usr = $this->getApi()->getUserManager()->getUserById($userId);
                echo "<div class='selectedcustomerrow'><span class='fa fa-trash-o removecustomerfromfilter' userid='".$usr->id."'></i> " . $usr->fullName . "</div>";
            }
        }
    }

    public function useNewIncomeReport() {
        $startYear = (int)date("Y", strtotime($this->getSelectedFilter()->start));
        if($startYear >= 2019) {
            return true;
        }
        
        $storeid = $this->getFactory()->getStore()->id;
        if($storeid == "fd2fecef-1ca1-4231-86a6-0ec445fbac83") {
            return true;
        }
        
        return false;
    }

    public function getIncomeReportData($convertToExcelDate) {
        $filter = $this->createPmsCoverageFilter();
        
        $data = $this->getApi()->getPmsCoverageAndIncomeReportManager()->getStatistics($this->getSelectedMultilevelDomainName(), $filter);
        $result = $data->entries;
        if(!$convertToExcelDate) {
            $this->printUsersTotal((array)$data->usersTotal);
        }
        
        $matrix = array();
        $productsInUse = array();
        
        //First find all products in use for this report segment.0
        foreach($result as $day) {
            foreach($day->products as $productId => $total) {
                if($total != 0) {
                    $productsInUse[$productId] = 0;
                }
            }
        }
        
        //Create a header
        $header = array();
        $header[] = "Date";
        foreach($productsInUse as $prodId => $val) {
            $header[] = $this->getApi()->getProductManager()->getProduct($prodId)->name;
        }
        $header[] = "Total";
        $matrix[] = $header;
        
        
        //create a body
        $everything = 0;
        foreach($result as $day) {
            $row = array();
            if($convertToExcelDate) {
                $row[] = date("d/m/Y", strtotime($day->day));
            } else {
                $row[] = date("d.m.Y", strtotime($day->day));
            }
            foreach($productsInUse as $prodId => $val) {
                $row[$prodId] = $day->products->{$prodId};
                $productsInUse[$prodId] += $row[$prodId];
            }
            $row[] = $day->total;
            $everything += $day->total;
            $matrix[] = $row;
        }
            
        //Create a footer 
        $footer = array();
        $footer[] = "";
        foreach($productsInUse as $productId => $total) {
            $footer[$productId] = $total;
        }
        $footer["total"] = $everything;
        $matrix[] = $footer;
        
        return $matrix;
    }
    
    public function quickloaduser() {
        $selected = $this->getSelectedFilter();
        $selected->customers = array();
        $selected->customers[$_POST['data']['userid']] = 1;
        $_SESSION['savedfilter'] = json_encode($selected);
    }
    
    public function loadTop30Customers() {
        $selectedFilter = $this->createPmsCoverageFilter();
        $selectedFilter->userIds = array();
        $md5 = md5(serialize($selectedFilter) . "-" . date("dmy"));
//        
        if(isset($_SESSION['top30res'][$md5])) {
            $res = json_decode($_SESSION['top30res'][$md5], true);
        } else {
            $data = $this->getApi()->getPmsCoverageAndIncomeReportManager()->getStatistics($this->getSelectedMultilevelDomainName(), $selectedFilter);
            $res = (array)$data->usersTotal;
            $_SESSION['top30res'][$md5] = json_encode($data->usersTotal);
        }
        $i = 1;
        
        arsort($res);
        foreach($res as $userId => $amount) {
            $user = $this->getApi()->getUserManager()->getUserById($userId);
            echo "<span class='top30customerbutton' gsclick='quickloaduser' userid='".$user->id."'>$i. " . $user->fullName . " : " . round($amount) . " </span>";
            $i++;
            if($i > 20) {
                break;
            }
        }
    }

    public function printUsersTotal($usersTotal) {
        arsort($usersTotal);
        $counter = 0;
        echo "<table class='top30list'>";
        foreach($usersTotal as $userId => $total) {
            echo "<tr>";
            echo "<td>" . $this->getApi()->getUserManager()->getUserById($userId)->fullName . "</td>";
            echo "<td>" . round($total,2) . "</td>";
            echo "</tr>";
            $counter++;
            if($counter > 30) {
                break;
            }
        }
        echo "</table>";
    }

    public function createPmsCoverageFilter() {
        $selectedFilter = $this->getSelectedFilter();
        $filter = new \core_pmsmanager_CoverageAndIncomeReportFilter();
        $filter->start = $this->convertToJavaDate(strtotime($selectedFilter->start));
        $filter->end = $this->convertToJavaDate(strtotime($selectedFilter->end));
        $filter->incTaxes = false;
        $filter->channel = $selectedFilter->channel;
        $filter->departmentIds = $selectedFilter->departmentIds;
        $filter->segments = array();
        if($selectedFilter->segment) {
            $filter->segments[] = $selectedFilter->segment;
        } 
        if(isset($_POST['event']) && $_POST['event'] == "downloadIncomeReportIncTaxesNew") {
            $filter->incTaxes = true;
        }
        
        foreach($selectedFilter->typeFilter as $typeId) {
            $type = $this->getApi()->getBookingEngine()->getBookingItemType($this->getSelectedMultilevelDomainName(), $typeId);
            $filter->products[] = $type->productId;
        }
        if(isset($selectedFilter->customers)) {
            foreach($selectedFilter->customers as $usrid => $val) {
                $filter->userIds[] = $usrid;
            }
        }
        return $filter;
    }

}
?>
