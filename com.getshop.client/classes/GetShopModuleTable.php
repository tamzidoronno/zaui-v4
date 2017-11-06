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
    private $args;
    
    function __construct(\ApplicationBase $application, $managerName, $functionName, $args, $attributes) {
        $this->attributes = $attributes;
        $this->application = $application;
        $this->manangerName = $managerName;
        $this->functionName = $functionName;
        $this->attributes = $attributes;
        $this->args = $args;
    }

    public function render() {
        $this->loadData();
        $this->renderTable();
    }
    
    private function loadData() {
        $api = $this->application->getApi();
        $managerName = "get".$this->manangerName;
        $res = $api->$managerName();
        $this->data = call_user_func_array(array($res, $this->functionName), $this->args);
    }

    private function renderTable() {
        echo "<div class='GetShopModuleTable'>";
        
            echo "<div class='attributeheader datarow'>";
                $i = 1;
                foreach ($this->attributes as $attribute) {
                    $i++;
                    echo "<div class='col col_$i col_$attribute[0]'>$attribute[1]</div>";
                }
            echo "</div>";

            $j = 1;
            foreach ($this->data as $data) {
                $odd = $j % 2 ? "odd" : "even";
                echo "<div class='datarow $odd'>";
                
                $i = 1;
                foreach ($this->attributes as $attribute) {

                    $val = "";
                    if (!isset($attribute[3])) {
                        $val = $data->{$attribute[2]};
                    } else {
                        $functionName = $attribute[3];
                        $val = $this->application->$functionName($data);
                    }

                    echo "<div class='col col_$i col_$attribute[0]'>$val</div>";
                    $i++;
                }
                echo "</div>";
                $j++;
            }
        echo "</div>";
    }

}
