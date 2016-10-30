<?php
namespace ns_9de81608_5cec_462d_898c_1266d1749320;

class PmsConfiguration extends \WebshopApplication implements \Application {
    public function getDescription() {
        return "Configure your booking engine to fit your needs";
    }

    public function getName() {
        return "PmsConfiguration";
    }

    public function sendStatistics() {
        $this->getApi()->getPmsManager()->sendStatistics($this->getSelectedName());
    }
    
    public function togglePaymentOnChannel() {
        $config = $this->getApi()->getPmsManager()->getConfiguration($this->getSelectedName());
        foreach($config->channelConfiguration as $key => $chanConfig) {
            if($key != $_POST['data']['id']) {
                continue;
            }
            $config->channelConfiguration->{$key}->ignoreUnpaidForAccess = !$config->channelConfiguration->{$key}->ignoreUnpaidForAccess;
        }
        $this->getApi()->getPmsManager()->saveConfiguration($this->getSelectedName(), $config);
    }
    
    public function createProduct() {
        $product = $this->getApi()->getProductManager()->createProduct();
        $product->name = $_POST['data']['name'];
        $this->getApi()->getProductManager()->saveProduct($product);
    }
    
    public function toggleVisibleInBooking() {
        $config = $this->getApi()->getPmsManager()->getConfiguration($this->getSelectedName());
        foreach($config->channelConfiguration as $key => $chanConfig) {
            if($key != $_POST['data']['id']) {
                continue;
            }
            $config->channelConfiguration->{$key}->displayOnBookingProcess = !$config->channelConfiguration->{$key}->displayOnBookingProcess;
        }
        $this->getApi()->getPmsManager()->saveConfiguration($this->getSelectedName(), $config);
    }
    
    public function render() {
        if(!$this->getSelectedName()) {
            echo "Please specify a booking engine first";
            return;
        }
        
        $this->includefile("notificationpanel");
    }
    
    public function addNewChannel() {
        $channel = $_POST['data']['name'];
        $this->getApi()->getPmsManager()->createChannel($this->getSelectedName(), $channel);
    }
    
    public function deleteAllBookings() {
        $code = $_POST['data']['code'];
        $this->getApi()->getPmsManager()->deleteAllBookings($this->getSelectedName(), $code);
    }
    
    
    public function saveContent() {
        $config = $this->getApi()->getPmsManager()->getConfiguration($this->getSelectedName());
        if($_POST['data']['fromid'] == "fireinstructions") {
            $config->fireinstructions = $_POST['data']['content'];
        } else if($_POST['data']['fromid'] == "otherinstructionsfiled") {
            $config->otherinstructions = $_POST['data']['content'];
        } else {
            $config->contracts->{$this->getFactory()->getCurrentLanguage()} = $_POST['data']['content'];
        }
        $this->getApi()->getPmsManager()->saveConfiguration($this->getSelectedName(), $config);
    }
    
    public function removeChannel() {
        $this->getApi()->getPmsManager()->removeChannel($this->getSelectedName(), $_POST['data']['channel']);
    }
    
    public function startsWith($haystack, $needle) {
        return $needle === "" || strrpos($haystack, $needle, -strlen($haystack)) !== FALSE;
    }

    public function getSelectedName() {
        return $this->getConfigurationSetting("engine_name");
    }
    
    public function saveSettings() {
        foreach($_POST['data'] as $key => $value) {
            $this->setConfigurationSetting($key, $value);
        }
    }
    
