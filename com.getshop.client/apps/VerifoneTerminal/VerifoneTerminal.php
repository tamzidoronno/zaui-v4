<?php
namespace ns_6dfcf735_238f_44e1_9086_b2d9bb4fdff2;

class VerifoneTerminal extends \PaymentApplication implements \Application {
    public function getDescription() {
        return "We have a terminal connected directly to our booking system. Whenever paying with a verifone payment terminal you will automatically mark payments and completed. This integration is complex and needs a getshop expert to complete.";
    }

    public function getName() {
        return "Verifone terminal";
    }

    /**
     * Should display you configuration page.
     */
    public function renderConfig() {
        $this->includefile("terminalconfig");
    }
    
    public function render() {
        
    }
    
    public function getIds() {
        $ids = $this->getConfigurationSetting("terminalids");
        if(!$ids) {
            $ids = array();
            for($i = 0; $i < 5; $i++) {
                $ids[$i] = $this->v4();
            }
            $this->setConfigurationSetting("terminalids", json_encode($ids));
        } else {
            $ids = json_decode($this->getConfigurationSetting("terminalids"));
        }
        return $ids;
    }

    public function v4() {
    return sprintf('%04x%04x-%04x-%04x-%04x-%04x%04x%04x',

      // 32 bits for "time_low"
      mt_rand(0, 0xffff), mt_rand(0, 0xffff),

      // 16 bits for "time_mid"
      mt_rand(0, 0xffff),

      // 16 bits for "time_hi_and_version",
      // four most significant bits holds version number 4
      mt_rand(0, 0x0fff) | 0x4000,

      // 16 bits, 8 bits for "clk_seq_hi_res",
      // 8 bits for "clk_seq_low",
      // two most significant bits holds zero and one for variant DCE1.1
      mt_rand(0, 0x3fff) | 0x8000,

      // 48 bits for "node"
      mt_rand(0, 0xffff), mt_rand(0, 0xffff), mt_rand(0, 0xffff)
    );
  }    
    
    
    
    public function saveSettings() {
        $this->setConfigurationSetting("ipaddr0", $_POST['ipaddr0']);
        $this->setConfigurationSetting("ipaddr1", $_POST['ipaddr1']);
        $this->setConfigurationSetting("ipaddr2", $_POST['ipaddr2']);
        $this->setConfigurationSetting("ipaddr3", $_POST['ipaddr3']);
        $this->setConfigurationSetting("ipaddr4", $_POST['ipaddr4']);
    }
    
    public function getColor() {
        return "green";
    }
}
?>
