<?php

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Description of ContentManager
 *
 * @author ktonder
 */
class ContentManager extends PluginBase {
    public static function render($name, $commonPage=false) {
        $contentManager = new ContentManager();
        $contentManager->renderInner($name, $commonPage);
    }
    
    private function renderInner($name, $commonPage=false) {
        $database = new Database();
        
        if(isset($_SESSION['language'])) {
            $origname = $name;
            $name .= "_" . $_SESSION['language'];
        }
        
        $data = $database->get($name, get_class($this));
        if(!$data) {
            $data = $database->get($origname, get_class($this));
        }
        
        
        echo "<div class='contentarea'>";
        
        if (!count($data)) {
            $this->saveData($name, "new entry", $commonPage);
            $data = $database->get($name, get_class($this));
        } 
        
        echo $data[0]['content'];

        if ($this->isLoggedIn()) {
            echo "<span class='editcontentbutton' keyid='$name'>[ edit ]</span>";
        }
        echo "</div>";
    }
    
    public function save() {
        $database = new Database();
        $data = $database->save($_POST['dataid'], $_POST['data'], get_class($this));
    }
    
    public function getAllKeysForPage($pageName) {
        $database = new Database();
        return $database->getAllKeysForPage($pageName, 'ContentManager');
    }

    public function saveData($id, $content, $commonPage=false) {
        
        $content = [
            'content' => $content,
            'id' => $id
        ];
        
        $database = new Database();
        $database->save($content, get_class($this), $commonPage);
        
    }
    
    public function getData($name) {
        $database = new Database();
        $data = $database->get($name, get_class($this));
        return $data[0]['content'];
    }

}
