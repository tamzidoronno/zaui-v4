<?php
namespace ns_d6e01372_c0d8_442e_ae4f_6aa0eafd2a22;

class ProMeisterScoreSettings extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "ProMeisterScoreSettings";
    }

    public function render() {
        $this->includefile("promeisterScoreSettings");
    }
    
    public function saveresult() {
        $score = $this->getApi()->getQuestBackManager()->getResultRequirement();
        if (!$score) {
            $score = new \core_questback_data_ResultRequirement();
        }
        
        $score->testId = $_POST['data']['testname'];
        $score->requirements = [];
        
        $groups = $this->getApi()->getUserManager()->getAllGroups();
        $categories = $this->getApi()->getQuestBackManager()->getCategories();
        
        $score->catsThatShouldBeUsed = [];
        foreach ($categories as $cat) {
            if (isset($_POST['data']['use_'.$cat->id]) && $_POST['data']['use_'.$cat->id] == "true") {
                $score->catsThatShouldBeUsed[] = $cat->id;
            }
        }
        
        foreach ($groups as $group) {
            $groupid = $group->id;
            if (!isset($score->groupRequiments->{$group->id})) {
                $score->groupRequiments->{$group->id} = new \core_questback_data_GroupRequirement();
            }
            
            foreach ($categories as $cat) {
                $requirement = new \core_questback_data_Requirement();    
                $requirement->required = $_POST['data']['required_'.$groupid."_".$cat->id];
                $requirement->mandatory = $_POST['data']['mandatory_'.$groupid."_".$cat->id];
                $score->groupRequiments->{$group->id}->requirements->{$cat->id} = $requirement;
            }
        }
        
        $this->getApi()->getQuestBackManager()->saveQuestBackResultRequirement($score);
    }
}
?>
