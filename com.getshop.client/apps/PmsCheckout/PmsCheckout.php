<?php

namespace ns_2e51d163_8ed2_4c9a_a420_02c47b1f7d67;

class PmsCheckout extends \WebshopApplication implements \Application {

    private $currentCart = null;
    private $readOnly = false;

    public function getDescription() {
        
    }

    public function getName() {
        return "PmsCheckout";
    }

    public function loadCartFilter() {
        $this->includefile("cartfilter");
    }

    public function render() {
        echo "<div class='checkoutarea'>";
        $this->includefile("checkout");
        echo "</div>";
    }

    public function renderPreview() {
        $this->includefile("preview");
    }

    public function setCurrentItem($item) {
        $this->currentItem = $item;
    }

    public static function sortByDate($a, $b) {
        $ad = strtotime($a->date);
        $bd = strtotime($b->date);

        if ($ad == $bd) {
            return 0;
        }

        return $ad < $bd ? -1 : 1;
    }

    /**
     * @return \core_cartmanager_data_CartItem;
     */
    public function getCurrentItem() {
        return $this->currentItem;
    }

    public function getTotalInCart() {
        echo $this->getApi()->getCartManager()->getCartTotalAmount();
    }

    public function updateItem() {
        $cart = $this->getApi()->getCartManager()->getCart();
        foreach ($cart->items as $item) {
            if ($item->cartItemId == $_POST['data']['itemid']) {
                if (isset($_POST['data']['matrixPrice'])) {
                    if($_POST['data']['checked'] == "false") {
                        unset($item->priceMatrix->{$_POST['data']['matrixDate']});
                    } else {
                        $item->priceMatrix->{$_POST['data']['matrixDate']} = $_POST['data']['matrixPrice'];
                    }
                }
                $newAddonsMatrix = array();
                if (isset($_POST['data']['addonid'])) {
                    foreach ((array) $item->itemsAdded as $addonItem) {
                        if ($addonItem->addonId == $_POST['data']['addonid']) {
                            if($_POST['data']['checked'] == "false") {
                                continue;
                            }
                            
                            $addonItem->count = $_POST['data']['addonCount'];
                            $addonItem->price = $_POST['data']['addonPrice'];
                            $newAddonsMatrix[] = $addonItem;
                        }
                    }
                }
                $item->itemsAdded = $newAddonsMatrix;

                $total = 0;
                $count = 0;

                $startDate = null;
                $endDate = null;
                foreach ((array) $item->priceMatrix as $date => $val) {
                    $startDate = $this->checkIfStartDate($date, $startDate);
                    $endDate = $this->checkIfEndDate($date, $endDate);
                    $count++;
                    $total += $val;
                }
                foreach ((array) $item->itemsAdded as $addonItem) {
                    $startDate = $this->checkIfStartDate($addonItem->date, $startDate);
                    $endDate = $this->checkIfEndDate($addonItem->date, $endDate);
                    
                    $count += $addonItem->count;
                    $total += ($addonItem->count * $addonItem->price);
                    
                }
                $item->count = $count;
                if($count > 0) {
                    $item->product->price = ($total / $count);
                } else {
                    $item->product->price = 0;
                }
                
                $item->startDate = $this->convertToJavaDate(strtotime($startDate));
                if(sizeof((array)$item->priceMatrix) > 0) {
                    $item->endDate = $this->convertToJavaDate(strtotime($endDate)+86400);
                } else {
                    $item->endDate = $this->convertToJavaDate(strtotime($endDate));
                }
                
                if(!$endDate) { $item->endDate = null; }
                if(!$startDate) { $item->startDate = null; }
                
                $this->getApi()->getCartManager()->updateCartItem($item);
            }
        }
    }

    public function setCurrentCart($cart) {
        $this->currentCart = $cart;
    }

    public function setOriginalCart($cart = false) {
        $this->originalCart = $this->getApi()->getCartManager()->getCart();
    }

    public function filterCartByDate() {
        $id = $this->getModalVariable("orderUnderConstrcutionId");
        $start = $this->convertToJavaDate(strtotime($_POST['data']['start']));
        $end = $this->convertToJavaDate(strtotime($_POST['data']['end']));
        $this->getApi()->getCartManager()->filterByDate($start, $end);
        $this->includefile("checkout");
    }

    public function removeItem() {
        $this->getApi()->getCartManager()->removeCartItem($_POST['data']['id']);
    }

