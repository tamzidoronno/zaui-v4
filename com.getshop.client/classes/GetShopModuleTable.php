<?php

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Description of GetShopModuleTable
 *
 * @author ktonder
 */
class GetShopModuleTable {
    private $application;
    private $manangerName; 
    private $functionName;
    private $attributes;
    private $extraData;
    private $args;
    private $avoidAutoExpanding = false;
    
    function __construct(\ApplicationBase $application, $managerName, $functionName, $args, $attributes, $extraData = null) {
        $this->attributes = $attributes;
        $this->application = $application;
        $this->manangerName = $managerName;
        $this->functionName = $functionName;
        $this->extraData = $extraData;
        $this->args = $args;
    }

    public function render() {
        $this->uuid = uniqid();
        $this->loadData();
        $this->clearJavaScriptData();
        $this->renderTable();
        $this->printJavaScript();
    }
    
    private function loadData() {
        if(isset($this->data) && $this->data) {
            return;
        }
        
        $api = $this->application->getApi();
        $managerName = "get".$this->manangerName;
        $res = $api->$managerName();
        $this->data = call_user_func_array(array($res, $this->functionName), $this->args);
    }

    private function renderTable() {
        echo "<div class='GetShopModuleTable' identifier='".$this->getIdentifier()."' method='".$this->manangerName."_".$this->functionName."'>";
        
            echo "<div class='attributeheader datarow'>";
                $i = 1;
                foreach ($this->attributes as $attribute) {
                    if ($attribute[1] !== "gs_hidden") {
                        echo "<div class='col col_$i col_$attribute[0]' index='".$attribute[0]."'>$attribute[1]</div>";
                    }
                    $i++;
                    
                }
            echo "</div>";

            $j = 1;
            foreach ($this->data as $data) {
                $odd = $j % 2 ? "odd" : "even";
                $active = $this->shouldShowRow($j);
                $activeClass = $active? "active" : "";
                
                echo "<div class='datarow $odd $activeClass' rownumber='$j'>";
                    echo "<div class='datarow_inner'>";
                        $i = 1;
                        $postArray = array();

                        foreach ($this->attributes as $attribute) {

                            $val = "";
                            if (!isset($attribute[3])) {
                                $val = $data->{$attribute[2]};
                            } else {
                                $functionName = $attribute[3];
                                $colVal = @$data->{$attribute[2]};
                                $val = $this->application->$functionName($data, $colVal);
                            }

                            $postArray[$attribute[0]] = $val;

                            if ($attribute[1] !== "gs_hidden") {
                                echo "<div class='col col_$i col_$attribute[0]' index='".$attribute[0]."'>$val</div>";
                            }
                            $i++;
                        }
                        
                        if ($this->extraData != null) {
                            $postArray = array_merge($postArray, $this->extraData);
                        }

                        $this->printJavaScriptData($postArray, $j);
                    echo "</div>";
                    
                    if ($active) {
                        echo "<div class='datarow_extended_content' style='display:block;'>";
                        $this->renderTableContent($postArray, $j);
                        echo "</div>";    
                    } else {
                        echo "<div class='datarow_extended_content'></div>";    
                    }
                    
                echo "</div>";
                $j++;
            }
        echo "</div>";
    }
    
    private function printJavaScriptData($data, $rowNumber) {
        $functionName = $this->getFunctionName();
        
        if (!method_exists($this->application, $functionName)) {
            return;
        }
        ?>
        <script>
            gs_modules_data_array['<? echo $this->getIdentifier(); ?>']['<? echo $rowNumber; ?>'] = <? echo json_encode($data); ?>
        </script>
        <?
    }
    
    private function printJavaScript() {
        $functionName = $this->getIdentifier();
        
        if (!method_exists($this->application, $functionName)) {
            return;
        }
    }
    
    private function getIdentifier() {
        return $this->manangerName."_".$this->functionName."_".$this->uuid;
    }

    public function clearJavaScriptData() {
        ?>
        <script>
            if (typeof(gs_modules_data_array) === "undefined") {
                gs_modules_data_array = {};
            }
            
            gs_modules_data_array['<? echo $this->getIdentifier(); ?>'] = {};
        </script>
            
        <?
    }

    public function setData($data) {
        $this->data = $data;
    }
    
    private function shouldShowRow($rownumber) {
        if ($this->avoidAutoExpanding) {
            return false;
        }
        
        if (!isset($_SESSION['gs_moduletable_'.$this->getFunctionName()])) {
            return false;
        }
        $sessionData = $_SESSION['gs_moduletable_'.$this->getFunctionName()];
        
        if ($sessionData['rownumber'] == $rownumber) {
            return true;
        }
        
        return false;
    }

    private function renderTableContent($attribute, $rownumber) {
        $sessionData = $_SESSION['gs_moduletable_'.$this->getFunctionName()];
        $_POST['data'] = $attribute;
        
        if (isset($sessionData['index'])) {
            $_POST['data']['gscolumn'] = $sessionData['index'];
        }
        
        $functioName = $this->getFunctionName();
        $this->application->$functioName();
    }

    public function getFunctionName() {
        return $this->manangerName."_".$this->functionName;
    }

    public function avoidAutoExpanding() {
        $this->avoidAutoExpanding = true;
    }

}
