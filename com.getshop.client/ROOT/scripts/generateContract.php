<?php
chdir("../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();
//exit(0);
$userId = $_GET['userid'];


$user = $factory->getApi()->getUserManager()->getUserById($userId);
$order = $factory->getApi()->getHotelBookingManager()->getReservationByReferenceId($_GET['refid']);
$room = $factory->getApi()->getHotelBookingManager()->getRoom($order->roomIds[0]);
$types = $factory->getApi()->getHotelBookingManager()->getRoomTypes();
$products = $factory->getApi()->getProductManager()->getAllProducts();

$taxgroups = $factory->getApi()->getProductManager()->getTaxes();
$apps = $factory->getApi()->getPageManager()->getApplications();
$hotelbookingmanagementapp = null;
foreach($apps as $app) {
    if($app->appName === "HotelbookingManagement" && isset($app->settings->{"utleier_postnr"})) {
        $hotelbookingmanagementapp = $app;
    }
}


$selectedType = false;
foreach($types as $type) {
    /* @var $type core_hotelbookingmanager_RoomType */
    if($type->id == $room->roomType) {
        $selectedType = $type;
        break;
    }
}

$selectedProduct =false;
foreach($products as $product) {
    if($product->sku == $selectedType->name) {
        $selectedProduct = $product;
        break;
    }
}

foreach($taxgroups as $group) {
    if($group->groupNumber == $selectedProduct->taxgroup) {
        $foundgroup = $group;
        break;
    }
}
$taxes = 0;
$totalPrice = $selectedProduct->price;
if($foundgroup) {
    $taxes = $selectedProduct->price * ($group->taxRate/100);
    $totalPrice += $taxes;
}

function replacevariables($content) {
    global $user, $room, $selectedProduct, $selectedType, $order, $hotelbookingmanagementapp, $taxes, $totalPrice;
    $content = str_replace("[navn]", $user->fullName, $content);
    $content = str_replace("[org_fnr]", $user->birthDay, $content);
    $content = str_replace("[postaddr]", $user->address->address . ", " . $user->address->postCode . " " . $user->address->city, $content);
    $content = str_replace("[rom]", $room->roomName, $content);
    $content = str_replace("[areal]", $selectedType->name, $content);
    $content = str_replace("[startdato]", date("d.m.Y", strtotime($order->startDate)), $content);
    $content = str_replace("[dagensdato]", date("d.m.Y", time()), $content);
    $content = str_replace("[dag_i_maned]", date("d", strtotime($order->startDate)), $content);
    $content = str_replace("[year]", date("Y", strtotime($order->startDate)), $content);
    $content = str_replace("[pris]", $selectedProduct->price, $content);
    $content = str_replace("[taxes]", $taxes, $content);
    $content = str_replace("[total_price]", $totalPrice, $content);
    $content = str_replace("[admingebyr]", $order->bookingFee, $content);
    
    $content = str_replace("[utleier_navn]", $hotelbookingmanagementapp->settings->{"utleier_navn"}->value, $content);
    $content = str_replace("[utleier_adresse]", $hotelbookingmanagementapp->settings->{"utleier_adresse"}->value, $content);
    $content = str_replace("[utleier_postnr]", $hotelbookingmanagementapp->settings->{"utleier_postnr"}->value, $content);
    $content = str_replace("[utleier_sted]", $hotelbookingmanagementapp->settings->{"utleier_sted"}->value, $content);
    return $content;
}

//echo "<pre>";
//print_r($user);
//echo "</pre>";
//exit(0);

$tmpFolder = uniqid();
mkdir("/tmp/$tmpFolder");
if($user->isPrivatePerson) {
    $extension = "private";
} else {
    if($user->mvaRegistered) {
        $extension = "company";
    } else {
        $extension = "company_ex_taxes";
    }
}
if(isset($_GET['type'])) {
    if($_GET['type'] == "standard") {
        $filename = "contract_$extension.docx";
    } else if($_GET['type'] == "autogiro") {
        $filename = "autogiro_$extension.docx";
    } else {
        $filename = "bilag_$extension.docx";
    }
} else {
    $filename = "bilag_$extension.docx";
}
if(!file_exists("scripts/birkelunden/$filename")) {
    echo "File: $filename does not exists.";
    exit(0);
}
header('Content-Type: application/vnd.openxmlformats-officedocument.wordprocessingml.document');
header('Content-Disposition: attachment;filename="document.docx"');
`cp -r scripts/birkelunden/$filename /tmp/$tmpFolder/$filename`;

chdir("/tmp/$tmpFolder/");
`unzip *.docx`;

$content = file_get_contents("/tmp/$tmpFolder/word/document.xml");
$content = replacevariables($content);
file_put_contents("/tmp/$tmpFolder/word/document.xml", $content);
chdir("/tmp/$tmpFolder/");
`zip -r /tmp/document.docx *`;
$handle = fopen("/tmp/document.docx", "r");
$contents = fread($handle, filesize("/tmp/document.docx"));
fclose($handle);
`rm -rf /tmp/$tmpFolder`;
echo $contents;
?>