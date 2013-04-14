<?php
class LogoManager extends TestBase {
    function LogoManager($api) {
        $this->api = $api;
    }
    
    
    /**
     * Set a reference to the logo.
     */
    function test_setLogo() {
        $manager = $this->getApi()->getLogoManager();
        
        //Set a logo, the id of the logo, this id (reference) should be
        //handled by whatever handling uploading files.
        $logoid = "some_reference";
        $manager->setLogo($logoid);
    }
    
    /**
     * Get the logo reference.
     */
    function test_getLogo() {
        $manager = $this->getApi()->getLogoManager();
        
        //First set one
        $manager->setLogo("somelogoid");
        
        //Now fetch it.
        $logoid = $manager->getLogo();
    }
    
    /**
     * Delete the current logo.
     */
    function test_deleteLogo() {
        $manager = $this->getApi()->getLogoManager();
        
        //First set one
        $manager->setLogo("somelogoid");
        
        //Now delete it.
        $logoid = $manager->deleteLogo();
    }
}

?>
