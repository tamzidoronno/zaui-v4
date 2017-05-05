<?php
namespace ns_b3f563f6_2408_4989_8499_beabaad36574;

class ScormList extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "ScormList";
    }

    public function render() {
        $this->includefile("scorms");
    }

    public function printRow($scorm, $subgroup = false) {
        if (!$scorm)
            return;
        
        $isGroupedScormPackage = $scorm->groupedScormPackage;
    
        $link = "http://moodle.getshop.com/mod/scorm/getshopplayer.php?userid=".\ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::getUserObject()->id."&scormid=".$scorm->scormId;
        if ($isGroupedScormPackage) {
            $ids = implode(",", $scorm->groupedScormPackageIds);
            $link = "http://moodle.getshop.com/mod/scorm/getshopplayer.php?userid=".\ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::getUserObject()->id."&scormid=&scormids=".$ids;
        }
        
        $subgroupclass = $subgroup ? "subgroup" : "";
        ?>
        <div class="scormrow <? echo $subgroupclass; ?>">
            <div><? echo $scorm->scormName; ?></div>
            <div><? echo $scorm->groupedScormPackage ? "" : $scorm->score; ?></div>
            <div>
                <? 
                if ($scorm->groupedScormPackage) {
                    echo "";
                } else {
                    echo $scorm->passed ? $this->__f("Yes") : $this->__f("No"); 
                }
                ?>
            </div>
            <div>
                <? if (!$subgroup) { ?>
                <a style="color:blue;" target="_blank" href="<? echo $link; ?>"><? echo $this->__f("Go to test"); ?></a>
                <? } ?>
            
            </div>
        </div>
        <?
        
        if ($isGroupedScormPackage) {
            echo "<div class='groupedInformation'>".$this->__w("This course consist of multiple parts, make sure to complete all of them")."</div>";
        }
    }

    public function isCompanySelected() {
        return isset($_SESSION['ProMeisterSpiderDiager_current_user_id']) && $_SESSION['ProMeisterSpiderDiager_current_user_id'] == "company";
    }
    
    public function getUserId() {
        return isset($_SESSION['ProMeisterSpiderDiager_current_user_id_toshow']) ? $_SESSION['ProMeisterSpiderDiager_current_user_id_toshow'] : \ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::getUserObject()->id;
    }

}
?>
