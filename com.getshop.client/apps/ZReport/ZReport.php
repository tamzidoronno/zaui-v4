<?php
namespace ns_b6143df9_a5cd_424c_9f17_8e24616b2c3f;

class ZReport extends \ReportingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "ZReport";
    }

    public function render() {
        $this->includefile("zreport");
    }
    
    public function showReport() {
        $_SESSION['ZREPORT_ALL_DATA'] = json_encode($_POST);
    }

    public function getOrders() {
        $orders = array();

        if (isset($_POST['data']['from'])) {
            $javaFrom = $this->convertToJavaDate(strtotime($_POST['data']['from']));
            $javaTo = $this->convertToJavaDate(strtotime($_POST['data']['to']));
            $orders = $this->getApi()->getOrderManager()->getOrdersPaid($_POST['data']['paymentid'], $_POST['data']['userid'], $javaFrom, $javaTo);
        }
        
        return $orders;
    }

    public function downloadExcelReport() {
        $_POST = json_decode($_SESSION['ZREPORT_ALL_DATA'], true);
        
        $orders = $this->getOrders();
        $rows = array();
        $sum = 0;
        
        $header = array();
        $header[] = "Date";
        $header[] = "Orderid";
        $header[] = "Description";
        $header[] = "Sum";
        
        $rows[] = $header;
        
        foreach ($orders as $order) {

            foreach ($order->payment->transactionLog as $time => $text) {
                $row = array();
                $readableTime = date("Y-m-d H:i:s", substr($time, 0, 10));
                $total = $this->getApi()->getOrderManager()->getTotalAmount($order);
                $sum += $total; 
                $row[] = $readableTime;
                $row[] = $order->incrementOrderId;
                $row[] = str_replace(",", "_", $text);
                $row[] = $total;
                $rows[] = $row;
            }
        }
        
        $endRow = array();
        $endRow[] = "Total";
        $endRow[] = "";
        $endRow[] = "";
        $endRow[] = $sum;
        
        $rows[] = $endRow;
        
        echo json_encode($rows);
        die();
    }
}
?>
