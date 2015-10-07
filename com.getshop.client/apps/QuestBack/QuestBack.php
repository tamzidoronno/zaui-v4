<?php

/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

namespace ns_07422211_7818_445e_9f16_ad792320cb10;

class QuestBack extends \ApplicationBase implements \Application {
    public function getDescription() {
        return $this->__f("This application allows Editors and Adminsitrators to setup and manage questbacks");
    }

    public function getName() {
        return "QuestBack";
    }

    public function render() {
        echo "QuestBack - - - ";
        
        $question = $this->getApi()->getQuestBackManager()->createNewQuestion("What is the best world to live in?", 0);
        echo "<pre>";
        print_r($question);
        echo "</pre>";
    }
}