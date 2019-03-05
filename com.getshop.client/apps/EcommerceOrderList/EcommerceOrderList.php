<?php
namespace ns_9a6ea395_8dc9_4f27_99c5_87ccc6b5793d;

class EcommerceOrderList extends \MarketingApplication implements \Application {
    private $selectedOrder;
    private $orderIds = null;
    private $externalReferenceIds = array();
    public $paymentLinkCallback = "";

    public function getDescription() {
        
    }

    public function getName() {
        return "EcommerceOrderList";
    }

    public function render() {
        $this->printTable();
    }
    
    /**
     * 
     * @param \core_ordermanager_data_Order $order
     * @return string
     */
    public function formatUser($order) { 
        $user = $this->getApi()->getUserManager()->getUserById($order->userId);
        if (!$user) {
            return "N/A";
        }
        
        $text = "<div class='fullname' title='".$user->fullName."'>" . $user->fullName . "</div>";
        $names = array();
        $text .= "<div class='guestnames'>";
        foreach($order->cart->items as $item) {
            if(in_array($item->product->metaData, $names)) {
                continue;
            }
            $names[] = $item->product->metaData;
            $text .= $item->product->metaData . " ";
        }
        $text .= "</div>";
        return $text;
    }
    
    public function formatIncTaxes($order) {
        return \ns_9de54ce1_f7a0_4729_b128_b062dc70dcce\ECommerceSettings::formatPrice($this->getApi()->getOrderManager()->getTotalAmount($order));
    }
    
    public function formatExTaxes($order) {
        return \ns_9de54ce1_f7a0_4729_b128_b062dc70dcce\ECommerceSettings::formatPrice($this->getApi()->getOrderManager()->getTotalAmountExTaxes($order));
    }
    
    public function deleteOrder() {
        $orderid = $_POST['data']['id'];
        $this->getApi()->getOrderManager()->deleteOrder($orderid);
        $this->getApi()->getPmsManager()->orderChanged($this->getSelectedMultilevelDomainName(), $orderid);
    }
    
    public function reloadRow() {
        $order = $this->getSelectedOrder();
        $result = array();
        $result['rowCreatedDate'] = $this->formatRowCreatedDate($order);
        $result['paymentDate'] = $this->formatPaymentDate($order);
        $result['user'] = $this->formatUser($order);
        $result['shipmentdate'] = $this->formatShipmentDate($order);
        $result['payment'] = $this->formatPaymentType($order);
        $result['inctaxes'] = $this->formatIncTaxes($order);
        $result['extaxes'] = $this->formatExTaxes($order);
        $result['state'] = $this->formatState($order);
        echo json_encode($result);
    }
    
    public function formatPaymentDate($order) {
        $text = "<span style='display:none;' hiddenorderid='".$order->id."'></span>";
        if($order->status != 7) {
            $text .= "<b style='color:red;'>NOT PAID</b>";
        } else {
            if ($order->paymentDate) {
                $text .= \GetShopModuleTable::formatDate($order->paymentDate);
            } else {
                $text .= "N/A";
            }
        }
        $text = "<div>" . $text . "</div>";
        if($order->recieptEmail) {
            $text .= "<div class='recieptemail' title='Reciept email'>" . $order->recieptEmail . "</div>";
        }
        return $text;
    }
    
    public function formatTransferredToAccounting($order) {
        if ($order->transferredToAccountingSystem) {
            return "<i class='fa fa-check'></i>";
        } else {
            return "<i class='fa fa-close'></i>";
        }
    }
    
