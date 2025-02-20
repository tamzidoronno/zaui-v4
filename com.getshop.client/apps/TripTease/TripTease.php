<?php
namespace ns_c844b3fe_84b0_4d26_a8e2_8aa361ed82c4;

class TripTease extends \WebshopApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "TripTease";
    }

    public function render() {
        $this->includefile("configuration");
    }
    
    public function getBookingId() {
        return $_SESSION['tripteasebookingid'];
    }
    
    public function renderBottom() {
        $page = $this->getFactory()->getPage();
        if($page->getId() == "payment_success") {
            $id = $this->getHotelKey();
            $value = $this->getBookingValue();
            $bookingId = $this->getBookingId();
            ?>
            <script src="https://static.triptease.io/paperboy/confirm?hotelkey=<?php echo $id; ?>&bookingValue=<?php echo $value; ?>&bookingCurrency=NOK&bookingReference=<?php echo $bookingId; ?>" defer></script>
            <?php
        }
    }
    
    public function getHotelKey() {
        $config = $this->getApi()->getPmsManager()->getConfiguration($this->getSelectedMultilevelDomainName());
        return $config->tripTeaseHotelId;
    }
        
    /**
     * @param \core_pmsmanager_Room $room
     */
    public function renderPriceWidget($selectedRoom, $booking, $id) {
        if($id) {
            $booking = $this->getApi()->getPmsManager()->getCurrentBooking($this->getSelectedMultilevelDomainName());
            $_SESSION['tripteasebookingid'] = $booking->id;
            $_SESSION['tripteasebookingbookingvalue'] = $booking->totalPrice;
            $lang = $booking->language;
            if($booking->language == "nb_NO") {
                if(stristr($lang, "_")) {
                    $splitted = explode("_", $lang);
                    $lang = $splitted[1];
                }
            }
            ?>
            <div class="price-fighter-widget"
                data-pf-hotelkey="<?php echo $this->getHotelKey(); ?>"
                data-pf-checkin="<?php echo date("Y-m-d", strtotime($booking->sessionStartDate)); ?>"
                data-pf-checkout="<?php echo date("Y-m-d", strtotime($booking->sessionEndDate)); ?>"  
                data-pf-direct-price="<?php echo $selectedRoom->price; ?>"
                data-pf-room-rate="<?php echo $selectedRoom->type->name; ?>"
                data-pf-adults="1"
                data-pf-children="0"
                data-pf-children-ages="7"
                data-pf-currency="NOK"
                data-pf-language="<?php echo $lang; ?>">
            </div>
            <?php
        }
    }
    
    public function renderOnStartup() {
        $id = $this->getHotelKey();
        if($id) {
            ?>
            <script src="https://static.triptease.io/paperboy/paperboy.js?hotelkey=<?php echo $id; ?>" defer></script>
            <?php
        }
    }
    
    public function saveTripTeaseConfig() {
        $config = $this->getApi()->getPmsManager()->getConfiguration($this->getSelectedMultilevelDomainName());
        $config->tripTeaseHotelId = $_POST['data']['hotelkey'];
        $this->getApi()->getPmsManager()->saveConfiguration($this->getSelectedMultilevelDomainName(), $config);
    }

    public function getBookingValue() {
        return $_SESSION['tripteasebookingbookingvalue'];
    }

}
?>
