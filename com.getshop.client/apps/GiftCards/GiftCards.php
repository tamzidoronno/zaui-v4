<?php
namespace ns_8a98611e_bfb4_437e_af0d_561a882b0777;

class GiftCards extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "GiftCards";
    }

    public function render() {
        $this->includefile("header");
        $this->printTable();
    }
    
    public function formatRowCreatedDate($giftCard) {
        return \GetShopModuleTable::formatDate($giftCard->rowCreatedDate);
    }
    
    public function printTable() {
        $args = array();
        
        $attributes = array(
            array('id', 'gs_hidden', 'id'),
            array('rowCreatedDate', 'CREATED', 'rowCreatedDate', 'formatRowCreatedDate'),
            array('cardCode', 'CARD CODE', 'cardCode'),
            array('giftCardValue', 'VALUE', 'giftCardValue'),
            array('remainingValue', 'Remaining Value', 'remainingValue')
        );
        
        $table = new \GetShopModuleTable($this, 'GiftCardManager', 'getAllCards', $args, $attributes);
        $table->avoidAutoExpanding();
        $table->render();
    }

}
?>
