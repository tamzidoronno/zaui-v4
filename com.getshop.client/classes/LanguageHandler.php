<?php

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Description of LanguageHandler
 *
 * @author ktonder
 */
class LanguageHandler {

    private $lang = "en";

    public function getErrorMessage($errorCode) {
        $ret = null;
        if (($handle = fopen("../language/".$this->lang."/errors.csv", "r")) !== FALSE) {
            while (($data = fgetcsv($handle, 1000, ",")) !== FALSE) {
                if ($data[0] == $errorCode) {
                    $ret = $data[1];
                    break;
                }
            }
            fclose($handle);
        }
        return $ret;
    }


}

?>
