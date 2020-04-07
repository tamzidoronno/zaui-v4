<?php

chdir("../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();

$entityBody = file_get_contents('php://input');

$res = json_decode($entityBody, true);

$failed = false;

$booking = $factory->getApi()->getPmsManager()->startBooking("hybelhotell");

if(!$res['innflyttingsdato'] || !$res['utflyttingsdato']) {
    $failed = "Ugyldig dato, datoen kan ikke være blank.";
}

$res['innflyttingsdato'] = str_replace("/", ".", $res['innflyttingsdato']);
$res['utflyttingsdato'] = str_replace("/", ".", $res['utflyttingsdato']);

$start = strtotime($res['innflyttingsdato']);
$end = strtotime($res['utflyttingsdato']);

$startYear = date("Y", $start);
$endYear = date("Y", $end);
if($endYear < 2020 || $startYear < 2020 || $start > $end) {
    $failed = "Ugyldig dato, datoen kan ikke være fra " . date("d.m.Y", $start). " til " . date("d.m.Y", $end);
}

$booking->registrationData->resultAdded = array();
$booking->registrationData->resultAdded['choosetyperadio'] = $res["privatbedrift"] == "privat" ? "registration_private" : "registration_company";
$booking->registrationData->resultAdded['company_vatNumber'] = $res["orgnr"];
$booking->registrationData->resultAdded['company_contact'] = $res["kontakpersonnavn"];
$booking->registrationData->resultAdded['company_name'] = $res["selskapsnavn"];
$booking->registrationData->resultAdded['company_email'] = $res["kontakpersonepost"];
$booking->registrationData->resultAdded['company_prefix'] = $res["kontakpersontelprefix"];
$booking->registrationData->resultAdded['company_phone'] = $res["kontakpersontel"];
$booking->registrationData->resultAdded['company_address_address'] = $res["firmaadresse"];
$booking->registrationData->resultAdded['company_address_postCode'] = $res["firmaadressepostnummer"];
$booking->registrationData->resultAdded['company_address_city'] = $res["firmaadressepoststed"];
$booking->registrationData->resultAdded['company_emailAddressToInvoice'] = $res["fakturaepost"];
$booking->registrationData->resultAdded['company_invoicenote'] = $res["fakturareferanse"];


$booking->registrationData->resultAdded['user_fullName'] = $res["privatnavn"];
$booking->registrationData->resultAdded['user_prefix'] = $res["privattelprefix"];
$booking->registrationData->resultAdded['user_cellPhone'] = $res["privattel"];
$booking->registrationData->resultAdded['user_emailAddress'] = $res["privatepost"];
$booking->registrationData->resultAdded['user_birthday'] = $res["fødselsdato"];



$booking->comments = array();

if($res['melding']) {
    $comment = new core_pmsmanager_PmsBookingComment();
    $comment->comment = $res['melding'];
    $booking->comments[time()*1000] = $comment;
}

$manager = new \ns_7e828cd0_8b44_4125_ae4f_f61983b01e0a\PmsManagement();

$room = new \core_pmsmanager_PmsBookingRooms();
$room->date = new \core_pmsmanager_PmsBookingDateRange();
$room->date->start = $manager->convertToJavaDate($start);
$room->date->end = $manager->convertToJavaDate($end);
if($res['type'] == "hybel15") {
    $room->bookingItemTypeId = "c86f3095-2bd0-4833-a96d-207a406a667d";
} else {
    $room->bookingItemTypeId = "49052ae4-0859-4173-8406-11c499a7f5fe";
}

if($res["privatnavn"]) {
    $guest = new core_pmsmanager_PmsGuests();
    $guest->name = $res["privatnavn"];
    $guest->prefix = $res["privattelprefix"];
    $guest->phone = $res["privattel"];
    $guest->email = $res["privatepost"];
    
    
    $room->guests = array();
    $room->guests[] = $guest;
}

$booking->rooms = array();
$booking->rooms[] = $room;

if(!$failed) {
    $factory->getApi()->getPmsManager()->setBooking("hybelhotell", $booking);
    $booking = $factory->getApi()->getPmsManager()->completeCurrentBooking("hybelhotell");
}

if(!$booking) {
    $failed = "Noe gikk galt ved registrering av din booking, vennligst kontakt oss.";
}

echo "<center>";
if(!$failed) {
    echo "<h1>Takk for din henvendelse, vi kontakter deg så snart som mulig.</h1>";
} else {
    header($_SERVER['SERVER_PROTOCOL'] . ' 400 Bad request', true, 400);
    echo "<h1>$failed</h1>";
}
echo "</center>";


?>