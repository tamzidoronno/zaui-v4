<?php
chdir("../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();




$pageURL = 'http';
if (@$_SERVER["HTTPS"] == "on") {$pageURL .= "s";}
    $pageURL .= "://";
if ($_SERVER["SERVER_PORT"] != "80") {
    $pageURL .= $_SERVER["SERVER_NAME"].":".$_SERVER["SERVER_PORT"].$_SERVER["REQUEST_URI"];
} else {
    $pageURL .= $_SERVER["SERVER_NAME"].$_SERVER["REQUEST_URI"];
}

$pageURL = str_replace("generatepmssimpleeventpdf", "displaypmssimpleevent",$pageURL);

$storeId = $factory->getStore()->id;
$pdf = $factory->getApi()->getGetShop()->getBase64EncodedPDFWebPage($pageURL);


$readevent = $_GET['readevent'];
$res = $factory->getApi()->getPmsEventManager()->getEntryShort($_GET['engine'], $readevent);
$title = $res->title;

header('Pragma: public');  // required
header('Expires: 0');  // no cache
header("Content-type:application/pdf");
header("Content-Disposition:attachment;filename=" . $title . ".pdf");

echo base64_decode($pdf);
?>