    /**
     * @param \core_ordermanager_data_Order $order
     * @return string
     */
    public function formatState($order) {
        $roomid = "";
        if($this->externalReferenceIds) {
            $roomid = $this->externalReferenceIds[0];
        }
        
        $text = str_replace("\\", "\\\\", $order->payment->paymentType) . "()";
        if(!$order->payment->paymentType) {
            return;
        }
        $text = "\\" . $order->payment->paymentType;
        $instance = new $text();
        
        $text = "";
        if ($order->closed && !@count($order->creditOrderId) && !$order->isCreditNote) {
            $text = '<i class="fa fa-lock" title="Order has been locked"></i>';
            $text .= '<i class="fa fa-history dontExpand creditOrder" style="cursor:pointeR;" orderid="'.$order->id.'" title="Credit order"></i>';
        }
        
        if (!$order->closed && !@count($order->creditOrderId)) {
            $text = '<i class="fa fa-unlock"></i>';
            $text .= "<i class='fa fa-trash-o dontExpand deleteOrder' style='cursor:pointeR;' orderid='".$order->id."' title='Delete order'></i>";
        }
        $text .= "<i class='fa fa-download dontExpand' title='Download order' style='cursor:pointeR;' onclick='window.open(\"/scripts/downloadInvoice.php?orderId=".$order->id."&incrementalOrderId=".$order->incrementOrderId."\");'></i>";
        $text .= "<i class='fa fa-forward dontExpand sendemail' roomid='".$roomid."' orderid='".$order->id."' title='Send order / reciept' style='cursor:pointer;'></i><span class='sendpaymentlinkwindow'></span> ";
        if($instance->hasPaymentLink()) {
            $text .= "<i class='fa fa-arrow-right dontExpand sendpaymentlink' roomid='".$roomid."' callback='".$this->paymentLinkCallback."' orderid='".$order->id."' title='Send payment link' style='cursor:pointer;'></i> ";
        }
        if(@$order->invoiceNote) {
            $text .= "<i class='fa fa-sticky-note' title='".$order->invoiceNote."'></i>";
        }
        
        $text .= $this->getVerifoneTerminalPrinter($order);
        $text .= $this->getCashPointPrinters($order);
        
        return $text;
    }
    
    public function sendReceipt() {
        $this->getApi()->getInvoiceManager()->sendReceiptToCashRegisterPoint($_POST['data']['deviceid'], $_POST['data']['orderid']);
    }
    
    public function updatePaymentLinkText() {
        $booking = $this->getApi()->getPmsManager()->getBookingWithOrderId($this->getSelectedMultilevelDomainName(), $_POST['data']['orderid']);
        $selectedPrefix = $_POST['data']['prefix'];
        $mesage = $this->getApi()->getPmsNotificationManager()->getSpecificMessage($this->getSelectedMultilevelDomainName(), "booking_sendpaymentlink", $booking, null, "sms", $selectedPrefix);
        if(!$mesage) {
            $mesage = $this->getApi()->getPmsNotificationManager()->getSpecificMessage($this->getSelectedMultilevelDomainName(), "booking_sendpaymentlink", $booking, null, "email", $selectedPrefix);
        } 
        $msg = $mesage->content;
        $msg = str_replace("{name}", $_POST['data']['name'], $msg);
        echo $msg;
    }
    
    public function creditOrder() {
        $creditedOrder = $this->getApi()->getOrderManager()->creditOrder($_POST['data']['id']);
        $booking = $this->getApi()->getPmsManager()->getBookingWithOrderId($this->getSelectedMultilevelDomainName(), $_POST['data']['id']);
        if($booking) {
            $booking->orderIds[] = $creditedOrder->id;
            $this->getApi()->getPmsManager()->saveBooking($this->getSelectedMultilevelDomainName(), $booking);
            $this->getApi()->getOrderManager()->saveOrder($creditedOrder);
        }
    }
    
    public function formatRowCreatedDate($order) {
        return \GetShopModuleTable::formatDate($order->rowCreatedDate);
    }
    
    public function OrderManager_getOrdersFiltered() {
        $app = new \ns_bce90759_5488_442b_b46c_a6585f353cfe\EcommerceOrderView();
        $app->externalReferenceIds = $this->externalReferenceIds;
        $app->loadOrder($_POST['data']['id']);
        $app->renderApplication(true, $this);
    }
    
    public function formatPaymentType($order) {
        $id = $order->payment->paymentType;
        $id = substr($id, 0, strpos($id, "\\"));
        $id = str_replace("ns_", "", $id);
        $id = str_replace("_", "-", $id);
        $app = $this->getFactory()->getApplicationPool()->getApplicationSetting($id);
        $appInstance = $this->getFactory()->getApplicationPool()->createInstace($app);
        if(!$appInstance || !method_exists($appInstance, "getName")) {
            $name = "No method " . $id;
        } else {
            $name = $appInstance->getName();
            if($name == "InvoicePayment") {
                $name = "Invoice";
            }
        }
        return $name;
    }
    
