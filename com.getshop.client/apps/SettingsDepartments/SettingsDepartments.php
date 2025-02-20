<?php
namespace ns_d3bd5a9e_2e8d_4992_b6c4_aacec6ae284e;

class SettingsDepartments extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "SettingsDepartments";
    }

    public function render() {
        $this->includefile("leftmenu");
        echo "<div class='workarea'>";
            $this->includefile($this->getActiveTab());
        echo "</div>";
    }
    
    public function showCreateDepartment() {
        $this->setActiveTab('createnew');
    }

    public function setActiveTab($param0) {
        $_SESSION['ns_d3bd5a9e_2e8d_4992_b6c4_aacec6ae284e_active_tab'] = $param0;
    }
    
    public function getActiveTab() {
        if (!isset($_SESSION['ns_d3bd5a9e_2e8d_4992_b6c4_aacec6ae284e_active_tab']) || !$_SESSION['ns_d3bd5a9e_2e8d_4992_b6c4_aacec6ae284e_active_tab'])
            return "departments";
        
        return $_SESSION['ns_d3bd5a9e_2e8d_4992_b6c4_aacec6ae284e_active_tab'];
    }
    
    public function createNew() {
        $this->getApi()->getDepartmentManager()->createDepartment($_POST['data']['name']);
        $this->setActiveTab("departments");
    }
    
    public function showDepartments() {
        $this->setActiveTab("departments");
    }
    
    public function deleteDepartment() {
        $this->getApi()->getDepartmentManager()->deleteDepartment($_POST['data']['departmentid']);
    }

    public function startEditingDepartment() {
        $_SESSION['edit_department_id'] = $_POST['data']['departmentid'];
        $this->setActiveTab('editdepartment');
    }
    
    public function saveDepartment() {
        $department = $this->getApi()->getDepartmentManager()->getDepartment($_SESSION['edit_department_id']);
        $department->name = $_POST['data']['name'];
        $department->code = $_POST['data']['code'];
        $this->getApi()->getDepartmentManager()->saveDepartment($department);
        $this->setActiveTab("departments");
    }
    
    public function doARecalculationOfData() {
        $this->getApi()->getPmsManager()->resetDeparmentsOnOrders($this->getSelectedMultilevelDomainName());
    }
}
?>
