<?php
namespace ns_f8f67e15_ee94_491f_b04f_258069a57b18;

class QuestBackResultOverview extends \MarketingApplication implements \Application {
    private $results = null;
    
    public function getDescription() {
        
    }

    public function getName() {
        return "QuestBackResultOverview";
    }

    public function render() {
        if ($this->getModalVariable("referenceid")) {
            $this->results = $this->getApi()->getQuestBackManager()->getResultWithReference($this->getModalVariable("testid"), $this->getModalVariable("referenceid"));
        } else {
            $this->results = $this->getApi()->getQuestBackManager()->getResult($this->getModalVariable("testid"));
        }
        
        $this->includefile("result");
    }

    /**
     * 
     * @return \core_questback_data_QuestBackResult
    */
    public function getResults() {
        return $this->results;
    }

    public function countOption($id, $values) {
        $count = 0;
        
        foreach ($values as $key => $value) {
            if ($value->answer === $id) {
                $count++;
            }
        }
        
        if ($count < 1)
            return 0;
            
        $percent = $count / count($values) * 100;
        return number_format((float)$percent, 2, '.', '')."%";
    }

}
?>
