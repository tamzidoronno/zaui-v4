<?php
namespace ns_b7fb195b_8cea_4d7b_922e_dee665940de2;

class InvoicingOverdueList extends \MarketingApplication implements \Application {
    private $orderTotalAmounts = array();
    
    private $overDueInvoices = null;
    
    public function getDescription() {
        
    }

    public function getName() {
        return "InvoicingOverdueList";
    }

    public function render() {
        echo "<div class='orderlist'>";
            $this->includefile("duelist");
        echo "</div>";
        
        echo "<div class='summary'>";
            $this->includefile("summary");
        echo "</div>";
    }

    public function calculateDaysSince($date) {
        $now = time(); // or your date as well
        $your_date = strtotime($date);
        $datediff = $now - $your_date;

        return round($datediff / (60 * 60 * 24));
    }

    public function getTotalPaidAmount($order) {
        $amountPaid = 0.0;
        
        foreach ($order->orderTransactions as  $trans) {
            $amountPaid += $trans->amount;
        }
        
        return $amountPaid;
        
    }

    /**
     * 
     * @return \core_ordermanager_data_Order[];
     */
    public function getOverDueInvoices() {
        if (!$this->overDueInvoices) {
            $this->overDueInvoices = $this->getApi()->getOrderManager()->getOverdueInvoices(null);
        } 
        
        return $this->overDueInvoices;
    }

    public function getTotalAmountForOrder($order) {
        if (!isset($this->orderTotalAmounts[$order->incrementOrderId])) {
            $this->orderTotalAmounts[$order->incrementOrderId] = $this->getApi()->getOrderManager()->getTotalForOrderById($order->id);
        }
        
        return $this->orderTotalAmounts[$order->incrementOrderId];
    }

    public function getGroupNumber($totalGrouped, $days) {
        for ($i=0;$i<count($totalGrouped); $i++) {
            $low = $i == 0 ? 0 : $totalGrouped[$i-1];
            $high = $totalGrouped[$i];
            if ($low <= $days && $days < $high) {
                return $totalGrouped[$i];
            }
        }
    }

}
?>
