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

if(!$selectedType) {
    echo "Room type: " . $room->roomType . " not found, please set up room type on room: " . $room->roomName;
    echo "<hr>";
    print_r($room);
    exit(0);
}

$selectedProduct =false;
foreach($products as $product) {
    if($product->sku == $selectedType->name) {
        $selectedProduct = $product;
        break;
    }
}

if(!$selectedProduct) {
    echo "Product not found. :( on " . $product->sku;
    exit(0);
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
    $taxes = @$selectedProduct->price * ($group->taxRate/100);
    $totalPrice += $taxes;
}

function replacevariables($content) {
    global $user, $room, $selectedProduct, $selectedType, $order, $hotelbookingmanagementapp, $taxes, $totalPrice;
    $content = str_replace("gsnavn", $user->fullName, $content);
    $content = str_replace("gsorgfnr", $user->birthDay, $content);
    $content = str_replace("gspostaddr", $user->address->address . ", " . $user->address->postCode . " " . $user->address->city, $content);
    $content = str_replace("gsrom", $room->roomName, $content);
    $content = str_replace("gsareal", $selectedType->name, $content);
    $content = str_replace("gsstartdato", date("d.m.Y", strtotime($order->startDate)), $content);
    $content = str_replace("gsdagensdato", date("d.m.Y", time()), $content);
    $content = str_replace("gsdagimaned", date("d", strtotime($order->startDate)), $content);
    $content = str_replace("gsyear", date("Y", strtotime($order->startDate)), $content);
    $content = str_replace("gspris", $selectedProduct->price, $content);
    $content = str_replace("gstaxes", $taxes, $content);
    $content = str_replace("gstotalprice", $totalPrice, $content);
    $content = str_replace("gsadmingebyr", $order->bookingFee, $content);
    $content = str_replace("gspostnr", $user->address->postCode, $content);
    $content = str_replace("gssted", $user->address->city, $content);
    $content = str_replace("gsadresse", $user->address->address, $content);
    
    $content = str_replace("gsutleiernavn", $hotelbookingmanagementapp->settings->{"utleier_navn"}->value, $content);
    $content = str_replace("gsutleieradresse", $hotelbookingmanagementapp->settings->{"utleier_adresse"}->value, $content);
    $content = str_replace("gsutleierpostnr", $hotelbookingmanagementapp->settings->{"utleier_postnr"}->value, $content);
    $content = str_replace("gsutleiersted", $hotelbookingmanagementapp->settings->{"utleier_sted"}->value, $content);
    return $content;
}

//echo "<pre>";
//print_r($user);
//echo "</pre>";

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
        $filename = "autogiro.docx";
    } else {
        $filename = "bilag.docx";
    }
} else {
    $filename = "bilag_$extension.docx";
}
if(!file_exists("scripts/birkelunden/$filename")) {
    echo "File: $filename does not exists.";
    exit(0);
}
`cp -r scripts/birkelunden/$filename /tmp/$tmpFolder/$filename`;

chdir("/tmp/$tmpFolder/");
`unzip *.docx`;

$content = file_get_contents("/tmp/$tmpFolder/word/document.xml");
$content = replacevariables($content);


header('Content-Type: application/vnd.openxmlformats-officedocument.wordprocessingml.document');
header('Content-Disposition: attachment;filename="document.docx"');
file_put_contents("/tmp/$tmpFolder/word/document.xml", $content);
chdir("/tmp/$tmpFolder/");
`zip -r /tmp/document.docx *`;
$handle = fopen("/tmp/document.docx", "r");
$contents = fread($handle, filesize("/tmp/document.docx"));
fclose($handle);
`rm -rf /tmp/$tmpFolder`;
echo $contents;
?>