    public function saveNotifications() {
        $notifications = $this->getApi()->getPmsManager()->getConfiguration($this->getSelectedName());
        foreach($_POST['data'] as $key => $value) {
            if($this->endsWith($key, "_email")) {
                $key = substr($key, 0, strlen($key)-6);
                $notifications->emails->{$key} = $value;
            }
        }
        foreach($_POST['data'] as $key => $value) {
            if($this->endsWith($key, "_title")) {
                $key = substr($key, 0, strlen($key)-6);
                $notifications->emailTitles->{$key} = $value;
            }
        }
        foreach($_POST['data'] as $key => $value) {
            if($this->endsWith($key, "_sms")) {
                $key = substr($key, 0, strlen($key)-4);
                $notifications->smses->{$key} = $value;
            }
        }
        foreach($_POST['data'] as $key => $value) {
            if($this->endsWith($key, "_admin")) {
                $key = substr($key, 0, strlen($key)-6);
                $notifications->adminmessages->{$key} = $value;
            }
        }
        
        $emailsToNotify = array();
        foreach($_POST['data'] as $key => $value) {
            if(!$value) {
                continue;
            }
            if(stristr($key, "emailtonotify_")) {
                $indexed = explode("_", $key);
                $type = $indexed[1];
                if(!isset($emailsToNotify[$type])) {
                    $emailsToNotify[$type] = array();
                }
                $emailsToNotify[$type][] = $value;
            }
        }
        $notifications->emailsToNotify = $emailsToNotify;
        
        foreach($_POST['data'] as $key => $value) {
            if($key == "otherinstructions") {
                continue;
            }
            if($key == "fireinstructions") {
                continue;
            }
            if(property_exists($notifications, $key)) {
                $notifications->{$key} = $value;
            }
        }
        for($i = 1; $i<=7;$i++) {
            if(isset( $_POST['data']['cleaningDays_'.$i])) {
                $notifications->cleaningDays->{$i} = $_POST['data']['cleaningDays_'.$i];
            }
        }
        
        for($i = 1; $i <= 12; $i++) {
            if(!isset($notifications->budget->{$i})) {
                $notifications->budget->{$i} = new \core_pmsmanager_PmsBudget();
            }
            $notifications->budget->{$i}->month = $i;
            $notifications->budget->{$i}->coverage_percentage = $_POST['data']['budget_percentage_'.$i];
            $notifications->budget->{$i}->budget_amount = $_POST['data']['budget_amount_'.$i];
        }
        
        $translationMatrix = array();
        foreach($_POST['data'] as $key => $val) {
            if(stristr($key, "channel_translation_")) {
                $channel = str_replace("channel_translation_", "", $key);
                if(!isset($notifications->channelConfiguration->{$channel})) {
                    $notifications->channelConfiguration->{$channel} = new \core_pmsmanager_PmsChannelConfig();
                }
                $notifications->channelConfiguration->{$channel}->humanReadableText = $val;
            }
        }
        
        $channelPaymentTypes = array();
        foreach($_POST['data'] as $key => $val) {
            if(stristr($key, "channel_payment_")) {
                $channel = str_replace("channel_payment_", "", $key);
                $notifications->channelConfiguration->{$channel}->preferredPaymentType = $val;
            }
        }
        
        $notifications->addonConfiguration = $this->buildAddonConfigs();
        $notifications->cleaningPriceConfig = $this->buildCleaningPriceConfig();
        $notifications->extraCleaningCost = $this->buildExtraCleaningCost();
        
        $notifications->defaultMessage->{$this->getFactory()->getCurrentLanguage()} = $_POST['data']['defaultmessage'];
        
        //Save addonsfromproduct
        $prods = $this->getApi()->getProductManager()->getAllProducts();
        foreach($prods as $prod) {
            $conf = new \core_pmsmanager_PmsBookingAddonItem();
            $found = false;
            foreach($notifications->addonConfiguration as $tmpaddon) {
                if($tmpaddon->productId == $prod->id) {
                    $conf = $tmpaddon;
                    $found = true;
                }
            }
            
            if(!$found) {
                $notifications->addonConfiguration[] = $conf;
            }
            
            $conf->productId = $prod->id;
            $conf->isAvailableForCleaner = $_POST['data']['addonconfig_' . $prod->id . "_isAvailableForCleaner"];
            $conf->isAvailableForBooking = $_POST['data']['addonconfig_' . $prod->id . "_isAvailableForBooking"];
            $conf->isSingle = $_POST['data']['addonconfig_' . $prod->id . "_isSingle"];
            $conf->description = $_POST['data']['addonconfig_' . $prod->id . "_description"];
            $conf->price = $_POST['data']['addonconfig_' . $prod->id . "_price"];
            
            $prod->sku = $_POST['data']['addonconfig_' . $prod->id . "_taxcode"];
            $prod->accountingAccount = $_POST['data']['addonconfig_' . $prod->id . "_account"];
            $prod->name = $_POST['data']['addonconfig_' . $prod->id . "_name"];
            $prod->taxgroup = $_POST['data']['addonconfig_' . $prod->id . "_taxgroup"];
            $this->getApi()->getProductManager()->saveProduct($prod);
        }
        
        
        $this->getApi()->getPmsManager()->saveConfiguration($this->getSelectedName(), $notifications);
        
        //Save coupons.
        foreach($_POST['data'] as $key => $val) {
            if(stristr($key, "coupon_")) {
                $couponid = str_replace("coupon_", "", $key);
                $coupon = $this->getApi()->getCartManager()->getCouponById($couponid);
                $coupon->channel = $val;
                $this->getApi()->getCartManager()->addCoupon($coupon);
            }
        }
    }
    
