<?php

include '../loader.php';
include 'excel.php';

if ($_GET['type'] == "calendarbookedusers") {
    $generator = new GenerateReport();
    $generator->generateCalendarBookedReport($_GET['group']);
}

class GenerateReport {

    var $factory;

    function __construct() {
        $this->factory = IocContainer::getFactorySingelton();
    }

    public function generateCalendarBookedReport($group) {
        $entry = $this->factory->getApi()->getCalendarManager()->getEntry($_GET['entryid']);
        $attendees = $entry->attendees;
        $rows = array();
        $line = array();
        $line["Navn"] = "";
        $line["E-post"] = "";
        $line["Tlf nr"] = "";
        $line["Firmanavn"] = "";
        $line["Org.nr"] = "";

        $rows[] = $line;
        $rows[] = array("" => "Påmeldte");
        foreach ($attendees as $attandee) {
            $user = $this->factory->getApi()->getUserManager()->getUserById($attandee);
            if (is_array($user->groups)) {
                if (in_array($group, $user->groups)) {
                    $rows[] = $this->createExcelRow($user);
                }
            }
        }
        $rows[] = array("" => "");
        $rows[] = array("" => "Venteliste");
        
        foreach ($entry->waitingList as $attandee) {
            $user = $this->factory->getApi()->getUserManager()->getUserById($attandee);
            if (is_array($user->groups)) {
                $line = array();
                if (in_array($group, $user->groups)) {
                    $rows[] = $this->createExcelRow($user);
                }
            }
        }
        $rows = $this->convertToExcelCharSet($rows);
        $this->printExcelHeader($entry->title. "_".date("Y-m-d").".xls");
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

    public function createExcelRow($user) {
        $line = array();
        $line[] = $user->fullName;
        $line[] = $user->emailAddress;
        $line[] = $user->cellPhone;
        $line[] = $user->company->name;
        $line[] = $user->company->vatNumber;
        return $line;
    }

}

?>
