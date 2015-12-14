<?php
chdir("../");
include '../loader.php';
include 'excel.php';

class GenerateReport {

    var $factory;

    function __construct() {
        $this->factory = IocContainer::getFactorySingelton();
    }
    
    private function getGrouped($results) {
        $grouped = [];
        foreach ($results as $result) {
            $grouped[$result->locationId][] = $result;
        }
        
        return $grouped;
        
    }
    
    private function getGroups($grouped) {
        $groups = [];
        $firstEntry = array_values($grouped)[0];
        foreach ($firstEntry as $groupedEntry) {
            $groups[] = $groupedEntry->group;
        }
        
        return $groups;
    }
    
    private function getHeaderRow($results) {
        $grouped = $this->getGrouped($results);
        $groups = $this->getGroups($grouped);
        
        $line = array();
        $line[] = "Location";
        foreach ($groups as $group) {
            $line[]= $group->groupName;
        }
        
        return $line;
    }
    
    private function getCount($entries, $group, $watinglist=false) {
        foreach ($entries as $entry) {
            if ($entry->group->id == $group->id) {
                return $watinglist ? $entry->waitingList : $entry->signedOn;
            }
        }

        return 0;
    }

    private function getLocation($locations, $locationId) {
        foreach ($locations as $location) {
            if ($location->id == $locationId) {
                return $location;
            }
        }

        return null;
    }

    public function generateCalendarBookedReport($start, $end) {
        $rows = array();
        $results = $this->factory->getApi()->getCalendarManager()->getDetailedStatisticGroupedByLocations($start, $end);
        $locations = $this->factory->getApi()->getCalendarManager()->getAllLocations();

        $grouped = $this->getGrouped($results);
        $groups = $this->getGroups($grouped);
        $rows[] = $this->getHeaderRow($results);
        
        
        foreach ($grouped as $locationId => $entryResults) {
            $newLine = [];
            $location = $this->getLocation($locations, $locationId);
            $newLine[] = str_replace(",", " - ", $location->location);

            foreach ($groups as $group) {
                $newLine[] = $this->getCount($entryResults, $group);
            }

            $rows[] = $newLine;
        }


        $rows[] = [''];
        $rows[] = [''];
        $rows[] = ['Waitinglist'];
        
        foreach ($grouped as $locationId => $entryResults) {
            $newLine = [];
            $location = $this->getLocation($locations, $locationId);
            $newLine[] = str_replace(",", " - ", $location->location);
            
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

$generator = new GenerateReport();
$generator->generateCalendarBookedReport($_GET['from'], $_GET['to']);
?>

