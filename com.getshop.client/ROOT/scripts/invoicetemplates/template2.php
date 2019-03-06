<?
/* @var $order \core_ordermanager_data_Order */
/* @var $accountingDetails \core_productmanager_data_AccountingDetail */
include 'InvoiceTemplateTranslator.php';

function file_get_contents_utf8($fn) {
     $content = file_get_contents($fn);
      return mb_convert_encoding($content, 'UTF-8',
          mb_detect_encoding($content, 'UTF-8, ISO-8859-1', true));
}

$inputJSON = file_get_contents_utf8('php://input');

//$inputJSON = mb_convert_encoding($inputJSON, 'HTML-ENTITIES', "UTF-8");
$invoiceData = json_decode($inputJSON);
//echo $inputJSON;
//echo mb_detect_encoding($inputJSON);
//die("");

$order = $invoiceData->order;
$accountingDetails = $invoiceData->accountingDetails;

$total = 0;

$translator = new InvoiceTemplateTranslator($order->language, $order->currency);
$calculatedTaxes = array();

?>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
<style>
    body {
        font-family: 'Ubuntu';
        font-size: 18px;
    }
    
    .logoarea img {
        margin: 20px;
        max-height: 150px;
        max-width: 400px;
    }
    
    .bold {
        font-weight: bold;
    }
    
    .row .col {
        display: inline-block;
        padding-left: 10px;
        padding-top: 3px;
    }
    
    .row .col.col1 { width: 150px; }
    .row .col.col2 { width: 150px; }
    .row .col.col3 { width: 400px; }
    
    .outerproductrow,
    .productrow {
        margin-left: 20px;
    }
    
    div.productrow:nth-child(even) {
        background-color: #EEE;
    }
    
    .outerproductrow .col,
    .productrow .col {
        display: inline-block;
        vertical-align: top;
        box-sizing: border-box;
        padding: 10px;
    }
   
    .outerproductrow .col {
        padding-top: 10px;
        padding-bottom: 0px;
    }
    
    .outerproductrow .col.col1,
    .productrow .col.col1 { width: 550px; }
    
    .outerproductrow .col.col2,
    .productrow .col.col2 { width: 120px; text-align: right;}
    
    .outerproductrow .col.col3,
    .productrow .col.col3 { width: 100px; text-align: right;}
    
    .outerproductrow .col.col4,
    .productrow .col.col4 { width: 150px; text-align: right;}
    
    .productrowheader {
        border-top: solid 1px #DDD;
        padding-top: 20px;
        font-weight: bold;
        padding-bottom: 20px;
    }
    
</style>
<div class='page' style='margin-top: 20px;' >
    <div style='width: 40%; display: inline-block; vertical-align: top; '>
        <div class='logoarea'>{logo}</div>
    </div>
    <div class='logoarea' style='width: 29%; display: inline-block; text-align: left; vertical-align: top; color: #666; padding-top: 30px;'>
        <div ><? echo $accountingDetails->companyName; ?></div>
        <div ><? echo $accountingDetails->address; ?></div>
        <div ><? echo $accountingDetails->postCode." ".$accountingDetails->city; ?></div>
    </div>
    <div class='logoarea' style='width: 29%; display: inline-block; text-align: left; vertical-align: top; color: #666; padding-top: 30px;'>
        <div ><? echo $translator->translate("Email").": ".$accountingDetails->contactEmail; ?></div>
        <div ><? echo $translator->translate("Phone").": ".$accountingDetails->phoneNumber; ?></div>
        <div ><? echo $translator->translate("VAT").": ".$accountingDetails->vatNumber; ?></div>
    </div>
</div>

<div class='page' style='margin: 20px;' >
    <div style='border-bottom: solid 1px #DDD; padding: 5px;  padding-left: 10px; text-transform: uppercase; color: #3b7fb1; font-size: 22px;'><? echo $translator->translate("Invoice"); ?></div>
    
    <div class='row'>
        <div class='col col1'><? echo $translator->translate("Invoice number"); ?></div>
        <div class='col col2 bold'><? echo $order->incrementOrderId; ?></div>
        <div class='col col3 bold'><? echo $order->cart->address->fullName; ?></div>
    </div>
    
    <div class='row'>
        <div class='col col1'><? echo $translator->translate("Invoice date"); ?></div>
        <div class='col col2'><? echo date('d.m.Y', strtotime($order->rowCreatedDate)); ?></div>
        <div class='col col3'><? echo $order->cart->address->address; ?></div>
    </div>
    
    <div class='row'>
        <div class='col col1'><? echo $translator->translate("Due date"); ?></div>
        <div class='col col2 bold' style='color: red;'><? echo date('d.m.Y', strtotime($order->dueDate)); ?></div>
        <div class='col col3'><? echo $order->cart->address->postCode." ".$order->cart->address->city; ?></div>
    </div>
    
    <div class='row'>
        <div class='col col1'><? echo $translator->translate("Currency"); ?></div>
        <div class='col col2 bold' style='color: green;'><? echo $translator->getCurrencyDisplayText(); ?></div>
        <div class='col col3'></div>
    </div>
