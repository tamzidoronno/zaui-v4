<?php
include '../loader.php';
$factory = IocContainer::getFactorySingelton();

function getLogo(   ) {
    $api = IocContainer::getFactorySingelton()->getApi();
    return $api->getLogoManager()->getLogo()->LogoId;
}

function getEntries(Factory $factory) {
    $entries = $factory->getApi()->getCalendarManager()->getEntries((int)date('Y'), (int)date('m'), (int)date('d'));
    
    $jsonarray = array();
    foreach ($entries as $entry) {
        /* @var $entry core_calendarmanager_data_Entry */
        $json = array();
        $json["description"] = $entry->description;
        $json["starttime"] = $entry->starttime;
        $json["location"] = $entry->location;
        $json["stoptime"] = $entry->stoptime;
        $json["entryId"] = $entry->entryId;
        $json["maxAttendees"] = $entry->maxAttendees;
        $json["attendees"] = $entry->attendees;
        $json["userId"] = $entry->userId;
        $json["title"] = $entry->title;
        $json["day"] = $entry->day;
        $json["month"] = $entry->month;
        $json["year"] = $entry->year;
        $jsonarray[] = $json;
    }
    return json_encode($jsonarray);
}

if (isset($_GET['register']) && $_GET['register'] == true) {
    $application = $factory->getApplicationPool()->getApplicationByName('Booking');
    $application->registerEvent($_POST);
    return;
}

$logo = getLogo($factory);
?>

<script type="text/javascript">
Config = {
    logoid: '<?php echo $logo; ?>',
    entries: <?php echo getEntries($factory); ?>
}
</script>

<!DOCTYPE html> 
<html> 
    <head> 
        <meta charset="utf-8"> 
        <title><?php echo $factory->getStore()->configuration->shopName; ?></title> 
        <link rel="apple-touch-icon" href="/displayImage.php?id=<?php echo $logo; ?>&width=114&height=114"/>
        <script src="js/touch/sencha-touch-all.js" type="text/javascript"></script> 
        <link href="js/touch/resources/css/sencha-touch.css" rel="stylesheet" type="text/css" /> 
        <link href="js/touch/resources/css/addon.css" rel="stylesheet" type="text/css" /> 
        <script src="js/app/touch/booking/app.js" type="text/javascript"></script>
    </head> 
    <body></body> 
</html>