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
    
    public function deleteGiftCard() {
        $this->getApi()->getGiftCardManager()->deleteGiftCard($_POST['data']['cardid']);
    }
    
    public function formatAction($giftCard) {
        $res = "<i class='fa fa-trash-o' gsclick='deleteGiftCard' cardid='".$giftCard->id."' gs_confirm='Are you sure you want to delete this giftcard?'></i> ";
        $res .= "<i class='fa fa-edit editgiftcard'><span class='editgiftcardpanel' gstype='form' method='updateGiftCard' style='position: absolute;
    border: solid 1px #bbb;
    width: 170px;display:none;
    padding: 3px;
    text-align: center;
    background-color: #fff;
    z-index: 2;'>";
        $res .= "<div style='height: 20px;'><span style='float:left;'>Amount</span><span style='float:right;'>Remaining</span></div>";
        $res .= "<input type='hidden' style='width:60px;' class='gsniceinput1' gsname='code' value='".$giftCard->cardCode."'>";
        $res .= "<input type='txt' style='width:60px;' class='gsniceinput1' gsname='giftCardValue' value='".$giftCard->giftCardValue."'>";
        $res .= "<input type='txt' class='gsniceinput1' style='width:60px;' gsname='remainingValue' value='".$giftCard->remainingValue."'>";
        $res .= "<div class='shop_button' gstype='submit' style='width:142px; margin-top: 3px;'>Update</div>";
        $res .= "</span></i>";
        return $res;
    }
    
    public function updateGiftCard() {
        $card = $this->getApi()->getGiftCardManager()->getGiftCard($_POST['data']['code']);
        $card->remainingValue = $_POST['data']['remainingValue'];
        $card->giftCardValue = $_POST['data']['giftCardValue'];
        $this->getApi()->getGiftCardManager()->saveGiftCard($card);
    }
    
    public function printTable() {
        $args = array();
        
        $attributes = array(
            array('id', 'gs_hidden', 'id'),
            array('delete', 'ACTION', 'rowCreatedDate', 'formatAction'),
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
