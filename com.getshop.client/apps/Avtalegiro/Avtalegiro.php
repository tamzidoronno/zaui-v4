<?php
namespace ns_8f5f7f8f_de42_4867_82cc_63eb0cb55fa1;

class Avtalegiro extends \PaymentApplication implements \Application {
    public function getDescription() {
        return $this->__w("Setup for norwegian avtalegiro.");
    }

    public function getName() {
        return "Avtalegiro";
    }

    public function render() {
        
    }
    
    public function renderConfig() {
        $this->includefile("avtalegiroconfig");
    }
    
    /**
     * 
     * @param type $userId
     * @param type $type 1 = Avtalegiro, 2 = Efaktura b2c, 3 = Avtalegiro+efakturab2c.
     */
    public function createLink($userId, $type = 1) {
        $user = $this->getApi()->getUserManager()->getUserById($userId);
        $url = "https://pvu.nets.no/pvu/pvu.do?";
        switch($type) {
            case "2":
                $url = "https://pvu.nets.no/pvuefaktura/logon.do?";
                break;
            case "3":
                $url = "https://pvu.nets.no/pvuatgefa/atgefa.do?";
                break;
        }
        
        $incCustomerId = $user->customerId;
        for($i = 0; $i <= 7-strlen($incCustomerId); $i++) {
            $incCustomerId = "0" . $incCustomerId;
        }
        $kid = $this->mod10($incCustomerId);
        
        $url .= "&account=".$this->getConfigurationSetting("account");
        $url .= "&name=".$this->getConfigurationSetting("name");
        $url .= "&limit=".$this->getConfigurationSetting("limit");
        $url .= "&utstederid=".$this->getConfigurationSetting("utstederid");
        $url .= "&brandid=".$this->getConfigurationSetting("brandid");
        $url .= "&kid=".$kid;
        $url .= "&wi=f";
        if($user->emailAddressToInvoice) {
            $url .= "&epost=".$user->emailAddressToInvoice;
        } else {
            $url .= "&epost=".$user->emailAddress;
        }
        $url .= "&url=".$this->getConfigurationSetting("url");
        
        return $url;
    }
    
    function mod10($kundenr) {
        $kundenrsnudd = strrev($kundenr);
        $splitstring = str_split($kundenrsnudd);
        $beregn = $splitstring[0]*2+$splitstring[1]*3+$splitstring[2]*4+$splitstring[3]*5+$splitstring[4]*6+$splitstring[5]*7+$splitstring[6]*2;
        $tall_del = $beregn/11;
        $tall_mellom = explode('.', $tall_del);
        $tall2 = $tall_mellom[0];
        $deletall = 11*$tall2;
        $tallberegn = $beregn - $deletall;
        $kontrollsiffer = 11-$tallberegn;

        if($kontrollsiffer == 1)
        {
         $kontrollsifferet = "-";
        }
        elseif($kontrollsiffer == 10)
        {
         $kontrollsifferet = "-";
        }
        else
        {
         $kontrollsifferet = $kontrollsiffer;
        }

        $array = array($kundenr. $kontrollsifferet);
        $kidnr = implode(".", $array);
        return $kidnr;
}
    
    public function save() {
        $this->setConfigurationSetting("account", $_POST['account']);
        $this->setConfigurationSetting("name", $_POST['name']);
        $this->setConfigurationSetting("limit", $_POST['limit']);
        $this->setConfigurationSetting("utstederid", $_POST['utstederid']);
        $this->setConfigurationSetting("brandid", $_POST['brandid']);
        $this->setConfigurationSetting("url", $_POST['url']);
    }
    
}
?>
