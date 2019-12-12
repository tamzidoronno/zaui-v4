<?php

/* @var $this ns_57db782b_5fe7_478f_956a_ab9eb3575855\SalesPointNewSale */
chdir("../../../../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton(false);
$product = $factory->getApi()->getProductManager()->getProduct($_POST['productid']);

$tab = $factory->getApi()->getPosManager()->getTabForPga($_POST['tabid']);

if ($tab == null) {
    $tab = $factory->getApi()->getPosManager()->createTabForPga($_POST['tabid'], "tmp tab created by PGA");
}

$cartItem = new \core_cartmanager_data_CartItem();
$cartItem->count = 1;
$cartItem->product = $product;

if (isset($_POST['extraIds'])) {
    $cartItem->product->selectedExtras = array();
    foreach ($_POST['extraIds'] as $id => $arr) {
        $cartItem->product->selectedExtras[$id] = $arr;
    }
}

$factory->getApi()->getPosManager()->addToTabPga($tab->id, $cartItem);