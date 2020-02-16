<?php
namespace ns_df05feab_f657_49ee_a338_82d5f8c14ed5;

class ApacGeneralSettings extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "ApacGeneralSettings";
    }

    public function render() {
        $this->includefile("settings");
        $this->showTransferButton();
    }

    public function showTransferButton() {
//       $domainName = "default";
//        $config = $this->getApi()->getPmsManager()->getConfiguration($domainName);
//        $lockservers = $config->lockServerConfigs; 
//        $newLockServers = $this->getApi()->getGetShopLockSystemManager()->getLockServers();
//        
//        $items = $this->getApi()->getBookingEngine()->getBookingItems($domainName);
//        
//        foreach ($items as $item) {
//            $lock = $this->getLockByItem($item, $lockservers, $domainName);
//            if ($lock) {
//                $matchingLock = $this->getMatchingLock($lock, $newLockServers); 
//            }
//        }
    }
    
    public function transferGateways() {
        $domainName = "default";
        $config = $this->getApi()->getPmsManager()->getConfiguration($domainName);
        $lockservers = $config->lockServerConfigs; 
        $newLockServers = $this->getApi()->getGetShopLockSystemManager()->getLockServers();
       
        foreach ($lockservers as $server) {
            $type = $server->locktype == "getshophotellock" ? "zwaveserver" : "getshoplockbox";
            
            $this->getApi()->getGetShopLockSystemManager()->createServer(
                $type, 
                $server->arxHostname, 
                $server->arxUsername, 
                $server->arxPassword, 
                $server->serverSource, 
                "");
        }
        
        $servers = $this->getApi()->getGetShopLockSystemManager()->getLockServers();
        foreach ($servers as $server) {
            $this->getApi()->getGetShopLockSystemManager()->startFetchingOfLocksFromServer($server->id);
        }
    }
    
    public function updateFromOldSystem() {
        $domainName = "default";
        $config = $this->getApi()->getPmsManager()->getConfiguration($domainName);
        $lockservers = $config->lockServerConfigs; 
        $newLockServers = $this->getApi()->getGetShopLockSystemManager()->getLockServers();

        // Start transfer of codes and access groups
        $items = $this->getApi()->getBookingEngine()->getBookingItems($domainName);
         
        // Delete Old Groups
        $oldGroups = $this->getApi()->getGetShopLockSystemManager()->getAllGroups();
        foreach ($oldGroups as $group) {
            $this->getApi()->getGetShopLockSystemManager()->deleteGroup($group->id);
        }
        
        $allLocks = array();
        foreach ($lockservers as $server) {
            $allLocks[$server->serverSource] = $this->getApi()->getGetShopLockManager()->getAllLocks($domainName, $server->serverSource);
        }
        $codeSize = $this->getApi()->getGetShopLockSystemManager()->getCodeSize();
        $newMasterGroup = $this->getApi()->getGetShopLockSystemManager()->createNewLockGroup("Master Codes", 5, $codeSize);
        
        // Connect all locks to mastergroup
        $setGroup = array();
        foreach ($items as $item) {
            $lockConfig = $this->getLockByItem($item, $lockservers, $domainName, $allLocks);
            $lock = $lockConfig[0];
            $lockServer = $lockConfig[1];
            if (!$lock)
                continue;
            
            $matchingLock = $this->getMatchingLock($lock, $lockServer, $newLockServers); 
            if (!isset($setGroup[$matchingLock->connectedToServerId])) {
                $setGroup[$matchingLock->connectedToServerId] = array();
            }
            $setGroup[$matchingLock->connectedToServerId][] = $matchingLock->id;
        }
        
        foreach ($newLockServers as $newLockServer) {
            if ($newLockServer->className == "com.thundashop.core.getshoplocksystem.GetShopLockBoxServer") {
                foreach ($newLockServer->locks as $extraLock) {
                    $setGroup[$newLockServer->id][] = $extraLock->id;
                }
            }
        }
        $this->getApi()->getGetShopLockSystemManager()->setLocksToGroup($newMasterGroup->id, $setGroup);
        
        // Set the mastercodes from the first lock.
        foreach ($items as $item) {
            $lock = null;
            $setGroup = array();
            $lockConfig = $this->getLockByItem($item, $lockservers, $domainName, $allLocks);
            $lock = $lockConfig[0];
            $lockServer = $lockConfig[1];
            if (!$lock)
                continue;
            
            for ($i=1;$i<=5;$i++) {
                $code = $lock->codes->{$i};
                $this->getApi()->getGetShopLockSystemManager()->changeCode($newMasterGroup->id, $i, $code->code, "");
            }
            
            break;
        }
        
        // Update if mastercode transferred to lock.
        foreach ($items as $item) {
            $lock = null;
            $setGroup = array();
            $lockConfig = $this->getLockByItem($item, $lockservers, $domainName, $allLocks);
            $lock = $lockConfig[0];
            $lockServer = $lockConfig[1];
            
            if (!$lock)
                continue;
            
            $matchingLock = $this->getMatchingLock($lock, $lockServer, $newLockServers); 
            
            for ($i=1;$i<=5;$i++) {
                $code = $lock->codes->{$i};
                if ($code->addedToLock && isset($lock->zwaveid)) {
                    $this->getApi()->getGetShopLockSystemManager()->markCodeAsUpdatedOnLock($matchingLock->connectedToServerId, $matchingLock->id, ($i));
                }
            }
        }
        
        // SET USER 
        foreach ($items as $item) {
            $lock = null;
            $setGroup = array();
            $lockConfig = $this->getLockByItem($item, $lockservers, $domainName, $allLocks);
            $lock = $lockConfig[0];
            $lockServer = $lockConfig[1];
            if (!$lock)
                continue;
            
            $lockGroup = $this->getApi()->getGetShopLockSystemManager()->createNewLockGroup($item->bookingItemName, 15, $codeSize);
            
            $matchingLock = $this->getMatchingLock($lock, $lockServer, $newLockServers); 
            $setGroup = array();
            $setGroup[$matchingLock->connectedToServerId] = array();
            $setGroup[$matchingLock->connectedToServerId][] = $matchingLock->id;
            foreach ($newLockServers as $newLockServer) {
                if ($newLockServer->className == "com.thundashop.core.getshoplocksystem.GetShopLockBoxServer") {
                    foreach ($newLockServer->locks as $extraLock) {
                        $setGroup[$newLockServer->id][] = $extraLock->id;
                    }
                }
            }
            $this->getApi()->getGetShopLockSystemManager()->setLocksToGroup($lockGroup->id, $setGroup);
            
            $item->lockGroupId = $lockGroup->id;
            $this->getApi()->getBookingEngine()->saveBookingItem($domainName, $item);
            $lockGroup = $this->getApi()->getGetShopLockSystemManager()->getGroup($lockGroup->id);
            
            for ($i=1;$i<=15;$i++) {
                $code = $lock->codes->{$i+5};
                $this->getApi()->getGetShopLockSystemManager()->changeCode($lockGroup->id, ($i), $code->code, "");
                if ($code->addedToLock) {
                    $this->markCodeAsAdded($lockGroup, ($i));
                }
            }
        }
    }

    public function getLockByItem($item, $lockservers, $domainName, $allLocks) {
        foreach ($lockservers as $server) {
            $locks = $allLocks[$server->serverSource];
            foreach ($locks as $lock) {
                if ($item->bookingItemAlias == $lock->id) {
                    return array($lock, $server);
                }
            }
        }
        
        return null;
    }

    public function getMatchingLock($lock, $server, $newLockServers) {
        if (!$lock) {
            return;
        }
        foreach ($newLockServers as $lockServer) {
            if ($lockServer->givenName == $server->serverSource)
            foreach ($lockServer->locks as $newLock) {
                if (!isset($lock->zwaveid))
                    continue;
                
                if ($newLock->zwaveDeviceId == $lock->zwaveid) {
                    return $newLock;
                }
            }
        }
    }

    public function markCodeAsAdded($lockGroup, $slotId) {
        $masterSlot = $lockGroup->groupLockCodes->{$slotId};
        foreach ($masterSlot->subSlots as $subSlot) {
            $this->getApi()->getGetShopLockSystemManager()->markCodeAsUpdatedOnLock($subSlot->connectedToServerId, $subSlot->connectedToLockId, $subSlot->slotId);
        }
    }

    public function deleteOldLockSystem() {
        $domainName = "default";
        $config = $this->getApi()->getPmsManager()->getConfiguration($domainName);
        $config->lockServerConfigs = array();
        $this->getApi()->getPmsManager()->saveConfiguration($domainName, $config);
    }
    
    public function transferActiveBookings() {
        $domainName = "default";
        $filter = new \core_pmsmanager_PmsBookingFilter();
        $filter->filterType = "active";
        $filter->startDate = $this->convertToJavaDate(time());
        $filter->endDate = $this->convertToJavaDate(time());
        
        $activeBookings = $this->getApi()->getPmsManager()->getAllBookings($domainName, $filter);
        
        foreach ($activeBookings as $activeBooking) {
            foreach ($activeBooking->rooms as $room) {
                $this->getApi()->getPmsManager()->transferFromOldCodeToNew($domainName, $room->pmsBookingRoomId);
            }
        }
    }
    
    public function setCodeSize() {
        $this->getApi()->getGetShopLockSystemManager()->setCodeSize($_POST['data']['codeSize']);
    }
}
?>
