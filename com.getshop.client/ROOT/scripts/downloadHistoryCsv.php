<?php
header("Content-type: text/csv");
header("Content-Disposition: attachment; filename=file.csv");
header("Pragma: no-cache");
header("Expires: 0");

chdir("../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();
$apps = $factory->getApi()->getPageManager()->getApplicationsForPage("5cb79939-9a26-4ed1-9ffb-424b814b3ad5");
$appInstance = $factory->getApplicationPool()->getApplicationInstance("61ac9405-0eef-4621-b162-697f2b8c7eac");

$sedoxPanel = null;
foreach ($apps as $app) {
    if ($app->id == "61ac9405-0eef-4621-b162-697f2b8c7eac") {
        $sedoxPanel = $app;
        break;
    }
}

/** @var \ns_32b5f680_dd8d_11e3_8b68_0800200c9a66\SedoxUserPanel */
$sedoxPanel = $factory->getApplicationPool()->createAppInstance($sedoxPanel);

if ($_GET['version'] == "credithistory") {
    $hists = $sedoxPanel->getLatestCreditHistory();
    $hists = array_reverse($hists);
    foreach ( $hists as $hist) {
        $id = str_replace(",", " ", $hist->transactionReference);
        $desc = str_replace(",", " ", $hist->description);
        $amount = str_replace(",", " ", $hist->amount);
        $date = str_replace(",", " ", $hist->dateCreated);
        echo $id.",".$desc.",".$amount.",".$date."\n";    
    }
}

if ($_GET['version'] == "uploadhistory") {
    $hists = $sedoxPanel->getLatestUploadedProducts();
    foreach ( $hists as $product) {
        $name = str_replace(",", " ", $product->brand . " " . $product->model . " " . $product->engineSize . " " . $product->power . " " . $product->year);
        $id = str_replace(",", " ", $product->id);
        $reference = str_replace(",", " ", ns_23fac58b_5066_4222_860c_a9e88196b8a1\SedoxProductView::getReference($product));
        $date = str_replace(",", " ", $product->rowCreatedDate);
        
        echo "$name,$id,$reference,$date\n";
    }

}

if ($_GET['version'] == "slaves") {
    $hists = $sedoxPanel->getOrders();
    $slaves = $sedoxPanel->getSlaves();
    foreach ($slaves as $slave) {
        $histories = array_reverse($slave->creditAccount->history);
        foreach ($histories as $history) {
            echo str_replace(",", " ", $history->dateCreated).",";
            echo str_replace(",", " ", $history->transactionReference).",";
            echo str_replace(",", " ", $history->description).",";
            echo str_replace(",", " ", $history->amount)."\n";
        }
    }

}

if ($_GET['version'] == "downloadhistory") {
    $hists = $sedoxPanel->getOrders();
    foreach ( $hists as $order) {
        $product = $sedoxPanel->getApi()->getSedoxProductManager()->getProductById($order->productId);
        if ($product == null) {
            continue;
        }
        $name = $product->brand . " " . $product->model . " " . $product->engineSize . " " . $product->power . " " . $product->year;
        $id = $product->id;
        $date = $product->rowCreatedDate;

        echo str_replace(",", " ", $id).",";
        echo str_replace(",", " ", $name).",";
        echo str_replace(",", " ", $date).",";
        echo "\n";
    }
}


