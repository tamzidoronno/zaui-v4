<?php
namespace ns_d54f339d_e1b7_412f_bc34_b1bd95036d83;

class Footer extends \SystemApplication implements \Application {

    var $footerConfig;

    public function getDescription() {
        return $this->__("This footer application contains the footer information for your webshop, all menu entries are sorted.");
    }

    public function getName() {
        return $this->__("Footer");
    }

    public function postProcess() {
        
    }

    public function preProcess() {
        
    }

    public function changeTab() {
        $this->footerConfig = $this->getApi()->getFooterManager()->getConfiguration();
        $_SESSION['Footer']['tab'] = $_POST['data']['changetab'];
        $this->includefile("FooterTemplateConfig");
    }

    public function render() {
        $this->footerConfig = $this->getApi()->getFooterManager()->getConfiguration();
        $this->includefile("FooterTemplate");
    }

    public function getFooterConfiguration() {
        return $this->footerConfig;
    }

    public function displayConfig() {
        $this->footerConfig = $this->getApi()->getFooterManager()->getConfiguration();
        $this->includeFile("FooterTemplateConfig");
    }
    
    public function saveContent() {
        $this->footerConfig = $this->getApi()->getFooterManager()->getConfiguration();
        
        $content = $_POST['data']['content'];
        $altid = $_POST['data']['altid'];
        $contentId = $this->getFooterConfiguration()->columnIds->{$altid};
        $this->getFactory()->getApi()->getContentManager()->saveContent($contentId, $content);
    }
    
    public function updateColCount() {
        $this->getApi()->getFooterManager()->setLayout($_POST['data']['count']);
    }
}
?>
