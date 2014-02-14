<?php

namespace ns_1fd7f850_d5c4_11e2_8b8b_0800200c9a66;

/**
 *
 * @author ktonder
 */
class MtbTheme extends \ThemeApplication {
    var $entries;
    var $dept;
    var $currentMenuEntry;
    
    /** @var \GetShopApi */
    private $api;

    public function getDescription() {
    }

    function __construct() {
        $this->api = $this->getApi();

        $this->setTotalWidth(1100);
        $this->setWidthLeft(308);
        $this->setWidthRight(208);
        $this->setWidthMidle(560);
        $this->setHeaderHeight(100);
        $this->setFooterHeight(202); 

        // LOGO
        $this->setVariable('ns_974beda7_eb6e_4474_b991_5dbc9d24db8e', 'height', 110);
        $this->setVariable('ns_974beda7_eb6e_4474_b991_5dbc9d24db8e', 'width', 500);

        $this->setVariable('ns_e9d04f19_6eaa_4a17_9b7b_aa387dbaed92', 'height', 120);
        $this->setVariable('ns_e9d04f19_6eaa_4a17_9b7b_aa387dbaed92', 'width', 120);

        $this->setCategoryColumnCount2Layout(4);
        $this->setCategoryColumnCount3Layout(3);
    }

    private function setColors() {
        $store = $this->getApi()->getStoreManager()->getMyStore();
        $store->configuration->colors->baseColor = "FFFFFF";
        $store->configuration->colors->backgroundColor = "FFFFFF";
        $store->configuration->colors->textColor = "FFFFFF";       
        $store->configuration->colors->buttonBackgroundColor = "0010C4";
        $this->getApi()->getStoreManager()->saveStore($store->configuration);
    }
    
    public function applicationAdded() {
//        $this->setColors();  
        
        if (isset($_POST['data']['prepopulate']) && $_POST['data']['prepopulate'] != "false") {
            $sitebuilder = $this->getSiteBuilder();
            $sitebuilder->clearPage('home');
            $sitebuilder->clearTopMenu();
            $sitebuilder->addBannerSlider("home", dirname(__FILE__) . "/banner/", 400);
            $sitebuilder->addProductList('home', 2, "rowview");
            $productPage = $this->getSiteBuilder()->createPage("My products");
            $sitebuilder->addProductList($productPage, 1, "listview");
            $aboutus = $this->getSiteBuilder()->createPage("About us");
            $sitebuilder->addContentManager($aboutus, null);
            $contact = $this->getSiteBuilder()->createPage("Contact us", 2);
            $sitebuilder->addContactForm($contact);
            $sitebuilder->addContentManager($contact, "<div style='color: white; font-size: 14px;'> <b>Head quarter:</b><br> Sant louise street 12<br> California <br>Phone: 123456789 <br>email: test@gmail.com<br><br><b>Secondary company</b><br>Trollstreet 14<br>8626 Mo I Rana<br>Norway<br>Phone: 123412311 <br>Email: test2@gmail.com</div>", "left");
            $sitebuilder->addFooterContent('<table border="0" cellpadding="0" cellspacing="0" style="width: 100%;" class=" cke_show_border"><tbody><tr><td style="vertical-align: top;"><br><span style="color:#FFFFFF;"><span style="font-size:12px;">My web shop name<br>My street 1<br>My country</span></span></td><td style="text-align: right;"><br><span style="color:#FFFFFF;"><span style="font-size:12px;">Some other information<br>Conact information<br>1234567<br>post@mail.com</span></span><br><br></td></tr></tbody></table>');
        }
    }
}
?>
