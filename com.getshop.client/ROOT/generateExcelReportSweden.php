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
        $this->printSingleGroup($group);
    }
    
    private function getHeadingRow($entry, $groupName) {
        $heading = array();
        $heading["Kurs"] = $entry->title;
        $heading["Lokasjon"] = $entry->location;
        $heading["Dato"] = $entry->day.".".$entry->month.".".$entry->year;
        $heading["Gruppe"] = $groupName;
        $heading["Antall dager"] = sizeof($entry->otherDays)+1;
        return $heading;
    }

    private function getStackedGroups() {
        $allGroups = $this->factory->getApi()->getUserManager()->getAllGroups();
        $stackedGroups = array();
        foreach($allGroups as $groupobj) {
            $stackedGroups[$groupobj->id] = $groupobj;
        }
        
        return $stackedGroups;
    }
    
    private function printSingleGroup($group) {
        $entry = $this->factory->getApi()->getCalendarManager()->getEntry($_GET['entryid']);
        $attendees = $entry->attendees;
        $rows = array();
        
        $stackedGroups = $this->getStackedGroups();
        $groupName = isset($stackedGroups[$group]) ? $stackedGroups[$group]->groupName : "alle";
        
        $rows[] = $this->getHeadingRow($entry, $groupName);
        $rows[] = $this->getEmptyLine();
        $rows[] = $this->getUserHeading();
        
        foreach ($attendees as $attandee) {
            $user = $this->factory->getApi()->getUserManager()->getUserById($attandee);
            if (is_array($user->groups) || $group == "all") {
                if ((count($user->groups) && in_array($group, $user->groups)) || $group == "all") {
                    $rows[] = $this->createExcelRow($user, $entry, $group == "all", $stackedGroups);
                }
            }
        }
        $rows[] = array("" => "");
        $rows[] = array("" => "Venteliste");
        
        foreach ($entry->waitingList as $attandee) {
            $user = $this->factory->getApi()->getUserManager()->getUserById($attandee);
            if (is_array($user->groups) || $group == "all") {
                $line = array();
                if (in_array($group, $user->groups) || $group == "all") {
                    $rows[] = $this->createExcelRow($user, $entry, $group == "all", $stackedGroups);
                }
            }
        }
        
        $rows = $this->convertToExcelCharSet($rows);
        
//        echo "<pre>";
//        print_r($rows);
//        echo "</pre>";
        $this->printExcelHeader($entry->title." ".$entry->year."-".sprintf("%02d", $entry->month)."-".sprintf("%02d", $entry->day).".xls");
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
    
    public function createExcelRow($user, $entry, $all=false, $stackedGroups) {
        $line = array();
        $line[] = $user->referenceKey;
        

//        $line[] = $user->emailAddress;
//        $line[] = $user->cellPhone;
        
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
        $line[] = $user->fullName;
        $line[] = $entry->title;
        $line[] = $entry->location;
        $line[] = $user->emailAddress;
        
        if (count($user->comments)) {
            $commentString = "";
            foreach ($user->comments as $comment) {
                if ($comment->appId == "798538dc-f9d1-417c-86a9-a142c6b825c5")
                    $commentString .= $comment->comment." | ";
            }
            
            $line[] = $commentString;
        } else {
            $line[] = "";
        }
        
        
        if ($all) {
            $line[] = @$stackedGroups[@$user->groups[0]]->groupName;
        }
        
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

    public function getEmptyLine() {
        $line = array();
        $line["Navn"] = "";
        $line["E-post"] = "";
        $line["Tlf nr"] = "";
        $line["Firmanavn"] = "";
        $line["Org.nr"] = "";
        $line["Kommentar"] = "";
        return $line;
    }

    public function getUserHeading() {
        return ["Group reference id", "Address", "Name", "Event name", "Ort", "Email", "Comments:"];
    }

}

?>
