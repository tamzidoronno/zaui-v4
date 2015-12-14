<?php
//header("Pragma: no-cache");
//header('Expires: 0');
//header('Cache-Control: must-revalidate, post-check=0, pre-check=0');
//header('Content-Description: File Transfer');
//header('Content-Type: text/csv');
//header("Content-Disposition: attachment; filename=file.csv");
//header('Content-Transfer-Encoding: binary'); 
//
//echo chr(0xEF) . chr(0xBB) . chr(0xBF);
//
//$start = urldecode($_GET['from']);
//$end = urldecode($_GET['to']);
//
//chdir("../");
//include '../loader.php';
//$factory = IocContainer::getFactorySingelton(true, false);
//
//$results = $factory->getApi()->getCalendarManager()->getDetailedStatistic($start, $end);
//$grouped = [];
//foreach ($results as $result) {
//    $grouped[$result->entryId][] = $result;
//}
//
//$groups = [];
//$firstEntry = array_values($grouped)[0];
//foreach ($firstEntry as $groupedEntry) {
//    $groups[] = $groupedEntry->group;
//}
//
//// Title normal list
//echo "Entry, date";
//foreach ($groups as $group) {
//    echo ",".$group->groupName;
//}
//echo "\n";
//
//function getCount($entries, $group, $watinglist=false) {
//    foreach ($entries as $entry) {
//        if ($entry->group->id == $group->id) {
//            return $watinglist ? $entry->waitingList : $entry->signedOn;
//        }
//    }
//    
//    return 0;
//}
//
//foreach ($grouped as $entryId => $entryResults) {
//    $entry = $factory->getApi()->getCalendarManager()->getEntry($entryId);
//    echo $entry->title.",".$entry->day."/".$entry->month."-".$entry->year;
//    
//    foreach ($groups as $group) {
//        echo ",".getCount($entryResults, $group);
//    }
//    
//    echo "\n";
//}
//
//
//echo "\n";
//echo "\n ";
//echo "Waitinglist";
//echo "\n";
//
//foreach ($grouped as $entryId => $entryResults) {
//    $entry = $factory->getApi()->getCalendarManager()->getEntry($entryId);
//    echo $entry->title.",".$entry->day."/".$entry->month."-".$entry->year;
//    
//    foreach ($groups as $group) {
//        echo ",".getCount($entryResults, $group, true);
//    }
//    
//    echo "\n";
//}

chdir("../");
include '../loader.php';
include 'excel.php';

class GenerateReport {

    var $factory;

    function __construct() {
        $this->factory = IocContainer::getFactorySingelton();
    }
    
    private function getCount($entries, $group, $watinglist=false) {
        foreach ($entries as $entry) {
            if ($entry->group->id == $group->id) {
                return $watinglist ? $entry->waitingList : $entry->signedOn;
            }
        }

        return 0;
    }

    public function generateCalendarBookedReport($start, $end) {
        $rows = array();
        $results = $this->factory->getApi()->getCalendarManager()->getDetailedStatistic($start, $end);
        
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
        $headerLine = ['Entry', 'Date'];
        foreach ($groups as $group) {
            $headerLine[] = $group->groupName;
        }
        
        $rows[] = $headerLine;


        foreach ($grouped as $entryId => $entryResults) {
            $entry = $this->factory->getApi()->getCalendarManager()->getEntry($entryId);
            $newLine = [$entry->title,$entry->day."/".$entry->month."-".$entry->year];
            
            foreach ($groups as $group) {
                $newLine[] = $this->getCount($entryResults, $group);
            }

            $rows[] = $newLine;
        }
        
        $rows[] = [''];
        $rows[] = [''];
        $rows[] = ["WaitingLinst"];
        foreach ($grouped as $entryId => $entryResults) {
            $entry = $this->factory->getApi()->getCalendarManager()->getEntry($entryId);
            $newLine = [$entry->title,$entry->day."/".$entry->month."-".$entry->year];
            
            foreach ($groups as $group) {
                $newLine[] = $this->getCount($entryResults, $group, true);
            }

            $rows[] = $newLine;
        }

        $rows = $this->convertToExcelCharSet($rows);
        $this->printExcelHeader("statistic_".date("Y-m-d").".xls");
        $export_file = "xlsfile://tmp/example.xls";
        $fp = fopen($export_file, "wb");
        if (!is_resource($fp)) {
            die("Cannot open $export_file");
        }

        fwrite($fp, serialize($rows));
        fclose($fp);
    }
    
    function convertToExcelCharSet($array) {
        foreach($array as $rowid => $row) {
            foreach($row as $index => $cell) {
                $array[$rowid][$index] = mb_convert_encoding($cell, "ISO-8859-1", "UTF-8");
            }
        }
        return $array;
    }

    function printExcelHeader($name) {
        header("Content-Type:   application/vnd.ms-excel; charset=ISO-8859-1");
        header("Content-Disposition: attachment; filename=" . basename($name));  //File name extension was wrong
        header("Expires: 0");
        header("Cache-Control: must-revalidate, post-check=0, pre-check=0");
        header("Cache-Control: private", false);
    }
}

$generator = new GenerateReport();
$generator->generateCalendarBookedReport($_GET['from'], $_GET['to']);
?>