    public function formatIncrementOrderId($row) {
        $ret = "";
        if ($this->externalReferenceIds) {
            $roomId = $this->externalReferenceIds[0];
            $corroloate = $this->getApi()->getPmsManager()->doesOrderCorrolateToRoom($this->getSelectedMultilevelDomainName(),$roomId, $row->id);
            if (!$corroloate) {
                $ret .= "<i class='fa fa-warning not_connected_to_room' title='".$this->__f("This order has no orderlines for the current room you are looking at, probably belongs to other rooms in the group.")."'></i>";
            }
        }
        $ret .= $row->incrementOrderId;
        return $ret;
    }

    public function printTable() {
        $filterOptions = new \core_common_FilterOptions();
        
        if ($this->orderIds) {
            if (!$filterOptions->extra) {
                $filterOptions->extra = new \stdClass();
            }
            
            $filterOptions->extra->orderids = implode(",", $this->orderIds);
        }
  
        $args = array($filterOptions);
        
        $attributes = array(
            array('id', 'gs_hidden', 'id'),
            array('incrementOrderId', 'ORDER ID', 'incrementOrderId', 'formatIncrementOrderId'),
            array('rowCreatedDate', 'CREATED', 'rowCreatedDate', 'formatRowCreatedDate'),
            array('paymentDate', 'PAYMENT DATE', null, 'formatPaymentDate'),
            array('transferredToAccounting', '<span title="Transferred to accounting">TFA</span>', null, 'formatTransferredToAccounting'),
            array('user', 'CUSTOMER', null, 'formatUser'),
            array('shipmentdate', 'REQUEST DATE', null, 'formatShipmentDate'),
            array('payment', 'PAYMENT', null, 'formatPaymentType'),
            array('inctaxes', 'INC TAXES', null, 'formatIncTaxes'),
            array('extaxes', 'EX TAXES', null, 'formatExTaxes'),
            array('state', 'OPTIONS', null, 'formatState')
        );
        
        $table = new \GetShopModuleTable($this, 'OrderManager', 'getOrdersFiltered', $args, $attributes);
        $table->avoidAutoExpanding();
        $table->sortByColumn("incrementOrderId", false);
        $table->renderPagedTable();
        ?>
        <script>
            $('.not_connected_to_room').closest('.datarow_inner').addClass('not_connected_to_rom_row');
        </script>
            
        <?
    }

    
    /**
     * 
     * @param \core_ordermanager_data_Order $order
     */
    public function formatShipmentDate($order) {
        $text = str_replace("\\", "\\\\", $order->payment->paymentType) . "()";
        if(!$order->payment->paymentType) {
            return;
        }
        $text = "\\" . $order->payment->paymentType;
        $instance = new $text();
        
        $text = "";
        if($order->avoidAutoSending) {
            $text = "Not being sent";
        } else {
            if($order->shippingDate) {
                $date = date("d.m.Y", strtotime($order->shippingDate));
                if($instance->hasPaymentLink()) {
                    $text = "<span title='Payment link will be sent at $date'>" . $date . "</span>";
                } else if($instance->hasAttachment()) {
                    $text = "<span title='Will be sent at $date'>" . $date . "</span>";
                } else {
                    $text = "N/A";
                }
            }
        }
        $sentDate = null;
        $addr = "";
        $sendLog = "";
        foreach($order->shipmentLog as $logEntry) {
            if($logEntry->date) {
                if(!$sentDate || $sentDate < strtotime($logEntry->date)) {
                    $sentDate = strtotime($logEntry->date);
                    $sendLog .= date("d.m.Y H:i", $sentDate) . " sent to " . $logEntry->address . "\n";
                    $addr = $logEntry->address;
                }
            }
        }
        if($sentDate) {
            $sentDate = "<i class 'fa fa-share'></i> " . date("d.m.Y", $sentDate);
        } else {
            $sentDate = "Not sent yet";
        }
        $roomid = "";
        if($this->externalReferenceIds) {
            $roomid = $this->externalReferenceIds[0];
        }
        
        $text .= "<div class='sentdate'><span title='$sendLog'>" . $sentDate."</span></div>";
        return $text;
    }

    public function loadPaymentLinkConfig() {
        $this->includefile("paymentlinkbox");
    }
    
