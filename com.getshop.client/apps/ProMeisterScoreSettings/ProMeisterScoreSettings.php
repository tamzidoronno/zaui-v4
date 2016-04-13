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
        
        $score->requirements = [];
        
        $groups = $this->getApi()->getUserManager()->getAllGroups();
        $tests = $this->getApi()->getQuestBackManager()->getAllTests();
        
        $score->testsThatShouldBeUsed = [];
        foreach ($tests as $test) {
            if (isset($_POST['data']['use_'.$test->id]) && $_POST['data']['use_'.$test->id] == "true") {
                $score->testsThatShouldBeUsed[] = $test->id;
            }
        }
        
        foreach ($groups as $group) {
            $groupid = $group->id;
            if (!isset($score->groupRequiments->{$group->id})) {
                $score->groupRequiments->{$group->id} = new \core_questback_data_GroupRequirement();
            }
            
            foreach ($tests as $test) {
                $requirement = new \core_questback_data_Requirement();    
                $requirement->required = $_POST['data']['required_'.$groupid."_".$test->id];
                $score->groupRequiments->{$group->id}->requirements->{$test->id} = $requirement;

            }
        }
        
        $this->getApi()->getQuestBackManager()->saveQuestBackResultRequirement($score);
    }
}
?>
