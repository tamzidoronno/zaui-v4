<?php //
$factory = IocContainer::getFactorySingelton();

$customers = $factory->getApi()->getPmsGetShopOverView()->getCustomerToSetup();

echo "<h1>Setup progress on current customers</h1>";

echo "<table width='100%' cellspacing='1' cellpadding='0'>";
echo "<tr>";
echo "<th align='left'>Date</th>";
echo "<th align='left'>Customer</th>";
echo "<th>Payments</th>";
echo "<th>MSGS</th>";
echo "<th>GDPR</th>";
echo "<th>T&C</th>";
echo "<th>Cats</th>";
echo "<th>Rooms</th>";
echo "<th>Website</th>";
echo "<th>Products</th>";
echo "<th>Accounting</th>";
echo "<th>Plink</th>";
echo "<th>Prices</th>";
echo "<th>wubook</th>";
echo "<th>reciept</th>";
echo "<th>locks</th>";
echo "<th>training</th>";
echo "<th>completed</th>";
echo "</tr>";

foreach($customers as $cust) {
    echo "<tr storeid='".$cust->customerStoreId."' class='customerrow'>";
    echo "<td>" . date("d.m.Y", strtotime($cust->storeDate)) . "</td>";
    
    echo "<td class='customerfield' style='width:500px;'><span class='commentbtn'>(c)</span> " . $cust->address . " (<span class='commentfield'>".$cust->comment."</span>" .  ")</td>";
    
    $stateclass = $cust->paymentMethodsSetup > 0 ? "correct" : "fail";
    echo "<td align='center' class='$stateclass'>".$cust->paymentMethodsSetup."</td>";
    
    $stateclass = $cust->numberOfMessagesSetup > 0 ? "correct" : "fail";
    echo "<td align='center' class='$stateclass'>".$cust->numberOfMessagesSetup."</td>";
    
    $gdpr = $cust->acceptedGdpr ? "YES" : "NO";
    $stateclass = $cust->acceptedGdpr ? "correct" : "fail";
    echo "<td align='center' class='$stateclass'>$gdpr</td>";
    
    $terms = $cust->addedTermsAndConditions ? "YES" : "NO";
    $stateclass = $cust->addedTermsAndConditions ? "correct" : "fail";
    echo "<td align='center' class='$stateclass'>$terms</td>";
    
    $stateclass = $cust->numberOfCategoriesAdded > 0 ? "correct" : "fail";
    echo "<td align='center' class='$stateclass'>".$cust->numberOfCategoriesAdded."</td>";
    
    $stateclass = $cust->numberOfRoomsAdded > 0 ? "correct" : "fail";
    echo "<td align='center' class='$stateclass'>".$cust->numberOfRoomsAdded."</td>";
    
    $wsite = $cust->websiteCompleted ? "YES" : "NO";
    $stateclass = $cust->websiteCompleted ? "correct" : "fail";
    echo "<td align='center' class='$stateclass cantoggleyesno' attr='websiteCompleted'>$wsite</td>";
    
    $stateclass = $cust->numberOfProductsCreated > 0 ? "correct" : "fail";
    echo "<td align='center' class='$stateclass'>".$cust->numberOfProductsCreated."</td>";
    
    $acc = $cust->completedAccountingIntegration ? "YES" : "NO";
    $stateclass = $cust->completedAccountingIntegration ? "correct" : "fail";
    echo "<td align='center' class='$stateclass cantoggleyesno' attr='completedAccountingIntegration'>$acc</td>";
    
    $plink = $cust->completedPaymentlinkTest ? "YES" : "NO";
    $stateclass = $cust->completedPaymentlinkTest ? "correct" : "fail";
    echo "<td align='center' class='$stateclass cantoggleyesno' attr='completedPaymentlinkTest'>$plink</td>";
    
    $stateclass = $cust->pricesCompleted ? "correct" : "fail";
    $prices = $cust->pricesCompleted ? "YES" : "NO";
    echo "<td align='center' class='$stateclass'>$prices</td>";
    
    $stateclass = $cust->wubookSetUpState == 2 ? "correct" : "fail";
    echo "<td align='center' class='$stateclass'>".$cust->wubookSetUpState."</td>";
    
    $stateclass = $cust->numberOfFieldsSetupInInvoice >= 12 ? "correct" : "fail";
    echo "<td align='center' class='$stateclass'>".$cust->numberOfFieldsSetupInInvoice."</td>";
    
    $stateclass = $cust->locksInstalled == 12 ? "correct" : "fail";
    $locks = $cust->locksInstalled ? "YES" : "NO";
    echo "<td align='center' class='$stateclass cantoggleyesno' attr='locksInstalled'>$locks</td>";
    
    $stateclass = $cust->trainingCompleted == 12 ? "correct" : "fail";
    $training = $cust->trainingCompleted ? "YES" : "NO";
    echo "<td align='center' class='$stateclass cantoggleyesno' attr='trainingCompleted'>$training</td>";
    
    $stateclass = $cust->completed ? "correct" : "fail";
    $training = $cust->completed ? "YES" : "NO";
    echo "<td align='center' class='$stateclass cantoggleyesno' attr='completed'>$training</td>";
    echo "</tr>";
}
echo "</table>";
?>

<style>
    .correct { background-color:green; color:#fff; }
    .fail { background-color:red; color:#fff; }
    .cantoggleyesno { cursor:pointer; }
    .commentbtn { cursor:pointer; }
    .customerrow:hover td.customerfield { font-weight: bold; }
</style>

<script>
    $('.commentbtn').click(function() {
        var storeid = $(this).closest('tr').attr('storeid');
        var curcomment = $(this).closest('tr').find('.commentfield').text();
        var comment = prompt("Add comment", curcomment);
        $(this).closest('tr').find('.commentfield').html(comment);
        $.ajax('comment.php', {
            dataType : "POST",
            data: {
                "storeid" : storeid,
                "comment" : comment
            },
            success : function(res) {}

        });
    });
    
    $('.cantoggleyesno').click(function() {
        var attr = $(this).attr('attr');
        var storeid = $(this).closest('tr').attr('storeid');
        var val = $(this).html();
        $(this).removeClass('correct');
        $(this).removeClass('fail');
        if(val === "NO") {
            $(this).html('YES');
            $(this).addClass('correct');
        } else {
            $(this).html('NO');
            $(this).addClass('fail');
        }
        
        $.ajax('toggle.php', {
            data: {
                "storeid" : storeid,
                "attribute" : attr
            },
            success : function(res) {}

        });
    });
</script>