<?php
/**
 * Description of CarTuningApplication
 *
 * @author ktonder
 */
namespace ns_3bfa7e0d_3280_4c85_8f72_7516cd145446;

class CarTuningApplication extends \WebshopApplication implements \Application { 
    public function getDescription() {
        return $this->__f("Simple cartuning application.");
    }

    public function getName() {
        return $this->__f("Car tuning");
    }

    public function getTopModels() {
        $top = $this->getApi()->getCarTuningManager()->getCarTuningData(null);
        return $top;
    }
    
    public function render() {
        $this->includefile("tuningdata");
    } 
    
    public function printCarTuningList($list, $subindex, $offset) {
        $offsetCount = 0;
        $display = "";
        if($subindex > 0) {
            $display = "style='display:none;'";
        }
        if($subindex > 0) {
            echo "<div class='back_button tuning_data_entry topLevel subindex_".$subindex . " offset_".$offset."' subindex='".($subindex-2)."' offsetcount='".($subindex-1)."'><div class='back'>Tilbake</div></div>";
        }
        foreach ($list as $entry) {
            /* @var $entry \core_cartuning_CarTuningData */
            $printclass = "topLevel subindex_".$subindex . " offset_".$offset;
            $attr = "";
            $styler = "";
            if(sizeof($entry->subEntries)) {
                $this->printCarTuningList($entry->subEntries, $subindex+1, $offsetCount);
            } else {
                $attr = "data='".json_encode($entry)."'";
                $styler = "style='background-image:none;'";
            }
            echo "<div $attr $display class='tuning_data_entry $printclass' subindex='$subindex' offset='$offset' offsetcount='$offsetCount'><div $styler class='tuning_data_entry_inner'>".$entry->name."</div></div>";
            $offsetCount++;
        }
    }
   
    public function printConfigurationThree($list) {
        echo "<ul>";
        foreach($list as $entry) {
            /* @var $entry \core_cartuning_CarTuningData */
            $test = clone $entry;
            unset($test->subEntries);
            echo "<li entry='".json_encode($test)."'>" . $entry->name;
            if(sizeof($entry->subEntries)) {
                $this->printConfigurationThree($entry->subEntries);
            }
            echo "</li>";
        }
        echo "</ul>";
    }
    
    public function loadSettings() {
        ?>
        <link rel="stylesheet" href="/js/jstree/themes/default/style.min.css" />
        <script src="/js/jstree/jstree.min.js"></script>
        
        <div style="padding: 10px;" id="html1">
            <ul>
                <li>Tuningdata
                           <? $this->printConfigurationThree($this->getApi()->getCarTuningManager()->getCarTuningData(null)); ?>
                </li>
            </ul>
      </div>
        <style>
            .CarTuningApplication .configurationbox { position:absolute; right: 10px; top: 10px; background-color:#FFF; padding: 10px; border: solid 1px #BBB; }
        </style>
        <div class="configurationbox">
            <table>
                <tr>
                    <td colspan="2"><b>Information about <span class="name"></span></b></td>
                </tr>
                <tr>
                    <td>originalHp</td>
                    <td><input gs_name="originalHp"></td>
                </tr>
                <tr>
                    <td>originalNm</td>
                    <td><input gs_name="originalNm"></td>
                </tr>
                <tr>
                    <td>maxHp</td>
                    <td><input gs_name="maxHp"></td>
                </tr>
                <tr>
                    <td>maxNw</td>
                    <td><input gs_name="maxNw"></td>
                </tr>
                <tr>
                    <td>normalHp</td>
                    <td><input gs_name="normalHp"></td>
                </tr>
                <tr>
                    <td>normalNw</td>
                    <td><input gs_name="normalNw"></td>
                </tr>
            </table>
        </div>
        <span class="gs_button save_tuning_data"><? echo $this->__f("Save data"); ?></span>
        <script>
             $('#html1').jstree({
            "core" : {
              "check_callback" : true
            },
           "plugins" : [ "dnd", "contextmenu", "sort" ]
         });
         $('#html1').on('changed.jstree', function (e, data) {
             var li = $(e.target).find('.jstree-clicked').closest('li');
             app.CarTuningApplication.loadEntry(JSON.parse(li.attr('entry')));
          });
         $('#html1').on('rename_node.jstree', function (e, data) {
             var entryresult = {};
             if($("li#"+data.node.id).attr('entry') !== undefined) {
                 entryresult = JSON.parse($("li#"+data.node.id).attr('entry'));
             }
             entryresult.name = data.text;
             app.CarTuningApplication.updateNode(data.node.id, JSON.stringify(entryresult));
             app.CarTuningApplication.loadEntry(entryresult);
          });
        </script>
        <style>
            .vakata-context { z-index: 101; }
        </style>
        <?
    }
    
    function finalizeData($data) {
        foreach($data as $key => $entry) {
            foreach($entry as $key2 => $val) {
                if($key2 != "subEntries") {
                    if(!$data[$key][$key2]) {
                        unset($data[$key][$key2]);
                    }
                } else {
                    $data[$key][$key2] = $this->finalizeData($data[$key][$key2]);
                }
            }
        }
        return $data;
    }
    
    function saveTuningData() {
       $data = $this->finalizeData($_POST['data']['data']);
       $this->getApi()->getCarTuningManager()->saveCarTuningData($data);
    }
}

?>