    public function createOrder() {
        $payment = $_POST['data']['payment'];
        $cardId = "";
        if(stristr($payment, "savedcard_")) {
            $cardId = str_replace("savedcard_", "", $payment);
            $card = $this->getApi()->getUserManager()->getCard($cardId);
            if(strtolower($card->savedByVendor) == "stripe") { $payment = "3d02e22a-b0ae-4173-ab92-892a94b457ae"; };
            if(strtolower($card->savedByVendor) == "dibs") { $payment = "d02f8b7a-7395-455d-b754-888d7d701db8"; };
            if(strtolower($card->savedByVendor) == "epay") { $payment = "8f5d04ca-11d1-4b9d-9642-85aebf774fee"; };
        }
        
        $orderIds = array();
        if (isset($_POST['data']['appendtoorder']) && $_POST['data']['appendtoorder']) {
            $originalCart = $this->getOriginalCartFromSession();
            $order = $this->getApi()->getOrderManager()->getOrder($_POST['data']['appendtoorder']);
            foreach ($originalCart->items as $item) {
                $order->cart->items[] = $item;
            }
            $this->getApi()->getOrderManager()->saveOrder($order);
            $orderIds[] = $order->id;
        } else {
            $id = $this->getModalVariable("orderUnderConstrcutionId");
            $type = "all";
            if (isset($_POST['data']['paymenttypeselection'])) {
                $type = $_POST['data']['paymenttypeselection'];
            }
            $overrideDate = isset($_POST['data']['overridedate']) ? $this->convertToJavaDate(strtotime($_POST['data']['overridedate'])) : null;
            $orderIds = $this->getApi()->getPmsInvoiceManager()->convertCartToOrders($this->getSelectedMultilevelDomainName(), $id, null, $payment, $type, $overrideDate);
            
            if ($type == "merged") {
                //Do something here.
                $mainorder = $this->getApi()->getOrderManager()->mergeAndCreateNewOrder($_POST['data']['userid'], $orderIds, $payment, "");
                $orderIds[] = $mainorder->id;
            }
            
            foreach ($orderIds as $orderId) {
                $this->getApi()->getPmsManager()->orderCreated($this->getSelectedMultilevelDomainName(), $orderId);
                $order = $this->getApi()->getOrderManager()->getOrder($orderId);
                if ($type == "uniqueorder" || $type == "uniqueordersendpaymentlink") {
                    $room = $this->getRoom($orderId, $order);
                    foreach ($room->guests as $guest) {
                        if ($type == "uniqueordersendpaymentlink" && ($guest->phone || $guest->email)) {
                            $this->getApi()->getPmsManager()->sendPaymentLink($this->getSelectedMultilevelDomainName(), $orderId, $room->pmsBookingRoomId, $guest->email, $guest->prefix, $guest->phone);
                        }
                    }
                }
            }
            
            if($cardId) {
                foreach($orderIds as $orderId) {
                    $this->getApi()->getOrderManager()->payWithCard($orderId, $cardId);
                }
            }
        }
        $this->printCreatedOrders($orderIds);
    }

    /**
     * 
     * @return \core_cartmanager_data_Cart
     */
    public function getOriginalCartFromSession() {
        if (!isset($this->originalCart)) {
            $this->setOriginalCart();
        }
        return $this->originalCart;
    }

    public function getCartItemFromCart($cart, $id) {
        foreach ($cart->items as $cartItem) {
            if ($cartItem->cartItemId === $id) {
                return $cartItem;
            }
        }

        return null;
    }

    public function createNewPriceMatrix($cartItemToCheck) {
        $retMatrix = new \stdClass();
        $postedCartItem = $this->getPostedCartItem($cartItemToCheck->cartItemId);

        foreach ($postedCartItem['priceMatrix'] as $priceMatrix) {
            if ($priceMatrix['enabled'] === "true") {
                $retMatrix->{$priceMatrix['date']} = $priceMatrix['value'];
            } else {
                $retMatrix->{$priceMatrix['date']} = 0;
            }
        }

        return $retMatrix;
    }

    public function createAddons($originalCartItem) {
        $postedItem = $this->getPostedCartItem($originalCartItem->cartItemId);
        $postedAddons = $postedItem['addons'];
        $items = $originalCartItem->itemsAdded;
        $retAddons = array();

        foreach ($postedAddons as $postedAddon) {


            $addonItem = null;

            foreach ($items as $itemAdded) {
                if ($itemAdded->addonId == $postedAddon['id']) {
                    $addonItem = $itemAdded;
                }
            }

            if (!$addonItem) {
                continue;
            }

            $addonItem->price = $postedAddon['price'];
            $addonItem->count = $postedAddon['count'];

            if ($postedAddon['enabled'] !== "true") {
                $addonItem->price = 0;
            }

            $retAddons[] = $addonItem;
        }

        return $retAddons;
    }

