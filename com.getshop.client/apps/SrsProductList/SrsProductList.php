<?php
namespace ns_4b66e3c1_1f99_42f2_8262_9359f74d9675;

class SrsProductList extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "SrsProductList";
    }

    public function ProductManager_getAllProductsForRestaurant() {
        $view = new \ns_f52bea98_9616_4b43_bd28_32e62fa95c91\ProductView();
        $view->setProductId($_POST['data']['id']);
        $view->renderApplication(true, $this);
    }
    
    public function render() {
        $this->includefile("header");
        $this->renderTable();
    }

    public function renderTable() {
        $filter = $this->createFilter();
        $args = array($filter);

        $attributes = array(
            array('id', 'gs_hidden', 'id'),
            array('name', 'NAME', 'name'),
            array('price', 'PRICE', 'price'),
            array('', 'PRICE', 'price'),
        );

        $table = new \GetShopModuleTable($this, 'ProductManager', "getAllProductsForRestaurant", $args, $attributes);
        $table->renderPagedTable();
    }

    public function createFilter() {
        $options = new \core_common_FilterOptions();
        $options->searchWord = $this->getSearchWord();
        return $options;
    }

    public function setSearchWord() {
        $_SESSION['ns_4b66e3c1_1f99_42f2_8262_9359f74d9675_searchword'] = $_POST['data']['searchword'];
    }

    public function getSearchWord() {
        return isset($_SESSION['ns_4b66e3c1_1f99_42f2_8262_9359f74d9675_searchword']) ? $_SESSION['ns_4b66e3c1_1f99_42f2_8262_9359f74d9675_searchword'] : "";
    }

}
?>
