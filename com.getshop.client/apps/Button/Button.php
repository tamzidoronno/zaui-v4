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
        if (isset($_POST['event']) && $_POST['event'] == "sendMail") {
            $successMessage = $this->getConfigurationSetting("successMessage");
            if ($successMessage) {
                echo "<script>alert('$successMessage');</script>";
            }
        }
        $this->includefile("button");
    }
    
    public function saveText() {
        $this->setConfigurationSetting("text", $_POST['data']['text']);
    }
    
    public function saveButtonTemplate() {
        $this->setConfigurationSetting("btntemplate", $_POST['data']['text']);
    }
    
    public function getButtonTemplate() {
        return $this->getConfigurationSetting("btntemplate");
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
    
    public function setExternalPage() {
        $this->setConfigurationSetting("type", "link_to_external_page");
        $this->setConfigurationSetting("url", $_POST['data']['link']);
        $this->setConfigurationSetting("popup", $_POST['data']['popup']);
    }
    
    public function setInternalPage() {
        $this->setConfigurationSetting("type", "link_to_interal_page");
        $this->setConfigurationSetting("page_id", $_POST['data']['page_id']);
    }
    
    public function setModal() {
        $this->setConfigurationSetting("type", "link_to_modal");
        $this->setConfigurationSetting("page_id", $_POST['data']['link']);
    }

    public function getText() {
        $text = $this->getConfigurationSetting("text") ? $this->getConfigurationSetting("text") : $this->__f("Button");
        return $text;
    }
    
    public function setToLogout() {
        $this->setConfigurationSetting("type", "logout");
    }
    
    public function setGoBack() {
        $this->setConfigurationSetting("type", "goback");
    }
    
    public function saveEmailConfig() {
        $this->setConfigurationSetting("type", "send_email");
        $this->setConfigurationSetting("successMessage", $_POST['data']['successMessage']);
        $this->setConfigurationSetting("subject", $_POST['data']['subject']);
        $this->setConfigurationSetting("content", $_POST['data']['content']);
        $this->setConfigurationSetting("to", $_POST['data']['to']);   
        $this->setConfigurationSetting("sendToUser", $_POST['data']['sendToUser']);   
    }
    
    public function sendMail() {
        $to = $this->getConfigurationSetting("to");
        $subject = $this->getConfigurationSetting("subject");
        $content = $this->getConfigurationSetting("content");
        $from = $this->getFactory()->getStoreConfiguration()->emailAdress;
        $content = $this->manipulateString($content);
        $subject = $this->manipulateString($subject);
        $fromName = $this->getFactory()->getStoreConfiguration()->shopName;
        $this->getApi()->getMessageManager()->sendMail($to, $to, $subject, $content, $from, $fromName);
        
        $user = \ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::getUserObject();
        if ($user && $this->getConfigurationSetting("sendToUser") == "true") {
            $this->getApi()->getMessageManager()->sendMail($user->emailAddress, $user->fullName, $subject, $content, $from, $fromName);
        }
    }

    public function manipulateString($content) {
        $user = \ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::getUserObject();
        $content = str_replace("{User.FullName}", @$user->fullName, $content);
        $content = str_replace("{User.Company.Name}", @$user->companyObject->name, $content);
        $content = str_replace("{User.Company.Vatnumber}", @$user->companyObject->vatNumber, $content);
        $content = str_replace("{User.Email}", @$user->emailAddress, $content);
        $content = str_replace("{User.Cellphone}", @$user->cellPhone, $content);
        $content = str_replace("{User.Company.Address}", @$user->companyObject->address->address, $content);
        $content = str_replace("{User.Company.Postnumber}", @$user->companyObject->address->postCode, $content);
        $content = str_replace("{User.Company.City}", @$user->companyObject->address->city, $content);
        
        return $content;
    }

}
