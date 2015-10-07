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
        if ($this->getConfigurationSetting("type") == "") {
            $this->includefile('selectType');
        } else {
            $this->includefile("type_template_".$this->getConfigurationSetting("type"));
        }
    }
    
    public function setType() {
        $this->setConfigurationSetting("type", $_POST['data']['type']);
    }
    
    public function saveHeaderText() {
        $this->setConfigurationSetting("headingtext", $_POST['data']['headingText']);
    }
    
    public function getOptions() {
        $options = $this->getConfigurationSetting("options");
        if (!$options) {
            $options = [];
        } else {
            $options = json_decode($options);
        }
        
        return $options;
    }
    
    function gen_uuid() {
        return sprintf( '%04x%04x-%04x-%04x-%04x-%04x%04x%04x',
            // 32 bits for "time_low"
            mt_rand( 0, 0xffff ), mt_rand( 0, 0xffff ),

            // 16 bits for "time_mid"
            mt_rand( 0, 0xffff ),

            // 16 bits for "time_hi_and_version",
            // four most significant bits holds version number 4
            mt_rand( 0, 0x0fff ) | 0x4000,

            // 16 bits, 8 bits for "clk_seq_hi_res",
            // 8 bits for "clk_seq_low",
            // two most significant bits holds zero and one for variant DCE1.1
            mt_rand( 0, 0x3fff ) | 0x8000,

            // 48 bits for "node"
            mt_rand( 0, 0xffff ), mt_rand( 0, 0xffff ), mt_rand( 0, 0xffff )
        );
    }
    
    public function addOption() {
        $options = $this->getOptions();
        
        $option = new QuestBackOption();
        $option->text = "New option";
        $option->id = $this->gen_uuid();
        $options[] = $option;
        
        $json = json_encode($options);
        $this->setConfigurationSetting("options", $json);
    }
    
    public function optionTextChanged() {
        $options = $this->getOptions();
        
        foreach ($options as $option) {
            if ($option->id == $_POST['data']['optionId']) {
                $option->text = $_POST['data']['text'];
            }
        }
        
        $json = json_encode($options);
        $this->setConfigurationSetting("options", $json);
    }
    
    public function deleteOption() {
        $options = $this->getOptions();
        $keep = [];
        
        foreach ($options as $option) {
            if ($option->id != $_POST['data']['optionId']) {
                $keep[] = $option;
            }
        }
        
        $json = json_encode($keep);
        $this->setConfigurationSetting("options", $json);
    }
    
    public function markAsCorrectOption() {
        $options = $this->getOptions();
        
        foreach ($options as $option) {
            if ($option->id == $_POST['data']['optionId']) {
                $option->correctAnswer = !$option->correctAnswer;
            }
        }
        
        $json = json_encode($options);
        $this->setConfigurationSetting("options", $json);
    }
    
}

class QuestBackOption {
    public $id = "";
    public $text = "";
    public $correctAnswer = false;
}