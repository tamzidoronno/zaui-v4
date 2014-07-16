<?php
function getTuningfile($product) {
    foreach ($product->binaryFiles as $binFile) {
        if ($binFile->fileType != "Original") {
            return $binFile->id;
        }
    }
    
    return 0;
}

chdir("../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();
$orders = $factory->getApi()->getSedoxProductManager()->getProductsFirstUploadedByCurrentUser();

$orders = array_splice($orders , 0, 10);

$files = array();

foreach ($orders as $product) {
    /* @var $product SedoxProduct */
	$data = array();
    
	    
	$data['Car'] = $product->brand;
	$data['Model'] = $product->model;
	$data['EngineSize'] = $product->engineSize;
	$data['HorsePower'] = $product->power;
	$data['Year'] = $product->year;
	$data['tuningfileid'] = getTuningfile($product);
	$data['ProductId'] = (int)$product->id;
	$data['dateCreated'] = $product->rowCreatedDate;
    $data['comment'] = $product->comment;
    $data['tool'] = $product->tool;
    $data['gear'] = $product->gearType;
    $data['started'] = $product->started == true;
	$files[] = $data;
}

echo json_encode($files);


?>

