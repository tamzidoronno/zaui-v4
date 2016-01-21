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
        $entries = $this->factory->getApi()->getCalendarManager()->getAllEventsConnectedToPageIncludedPast($_GET['pageId']);

        $rows[] = ["Name", "Email", "Phone number", "Address", "Vat number", "Comments", "Event name", "Group reference id", "Fakturastatus", "Date" ];

        foreach ($entries as $entry) {
            $entry = $this->factory->getApi()->getCalendarManager()->getEntry($entry->entryId);
            $attendees = $entry->attendees;
            

            $allGroups = $this->factory->getApi()->getUserManager()->getAllGroups();
            
            $stackedGroups = array();
            foreach($allGroups as $groupobj) {
                $stackedGroups[$groupobj->id] = $groupobj;
            }

            foreach ($attendees as $attandee) {
                $user = $this->factory->getApi()->getUserManager()->getUserById($attandee);
                if (is_array($user->groups)) {
                    if (in_array($group, $user->groups)) {
                        $rows[] = $this->createExcelRow($user, $entry);
                    }
                }
            }
//            $rows[] = array("" => "");
//            $rows[] = array("" => "Venteliste");
//
//            foreach ($entry->waitingList as $attandee) {
//                $user = $this->factory->getApi()->getUserManager()->getUserById($attandee);
//                if (is_array(@$user->groups)) {
//                    $line = array();
//                    if (in_array($group, $user->groups)) {
//                        $rows[] = $this->createExcelRow($user, $entry);
//                    }
//                }
//            }
        }

//        echo "<pre>";
//        print_r($rows);
//        die("TEST");
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
        $line[] = $entry->title;
        $line[] = $user->referenceKey;
        
        if (strtotime($entry->day."-".$entry->month."-".$entry->year) < time()) {
            $line[] = @$this->getParticipateData($entry->participateData->{$user->id}); 
        } else {
            $line[] = "";
        }
        
        $line[] = $entry->day."/".$entry->month."-".$entry->year;
        
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
    
    public function getParticipateData($data) {
        if ($data == "participated") 
            return "Deltatt";
        
        if ($data == "notvalid_cancel") 
            return "Faktureres 50%";
        
        if ($data == "valid_cancel") 
            return "Skal ikke faktureres";
        
        if ($data == "valid_free") 
            return "Deltatt, ingen fakturering";
        
        return $data;
    }

}

?>
