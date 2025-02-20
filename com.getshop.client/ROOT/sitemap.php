<?php

include '../loader.php';
$factory = IocContainer::getFactorySingelton();
header("Content-Type: text/xml;charset=UTF8");

$allProducts = $factory->getApi()->getProductManager()->getAllProducts();
$lists = $factory->getApi()->getListManager()->getLists();


echo "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
echo "<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">";

$address = "http://" . $_SERVER['SERVER_NAME'];

foreach ($allProducts as $product) {
    echo "<url>\n";
    echo "<loc>" . $address .  \GetShopHelper::makeSeoUrl($product->uniqueName)."</loc>\n";
    echo "</url>\n";
}

foreach ($lists as $list) {
    $entries = $factory->getApi()->getListManager()->getList($list);
    if(isset($entries)) {
        foreach($entries as $entry) {
            if($entry->pageId != "home" && strlen($entry->pageId) < 20) {
                continue;
            }
            echo "<url>\n";
            echo "<loc>" . $address . GetShopHelper::makeSeoUrl($entry->uniqueName) . "</loc>\n";
            echo "</url>\n";

        }
    }
}
echo "</urlset>";
