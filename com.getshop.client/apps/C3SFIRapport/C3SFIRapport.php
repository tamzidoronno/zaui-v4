<?php
namespace ns_00c40488_e21c_4e54_bed4_8043df326268;

class C3SFIRapport extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "C3SFIRapport";
    }

    public function render() {
        if (!isset($_SESSION['selected_partner'])) {
            $this->includefile('selectpartner');
        }
        
        if (isset($_SESSION['selected_partner']) && !isset($_SESSION['selected_projectid'])) {
            $this->includefile('selectProject');
        }
        
        echo "<div gsclick='clearIt'>Clear</div>";  
        
        if (isset($_SESSION['selected_partner']) && isset($_SESSION['selected_projectid'])) {
            $this->includefile('sfireport');
        }
        
        
    }
    
    public function clearIt() {
        unset($_SESSION['selected_partner']);
        unset($_SESSION['selected_projectid']);
    }
    
    public function setSelectedCompany() {
        $_SESSION['selected_partner'] = $_POST['data']['value'];
    }
    
    public function setSelectedProject() {
        $_SESSION['selected_projectid'] = $_POST['data']['value'];
    }

    public function getPartnerName() {
        return $this->getApi()->getUserManager()->getCompany($_SESSION['selected_partner'])->name;
    }

}
?>
