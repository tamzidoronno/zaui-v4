<?php
namespace ns_11234b3f_452e_42ce_ab52_88426fc48f8d;

class SalesPointTabPayment extends \ns_57db782b_5fe7_478f_956a_ab9eb3575855\SalesPointCommon implements \Application {
    private $tab = null;
    private $currentOrder = null;
    
    public function getDescription() {
        
    }

    public function getName() {
        return "SalesPointTabPayment";
    }

    public function render() {
        if ($this->preRender()) {
            return;
        }
        
        // Pay on room
        if (isset($_SESSION['ns_11234b3f_452e_42ce_ab52_88426fc48f8d_complete_payonroom'])) {
            $this->includefile("ns_f86e7042_f511_4b9b_bf0d_5545525f42de");
            return;
        }
        
        if (isset($_SESSION['ns_11234b3f_452e_42ce_ab52_88426fc48f8d_complete_payment'])) {
            $order = $this->getCurrentOrder(); 
            $paymentType = $order->payment->paymentType;
            $paymentTypeArr = explode('\\', $paymentType);
            $paymentId = $paymentTypeArr[0];
            $this->includefile($paymentId);
        } else {
            $this->includefile("taboveriew");
        }
    }

    /**
     * 
     * @return \core_pos_PosTab
     */
    public function getCurrentTab() {
        if ($this->tab) {
            return $this->tab;
        }
        
        $tabId = $this->getModalVariable("tabid");
        
        if (!$tabId) {
            return null;
        }
        
        $this->tab = $this->getApi()->getPosManager()->getTab($tabId);
        return $this->tab;
    }
    
    public function getTotalForItems() {
        $itemsToCheck = $this->getPostedCartItems();
        echo $this->getApi()->getPosManager()->getTotalForItems($itemsToCheck);
        die();
    }

    public function getItemFromTab($newItemId) {
        $tab = $this->getCurrentTab();
        foreach ($tab->cartItems as $item) {
            if ($item->cartItemId == $newItemId) {
                return $item;
            }
        }
        
        return null;
    }

    public function getPostedCartItems() {
        $tab = $this->getCurrentTab();
        $itemsToCheck = array();
        
        foreach ($_POST['data']['items'] as $newItem) {
            if ($newItem['active'] == "true") {
                $cartItemFromTab = $this->getItemFromTab($newItem['cartitemid']);
                $cartItemFromTab->product->price = $newItem['price'];
                $cartItemFromTab->count = $newItem['count'];
                $cartItemFromTab->overridePriceIncTaxes = null;
                $itemsToCheck[] = $cartItemFromTab;
            }
        }
        
        return $itemsToCheck;
    }
    
    public function startPayment() {
        $tab = $this->getCurrentTab();
        $cartItems = $this->getPostedCartItems();
        
        if ($_POST['data']['payementId'] == "f86e7042-f511-4b9b-bf0d-5545525f42de") {
            $_SESSION['ns_11234b3f_452e_42ce_ab52_88426fc48f8d_complete_payonroom'] = json_encode($cartItems);
        } else {
            $order = $this->getApi()->getPosManager()->createOrder($cartItems, $_POST['data']['payementId'], $tab->id);
            $_SESSION['ns_11234b3f_452e_42ce_ab52_88426fc48f8d_complete_payment'] = $order->id;
        }
    }
    
    public function getCartItemsForRoom() {
        if (!isset($_SESSION['ns_11234b3f_452e_42ce_ab52_88426fc48f8d_complete_payonroom']))
            return array();
        
        return json_decode($_SESSION['ns_11234b3f_452e_42ce_ab52_88426fc48f8d_complete_payonroom']);
    }

    /**
     * 
     * @return \core_ordermanager_data_Order
     */
    public function getCurrentOrder() {
        if ($this->currentOrder == null) {
            $this->currentOrder = $this->getApi()->getOrderManager()->getOrder($_SESSION['ns_11234b3f_452e_42ce_ab52_88426fc48f8d_complete_payment']);
        }
        
        return $this->currentOrder;
    }

    public static function usortRoomsByRoomName($roomA, $roomB) {
        return strcmp($roomA->bookingItemName, $roomB->bookingItemName);
    }
    
