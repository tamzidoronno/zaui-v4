<?php
/* Call this file 'hello-world.php' */
//fclose(STDIN);
//fclose(STDOUT);
//fclose(STDERR);
//$STDIN = fopen('/dev/null', 'r');
//$STDOUT = fopen('/tmp/test.log', 'wb');
//$STDERR = fopen('/tmp/error.log', 'wb');

$jsonData = file_get_contents('/tmp/printcontent.txt');
$printMessage = json_decode($jsonData);
require __DIR__ . '/vendor/autoload.php';

use Mike42\Escpos\Printer;
use Mike42\Escpos\EscposImage;
use Mike42\Escpos\PrintConnectors\FilePrintConnector;

/* Fill in your own connector here */
$connector = new FilePrintConnector("/tmp/receipt2.prn");

/* Information for the receipt */
$items = array();
foreach ($printMessage->itemLines as $itemLine) {
    if (strlen($itemLine->description) > 30) {
        $multiplines = explode( "\n", wordwrap( $itemLine->description, 33));
        
        $i = 0;
        foreach ($multiplines as $line) {
            $i++;
            if (count($multiplines) == $i) {
                $items[] = new item($line, $itemLine->price);
            } else {
                $items[] = new item($line, "");
            }
        }
    } else {
        $items[] = new item($itemLine->description, $itemLine->price);
    }
    
}

$items[] = new item('', "");
$items[] = new item('Subtotal', $printMessage->totalIncVat);

$items[] = new item("", "");
$items[] = new item("Hvorav MVA", "");
foreach ($printMessage->vatLines as $vatline) {
    $items[] = new item("Mva ".$vatline->percent."%", $vatline->total);
}

$total = new item('Total', $printMessage->totalIncVat, true);

/* Start the printer */
$printer = new Printer($connector);

/* Print top logo */
$printer -> setJustification(Printer::JUSTIFY_CENTER);

$receiptText = $printMessage->paymentDate ? "KVITTERING" : "IKKE KVITTERING FOR SALG";
/* Title of receipt */
$printer -> setEmphasis(true);
$printer -> setTextSize(2, 3);
$printer -> text($receiptText."\n");
$printer -> setEmphasis(false);
$printer -> feed(2);


/* Name of shop */
$printer -> selectPrintMode(Printer::MODE_DOUBLE_WIDTH);
$printer -> text($printMessage->accountDetails->companyName."\n");
$printer -> selectPrintMode();
$printer -> text($printMessage->accountDetails->address."\n");
$printer -> text($printMessage->accountDetails->postCode."\n");
$printer -> text("Orgnr: ".$printMessage->accountDetails->vatNumber."\n");
$printer -> text("Epost: ".$printMessage->accountDetails->contactEmail."\n");
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
    $printer -> text("Betalingsmåte: ".$printMessage->paymentMethod . "\n");
}

/* Cut the receipt and open the cash drawer */
$printer -> feed(2);
$printer -> cut();
$printer -> pulse();

$printer -> close();

/* A wrapper to do organise item names & prices into columns */
class item
{
    private $name;
    private $price;
    private $dollarSign;

    public function __construct($name = '', $price = '', $dollarSign = false)
    {
        $this -> name = $name;
        if ($price != "") {
            $this -> price = number_format($price, 2);
        }
        
        $this -> dollarSign = $dollarSign;
    }
    
    public function __toString()
    {
        $rightCols = 10;
        $leftCols = 32;
        if ($this -> dollarSign) {
            $leftCols = $leftCols/2  - $rightCols/2 - 1 ;
        }
        $left = str_pad($this -> name, $leftCols) ;
        
        $sign = ($this -> dollarSign ? 'Kr ' : '');
        $right = str_pad($sign . $this -> price, $rightCols, ' ', STR_PAD_LEFT);
        return "$left$right\n";
    }
}
