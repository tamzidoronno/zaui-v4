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
    
    private function showPart($filename) {
        echo "<div class='part'>";
            $this->includefile($filename);
        echo "</div>";
    }

    public function render() {
        if (\ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::isAdministrator()) {
            $this->printMenu();
            echo "<div class='parts'>";
                $this->showPart("overview");
            echo "</div>";
        } else {
            echo "Please login";
        }
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

    public function printMenu() {
        echo "<div class='menuouter'>";
            echo "<div class='menuelement active'>Questions</div>";
            echo "<div class='menuelement'>Tests</div>";
            echo "<div class='menuelement'>User Management</div>";
            echo "<div class='menuelement'>QuestBack Settings</div>";
        echo "</div>";
    }

}
