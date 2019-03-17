<?
//
//if(isset($_GET['generatingePdf'])) {
//    $storeId = $_GET['storeid'];
//    echo file_get_contents("/tmp/tmpstats_$storeId.pdf");
//    exit(0);
//}
//
//ob_start();
//
//chdir("../");
//include '../loader.php';
//
//$factory = IocContainer::getFactorySingelton();
//$api = $factory->getApi();
//if(isset($_GET['sessid'])) {
//    session_id($_GET['sessid']);
//}
//if(isset($_GET['username'])) {
//    $username = $_GET['username'];
//    $password = $_GET['password'];
//    $factory->getFactory()->getApi()->getUserManager()->logOn($username, $password);
//}
//
//$doit = false;
//if(date('t') == date('d')){
//    $doit = true;
//}
//if(date('w') == 0){
//    $doit = true;
//}
//
//if(isset($_GET['timeperiode'])) {
//    if($_GET['timeperiode'] == "yearly" && date('t') == date('d')) {
//        $doit = true;
//    } else {
//        $doit = false;
//    }
//}
//
//if(!$doit) {
//    return;
//}
//?>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
</head>
//<?php
//$_POST['data']['periode'] = "daily";
//$startdate = strtotime(date('01-m-Y'));
//$enddate = strtotime(date("Y-m-t"));
//if(isset($_GET['timeperiode']) && $_GET['timeperiode'] == "yearly" && date('t') == date('d')) {
//    $startdate = strtotime(date('01-01-Y'));
//    $enddate = strtotime(date("Y-12-t"));
//    $_POST['data']['periode'] = "monthly";
//}
//$enddate = strtotime(date("d.m.Y 23:00", $enddate));
//    
//
//$instances = $factory->getApi()->getStoreApplicationInstancePool()->getApplicationInstances("7e828cd0-8b44-4125-ae4f-f61983b01e0a");
//foreach($instances as $instance) {
//    $app = $factory->getFactory()->getApplicationPool()->createAppInstance($instance);
//    
//    /* @var @app ns_7e828cd0_8b44_4125_ae4f_f61983b01e0a\PmsManagement */
//    echo "<div style='width: 1200px; margin: auto;'>";
//    $_POST['event'] = "true";
//    $filter = new core_pmsmanager_PmsBookingFilter();
//    $filter->filterType = "summary";
//    $filter->startDate = $app->convertToJavaDate($startdate);
//    $filter->endDate = $app->convertToJavaDate($enddate);
//    $app->setCurrentFilter($filter);
//
//    $app->includeManagementViewResult();
//    
//    $filter = new core_pmsmanager_PmsBookingFilter();
//    $filter->startDate = $app->convertToJavaDate($startdate);
//    $filter->endDate = $app->convertToJavaDate($enddate);
//    $filter->filterType = "stats";
//    $app->setCurrentFilter($filter);
//    echo "<h2>".$factory->__w("Total coverage")."</h2>";
//    $app->includeManagementViewResult();
//    
//    $channels = $app->getChannels();
//    foreach($channels as $chan) {
//        if(!$chan || !$chan->humanReadableText) {
//            continue;
//        }
//        $filter->typeFilter = array();
//        $filter->filterType = "stats";
//        $filter->channel = $chan->channel;
//        $app->setCurrentFilter($filter);
//        echo "<div class='pageheight'>";
//        echo "<h2>" . $chan->humanReadableText . "</h2>";
//        $app->includeManagementViewResult();
//        echo "</div>";
//    }
//    
//    $types = $app->getTypes();
//    foreach($types as $id => $type) {
//        $filter->typeFilter = array();
//        $filter->typeFilter[] = $type->id;
//        $filter->filterType = "stats";
//        $filter->channel = "";
//        $app->setCurrentFilter($filter);
//        echo "<div class='pageheight'>";
//        echo "<h2>" . $type->name . "</h2>";
//        $app->includeManagementViewResult();
//        echo "</div>";
//    }
//    echo "</div>";
//}
//?>
<style>
    h1,h2 { text-align: center; }
    .changeintervalrow { display:none; }
    th {
	color: #6D929B;
	border-right: 1px solid #C1DAD7;
	border-bottom: 1px solid #C1DAD7;
	border-top: 1px solid #C1DAD7;
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
.pageheight, .pdfPageSizer { height: 1700px; padding-left:50px; padding-right: 50px; padding-top: 10px; }
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

//<?php
//if(!isset($_GET['generatingePdf'])) {
//    $pageURL = 'http';
//    if (@$_SERVER["HTTPS"] == "on") {$pageURL .= "s";}
//        $pageURL .= "://";
//    if ($_SERVER["SERVER_PORT"] != "80") {
//        $pageURL .= $_SERVER["SERVER_NAME"].":".$_SERVER["SERVER_PORT"].$_SERVER["REQUEST_URI"];
//    } else {
//        $pageURL .= $_SERVER["SERVER_NAME"].$_SERVER["REQUEST_URI"];
//    }
//
//    
//    $storeId = $factory->getStore()->id;
//    $res = ob_get_contents();
//    ob_end_clean();
//    file_put_contents("/tmp/tmpstats_$storeId.pdf", $res);
//    if(strpos($pageURL, "?") > 0) {
//        $addr = substr($pageURL, 0, strpos($pageURL, "?"));
//    }
//    $addr = $addr."?generatingePdf=true&sessid=".  session_id()."&storeid=$storeId";
//    $pdf = $api->getGetShop()->getBase64EncodedPDFWebPage($addr);
//    
//    $emailsSentTo = array();
//header('Pragma: public');  // required
//header('Expires: 0');  // no cache
//header("Content-type:application/pdf");
//header("Content-Disposition:attachment;filename=".$_GET['incrementalOrderId'].".pdf");
//
////    echo base64_decode($pdf);
//    $attachment = array();
//    $attachment['statistics.pdf'] = $pdf;
//
//    $instances = $factory->getApi()->getStoreApplicationInstancePool()->getApplicationInstances("7e828cd0-8b44-4125-ae4f-f61983b01e0a");
//    $emailtitle = "Weekly update on monthly statistic";
//    foreach($instances as $instance) {
//        /* @var $instance ns_7e828cd0_8b44_4125_ae4f_f61983b01e0a\PmsManagement */
//        $config = $api->getPmsManager()->getConfiguration($app->getSelectedName());
//        $app = $factory->getFactory()->getApplicationPool()->createAppInstance($instance);
//        $emails = $config->emailsToNotify->{'report'};
//        foreach($emails as $email) {
//            if(in_array($email, $emailsSentTo)) {
//                continue;
//            }
//            $emailsSentTo[] = $email;
//            $api->getMessageManager()->sendMailWithAttachments($email, $email, $emailtitle, "Attached you will find the statistics for this periode.", "post@wh.no", "post@wh.no", $attachment);
//        }
//    }
//}

?>