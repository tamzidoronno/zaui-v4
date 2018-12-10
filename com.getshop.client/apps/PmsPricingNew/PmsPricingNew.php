<?php
namespace ns_4c8e3fe7_3c81_4a74_b5f6_442f841a0cb1;

class PmsPricingNew extends \WebshopApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "PmsPricingNew";
    }
    
    public function updateDiscountCode() {
        $this->saveDiscountCode();
    }
    
    public function loadAdvancedPricePlan() {
        $this->includefile("priceyieldpanel");
    }

    public function saveYieldPlan() {
        $plan = new \core_pmsmanager_PmsAdvancePriceYield();
        $plan->id = $_POST['data']['planid'];
        $plan->start = $this->convertToJavaDate(strtotime($_POST['data']['startyielddate']));
        $plan->end = $this->convertToJavaDate(strtotime($_POST['data']['endyeilddate']));
        $plan->yeilds = array();
        
        foreach($_POST['data'] as $key => $val) {
            if(stristr($key, "yield_")) {
                $key = str_replace("yield_", "", $key);
                $offsets = explode("_", $key);
                $entry = new \core_pmsmanager_YieldEntry();
                $entry->daysPrior = $offsets[0];
                $entry->occupancy = $offsets[1];
                $entry->yield = str_replace("%", "", $val);
                $plan->yeilds[$key] = $entry;
            }
        }
        
        $plan->types = array();
        foreach($_POST['data'] as $key => $val) {
            if(stristr($key, "validforcategory_")) {
                $room = str_replace("validforcategory_", "", $key);
                if($val == "true") {
                    $plan->types[] = $room;
                }
            }
        }
        
        
        $this->getApi()->getPmsInvoiceManager()->saveAdvancePriceYield($this->getSelectedMultilevelDomainName(), $plan);
    }
    
    public function deleteAdvancePricePlan() {
        $this->getApi()->getPmsInvoiceManager()->deleteYieldPlan($this->getSelectedMultilevelDomainName(), $_POST['data']['planid']);
    }
    
    public function deletePricePlan() {
        $code = $_POST['data']['code'];
        $this->getApi()->getPmsManager()->deletePricePlan($this->getSelectedName(), $code);
    }
    
    public function getSelectedName() {
        return $this->getSelectedMultilevelDomainName();
    }
    
    public function saveCoveragePrices() {
        $pricePlan = $this->getPrices();
        $newArray = array();
        
        for($i = 0; $i < 3; $i++) {
            if($_POST['data']['coverage_'. $i]) {
                 $newArray[$_POST['data']['coverage_'. $i]] = $_POST['data']['price_'. $i];
            }
        }
        
        $pricePlan->coveragePrices = $newArray;
        $pricePlan->coverageType = $_POST['data']['coveragePriceType'];
        $this->getApi()->getPmsManager()->setPrices($this->getSelectedName(), $pricePlan->code, $pricePlan);
    }
    
    public function saveAddonsToDiscount() {
        $couponId = $_POST['data']['couponid'];
        $coupon = $this->getApi()->getCartManager()->getCouponById($couponId);
        
        $res = array();
        foreach($_POST['data'] as $key => $val) {
            if((stristr($key, "_toinclude") || stristr($key, "_includedinroomprice")) && $val == "true") {
                if(stristr($key, "_toinclude")) {
                    $productId = str_replace("_toinclude", "", $key);
                }
                if(stristr($key, "_includedinroomprice")) { 
                    $productId = str_replace("_includedinroomprice", "", $key);
                }
                $includedInRoomPrice = $_POST['data'][$productId.'_includedinroomprice'];
                $toAdd = new \core_cartmanager_data_AddonsInclude();
                $toAdd->productId = $productId;
                $toAdd->includeInRoomPrice = ($includedInRoomPrice == "true");
                $res[] = $toAdd;
            }
        }

        $coupon->addonsToInclude = $res;
        $this->getApi()->getCartManager()->addCoupon($coupon);
    }
    
    public function loadCouponAddonIncludePanel() {
        $addons = $this->getApi()->getPmsManager()->getAddonsAvailable($this->getSelectedName());
        $coupon = $this->getApi()->getCartManager()->getCouponById($_POST['data']['id']);
        
        ?>
        <div style="text-align: right;">
             <i class="fa fa-close"  onclick="$('.addaddonsinludepanel').hide()" style="cursor:pointer;"></i>
         </div>
        <?php
        
        echo "<div gstype='form' method='saveAddonsToDiscount'>";
        echo "<input type='hidden' value='".$_POST['data']['id']."' gsname='couponid'>";
        echo "<div style='height: 350px; overflow:auto;'>";
        foreach($addons as $addon) {
            if(!$addon->name) {
                continue;
            }
            
            $included = "";
            $inroomprice = "";
            foreach($coupon->addonsToInclude as $addonav) {
                if($addonav->productId == $addon->productId) {
                    $included = "CHECKED";
                }
                if($addonav->productId == $addon->productId && $addonav->includeInRoomPrice) {
                    $inroomprice = "CHECKED";
                }
            }
            
            echo "<input type='checkbox' title='Include' gsname='".$addon->productId . "_toinclude' $included> ";
            echo "<input type='checkbox' title='Include in room price'  gsname='".$addon->productId . "_includedinroomprice' $inroomprice> ";
            echo $addon->name  . "<span style='float:right;'>" . $addon->price . "</span> ". "<br>";
        }
        echo "</div>";
        echo "<span class='pmsbutton' gstype='submit' style='width:100%; margin-top: 10px; box-sizing:border-box; text-align:center;'>Save settings</span>";
        echo "</div>";
    }
    
    public function saveAddonPriceOnPricePlan() {
        $res = array();
        foreach($_POST['data'] as $key => $val) {
            if(stristr($key, "product_")) {
                if(!$val) {
                    continue;
                }
                $productId = str_replace("product_", "", $key);
                $val = str_replace(",",".", $val);
                $res[$productId] = $val;
            }
        }
        $pricePlan = $this->getPrices();
        $pricePlan->productPrices = $res;
        $this->getApi()->getPmsManager()->setPrices($this->getSelectedName(), $this->getSelectedPricePlan(), $pricePlan);
    }
    
    public function selectPricePlan() {
        $_SESSION['selectedpriceplan'] = $_POST['data']['selectedpriceplan'];
    }
    
    public function getSelectedPricePlan() {
        if(isset($_SESSION['selectedpriceplan'])) {
            return $_SESSION['selectedpriceplan'];
        }
        return "default";
    }
    
    public function creatnewpriceplan() {
        if(!$_POST['data']['newplan']) {
            return;
        }
        $this->getApi()->getPmsManager()->createNewPricePlan($this->getSelectedName(), $_POST['data']['newplan']);
    }
    
    public function savelongtermdeal() {
        $pricePlan = $this->getPrices();
        $newArray = array();
        for($i = 0; $i < 10; $i++) {
            if($_POST['data']['minday_'. $i] && $_POST['data']['discount_'. $i]) {
                 $newArray[$_POST['data']['minday_'. $i]] = $_POST['data']['discount_'. $i];
            }
        }
        $pricePlan->longTermDeal = $newArray;
        $this->getApi()->getPmsManager()->setPrices($this->getSelectedName(), $pricePlan->code, $pricePlan);
    }
    
    public function render() {
        if(!$this->getSelectedName()) {
            echo "PLease specify a booking engine first";
        } else {
            echo $this->getSelectedMultilevelDomainName();
            $types = (array)$this->getApi()->getBookingEngine()->getBookingItemTypesWithSystemType($this->getSelectedMultilevelDomainName(), null);
            if(sizeof($types) == 0) {
                $this->includefile("nocategoriesyet");
            } else {
                echo "<div style='max-width:1500px;margin:auto;'>";
                $this->includefile("pricingview");
                echo "</div>";
            }
        }
    }
    
    public function setNewPrices() {
        $pricingObject = $this->getPrices();
        if(isset($_POST['data']['prices'])) {
            $prices = $_POST['data']['prices'];
            $pricingObject->dailyPrices = $prices;
        }

        $pricingObject->pricesExTaxes = $_POST['data']['prices_ex_taxes'] == "true";
        $pricingObject->privatePeopleDoNotPayTaxes = $_POST['data']['privatePeopleDoNotPayTaxes'] == "true";
        $pricingObject->derivedPrices = $_POST['data']['derivedPrices'];
        $pricingObject->derivedPricesChildren = $_POST['data']['derivedPricesChildren'];
        if(isset($_POST['data']['priceType'])) {
            $pricingObject->defaultPriceType = $_POST['data']['priceType'];
        }
        
        $this->getApi()->getPmsManager()->setPrices($this->getSelectedName(), $this->getSelectedPricePlan(), $pricingObject);
    }

    public function selectDates() {
        $start = $_POST['data']['start'];
        $end = $_POST['data']['end'];
        
        $_SESSION['pmspricing'][$this->getSelectedName()]['start'] = $start;
        $_SESSION['pmspricing'][$this->getSelectedName()]['end'] = $end;
    }

    public function showSettings() {
        $this->includefile("settings");
    }
    
    public function saveSettings() {
        foreach($_POST['data'] as $key => $value) {
            $this->setConfigurationSetting($key, $value);
        }
    }

    
    public function createcoupon() {
        $coupon = new \core_cartmanager_data_Coupon();
        $coupon->amount = $_POST['data']['amount'];
        $coupon->type = $_POST['data']['type'];
        $coupon->code = $_POST['data']['code'];
        $coupon->timesLeft = (int)$_POST['data']['times'];
        $coupon->priceCode = $this->getSelectedPricePlan();
        
        
        $this->addCouponError = "";
        if ($coupon->code == "" ) {
            $this->addCouponError = "Code can not be empty";
        }
        
        if (!is_numeric($coupon->timesLeft)) {
            $this->addCouponError = "Amount must be a number";
        }
        
        if (!$this->int_ok($coupon->amount) || $coupon->amount < 0) {
            $this->addCouponError = "Times must be a number";
        }
        
        if ($coupon->type == "PERCENTAGE" && ($coupon->amount < 0 || $coupon->amount > 100)) {
            $this->addCouponError = "Not a valid percentage";
        }
        
        if($coupon->type == "FIXEDPRICE") {
            $types = $this->getApi()->getBookingEngine()->getBookingItemTypes($this->getSelectedMultilevelDomainName());
            $maxcount = 0;
            foreach($types as $type) {
                if($type->size > $maxcount) {
                    $maxcount = $type->size;
                }
            }
            $coupon->dailyPriceAmountByType=array();
            foreach($types as $type) {
                for($i = 1; $i <= $maxcount; $i++) {
                    $coupon->dailyPriceAmountByType[$type->id."_".$i] = $coupon->amount;
                }
            }
        }
        
        
        if(!$this->addCouponError) {
            $this->getApi()->getCartManager()->addCoupon($coupon);
        }
    }
    
    private function int_ok($val) {
        return ($val !== true) && ((string)(int) $val) === ((string) $val);
    }
    
    public function getRepeatingSummary($coupon) {
        if(!$coupon->whenAvailable) {
            return "";
        }
        
        $text = "";
        if($coupon->whenAvailable->repeattype == "repeat") {
            if($coupon->whenAvailable->data->repeatPeride == "3") {
                $text = $this->__w("Daily");
            }
            if($coupon->whenAvailable->data->repeatPeride == "1") {
                $text = $this->__w("Every {periode} week") . " (";
                $text = str_replace("{periode}", $coupon->whenAvailable->data->repeatEachTime, $text);
                if($coupon->whenAvailable->data->repeatMonday) {
                    $text .= strtolower($this->__w("Mon")) . ", ";
                }
                if($coupon->whenAvailable->data->repeatTuesday) {
                    $text .= strtolower($this->__w("Tue")) . ", ";
                }
                if($coupon->whenAvailable->data->repeatWednesday) {
                    $text .= strtolower($this->__w("Wed")) . ", ";
                }
                if($coupon->whenAvailable->data->repeatThursday) {
                    $text .= strtolower($this->__w("Thu")) . ", ";
                }
                if($coupon->whenAvailable->data->repeatFriday) {
                    $text .= strtolower($this->__w("Fri")) . ", ";
                }
                if($coupon->whenAvailable->data->repeatSaturday) {
                    $text .= strtolower($this->__w("Sat")) . ", ";
                }
                if($coupon->whenAvailable->data->repeatSunday) {
                    $text .= strtolower($this->__w("Sun")) . ", ";
                }
                $text = substr($text, 0, -2) . ")";
            }
            if($coupon->whenAvailable->data->repeatPeride == "2") {
                if($coupon->whenAvailable->data->repeatAtDayOfWeek) {
                    $text = $this->__w("Repeats montly same day in week");
                } else {
                    $text = $this->__w("Repeats montly same date in month");
                }
            }
            
            $text .= " " . $this->__w("until") . " " . date("d.m.Y", strtotime($coupon->whenAvailable->data->endingAt));
        }
        if($coupon->pmsWhenAvailable == "REGISTERED") {
            $text .= " (" . $this->__w("when booked") . ")";
        } else {
            $text .= " (" . $this->__w("when staying") . ")";
        }
        return $text;
    }
    
    
    public function loadCouponRepeatingDataPanel() {
        $this->includefile("addmoredates");
    }
    
    public function addRepeatingDates() {
        $repeat = new \ns_46b52a59_de5d_4878_aef6_13b71af2fc75\PmsBookingSummary();
        $data = $repeat->createRepeatingDateObject();
        $coupon = $this->getApi()->getCartManager()->getCouponById($_POST['data']['couponid']);
        $coupon->pmsWhenAvailable = $_POST['data']['pmsWhenAvailable'];
        $coupon->whenAvailable = $data;
        
        $coupon->productsToSupport = array();
        foreach($_POST['data'] as $key => $value) {
            if($value !== "true") {
                continue;
            }
            if(stristr($key, "productdiscount_")) {
                $product = str_replace("productdiscount_", "", $key);
                $coupon->productsToSupport[] = $product;
            }
        }
        $coupon->minDays = $_POST['data']['minDays'];
        $coupon->maxDays = $_POST['data']['maxDays'];
        
        $this->getApi()->getCartManager()->addCoupon($coupon);
    }

    public function removeCoupon() {
        $coupon = $this->getApi()->getCartManager()->getCouponById($_POST['data']['id']);
        $this->getApi()->getCartManager()->removeCoupon($coupon->code);
    }
    
    public function savecommentoncoupon() {
        $coupon = $this->getApi()->getCartManager()->getCouponById($_POST['data']['couponid']);
        $coupon->description = $_POST['data']['description'];
        $this->getApi()->getCartManager()->addCoupon($coupon);
        
    }
    
    
    public function getStart() {
        if(isset($_SESSION['pmspricing'][$this->getSelectedName()]['start'])) {
            return $_SESSION['pmspricing'][$this->getSelectedName()]['start'];
        }
        return date("d.m.Y", time());
    }

    public function getEnd() {
        if(isset($_SESSION['pmspricing'][$this->getSelectedName()]['end'])) {
            return $_SESSION['pmspricing'][$this->getSelectedName()]['end'];
        }
        return date("d.m.Y", time()+(86400*90));        
    }

    public function updateProgressivePrices() {
        $itemTypes = $this->getApi()->getBookingEngine()->getBookingItemTypes($this->getSelectedName());
        $result = [];
        foreach($itemTypes as $type) {
            $typeId = $type->id;
            for($i = 0; $i < 10; $i++) {
                $entry = new \core_pmsmanager_ProgressivePriceAttribute();
                $entry->numberOfTimeSlots = $_POST['data']['date_slot_'.$i."_".$typeId];
                $entry->price = $_POST['data']['price_slot_'.$i."_".$typeId];
                if($entry->numberOfTimeSlots) {
                    if(!isset($result[$typeId])) {
                        $result[$typeId] = array();
                    }
                    $result[$typeId][] = $entry;
                }
            }
        }
        
        
        $pricingObject = $this->getPrices();
        $pricingObject->progressivePrices = $result;

        $this->getApi()->getPmsManager()->setPrices($this->getSelectedName(), $this->getSelectedPricePlan(), $pricingObject);
    }
    
    public function changepricetype() {
        $prices = $this->getPrices();
        $prices->defaultPriceType = $_POST['data']['pricetype'];
        $prices->pricesExTaxes = $_POST['data']['pricesExTaxes'] == "true";
        $prices->code = $_POST['data']['code'];
        $this->getApi()->getPmsManager()->setPrices($this->getSelectedName(), $this->getSelectedPricePlan(), $prices);
    }
    
    public function getPrices() {
        return $this->getApi()->getPmsManager()->getPricesByCode($this->getSelectedName(), $this->getSelectedPricePlan(),
            $this->convertToJavaDate(strtotime($this->getStart())),
            $this->convertToJavaDate(strtotime($this->getEnd())));
    }

    public function saveDiscountCode() {
        $this->saveAddonsToDiscount();
        $this->addRepeatingDates();
        $this->saveDiscountInformation();
        $this->saveCouponPrices();
    }

    public function saveDiscountInformation() {
        $coupon = $this->getApi()->getCartManager()->getCouponById($_POST['data']['couponid']);
        $coupon->timesLeft = $_POST['data']['timesLeft'];
        $coupon->type = $_POST['data']['type'];
        $coupon->description = $_POST['data']['description'];
        $this->getApi()->getCartManager()->addCoupon($coupon);
    }

    public function loadEditDiscountCode() {
        $this->includefile("discountcodesetup");
    }
    
    public function saveCouponPrices() {
        $coupon = $this->getApi()->getCartManager()->getCouponById($_POST['data']['couponid']);
        if($coupon->type == "FIXEDPRICE") {
            $types = $this->getApi()->getBookingEngine()->getBookingItemTypes($this->getSelectedMultilevelDomainName());
            $maxcount = 0;
            foreach($types as $type) {
                if($type->size > $maxcount) {
                    $maxcount = $type->size;
                }
            }
            $coupon->dailyPriceAmountByType=array();
            foreach($types as $type) {
                for($i = 1; $i <= $maxcount; $i++) {
                    if(isset($_POST['data'][$type->id."_".$i]) && $_POST['data'][$type->id."_".$i]) {
                        $coupon->dailyPriceAmountByType[$type->id."_".$i] = $_POST['data'][$type->id."_".$i];
                    }
                }
            }
        } else {
            $coupon->amount = $_POST['data']['otherpriceamount'];
        }
        $this->getApi()->getCartManager()->addCoupon($coupon);
    }

}
?>
