<?php

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Description of SedoxLatestProducts
 *
 * @author ktonder
 */
namespace ns_8c666b2a_3dd2_4526_9f3d_1edae1e5a7af;

class SedoxLatestProducts extends \ApplicationBase implements \Application {
	
	public function getDescription() {
		return "Sedox Latests Products";
	}

	public function getName() {
		return "SedoxLatestProducts";
	}

	public function render() {
		$this->includefile("sedoxlatestsproducts");
	}
	
	public function getLatestProducts() {
		$prods = $this->getApi()->getSedoxProductManager()->getLatestProductsList(50);
		return $prods;
	}

}
