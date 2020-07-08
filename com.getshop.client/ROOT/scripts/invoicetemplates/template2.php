<?
/* @var $order \core_ordermanager_data_Order */
/* @var $accountingDetails \core_productmanager_data_AccountingDetail */
include 'InvoiceTemplateTranslator.php';
if(file_exists("../../../classes/CountryCodes.php")) {
    include '../../../classes/CountryCodes.php';
}

function file_get_contents_utf8($fn) {
     $content = file_get_contents($fn);
     $content = base64_decode($content);
    
      return mb_convert_encoding($content, 'UTF-8',
          mb_detect_encoding($content, 'UTF-8, ISO-8859-1', true));
}

if (!isset($serializedOrder)) {
    $inputJSON = file_get_contents_utf8('php://input');
} else {
    $inputJSON = $serializedOrder;
}

//$inputJSON = mb_convert_encoding($inputJSON, 'HTML-ENTITIES', "UTF-8");
$invoiceData = json_decode($inputJSON);
//echo $inputJSON;
//echo mb_detect_encoding($inputJSON);
//die("");

$order = $invoiceData->order;
$accountingDetails = $invoiceData->accountingDetails;

$language = $order->language;
$currency = $order->currency;
if(!$language) { $language = $invoiceData->accountingDetails->language; }
if(!$currency) { $currency = $invoiceData->accountingDetails->currency; }
$total = 0;
$translator = new InvoiceTemplateTranslator($language, $currency);
$calculatedTaxes = array();
$isInvoice = $order->payment->paymentType == "ns_70ace3f0_3981_11e3_aa6e_0800200c9a66\\InvoicePayment";

?>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
<style>
    .invoice_template {
        font-family: 'Ubuntu';
        font-size: 18px;
    }
    
    .invoice_template .logoarea img {
        margin: 20px;
        max-height: 150px;
        max-width: 400px;
    }
    
    .invoice_template .bold {
        font-weight: bold;
    }
    
    .invoice_template .row .col {
        display: inline-block;
        padding-left: 10px;
        padding-top: 3px;
    }
    
    .invoice_template .row .col.col1 { width: 630px; box-sizing: border-box; padding-left: 80px; }
    .invoice_template .row .col.col2 { width: 150px; }
    .invoice_template .row .col.col3 { width: 100px; }
    
    .invoice_template .outerproductrow,
    .invoice_template .productrow {
        margin-left: 20px;
    }
    
    .invoice_template div.productrow:nth-child(even) {
        background-color: #EEE;
    }
    
    .invoice_template .outerproductrow .col,
    .invoice_template .productrow .col {
        display: inline-block;
        vertical-align: top;
        box-sizing: border-box;
        padding: 10px;
    }
   
    .invoice_template .outerproductrow .col {
        padding-top: 10px;
        padding-bottom: 0px;
    }
    
    .invoice_template .outerproductrow .col.col1,
    .invoice_template .productrow .col.col1 { width: 550px; }
    
    .invoice_template .outerproductrow .col.col2,
    .invoice_template .productrow .col.col2 { width: 120px; text-align: right;}
    
    .invoice_template .outerproductrow .col.col3,
    .invoice_template .productrow .col.col3 { width: 100px; text-align: right;}
    
    .invoice_template .outerproductrow .col.col4,
    .invoice_template .productrow .col.col4 { width: 150px; text-align: right;}
    
    .invoice_template .taxesouterrow .col.col1 { width: 405px;}
    .invoice_template .taxesouterrow .col { text-align: left !important;}
    .invoice_template .taxesouterrow .col.col3 { width: 150px;text-align:left !important;}
    .invoice_template .taxesouterrow .col.col5 { width: 90px;text-align:right !important;}
    .invoice_template .taxesouterrow .col.col5 { width: 90px;text-align:right !important;}
    
    .invoice_template .paymenthasbeencompleted {    
        position: absolute;
    width: 100%;
    height: 166px;
    box-sizing: border-box;
    text-align: center;
    padding-top: 60px;
    font-size: 40px;
    color: red;
    border-radius: 7px;
    margin-top: 10px;
    }
    
    .invoice_template .metadata {
        font-size: 15px;
        padding: 3px;
    }
    @media print {
        .new-page {
            page-break-before: always;
        }
    }

    .invoice_template .productrowheader {
        border-top: solid 1px #DDD;
        padding-top: 20px;
        font-weight: bold;
        padding-bottom: 20px;
    }
    
