<?php
namespace ns_acb219a1_4a76_4ead_b0dd_6f3ba3776421;

class CrmCustomerView extends \MarketingApplication implements \Application {
   
    private $user;
    private $domainName;

    public function getDescription() {
        
    }
    
    public function loadDiscountCode() {
        $pmsPricingNew = new \ns_4c8e3fe7_3c81_4a74_b5f6_442f841a0cb1\PmsPricingNew();
        $pmsPricingNew->loadEditDiscountCode();
    }
    
    public function formatRooms($booking) {
        return sizeof($booking->rooms);
    }
    
    public function createDiscountCode() {
        $coupon = new \core_cartmanager_data_Coupon();
        $coupon->amount = 0;
        $coupon->type = "PERCENTAGE";
        $coupon->code = $_POST['data']['code'];
        $coupon->timesLeft = 9999;
        $coupon->priceCode = "default";
        
        $this->getApi()->getCartManager()->addCoupon($coupon);
    }
    
    public function printCards($userId) {
        $user = $this->getApi()->getUserManager()->getUserById($userId);
        $res = (array)$user->savedCards;
        if(sizeof($res) == 0) {
            echo "<br>";
            echo "We have no cards registered on this customer.";
        }
        foreach($user->savedCards as $card) {
            echo "<i class='fa fa-trash-o deletecardbutton' cardid='".$card->id."' userid='$userId'></i> " . $card->mask . " - " . $card->expireMonth . "/" . $card->expireYear . "<br>";
        }
    }
    
    public function updateRestrictions() {
        $userId = $_POST['data']['userid'];
        $domain = $_POST['data']['domain'];
        $config = $this->getApi()->getPmsManager()->getConfiguration($domain);
        $toAdd = array();
        foreach($_POST['data'] as $key => $val) {
            if($val != "true") {
                continue;
            }
            if(stristr($key, "area_")) {
                $toAdd[] = str_replace("area_", "", $key);
            }
        }
        $config->mobileViewRestrictions->{$userId} = $toAdd;
        $this->getApi()->getPmsManager()->saveConfiguration($domain, $config);
        
        $user = $this->getUser();
        $newAnnoutationsAdded = array();
        foreach($user->annotionsAdded as $key => $val) {
            if($val == "ExcludePersonalInformation" && $_POST['data']['area_userdata'] != "true") {
                continue;
            }
            $newAnnoutationsAdded[$key] = $val;
        }
        $user->annotionsAdded = $newAnnoutationsAdded;
        $this->getApi()->getUserManager()->saveUser($user);
        
    }
    
    public function saveDiscountPreferences() {
        $domain = $_POST['data']['domain'];
        $user = $this->getApi()->getUserManager()->getUserById($_POST['data']['userid']);
        $user->preferredPaymentType = $_POST['data']['preferredPaymentType'];
        $user->showExTaxes = $_POST['data']['showExTaxes'] == "true";
        $user->referenceKey = $_POST['data']['referencecode'];
        
        $discount = $this->getApi()->getPmsInvoiceManager()->getDiscountsForUser($domain, $user->id);
        $discount->supportInvoiceAfter = $_POST['data']['createAfterStay'] == "true";
        $discount->discountType = 0;
        $discount->pricePlan = "default";
        $discount->attachedDiscountCode = $_POST['data']['attachedDiscountCode'];

        if($_POST['data']['discounttype'] == "fixedprice") {
            $discount->discountType = 1;
        }
        foreach($_POST['data'] as $index => $val) {
            if(stristr($index, "discount_")) {
                $room = str_replace("discount_", "", $index);
                if($val && is_numeric($val)) {
                    $discount->discounts->{$room} = $val;
                }
            }
        }
        
        $enabledMethods = array();
        foreach($_POST['data'] as $index => $val) {
            if(stristr($index, "enabledpmethod_")) {
                if($val != "true") {
                    continue;
                }
                $id = str_replace("enabledpmethod_", "", $index);
                $enabledMethods[] = $id;
            }
        }
        $user->enabledPaymentOptions = $enabledMethods;
        
        $this->getApi()->getPmsInvoiceManager()->saveDiscounts($domain, $discount);
        $this->getApi()->getUserManager()->saveUser($user);
        
        if($_POST['data']['attachedDiscountCode']) {
            $this->updateDiscountCode();
        }
    }
    
    /** @param \core_pmsmanager_PmsBooking $booking */
    public function formatGuests($booking) {
        $guest = "";
        $guests = array();
        $count = 0;
        foreach($booking->rooms as $room) {
            $count += $room->numberOfGuests;
           foreach($room->guests as $guest) {
               if($guest->name) {
                   $guests[] = $guest->name;
               }
           }
        }
        $title = join(", ", $guests);
        return "<span title='$title'>" . $count . "</span>";
    }
    
