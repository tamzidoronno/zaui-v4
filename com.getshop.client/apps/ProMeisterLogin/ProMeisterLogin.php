<?php

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Description of ProMeisterLogin
 *
 * @author ktonder
 */
namespace ns_2f98236f_b36d_4d5c_93c6_0ad99e5b3dc6;

class ProMeisterLogin extends \ApplicationBase implements \Application {
    public function getDescription() {
        return "ProMeister Login";
    }

    public function getName() {
        return "ProMeister Login";
    }

    public function render() {
        $this->includefile("loginform");
    }
    
    public function renderConfig() {
        echo "test";
    }
    
    public function getCheckType() {
        if ($_SESSION['group'] == "1cdd1d93-6d1b-4db3-8e91-3c30cfe38a4a") {
            return 1;
        }

        if ($_SESSION['group'] == "ddcdcab9-dedf-42e1-a093-667f1f091311" || $_SESSION['group'] == "608c2f52-8d1a-4708-84bb-f6ecba67c2fb") {
            return 2;
        }

        return 0;
    }
    
}
