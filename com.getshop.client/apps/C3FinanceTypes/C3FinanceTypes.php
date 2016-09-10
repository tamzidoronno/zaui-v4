<?php
namespace ns_2ed184ce_36b6_4462_950d_e1451496c384;

class C3FinanceTypes extends \MarketingApplication implements \Application {

    private $group;

    public function getDescription() {
        
    }

    public function getName() {
        return "C3FinanceTypes";
    }

    public function render() {
        
    }
    
    public function renderGroupInformation($group) {
        $this->group = $group;
        $this->includefile("usergroupsettings");
    }
    
    function getGroup() {
        return $this->group;
    }
    
    public function saveFinance() {
        $this->getApi()->getC3Manager()->saveGroupInfo($_POST['groupid'], "forskning", $_POST['forskning']);
        $this->getApi()->getC3Manager()->saveGroupInfo($_POST['groupid'], "egen", $_POST['egen']);
    }
}
?>
