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
        $this->clearJavaScriptData();
        $this->renderTable();
        $this->printJavaScript();
    }
    
    private function loadData() {
        if($this->data) {
            return;
        }
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
                    if ($attribute[1] !== "gs_hidden") {
                        echo "<div class='col col_$i col_$attribute[0]' index='".$attribute[0]."'>$attribute[1]</div>";
                    }
                    
                }
            echo "</div>";

            $j = 1;
            foreach ($this->data as $data) {
                $odd = $j % 2 ? "odd" : "even";
                echo "<div class='datarow $odd' rownumber='$j'>";
                    echo "<div class='datarow_inner'>";
                        $i = 1;
                        $postArray = array();

                        foreach ($this->attributes as $attribute) {

                            $val = "";
                            if (!isset($attribute[3])) {
                                $val = $data->{$attribute[2]};
                            } else {
                                $functionName = $attribute[3];
                                $val = $this->application->$functionName($data);
                            }

                            $postArray[$attribute[0]] = $val;

                            if ($attribute[1] !== "gs_hidden") {
                                echo "<div class='col col_$i col_$attribute[0]' index='".$attribute[0]."'>$val</div>";
                            }
                            $i++;
                        }

                        $this->printJavaScriptData($postArray, $j);
                    echo "</div>";
                    echo "<div class='datarow_extended_content'></div>";
                echo "</div>";
                $j++;
            }
        echo "</div>";
    }
    
    private function printJavaScriptData($data, $rowNumber) {
        $functionName = $this->getIdentifier();
        
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
        ?>

        <script>
            $('.GetShopModuleTable .datarow .datarow_inner').on('click', function(e) {
                var target = $(e.target);
                var base = $(this).closest('.datarow');
                base.find('.datarow_extended_content').html("");
                $('.GetShopModuleTable .datarow_extended_content').slideUp();
                var rowNumber = $(base).attr('rownumber');
                
                var data = gs_modules_data_array['<? echo $this->getIdentifier(); ?>'][rowNumber];
                data['gscolumn'] = target.attr('index');
                base.find('.datarow_extended_content').slideDown();
                
                var event = thundashop.Ajax.createEvent(null, '<? echo $functionName; ?>', this, data);
                event['synchron'] = true;
                
                thundashop.Ajax.post(event, function(res) {
                    base.find('.datarow_extended_content').html(res);

                });
            });
            
        </script>
        <?
    }
    
    private function getIdentifier() {
        return $this->manangerName."_".$this->functionName;
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

}
