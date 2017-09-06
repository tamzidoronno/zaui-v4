<?php
namespace ns_a6f62935_89bf_4e89_ae9a_647f2b25a432;

class PmsSubscriptionOverview extends \WebshopApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "PmsSubscriptionOverview";
    }
    
    public function downloadSummaryReport() {
        $start = strtotime("-1 month");
        $start = strtotime(date("05.m.Y", $start));
        $end = strtotime(date("t.m.Y", time()));

        $result = $this->getApi()->getPmsReportManager()->getSubscriptionReport($this->getSelectedName(), $this->convertToJavaDate($start), $this->convertToJavaDate($end));
        $rows = array();
        foreach($result as $res) {
            if(sizeof($res->orders) == 0) {
                $row = array();
                $row[] = $res->itemName;
                $row[] = $res->owner;
                $row[] = "";
                $row[] = "";
                $row[] = "";
                $row[] = "";
                $rows[] = $row;
            }
            foreach($res->orders as $ord) {
                $row = array();
                $row[] = $res->itemName;
                $row[] = $res->owner;
                $row[] = $ord->incrementOrderId;
                $row[] = substr($ord->payment->paymentType, strpos($ord->payment->paymentType,"\\")+1);
                $row[] = round($this->getApi()->getOrderManager()->getTotalAmountExTaxes($ord));
                $row[] = $ord->status == 7 ? "yes" : "no";
                $rows[] = $row;
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
