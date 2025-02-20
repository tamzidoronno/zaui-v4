<?php
//chdir("../");
//include '../loader.php';
//$factory = new Factory(true);
//$factory->initialize(true, true);

if(!isset($_GET['start'])) {
    $_GET['start'] = date("d.m.Y H:i", time()-(60*60*3));
    $_GET['end'] = date("d.m.Y H:i", time());
    
}

if (!isset($_GET['start']) || !isset($_GET['end'])) {
    echo "Please specify start and end date";
    return;
}

$filter = array(
    'startedMs' => array('$gt' => strtotime($_GET['start'])*1000, '$lt' => strtotime($_GET['end'])*1000),
); 

$options = array();

$query = new MongoDB\Driver\Query($filter, $options);

$javaStartDate = date("c", strtotime($_GET['start']));
$javaEndDate = date("c", strtotime($_GET['end']));


$manager = new MongoDB\Driver\Manager("mongodb://10.0.4.33:30000");
$cursor = $manager->executeQuery('StoreThreadLog.col_all', $query);

$result = array();
foreach ($cursor as $document) {
    $result[] = $document;
}

function cmp($a, $b) {
    return ($a->startedMs < $b->startedMs) ? 1 : -1;
}

usort($result, "cmp");


// $factory->getApi()->getGetShop()->getThreadLogs($_GET['belongsToStoreId'], $javaStartDate, $javaEndDate);

?>

<head>
    <link rel="shortcut icon" href="favicon2.ico" type="image/x-icon">
</head>


<style>
    th { text-align: left; padding: 5px; }
    td { text-align: left; padding: 5px; }
    tr.yellow td { background-color: yellow; }
    tr.red td { background-color: red; color:#fff; }
    tr.black td { background-color: black; color: #FFF; }
</style>
<table>
    <th>Time</th>
    <th>Name</th>
    <th>Startedms</th>
    <th>Endedms</th>
    <th>Time used</th>
    <th>Type</th>
    <th>Storeid</th>
    
<?
    foreach ($result as $threadlog) {
//        if($threadlog->belongsToStoreId != "1ed4ab1f-c726-4364-bf04-8dcddb2fb2b1") {
//            continue;
//        }
        if($threadlog->type == "async") {
            continue;
        }
        if($threadlog->type == "cron") {
            continue;
        }
        $warningClass = "";
        if (!@$threadlog->timeUsed) {
            $warningClass = "black";
//            continue;
        } else if (@$threadlog->timeUsed > 15000) {
            $warningClass = "red";
//	     continue;
        } else if ($threadlog->timeUsed > 40) {
            $warningClass = "yellow";
            continue;
        } else {
            continue;
        }
        
       ?>
        <tr class="<? echo $warningClass; ?>">
            <td><? echo date('d.m.Y h:i:s', date($threadlog->startedMs/1000)); ?></td>
            <td><? echo $threadlog->threadName; ?></td>
            <td><? echo $threadlog->startedMs; ?></td>
            <td><? echo @$threadlog->endedMs; ?></td>
            <td><? echo @$threadlog->timeUsed; ?></td>
            <td><? echo $threadlog->type; ?></td>
            <td><? echo $threadlog->belongsToStoreId; ?></td>

        </tr>
        <?
    }
?>
        
</table>