    function endsWith($haystack, $needle) {
    // search forward starting from end minus needle length characters
        return $needle === "" || (($temp = strlen($haystack) - strlen($needle)) >= 0 && strpos($haystack, $needle, $temp) !== FALSE);
    }
    
    public function markAllKeyDelivered() {
        $this->getApi()->getPmsManager()->markKeyDeliveredForAllEndedRooms($this->getSelectedName());
    }
    
    public function showSettings() {
        $this->includefile("settings");
    }

    public function buildAddonConfigs() {
        $allAddons = array();
        for($i = 1; $i <=  7; $i++) {
            $addon = new \core_pmsmanager_PmsBookingAddonItem();
            $addon->addonType = $i;
            $addon->isActive = $_POST['data']['addon_active_'.$i];
            $addon->isSingle = $_POST['data']['addon_single_'.$i];
            $addon->productId = $_POST['data']['addon_productid_'.$i];
            $allAddons[$i] = $addon;
        }
        return $allAddons;
    }

    public function buildCleaningPriceConfig() {
        $types = $this->getApi()->getBookingEngine()->getBookingItemTypes($this->getSelectedName());
        $res = array();
        foreach($types as $type) {
            $stats = new \core_pmsmanager_CleaningStatistics();
            for($i = 1; $i <= 7; $i++) {
                $stats->cleanings[$i] = $_POST['data']['cleaningcost_' . $type->id . "_" . $i];
                if(!$stats->cleanings[$i]) {
                    $stats->cleanings[$i] = 0;
                }
            }
            $stats->itemCount = $_POST['data']['cleaningcost_' . $type->id . "_count"];
            $res[$type->id] = $stats;
        }
        return $res;
    }

    public function buildExtraCleaningCost() {
        $res = array();
        foreach($_POST['data'] as $key => $val) {
            if(stristr($key, "extra_cleaningcost_")) {
                $k = str_replace("extra_cleaningcost_", "", $key);
                $res[$k] = $val;
                if(!$res[$k]) {
                    $res[$k] = 0;
                }
            }
        }
        return $res;
    }

    public function setItem() {
        $_SESSION['PMSCONFIG_ITEMID'] = $_POST['data']['item'];
    }
    
    public function isHotel($notificationSettings) {
        return $notificationSettings->bookingProfile == "hotel";
    }
    
    function createInventory() {
        if(!$_POST['data']['itemname']) {
            return;
        }
        $inventoryProduct = $this->getApi()->getProductManager()->createProduct();
        $inventoryProduct->name = $_POST['data']['itemname'];
        $this->getApi()->getProductManager()->saveProduct($inventoryProduct);
        
        $newInventory = new \core_pmsmanager_PmsInventory();
        $newInventory->productId = $inventoryProduct->id;
        $newInventory->count = 1;
        
        $config = $this->getApi()->getPmsManager()->getConfiguration($this->getSelectedName());
        $config->inventoryList->{$inventoryProduct->id} = $newInventory;
        $this->getApi()->getPmsManager()->saveConfiguration($this->getSelectedName(), $config);
    }
    
    public function addItemToSelectedRoom() {
        $inventory = new \core_pmsmanager_PmsInventory();
        $inventory->count = 1;
        $inventory->productId = $_POST['data']['id'];
        
        echo $this->getSelectedItem();
        if(!$this->getSelectedItem()) {
            return;
        }
        
        $additonal = $this->getApi()->getPmsManager()->getAdditionalInfo($this->getSelectedName(), $this->getSelectedItem());
        $additonal->inventory->{$inventory->productId} = $inventory;
        $this->getApi()->getPmsManager()->updateAdditionalInformationOnRooms($this->getSelectedName(), $additonal);
    }
    
