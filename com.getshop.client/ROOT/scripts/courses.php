<?php
header("Content-type: text/csv");
header("Content-Disposition: attachment; filename=file.csv");
header("Pragma: no-cache");
header("Expires: 0");

chdir("../");
include '../loader.php';

$year = (int)date('Y');
$month = (int)date('m');

$factory = IocContainer::getFactorySingelton();
$calenderManager = $factory->getApi()->getCalendarManager();
$months = $calenderManager->getMonthsAfter($year, $month);

foreach ($months as $month) {
    foreach($month->days as $day) {
        foreach ($day->entries as $entry) {
            echo $entry->title.";".
                    $entry->starttime.";".
                    $entry->stoptime.";".
                    $entry->day.";".
                    $entry->month.";".
                    $entry->year.";".
                    $entry->location.";".
                    $entry->locationExtended.";".
                    $entry->maxAttendees.";".
                    count($entry->attendees).";".
                    "http://www.promeister.academy/index.php?page=02474f5b-1d72-409f-9e2d-c3cf0182baec&entry=".$entry->entryId."\n";    
        }
    }
}

// Title
// StartTime
// EndTime
// Day
// Month
// Year
// Location
// Location extended description
// Head count for the event (maximum)
// People already signed up
// Link for signup
?>
