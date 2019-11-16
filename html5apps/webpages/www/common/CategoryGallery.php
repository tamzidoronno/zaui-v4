<?php

/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

class CategoryGallery extends PluginBase {
     public static function render($name, $commonPage=false) {
         $gallery = new CategoryGallery();
         $gallery->renderInner($name, $commonPage);
    }
    
    
    public function save() {
        $database = new Database();
        $data = $database->save($_POST['dataid'], $_POST['data'], get_class($this));
    }
    
    public function getAllKeysForPage($pageName) {
        $database = new Database();
        return $database->getAllKeysForPage($pageName, 'CategoryGallery');
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

    public function renderInner($name, $commonPage) {
        $res = $this->getData($name);
        $res = json_encode($res);
        
        
        if ($this->isLoggedIn()) {
            echo "<script>gs_categorygalleryisloggedon=true;</script>";
        } else {
            echo "<script>gs_categorygalleryisloggedon=false;</script>";
        }
     
        echo "<div class='gscontatentgalleryapp' id='$name'></div><script>$('#$name').CategoryGallery($res);</script>";
        
    }

}