<?php
/* Call this file 'hello-world.php' */
//fclose(STDIN);
//fclose(STDOUT);
//fclose(STDERR);
//$STDIN = fopen('/dev/null', 'r');
//$STDOUT = fopen('/tmp/test.log', 'wb');
//$STDERR = fopen('/tmp/error.log', 'wb');

$jsonData = file_get_contents('/tmp/'.$argv[1].'.txt');
$storeId = $argv[2];

$printMessage = json_decode($jsonData);
require __DIR__ . '/vendor/autoload.php';

use Mike42\Escpos\Printer;
use Mike42\Escpos\EscposImage;
use Mike42\Escpos\PrintConnectors\FilePrintConnector;

$padLeft = "";

// // Just a hack during installation, please rewrite to send printertype.
// Just a hack during installation, please rewrite to send printertype.
$printerType = $storeId == "ac8bff70-a8b9-4fa1-8281-a12e24866bdb" ? "customK80" : "firstone";

if ($printerType == "customK80") {
    $padLeft = "  ";
    item::$leftColSize = 38;
}
/* Fill in your own connector here */
$connector = new FilePrintConnector("/tmp/".$argv[1].".prn");

/* Information for the receipt */
$items = array();
foreach ($printMessage->itemLines as $itemLine) {
    if (strlen($itemLine->description) > 30) {
        $multiplines = explode( "\n", wordwrap( $itemLine->description, 33));
        
        $i = 0;
        foreach ($multiplines as $line) {
            $i++;
            if (count($multiplines) == $i) {
                $items[] = new item($line, $itemLine->price, false, $padLeft);
            } else {
                $items[] = new item($line, "", false, $padLeft);
            }
        }
    } else {
        $items[] = new item($itemLine->description, $itemLine->price, false, $padLeft);
    }
    
}

$items[] = new item('', "", false, $padLeft);
$items[] = new item('Subtotal', $printMessage->totalIncVat, false, $padLeft);
$items[] = new item("", "", false, $padLeft);
$items[] = new item("Hvorav MVA", "", false, $padLeft);
foreach ($printMessage->vatLines as $vatline) {
    $items[] = new item("Mva ".$vatline->percent."%", $vatline->total, false, $padLeft);
}

$total = new item('Total', $printMessage->totalIncVat, true, $padLeft);

/* Start the printer */
$printer = new Printer($connector);

/* Print top logo */
$printer -> setJustification(Printer::JUSTIFY_CENTER);

$receiptText = $printMessage->paymentDate ? "SALGSKVITTERING" : "IKKE KVITTERING\n".$padLeft."FOR SALG";

$logoFileName = "/storage/logo/".$argv[2].".png";
if (file_exists($logoFileName)) {
    $img = EscposImage::load($logoFileName, false);
    $printer -> bitImageColumnFormat($img);
}

/* Title of receipt */
$printer -> setEmphasis(true);
$printer -> setTextSize(2, 3);
$printer -> text($receiptText."\n");
$printer -> setEmphasis(false);
$printer -> feed(2);


/* Name of shop */
//$printer -> selectPrintMode(Printer::MODE_DOUBLE_WIDTH);
$printer -> selectPrintMode();
$printer -> text($padLeft.$printMessage->accountDetails->companyName."\n");
$printer -> text($padLeft.$printMessage->accountDetails->address."\n");
$printer -> text($padLeft.$printMessage->accountDetails->postCode."\n");
$printer -> text($padLeft."Orgnr: ".$printMessage->accountDetails->vatNumber."\n");
$printer -> text($padLeft."Epost: ".$printMessage->accountDetails->contactEmail."\n");
if (isset($printMessage->accountDetails->phoneNumber)) {
    $printer -> text($padLeft."Telefon: ".$printMessage->accountDetails->phoneNumber."\n");
}
$printer -> feed();

/* Items */
$printer -> setJustification(Printer::JUSTIFY_LEFT);
$printer -> setEmphasis(false);


foreach ($items as $item) {
    $printer -> text($item);
}

$printer -> setEmphasis(false);
$printer -> feed();

/* Tax and total */
$printer -> selectPrintMode(Printer::MODE_DOUBLE_WIDTH);
$printer -> text($total);
$printer -> selectPrintMode();

if ($printMessage->paymentDate) {
/* Footer */
    $printer -> feed(2);
    $printer -> setJustification(Printer::JUSTIFY_CENTER);
    $printer -> text("Dato: ".date('d.m.Y H:i:s', strtotime($printMessage->paymentDate)) . "\n");
    $printer -> text("Betalingsmetode: ".$printMessage->paymentMethod . "\n");
}

/* Cut the receipt and open the cash drawer */
$printer -> feed(2);
$printer -> cut(Printer::CUT_PARTIAL);
$printer -> pulse();

$printer -> close();

/* A wrapper to do organise item names & prices into columns */
class item
{
    private $name;
    private $price;
    private $dollarSign; 
    private $padLeft;
    public static $leftColSize = 32;

    public function __construct($name = '', $price = '', $dollarSign = false, $padLeft = "")
    {
        $this -> name = $name;
	$this -> padLeft = $padLeft;

        if ($price != "") {
            $this -> price = number_format($price, 2);
        }
        
        $this -> dollarSign = $dollarSign;
    }
    
    public function __toString()
    {
        $rightCols = 10;
        $leftCols = item::$leftColSize - strlen($this->padLeft);
        if ($this -> dollarSign) {
            $leftCols = $leftCols/2  - $rightCols/2 - 1 ;
        }
        $left = str_pad($this -> name, $leftCols) ;
        
        $sign = ($this -> dollarSign ? 'Kr ' : '');
        $right = str_pad($sign . $this -> price, $rightCols, ' ', STR_PAD_LEFT);
        return $this->padLeft.$left.$right."\n";
    }
}