    public function cancelCurrentOrder() {
        unset($_SESSION['ns_11234b3f_452e_42ce_ab52_88426fc48f8d_invoice_userid']);
        
        if (isset($_SESSION['ns_11234b3f_452e_42ce_ab52_88426fc48f8d_complete_payment'])) {
            $order = $this->getCurrentOrder();
            $this->getApi()->getOrderManager()->deleteOrder($order->id);
            unset($_SESSION['ns_11234b3f_452e_42ce_ab52_88426fc48f8d_complete_payment']);    
        }
        
        if (isset($_SESSION['ns_11234b3f_452e_42ce_ab52_88426fc48f8d_complete_payonroom'])) {
            unset($_SESSION['ns_11234b3f_452e_42ce_ab52_88426fc48f8d_complete_payonroom']);
        }
    }
    
    public function completeCurrentOrder() {
        $receiptPrinterId = $this->getSelectedReceiptPrinter();
        $kitchenPrinterId = $this->getSelectedKitchenPrinter();
        
        $tab = $this->getCurrentTab();
        
        $metaData = isset($_POST['data']['gsextradatafromprecheck']) ? $_POST['data']['gsextradatafromprecheck'] : array();
        
        $this->getApi()->getPosManager()->completeTransaction($tab->id, $this->getCurrentOrder()->id, $receiptPrinterId, $kitchenPrinterId, $metaData);
        unset($_SESSION['ns_11234b3f_452e_42ce_ab52_88426fc48f8d_complete_payment']);
        
        $totalForTab = $this->getApi()->getPosManager()->getTotal($tab->id);
        if (!$totalForTab) {
            $salesPointNew = new \ns_57db782b_5fe7_478f_956a_ab9eb3575855\SalesPointNewSale();
            $salesPointNew->deleteCurrentTab();
            $this->closeModal();
        }
    }

    public function startVerifonePaymentProcess() {
        $deviceId = $this->getCurrentTerminalId();
        $_SESSION['ns_11234b3f_452e_42ce_ab52_88426fc48f8d_complete_payment_state'] = "in_progress";
        $this->getApi()->getVerifoneManager()->chargeOrder($this->getCurrentOrder()->id, $deviceId, false);
    }

    public function getLastTerminalMessage() {
        $messages = $this->getApi()->getVerifoneManager()->getTerminalMessages();
        $this->getApi()->getVerifoneManager()->clearMessages();
        
        if ($messages && count($messages)) {
            $_SESSION['ns_11234b3f_452e_42ce_ab52_88426fc48f8d_last_terminal_message'] = end($messages);
        }
        
        $this->updateState();
        
        return $_SESSION['ns_11234b3f_452e_42ce_ab52_88426fc48f8d_last_terminal_message'];
    }
    
    public function getPaymentProcessMessage() {
        $this->includefile("verifonepaymentprocess");
        die();
    }
    
    public function updateState() {
        if ($_SESSION['ns_11234b3f_452e_42ce_ab52_88426fc48f8d_last_terminal_message'] == "completed") {
            $_SESSION['ns_11234b3f_452e_42ce_ab52_88426fc48f8d_complete_payment_state'] = "completed";
        }
        
        if ($_SESSION['ns_11234b3f_452e_42ce_ab52_88426fc48f8d_last_terminal_message'] == "payment failed") {
            $_SESSION['ns_11234b3f_452e_42ce_ab52_88426fc48f8d_complete_payment_state'] = "failed";
        }
    }

    public function getCurrentTerminalId() {
        $devices = $this->getActiveVerifoneDevices();
        $deviceId = str_replace("ipaddr", "", $devices[0]->name);
        return $deviceId;
    }
    
    public function cancelVerifonePayment() {
        $deviceId = $this->getCurrentTerminalId();
        $this->getApi()->getVerifoneManager()->cancelPaymentProcess($deviceId);
        die();
    }

