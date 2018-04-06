<?php
namespace ns_acb219a1_4a76_4ead_b0dd_6f3ba3776421;

class CrmCustomerView extends \MarketingApplication implements \Application {
   
    private $user;
    private $domainName;

    public function getDescription() {
        
    }
    
    public function formatRooms($booking) {
        return sizeof($booking->rooms);
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
    }
    
    public function saveDiscountPreferences() {
        $domain = $_POST['data']['domain'];
        $user = $this->getApi()->getUserManager()->getUserById($_POST['data']['userid']);
        $user->preferredPaymentType = $_POST['data']['preferredPaymentType'];
        $user->showExTaxes = $_POST['data']['showExTaxes'] == "true";
        $discount = $this->getApi()->getPmsInvoiceManager()->getDiscountsForUser($domain, $user->id);
        $discount->supportInvoiceAfter = $_POST['data']['createAfterStay'] == "true";
        $discount->discountType = 0;
        $discount->pricePlan = $_POST['data']['pricePlan'];
        $discount->attachedDiscountCode = $_POST['data']['attachedDiscountCode'];
        
        if($_POST['data']['discounttype'] == "fixedprice") {
            $discount->discountType = 1;
        }
        foreach($_POST['data'] as $index => $val) {
            if(stristr($index, "discount_")) {
                $room = str_replace("discount_", "", $index);
                $discount->discounts->{$room} = $val;
            }
        }
        $this->getApi()->getPmsInvoiceManager()->saveDiscounts($domain, $discount);
        $this->getApi()->getUserManager()->saveUser($user);
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
    
    public function saveUserSettings() {
        $user = $this->getUser();
        $user->type = $_POST['data']['type'];
        
         if (isset($_POST['data']['canchangepagelayout'])) {
            $user->canChangeLayout = $_POST['data']['canchangepagelayout'];
         }
        
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
        
        return $this->getApi()->getStoreManager()->getMultiLevelNames();
    }

}
?>
