<?php
namespace ns_92f398b2_7be4_4aad_a790_2aa189108e3c;

class SavedCardsPrinter extends \WebshopApplication implements \Application {
    var $redirectToPayment;
    
    public function getDescription() {
        
    }

    public function getName() {
        return "SavedCardsPrinter";
    }

    public function render() {
      if($this->redirectToPayment) {
            ?>
            <script>
                thundashop.common.goToPageLink("?page=cart&payorder=<?php echo $this->redirectToPayment; ?>");
            </script>
            <?php
        } else {
            $this->includefile("cards");
        }
    }
    
    public function addCard() {
        $orderId = $this->getApi()->getOrderManager()->createRegisterCardOrder();
        $this->redirectToPayment = $orderId;
        
    }
    
    public function removeCard() {
        $id = $_POST['data']['id'];
        
        $user = $this->getApi()->getUserManager()->getLoggedOnUser();
        $cards = array();
        foreach($user->savedCards as $card) {
            if($card->id == $id) {
                continue;
            }
            $cards[] = $card;
        }
        $user->savedCards = $cards;
        $this->getApi()->getUserManager()->saveUser($user);
    }
}
?>
