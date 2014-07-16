<?php
chdir("../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();
$sedoxFileUpload = new \ns_a2172f9b_c911_4d9a_9361_89b57bc01d40\SedoxFileUpload();


$jsonstring = urldecode($_GET['request']);
$json = json_decode(stripslashes($jsonstring), true);

$binaryFileRead = fread(fopen($_FILES["file"]["tmp_name"], "r"), filesize($_FILES["file"]["tmp_name"]));
$filename = $_FILES["file"]["filename"];
$filecontent = base64_encode($binaryFileRead);

$brand = $json['Car'];
$model = $json['Model'];
$engineSize = $json['EngineSize'];
$power = $json['HorsePower'];
$tool = $json['Tool'];
$year = $json['Year'];
$slave = null;
$comment = $json['extraComment'];
$geartype = $json['ManualGear'] == true ? "man" : "auto";
$useCredit = "yes";
$sedoxFileUpload->saveFileContent($brand, $model, $engineSize, $power, $year, $tool, $comment, $geartype, $useCredit, $slave, $filename, $filecontent);

?>