    public function getActiveVerifoneDevices() {
        $verifoneApp = $this->getApi()->getStoreApplicationPool()->getApplication('6dfcf735-238f-44e1-9086-b2d9bb4fdff2');

        $activeDevices = array();
        foreach ($verifoneApp->settings as $key => $setting) {
            if (strstr($key, "ipaddr") && $setting->value) {
                $activeDevices[] = $setting;
            }
        }
        
        return $activeDevices;
    }

    public function getItemName($items, $id) {
        foreach ($items as $item) {
            if ($item->id == $id) {
                return $item->bookingItemName;
            }
        }
        
        return null;
    }
    
    public function addCurrentItemsToRoom() {
        $bookingEngineName = $this->getBookingEngineNameByPayOnRoomPaymentMethod();
        
        $cartItems = $this->getCartItemsForRoom();
        foreach ($cartItems as $item) {
            $this->getApi()->getPmsManager()->addCartItemToRoom($bookingEngineName, $item, $_POST['data']['roomid'], "SalesPoint");
        }
        
        $booking = $this->getApi()->getPmsManager()->getBookingFromRoom($bookingEngineName,  $_POST['data']['roomid']);
        $guestName = "";
        $roomName = "";
        foreach ($booking->rooms as $room) {
            if ($room->bookingItemId) {
                $bookingItem = $this->getApi()->getBookingEngine()->getBookingItem($bookingEngineName, $room->bookingItemId);
                $roomName = $bookingItem->bookingItemName;
            }
            
            $guestName = @$room->guests[0]->name;
        }
        
        $gdsDeviceId = $this->getSelectedReceiptPrinter();
        
        $this->getApi()->getPosManager()->printRoomReceipt($gdsDeviceId, $roomName, $guestName, $cartItems);
        $this->getApi()->getPosManager()->removeItemsFromTab($this->getCurrentTab()->id, $cartItems);
        $this->cancelCurrentOrder();
    }

    public function getBookingEngineNameByPayOnRoomPaymentMethod() {
        $paymentMethod = $this->getApi()->getStoreApplicationPool()->getApplication("f86e7042-f511-4b9b-bf0d-5545525f42de");

        $bookingEngineName = false;
        $bookingEngineName = @$paymentMethod->settings->bookingengine->value;
        
        return $bookingEngineName;
    }
    
    public function changeUser() {
        $_SESSION['ns_11234b3f_452e_42ce_ab52_88426fc48f8d_invoice_userid'] = $_POST['data']['userid'];
    }
    
    public function createNewUser() {
        $user = new \core_usermanager_data_User();
        $user->fullName = $_POST['data']['name'];
        $user = $this->getApi()->getUserManager()->createUser($user);
        $_SESSION['ns_11234b3f_452e_42ce_ab52_88426fc48f8d_invoice_userid'] = $user->id;
        return $user;
    }
    
    public function createCompany() {
        $company = new \core_usermanager_data_Company();
        
        $company->name = $_POST['data']['companyname'];
        $company->vatNumber = $_POST['data']['vatnumber'];
        
        $company->address = new \core_usermanager_data_Address();
        
        $user = $this->getApi()->getUserManager()->createUserAndCompany($company);
        
        $_SESSION['ns_11234b3f_452e_42ce_ab52_88426fc48f8d_invoice_userid'] = $user->id;
        
        return $user;
    }
    
    public function saveTabForInvoicing() {
        $order = $this->getCurrentOrder();
        $order->userId = $_SESSION['ns_11234b3f_452e_42ce_ab52_88426fc48f8d_invoice_userid'];
        $this->getApi()->getOrderManager()->saveOrder($order);
        $this->completeCurrentOrder();
        unset($_SESSION['ns_11234b3f_452e_42ce_ab52_88426fc48f8d_invoice_userid']);
    }

    public function getNameOfPaymentMethod($paymentAppId) {
        $app = $this->getApi()->getStoreApplicationPool()->getApplication($paymentAppId);
        $instance = $this->getFactory()->getApplicationPool()->createInstace($app);
        return $instance->getName();
    }

    public function getRemainingGiftCard() {
        $giftCard = $this->getApi()->getGiftCardManager()->getGiftCard($_POST['data']['code']);
        
        if (!$giftCard) {
            echo "0";
            die();
        }
        
        echo $giftCard->remainingValue;
        die();
    }
}
?>