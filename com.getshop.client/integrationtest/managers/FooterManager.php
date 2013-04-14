<?php

class FooterManager extends TestBase {

    public function FooterManager($api) {
        $this->api = $api;
    }
    
    /**
     * To fetch the configuration for the footer.
     */
    public function test_getConfiguration() {
        $manager = $this->getApi()->getFooterManager();
        $config = $manager->getConfiguration();
    }
    
    /**
     * Change the number of colums set for a footer
     */
    public function test_setLayout() {
        $manager = $this->getApi()->getFooterManager();
        
        //change it to three columns.
        $manager->setLayout(3);
    }
    
    /**
     * Change the type for one of the columns.
     */
    public function test_setType() {
        $manager = $this->getApi()->getFooterManager();
        $manager->setType(2, 1);
    }

}

?>
