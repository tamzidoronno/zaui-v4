<?php
namespace ns_752aee89_0abc_43cf_9067_5aeadfe07cc1;

class PgaMenu extends PgaCommon implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "PgaMenu";
    }

    public function render() {
        $this->includefile("leftmenu");
    }

    
}


?>
