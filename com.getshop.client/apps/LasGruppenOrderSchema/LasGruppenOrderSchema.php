<?php

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Description of LasGruppenOrderSchema
 *
 * @author ktonder
 */

namespace ns_7004f275_a10f_4857_8255_843c2c7fb3ab;

class LasGruppenOrderSchema extends \ApplicationBase implements \Application {
    public function getDescription() {
        return "TEST";
    }

    public function getName() {
        return $this->__f("LasGruppen Ordering Schema");
    }

    public function render() {
        $this->includefile("orderingschema");
    }
    
    public function getBrReg() {
        $company = $this->getApi()->getUtilManager()->getCompanyFromBrReg($_POST['data']['number']);
        echo json_encode($company);
        die();
    }

}
