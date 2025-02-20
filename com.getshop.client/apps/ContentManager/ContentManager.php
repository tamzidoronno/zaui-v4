<?php

namespace ns_320ada5b_a53a_46d2_99b2_9b0b26a7105a;

/**
 * Description of ContentManager
 *
 * @author boggi
 */
class ContentManager extends \WebshopApplication implements \Application {

    public $content = "";
    public $id = 1002;
    public $showEdit = false;

    //put your code here
    public function getDescription() {
        return $this->__w("Add text to your page with one of the best text editors on the web for manipulating text.");
    }

    public function getAvailablePositions() {
        return "left middle right";
    }

    public function getName() {
        return $this->__w("Text editor");
    }

    public function postProcess() {
        
    }

    public function preProcess() {
        
    }

    public function applicationAdded() {
    }

    public function getStarted() {
        echo $this->__f("Add this application and get started adding content to your page.");
    }

    public function loadContent() {
        $this->content = $this->getApi()->getContentManager()->getContent($this->getConfiguration()->id);
        
        if (\ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::isAdministrator()) {
            return;
        }
        
        preg_match_all('~{(.+?)}~', $this->content, $m);
        
        if (count($m[0])) {
            foreach ($m[0] as $key) {
                if (isset($_SESSION[$key])) {
                    $this->content = str_replace($key, $_SESSION[$key], $this->content);
                }
            }
        }
    }
    
    public function renderConfig() {
        $this->includefile("settings");
    }

    public function render() {
        $this->loadContent();
        $this->includefile("TContentManager");
        if ($this->showEdit) {
            echo "Showing edit!";
        }
    }
    
    public function activatePdf() {
        $this->setConfigurationSetting("haspdf", $_POST['data']['pdf']);
    }
    
    public function hasPDF() {
        $setting = $this->getConfigurationSetting("haspdf");
        if(!$setting) {
            return 0;
        }
        return (int)$setting;
    }

    public function getContent() {
        //Fix to display images that has no http url attach to it.
                $content = str_replace("src=\"www", "src=\"http://www", $this->content);
        if(!$content) {
            $content = $this->__f("Text area") . "<br>";
            $content .= "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.";
        }
        return $content;
    }

    public function isEditable() {
        if (isset($_SESSION['CONTENTMANAGER'][$this->getConfiguration()->id])) {
            return true;
        }
        return false;
    }
    
    public function saveGlobalSettings() {
        $this->setConfigurationSetting("isSimpleModeActivated", $_POST['isSimpleModeActivated']);
    }
    
    public function isSimpleModeActivated() {
        return $this->getConfigurationSetting("isSimpleModeActivated") == "true";
    }

    public function saveContent() {
//        $data1 =  strip_tags($_POST['data']['content'], "<img>");
//        
//        $content = str_replace("&nbsp;", "",  $data1);
//        $content = str_replace(" ", "",  $content);
//        $content = trim(preg_replace('/\s+/', '',$content));
//        $content = trim($content);
//        if (strlen($content) == 0 || !$content) {
//            $pageId = $this->getPage()->id;
//            $this->getApi()->getPageManager()->removeApplication($this->getConfiguration()->id, $pageId);
//            $this->content = "";
//            $this->getFactory()->clearCachedPageData();
//            $this->getFactory()->initPage();
//        } else {
            $this->getApi()->getContentManager()->saveContent($this->getConfiguration()->id, $_POST['data']['content']);
//            $this->content = $content;
//        }
    }

    public function getYoutubeId() {
        return "98plWfJSLEo";
    }

    public function editContent($withLoad = true) {
        if ($withLoad) {
            $this->loadContent();
        }
        $uid = uniqid();
        ?>
        <div gsname="content" gstype="ckeditor" class='content' style='min-height: 400px;' id='editcontentfield_<? echo $uid; ?>'>
        <? echo $this->content; ?>
        </div>

        <? if ($withLoad) { ?>
            <span class="button" id="savecontentmanager">
                <div class="rightglare"></div>
                <div class="filler"></div>  
                <ins id="saveconfiguration"><? echo $this->__f("Save"); ?></ins>
            </span>
            <?
        }
    }

}
?>
