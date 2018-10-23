<style>
    .row {
        border-bottom: solid 1px;
    }
    
    .row .col {
        display: inline-block;
        vertical-align: top;
        width: 200px;
    }
</style>    

<?php
chdir("../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();
$file = $factory->getApi()->getGetShopAccountingManager()->getOrderFile($_GET['fileId']);

echo "count: ".count($file->accountingTransactionLines);

$grouped = array();
foreach ($file->accountingTransactionLines as $line) {
    $grouped[$line->start][] = $line;
}

function sortFunction($a, $b) {
    return strtotime($a) > strtotime($b);
}

uksort($grouped, "sortFunction");

$totalDebit = 0;
$totalCredit = 0;

foreach ($grouped as $lines) {
    echo "<hr>";
    foreach ($lines as $line) {
        echo "<div class='row'>";
            echo "<div class='col col_start'>".date('d.m.Y h:i:s', strtotime($line->start))."</div>";
            echo "<div class='col col_end'>".date('d.m.Y h:i:s', strtotime($line->end))."</div>";
            echo "<div class='col col_accountingnumber'>".$line->accountNumber."</div>";
            echo "<div class='col col_description'>".$line->description."</div>";
            echo "<div class='col col_debit'>".$line->debit."</div>";
            echo "<div class='col col_credit'>".$line->credit."</div>";
        echo "<div>";
        
        $totalDebit += $line->debit;
        $totalCredit += $line->credit;
    }
}

echo "<hr>";
echo "<div class='row'>";
    echo "<div class='col col_start'>Sum egendeler</div>";
    echo "<div class='col col_end'></div>";
    echo "<div class='col col_accountingnumber'></div>";
    echo "<div class='col col_description'></div>";
    echo "<div class='col col_debit'>".$totalDebit."</div>";
    echo "<div class='col col_credit'>".$totalCredit."</div>";
echo "<div>";

?>