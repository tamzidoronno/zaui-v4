<?php

function getTuningfile($product, $orgFileName = false) {
    foreach ($product->binaryFiles as $binFile) {
        if ($orgFileName && $binFile->fileType == "Original") {
            return $binFile->orgFilename;
        }

        if ($binFile->fileType != "Original") {
            return $binFile->id;
        }
    }

    return 0;
}

chdir("../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();



$filterdata = new \core_sedox_FilterData();
$filterdata->ascending = false;
$filterdata->filterText = "";
$filterdata->sortBy = "sedoxproduct_sedoxid";
$filterdata->pageNumber = 1;
$filterdata->pageSize = 10;

$orders = $factory->getApi()->getSedoxProductManager()->getProductsFirstUploadedByCurrentUser($filterdata);
$orders = array_splice($orders, 0, 10);

$files = array();

foreach ($orders as $product) {
    /* @var $product SedoxProduct */
    $data = array();

    $data['Car'] = $product->brand;
    $data['Model'] = $product->model;
    $data['EngineSize'] = $product->engineSize;
    $data['HorsePower'] = $product->power;
    $data['Year'] = $product->year;
    $data['originalFileName'] = getTuningfile($product, true);
    $data['tuningfileid'] = getTuningfile($product);
    $data['ProductId'] = (int) $product->id;
    $data['dateCreated'] = $product->rowCreatedDate;
    $data['comment'] = $product->comment;
    $data['tool'] = $product->tool;
    $data['gear'] = $product->gearType;
    $data['started'] = $product->started == true;
    $data['originalChecksum'] = $product->originalChecksum;
    $files[] = $data;
}

echo json_encode($files);
?>