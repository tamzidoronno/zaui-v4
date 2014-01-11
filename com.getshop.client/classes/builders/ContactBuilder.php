<?php

class ContactBuilder {
    private $factory;
    private $page;
    
    /**
     * @param Factory $factory
     * @param core_pagemanager_data_Page $page
     */
    function __construct($factory, $page) {
        $this->factory = $factory;
        $this->page = $page;
    }

    function buildPage($type) {
        $this->setLayout($type);
        $this->populateLayout($type);
        $this->factory->initPage();
    }

    public function populateLayout($type) {
        $siteBuilder = new SiteBuilder();
        $siteBuilder->clearPage();
        //Adding the image
        switch($type) {
            case 0:
            case 2:
            case 3:
                $siteBuilder->addImageDisplayer("8d566b68-6b2c-4816-82e9-56189b9b1c9a", "col_2");
                break;
            case 1:
            case 4:
                $siteBuilder->addImageDisplayer("8d566b68-6b2c-4816-82e9-56189b9b1c9a", "col_1");
                break;
        }
        
        //Adding the content manager
        switch($type) {
            case 0:
            case 2:
                $siteBuilder->addContentManager($this->getContactText(), "col_1");
                break;
            case 1:
                $siteBuilder->addContentManager($this->getContactText(), "col_2");
                break;
            case 3:
            case 4:
                $siteBuilder->addContentManager($this->getContactText(), "main_1");
                break;
        }
        
        
        //Adding the content manager
        switch($type) {
            case 0:
            case 1:
            case 2:
                $siteBuilder->addContactForm("main_1");
                break;
            case 3:
                $siteBuilder->addContactForm("col_1");
                break;
            case 4:
                $siteBuilder->addContactForm("col_2");
                break;
        }
    }
    
    public function setLayout($type) {
        $type=(int)$type;
        $pb = new PageBuilder(null,null,null);
        switch($type) {
            case 0:
            case 1:
            case 3:
            case 4:
                $layout = $pb->convertToNewLayout(13);
                break;
            case 2:
                $layout = $pb->convertToNewLayout(27);
                break;
        }
        $this->page->layout = $layout;
        $this->factory->getApi()->getPageManager()->savePage($this->page);
    }

    public function getContactText() {
        $text = "<h1>We are always here for you</h1>";
        $text .= "Phone : xxxXXXxxxXXX<br>";
        $text .= "Email : my@email.com<br>";
        $text .= "Contact person : My name<br>";
        return $text;
    }
    
}

?>