    public function loadSendEmail() {
        $booking = $this->getApi()->getPmsManager()->getBookingWithOrderId($this->getSelectedMultilevelDomainName(), $this->getSelectedOrder()->id);
        $bookings = $this->getApi()->getPmsManager()->getBookingsFromGroupInvoicing($this->getSelectedMultilevelDomainName(), $this->getSelectedOrder()->id);
        
        if ($bookings && count($bookings) && !isset($_POST['data']['bookingid'])) {
            $this->includefile("selectbookingandroom");
        } else if (!$booking && !isset($_POST['data']['bookingid'])) {
            $this->includefile("emailsendboxwithoutbooking");
        } else {
            $this->includefile("emailsendbox");
        }
    }
    
    public function sendEmail() {
        $email = $_POST['data']['bookerEmail'];
        $bookingId = $_POST['data']['bookingid'];
        $orderid = $_POST['data']['orderid'];
        $msg = $_POST['data']['emailMessage'];
        $subject = $_POST['data']['subject'];
        $res = $this->getApi()->getPmsInvoiceManager()->sendRecieptOrInvoiceWithMessage($this->getSelectedMultilevelDomainName(), $orderid, $email, $bookingId, $msg, $subject);
    }
    
    public function sendByEhf() {
        $bookingId = $_POST['data']['bookingid'];
        $orderid = $_POST['data']['orderid'];
        $xml = $this->getApi()->getOrderManager()->getEhfXml($orderid);
        if ($xml == "failed") {
            echo "1. Something wrong happend while creating EHF invoice, GetShop has been notified and will contact you once its sorted out. ";
            return;
        }
        
        $res = $this->sendDocument($xml);
        if ($res[0] != "ok") {
            echo "2. Something went wrong during sending EHF, please contact GetShop Support.";
            return;
        }
        
        $this->getApi()->getOrderManager()->registerSentEhf($orderid);
        $this->getApi()->getOrderManager()->closeOrder($orderid, "Invoice sent by EHF to customer.");
    }
    
    public function sendPaymentLink() {
        $orderid = $_POST['data']['orderid'];
        $bookingid = $_POST['data']['bookingid'];
        $email = $_POST['data']['bookerEmail'];
        $prefix = $_POST['data']['bookerPrefix'];
        $phone = $_POST['data']['bookerPhone'];
        $msg = $_POST['data']['smsMessage'];
        $subject = $_POST['data']['subject'];

        echo "<div style='border: solid 1px; padding: 10px; margin-bottom: 10px;'>";
        echo "<i class='fa fa-info'></i> Paymentlink has been sent.";
        echo "<script>$('.informationbox-outer').scrollTop(0);</script>";
        echo "</div>";
        
        $this->getApi()->getPmsManager()->sendPaymentLinkWithText($this->getSelectedMultilevelDomainName(), $orderid, $bookingid, $email, $prefix, $phone, $msg, $subject);
    }
    
    
    /**
     * 
     * @return \core_ordermanager_data_Order 
     */
    public function getSelectedOrder() {
        $this->setData();
        return $this->selectedOrder;
    }

    public function setData($force=false) {
        if (!$this->selectedOrder || $force) {
            $this->selectedOrder = $this->getApi()->getOrderManager()->getOrder($_POST['data']['id']);
        }
    }
    
    public function setOrderIds($orderIds) {
        $this->orderIds = $orderIds;
    }

    public function setExternalReferenceIds($ids) {
        $this->externalReferenceIds = $ids;
    }

