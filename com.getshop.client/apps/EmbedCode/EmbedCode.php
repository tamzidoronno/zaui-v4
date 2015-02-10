<?php
namespace ns_78dcce17_a1c5_4368_94e9_948788235c4e;

class EmbedCode extends \ApplicationBase implements \Application {
    
    public function getDescription() {
        return $this->__w("Embed your own code into the page");
    }

    public function getName() {
        return "EmbedCode";
    }
    
     public function renderOnStartup() {
         echo $this->getCodeStartup();
     }
    

    public function getCode() {
        return $this->getConfigurationSetting("code");
    }
    public function getCodeStartup() {
        return $this->applicationSettings->settings->{"code_startup"}->value;
    }
    
    public function render() {
        $code = $this->getCode();
        if($code) {
            echo $code;
        } else {
            echo $this->__w("No code added yet");
        }
    }
    
    public function loadConfigBox() {
        $this->includefile("configuration");
    }
    
    public function saveCode() {
        $code = $_POST['data']['code'];
        $code_startup = $_POST['data']['code_startup'];
        $this->setConfigurationSetting("code", $code);

        $config = $this->getConfiguration();
        $this->setConfiguration(null);
        $this->setConfigurationSetting("code_startup", $code_startup);
        $this->setConfiguration($config);
    }

}

?>
