<?php

/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

namespace ns_778eb415_ec18_4202_8012_092f6f5ae292;

class QuestBackProgress extends \ApplicationBase implements \Application {
    public function getDescription() {
        return $this->__f("This application allows Editors and Adminsitrators to setup and manage questbacks");
    }

    public function getName() {
        return "QuestBackProgress";
    }

    public function render() {
        $this->includefile("progress");
    }
    
    public function getProgress() {
        $testId = \ns_cc678bcb_0e87_4c6c_aaad_8ec24ecdf9df\QuestBackUserOverview::getCurrentRunningTestId();
        if ($testId) {
            return $this->getApi()->getQuestBackManager()->getProgress($testId);
        } else {
            return 0;
        }
    }
}