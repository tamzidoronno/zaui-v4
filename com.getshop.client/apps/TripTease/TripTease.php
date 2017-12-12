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
            <script defer src="https://static.triptease.io/paperboy/confirm?hotelkey=<?php echo $id; ?>&bookingValue=<?php echo $value; ?>&bookingCurrency=NOK&bookingReference=<?php echo $bookingId; ?>"></script>
            <?php
        }
    }
    
    public function getHotelKey() {
        $config = $this->getApi()->getPmsManager()->getConfiguration($this->getSelectedMultilevelDomainName());
        return $config->tripTeaseHotelId;
    }
        
    public function renderPriceWidget() {
        $config = $this->getApi()->getPmsManager()->getConfiguration($this->getSelectedMultilevelDomainName());
        $id = $config->tripTeaseHotelId;
        if($id) {
            $booking = $this->getApi()->getPmsManager()->getCurrentBooking($this->getSelectedMultilevelDomainName());
            $_SESSION['tripteasebookingid'] = $booking->id;
            $_SESSION['tripteasebookingbookingvalue'] = $booking->totalPrice;
            foreach($booking->rooms as $room) {
                ?>
                <div class="price-fighter-widget"
                    data-pf-hotelkey="<?php echo $this->getHotelKey(); ?>"
                    data-pf-checkin="<?php echo date("Y-m-d", strtotime($room->date->start)); ?>"
                    data-pf-checkout="<?php echo date("Y-m-d", strtotime($room->date->end)); ?>"  
                    data-pf-direct-price="<?php echo date("Y-m-d", strtotime($room->totalCost)); ?>"
                    data-pf-room-rate="<?php echo date("Y-m-d", strtotime($room->price)); ?>"
                    data-pf-adults="<?php echo date("Y-m-d", strtotime($room->numberOfGuests)); ?>"
                    data-pf-children="1"
                    data-pf-children-ages="7"
                    data-pf-currency="NOK"
                    data-pf-language="<?php echo $booking->language; ?>">
                </div>
                <?php
            }
        }
    }
    
    public function renderOnStartup() {
        $id = $this->getHotelKey();
        if($id) {
            ?>
            <script defer src="https://static.triptease.io/paperboy/paperboy.js?hotelkey=<?php echo $id; ?>"></script>
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
