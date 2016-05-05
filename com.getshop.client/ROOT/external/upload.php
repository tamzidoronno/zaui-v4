<?php
chdir("../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();


$jsonstring = urldecode($_GET['request']);
$json = json_decode(stripslashes($jsonstring), true);

$binaryFileRead = fread(fopen($_FILES["file"]["tmp_name"], "r"), filesize($_FILES["file"]["tmp_name"]));
$filename = $_FILES["file"]["filename"];
$filecontent = base64_encode($binaryFileRead);
$slave = null;
$geartype = $json['ManualGear'] == true ? "man" : "auto";
$useCredit = "yes";

$brand = $json['Car'];
$model = $json['Model'];
$engineSize = $json['EngineSize'];
$power = $json['HorsePower'];
$tool = $json['Tool'];
$year = $json['Year'];
$comment = $json['extraComment'];

// Save filecontent
$sedoxProduct = new \core_sedox_SedoxProduct();
$sedoxProduct->brand = $brand;
$sedoxProduct->model = $model;
$sedoxProduct->engineSize = $engineSize;
$sedoxProduct->power = $power;
$sedoxProduct->year = $year;
$sedoxProduct->tool = $tool;
$sedoxProduct->gearType = $geartype;

$options = new \core_sedox_SedoxBinaryFileOptions();

$reference = "";
if (isset($json['reference'])) {
    $reference = $json['reference'];
}

$factory->getApi()->getSedoxProductManager()->createSedoxProduct($sedoxProduct, $filecontent, $filename, $slave, "windowsapp", $comment, $useCredit, $options, $reference);
        
?>