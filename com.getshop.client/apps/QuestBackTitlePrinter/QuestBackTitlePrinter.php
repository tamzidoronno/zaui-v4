<?php

/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

namespace ns_5366fb18_af90_44ed_99b7_5bb361fec16c;

class QuestBackTitlePrinter extends \ApplicationBase implements \Application {
    public function getDescription() {
        return $this->__f("This application allows Editors and Adminsitrators to setup and manage questbacks");
    }

    public function getName() {
        return "QuestBackTitlePrinter";
    }

    public function render() {
        echo "<div class='title'>".$this->getApi()->getQuestBackManager()->getQuestionTitle($this->getPage()->javapage->id)."</div>";
    }
}