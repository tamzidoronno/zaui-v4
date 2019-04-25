<?php
namespace ns_8edb700e_b486_47ac_a05f_c61967a734b1;

class IntegratedPaymentTerminal extends \PaymentApplication implements \Application {

    public function getName() {
        return "Integrated payment terminal";
    }

    public function getDescription() {
        return "Connected an integrated nets payment terminal.";
    }
    
    public function render() {
        
    }
    
    public function getIcon() {
        return "terminal.svg";
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
        $this->setConfigurationSetting("token0", $_POST['token0']);
        $this->setConfigurationSetting("token1", $_POST['token1']);
        $this->setConfigurationSetting("token2", $_POST['token2']);
        $this->setConfigurationSetting("token3", $_POST['token3']);
        $this->setConfigurationSetting("token4", $_POST['token4']);
    }
    
        
    /**
     * Should display you configuration page.
     */
    public function renderConfig() {
        $this->includefile("terminalconfig");
    }
    
    
}
?>
