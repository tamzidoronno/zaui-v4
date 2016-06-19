<?php
namespace ns_a97cd73e_11f6_4742_ad1e_07ffd9e51e9f;

class ProMeisterUserTestList extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "ProMeisterUserTestList";
    }

    public function render() {
        $this->includefile("testlist");
    }
    
   public function startTest() {
        $_SESSION['ns_cc678bcb_0e87_4c6c_aaad_8ec24ecdf9df_current_testid'] = $_POST['data']['testid'];
        echo $this->getApi()->getQuestBackManager()->getNextQuestionPage(\ns_cc678bcb_0e87_4c6c_aaad_8ec24ecdf9df\QuestBackUserOverview::getCurrentRunningTestId());
        die();
    }
}
?>
