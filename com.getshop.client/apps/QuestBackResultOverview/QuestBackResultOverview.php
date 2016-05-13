<?php
namespace ns_f8f67e15_ee94_491f_b04f_258069a57b18;

class QuestBackResultOverview extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "QuestBackResultOverview";
    }

    public function render() {
        $results = $this->getApi()->getQuestBackManager()->getResult($this->getModalVariable("testid"));
        echo "<pre>";
        print_r($results);
        echo "</pre>";
    }
}
?>
