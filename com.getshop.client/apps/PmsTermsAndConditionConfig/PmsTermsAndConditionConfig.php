<?php
namespace ns_b87159c1_2e10_4be0_a2c8_e702ec92c8eb;

class PmsTermsAndConditionConfig extends \WebshopApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "PmsTermsAndConditionConfig";
    }

    public function saveContent() {
        $langauge = $this->getLanguage();
        $notificationSettings = $this->getApi()->getPmsManager()->getConfiguration($this->getSelectedMultilevelDomainName());
        $notificationSettings->contracts->{$langauge} = $_POST['data']['content'];
        $this->getApi()->getPmsManager()->saveConfiguration($this->getSelectedMultilevelDomainName(), $notificationSettings);
    }
    
    public function changeLanguage() {
        $_SESSION['PmsSendMessagesConfigurationLang'] = $_POST['data']['lang'];
    }
    
    public function getLanguage() {
        if(isset($_SESSION['PmsSendMessagesConfigurationLang'])) {
            return $_SESSION['PmsSendMessagesConfigurationLang'];
        }
        return $this->getFactory()->getMainLanguage();
    }
    
    public function render() {
        $notificationSettings = $this->getApi()->getPmsManager()->getConfiguration($this->getSelectedMultilevelDomainName());
        $langauge = $this->getLanguage();
        $languages = $this->getFactory()->getLanguageCodes();
        $languages[] = $this->getFactory()->getMainLanguage();
        ?>
        <select class='gsniceselect1 changeLanguage' style='width:100%;margin-bottom: 5px;' gsname="language">
            <?php
                foreach($languages as $key) {
                    $selected = ($key == $langauge) ? "SELECTED" : "";
                    echo "<option value='$key' $selected>$key</option>";
                }
            ?>
        </select>

        <div id="contractfield" style="border: solid 1px; min-height: 400px;">
        <?
            if(isset($notificationSettings->contracts->{$langauge})) {
                echo $notificationSettings->contracts->{$langauge};
            }
        ?>
        </div>
        <div style='text-align:right; margin-top: 10px;'>
            <span class='shop_button savetermsandconditions'>Save terms and conditions</span>
        </div>
<br>
<br>
<br>
        <?php
    }
    
    public function saveTermsAndConditions() {
        $langauge = $this->getLanguage();
        $notificationSettings = $this->getApi()->getPmsManager()->getConfiguration($this->getSelectedMultilevelDomainName());
        $notificationSettings->contracts->{$langauge} = $_POST['data']['content'];
        $this->getApi()->getPmsManager()->saveConfiguration($this->getSelectedMultilevelDomainName(), $notificationSettings);
    }
}
?>
