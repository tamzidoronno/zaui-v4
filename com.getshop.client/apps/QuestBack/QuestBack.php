<?php

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Description of QuestBack
 *
 * @author ktonder
 */
namespace ns_97cb6953_6b7f_4e99_99d8_5764ffed18b5;

class QuestBackQuestion {
    public $id;
    public $question;
    public $option1;
    public $option2;
    public $option3;
    public $option4;
    public $option5;
    public $option6;
}

class QuestBackAnswer {
    public $questionid;
    public $answer;
}

class QuestBackAnswers {
    public $answers;
}

class QuestBack extends \MarketingApplication implements \Application {
    
    private $questions = array();

    public $results = array();
    
    public function setConfiguration($configuration) {
        parent::setConfiguration($configuration);
        $questionsJson = $this->getConfigurationSetting("questions");
        if ($questionsJson) {
            $this->questions = json_decode($questionsJson);
        }
        
        $results = $this->getConfigurationSetting("results");
        if ($results) {
            $this->results = json_decode($results);
        }
    }
    
    public function getDescription() {
        return $this->__f("Get feedback from your customers by asking them a few questions?");
    }

    public function getName() {
        return $this->__f("QuestBack");
    }

    public function render() {
        $this->includeFile("questback");
        if ($this->hasWriteAccess()) {
            $this->includefile("setup");
        }
    }
    
    public function removeQuestion() {
        $newquestions = array();
        foreach ($this->questions as $question) {
            if ($question->id == $_POST['data']['id']) {
                continue;
            }
            $newquestions[] = $question;
        }
        $this->questions = $newquestions;
        $this->saveQuestionArray();
    }
    
    public function saveQuestion() {
        $question = new QuestBackQuestion();
        $question->id =  uniqid();
        $question->question = $_POST['data']['question'];
        $question->option1 = $_POST['data']['ans1'];
        $question->option2 = $_POST['data']['ans2'];
        $question->option3 = $_POST['data']['ans3'];
        $question->option4 = $_POST['data']['ans4'];
        $question->option5 = $_POST['data']['ans5'];
        $question->option6 = $_POST['data']['ans6'];
        $this->questions[] = $question;
        $this->saveQuestionArray();
    }
    
    private function saveQuestionArray() {
        $serialized = json_encode($this->questions);
        $this->setConfigurationSetting("questions", $serialized);
    }
    
    public function getQuestions() {
        return $this->questions;
    }

    public function answerQuestion() {
        $answer = new QuestBackAnswer();
        $answer->questionid = $_POST['data']['id'];
        $answer->answer = $_POST['data']['answer'];
        
        $sessionAnswers = $this->getCurrentSessionQuestBackAnswers();
        $sessionAnswers->answers[] = $answer;
        $json = json_encode($sessionAnswers);
        $_SESSION['questback_session_answers'] = $json;
        
        $nextQuestion = $this->getCurrentQuestion();
        if (!$nextQuestion) {
            $this->results[] = $sessionAnswers;
            $jsonSet = json_encode($this->results);
            $this->startAdminImpersonation("PageManager", "setApplicationSettings");
            $this->setConfigurationSetting("results", $jsonSet);
            $this->stopImpersionation();
        }
    }
    
    public function getCurrentQuestion() {
        $questionIndex = $this->getQuestionIndex();
        
        $i = 0;
        foreach($this->questions as $question) {
            if ($i == $questionIndex) {
                return $question;
            }
            $i++;
        }
        
        return false;
    }
    
    public function getTotalQuestionCount() {
        return count($this->questions);
    }
    
    public function getQuestionIndex() {
        return count($this->getCurrentSessionQuestBackAnswers()->answers);
    }

    public function getCurrentSessionQuestBackAnswers() {
        if (isset($_SESSION['questback_session_answers'])) {
            $json = $_SESSION['questback_session_answers'];
            return json_decode($json);
        }
        
        return new QuestBackAnswers();
    }

    public function reset() {
        unset($_SESSION['questback_session_answers']);
    }
    
    public function printResult($option, $index, $question) {
        if (!$option) {
            return;
        }
        echo $option." (".$this->getPercentage($question, $index)."%)<br/>";
    }

    public function getPercentage($question, $index) {
        $currentResult = array();
       
        
        
        
        foreach ($this->results as $result) {
            foreach ($result as $answers) {
                foreach ($answers as $answer) {

                    $questionid = $answer->questionid;
                    $answerIndex = $answer->answer;

                    if (!isset($currentResult[$questionid])) 
                        $currentResult[$questionid] = array();

                    if (!isset($currentResult[$questionid][$answerIndex]))
                        $currentResult[$questionid][$answerIndex] = 0;

                    $currentResult[$questionid][$answerIndex] += 1;
                }
            }
        }
        
        if (!isset($currentResult[$question->id])) {
            return 0;
        }
        
        $total = 0;
        foreach ($currentResult[$question->id] as $count) {
            $total += $count;
        }
        
        $totalForCurrentAnswer = 0;
        if (isset($currentResult[$question->id][$index])) {
            $totalForCurrentAnswer = $currentResult[$question->id][$index];
        }
        
        if (!$totalForCurrentAnswer) {
            return 0;
        }
        
        
        return round(($totalForCurrentAnswer/$total)*100,2);   
    }

    public function requestAdminRights() {
        $this->requestAdminRight("PageManager", "setApplicationSettings", "need to store data after a questback has been completed");
    }
}