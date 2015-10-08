<?php

/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

namespace ns_cc678bcb_0e87_4c6c_aaad_8ec24ecdf9df;

class QuestBackUserOverview extends \ApplicationBase implements \Application {
    public function getDescription() {
        return $this->__f("Allow your customers to take the tests you setup.");
    }

    public function getName() {
        return "QuestBackUserOverview";
    }

    public function render() {
        $this->includefile("yourtests");
    }
    
    public static function getCurrentRunningTestId() {
        if (isset($_SESSION['ns_cc678bcb_0e87_4c6c_aaad_8ec24ecdf9df_current_testid'])) {
            return $_SESSION['ns_cc678bcb_0e87_4c6c_aaad_8ec24ecdf9df_current_testid'];
        }
        
        return null;
    }
    
    public function startTest() {
        $_SESSION['ns_cc678bcb_0e87_4c6c_aaad_8ec24ecdf9df_current_testid'] = $_POST['data']['testid'];
        echo $this->getApi()->getQuestBackManager()->getNextQuestionPage(QuestBackUserOverview::getCurrentRunningTestId());
        die();
    }
}