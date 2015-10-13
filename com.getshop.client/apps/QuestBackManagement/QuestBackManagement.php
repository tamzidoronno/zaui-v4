<?php

/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

namespace ns_3ff6088a_43d5_4bd4_a5bf_5c371af42534;

class QuestBackManagement extends \ApplicationBase implements \Application {
    public function getDescription() {
        return $this->__f("Add this application to a page and let customers take a questback");
    }

    public function getName() {
        return "QuestBackManagement";
    }
    
    private function showPart($filename, $toshow) {
        $display = $this->getToShow() == $toshow ? "display: block;" : "display: none;";
        echo "<div class='part' toshow='$toshow' style='$display'>";
            $this->includefile($filename);
        echo "</div>";
    }

    public function render() {
        unset($_SESSION['ns_cc678bcb_0e87_4c6c_aaad_8ec24ecdf9df_current_testid']);
        
        if (\ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::isAdministrator()) {
            $this->printMenu();
            echo "<div class='parts'>";
                $this->showPart("overview", "questions");
                $this->showPart("tests", "tests");
                $this->showPart("usersmanagement", "usersmanagement");
                $this->showPart("results", "results");
            echo "</div>";
            
        } else {
            echo "Please login";
        }
    }
    
    public function showTestResults() {
        $test = $this->getApi()->getQuestBackManager()->getTest($_POST['data']['testId']);
        if (!$test) {
            echo "No test selected";
            return;
        }
        
        $this->includefile("result_printer");
    }
    public function deleteTest() {
        $this->getApi()->getQuestBackManager()->deleteTest($_POST['data']['testid']);
    }
    
    public function showTemplatePage() {
        $this->getApi()->getQuestBackManager()->createTemplatePageIfNotExists();
    }
    
    public function saveList() {
        $this->setConfigurationSetting("list", $_POST['data']['list']);
        $this->getApi()->getQuestBackManager()->questionTreeChanged($this->getConfiguration()->id);
    }
    
    public function getPageIdForQuestion() {
        echo $this->getApi()->getQuestBackManager()->getPageId($_POST['data']['entryId']);
        die();
    }

    public function createTest() {
        $this->getApi()->getQuestBackManager()->createTest($_POST['data']['testname']);
    }
    
    private function getActiveClass($toshow) {
        return $this->getToShow() == $toshow ? "active" : "";
    }
    
    public function printMenu() {
        echo "<div class='menuouter'>";
            echo "<div class='menuelement ".$this->getActiveClass("questions")."' toshow='questions'>Questions</div>";
            echo "<div class='menuelement ".$this->getActiveClass("tests")."' toshow='tests'>Tests</div>";
            echo "<div class='menuelement ".$this->getActiveClass("usersmanagement")."' toshow='usersmanagement'>User Management</div>";
            echo "<div class='menuelement ".$this->getActiveClass("results")."' toshow='results'>Results</div>";
        echo "</div>";
    }
    
    public function setToShow() {
        $_SESSION['ns_df435931_9364_4b6a_b4b2_951c90cc0d70_toshow'] = $_POST['data']['toshow'];
    }
    
    public function getToShow() {
        if (!isset($_SESSION['ns_df435931_9364_4b6a_b4b2_951c90cc0d70_toshow'])) {
            return "questions";
        }
        
        return $_SESSION['ns_df435931_9364_4b6a_b4b2_951c90cc0d70_toshow'];
    }
    
    public function showTestSettings() {
        $this->includefile("testsettings");
    }
    
    public function saveTestSettings() {
        $test = $this->getApi()->getQuestBackManager()->getTest($_POST['data']['testid']);
        $test->questions = $_POST['data']['nodeIds'];
        $test->forceCorrectAnswer = $_POST['data']['forceCorrectAnswer'];
        $test->name = $_POST['data']['name'];
        $this->getApi()->getQuestBackManager()->saveTest($test);
    }
    
    public function gsEmailSetup($model) {
        if (!$model) {
            $this->includefile("emailsettings");
            return;
        } 
        
        $this->setConfigurationSetting("ordersubject", $_POST['ordersubject']);
        $this->setConfigurationSetting("orderemail", $_POST['emailconfig']);
        $this->setConfigurationSetting("shouldSendEmail", $_POST['shouldSendEmail']);
    }
    
    public function assignTestToUsers() {
        foreach ($_POST['data']['usersIds'] as $userId) {
            $this->getApi()->getQuestBackManager()->assignUserToTest($_POST['data']['testId'], $userId);
        }
    }
}
