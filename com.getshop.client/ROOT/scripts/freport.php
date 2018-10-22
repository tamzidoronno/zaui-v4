<style>
    .row {
        border-bottom: solid 1px #DDD;
        font-size: 16px;
        padding: 5px;
    }
    
    .row .col {
        display: inline-block;
        vertical-align: top;
        width: 200px;
    }
    
    .row .col.col_start {
        width: 50px;
    }
    
    .row .col.col_description {
        width: calc(100% - 480px);
    }
    
    
    .monthoverview {
        margin-bottom: 50px;
        margin-left: 20px;
        margin-right: 20px;
    }
    
    .monthheader {
        background-color: #EFEFEF;
        font-size: 18px;
        text-align: center;
        padding: 5px;
        line-height: 30px;
    }
    
    .summary {
        background-color: #EEE;
        padding: 20px;
        font-size: 20px;
        margin-top: 50px;
    }
    
    .heading {
        padding: 20px;
        font-size: 20px;
    }
</style>    

<?php
setlocale(LC_TIME, 'nb_NO');
chdir("../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();
$file = $factory->getApi()->getGetShopAccountingManager()->getOrderFile($_GET['fileId']);
$eiendeler = array();

$GLOBALS['overAllDebit'] = 0;
$GLOBALS['overAllCredit'] = 0;


$grouped = array();
foreach ($file->accountingTransactionLines as $line) {
    $monthDate = date('01.m.Y', strtotime($line->start));
    
    if (!isset($grouped[$monthDate])) {
        $grouped[$monthDate] = array();
    }
    
    if ((int)$line->accountNumber < 2000) {
        $eiendeler[$line->accountNumber][] = $line;
        continue;
    }
    
    if (!isset($grouped[$monthDate][$line->accountNumber])) {
        $grouped[$monthDate][$line->accountNumber] = array();
    }
    
    $grouped[$monthDate][$line->accountNumber][] = $line;
}

function sortFunction($a, $b) {
    return ((int)$a) > ((int)$b);
}

function sortFunctionDate($a, $b) {
    return ((int)strtotime($a)) > ((int)strtotime($b));
}

$start = date('d.m.Y', strtotime($file->startDate));
$end = date('d.m.Y', strtotime($file->endDate));
?>

<div class="heading">
    <h2>Regnskapsbilag</h2>
    Opprettet dato: <? echo date('d/m-Y H:i:s', strtotime($file->rowCreatedDate)); ?>
    <br/> Eiendelsperiode: <? echo $start. " - ".$end; ?>
</div>

<?

function printLines($monthLines, $sumtext, $api) {
    $totalDebit=0;
    $totalCredit=0;
    
    uksort($monthLines, "sortFunction");

    echo "<div class='row total'>";
       echo "<div class='col col_start'>Konto</div>";
       echo "<div class='col col_description'></div>";
       echo "<div class='col col_debit'>Debit</div>";
       echo "<div class='col col_credit'>Credit</div>";
   echo "</div>";
   
    foreach ($monthLines as $accountNumber => $lines) {
        $subDebit = 0;
        $subCredit = 0;

        foreach ($lines as $line) {
            $subDebit += $line->debit;
            $subCredit += $line->credit;
        }

        if (!$subDebit && !$subCredit)
            continue;

        $desc = $line->description ? $line->description : "";
        if (!$desc) {
            $accountingDetail = $api->getProductManager()->getAccountingDetail($accountNumber);
            $desc = $accountingDetail ? $accountingDetail->description : "";
        }
        
        echo "<div class='row'>";
            echo "<div class='col col_start'>$accountNumber</div>";
            echo "<div class='col col_description'>$desc</div>";
            echo "<div class='col col_debit'>".$subDebit."</div>";
            echo "<div class='col col_credit'>".$subCredit."</div>";
        echo "</div>";

        $totalDebit += $subDebit;
        $totalCredit += $subCredit;
    }

    echo "<div class='row total'>";
       echo "<div class='col col_start'></div>";
       echo "<div class='col col_description'><b>$sumtext</b></div>";
       echo "<div class='col col_debit'><b>".$totalDebit."</b></div>";
       echo "<div class='col col_credit'><b>".$totalCredit."</b></div>";
   echo "</div>";
   
   $GLOBALS['overAllDebit'] += $totalDebit;
   $GLOBALS['overAllCredit'] += $totalCredit;
}


echo "<div class='monthoverview'>";
    echo "<div class='monthHeader'> Eiendeler $start - $end</div>";
    printLines($eiendeler, "Sum eiendeler", $factory->getApi());
echo "</div>";

uksort($grouped, "sortFunctionDate");

foreach ($grouped as $firstDayOfMonthKey => $monthLines) {
    if (!count($monthLines))
        continue;

    $monthName = date('F - Y', strtotime($firstDayOfMonthKey));
    
    echo "<div class='monthoverview'>";
        echo "<div class='monthHeader'> Salgs og driftsinntekter - $monthName</div>";
        printLines($monthLines, "Sum Salgs og driftsinntekter", $factory->getApi());
    echo "</div>";
}

$overAllDebit = round($GLOBALS['overAllDebit'], 2);
$overAllCredit = round($GLOBALS['overAllCredit'],2);


echo "<div class='monthoverview'>";
    echo "<div class='row total'>";
       echo "<div class='col col_start'></div>";
       echo "<div class='col col_description'>Sum</div>";
       echo "<div class='col col_debit'>".$overAllDebit."</div>";
       echo "<div class='col col_credit'>".$overAllCredit."</div>";
   echo "</div>";
echo "</div>";
?>