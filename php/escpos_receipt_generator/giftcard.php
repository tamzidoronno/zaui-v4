<?php
/* Call this file 'hello-world.php' */
//fclose(STDIN);
//fclose(STDOUT);
//fclose(STDERR);
//$STDIN = fopen('/dev/null', 'r');
//$STDOUT = fopen('/tmp/test.log', 'wb');
//$STDERR = fopen('/tmp/error.log', 'wb');

$jsonData = file_get_contents('/tmp/'.$argv[1].'.txt');
$printMessage = json_decode($jsonData);
require __DIR__ . '/vendor/autoload.php';

use Mike42\Escpos\Printer;
use Mike42\Escpos\EscposImage;
use Mike42\Escpos\PrintConnectors\FilePrintConnector;

/* Fill in your own connector here */
$connector = new FilePrintConnector("/tmp/".$argv[1].".prn");

/* Information for the receipt */
$items = array();

/* Start the printer */
$printer = new Printer($connector);

/* Print top logo */
$printer -> setJustification(Printer::JUSTIFY_CENTER);

/* Title of receipt */


/* Name of shop */
$printer -> selectPrintMode(Printer::MODE_DOUBLE_WIDTH);
$printer -> setEmphasis(true);
$printer -> setTextSize(2, 3);
$printer -> text("Gavekort\n");

$printer -> selectPrintMode();
$printer -> feed();
$printer -> selectPrintMode(Printer::MODE_DOUBLE_WIDTH);
$printer -> text($printMessage->accountDetails->companyName."\n");
$printer -> selectPrintMode();
$printer -> text($printMessage->accountDetails->address."\n");
$printer -> text($printMessage->accountDetails->postCode."\n");
$printer -> text("Orgnr: ".$printMessage->accountDetails->vatNumber."\n");
$printer -> text("Epost: ".$printMessage->accountDetails->contactEmail."\n");
$printer -> feed();

/* Name of shop */
$printer -> setTextSize(1, 2);
$printer -> text("Beløp: ".$printMessage->giftCardValue."\n");
$printer -> feed();
$printer -> feed();

$printer -> setEmphasis(true);
$printer -> setTextSize(2, 3);
$printer -> text("Kode: ".$printMessage->code."\n");
$printer -> setEmphasis(false);
$printer -> feed(2);


/* Cut the receipt and open the cash drawer */
$printer -> feed(2);
$printer -> cut();
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
