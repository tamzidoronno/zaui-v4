<?php
/**
 * Description of Page
 *
 * @author ktonder
 */
class Page extends FactoryBase {
    private $userLevel = 0;
    public $id;
    public $skeletonType;
    public $areas;
    public $description;
    public $emptySkeleton = false;
    
    public static $systemPages = array("orderoverview", "checkout", "myaccount", "users", "settings", "domain", "cart");

    /** @var core_pagemanager_data_Page */
    private $parentPage;

    public function getApplications() {
        $apps = array();
        foreach ($this->areas as $area) {
            $apps = array_merge($apps, $area->getApplications());
        } 
 
        return $apps;
    }

    public function isSystemPage() {
        return in_array($this->id, Page::$systemPages);
    }
    /**
     * Get the specified area.
     * 
     * @param string $area
     * @return PageArea
     */
    public function getApplicationArea($area) {
        if (!isset($this->areas[$area])) {
            return null;
        }
        return $this->areas[$area];
    }
    
    public function standAloneApp($app) {
        $this->skeletonType = 1;
    }
    
    /**
     * @param core_pagemanager_data_Page $page
     */
    function __construct($page) {
        if (!$page) {
            $this->skeletonType = "NotFound";
        } else {
            $this->parentPage = $page->parent;
            $this->id = $page->id;
            $this->areas = array();
            $this->userLevel = $page->userLevel;
            $this->description = $page->description;
            $this->createAllPageAreas($page);    
            if (!isset($this->userLevel))
                $this->userLevel = 0;
        }
        
    }
    
    private function createAllPageAreas($page) {
        
        foreach ($page->pageAreas as $pagearea) {
            $this->areas[$pagearea->type] = new PageArea($this, $pagearea);
        }
        
        $this->skeletonType = $page->type;
    }

    
    public function getId() {
        return $this->id;
    }

    public function getSkeletonType() {
        return $this->skeletonType;
    }

    public function setSkeletonType($skeletonType) {
        $this->skeletonType = $skeletonType;
    }

    public function getAreas() {
        return $this->areas;
    }

    public function getLeftApplicationArea() {
        return $this->areas['left'];
    }
    
    /**
     * @return PageArea
     */
    public function getMiddleApplicationArea() {
        return $this->areas['middle'];
    }
    
    /**
     * @return PageArea
     */
    public function getSubHeaderArea() {
        return $this->areas['subheader'];
    }
    
    /**
     * @return PageArea
     */
    public function getRightApplicationArea() {
        return $this->areas['right'];
    }
    
    public function loadSkeleton() {
        $this->emptySkeleton = false;
        $editorMode = $this->getFactory()->isEditorMode() ? "editormode" : '';
        echo '<div id="skeleton" class="'.$editorMode.'">';
        $this->includefile('skeleton'.$this->skeletonType);
        echo '</div>';
        if ($this->skeletonType != 5)
            $this->includefile('bottom');
    }
    
     private function getAppAreaHtml($pageArea) {
        if ($pageArea == null) {
            return;
        }
        ob_start();
        $pageArea->render();
        $html = ob_get_contents();
        ob_end_clean();
        return $html;
    }
    
    private function getMainMenuContent() {
        ob_start();
        $this->getFactory()->getApplicationPool()->getApplicationInstance("bf35979f-6965-4fec-9cc4-c42afd3efdd7")->render();
        $html = ob_get_contents();
        ob_end_clean();
        return $html;
    }

    private function getBreadCrumbContent() {
        ob_start();
        $this->getFactory()->getApplicationPool()->getApplicationInstance("7093535d-f842-4746-9256-beff0860dbdf")->render();
        $html = ob_get_contents();
        ob_end_clean();
        return $html;
    }
    
    public function getThemeApplicationSettingsId() {
        $theme = $this->getFactory()->getApplicationPool()->getSelectedThemeApp();
        if ($theme != null) {
            return $theme->getApplicationSettings()->id;
        }
        return "";
    }
    
    private function getSkeletonLayout() {
        ob_start();
        $this->emptySkeleton = true;
        $this->includefile('skeleton'.$this->skeletonType);
        $html = ob_get_contents();
        ob_end_clean();
        return $html;
    }
    
    private function getBottomHtml() {
        ob_start();
        $this->getFactory()->getBottomHtml();
        $html = ob_get_contents();
        ob_end_clean();
        return $html;
    }
    
    public function loadJsonContent() {
        
        
        $contents['skeleton'] = $this->getSkeletonLayout();
        $contents['mainmenu'] = $this->getMainMenuContent();
        $contents['apparea-breadcrumb'] = $this->getBreadCrumbContent();
        
        foreach ($this->areas as $area) {
            $type = $area->getType();
            $contents['apparea-'.$type] = $this->getAppAreaHtml($this->getApplicationArea($type));
        }
        
        if (isset($_GET['page']) && $_GET['page'] == "settings") {
            $contents['apparea-bottom'] = $this->getFactory()->getBottomHtml();
        }
        
        $contents['errors'] = $this->getFactory()->getErrorsHtml();
        $contents['errorCodes'] = $this->getFactory()->getErrorCodes();
        echo json_encode($contents);
    }
    
    public function getApplicationByAppId($id) {
        $pageArea = $this->getApplicationAreaByAppId($id);
        
        if ($pageArea) {
            return $pageArea->getApplication($id);
        }
        
        $factory = IocContainer::getFactorySingelton();
        return $factory->getApplicationPool()->getApplicationInstance($id);
    }
    
    /**
     * Gets the page area by application id.
     * 
     * @param int $id
     * @return PageArea
     */
    public function getApplicationAreaByAppId($id) {
        foreach ($this->areas as $area) {
            if ($area->hasApplication($id)) {
                return $area;
            }
        }
        
        return null;
    }
    
    /**
     * @return core_pagemanager_data_Page 
     */
    public function getParent() {
        return $this->parentPage;
    }
    
    public function getUserLevel() {
        if (!isset($this->userLevel))
            $this->userLevel = 0;
        
        return $this->userLevel;
    }
}

?>
