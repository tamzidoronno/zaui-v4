<?
namespace ns_d315510d_198f_4c16_beef_54f979be58cf;

class GetShopAdmin extends \ApplicationBase implements \Application {
    public function getDescription() {
        return "";
    }

    public function getName() {
        return "GetShop Admin";
    }

    public function render() {
        echo "You can not add this application here!";
    }
    
    public function renderConfig() {
        $this->includefile("showApplications");
        $this->includefile("showStoreSettings");
    }
    
    public function saveApplication() {
        $app = $this->getApi()->getGetShopApplicationPool()->get($_POST['value']);
        $app->isPublic = $_POST['ispublic'];
        $app->isFrontend = $_POST['isfrontend'];
        $app->isFrontend = $_POST['isfrontend'];
        $app->moduleId = $_POST['module'];
        
        $this->getApi()->getGetShopApplicationPool()->saveApplication($app);
    }
    
    public function deleteApplication() {
        $this->getApi()->getGetShopApplicationPool()->deleteApplication($_POST['value']);
    }
    
    public function saveStoreSettings() {
        $store = $this->getFactory()->getApi()->getStoreManager()->setIsTemplate($this->getFactory()->getStore()->id, $_POST['isTemplate']);
    }

}
?>