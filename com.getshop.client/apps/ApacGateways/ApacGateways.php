<?php
namespace ns_2d6a27b9_b238_4406_9f03_c4ca8184f590;

class ApacGateways extends \MarketingApplication implements \Application {
    private $currentServer;
    
    public function getDescription() {
        
    }

    public function getName() {
        return "ApacGateways";
    }

    public function assignRoute() {
        $routing = array();
        if(isset($_POST['data']['node1']) && $_POST['data']['node1']) { $routing[] = $_POST['data']['node1']; } else { $routing[] = "0"; } 
        if(isset($_POST['data']['node2']) && $_POST['data']['node2']) { $routing[] = $_POST['data']['node2']; } else { $routing[] = "0"; } 
        if(isset($_POST['data']['node3']) && $_POST['data']['node3']) { $routing[] = $_POST['data']['node3']; } else { $routing[] = "0"; } 
        if(isset($_POST['data']['node4']) && $_POST['data']['node4']) { $routing[] = $_POST['data']['node4']; } else { $routing[] = "0"; } 
        
        $lock = $this->getApi()->getGetShopLockSystemManager()->getLock($_POST['data']['serverid'], $_POST['data']['root']);
        $lock->routing = $routing;
        $this->getApi()->getGetShopLockSystemManager()->saveLocstarLock($_POST['data']['serverid'], $lock);
        $this->getApi()->getGetShopLockSystemManager()->updateZwaveRoute($_POST['data']['serverid'], $_POST['data']['root']);
    }
    
    public function renameNode() {
        $serverId = $_POST['data']['serverid'];
        $path = "ZWave.zway/Run/devices[". $_POST['data']['nodeid']."].data.givenName.value=\"".$_POST['data']['name']."\"";
//        $path = "ZWaveAPI/Run/devices[". $_POST['data']['nodeid']."].data.givenName.value=".urlencode($_POST['data']['name']);
        $this->getApi()->getGetShopLockSystemManager()->restCall($serverId, $path);
        echo $serverId . " : " . $path;
    }
    
    public function doRestCall() {
        $path = $_POST['data']['path'];
        echo $this->getApi()->getGetShopLockSystemManager()->restCall($_POST['data']['id'], $path);
    }

    public function startInclusion() {
        $_POST['data']['id'] = $_POST['data']['serverid'];
        $this->loadCurrentServer();
        echo $this->getApi()->getGetShopLockSystemManager()->restCall($this->getServer()->id, "ZWave.zway/Run/controller.AddNodeToNetwork(1)");
    }
    public function stopInclusion() {
        $_POST['data']['id'] = $_POST['data']['serverid'];
        $this->loadCurrentServer();
        echo $this->getApi()->getGetShopLockSystemManager()->restCall($this->getServer()->id, "ZWave.zway/Run/controller.AddNodeToNetwork(0)");
    }

    public function startExclusion() {
        $_POST['data']['id'] = $_POST['data']['serverid'];
        $this->loadCurrentServer();
        echo $this->getApi()->getGetShopLockSystemManager()->restCall($this->getServer()->id, "ZWave.zway/Run/controller.RemoveNodeFromNetwork(1)");
    }
    public function stopExclusion() {
        $_POST['data']['id'] = $_POST['data']['serverid'];
        $this->loadCurrentServer();
        echo $this->getApi()->getGetShopLockSystemManager()->restCall($this->getServer()->id, "ZWaveZWave.zway/Run/controller.RemoveNodeFromNetwork(0)");
    }
    
    public function render() {
        $this->printServerList();
        $this->includefile("addnewserver");
    }
    
    public function saveConnectionDetails() {
        $this->getApi()->getGetShopLockSystemManager()->updateConnectionDetails(
                $_POST['data']['serverid'],
                $_POST['data']['hostname'], 
                $_POST['data']['username'], 
                $_POST['data']['password'], 
                $_POST['data']['servername'],
                $_POST['data']['token']);
    }
    
    public function createNewServer() {
        $this->getApi()->getGetShopLockSystemManager()->createServer(
                $_POST['data']['type'], 
                $_POST['data']['hostname'], 
                $_POST['data']['username'], 
                $_POST['data']['password'], 
                $_POST['data']['name'],
                $_POST['data']['token']);
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
    
    public function toggleUpdate() {
        $this->getApi()->getGetShopLockSystemManager()->toggleActiveServer($_POST['data']['serverid']);
    }
}
?>