    public function formatCreated($booking) {
        return date("d.m.Y", strtotime($booking->rowCreatedDate));
    }
    /** @param \core_pmsmanager_PmsBooking $booking */
    public function formatbookingCost($booking) {
        return $booking->totalPrice;
    }
    
    /** @param \core_pmsmanager_PmsBooking $booking */
    public function formatpaymentCost($booking) {
        $total = $booking->totalPrice - $booking->totalUnsettledAmount;
        return $total;
    }
    
    /**
     * 
     * @param \core_pmsmanager_PmsBooking $booking
     * @return type
     */
    public function formatOrders($booking) {
        return sizeof($booking->orderIds);
    }
    
    public function formatRoomNames() {
        return "";
    }
    
    public function setUserId() {
        $_SESSION['usersrow_lastuserid'] = $_POST['data']['id'];
    }

    public function getName() {
        return "UsersRow";
    }

    public function getUser() {
        if(!$this->user) {
            $this->user = $this->getApi()->getUserManager()->getUserById($_SESSION['usersrow_lastuserid']);
        }
        return $this->user;
    }
    
    public function render() {
        echo "<table cellspacing='0' cellpadding='0' width='100%'>";
        echo "<tr>";
        echo "<td valign='top' style='width:150px;'>";
        $this->includefile("leftmenu");
        echo "</td>";
        echo "<td valign='top'>";
        echo "<div style='padding-left: 20px;' class='mainarea'>";
        $this->loadSelectedArea();
        echo "</div>";
        echo "</td>";
        echo "</tr>";
        echo "</table>";
    }
    
    public function saveCumsterDetails() {
        $user = $this->getApi()->getUserManager()->getUserById($_POST['data']['userid']);
        
        /*
            [userid] => 2a3edf2e-7811-44a4-8687-58576c09526e
            [fullName] => ACCON AS
            [emailAddress] => kristine@accon.no
            [prefix] => 47
            [cellPhone] => 97141516
            [emailAddressToInvoice] => 
            [address_fullname] => ACCON AS
            [address_address] => Bjørnsons vei 6A
            [address_postcode] => 3117
            [address_city] => TØNSBERG
            [vatNumber] => 915136613
            [invoiceReference] => 
            [invoice_address] => 
            [invoice_postcode] => 
            [invoice_city] => 
         */
        
        $user->fullName = $_POST['data']['fullName'];
        $user->emailAddress = $_POST['data']['emailAddress'];
        $user->prefix = $_POST['data']['prefix'];
        $user->cellPhone = $_POST['data']['cellPhone'];
        $user->emailAddressToInvoice = $_POST['data']['emailAddressToInvoice'];
        if(!$user->address) {
            $user->address = new \core_usermanager_data_Address();
        }
        $user->address->address = $_POST['data']['address_address'];
        $user->address->fullName = $_POST['data']['address_fullname'];
        $user->address->city = $_POST['data']['address_city'];
        $user->address->postCode = $_POST['data']['address_postcode'];
        $user->address->co = $_POST['data']['address_co'];
        
        
        $company = $user->companyObject;
        $company->invoiceReference = $_POST['data']['invoiceReference'];
        
        if(!$company->invoiceAddress) {
            $company->invoiceAddress = new \core_usermanager_data_Address();
        }
        $company->invoiceAddress->address = $_POST['data']['invoice_address'];
        $company->invoiceAddress->postCode = $_POST['data']['invoice_postcode'];
        $company->invoiceAddress->city = $_POST['data']['invoice_city'];
        $company->invoiceAddress->countrycode = $_POST['data']['invoice_countrycode'];
        $this->getApi()->getUserManager()->saveCompany($company);
        $this->getApi()->getUserManager()->saveUser($user);
    }
    
    public function connectToUser() {
        $vatNumber = $_POST['data']['vatnumber'];
        $userid = $_POST['data']['userid'];
        $this->getApi()->getUserManager()->connectCompanyToUser($userid, $vatNumber);
    }

    public function changeArea() {
        $_SESSION['usersrow_selectedarea'] = $_POST['data']['area'];
        $this->loadSelectedArea();
    }

    public function refresh() {
        $this->loadSelectedArea();
    }


    public function loadUser($id) {
        $_SESSION['usersrow_lastuserid'] = $id;
        $this->user = $this->getApi()->getUserManager()->getUserById($id);
    }

    public function getSelectedArea() {
        if(isset($_SESSION['usersrow_selectedarea'])) {
            return $_SESSION['usersrow_selectedarea'];
        }
        return "overview";
    }
    
    public function loadSelectedArea() {
        $area = $this->getSelectedArea();
        $this->includefile($area);
    }
    
    public function deleteUser() {
        $this->getApi()->getUserManager()->deleteUser($_POST['data']['userid']);
    }
    