    public function setPaymentLinkCallBack($callback) {
        $this->paymentLinkCallback = $callback;
    }

    
    public function sendDocument($doc) {
        if (!$this->getApi()->getStoreManager()->isProductMode()) {
            return array("ok");
        }
        
        $url = "https://www.ehfportal.no/fsc.php";
        $user = 'getshop';
        $pass = 'dj28ycz2x';

        $ch = curl_init();

        $params = array(
                'a1' => $user,
                'a2' => $pass,
                'a3' => $doc
        );

        curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, false);
        curl_setopt($ch, CURLOPT_TIMEOUT, 500);
        curl_setopt($ch, CURLOPT_URL, $url);
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1);
        curl_setopt($ch, CURLOPT_POSTFIELDS, $params);
        curl_setopt($ch, CURLOPT_SSL_VERIFYHOST, 0);
        curl_setopt($ch, CURLOPT_FOLLOWLOCATION, 0);

        $response = curl_exec($ch);
        $httpCode = curl_getinfo($ch, CURLINFO_HTTP_CODE);
        $curlInfo = curl_getinfo($ch);
        if($httpCode == 200) {
                $status = 'ok';
        } else {
                $status = 'error';
        }
        curl_close($ch);
        return array($status, $response, $curlInfo);
    }
    
    /**
     * 
     * @param \core_ordermanager_data_Order $order
     * @param \core_usermanager_data_User $user
     * @return array
     */

    public function getEhfProblems($order, $user) {
        $ret = array();
  
        if (!$user->fullName) {
            $ret[] = "The name of the customer can not be blank";
        }
        
        if (!$user->address || !$user->address->address) {
            $ret[] = "Street address of the customer can not be blank";
        }
        
        if (!$user->address || !$user->address->city) {
            $ret[] = "City of the customer address can not be blank";
        }
        
        if (!$user->address || !$user->address->postCode) {
            $ret[] = "Postcode of the customer address can not be blank";
        }
        
        
        if (!$user->companyObject || !$user->companyObject->name) {
            $ret[] = "Customer company name can not be blank";
        }
        
        return $ret;
    }

    /**
     * 
     * @param \core_ordermanager_data_Order $order
     */
    public function getVerifoneTerminalPrinter($order) {
        $app = $this->getApi()->getStoreApplicationPool()->getApplication("6dfcf735-238f-44e1-9086-b2d9bb4fdff2");
        if (!$app)
            return;
        
        $text = "";
        if($order->status != 7 && !$order->closed) {
            $offset = 0;
            for ($offset=0; $offset<5; $offset++) {
                $ip = $app->settings->{"ipaddr$offset"}->value;
                if ($ip) {
                    $text .= "<i class='fa fa-credit-card dontExpand' gsclick='sendToVerifone' verifonid='$offset' orderid='".$order->id."' title='Send to verifone terminal $offset'></i>";
                }
            }
            
        }
        return $text;
    }
    
    public function sendToVerifone() {
        $this->getApi()->getVerifoneManager()->chargeOrder($_POST['data']['orderid'], $_POST['data']['verifonid'], false);
    }
    
    public function getCashPointPrinters($order) {
        
        if(!isset($order->paymentDate) || !$order->paymentDate) {
            return "";
        }
        
        if($order->status != 7) {
            return "";
        }
        
        $missingPaymentTypeA = !$order->payment || !$order->payment->paymentType;
        $missingPaymentTypeB = isset($order->paymentType) && !$order->paymentType;
        
        if($missingPaymentTypeA && $missingPaymentTypeB) {
            return "";
        }
        
        $cashpoints = $this->getApi()->getGdsManager()->getDevices();
        
        if (!$cashpoints) {
            return "";
        }
        
        $text = "";
        
        $inValidPaymentMethods = array();
        $inValidPaymentMethods[] = 'ns_70ace3f0_3981_11e3_aa6e_0800200c9a66\InvoicePayment';
        $inValidPaymentMethods[] = 'ns_cbe3bb0f_e54d_4896_8c70_e08a0d6e55ba\SamleFaktura';
        
        $paymentType = $missingPaymentTypeB ? $order->payment->paymentType : $order->paymentType;
        if (in_array($paymentType, $inValidPaymentMethods)) {
            return ""; 
        }
        
        foreach ($cashpoints as $cashpoint) {
            if ($cashpoint->type == "cashap") {
                $orderId = isset($order->id) ? $order->id : $order->orderId;
                $text .= "<i class='fa fa-print dontExpand' gsclick='sendReceipt' deviceid='$cashpoint->id' orderid='".$orderId."' title='Print receipt on $cashpoint->name'></i>";
            }
        }
        
        return $text; 
        
    }

    public function handleGetShopModulePaging() {
        $table = new \GetShopModuleTable($this, 'OrderManager', 'getOrdersFiltered', $args, $attributes);
        $table->setCurrentPageNumber($_POST['data']['newpagenumber']);
    }
}
?>
