<?php

chdir("../");

if (!isset($_GET['showpicture'])) {
    include('../classes/mpdf/mpdf.php');
}

$id = $_GET['entry'];

if (isset($_GET['showpicture'])) {
    session_cache_limiter('none');
    header("Content-type: image/png");
    header("Cache-Control: private, max-age=10800, pre-check=10800");
    header("Pragma: private");
    
    header("Accept-Ranges: bytes");
    header("Connection:Keep-Alive");
    header("Content-Length:90070");
    header("Content-Type:image/jpeg");
    header("Date:Thu, 27 Nov 2014 13:20:10 GMT");
    header("Keep-Alive:timeout=5, max=98");
    header("Last-Modified:Sun, 07 Sep 2014 14:40:46 GMT");
    header("Server:Apache/2.4.7 (Ubuntu)");
}


include '../loader.php';
$factory = IocContainer::getFactorySingelton(false, false);

$entry = $factory->getApi()->getCalendarManager()->getEntry($_GET['entry']);

$attendees = $entry->attendees;
$day = $entry->day;
if($day < 10) {
    $day = "0" . $day;
}
$month = $entry->month;
if($month < 10) {
    $month = "0" . $month;
}

$date = $day . "." . $month . "." . $entry->year;
$year = $entry->year;
$groups = $factory->getApi()->getUserManager()->getAllGroups();

$location = $entry->location;


$eventDate = date('M d, Y h:m:s A', strtotime("$day-$month-$year"));


$diplomaPeriod = $factory->getApi()->getCalendarManager()->getDiplomaPeriod($eventDate);
$index = strpos($diplomaPeriod->backgroundImage, "base64,");

file_put_contents("diplomapictures/".$diplomaPeriod->id.".jpg", base64_decode(substr($diplomaPeriod->backgroundImage, ($index+7))));

$grouped = array();
foreach($groups as $group) {
    $grouped[$group->id] = $group;
}

$title = "No title specified yet.";
$title2 = "";
$heading = "No heading set yet.";
$text = "No body set yet.";

$pageid = $entry->linkToPage;
$eventData = $factory->getApi()->getCalendarManager()->getEventPartitipatedData($pageid);
if($eventData) {
    $title = $eventData->title;
    $title2 = $eventData->title2;
    $heading = $eventData->heading;
    $text = $eventData->body;
}

$content = "";
setlocale(LC_TIME, 'no_NO');
ob_start();

$signature = false;
if($entry->eventHelder) {
    foreach ($diplomaPeriod->signatures as $usersignature) {
        if ($usersignature->userid == $entry->eventHelder) {
            $signature = $usersignature->signature;
        }
    }
    
    $eventHelder = $factory->getApi()->getUserManager()->getUserById($entry->eventHelder);
}

$monthText = strftime('%B', strtotime('2012-'.$month.'-22 11:21:53'));
$monthText = $factory->__w($monthText);
$content .= "<head>
    <style>
        @page {
            color:#FFFFFF;
            background: url('/diplomapictures/".$diplomaPeriod->id.".jpg') 50% 0 no-repeat;
        }
        .page {
            text-align:center;
            text-alignment:center;
            color:#FFFFFF;
            height:220mm;
            width:210mm;
        }
        .title { text-align:center; font-weight:bold; width: 100%; font-size: 40px; margin-bottom: 5px; }
        .title2 { text-align:center; font-weight:bold; width: 100%; font-size: 25px; margin-bottom: 60px; }
        .heading { margin-top: 30px; }
        .body { margin-top: 20px;  }
        .name { font-weight: bold; font-size: 20px; text-transform:uppercase; }
        .company { font-weight: bold; font-size: 20px; text-transform:uppercase; }
        .footer { width: 100%; color:#FFFFFF; }
        .page_end {  page-break-after:always; }
        .signature_name { font-size: 10px; }
        .bottom_text { margin-top: 50px; }
        </style>
        </head>";

foreach($attendees as $attendee) {
    $user = $factory->getApi()->getUserManager()->getUserById($attendee);
    
    
    if(in_array($user->id, $entry->dropDiploma)) {
        continue;
    }
    
    $name = $user->fullName;
    /* @var $company core_usermanager_data_Group */
    $company = $grouped[$user->groups[0]];
    $logo = $company->imageId;
    $companyName = $company->groupName;
    ?>
    <div class="page">
        <br><br>
        <br><br>
        <br><br>
        <br><br>
        <br><br>
        <br><br>
        <br><br>
        <div class="title"><? echo parsed_text($title); ?></div>
        <div class="title2"><? echo parsed_text($title2); ?></div>
        <div class="name"><? echo $name; ?></div>
        <div class="company"><? echo $user->company->name; ?></div>
        <div class="heading"><? echo parsed_text($heading);?></div>
        <div class="body"><? echo parsed_text($text);?></div>
        <div class="bottom_text"><? echo $location;?>, den <? echo $entry->day; ?>. <? echo $monthText; ?> <? echo $year; ?>.</div>
    </div>
    <div class="footer">
        <div style="float:left; width:40mm;">
            <? if($signature) { ?>
                <img alt="" src="<?echo $signature; ?>">
            <? } else { ?>
                 No signature set yet.
            <? } ?>
            <? if($signature) { ?>
                <div class="signature_name">
                    <? echo $eventHelder->fullName . "<br>" . $eventHelder->companyName; ?>
                </div>
            <? } ?>
        </div>
            <div style="float:right; width:60mm;text-align:right;"><img src="/displayImage.php?id=<? echo $logo; ?>&height=40"></div>
    </div>
    <div class="page_end">
    </div>
    <?
}
$content .= ob_get_contents();
ob_end_clean();

$mpdf=new mPDF();
$mpdf->WriteHTML($content);
$mpdf->Output("document".uniqid().".pdf",'I'); 
//echo $content;

function parsed_text($text) {
    global $title, $day, $date, $name, $year, $month;
    
    $text = str_replace("%title%", $title, $text);
    $text = str_replace("%year%", $year, $text);
    $text = str_replace("%month%", $month, $text);
    $text = str_replace("%day%", $day, $text);
    $text = str_replace("%date%", $date, $text);
    $text = str_replace("%name%", $name, $text);
    
    
    $text = htmlentities($text);
    return $text;
}

?>

