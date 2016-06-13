<?php
namespace ns_9a07412b_a112_4803_8160_2eb11f2c5df2;

class C3EventRegistration extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "C3EventRegistration";
    }

    public function render() {
        echo "<table>";
        echo "<tr><td>Tid</td><td>".$this->getConfigurationSetting("time")."</td></tr>";
        echo "<tr><td>Sted</td><td>".$this->getConfigurationSetting("location")."</td></tr>";
        echo "<tr><td>Meld på</td><td><a href='".$this->getRegisterEmail()."'>klikk her</a></td></tr>";
        echo "</table>";
    }

    public function getRegisterEmail() {
        $reg = $this->getConfigurationSetting("register");
        if(stristr($reg, "@")) {
            return "mailto:" . $reg . "?subject=" . $this->getConfigurationSetting("email_subject");
        }
        
        return $reg;
    }

}
?>
