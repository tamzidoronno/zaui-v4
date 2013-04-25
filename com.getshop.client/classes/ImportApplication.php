<?php
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Description of ImportApplication
 *
 * @author ktonder
 */
class ImportApplication {
    public $appSettingsId;
    public $area;
    
    /** @var Factory */
    public $factory;
    
    function __construct($appSettingsId, $area) {
        $this->appSettingsId = $appSettingsId;
        $this->area = $area;
        $this->factory = IocContainer::getFactorySingelton();
    }

    public function getControlPanel() {
       $apps = $this->factory->getApi()->getPageManager()->getApplicationsByPageAreaAndSettingsId($this->appSettingsId, $this->area);
       $appsDiv = array();
       $i = 0;
       foreach ($apps as $app) {
           $appInstance = $this->factory->getApplicationPool()->createAppInstance($app);
           $appsDiv[] = $this->getHtmlForApp($appInstance, $i);
           $i++;
       }
       echo json_encode($appsDiv);
    }
    
    private function getHtmlForApp($appInstance, $i) {
        ob_start();
        echo "<div class='previewapp'>";
        $appInstance->renderApplication();
        echo "</div>";
        $html = ob_get_contents();
        ob_end_clean();
        return $html;
    }
    
    public function showMenu() {
        echo "<div id='ImportMenu' style=\"background-image: url('/skin/default/elements/ImportButtonBackShadow.png'); padding: 6px;  display:none; position: absolute; z-index: 999999999;\">";
        echo "<div class='inline' id='ImportPrevApp' ></div>";
        echo "<div class='inline' id='ImportNextApp' ></div>";
        echo "<div id='ImportSave'>Save</div>";
        echo "<div id='ImportCancel'>Cancel</div>";
        echo "</div>";
    }
}
?>