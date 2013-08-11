<?php

function sortApplicationSequence($object1, $object2) {
    $a = 0;
    $b = 0;
    if(isset($object1->sequence))
        $a = $object1->sequence;
    if(isset($object2->sequence))
        $b = $object2->sequence;
    
    if ($a == $b) {
        return 0;
    }
    
    return ($a < $b) ? -1 : 1;
}

/**
 * Description of PageArea
 *
 * @author ktonder
 */
class PageArea extends FactoryBase {
    private $page;
    /** @var core_pagemanager_data_PageArea */
    private $backendPageArea;
    private $applications = array();
    private $systemArea = array('header', 'footer');
    
    public function getApplication($id) {
        return $this->applications[$id];
    }
    
    function __construct(Page $page, $backendPageArea) {
        $this->backendPageArea = $backendPageArea;
        $this->page = $page;
        $this->initApps();
    }
    
    private function getSortedApps() {
        if ($this->backendPageArea->type == "header") {
            $this->applications[0] = new \ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login();
        }
        
        $apps = $this->backendPageArea->applications;
        $copy = array();
        
        foreach ($apps as $id => $value) {
            $copy[$id] = $value;
        }
        
        usort($copy, "sortApplicationSequence");
        return $copy;
    }
    
    private function initApps() {
        foreach ($this->getSortedApps() as $app) {
            $appInstance = IocContainer::getFactorySingelton()->getApplicationPool()->getApplicationInstance($app->id);
            if ($appInstance) {
                $this->applications[$app->id] = $appInstance;
            }
        }
    }
    
    private function showAccessDeniedMessage() {
        $user = \ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::getUserObject();
        $userLevel = $this->page->getUserLevel();

        if (!$user) {
            $user = new stdClass();
            $user->type = 0;
        }

        if($userLevel > $user->type) {
            echo "<div class='accessdenied'>";
            if($userLevel == 100) {
                echo $this->__w("This page is only available for adminsitrators");
            } else if($userLevel == 50) {
                echo $this->__w("This page is only available for editors.");
            } else if($userLevel == 10) {
                echo $this->__w("This page is only available for registered users.");
            } else {
                echo "Access denied";
            }
            $this->applications[0] = new \ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login();
            echo "</div>";
        }
    }
    
    public function render() {
        $this->includefile('applicationareamenu');
        $this->includefile('add_application_menu');

        if($this->backendPageArea->type == "middle") 
            $this->showAccessDeniedMessage();
        
        $isEmpty = $this->checkIfEmptyPage();
        $this->includefile($this->backendPageArea->type);
    }
    
    /**
     * Returns available applications
     * 
     * @return type 
     */
    public function getAvailableApplications() {
        return AppRegister::$register;
    }
    
    public function getApplications() {
        return $this->applications;
    }

    /**
     * Gets the backend page area.
     * 
     * @return core_pagemanager_data_PageArea 
     */
    public function getBackendPageArea() {
        return $this->backendPageArea;
    }

    public function hasApplication($id) {
        return array_key_exists($id, $this->applications);
    }

    public function checkIfEmptyPage() {
        if(sizeof((array)$this->backendPageArea->applications) == 0) {
            return true;
        }
        return false;
    }
    
    public function shouldDisplayAddMenu() {
        if (!$this->isEditorMode())
            return false;
        
        if (!$this->checkIfEmptyPage())
            return false;
        
        if ($this->backendPageArea->type == "middle")
            return true;
        
        if ($this->backendPageArea->type == "left")
            return true;
        
        if ($this->backendPageArea->type == "right")
            return true;
        
        return false;
    }

    public function remove($param0) {
        unset($this->applications[$param0]);
    }

    public function isSystemArea() {
        return in_array($this->backendPageArea->type, $this->systemArea);
    }
    
    public function getType() {
        return $this->backendPageArea->type;
    }
}

?>