</div>

<?
if ($order->invoiceNote) {
?>
<div class='page' style='margin: 20px;' >
    <div style='border-bottom: solid 1px #DDD; padding: 5px;  padding-left: 10px; text-transform: uppercase; color: #3b7fb1; font-size: 22px;'><? echo $translator->translate("Note"); ?></div>
    <div style="padding: 5px; padding-left: 10px;">
        <?
        echo nl2br($order->invoiceNote);
        ?>
    </div>
</div>
<?
}
?>
<div class='productrow productrowheader'>
    <div class='col col1'><? echo $translator->translate("Product"); ?></div>
    <div class='col col2'><? echo $translator->translate("Unit Price"); ?></div>
    <div class='col col3'><? echo $translator->translate("QTY"); ?></div>
    <div class='col col4'><? echo $translator->translate("Line Total"); ?></div>
</div>
<?
foreach ($order->cart->items as $item) {
    $lineTotal = $item->product->price * $item->count;
    $total += $lineTotal;
    $taxes = ($item->product->price - $item->product->priceExTaxes) * $item->count;
    if (!isset($calculatedTaxes[$item->product->taxGroupObject->taxRate])) {
        $calculatedTaxes[$item->product->taxGroupObject->taxRate] = 0;
    }
    
    $calculatedTaxes[$item->product->taxGroupObject->taxRate] += $taxes;
    ?>
    <div class='productrow '>
        <div class='col col1'><? echo $item->product->name; ?></div>
        <div class='col col2'><? echo $item->product->price; ?></div>
        <div class='col col3'><? echo $item->count; ?></div>
        <div class='col col4'><? echo $translator->formatPrice($lineTotal); ?></div>
    </div>
    <?
}
?>

<div class='outerproductrow'>
    <div class='col col1'></div>
    <div class='col col2'><? echo $translator->translate("Sub total"); ?></div>
    <div class='col col3'></div>
    <div class='col col4'><? echo $translator->formatPrice($total); ?></div>
</div>

<div class='outerproductrow'>
    <div class='col col1'></div>
    <div class='col col2'><? echo $translator->translate("Balance due"); ?></div>
    <div class='col col3'></div>
    <div class='col col4' style='color: #3b7fb1; font-size: 22px;'><? echo $translator->formatPrice($total); ?></div>
</div>

<div class='outerproductrow bold' style='border-bottom: solid 1px #DDD; margin-top: 30px;'>
    <div class='col col1'><? echo $translator->translate("Calculated Taxes"); ?></div>
    <div class='col col2'></div>
    <div class='col col3'><? echo $translator->translate("Percent"); ?></div>
    <div class='col col4'><? echo $translator->translate("Amount"); ?></div>
</div>

<?
foreach ($calculatedTaxes as $percent => $taxTotal) {
?>
    <div class='outerproductrow'>
        <div class='col col1'></div>
        <div class='col col2'></div>
        <div class='col col3'><? echo $percent."%"; ?></div>
        <div class='col col4'><? echo $translator->formatPrice($taxTotal); ?></div>
    </div>
<?
}
?>

<div style='margin: 20px; border: solid 1px #DDD; padding: 20px; '>
    <b><? echo $translator->translate("Bank details"); ?></b>
    <div><? echo $translator->translate("Account number").": ".$accountingDetails->accountNumber; ?></div>
    <div><? echo $translator->translate("IBAN").": ".$accountingDetails->iban; ?></div>
    <div><? echo $translator->translate("BIC/SWIFT").": ".$accountingDetails->swift; ?></div>
    <?
    if ($order->kid) {
    ?>
        <div><? echo $translator->translate("KID").": ".$order->kid; ?></div>
    <?
    }
    ?>
    
</div>
