<?php
namespace ns_2d6a27b9_b238_4406_9f03_c4ca8184f590;

class ApacGateways extends \MarketingApplication implements \Application {
    private $currentServer;
    
    public function getDescription() {
        
    }

    public function getName() {
        return "ApacGateways";
    }

    public function render() {
        $this->printServerList();
        $this->includefile("addnewserver");
    }
    
    public function createNewServer() {
        $this->getApi()->getGetShopLockSystemManager()->createServer(
                $_POST['data']['type'], 
                $_POST['data']['hostname'], 
                $_POST['data']['username'], 
                $_POST['data']['password'], 
                $_POST['data']['name']);
    }

    public function formatEditButton($server) {
        return "<i class='fa fa-edit' gsclick='showServer' serverid='$server->id'></i>";
    }
    
    
    public function GetShopLockSystemManager_getLockServers() {
        $this->loadCurrentServer();
        $this->includefile("server");
    }
    
    public function getServer() {
        return $this->currentServer ;
    }
    
    public function startFetchingOfLocks() {
        $this->getApi()->getGetShopLockSystemManager()->startFetchingOfLocksFromServer($_POST['data']['serverid']);
    }
    
    public function deleteServer() {
        $this->getApi()->getGetShopLockSystemManager()->deleteServer($_POST['data']['serverid']);
    }
    
    public function printServerList() {
        $args = array();
        
        $attributes = array(
            array("editbutton", "", 'editbutton', 'formatEditButton'),
            array('id', 'gs_hidden', 'id'),
            array('givenname', 'Server', 'givenName'),
            array('hostname', 'Hostname', 'hostname'),
            array('username', 'Username', 'username'),
            array('password', 'Password', 'password')
        );
        
        $table = new \GetShopModuleTable($this, 'GetShopLockSystemManager', 'getLockServers', $args, $attributes);
        $table->render();
    }

    public function loadCurrentServer() {
        $this->servers = $this->getApi()->getGetShopLockSystemManager()->getLockServers();
        foreach ($this->servers as $server) {
            if ($server->id == $_POST['data']['id']) {
                $this->currentServer = $server;
            }
        }
    }

    public function runCheck() {
        $this->getApi()->getGetShopLockSystemManager()->triggerCheckOfCodes($_POST['data']['serverid']);
    }
}
?>
