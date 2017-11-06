<?php
namespace ns_a6f62935_89bf_4e89_ae9a_647f2b25a432;

class PmsSubscriptionOverview extends \WebshopApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "PmsSubscriptionOverview";
    }
    
    public function downloadSummaryReport() {
        $this->doDownloadSummaryReport(false);
    }
    public function downloadSummaryReportUnpaid() {
        $this->doDownloadSummaryReport(true);
    }
    
    private function doDownloadSummaryReport($unpaidOnly) {
        $start = strtotime("-1 month");
        $start = strtotime(date("05.m.Y", $start));
        $end = strtotime(date("t.m.Y", time()));
        
        
        if($this->getFactory()->getStore()->id == "cd94ea1c-01a1-49aa-8a24-836a87a67d3b") {
            $start = strtotime("-1 month");
            $start = strtotime(date("01.m.Y 00:00", $start));
            $end = strtotime(date("t.m.Y 23:59", time()));
        }

        $result = $this->getApi()->getPmsReportManager()->getSubscriptionReport($this->getSelectedName(), $this->convertToJavaDate($start), $this->convertToJavaDate($end));
        $rows = array();
        
        $header = array();
        $header[] = "Subscription";
        $header[] = "Name";
        $header[] = "Order id";
        $header[] = "Payment type";
        $header[] = "Amount";
        $header[] = "Paid";
        $header[] = "Start";
        $header[] = "End";
        $header[] = "Months since start";
        $rows[] = $header;
        
        foreach($result as $res) {
            $d1 = new \DateTime();
            $d1->setTimestamp(strtotime($res->start));
            $d2 = new \DateTime();
            $d2->setTimestamp(time());
            $months = $d1->diff($d2)->m;

            if(sizeof($res->orders) == 0) {
                $row = array();
                $row[] = $res->itemName;
                $row[] = $res->owner;
                $row[] = "";
                $row[] = "";
                $row[] = "";
                $row[] = "";
                $row[] = date("d.m.Y", strtotime($res->start));
                $row[] = date("d.m.Y", strtotime($res->end));
                $row[] = $months;
                if(!$unpaidOnly) {
                    $rows[] = $row;
                }
            }
            foreach($res->orders as $ord) {
                $total = round($this->getApi()->getOrderManager()->getTotalAmountExTaxes($ord));
                $row = array();
                $row[] = $res->itemName;
                $row[] = $res->owner;
                $row[] = $ord->incrementOrderId;
                $row[] = substr($ord->payment->paymentType, strpos($ord->payment->paymentType,"\\")+1);
                $row[] = $total;
                $row[] = $ord->status == 7 ? "yes" : "no";
                $row[] = date("d.m.Y", strtotime($res->start));
                $row[] = date("d.m.Y", strtotime($res->end));
                $row[] = $months;
                if(!$unpaidOnly) {
                    $rows[] = $row;
                } else if($ord->status != 7 && $total > 0) {
                    if($this->getFactory()->getStore()->id == "cd94ea1c-01a1-49aa-8a24-836a87a67d3b") {
                        if(stristr($res->itemName, "med binding")) {
                            $rows[] = $row;
                        }
                    } else {
                        $rows[] = $row;
                    }
                }
            }
        }
        echo json_encode($rows);        
    }
    
    
    public function selectEngine() {
        $this->setConfigurationSetting("selectedkey", $_POST['data']['name']);
    }
    
    public function getSelectedName() {
        return $this->getConfigurationSetting("selectedkey");
    }
    
    public function render() {
        if(!$this->getSelectedName()) {
            echo "Please specify a booking engine first. <br>";
            $engns = $this->getApi()->getStoreManager()->getMultiLevelNames();

            foreach($engns as $engine) {
                echo '<div gstype="clicksubmit" style="font-size: 16px; cursor:pointer; margin-top: 10px;" method="selectEngine" gsname="name" gsvalue="'.$engine.'">' . $engine . "</div>";
            }
            return;
        }
        $this->includefile("subscriptionoverview");
    }
}
?>
