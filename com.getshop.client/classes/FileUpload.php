<?php

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Description of FileUpload
 *
 * @author boggi
 */
class FileUpload {

    public static function gen_uuid() {
        return sprintf('%04x%04x-%04x-%04x-%04x-%04x%04x%04x',
                        // 32 bits for "time_low"
                        mt_rand(0, 0xffff), mt_rand(0, 0xffff),
                        // 16 bits for "time_mid"
                        mt_rand(0, 0xffff),
                        // 16 bits for "time_hi_and_version",
                        // four most significant bits holds version number 4
                        mt_rand(0, 0x0fff) | 0x4000,
                        // 16 bits, 8 bits for "clk_seq_hi_res",
                        // 8 bits for "clk_seq_low",
                        // two most significant bits holds zero and one for variant DCE1.1
                        mt_rand(0, 0x3fff) | 0x8000,
                        // 48 bits for "node"
                        mt_rand(0, 0xffff), mt_rand(0, 0xffff), mt_rand(0, 0xffff)
        );
    }

    public static function storeFile($content) {
        $id = \FileUpload::gen_uuid();
        
        if(!is_dir("../uploadedfiles")) {
            mkdir("../uploadedfiles");
        }
        
        $fp = fopen("../uploadedfiles/".$id, "w");
        fwrite($fp, $content);
        fclose($fp);
        chmod("../uploadedfiles/".$id, 0444);
        
        return $id;
    }
    
    public static function saveFileFromUrl($url, $id) {
        chmod("../uploadedfiles/".$id, 0755);
        if(!file_exists("../uploadedfiles/".$id."_orig")) {
            copy("../uploadedfiles/".$id, "../uploadedfiles/".$id."_orig");
            
            chmod("../uploadedfiles/".$id."_orig", 0444);
        }
        
        file_put_contents("../uploadedfiles/".$id, file_get_contents($url));
        chmod("../uploadedfiles/".$id, 0444);
    }
    
    public static function revertFileFromOriginal($id) {
        if(file_exists("../uploadedfiles/".$id."_orig")) {
            chmod("../uploadedfiles/".$id, 0755);
            chmod("../uploadedfiles/".$id."_orig", 0755);

            copy("../uploadedfiles/".$id."_orig", "../uploadedfiles/".$id);

            chmod("../uploadedfiles/".$id, 0444);
            chmod("../uploadedfiles/".$id."_orig", 0444);
        }
    }
}

?>
