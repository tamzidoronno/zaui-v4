<?php
namespace ns_4a282640_6f2c_4f64_acf2_a92f9c06e2d9;

class PmsGetShopExpressSettings extends \WebshopApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "PmsGetShopExpressSettings";
    }

    public function render() {
        $this->includefile("settings");
    }
       
    public function removePmsView() {
        $config = $this->getApi()->getPmsManager()->getConfiguration($this->getSelectedMultilevelDomainName());
        unset($config->mobileViews->{$_POST['data']['id']});
        $this->getApi()->getPmsManager()->saveConfiguration($this->getSelectedMultilevelDomainName(), $config);
    }
    
    public function updateMobileView() {
        $viewId = $_POST['data']['id'];
        $config = $this->getApi()->getPmsManager()->getConfiguration($this->getSelectedMultilevelDomainName());
        $config->mobileViews->{$viewId}->name = $_POST['data']['name'];
        $config->mobileViews->{$viewId}->viewType = $_POST['data']['viewtype'];
        $config->mobileViews->{$viewId}->icon = $_POST['data']['icon'];
        $config->mobileViews->{$viewId}->paidFor = $_POST['data']['paidFor'] == "true";
        $config->mobileViews->{$viewId}->daysDisplacement = $_POST['data']['daysDisplacement'];
        $config->mobileViews->{$viewId}->sortType = $_POST['data']['sortType'];
        $this->getApi()->getPmsManager()->saveConfiguration($this->getSelectedMultilevelDomainName(), $config);
    }
    
    public function addproducttoview() {
        $id = $_POST['data']['id'];
        
        $config = $this->getApi()->getPmsManager()->getConfiguration($this->getSelectedMultilevelDomainName());
        $config->mobileViews->{$id}->products[] = $_POST['data']['prodid'];
        $this->getApi()->getPmsManager()->saveConfiguration($this->getSelectedMultilevelDomainName(), $config);

        $this->printProductsAdded($id);
    }
    
    public function updateMobileViewRestriction() {
        $toSave = array();
        foreach($_POST['data'] as $key => $val) {
            $res = explode("_", $key);
            $userId = $res[0];
            $area = $res[1];
            if($area == "userdata") {
                $user = $this->getApi()->getUserManager()->getUserById($userId);
                $newAnnotiations = array();
                foreach($user->annotionsAdded as $annotation) {
                    if($annotation == "ExcludePersonalInformation") {
                        continue;
                    }
                    $newAnnotiations[] = $annotation;
                }
                if($val === "true") {
                    $newAnnotiations[] = "ExcludePersonalInformation";
                }
                $user->annotionsAdded = $newAnnotiations;
                $this->getApi()->getUserManager()->saveUser($user);
            } else {
                if($val === "true") {
                    if(!isset($toSave[$userId])) {
                        $toSave[$userId] = array();
                    }
                    $toSave[$userId][] = $area;
                }
            }
        }
        
        $config = $this->getApi()->getPmsManager()->getConfiguration($this->getSelectedMultilevelDomainName());
        $config->mobileViewRestrictions = $toSave;
        $this->getApi()->getPmsManager()->saveConfiguration($this->getSelectedMultilevelDomainName(), $config);
    }
    
    public function removeproductfromview() {
        $id = $_POST['data']['id'];
        $prodId = $_POST['data']['prodid'];
        $config = $this->getApi()->getPmsManager()->getConfiguration($this->getSelectedMultilevelDomainName());
        $config->mobileViews->{$id}->products[] = $prodId;
        $res = array();
        foreach($config->mobileViews->{$id}->products as $tmpProdId) {
            if($tmpProdId != $prodId && $tmpProdId) {
                $res[] = $tmpProdId;
            }
        }
        $config->mobileViews->{$id}->products = $res;
        
        $this->getApi()->getPmsManager()->saveConfiguration($this->getSelectedMultilevelDomainName(), $config);

        $this->printProductsAdded($id);
    }
    
    public function loadProducts() {
        $products = $this->getApi()->getProductManager()->getAllProducts();
        echo "<table cellspacing='0' cellpadding='0'>";
        foreach($products as $product) {
            if(!$product->name) {
                continue;
            }
            echo "<tr>";
            echo "<td>" . $product->name . "</td>";
            echo "<td>";
            echo "<span style='padding-left: 30px; cursor:pointer;' class='addproducttoview' prodid='".$product->id."'>Add</span>";
            echo "</td>";
            echo "</tr>";
        }
        echo "</table>";
    }

    public function printProductsAdded($viewId) {
        $config = $this->getApi()->getPmsManager()->getConfiguration($this->getSelectedMultilevelDomainName());
        $views = $config->mobileViews;
        $view = $views->{$viewId};
        $prods = array();
        foreach($view->products as $prodId) {
            $product = $this->getApi()->getProductManager()->getProduct($prodId);
            $prods[] = "<i class='fa fa-trash-o removeProductFromMobileView' prodid='$prodId'></i> " . $product->name;
        }
        echo join(", ", $prods) . " ";

    }
    
    public function createPmsView() {
        $config = $this->getApi()->getPmsManager()->getConfiguration($this->getSelectedMultilevelDomainName());
        $view = new \core_pmsmanager_PmsMobileView();
        $view->name = $_POST['data']['name'];
        $view->id = uniqid();
        $config->mobileViews->{$view->id} = $view;
        $this->getApi()->getPmsManager()->saveConfiguration($this->getSelectedMultilevelDomainName(), $config);
    }
}
?>