    public function removeInventoryForRoom() {
        if(!$this->getSelectedItem()) {
            return;
        }
        
        $prodId = $_POST['data']['id'];
        
        $additonal = $this->getApi()->getPmsManager()->getAdditionalInfo($this->getSelectedName(), $this->getSelectedItem());
        unset($additonal->inventory->{$prodId});
        $this->getApi()->getPmsManager()->updateAdditionalInformationOnRooms($this->getSelectedName(), $additonal);
    }

    public function printInventoryForSelectedRoom() {
        $itemId = $this->getSelectedItem();
        if(!$itemId) {
            echo "<div class='inventoryitem'>Please select a room to add inventory to from the list above.</div>";
            return;
        }
        
        $additonal = $this->getApi()->getPmsManager()->getAdditionalInfo($this->getSelectedName(), $itemId);

        $found = false;
        foreach($additonal->inventory as $inv) {
            $product = $this->getApi()->getProductManager()->getProduct($inv->productId);
            echo "<div class='inventoryitem'>";
            echo "<i class='fa fa-trash-o removeInventoryForRoom' productid='".$inv->productId."'></i> ";
            echo $product->name;
            echo "<input type='txt' style='width: 30px; border: solid 1px;text-align:center; float:right;' class='inventoryonroomcount' productid='".$inv->productId."' value='".$inv->count."'></input>";
            echo "</div>";
            $found = true;
        }
        
        if(!$found) {
            $room = $this->getApi()->getBookingEngine()->getBookingItem($this->getSelectedName(), $this->getSelectedItem());
            echo "<div class='inventoryitem'>No inventory added to room ".$room->bookingItemName . ", when done you can clone room " . $room->bookingItemName . " by using the clone room function below.";
            echo "</div>";
        }
    }
    
    public function updateInventoryOnRoomCount() {
         if(!$this->getSelectedItem()) {
            return;
        }
        
        $prodId = $_POST['data']['productid'];
        $count = $_POST['data']['count'];
        
        $additonal = $this->getApi()->getPmsManager()->getAdditionalInfo($this->getSelectedName(), $this->getSelectedItem());
        $additonal->inventory->{$prodId}->count = $count;
        $this->getApi()->getPmsManager()->updateAdditionalInformationOnRooms($this->getSelectedName(), $additonal);
    }
    
    public function removeInventory() {
        $productId = $_POST['data']['attr1'];
        $config = $this->getApi()->getPmsManager()->getConfiguration($this->getSelectedName());
        unset($config->inventoryList->{$productId});
        $this->getApi()->getPmsManager()->saveConfiguration($this->getSelectedName(), $config);
        
        $allAdditional = $this->getApi()->getPmsManager()->getAllAdditionalInformationOnRooms($this->getSelectedName());
        foreach($allAdditional as $additionalInfo) {
            if(isset($additionalInfo->inventory->{$productId})) {
                unset($additionalInfo->inventory->{$productId});
                $this->getApi()->getPmsManager()->updateAdditionalInformationOnRooms($this->getSelectedName(), $additionalInfo);
            }
        }
    }
    
    public function printInventory() {
        $config = $this->getApi()->getPmsManager()->getConfiguration($this->getSelectedName());
        foreach($config->inventoryList as $productId => $inventory) {
            
            $product = $this->getApi()->getProductManager()->getProduct($productId);
            echo "<div class='inventoryitem'><i class='fa fa-trash-o' gsclick='removeInventory' gs_confirm='Are you sure' attr1='".$product->id."'></i> ";
            echo $product->name;
            echo "<span style='float:right;'>";
            echo "<i class='fa fa-plus-circle' title='All rooms having this item' style='cursor:pointer;' productid='".$product->id."'></i> ";
            echo "<i class='fa fa-arrow-right addInventoryItem' itemid='".$productId."'></i>";
            echo "</span>";
            echo "<div class='itemaddedtoroomlist'></div>";
            echo "</div>";
        }
    }
    
