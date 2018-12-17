<?php
namespace ns_7a273876_7390_45b3_95c3_79d3c0cec4d3;

class SalesPointViews extends \MarketingApplication implements \Application {
    /**
     * @var \core_pos_PosView
     */
    private $selectedView;
    
    public function getDescription() {
        
    }

    public function getName() {
        return "SalesPointViews";
    }
    
    public function renderEditView() {
        if ($this->selectedView->type == "tableview") {
            $this->includefile("tableviewedit");
        }
        if ($this->selectedView->type == "listview") {
            $this->includefile("listviewedit");
        }
    }

    public function render() {
        if(isset($_SESSION['ns_7a273876_7390_45b3_95c3_79d3c0cec4d3_editview'])) {
            $this->selectedView = $this->getApi()->getPosManager()->getView($_SESSION['ns_7a273876_7390_45b3_95c3_79d3c0cec4d3_editview']);
            $this->renderEditView();
            return;
        }
        
        $this->includefile("viewsettings");
    }
    
    public function createnewview() {
        $this->getApi()->getPosManager()->createNewView($_POST['data']['name'], $_POST['data']['type']);
    }
    
    public function deleteView() {
        $this->getApi()->getPosManager()->deleteView($_POST['data']['viewid']);
    }
    
    public function editView() {
        $_SESSION['ns_7a273876_7390_45b3_95c3_79d3c0cec4d3_editview'] = $_POST['data']['viewid'];
    }
    
    public function cancelEditView() {
        unset($_SESSION['ns_7a273876_7390_45b3_95c3_79d3c0cec4d3_editview']);
    }

    /**
     * @return \core_pos_PosView
     */
    public function getSelectedView() {
        if (!$this->selectedView) {
            $this->selectedView = $this->getApi()->getPosManager()->getView($_SESSION['ns_7a273876_7390_45b3_95c3_79d3c0cec4d3_editview']);
        }
        
        return $this->selectedView;
    }

    public function saveProductListToCurrentView() {
        $selectedView = $this->getSelectedView();
        $selectedView->productListsIds = $_POST['data']['productIds'];
        $this->getApi()->getPosManager()->saveView($selectedView);
    }
}
?>
