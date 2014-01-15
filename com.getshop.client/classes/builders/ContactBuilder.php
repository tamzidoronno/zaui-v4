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

        $this->addImage($type, $siteBuilder);
        $this->addContactForm($type, $siteBuilder);
        $this->addTextContent($type, $siteBuilder);
        $this->addMapContent($type, $siteBuilder);
    }

    public function setLayout($type) {
        $type = (int) $type;
        $pb = new PageBuilder(null, null, null);
        switch ($type) {
            case 0:
            case 1:
            case 3:
            case 4:
            case 16:
            case 17:
                $layout = $pb->convertToNewLayout(13);
                break;
            case 10:
            case 11:
            case 13:
            case 14:
            case 2:
                $layout = $pb->convertToNewLayout(27);
                break;
            case 5:
            case 6:
            case 7:
            case 8:
                $layout = $pb->convertToNewLayout(28);
                break;
            case 9:
            case 12:
            case 15:
                $layout = $pb->convertToNewLayout(29);
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

    public function addImage($type, $siteBuilder) {
        switch ($type) {
            case 0:
            case 2:
            case 3:
            case 6:
            case 10:
                //second column
                $siteBuilder->addImageDisplayer("8d566b68-6b2c-4816-82e9-56189b9b1c9a", "col_2");
                break;
            case 1:
            case 4:
            case 5:
                //First column
                $siteBuilder->addImageDisplayer("8d566b68-6b2c-4816-82e9-56189b9b1c9a", "col_1");
                break;
            case 7:
                //Fourth column
                $siteBuilder->addImageDisplayer("8d566b68-6b2c-4816-82e9-56189b9b1c9a", "col_4");
                break;
            case 8:
                $siteBuilder->addImageDisplayer("8d566b68-6b2c-4816-82e9-56189b9b1c9a", "col_3");
                break;
            case 12:
            case 13:
            case 14:
            case 16:
            case 17:
                //First row
                $siteBuilder->addImageDisplayer("10310e92-2184-4fb0-87d3-ba73739d23f7", "main_1");
                break;
            case 15:
                //Second row
                $siteBuilder->addImageDisplayer("10310e92-2184-4fb0-87d3-ba73739d23f7", "main_2");
                break;
        }
    }

    public function addContactForm($type, $siteBuilder) {
        switch($type) {
            case 0:
            case 1:
            case 2:
            case 15:
                $siteBuilder->addContactForm("main_1");
                break;
            case 3:
            case 8:
            case 10:
                $siteBuilder->addContactForm("col_1");
                break;
            case 4:
            case 5:
            case 7:
            case 11:
            case 13:
            case 14:
            case 16:
            case 17:
                $siteBuilder->addContactForm("col_2");
                break;
            case 6:
                $siteBuilder->addContactForm("col_4");
                break;
            case 9:
            case 12:
                $siteBuilder->addContactForm("main_2");
                break;
                

        }
    }

    public function addTextContent($type, $siteBuilder) {
        switch($type) {
            case 0:
            case 2:
            case 7:
            case 13:
            case 16:
                $siteBuilder->addContentManager($this->getContactText(), "col_1");
                break;
            case 1:
                $siteBuilder->addContentManager($this->getContactText(), "col_2");
                break;
            case 5:
            case 6:
                $siteBuilder->addContentManager($this->getContactText(), "col_3");
                break;
            case 8:
                $siteBuilder->addContentManager($this->getContactText(), "col_4");
                break;
            case 3:
            case 4:
            case 9:
            case 10:
            case 11:
                $siteBuilder->addContentManager($this->getContactText(), "main_1");
                break;
            case 12:
                $siteBuilder->addContentManager($this->getContactText(), "main_2");
                break;
                
                
        }
    }

    /**
     * @param type $type
     * @param SiteBuilder $siteBuilder
     */
    public function addMapContent($type, $siteBuilder) {
        switch($type) {
            case 6:
            case 11:
            case 14:
            case 17:
                $siteBuilder->addMap("col_1");
                break;
            case 8:
                $siteBuilder->addMap("col_2");
                break;
            case 7:
                $siteBuilder->addMap("col_3");
                break;
            case 5:
                $siteBuilder->addMap("col_4");
                break;
        }
    }

}

?>