</style>
<div class="invoice_template">
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

    <?
    $text = "";
    if ($isInvoice) {
        if ($order->closed) {
            $text = "Invoice";
        } else {
            $text = "Proforma Invoice";
        }
    } else {
        if ($order->closed) {
            $text = "Receipt";
        } else {
            $text = "Preview";
        }
        
    }
    ?>
    <div class='page' style='margin: 20px;' >
        <div style='border-bottom: solid 1px #DDD; padding: 5px;  padding-left: 10px; text-transform: uppercase; color: #3b7fb1; font-size: 22px;'><? echo $translator->translate($text); ?></div>

        <div class='row'>
            <div class='col col1 bold'><? echo $order->cart->address->fullName; ?>
            <?php if($order->cart->address->co) { echo " C/O: " . $order->cart->address->co; } ?>
            </div>
            <div class='col col2'><? echo !$isInvoice ? $translator->translate("Order number") : $translator->translate("Invoice number"); ?></div>
            <div class='col col3 bold'><? echo $order->incrementOrderId; ?></div>
        </div>
        <div class='row'>
            <div class='col col1'><? echo $order->cart->address->address; ?></div>
            <div class='col col2'><? echo !$isInvoice ? $translator->translate("Order date") : $translator->translate("Invoice date"); ?></div>
            <div class='col col3'><? echo date('d.m.Y', strtotime($order->rowCreatedDate)); ?></div>
        </div>
        <?php if($order->cart->address->address2) { ?>
                <div class='row'>
                    <div class='col col1'><? echo $order->cart->address->address2; ?></div>
                    <div class='col col2'></div>
                    <div class='col col3'></div>
                </div>
        <? } ?>
        <div class='row'>
            <div class='col col1'><? echo $order->cart->address->postCode." ".$order->cart->address->city; ?></div>
            <div class='col col2'><? echo $translator->translate("Due date"); ?></div>
            <div class='col col3 bold' style='color: red;'><? echo date('d.m.Y', strtotime($order->dueDate)); ?></div>
        </div>

        <div class='row'>
            <div class='col col1'><? 
            if(isset($order->cart->address->countrycode)) {
                $ccodes = \CountryCodes::getCodes();
                if(isset($ccodes[$order->cart->address->countrycode])) {
                    echo $ccodes[$order->cart->address->countrycode];
                }
            }
            ?></div>
            <div class='col col2'><? echo $translator->translate("Currency"); ?></div>
            <div class='col col3'><? echo $translator->getCurrencyDisplayText(); ?></div>
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
    $i = 1;
    $totalLines = sizeof((array)$order->cart->items);
    $pagenumber = 1;
    $lineNumber = 1;
    $rowsonlastpage = 0;
    foreach ($order->cart->items as $item) {
        $lineTotal = $item->product->price * $item->count;
        $total += $lineTotal;
        $taxes = ($item->product->price - $item->product->priceExTaxes) * $item->count;
        $key = $item->product->taxGroupObject->description . "_" . $item->product->taxGroupObject->taxRate;
        if (!isset($calculatedTaxes[$key])) {
            $calculatedTaxes[$key] = 0;
        }
        if (!isset($calculatedExTaxes[$key])) {
            $calculatedExTaxes[$key] = 0;
        }

        $calculatedTaxes[$key] += $taxes;
        $calculatedExTaxes[$key] += $lineTotal - $taxes;
        $metadata = "";
        $isStay = sizeof((array)$item->priceMatrix) > 0;
        if ($item->product->additionalMetaData) {
            $metadata .= $translator->translate("Room").": ".$item->product->additionalMetaData;
        }
        
        if ($item->product->metaData) {
            $metadata .= $metadata ? ", " : "";
            $metadata .= $item->product->metaData;
        }
        
        
        if ($isStay && @$item->startDate) {
            $metadata .= $metadata ? ", " : "";
            $metadata .= $translator->translate("Date").": ".date('d.m.Y', strtotime($item->startDate));
            if (@$item->endDate && ($item->startDate != $item->endDate)) {
                $metadata .= " - ".date('d.m.Y', strtotime($item->endDate));
            }
        }
        ?>
        <div class='productrow '>
            <div class='col col1'><? echo $item->product->name; 
            
                if ($metadata) {
                    echo "<div class='metadata'>".$metadata."</div>";
                }
            ?></div>
            <div class='col col2'><? echo $translator->formatPrice($item->product->price); ?></div>
            <div class='col col3'><? echo $item->count; ?></div>
            <div class='col col4'><? echo $translator->formatPrice($lineTotal); ?></div>
        </div>
        <?
        if($pagenumber == 1 && $lineNumber == 13) {
            echo "<div class='new-page'></div>";
            echo "<br><bR>";
            $pagenumber++;
            $lineNumber = 0;
        }
        if($pagenumber > 1 && $lineNumber == 18) {
            echo "<div class='new-page'></div>";
            echo "<br><bR>";
            $pagenumber++;
            $lineNumber = 0;
        }
        
        $lineNumber++;
    }
    
    
    if($pagenumber == 1 && $lineNumber > 8) {
        echo "<div class='new-page'></div>";
        echo "<br><bR>";
    }
    
    if($pagenumber > 1 && $lineNumber > 15) {
        echo "<div class='new-page'></div>";
        echo "<br><bR>";
    }
    
    ?>

    <div class='outerproductrow'>
        <div class='col col1'></div>
        <div class='col col2'><? echo $translator->translate("Sub total"); ?></div>
        <div class='col col3'></div>
        <div class='col col4'><? echo $translator->formatPrice($total); ?></div>
    </div>

    <?php
    if($order->status != 7) { ?>
        <div class='outerproductrow'>
            <div class='col col1'></div>
            <div class='col col2'><? echo $translator->translate("Balance due"); ?></div>
            <div class='col col3'></div>
            <div class='col col4' style='color: #3b7fb1; font-size: 22px;'><? echo $translator->formatPrice($total); ?></div>
        </div>
    <? } else {
        ?>
        <div class='outerproductrow' style='position:relative;'>
            <div class='paymenthasbeencompleted'><? echo $translator->translate("Already paid"); ?></div>
        </div>
        <?php
       } 
    ?>
    
    <div class='outerproductrow taxesouterrow bold' style='border-bottom: solid 1px #DDD; margin-top: 30px;'>
        <div class='col col1'><? echo $translator->translate("Calculated Taxes"); ?></div>
        <div class='col col2'><? echo $translator->translate("Percent"); ?></div>
        <div class='col col3'><? echo $translator->translate("Ex tax"); ?></div>
        <div class='col col4'><? echo $translator->translate("Tax"); ?></div>
        <div class='col col5'><? echo $translator->translate("Total"); ?></div>
    </div>

    <?
    foreach ($calculatedTaxes as $percent => $taxTotal) {
        if($taxTotal == 0) {
            continue;
        }
        $exTaxes = $calculatedExTaxes[$percent];
        $exTaxes = round($exTaxes, 2);
        $percent = explode("_", $percent);
        $desc = $percent[0];
        $percent = $percent[1];
    ?>
        <div class='outerproductrow taxesouterrow'>
            <div class='col col1'><?php echo $desc; ?></div>
            <div class='col col2'><? echo $percent."%"; ?></div>
            <div class='col col3'><?php echo $translator->formatPrice($exTaxes); ?></div>
            <div class='col col4'><? echo $translator->formatPrice($taxTotal); ?></div>
            <div class='col col5'><? echo $translator->formatPrice($taxTotal+$exTaxes); ?></div>
        </div>
    <?
    }
    
    ?>
        <div class='outerproductrow taxesouterrow'>
            <div class='col col1'></div>
            <div class='col col2'></div>
            <div class='col col3'></div>
            <div class='col col4'><? echo $translator->translate("Amount"); ?></div>
            <div class='col col5'><? echo $translator->formatPrice($total); ?></div>
        </div>
    <?
    
    if ($order->status != 7) {
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
        if($invoiceData->accountingDetails->unpaidinvoicetext && stristr($order->payment->paymentType, "InvoicePayment")) {
            echo "<div><b>" . $invoiceData->accountingDetails->unpaidinvoicetext . "</b></div>";
        }
        ?>

    </div>
    <?
    }
    ?>
</div>
