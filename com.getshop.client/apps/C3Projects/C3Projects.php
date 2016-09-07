<?php
namespace ns_74d458f4_3203_4488_813d_65741a0213c9;

class C3Projects extends \MarketingApplication implements \Application {
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
    
    public function saveProject(\core_c3_C3Project $project) {
        $project = $this->getApi()->getC3Manager()->getProject($_POST['value']);
        $this->saveProjectInternal($project);
    }
    
    public function saveProjectInternal(\core_c3_C3Project $project) {
        $project->name = $_POST['name'];
        $project->projectNumber = $_POST['projectid'];
        $this->getApi()->getC3Manager()->saveProject($project);
    }

}
?>
