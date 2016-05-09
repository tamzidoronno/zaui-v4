<?php
namespace ns_4194456a_09b3_4eca_afb3_b3948d1f8767;

class QuestBackResultPrinter extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "QuestBackResultPrinter";
    }

    public function render() {
        $test = $this->getCurrentTest();
        if ($test->type === "questback") {
            $this->wrapContentManager("thankyou", "Thank you");
        } else {
            $this->includefile("resultOverView");
        }
        
    }
    
    public function getCategories() {
        $test = $this->getCurrentTest();
        $categories = $this->getApi()->getQuestBackManager()->getCategoriesForTest($test->id);
        $result = $this->getCurrentTestResults();

        foreach ($categories as $cat) {
            $cat->result = QuestBackResultPrinter::getResult($result, $cat);
        }
        return $categories;
    }

    public static function getResult($result, $cat) {
        if (!$result) {
            return 0;
        }
        
        $total = "";
        $questionsInCategory = 0;
        $answers = $result->answers;
        foreach($answers as $answer) {
            if (($answer->parent == $cat) || ($answer->parent->id == $cat)) {
                $questionsInCategory++;
                $total += $answer->percentageOfCorrect;
            }
        }
        
        if (!$total) {
            return 0;
        }
        
        $number = $total/$questionsInCategory;
        return number_format((float)$number, 2, '.', '');
    }

    public function getCurrentTest() {
        $currentTestId = \ns_cc678bcb_0e87_4c6c_aaad_8ec24ecdf9df\QuestBackUserOverview::getCurrentRunningTestId();
        if (isset($_GET['testId'])) {
            return $this->getApi()->getQuestBackManager()->getTest($_GET['testId']);
        } 
        
        return $this->getApi()->getQuestBackManager()->getTest($currentTestId);
    }

    public function getCurrentTestResults() {
        $test = $this->getCurrentTest();
        if (isset($_GET['userId'])) {
            return $this->getApi()->getQuestBackManager()->getTestResults($_GET['userId'], $test->id);
        }
        
        return $this->getApi()->getQuestBackManager()->getTestResult($test->id);
    }

    public static function getResultClass($test, $cat) {
        if ($cat->result < $test->redTo) {
            return "red";
        }
        
        if ($cat->result <= $test->yellowTo && $cat->result >= $test->yellowFrom) {
            return "yellow";
        }
        
        if ($cat->result > $test->greenFrom) {
            return "green";
        }
        
        return "";
    }
    
    public static function getResultText($test, $cat) {
        if ($cat->result < $test->redTo) {
            return $test->redText;
        }
        
        if ($cat->result <= $test->yellowTo && $cat->result >= $test->yellowFrom) {
            return $test->yellowText;
        }
        
        if ($cat->result > $test->greenFrom) {
            return $test->greenText;
        }
        
        return "";
    }

}
?>
