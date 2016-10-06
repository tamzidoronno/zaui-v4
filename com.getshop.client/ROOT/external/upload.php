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
$useCredit = isset($json['useCredit']) && $json['useCredit'] == "true" ? true : false;

$brand = $json['Car'];
$model = $json['Model'];
$engineSize = $json['EngineSize'];
$power = $json['HorsePower'];
$tool = $json['Tool'];
$year = $json['Year'];
$comment = $json['extraComment'];
$type = isset($json['upload_type']) ? $json['upload_type'] : "";

if (isset($json['original_filename'])) {
    $filename = $json['original_filename'];
}

// Save filecontent
$sedoxProduct = new \core_sedox_SedoxProduct();
$sedoxProduct->brand = $brand;
$sedoxProduct->model = $model;
$sedoxProduct->engineSize = $engineSize;
$sedoxProduct->power = $power;
$sedoxProduct->year = $year;
$sedoxProduct->tool = $tool;
$sedoxProduct->gearType = $geartype;
$sedoxProduct->type = $type;

/**
 * type (String):
 * "car"
 * "tractor"
 * "truck"
 */

$options = new \core_sedox_SedoxBinaryFileOptions();
$options->requested_adblue = isset($json['upload_adblue']) && $json['upload_adblue'] == "true";
$options->requested_decat = isset($json['upload_decat']) && $json['upload_decat'] == "true";
$options->requested_dpf = isset($json['upload_dpf']) && $json['upload_dpf'] == "true";
$options->requested_dtc = isset($json['upload_dtc']) && $json['upload_dtc'] == "true";
$options->requested_egr = isset($json['upload_egr']) && $json['upload_egr'] == "true";
$options->requested_vmax = isset($json['upload_vmax']) && $json['upload_vmax'] == "true";
$options->requested_remaptype = isset($json['upload_remaptype']) ? $json['upload_remaptype'] : ""; 
/* 
    requested_remaptype (String):
    "Stage 1 Power"
    "Stage 2 Power"
    "Combi Power/ECO"
    "Full ECO"
    "Other"
 */
$options->requested_flaps = isset($json['upload_flaps']) && $json['upload_flaps'];

$reference = "";
if (isset($json['reference'])) {
    $reference = $json['reference'];
}

$factory->getApi()->getSedoxProductManager()->createSedoxProduct($sedoxProduct, $filecontent, $filename, $slave, "windowsapp", $comment, $useCredit, $options, $reference);
        
?>