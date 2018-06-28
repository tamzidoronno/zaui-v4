<?php
//$_GET['setLanguage'] = "nb_NO";

chdir("../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();
$cur = $factory->getCurrentLanguage();
$factory->setLanguage($_GET['lang']);
$factory = IocContainer::getFactorySingelton();
$api = $factory->getApi();
$order = $factory->getApi()->getOrderManager()->getOrder($_GET['orderid']);
$user = $api->getUserManager()->getUserById($order->userId);

$ecom = $api->getStoreApplicationPool()->getApplication("70ace3f0-3981-11e3-aa6e-0800200c9a66");
$ecomsettings = $factory->getApplicationPool()->createInstace($ecom);

$accountNumber = $ecomsettings->getConfigurationSetting("accountNumber");
$iban = $ecomsettings->getConfigurationSetting("iban");
$swift = $ecomsettings->getConfigurationSetting("swift");
$address = $ecomsettings->getConfigurationSetting("address");
$city = $ecomsettings->getConfigurationSetting("city");
$companyName = $ecomsettings->getConfigurationSetting("companyName");
$contactEmail = $ecomsettings->getConfigurationSetting("contactEmail");
$dueDays = $ecomsettings->getConfigurationSetting("duedays");
$vatNumber = $ecomsettings->getConfigurationSetting("vatNumber");
$webAddress = $ecomsettings->getConfigurationSetting("webAddress");
$useLanguage = $ecomsettings->getConfigurationSetting("language");
$kidSize = $ecomsettings->getConfigurationSetting("kidSize");
if($kidSize) { $kidSize = new Integer(kidSize); }
$kidType = $ecomsettings->getConfigurationSetting("defaultKidMethod");
$type = $ecomsettings->getConfigurationSetting("type");
$currency = $ecomsettings->getConfigurationSetting("currency");

$dueDate = date("d.m.Y", strtotime($order->rowCreatedDate) + ($dueDays*86400));
if(!$currency) { $currency = "NOK"; }
$curlang = $factory->getCurrentLanguage();
/**
 * @param core_cartmanager_data_CartItem  $item
 */
function createItemText($item) {
    $text = "";
    if($item->product->name) {
        $text .= $item->product->name;
    }
    $dates = "";
    if($item->startDate) {
       $dates .= date("d.m.y", strtotime($item->startDate));
    }
    if(isset($item->product->additionalMetaData)) {
        $text .= " " . $item->product->additionalMetaData;
    }
    if(isset($item->product->metaData)) {
        $text .= " " . $item->product->metaData;
    }
    if($item->endDate) {
        if($dates) { $dates .= " - "; };
       $dates .= date("d.m.y", strtotime($item->endDate));
    }
    if($dates) {
        $text .= " (" . $dates . ")";
    }
    
    return $text;
}
$taxes = array();
$netAmount = 0.0;
$total = $factory->getApi()->getOrderManager()->getTotalAmount($order);
ob_start();
?>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
</head>
<body>
    <div class='page1'>
        <div class='recipient'>
            <?php
            echo $user->fullName . "<br>";
            echo $user->address->address. "<br>";
            echo $user->address->postCode . " " . $user->address->city. "<br>";
            echo $user->address->countryname. "<br>";
            ?>
        </div>
        <div class='companyinfo'>
            <div class='companyinfoheader'>
                <?php
                if($order->status == 7) { echo $factory->__w("Reciept"); } else { echo $factory->__w("Invoice"); }
                ?>
            </div>
            <?php
            echo "<span class='companyname'>" . $companyName . "</span><br>";
            echo $vatNumber . "<br>";
            echo $address . "<br>";
            echo $city . "<br>";
            echo "<br>";
            echo "<div class='emailheader'>" . $contactEmail . "</div>";
            echo "<div class='orderemailinfo'>";
            echo $factory->__w("Order date") . ": " . date("d.m.Y H:i", strtotime($order->rowCreatedDate));
            echo "<span class='ordernumberinfo'>";
            echo $factory->__w("Order number") . ": " . $order->incrementOrderId;
            echo "</div>";
            echo "<div class='orderemailinfo'>";
            echo $factory->__w("Due date") . ": " . $dueDate;
            echo "<span class='ordernumberinfo'>";
            echo $factory->__w("To account") . ": " . $accountNumber;
            echo "</div>";
            echo "</span>";
            ?>
        </div>
        <div class='headerseperator'></div>
        <div class='itemsarea'>
            <?php
            foreach($order->cart->items as $item) {
                if(!isset($taxes[$item->product->taxGroupObject->taxRate])) {
                    $taxes[$item->product->taxGroupObject->taxRate] = 0;
                }
                $taxes[$item->product->taxGroupObject->taxRate] += $item->product->price - $item->product->priceExTaxes;
                $netAmount += $item->product->priceExTaxes;
                echo "<div class='orderitem'>";
                echo "<span class='orderitemdescription'>" . createItemText($item) . "</span>";
                echo "<span class='orderitemcount'>" . $item->count . "</span>";
                echo "<span class='orderitemprice'>" . round($item->product->priceExTaxes,2) . "</span>";
                echo "<span class='orderitemtax'>" . $item->product->taxGroupObject->taxRate . "%</span>";
                echo "<span class='orderitemtotal'>" . $item->product->price . " " . $currency . "</span>";
                echo "</div>";
            }
            ?>
        </div>

        <div class='notearea'>
            <div class='noteareaheader'><?php echo $factory->__w("Note"); ?></div>
            <div class='notearea'><?php if(isset($order->invoiceNote)) { echo nl2br($order->invoiceNote); } ?></div>
        </div>
        <div class='summaryarea'>
            <div class='summaryareaheader'><?php echo $factory->__w("Sumary"); ?></div>
            <div class='summaryline'><span class='summaryleft'><?php echo $factory->__w("Net amount"); ?></span><span class='summaryright'><?php echo round($netAmount, 2). " " . $currency; ?></span></div>
            <?php
            foreach($taxes as $tax => $value) {
                ?>
                <div class='summaryline'><span class='summaryleft'><?php echo $factory->__w("TAX") . " " . $tax . "%"; ?></span><span class='summaryright'><?php echo round($value, 2). " " . $currency; ?></span></div>
                <?php
            }
            ?>
            <div class='summaryline total'><span class='summaryleft'><?php echo $factory->__w("Total"); ?></span><span class='summaryright'><?php echo round($total, 2). " " . $currency; ?></span></div>
        </div>
        <div class='noteareasummaryseperator'></div>
        <div class='giroinformation'>
            
            <?php
            if($order->status == 7) {
                echo "<span class='alreadypaidtext'>" . $factory->__w("Already paid") . "</span>";
            }
            ?>
            
            <div class='giroheader'>
                <div class='paytoaccountinfo'>
                    <div class='paytoaccount'><?php echo $factory->__w("Pay to account"); ?></div>
                    <?php echo $accountNumber; ?>
                </div>
                <div class='amountToPayGiro'>
                    <div class='paytoaccount'><?php echo $factory->__w("Amount"); ?></div>
                    <span class='amounttopayarea'><?php echo round($total, 2); ?></span>
                </div>
                <div class='paidByAccount'>
                    <div class='paytoaccount'><?php echo $factory->__w("Paid by account"); ?></div>
                    <span class='amounttopayarea'><?php echo ""; ?></span>
                </div>
            </div>

            <div class='giromiddle'>
                <div class='giropaymentinfo giromiddleareas'>
                    <div class='giromiddleareaheader'><?php echo $factory->__w("Payment information"); ?></div>
                    <?php echo "<div>" . $factory->__w("Invoice date") . " : " . date("d.m.Y", strtotime($order->rowCreatedDate)) . "</div>"; ?>
                    <?php echo "<div>" . $factory->__w("Invoice number") . " : " . $order->incrementOrderId . "</div>"; ?>
                </div>         
                <div class='giropaywheninfo giromiddleareas'>
                    <div class='giromiddleareaheader'><?php echo $factory->__w("Due date"); ?></div>
                    <?php echo "<div>" . $dueDate . "</div>"; ?>
                </div>         
                <div class='giropaidby giromiddleareas'><div class='giromiddleareaheader'><?php echo $factory->__w("Paid by"); ?></div>
                </div>         
                <div class='giropaidto giromiddleareas'>
                    <div class='giromiddleareaheader'><?php echo $factory->__w("Paid to"); ?></div>
                    <?php echo "<div>" . $companyName . "</div>"; ?>
                    <?php echo "<div>" . $address . "</div>"; ?>
                    <?php echo "<div>" . $city . "</div>"; ?>
                </div>         
            </div>

            <div class='girofooter'>
                <div class='footaccountnumberarea'>
                    <span class='accountnumber'></span>
                    <span class='accountnumber'></span>
                    <span class='accountnumber'></span>
                    <span class='accountnumber'></span>
                    <span class='accountnumber'></span>
                    <span class='accountnumber'></span>
                    <span class='accountnumber'></span>
                    <span class='accountnumber'></span>
                    <span class='accountnumber'></span>
                    <span class='accountnumber'></span>
                    <span class='accountnumber'></span>
                    <span class='accountnumber'></span>
                </div>
                <div class='kidarea'>
                    <div class='girfooterheadertext'><?php echo $factory->__w("Customer identification (KID)"); ?></div>
                    <?php 
                    if(isset($order->kid)) {
                        echo $order->kid; 
                    }
                    ?>
                </div>
                <div class='intamountarea'>
                    <div class='girfooterheadertext'><?php echo $currency; ?></div>
                    <?php 
                    echo floor($total); 
                    ?>
                </div>
                <div class='remainderarea'>
                    <div class='girfooterheadertext'><?php echo "&nbsp;"; ?></div>
                    <?php 
                    $rest = $total-floor($total); 
                    if($rest < 10) {
                        echo "0" . $rest;
                    }else {
                        echo $rest;
                    }
                    ?>
                </div>
                <div class='toaccount'>
                    <div class='girfooterheadertext'><?php echo $factory->__w("To account"); ?></div>
                    <?php
                    echo $accountNumber;
                    ?>
                </div>
                <div style='clear:both;'></div>
            </div>
            <div class='girofooterbottom'></div>
        </div>
    </div>
</body>

<style>
    .alreadypaidtext { position:Absolute; width: 1030px; height:600px;z-index:2; background-color:rgba(0,0,0,0.8); text-align: center; font-size: 60px; color:#fff; box-sizing:border-box; padding-top: 100px; }
    .girfooterheadertext { font-weight: bold; font-size: 12px; }
    .kidarea { border-left: solid 1px; width: 200px; display: inline-block; float:left; height: 55px; margin-left: 40px; padding-left: 10px; padding-top: 5px; box-sizing: border-box; }
    .intamountarea { border-left: solid 1px; width: 100px; display: inline-block; float:left; height: 55px;  margin-left: 40px; padding-left: 10px; padding-top: 5px; box-sizing: border-box; }
    .remainderarea { border-left: solid 1px; width: 100px; display: inline-block; float:left; height: 55px;  margin-left: 40px; padding-left: 10px; padding-top: 5px; box-sizing: border-box; }
    .toaccount { border-left: solid 1px; width: 200px; display: inline-block; float:left; height: 55px; margin-left: 40px; padding-left: 10px; padding-top: 5px; box-sizing: border-box; }
    .girofooterbottom { height: 20px; background-color: #fff685;}
    .accountnumber { display:inline-block; background-color:#fff; height: 24px; width: 20px; margin-right: 3px; }
    .footaccountnumberarea { background-color: #fff685; padding: 2px; padding-left: 30px;padding-top: 5px; }
    .giromiddle { height: 400px; display:inline-block; position: relative; width: 100%; box-sizing: border-box;}
    .giropaywheninfo { position:absolute !important; right: 10px; top: 40px; }
    .giropaidto { position:absolute !important; right: 10px; bottom: 40px; width: 300px; height: 60px;}
    .giropaidby { position:absolute !important; left: 10px; bottom: 40px; width: 300px; height: 60px;}
    .giropaymentinfo { position:absolute !important; left: 10px; top: 40px; }
    .giromiddleareaheader { font-weight: bold; position:absolute; top: -22px; left:0px;}
    .giromiddleareas { border: solid 1px #bbb; display:inline-block; position:relative; padding: 10px;  }
    .giroinformation .amountToPayGiro { position: absolute; right: 520px; top: 20px;}
    .giroinformation .paidByAccount { position: absolute; right: 20px; top: 20px;}
    .giroinformation .amounttopayarea { background-color:#fff; padding: 10px; font-size: 16px; box-sizing: border-box; width: 200px;display:inline-block; height: 35px;}
    .paytoaccount { font-weight: bold; font-size: 12px; }
    .paytoaccountinfo { padding: 20px; font-size: 20px; }
    .giroheader { background-color: #fff685; height: 90px; position:relative; }
    .noteareaheader { font-weight: bold; border-bottom: solid 1px #bbb; text-transform: uppercase; }
    .summaryareaheader { font-weight: bold;  border-bottom: solid 1px #bbb; text-transform: uppercase; text-align: right; }
    .noteareasummaryseperator { clear:both; margin-bottom: 10px; height: 50px; }
    .summaryright { float:right; }
    .summaryline.total { font-style: italic; }
    .notearea { box-sizing: border-box; width: 70%; float:left; display:inline-block; padding-right: 20px; }
    .summaryarea { box-sizing: border-box; width: 30%; float:left; display:inline-block; }
    .itemsarea { height: 450px; display:inline-block; }
    .page1 {  width: 1024px; padding: 20px;}
    .recipient,.companyinfo { display:inline-block;  height: 150px;box-sizing: border-box;float:left;}
    .recipient { width:60%; }
    .companyinfo { width:40%; }
    .companyinfoheader { font-weight: bold; font-size: 20px; border-bottom: solid 1px; padding: 5px; text-align: right; margin-bottom: 5px;}
    .ordernumberinfo { float:right; }
    .headerseperator { clear:both; height: 80px; }
    .companyname { font-weight: bold; }
    .emailheader {border-bottom: solid 1px; margin-bottom: 5px; padding-bottom: 5px; font-size: 12px; }
    .orderemailinfo { font-size: 12px; }
    .orderitemdescription { display:inline-block; width: 700px; }
    .orderitemprice { display:inline-block; width: 120px; }
    .orderitemcount { display:inline-block; width: 40px; }
    .orderitemtotal { display:inline-block; width: 120px; text-align: right; }
</style>

<?php
$content = ob_get_contents();
ob_end_clean();
$factory->setLanguage($cur);

header('Content-Type: application/pdf');
header('filename="'.$order->incrementOrderId.'.pdf"');

$res = $api->getGetShop()->getBase64EncodedPDFWebPageFromHtml(base64_encode($content));
echo base64_decode($res);
//echo $content;
?>