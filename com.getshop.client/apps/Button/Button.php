<?php

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Description of Button
 *
 * @author ktonder
 */
namespace ns_2996287a_c23e_41ad_a801_c77502372789;

class Button extends \ApplicationBase implements \Application {
    public function getDescription() {
        return $this->__w("Add a button to your page. This button can be a add to cart button, go to page button, etc");
    }

    public function getName() {
        return $this->__w("Button");
    }

    public function render() {
        $this->includefile("button");
    }
    
    public function saveText() {
        $this->setConfigurationSetting("text", $_POST['data']['text']);
    }

    public function showSetup() {
        $this->includefile("setup");
    }
    
    public function searchForProduct() {
        $this->includefile("product_search_result");
    }
    
    public function setProductId() {
        $this->setConfigurationSetting("type", "add_to_cart");
        $this->setConfigurationSetting("product_id", $_POST['data']['product_id']);
    }
    
    public function addProductToCart() {
        $productId = $_POST['data']['productId'];
        $this->getApi()->getCartManager()->addProduct($productId, 1, []);
    }

    public function printEntry($etry) {
        $pageId = $etry->pageId;
        $buttonText = $this->getConfigurationSetting("page_id") == $pageId ? $this->__f("selected") : $this->__f("select");
        echo "<div style='background-color: #777; border-bottom: solid 1px #666;' class='outer_select_button_link_to_page'><div class='gs_button small select_button_set_link_to_internal_page' page_id='$pageId'>$buttonText</div><span> $etry->name</span></div>";
        if (count($etry->subentries)) {
            foreach ($etry->subentries as $subEntry) {
                $this->printEntry($subEntry);
            }
        }
    }
    
    public function setInternalPage() {
        $this->setConfigurationSetting("type", "link_to_interal_page");
        $this->setConfigurationSetting("page_id", $_POST['data']['page_id']);
    }

}
