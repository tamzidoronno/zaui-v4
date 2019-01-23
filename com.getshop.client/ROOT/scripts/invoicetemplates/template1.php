<?php
$accountNumber = "{accountNumber}";
$iban = "{iban}";
$swift = "{swift}";
$address = "{address}";
$city = "{city}";
$companyName = "{companyName}";
$contactEmail = "{contactEmail}";
$dueDays = "{duedays}";
$vatNumber = "{vatNumber}";
$webAddress = "{webAddress}";
$useLanguage = "{language}";
$kidSize = "{kidSize}";
$kidType = "{defaultKidMethod}";
$type = "{type}";
$currency = "{currency}";
$rowCreatedDate = "{rowCreatedDate}";
$dueDate = "{dueDate}";
$incrementOrderId = "{incrementOrderId}";
$itemLines = "{itemLines}";
$itemLinesLarge = "{itemLinesLarge}";
$invoiceNote = "{invoiceNote}";
$status = (int)$_GET['status'];
$kid = "{kid}";
$fullName = "{fullName}";
$addressaddress = "{addressaddress}";
$addresspostCode = "{addresspostCode}";
$addresscountryname = "{addresscountryname}";
$addresscity = "{addresscity}";
$total = "{total}";
$co = "{co}";
$netAmount = "{netAmount}";

function doTranslation($key) {
    return "<text>$key</text>";
}

?>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
</head>
<body>
    <div class='page1'>
        <div class='logoarea'>{logo}</div>
        <div class='recipient'>
            <?php
            echo $fullName . "<br>";
            echo $co;
            echo $addressaddress. "<br>";
            echo $addresspostCode . " " . $addresscity. "<br>";
            echo $addresscountryname. "<br>";
            ?>
        </div>
        <div class='companyinfo'>
            <div class='companyinfoheader'>
                <?php echo doTranslation("Invoice"); ?>
            </div>
            <?php
            echo "<span class='companyname'>" . $companyName . "</span><br>";
            echo $vatNumber . "<br>";
            echo $address . "<br>";
            echo $city . "<br>";
            echo "<br>";
            echo "<div class='emailheader'>" . $contactEmail . "</div>";
            echo "<div class='orderemailinfo'>";
            echo doTranslation("Order date") . ": " . $rowCreatedDate;
            echo "<span class='ordernumberinfo'>";
            echo doTranslation("Order number") . ": " . $incrementOrderId;
            echo "</div>";
            echo "<div class='orderemailinfo'>";
            echo doTranslation("Due date") . ": " . $dueDate;
            echo "<span class='ordernumberinfo'>";
            echo doTranslation("To account") . ": " . $accountNumber;
            echo "</div>";
            echo "</span>";
            ?>
        </div>
        <div class='headerseperator'></div>
        <div class='itemsarea'>
            
            <?php
            echo $itemLines;
            ?>
        </div>

        <div class='notearea'>
            <div class='noteareaheader'><?php echo doTranslation("Note"); ?></div>
            <div class='notearea'><?php if(isset($invoiceNote)) { echo nl2br($invoiceNote); } ?></div>
        </div>
        <div class='summaryarea'>
            <div class='summaryareaheader'><?php echo doTranslation("Sumary"); ?></div>
            <div class='summaryline'><span class='summaryleft'><?php echo doTranslation("Net amount"); ?></span><span class='summaryright'><?php echo $netAmount . " " . $currency; ?></span></div>
            {taxLines}
            <div class='summaryline total'><span class='summaryleft'><?php echo doTranslation("Total"); ?></span><span class='summaryright'><?php echo $total . " " . $currency; ?></span></div>
        </div>
        <div class='noteareasummaryseperator'></div>
        <div class='giroinformation'>
            
            <?php
            if($status == 7) {
                echo "<span class='alreadypaidtext'>" . doTranslation("Already paid") . "</span>";
            }
            ?>
            
            <div class='giroheader'>
                <div class='paytoaccountinfo'>
                    <div class='paytoaccount'><?php echo doTranslation("Pay to account"); ?></div>
                    <?php echo $accountNumber; ?>
                </div>
                <div class='amountToPayGiro'>
                    <div class='paytoaccount'><?php echo doTranslation("Amount"); ?></div>
                    <span class='amounttopayarea'><?php echo $total; ?></span>
                </div>
                <div class='paidByAccount'>
                    <div class='paytoaccount'><?php echo doTranslation("Paid by account"); ?></div>
                    <span class='amounttopayarea'><?php echo ""; ?></span>
                </div>
            </div>

            <div class='giromiddle'>
                <div class='giropaymentinfo giromiddleareas'>
                    <div class='giromiddleareaheader'><?php echo doTranslation("Payment information"); ?></div>
                    <?php echo "<div>" . doTranslation("Invoice date") . " : " . $rowCreatedDate . "</div>"; ?>
                    <?php echo "<div>" . doTranslation("Invoice number") . " : " . $incrementOrderId . "</div>"; ?>
                </div>         
                <div class='giropaywheninfo giromiddleareas'>
                    <div class='giromiddleareaheader'><?php echo doTranslation("Due date"); ?></div>
                    <?php echo "<div>" . $dueDate . "</div>"; ?>
                </div>         
                <div class='giropaidby giromiddleareas'><div class='giromiddleareaheader'><?php echo doTranslation("Paid by"); ?>
                    
                        <div class="paidbytext">
                            <?php
                            echo $fullName . "<br>";
                            echo $addressaddress. "<br>";
                            echo $addresspostCode . " " . $addresscity. "<br>";
                            echo $addresscountryname. "<br>";
                            ?>
                        </div>
                    </div>
                </div>         
                <div class='giropaidto giromiddleareas'>
                    <div class='giromiddleareaheader'><?php echo doTranslation("Paid to"); ?></div>
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
                    <div class='girfooterheadertext'><?php echo doTranslation("Customer identification (KID)"); ?></div>
                    <?php 
                    if(isset($kid)) {
                        echo $kid; 
                    }
                    ?>
                </div>
                <div class='intamountarea'>
                    <div class='girfooterheadertext'><?php echo $currency; ?></div>
                    {totalFloor}
                </div>
                <div class='remainderarea'>
                    <div class='girfooterheadertext'><?php echo "&nbsp;"; ?></div>
                    {totalRest}
                </div>
                <div class='toaccount'>
                    <div class='girfooterheadertext'><?php echo doTranslation("To account"); ?></div>
                    <?php
                    echo $accountNumber;
                    ?>
                </div>
                <div style='clear:both;'></div>
            </div>
            <div class='girofooterbottom'></div>
        </div>
    </div>
    <?php echo $itemLinesLarge; ?>
