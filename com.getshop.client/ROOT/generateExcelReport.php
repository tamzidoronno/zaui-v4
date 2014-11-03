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
        
        $allGroups = $this->factory->getApi()->getUserManager()->getAllGroups();
        $stackedGroups = array();
        foreach($allGroups as $groupobj) {
            $stackedGroups[$groupobj->id] = $groupobj;
        }
        
        $heading = array();
        $heading["Kurs type"] = $entry->title;
        $heading["Lokasjon"] = $entry->location;
        $heading["Dato"] = $entry->day.".".$entry->month.".".$entry->year;
        $heading["Gruppe"] = $stackedGroups[$group]->groupName;
        $heading["Antall dager"] = sizeof($entry->otherDays)+1;
        $rows[] = $heading;
        
        
        $line = array();
        $line["Navn"] = "";
        $line["E-post"] = "";
        $line["Tlf nr"] = "";
        $line["Firmanavn"] = "";
        $line["Org.nr"] = "";
        $line["Kommentar"] = "";

        $rows[] = $line;
        $rows[] = array("" => "Påmeldte");
        foreach ($attendees as $attandee) {
            $user = $this->factory->getApi()->getUserManager()->getUserById($attandee);
            if (is_array($user->groups)) {
                if (in_array($group, $user->groups)) {
                    $rows[] = $this->createExcelRow($user, $entry);
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
                    $rows[] = $this->createExcelRow($user, $entry);
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
    
    public function createExcelRow($user, $entry) {
        $line = array();
        $line[] = $user->fullName;
        $line[] = $user->emailAddress;
        $line[] = $user->cellPhone;
        $companyInformation = "";
        if (isset($user->company->name)) {
            $companyInformation .= $user->company->name;
        }
        
        if (isset($user->company->streetAddress)) {
            $companyInformation .= "\n".$user->company->streetAddress;
        }
        
        if (isset($user->company->streetAddress)) {
            $companyInformation .= "\n" .$user->company->postnumber;
        }
        
        if (isset($user->company->city)) {
            $companyInformation .= " - ".$user->company->city;
        }
        
        $line[] = $companyInformation;
        $line[] = $user->company->vatNumber;
        $line[] = $this->getComments($user, $entry);
        return $line;
    }

    public function getComments($user, $entry) {
        $commentText = "";
        
        if (count($user->comments)) {
            $i = 0;
            $counter = 0;
            foreach ($user->comments as $comment) {
                $counter++;
            }
            foreach ($user->comments as $comment) {
                $i++;
                if ($comment->extraInformation === $entry->entryId) {
                    $commentText .= preg_replace('#<br\s*/?>#i', "", $comment->comment);
                    if ($i < $counter) {
                        $commentText .= "\n----------------------\n";
                    }
                }
            }
        }
        
        return $commentText;
    }

}

?>
