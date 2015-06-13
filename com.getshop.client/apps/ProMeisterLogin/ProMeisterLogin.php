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

//put your code here
}