</body>

<style>
    .alreadypaidtext { position:Absolute; width: 1030px; height:600px;z-index:2; background-color:rgba(0,0,0,0.8); text-align: center; font-size: 60px; color:#fff; box-sizing:border-box; padding-top: 100px; }
    .girfooterheadertext { font-weight: bold; font-size: 12px; }
    .kidarea { border-left: solid 1px; width: 200px; display: inline-block; float:left; height: 55px; margin-left: 40px; padding-left: 10px; padding-top: 5px; box-sizing: border-box; }
    .intamountarea { border-left: solid 1px; width: 100px; display: inline-block; float:left; height: 55px;  margin-left: 40px; padding-left: 10px; padding-top: 17px; box-sizing: border-box; }
    .remainderarea { border-left: solid 1px; width: 100px; display: inline-block; float:left; height: 55px;  margin-left: 40px; padding-left: 10px; padding-top: 5px; box-sizing: border-box; }
    .toaccount { border-left: solid 1px; width: 200px; display: inline-block; float:left; height: 55px; margin-left: 40px; padding-left: 10px; padding-top: 5px; box-sizing: border-box; }
    .girofooterbottom { height: 20px; background-color: #fff685;}
    .accountnumber { display:inline-block; background-color:#fff; height: 24px; width: 20px; margin-right: 3px; }
    .footaccountnumberarea { background-color: #fff685; padding: 2px; padding-left: 30px;padding-top: 5px; }
    .giromiddle { height: 380px; display:inline-block; position: relative; width: 100%; box-sizing: border-box;}
    .giropaywheninfo { position:absolute !important; right: 10px; top: 40px; }
    .giropaidto { position:absolute !important; right: 10px; bottom: 40px; width: 300px; height: 60px;}
    .giropaidby { position:absolute !important; left: 10px; bottom: 40px; width: 300px; height: 60px;}
    .giropaymentinfo { position:absolute !important; left: 10px; top: 40px; }
    .giromiddleareaheader { font-weight: bold; position:absolute; top: -22px; left:0px; width:200px;}
    .paidbytext { font-weight: normal; font-size:14px; margin-top: 10px; margin-left: 5px; }
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
    .itemsarea { height: 400px; display:inline-block; }
    .logoarea { position:absolute; left:30px; top:30px; }
    .logoarea img { max-width: 400px; max-height: 150px; }
    .page1 {  width: 1024px; padding: 20px;}
    .recipient,.companyinfo { display:inline-block;  height: 150px;box-sizing: border-box;float:left;}
    .recipient { width:60%; margin-top:180px; padding-left:50px; }
    .companyinfo { width:40%; }
    .companyinfoheader { font-weight: bold; font-size: 20px; border-bottom: solid 1px; padding: 5px; text-align: right; margin-bottom: 5px;}
    .ordernumberinfo { float:right; }
    .headerseperator { clear:both; height: 80px; }
    .companyname { font-weight: bold; }
    .emailheader {border-bottom: solid 1px; margin-bottom: 5px; padding-bottom: 5px; font-size: 12px; }
    .orderemailinfo { font-size: 12px; }
    .orderitemdescription { display:inline-block; width: 650px; }
    .orderitemprice { display:inline-block; width: 120px; }
    .orderitemcount { display:inline-block; width: 40px; }
    .orderitemtotal { display:inline-block; width: 120px; text-align: right; }
    .page2 { padding: 20px; }
</style>
