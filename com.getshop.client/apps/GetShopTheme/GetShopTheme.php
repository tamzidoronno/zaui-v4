<?php
namespace ns_7a4f3750_895a_11e2_9e96_0800200c9a66
;
class GetShopTheme extends \ThemeApplication {
    
    public function getDescription() {
        return $this->__o("This design is private to getshop only.");
    }
    
    public function __construct() {
        $this->setTotalWidth(1200);
        $this->setWidthLeft(204);
        $this->setWidthRight(204);
        $this->setWidthMidle(670);
        $this->setHeaderHeight(200);
        $this->setFooterHeight(200);
        
        // LOGO
        $this->setVariable('ns_974beda7_eb6e_4474_b991_5dbc9d24db8e', 'height', 100);
        $this->setVariable('ns_974beda7_eb6e_4474_b991_5dbc9d24db8e', 'width', 100);
        
        // LeftMenu
        $this->setVariable('ns_00d8f5ce_ed17_4098_8925_5697f6159f66', 'canUploadIcons', true);
        $this->setVariable('ns_00d8f5ce_ed17_4098_8925_5697f6159f66', 'ICON_WIDTH', 40);
        $this->setVariable('ns_00d8f5ce_ed17_4098_8925_5697f6159f66', 'ICON_HEIGHT', 40);
    }
    
    public function addScripts() {
        echo '<link href="http://fonts.googleapis.com/css?family=Roboto" rel="stylesheet" type="text/css">';
        echo '<link href="http://fonts.googleapis.com/css?family=Lobster" rel="stylesheet" type="text/css">';
    }

}
?>