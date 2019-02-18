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
foreach ($printMessage->cartItems as $cartItem) {
    $text = $cartItem->count." x ".$cartItem->product->name;
    
    if (strlen($text) > 30) {
        $multiplines = explode( "\n", wordwrap( $text, 33));
        
        $i = 0;
        foreach ($multiplines as $line) {
            $i++;
            if (count($multiplines) == $i) {
                $items[] = new item($line, $cartItem->product->price);
            } else {
                $items[] = new item($line, "");
            }
        }
    } else {
        $items[] = new item($text, $cartItem->product->price);
    }
    
}

/* Start the printer */
$printer = new Printer($connector);

/* Print top logo */
$printer -> setJustification(Printer::JUSTIFY_CENTER);

$receiptText = "ADDED TO ROOM";
/* Title of receipt */
$printer -> setEmphasis(true);
$printer -> setTextSize(2, 3);
$printer -> text($receiptText."\n");
$printer -> setEmphasis(false);
$printer -> feed(2);

/* Items */

$printer -> setJustification(Printer::JUSTIFY_LEFT);
$printer -> setEmphasis(false);
$printer -> selectPrintMode();


foreach ($items as $item) {
    $printer -> text($item);
}

$printer -> setEmphasis(false);
$printer -> feed();
$printer -> text(new item("Signature:", ""));
$printer -> feed();
$printer -> feed();
$printer -> feed();
$printer -> feed();


$printer -> setTextSize(1, 1);
$printer -> text(new item("_________________________________", ""));
$printer -> text(new item("Name: ".$printMessage->guestName, ""));
$printer -> text(new item("Room: ".$printMessage->roomName, ""));
$printer -> text(new item("Date: ".date('d.m.Y H:i:s', strtotime($printMessage->date)), ""));

/* Tax and total */
$printer -> selectPrintMode(Printer::MODE_DOUBLE_WIDTH);

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
            $leftCols = $leftCols / 2 - $rightCols / 2;
        }
        $left = str_pad($this -> name, $leftCols) ;
        
        $sign = ($this -> dollarSign ? 'Kr ' : '');
        $right = str_pad($sign . $this -> price, $rightCols, ' ', STR_PAD_LEFT);
        return "$left$right\n";
    }
}