    public function addCurrentCartForPaymentProcess($pmsRoomId) {
        if (isset($_SESSION['payment_process_cart__tmp'])) {
            $_SESSION['payment_process_cart_' . $pmsRoomId] = $_SESSION['payment_process_cart__tmp'];
            unset($_SESSION['payment_process_cart__tmp']);
        } else if (isset($_SESSION['payment_process_cart_' . $pmsRoomId . "_tmp"])) {
            $_SESSION['payment_process_cart_' . $pmsRoomId] = $_SESSION['payment_process_cart_' . $pmsRoomId . "_tmp"];
            unset($_SESSION['payment_process_cart_' . $pmsRoomId . "_tmp"]);
        } else {
            $cart = $this->getApi()->getCartManager()->getCart();
            $_SESSION['payment_process_cart_' . $pmsRoomId] = json_encode($cart);
        }
    }

    public function cancelPaymentProcessForRoom($roomId) {
        unset($_SESSION['payment_process_cart_' . $roomId]);
    }

    public function cancelAllPaymentProcesses() {
        foreach ($_SESSION as $key => $val) {
            if (strstr($key, "payment_process_cart") > -1) {
                unset($_SESSION[$key]);
            }
        }
    }

    public function getCurrentCart() {
        if (isset($this->currentCart) && $this->currentCart) {
            return $this->currentCart;
        }

        return $this->getApi()->getCartManager()->getCart();
    }

    public function setRoomCart($pmsRoomId) {
        if (isset($_SESSION['payment_process_cart_' . $pmsRoomId])) {
            $cart = json_decode($_SESSION['payment_process_cart_' . $pmsRoomId]);
            $this->setOriginalCart($cart);
            $this->setCurrentCart($cart);
            $this->getApi()->getCartManager()->setCart($cart);
        }
    }

    public function getPostedCartItem($id) {
        $items = $_POST['data']['cartItems'];
        foreach ($items as $item) {

            if ($item['id'] == $id) {
                return $item;
            }
        }

        return null;
    }

    public function saveTmpCart($cart, $roomId) {
        $key = 'payment_process_cart_' . $roomId . "_tmp";
        $_SESSION[$key] = json_encode($cart);
    }

    public function getTmpCartForRoom($roomId) {
        $key = 'payment_process_cart_' . $roomId . "_tmp";

        if (isset($_SESSION[$key])) {
            $cart = json_decode($_SESSION[$key]);
            unset($_SESSION[$key]);
            return $cart;
        }

        return null;
    }

    public function setReadOnly() {
        $this->readOnly = true;
    }

    public function isReadOnly() {
        return $this->readOnly;
    }

    /**
     * 
     * @param type $orderId
     * @param type $order
     * @return \core_pmsmanager_PmsBookingRooms
     */
    public function getRoom($orderId, $order) {
        $roomId = "";
        foreach ($order->cart->items as $item) {
            $roomId = $item->product->externalReferenceId;
            break;
        }
        $booking = $this->getApi()->getPmsManager()->getBookingFromRoom($this->getSelectedMultilevelDomainName(), $roomId);
        foreach ($booking->rooms as $room) {
            if ($room->pmsBookingRoomId == $roomId) {
                return $room;
            }
        }
        return;
    }

    public function isGroupPayment() {
        $group = $this->getBookingGroup();
        return count($group) > 1;
    }

    public function processGroupPayment() {
        $group = $this->getBookingGroup();
    }

    public function getBookingGroup() {
        $cart = $this->getApi()->getCartManager()->getCart();
        $group = array();
        foreach($cart->items as $item) {
            $group[$item->pmsBookingId] = 1;
        }
        return array_keys($group);
    }

    public function printCreatedOrders($orderIds) {
        echo "<h1 style='text-align:center; border-bottom: solid 1px; margin:0px;'>Orders created</h1>";
        $orderlist = new \ns_9a6ea395_8dc9_4f27_99c5_87ccc6b5793d\EcommerceOrderList();
        $ids = array();
        $orderlist->setOrderIds($orderIds);
        $orderlist->setExternalReferenceIds($ids);
        $orderlist->renderApplication(true, $this);
    }

    public function checkIfEndDate($date, $curDate) {
        if(!$curDate || strtotime($curDate) < strtotime($date)) {
            return $date;
        }
        return $curDate;
    }

    public function checkIfStartDate($date, $curDate) {
        if(!$curDate || strtotime($curDate) > strtotime($date)) {
            return $date;
        }
        return $curDate;
    }

}

?>
