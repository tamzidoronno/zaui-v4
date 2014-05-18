<?php

include '../loader.php';
$factory = IocContainer::getFactorySingelton();

if ($_GET['action'] == "sync") {
    $factory->getApi()->getSedoxProductManager()->sync();
}

if ($_GET['action'] == "prepare") {
    $page = $factory->getApi()->getPageManager()->getPage("productview");
    if (!$page) {
        $page = $factory->getApi()->getPageManager()->createPageWithId(0, null, "productview");
        echo "Page created";
    }
    echo "<pre>";
    print_r($page);
    echo "</pre>";
}
?>
