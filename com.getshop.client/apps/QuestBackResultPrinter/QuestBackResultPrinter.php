<?php
namespace ns_4194456a_09b3_4eca_afb3_b3948d1f8767;

class QuestBackResultPrinter extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "QuestBackResultPrinter";
    }

    public function render() {
        $currentTestId = \ns_cc678bcb_0e87_4c6c_aaad_8ec24ecdf9df\QuestBackUserOverview::getCurrentRunningTestId();
        if (!$currentTestId) {
            echo "Please go back and select a test, there is no tests to show result for here";
            return;
        }
        
        $this->includefile("resultOverView");
    }
    
    public function getCategories() {
        $currentTestId = \ns_cc678bcb_0e87_4c6c_aaad_8ec24ecdf9df\QuestBackUserOverview::getCurrentRunningTestId();
        $test = $this->getApi()->getQuestBackManager()->getTest($currentTestId);
        $result = $this->getApi()->getQuestBackManager()->getTestResult($currentTestId);
        
        $categories = [];
        foreach($result->answers as $answer) {
            if (!in_array($answer->parent, $categories)) {
                $categories[] = $answer->parent;
            }
        }
        
        foreach ($categories as $cat) {
            $cat->result = $this->getResult($result, $cat);
        }
        return $categories;
    }

    public function getResult($result, $cat) {
        $total = "";
        $questionsInCategory = 0;
        foreach($result->answers as $answer) {
            if ($answer->parent == $cat) {
                $questionsInCategory++;
                $total += $answer->percentageOfCorrect;
            }
        }
        
        $number = $total/$questionsInCategory;
        return number_format((float)$number, 2, '.', '');
    }

}
?>