    public function saveUserSettings() {
        $user = $this->getUser();
        $user->type = $_POST['data']['type'];
        
         if (isset($_POST['data']['canchangepagelayout'])) {
            $user->canChangeLayout = $_POST['data']['canchangepagelayout'];
         }
        $user->sessionTimeOut = $_POST['data']['sessionTimeOut'];
        $this->getApi()->getUserManager()->saveUser($user);
        $this->user = $this->getApi()->getUserManager()->getUserById($user->id);

    }
    
    public function updateUsersRight() {
        $modules = $this->getApi()->getPageManager()->getModules();
        $user = $this->getUser();
        $user->hasAccessToModules = array();
        foreach($modules as $module) {
            if($_POST['data'][$module->id] == "true") {
                $user->hasAccessToModules[] = $module->id;
            }
        }
        $this->getApi()->getUserManager()->saveUser($user);
    }
    
    public function updatePmsRights() {
        $menu = new \ModulePage(null, "pms");
        $user = $this->getUser();
        $user->pmsPageAccess = array();
        foreach($menu->getTopMenuPms()->getEntries() as $entry) {
            if($_POST['data'][$entry->getPageId()] == "true") {
                $user->pmsPageAccess[] = $entry->getPageId();
            }
        }
        $this->getApi()->getUserManager()->saveUser($user);
    }
    
    public function updateSalesPointRights() {
        $menu = new \ModulePage(null, "salespoint");
        $user = $this->getUser();
        $user->salesPointPageAccess = array();
        foreach($menu->getTopMenuSalesPoint()->getEntries() as $entry) {
            if($_POST['data'][$entry->getPageId()] == "true") {
                $user->salesPointPageAccess[] = $entry->getPageId();
            }
        }
        $this->getApi()->getUserManager()->saveUser($user);
    }
    
    public function saveAccountingDetails() {
        $user = $this->getUser();
        $user->accountingId = $_POST['data']['accountingId'];
        $user->externalAccountingId = $_POST['data']['externalAccountingId'];
        $this->getApi()->getUserManager()->saveUser($user);
        $this->user = $this->getApi()->getUserManager()->getUserById($user->id);
    }
    
    public function updateUser() {
        $user = $this->getUser();
        
        $user->fullName = $_POST['data']['fullName'];
        $user->emailAddress = $_POST['data']['emailAddress'];
        $user->prefix = $_POST['data']['prefix'];
        $user->cellPhone = $_POST['data']['cellPhone'];
        $user->emailAddressToInvoice = $_POST['data']['emailAddressToInvoice'];
        
        if (!$user->address) {
            $user->address = new \core_usermanager_data_Address();
        }
        
        $user->address->fullName = $_POST['data']['address_fullname'];
        $user->address->address = $_POST['data']['address_address'];
        $user->address->postCode = $_POST['data']['address_postcode'];
        $user->address->city = $_POST['data']['address_city'];
        
        if (isset($_POST['data']['canchangepagelayout'])) {
            $user->canChangeLayout = $_POST['data']['canchangepagelayout'];
        }
        
        $this->getApi()->getUserManager()->saveUser($user);
        
        $this->user = $this->getApi()->getUserManager()->getUserById($user->id);
    }
    
    public function changePassword() {
        $user = $this->getUser();
        $this->getApi()->getUserManager()->updatePasswordSecure($user->id, $_POST['data']['password']);
    }
    
    public function changePinCode() {
        $user = $this->getUser();
        $user->secondaryLoginCode = $_POST['data']['pincode'];
        $this->getApi()->getUserManager()->saveUser($user);
    }
    
    public function regeneratTotpKey() {
        $this->loadData();
        $user = $this->getUser();
        $this->getApi()->getUserManager()->createGoogleTotpForUser($user->id);
    }
    
    public function getDomains() {
        if($this->domainName) {
            $array = array();
            $array[] = $this->domainName;
            return $this->array;
        }
        
        $names = $this->getApi()->getStoreManager()->getMultiLevelNames();
        $realnames = array();
        foreach($names as $name) {
            if($this->getApi()->getPmsManager()->isActive($name)) {
                $realnames[] = $name;
            }
        }
        $this->array = $realnames;
        return $realnames;
        
    }
    
    public function formatRowCreatedDate($user) {
        return \GetShopModuleTable::formatDate($user->rowCreatedDate);
    }

    public function updateDiscountCode() {
        $pmsPricing = new \ns_4c8e3fe7_3c81_4a74_b5f6_442f841a0cb1\PmsPricingNew();
        $pmsPricing->saveDiscountCode();
    }

    public function createCodeSuggestion($user) {
        
        $expr = '/(?<=\s|^)[a-z]/i';
        preg_match_all($expr, $user->fullName, $matches);
        while(true) {
            $code = join("", $matches[0]). rand(1000,9999);
            $coupon = $this->getApi()->getCartManager()->getCoupon($code);
            if(!$coupon) {
                break;
            }
        }
        
        return strtoupper($code);
    }

}
?>
