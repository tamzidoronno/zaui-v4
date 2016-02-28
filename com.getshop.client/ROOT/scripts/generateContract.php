<?php
chdir("../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();
//exit(0);
$userId = $_GET['userid'];

/**
 * Variables: userid, engine, bookingId, roomId
 */

$user = $factory->getApi()->getUserManager()->getUserById($userId);
$booking = $factory->getApi()->getPmsManager()->getBooking($_GET['engine'], $_GET['bookingId']);
$room = $booking->rooms[0];
$types = $factory->getApi()->getBookingEngine()->getBookingItemTypes($_GET['engine']);
$products = $factory->getApi()->getProductManager()->getAllProducts();

$taxgroups = $factory->getApi()->getProductManager()->getTaxes();
$apps = $factory->getApi()->getPageManager()->getApplications();
$item = $factory->getApi()->getBookingEngine()->getBookingItem($_GET['engine'], $room->bookingItemId);

$selectedType = false;
foreach($types as $type) {
    if($type->id == $room->bookingItemTypeId) {
        $selectedType = $type;
        break;
    }
}

if(!$selectedType) {
    echo "Room type: " . $type->roomType . " not found, please set up room type on room: " . $type->roomName;
    echo "<hr>";
    print_r($type);
    exit(0);
}


function replacevariables($content) {
    global $user, $item, $room, $selectedType, $hotelbookingmanagementapp;
    
    $totalPrice = $room->price * (1+($room->taxes/100));

    $content = str_replace("gsnavn", $user->fullName, $content);
    $content = str_replace("gsorgfnr", $user->birthDay, $content);
    $content = str_replace("gspostaddr", $user->address->address . ", " . $user->address->postCode . " " . $user->address->city, $content);
    $content = str_replace("gsrom", $item->bookingItemName, $content);
    $content = str_replace("gsareal", $selectedType->name, $content);
    $content = str_replace("gsstartdato", date("d.m.Y", strtotime($room->date->start)), $content);
    $content = str_replace("gsdagensdato", date("d.m.Y", time()), $content);
    $content = str_replace("gsdagimaned", date("d", strtotime($room->date->start)), $content);
    $content = str_replace("gsyear", date("Y", strtotime($room->date->start)), $content);
    $content = str_replace("gspris", $room->price, $content);
    $content = str_replace("gstaxes", $room->taxes, $content);
    $content = str_replace("gstotalprice", $totalPrice, $content);
    $content = str_replace("gsadmingebyr", "0", $content);
    $content = str_replace("gspostnr", $user->address->postCode, $content);
    $content = str_replace("gssted", $user->address->city, $content);
    $content = str_replace("gsadresse", $user->address->address, $content);
    
    if($_GET['engine'] == "semlagerhotell") {
        $content = str_replace("gsutleiernavn", "Sem Lagerhotell.no AS", $content);
        $content = str_replace("gsutleieradresse", "Døvleveien 23", $content);
        $content = str_replace("gsutleierpostnr", "SEM", $content);
        $content = str_replace("gsutleiersted", "3170", $content);
    } else {
        $content = str_replace("gsutleiernavn", "Sem Lagerhotell.no AS", $content);
        $content = str_replace("gsutleieradresse", "Døvleveien 23", $content);
        $content = str_replace("gsutleierpostnr", "SEM", $content);
        $content = str_replace("gsutleiersted", "3170", $content);
    }
    return $content;
}

//echo "<pre>";
//print_r($user);
//echo "</pre>";

$tmpFolder = uniqid();
mkdir("/tmp/$tmpFolder");
if(sizeof($user->company) == 0) {
    $extension = "private";
} else {
    if($user->company[0]->vatRegisterd) {
        $extension = "company";
    } else {
        $extension = "company_ex_taxes";
    }
}
if(!isset($_GET['bilag'])) {
    $filename = "contract_$extension.docx";
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