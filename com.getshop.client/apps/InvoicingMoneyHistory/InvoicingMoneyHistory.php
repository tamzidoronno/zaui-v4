<?php
namespace ns_6c5268a0_26ea_4905_8f23_79f5410912a8;

class InvoicingMoneyHistory extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "InvoicingMoneyHistory";
    }

    public function render() {
        $this->includefile("paymentlist");
    }

     public function getMonth() {
        if (isset($_SESSION['ns_6c5268a0_26ea_4905_8f23_79f5410912a8_month'])) {
            return $_SESSION['ns_6c5268a0_26ea_4905_8f23_79f5410912a8_month'];
        }
        
        return date('m');
    }

    public function getYear() {
        if (isset($_SESSION['ns_6c5268a0_26ea_4905_8f23_79f5410912a8_year'])) {
            return $_SESSION['ns_6c5268a0_26ea_4905_8f23_79f5410912a8_year'];
        }
        return date('Y');
    }
    
    public function getTotalPaidAmount($order) {
        $amountPaid = 0.0;
        
        foreach ($order->orderTransactions as  $trans) {
            $amountPaid += $trans->amount;
        }
        
        return $amountPaid;
    }
    
    public function showMonth() {
        $_SESSION['ns_6c5268a0_26ea_4905_8f23_79f5410912a8_month'] = $_POST['data']['month'];
        $_SESSION['ns_6c5268a0_26ea_4905_8f23_79f5410912a8_year'] = $_POST['data']['year'];
    }

    public function getNullOrdersArray($grouped) {
        $ordersToRemove = array();
        $toRemove = array();
        
        foreach ($grouped as $day => $transactionDtos) {
            $groupedByOrderIds = array();
            
            foreach ($transactionDtos as $dto) {
                $groupedByOrderIds[$dto->orderLight->incrementOrderId][] = $dto;
            }
            
            
            foreach (array_keys($groupedByOrderIds) as $orderId) {
                $dtos = $groupedByOrderIds[$orderId];
                $total = 0;
                
                foreach ($dtos as $dto) {
                    $total += $dto->orderTransaction->amount;
                }
                
                if ($total == 0) {
                    $toRemove[$day][$orderId] = true;

                }
            }
        }
        
        return $toRemove;
    }

}
?>
