<?
if(isset($_GET['generatingePdf'])) {
    echo file_get_contents("/tmp/tmpstatswh.pdf");
    exit(0);
}

ob_start();

chdir("../");
include '../loader.php';

$factory = IocContainer::getFactorySingelton();
$api = $factory->getApi();

if(isset($_GET['username'])) {
    $username = $_GET['username'];
    $password = $_GET['password'];
    $factory->getFactory()->getApi()->getUserManager()->logOn($username, $password);
}

$doit = false;
if(date('t') == date('d')){
    $doit = true;
}
if(date('w') == 0){
    $doit = true;
}

if(isset($_GET['timeperiode'])) {
    if($_GET['timeperiode'] == "yearly" && date('t') == date('d')) {
        $doit = true;
    } else {
        $doit = false;
    }
}

if(!$doit) {
    return;
}
?>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
</head>
<?php
$_POST['data']['periode'] = "daily";
$startdate = strtotime(date('01-m-Y'));
$enddate = strtotime(date("Y-m-t"));

if(isset($_GET['timeperiode']) && $_GET['timeperiode'] == "yearly" && date('t') == date('d')) {
    $startdate = strtotime(date('01-01-Y'));
    $enddate = strtotime(date("Y-12-t"));
    $_POST['data']['periode'] = "monthly";
}
    

$instances = $factory->getApi()->getStoreApplicationInstancePool()->getApplicationInstances("7e828cd0-8b44-4125-ae4f-f61983b01e0a");
foreach($instances as $instance) {
    $app = $factory->getFactory()->getApplicationPool()->createAppInstance($instance);
    
    /* @var @app ns_7e828cd0_8b44_4125_ae4f_f61983b01e0a\PmsManagement */
    echo "<div style='width: 1000px; margin: auto;'>";
    $_POST['event'] = "true";
    $filter = new core_pmsmanager_PmsBookingFilter();
    $filter->filterType = "summary";
    $filter->startDate = $app->convertToJavaDate($startdate);
    $filter->endDate = $app->convertToJavaDate($enddate);
    $app->setCurrentFilter($filter);
    $app->includeManagementViewResult();
    
    echo "<h2>Total coverage</h2>";
    $filter->typeFilter = array();
    $filter->filterType = "stats";
    $app->setCurrentFilter($filter);
    $app->includeManagementViewResult();
    
    $channels = $app->getChannels();
    foreach($channels as $chan) {
        if(!$chan || !$chan->humanReadableText) {
            continue;
        }
        echo "<h2>" . $chan->humanReadableText . "</h2>";
        $filter->typeFilter = array();
        $filter->filterType = "stats";
        $filter->channel = $chan->channel;
        $app->setCurrentFilter($filter);
        $app->includeManagementViewResult();
    }
    
    $types = $app->getTypes();
    foreach($types as $id => $type) {
        echo "<h2>" . $type->name . "</h2>";
        $filter->typeFilter = array();
        $filter->typeFilter[] = $type->id;
        $filter->filterType = "stats";
        $filter->channel = "";
        $app->setCurrentFilter($filter);
        $app->includeManagementViewResult();
    }
    echo "</div>";
}
?>
<style>
    .changeintervalrow { display:none; }
    th {
	font: bold 11px "Trebuchet MS", Verdana, Arial, Helvetica,
	sans-serif;
	color: #6D929B;
	border-right: 1px solid #C1DAD7;
	border-bottom: 1px solid #C1DAD7;
	border-top: 1px solid #C1DAD7;
	letter-spacing: 2px;
	text-transform: uppercase;
	text-align: left;
	padding: 6px 6px 6px 12px;
	background: #CAE8EA url(images/bg_header.jpg) no-repeat;
}

td {
	border-right: 1px solid #C1DAD7;
	border-bottom: 1px solid #C1DAD7;
	background: #fff;
	padding: 6px 6px 6px 12px;
	color: #000;
}

table tr:nth-child(even) td {
    background-color: #fff;
}
table tr:nth-child(odd) td {
    background-color: #efefef;
}
table td.budget_success { background-color:green !important; color:#fff; }
table td.budget_fail { background-color:red !important; color:#fff; }
td:nth-child(1) {
    border-left: 1px solid #C1DAD7;
}

tr:nth-child(2) {
	background: #F5FAFA;
	color: #B4AA9D;
}

</style>