    public function updateInventoryForAllRooms() {
        $product = $_POST['data']['product'];
        
        foreach($_POST['data'] as $key => $val) {
            if(stristr($key, "room_")) {
                $item = str_replace("room_", "", $key);
                $existingAddditional = $this->getApi()->getPmsManager()->getAdditionalInfo($this->getSelectedName(), $item);
                if($val === "true" && isset($existingAddditional->inventory->{$product})) {
                    continue;
                }
                if($val !== "true" && !isset($existingAddditional->inventory->{$product})) {
                    continue;
                }
                if($val === "true") {
                    $inventory = new \core_pmsmanager_PmsInventory();
                    $inventory->count = 1;
                    $inventory->productId = $product;
                    $existingAddditional->inventory->{$product} = $inventory;
                } else {
                    unset($existingAddditional->inventory->{$product});
                }
                $this->getApi()->getPmsManager()->updateAdditionalInformationOnRooms($this->getSelectedName(), $existingAddditional);
            }
        }
    }
    
    public function loadRoomsAddedToList() {
        $allItemTypes = $this->getApi()->getPmsManager()->getAllAdditionalInformationOnRooms($this->getSelectedName());
        $items = $this->indexList($this->getApi()->getBookingEngine()->getBookingItems($this->getSelectedName()));
        $product = $this->indexList($this->getApi()->getProductManager()->getAllProducts());
        
        $additionalItems = array();
        foreach($allItemTypes as $type) {
            $additionalItems[$type->itemId] = $type;
        }
        
        echo "<span gstype='form' method='updateInventoryForAllRooms'>";
        echo "<input type='hidden' gsname='product' value='".$_POST['data']['productid']."'>";
        echo "<table width='100%'>";
        echo "<tr>";
        $i = 0;
        foreach($items as $item) {
            $i++;
            $checked = "";
            
            if(isset($additionalItems[$item->id])) {
                if(isset($additionalItems[$item->id]->inventory->{$_POST['data']['productid']})) {
                    $checked = "CHECKED";
                }
            }
            
            echo "<td width='10'><input type='checkbox' gsname='room_".$item->id."' $checked></input>";
            echo "<td>" . $item->bookingItemName . "</td>";
            if($i % 6 == 0) {
                echo "</tr><tr>";
            }
        }
        echo "</tr>";
        echo "</table>";
        echo "<input type='button' gstype='submit' value='Update'>";
        echo "</span>";
    }

    public function getSelectedItem() {
        if(isset($_SESSION['PMSCONFIG_ITEMID'])) {
            return $_SESSION['PMSCONFIG_ITEMID'];
        }
        return "";
    }
    
    public function cloneRooms() {
        $basis = $_POST['data']['basisroom'];
        $additional = $this->getApi()->getPmsManager()->getAdditionalInfo($this->getSelectedName(), $basis);
        
        foreach($_POST['data'] as $key => $val) {
            if($val !== "true") {
                continue;
            }
            if(stristr($key, "room_")) {
                $item = str_replace("room_", "", $key);
                $toClone = $this->getApi()->getPmsManager()->getAdditionalInfo($this->getSelectedName(), $item);
                $toClone->inventory = $additional->inventory;
                $this->getApi()->getPmsManager()->updateAdditionalInformationOnRooms($this->getSelectedName(), $toClone);
            }
        }
    }
    

    public function printRoomInventoryList() {
        $allItemTypes = $this->getApi()->getPmsManager()->getAllAdditionalInformationOnRooms($this->getSelectedName());
        $items = $this->indexList($this->getApi()->getBookingEngine()->getBookingItems($this->getSelectedName()));
        $product = $this->indexList($this->getApi()->getProductManager()->getAllProducts());

        $additionalItems = array();
        foreach($allItemTypes as $type) {
            $additionalItems[$type->itemId] = $type;
        }

        echo "<table>";
        echo "<tr>";
        echo "<th align='left'>Room</th>";
        echo "<th align='left'>Inventory (<span class='roomcloning'  style='color:blue; cursor:pointer;' onclick='$(\".PmsConfiguration .cloneroompanel\").toggle();'>clone room</span>)</th>";
        echo "</tr>";
        foreach($items as $item) {
            $inventory = $additionalItems[$item->id]->inventory;
            echo "<tr>";
            echo "<td>" . $item->bookingItemName . "</td>";
            echo "<td>";
            foreach($inventory as $inv) {
                if(!$inv->productId) {
                    continue;
                }
                echo "<span class='productinventoryitem'>" . $inv->count . "x". $product[$inv->productId]->name . "</span>";
            }
            echo "</td>";
            echo "</tr>";
        }
        echo "</table>";
    }

}
?>
