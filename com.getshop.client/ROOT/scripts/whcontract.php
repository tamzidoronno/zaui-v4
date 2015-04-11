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

$textNo = "VILKÅR FOR BRUK AV OVERNATTINGSHYBEL

Dette dokumentet innebærer overlevering av tjenesten ved at eieren gir kunden rett til tilgang til overnattingshybelen i det angitte tidsrommet og på de angitte betingelser. 


1. PARTER
Eier: Halvdan Wilhelmsens Allé 22 AS
Kunde: [renter]

2. OVERNATTINGSHYBEL OG OVERLEVERING
Wilhelmsen House, Halfdan Wilhelmsens alle 22, 3116 Tønsberg, leilighet nummer [____]

Overnattingshybelen overtas fullt møblert i vanlig ryddet og rengjort stand. Overnattingshybelen aksepteres for øvrig "som den er" i samsvar med informasjon, tegninger og bilder på www.wh.no. Kunden har ikke rett til å bytte overnattingshybelen, og må akseptere mindre avvik fra oppgitt informasjon om overnattingshybelen.

3. BRUKSPERIODE
Kunden har krysset av på www.wh.no for om avtalen er ansett som kortidsbruk eller langtidsbruk. Denne avtalen er [kortidsbruk/langtidsbruk].

Bruksperioden er som valgt av kunden på www.wh.no og er bindende. Det er ikke anledning til å si opp avtalen før utløpet med mindre punkt 10 får anvendelse. 

Overnattingshybelen står tilgjengelig for bruk tidligst kl 15 på avtalt startdato for bruksperioden.

4. BETALING 
Betaling: [_____] per dag (inkl mva). Total betaling utgjør kr [_____].

Korttidsbruk: 
Kunden betaler forskuddsvis gjennom betalingsfortalen på www.wh.no. 

Landtidsbruk:  
Kunden betaler forskuddsvis for den første og de to siste kalendermånedene i den bestilte bruksperioden gjennom betalingsportalen på www.wh.no. Deretter faktureres kunden månedsvis den første i hver måned for én måneds bruk av overnattingshybelen. Betaling for de to siste kalendermånedene vil avregnes mot forskuddet fra første innbetaling.

Kunden er innforstått med at adgangskoden blir deaktivert dersom kunden ikke bidrar til oppfyllelse av sine forpliktelser etter dette punktet. 

5. KUNDENS PLIKTER
Overnattingshybelen kan bare benyttes til beboelse og overnatting, og kan bare benyttes av kunden.

Kunden plikter å ivareta overnattingshybelen og fellesarealer med tilbørlig aktsomhet og i alminnelig god stand. Kunden må for øvrig opptre i samsvar med de til enhver tid gjeldende husordensregler, se www.wh.no.

Kunden skal varsle eier umiddelbart dersom det oppdages skade på eiendommen. 

Kunden skal gi eier adgang til overnattingshybelen for rengjøring etter punkt 6 og for øvrig nødvendig vedlikehold.

6. EIERENS PLIKTER
Eieren skal sørge for rengjøring av overnattingshybelen hver femte dag. Kunden skal også rengjøre ved fraflytting av overnattingshybelen.

Eieren skal holde overnattingshybelen forsikret. Eieren bærer for øvrig intet ansvar for skader, kostnader eller tap som måtte oppstå ved innbrudd, brann, røykutvikling, strømavbrudd, vannskade eller av andre årsaker. Kunden er i denne forbindelse innforstått med at eieren skal holdes skadesløs.

7. MISLIGHOLD
Dersom overnattingshybelen ikke er i samsvar med avtalte vilkår, skal kunden gi beskjed til eieren innen rimelig tid etter at forholdet blir oppdaget. Forsinket reklamasjon medfører tap av eventuelle misligholdssanksjoner. 

Dersom kunden påfører overnattingshybelen skade, skal eieren varsles umiddelbart. Eieren vil utbedre forholdet til kundens kostnad. Dette vil bli fakturert særskilt.

Dersom kunden misligholder sin plikt etter punkt 4, blir kundens adgangskode deaktivert. Eieren kan kaste ut kunden 2 dager etter at koden er blitt deaktivert såfremt kunden ikke har rettet opp misligholdet. 

Dersom kunden misligholder sine plikter etter punkt 5, kan eieren kaste ut kunden med 4 dagers varsel dersom forholdet ikke rettes opp umiddelbart. 

Ved gjentatte brudd på denne avtalen kan eieren si opp avtalen med 10 dagers varsel.


8. FRAFLYTTING
Kunden plikter å flytte fra overnattingshybelen senest kl 12 den siste dagen av bruksperioden. Kunden skal ta med seg alle personlige eiendeler ved fraflytting. 

Kunden aksepterer at eieren får håndpant i alle eiendeler som legges igjen i overnattingshybelen. Dersom eieren vurderer at eiendelene har begrenset økonomisk verdi, står eieren fritt til å kaste eiendelene uten varsel.

9. DIVERSE
Husleieloven har ikke anvendelse på dette avtaleforholdet, jfr. husleieloven § 1-1 fjerde ledd.

Kunden vedtar at tvangsfravikelse kan gjennomføres etter tvangsfullbyrdelsesloven § 13-2 tredje ledd alternativ a.

Kunden vedtar at tvangsfravikelse kan gjennomføres etter tvangsfullbyrdelsesloven § 13-2 tredje ledd alternativ b.

Ettersom levering anses å finne sted ved endelig bestilling og oversendelse av avtalen, får ikke reglene om avbestilling etter forbrukerkjøpsloven § 41 anvendelse på avtaleforholdet. 

Ettersom avtalen faller inn under angrerettloven § 22 bokstav m), får ikke regler om angrerett anvendelse på avtaleforholdet.

10.  SÆRSKILT OM OPPSIGELSE VED VALG AV FLEKSILØSNING 
Dersom kunden har inngått avtale om langtidsbruk over 3 kalendermåneder og har krysset av for fleksiløsning, kan kunden si opp avtalen med 2 måneders skriftlig varsel. Dersom avtalen sies opp, vil betaling for resten av bruksperioden dekkes av innbetalt forskudd. 

Det løper en særskilt avgift for fleksiløsning som fremkommer av totalprisen i punkt 4.




Sign: 
Dette dokumentet anses elektronisk signert på vegne av kunden.
";

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
