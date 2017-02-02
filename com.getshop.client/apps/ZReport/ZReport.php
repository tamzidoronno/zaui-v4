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

    public function closePeriode() {
        $closedPeriode = new \core_ordermanager_data_ClosedOrderPeriode();
        $closedPeriode->startDate = $_SESSION['ZREPORT_JAVA_FROM'];
        $closedPeriode->endDate = $_SESSION['ZREPORT_JAVA_TO'];
        $closedPeriode->paymentTypeId = $_SESSION['ZREPORT_JAVA_PAYMENTID'];
        $this->getApi()->getOrderManager()->addClosedPeriode($closedPeriode);
        
    }
    
    public function getOrders() {
        $orders = array();

        if (isset($_POST['data']['from'])) {
            $javaFrom = $this->convertToJavaDate(strtotime($_POST['data']['from']));
            $javaTo = $this->convertToJavaDate(strtotime($_POST['data']['to']));
            
            $_SESSION['ZREPORT_JAVA_FROM'] = $javaFrom;
            $_SESSION['ZREPORT_JAVA_TO'] = $javaTo;
            $_SESSION['ZREPORT_JAVA_PAYMENTID'] = $_POST['data']['paymentid'];
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
   
    /**
     * 
     * @param type $orders
     * @return \core_cartmanager_data_CartItem[]
     */
    public function groupByProduct($orders) {
        $cartItems = new \stdClass();
        foreach ($orders as $order) {
            
            if (!isset($order->cart->items))
                continue;
            
            foreach ($order->cart->items as $cartItem) {
                if (!isset($cartItems->{$cartItem->product->id})) {
                    $cartItems->{$cartItem->product->id} = array();
                }
                $cartItems->{$cartItem->product->id}[] = $cartItem;
            }
        }
        
        return $cartItems;
    }
}
?>
