<?php
namespace ns_92f398b2_7be4_4aad_a790_2aa189108e3c;

class SavedCardsPrinter extends \WebshopApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "SavedCardsPrinter";
    }

    public function render() {
        $this->includefile("cards");
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
