<?php
namespace ns_74d458f4_3203_4488_813d_65741a0213c9;

class C3Projects extends \MarketingApplication implements \Application {

    private $company;

    public function getDescription() {
        
    }

    public function getName() {
        return "C3Projects";
    }

    public function render() {
        
    }
    
    public function renderConfig() {
        $this->includefile("overview");
    }
    
    public function createProject() {
        $project = new \core_c3_C3Project();
        $this->saveProjectInternal($project);
    }

    public function deleteProject() {
        $this->getApi()->getC3Manager()->deleteProject($_POST['value']);
    }
    
    public function saveProject() {
        $project = $this->getApi()->getC3Manager()->getProject($_POST['value']);
        $this->addWorkPackages($project);
        $this->saveProjectInternal($project);
    }
    
    public function saveProjectInternal(\core_c3_C3Project $project) {
        $project->name = $_POST['name'];
        $project->projectNumber = $_POST['projectid'];
        $project->projectOwner = $_POST['projectOwner'];
        $savedProject = $this->getApi()->getC3Manager()->saveProject($project);
        $_POST['value'] = $savedProject->id;
    }

    public function addWorkPackages(\core_c3_C3Project $project) {
        $workPackages = $this->getApi()->getC3Manager()->getWorkPackages();
        $workPackagesToAdd = [];
        foreach ($workPackages as $workPackage) {
            if ($_POST['wp_'.$workPackage->id] == "true") {
                $workPackagesToAdd[] = $workPackage->id;
            }
        }
        
        $project->workPackages = $workPackagesToAdd;
    }

    public function renderCompanySettings($company) {
        $this->company = $company;
        $this->includefile("companysettings");
    }
    
    function getCurrentCompany() {
        if (isset($_POST['value2'])) {
            return $this->getApi()->getUserManager()->getCompany($_POST['value2']);
        }
        return $this->company;
    }
    
    public function searchforprojects() {
    }
    
    public function addCompany() {
        $projectId = $_POST['value'];
        $companyId = $_POST['value2'];
        
        $this->getApi()->getC3Manager()->assignProjectToCompany($companyId, $projectId);
    }
    
    public function updateWorkPackageProjectCompany() {
        $this->getApi()->getC3Manager()->setProjectAccess($_POST['companyId'], $_POST['projectId'], $_POST['wpId'], $_POST['val']);
    }
    
    public function removeAccess() {
        $projectId = $_POST['value'];
        $companyId = $_POST['value2'];
        
        $this->getApi()->getC3Manager()->removeCompanyAccess($projectId, $companyId);
    }

    public function hasProjectWorkPackage($packageId, $workPackages) {
        foreach ($workPackages as $id => $pack) {
            if ($packageId == $id) {
                return true;
            } 
        }
        
        return false;
    }

    public function updateProjectCost() {
        $this->getApi()->getC3Manager()->setProjectCust($_POST['companyId'], $_POST['projectId'], $_POST['wpId'], $_POST['year'], $_POST['price']);
    }
}
?>
