<?php
header("Pragma: no-cache");
header('Expires: 0');
header('Cache-Control: must-revalidate, post-check=0, pre-check=0');
header('Content-Description: File Transfer');
header('Content-Type: text/csv');
header("Content-Disposition: attachment; filename=file.csv");
header('Content-Transfer-Encoding: binary'); 

echo chr(0xEF) . chr(0xBB) . chr(0xBF);

$start = urldecode($_GET['from']);
$end = urldecode($_GET['to']);

chdir("../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton(true, false);

$results = $factory->getApi()->getCalendarManager()->getDetailedStatistic($start, $end);
$grouped = [];
foreach ($results as $result) {
    $grouped[$result->entryId][] = $result;
}

$groups = [];
$firstEntry = array_values($grouped)[0];
foreach ($firstEntry as $groupedEntry) {
    $groups[] = $groupedEntry->group;
}

// Title normal list
echo "Entry, date";
foreach ($groups as $group) {
    echo ",".$group->groupName;
}
echo "\n";

function getCount($entries, $group, $watinglist=false) {
    foreach ($entries as $entry) {
        if ($entry->group->id == $group->id) {
            return $watinglist ? $entry->waitingList : $entry->signedOn;
        }
    }
    
    return 0;
}

foreach ($grouped as $entryId => $entryResults) {
    $entry = $factory->getApi()->getCalendarManager()->getEntry($entryId);
    echo $entry->title.",".$entry->day."/".$entry->month."-".$entry->year;
    
    foreach ($groups as $group) {
        echo ",".getCount($entryResults, $group);
    }
    
    echo "\n";
}


echo "\n";
echo "\n ";
echo "Waitinglist";
echo "\n";

foreach ($grouped as $entryId => $entryResults) {
    $entry = $factory->getApi()->getCalendarManager()->getEntry($entryId);
    echo $entry->title.",".$entry->day."/".$entry->month."-".$entry->year;
    
    foreach ($groups as $group) {
        echo ",".getCount($entryResults, $group, true);
    }
    
    echo "\n";
}
?>