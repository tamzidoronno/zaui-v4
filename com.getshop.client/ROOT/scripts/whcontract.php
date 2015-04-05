<?
chdir("../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();

$mgr = $factory->getApi()->getHotelBookingManager();
if(isset($_GET['id'])) {
    $data = $mgr->getUserBookingData($_GET['id']);
} else {
    $data = $mgr->getCurrentUserBookingData();
}
$roomCount = 0;
foreach($data->references as $reference) {
    foreach($reference->roomsReserved as $room) {
        $roomCount++;
    }
}

$room = $factory->getApi()->getProductManager()->getProduct($data->additonalInformation->roomProductId);

$vars = array();
$vars['rental'] = "WILHELMSEN HOUSE AS<br>Halfdan Wilhelmsens alle 22<br>3116 TØNSBERG<br>912 999 688 MVA";
$vars['renter'] = $data->references[0]->roomsReserved[0]->visitors[0]->name;
$vars['roomNumbers'] ="";
$vars['roomName'] = $room->name;
$vars['site'] = "www.wh.no";
$vars['periods'] = "";
$vars['roomcount'] = $roomCount;
$days = 0;
foreach($data->references as $reference) {
    $days += ((strtotime($reference->endDate) - strtotime($reference->startDate)) / 86400);
    $vars['periods'] .= date("d-m-Y", strtotime($reference->startDate)) . " - " . date("d-m-Y", strtotime($reference->endDate)) . "<br>";
}
$vars['days'] = ceil($days);

if($data->bookingPrice == 0) {
    $cart = $factory->getApi()->getCartManager();
    $vars['total'] = $cart->getCartTotalAmount();
    $vars['singleprice'] = $cart->getCart()->items[0]->product->price;
} else {
    $vars['singleprice'] = $data->bookingPrice;
    $vars['total'] = $data->bookingPrice * $days;
}

 $vars['total'] = round( $vars['total'], 2);
 $vars['singleprice'] = round( $vars['singleprice'], 2);

$textNo = "UTLEIE AV OVERNATTINGSHYBEL

1. PARTER
Leietaker: [renter]

Utleier: 
[rental]



2. LEIEOBJEKT OG OVERTAKELSE
Wilhelmsen House, Halfdan Wilhelmsens alle 22, 3116 Tønsberg, leilighet type: <u>[roomName]</u>

Leieobjektet overtas fullt møblert i vanlig ryddet og rengjort stand. Leieobjektet aksepteres for øvrig \"som den er\" i samsvar med informasjon, tegninger og bilder på [site]. Leietaker har ikke rett til å bytte leieobjekt, og må akseptere mindre avvik fra oppgitt informasjon om leieobjektet.

3. LEIEPERIODE
[periods]

4. BETALING AV LEIE
Leieavgift: [singleprice] per dag (inkl mva). Total leie utgjør kr [total].

[startShortRental] 
Leietaker har oppgitt informasjon om bankkort til Utleier på [site] ved inngåelse av leieavtalen. Utleier gis adgang til å sperre av tilstrekkelig beløp gjennom bankkortet til dekning av fullt leiebeløp. Når leieperioden er avsluttet, har Utleier rett til å trekke full leieavgift fra oppgitt bankkort.
[endShortRental] 

[startLongRental] 
Leietaker har oppgitt informasjon om bankkort til Utleier på [site] ved inngåelse av leieavtalen. Utleier har adgang til å sperre av tilstrekkelig beløp gjennom bankkortet til dekning av 60 dagers leie ad gangen. Utleier har adgang til å trekke leieavgift fra Leietakers bankkort etter 30 dager til dekning av påløpt leieperiode.
[endLongRental] 

Leietaker er innforstått med at adgangskoden blir deaktivert dersom leietaker ikke bidrar til oppfyllelse av sine forpliktelser etter dette punktet. 

5. LEIETAKERS PLIKTER
Leieobjektet kan bare benyttes til beboelse og overnatting, og kan bare benyttes av leietaker.

Leietaker plikter å ivareta leieobjektet med tilbørlig aktsomhet og i alminnelig god stand. Leietaker må for øvrig opptre i samsvar med husordensregler, se [site].

Leietaker skal varsle utleier umiddelbart dersom det oppdages skade på eiendommen. 

Leietaker skal la Utleier får adgang til utleieobjektet for rengjøring etter punkt 6

6. UTLEIERS ANSVAR
Utleier skal sørge for rengjøring av leieobjektet hver femte dag. Utleier skal rengjøre ved fraflytting av leieobjektet.

Utleier skal holde leieobjektet forsikret. Utleier bærer for øvrig intet ansvar for skader, kostnader eller tap som måtte oppstå ved innbrudd, brann, røykutvikling, strømavbrudd, vannskade eller av andre årsaker. Leietaker er i denne forbindelse innforstått med at utleier skal holdes skadesløs.

7. MISLIGHOLD
Dersom leietaker påfører leieobjektet skade, skal utleier varsles umiddelbart. Utleier vil utbedre forholdet til leietakers kostnad.

Dersom leietaker misligholder sin plikt etter punkt 4, blir leietakers adgangskode deaktivert. Utleier kan kaste ut leietaker 3 dager etter at koden er blitt deaktivert såfremt leietaker ikke har rettet opp misligholdet. 

Dersom Leietaker misligholder sine plikter etter punkt 5, kan Utleier kaste ut Leietaker med 5 dagers varsel dersom forholdet ikke rettes opp umiddelbart. 

Ved gjentatte brudd på denne avtalen kan utleier si opp avtalen med 10 dagers varsel.

8. FRAFLYTTING
Leietaker plikter å flytte fra leieobjektet senest kl 12 den siste dagen av leieperioden. Leietaker skal ta med seg alle personlige eiendeler ved fraflytting av leieobjektet. 

Dersom leietaker ikke flytter frivillig, vil utleier sørge for utflytting av leietaker.

Utleier får håndpant i alle eiendeler som legges igjen i leieobjektet tilfaller utleier. Dersom utleier vurderer eiendelene å ha begrenset økonomisk verdi, står utleier fritt til å kaste eiendelene uten varsel.

9. DIVERSE
Husleieloven har ikke anvendelse på dette leieforholdet, jfr. husleieloven § 1-1 fjerde ledd.

Leietaker vedtar at tvangsfravikelse kan gjennomføres etter tvangsfullbyrdelsesloven § 13-2 tredje ledd alternativ a.

Leietaker vedtar at tvangsfravikelse kan gjennomføres etter tvangsfullbyrdelsesloven § 13-2 tredje ledd alternativ b.



Sign: 
Dette dokumentet anses elektronisk signert på vegne av leietaker.";

foreach($vars as $key => $val) {
    $textNo = str_replace("[$key]", $val, $textNo);
}

if($data->additonalInformation->isPartner) {
    $newText = substr($textNo, 0, strpos($textNo, "[startShortRental]"));
    $newText .= substr($textNo, strpos($textNo, "[endShortRental]"));
    $textNo = $newText;
} else {
    $newText = substr($textNo, 0, strpos($textNo, "[startLongRental]"));
    $newText .= substr($textNo, strpos($textNo, "[endLongRental]"));
    $textNo = $newText;
}

$textNoLines = explode("\n", $textNo);
foreach($textNoLines as $key => $line) {
  if(stristr($line, "[startShortRental]")) unset($textNoLines[$key]);
  if(stristr($line, "[endShortRental]")) unset($textNoLines[$key]);
  if(stristr($line, "[startLongRental]")) unset($textNoLines[$key]);
  if(stristr($line, "[endLongRental]")) unset($textNoLines[$key]);
}
$textNo = implode("\n", array_values($textNoLines));


?>

<div style='width: 1024; margin: auto;'>
    <div style='display:inline-block; width: 50%; border-right: solid 1px; padding-right: 20px; box-sizing: border-box;'>
        <? echo nl2br($textNo); ?>
    </div>
</div>
