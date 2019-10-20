<?php
namespace ns_7db21d0e_6636_4dd3_a767_48b06932416c;

class PmsRestrictions extends \WebshopApplication implements \Application {
    var $errorMessage = "";
    var $wubookWarning = "";
    public function getDescription() {
        
    }

    public function getName() {
        return "PmsRestrictions";
    }

    public function closeForPeriode() {
        $config = $this->getApi()->getPmsManager()->getConfiguration($this->getSelectedMultilevelDomainName());
        $start = $this->convertToJavaDate(strtotime($_POST['data']['start'] . " " . $config->defaultStart));
        $end = $this->convertToJavaDate(strtotime($_POST['data']['end'] . " "  . $config->defaultEnd));
        
        if(strtotime($start) > strtotime($end)) {
            $this->errorMessage = "If you want to close for one day, please specify start as check in and end as check out.";
            return;
        }
        
        $data = new \core_pmsmanager_TimeRepeaterData();
        $data->firstEvent = new \core_pmsmanager_TimeRepeaterDateRange();
        $data->firstEvent->start = $start;
        $data->firstEvent->end = $end;
        $data->timePeriodeType = 0;
        $data->endingAt = $data->firstEvent->end;
        
        $config = $this->getApi()->getPmsManager()->getConfiguration($this->getSelectedMultilevelDomainName());
        $config->closedOfPeriode[] = $data;
        $this->getApi()->getPmsManager()->saveConfiguration($this->getSelectedMultilevelDomainName(), $config);
    }
    
    public function removeClosedOfUntil() {
        $config = $this->getApi()->getPmsManager()->getConfiguration($this->getSelectedMultilevelDomainName());
        $newArray = array();
        foreach($config->closedOfPeriode as $periode) {
            if($periode->repeaterId == $_POST['data']['id']) {
                continue;
            }
            $newArray[] = $periode;
        }
        $config->closedOfPeriode = $newArray;
        $this->getApi()->getPmsManager()->saveConfiguration($this->getSelectedMultilevelDomainName(), $config);
    }
    
    public function createRestriction() {
        $data = new \core_pmsmanager_TimeRepeaterData();
        $data->repeatMonday = $_POST['data']['repeatMonday'] == "true";
        $data->repeatTuesday = $_POST['data']['repeatTuesday'] == "true";
        $data->repeatWednesday = $_POST['data']['repeatWednesday'] == "true";
        $data->repeatThursday = $_POST['data']['repeatThursday'] == "true";
        $data->repeatFriday = $_POST['data']['repeatFriday'] == "true";
        $data->repeatSaturday = $_POST['data']['repeatSaturday'] == "true";
        $data->repeatSunday = $_POST['data']['repeatSunday'] == "true";
        $data->endingAt = $this->convertToJavaDate(strtotime($_POST['data']['endingAt']));
        $data->timePeriodeType = $_POST['data']['timeperiodetype'];
        $data->timePeriodeTypeAttribute = $_POST['data']['timePeriodeTypeAttribute'];
        if(isset($_POST['data']['repeatmonthtype'])) {
            $data->data->repeatAtDayOfWeek = $_POST['data']['repeatmonthtype'] == "dayofweek";
        }
        $data->repeatPeride = $_POST['data']['repeat_periode'];
        
        $data->firstEvent = new \core_pmsmanager_TimeRepeaterDateRange();
        $data->firstEvent->start = $this->convertToJavaDate(strtotime($_POST['data']['eventStartsAt'] . " " . $_POST['data']['starttime']));
        if(isset($_POST['data']['eventEndsAt'])) {
            $data->firstEvent->end = $this->convertToJavaDate(strtotime($_POST['data']['eventEndsAt'] . " " . $_POST['data']['endtime']));
        } else {
            $data->firstEvent->end = $this->convertToJavaDate(strtotime($_POST['data']['eventStartsAt'] . " " . $_POST['data']['endtime']));
        }
        
        if($data->timePeriodeType == 7 || $data->timePeriodeType == 8) {
            $data->timePeriodeTypeAttribute = 1;
        }
        
        if($data->repeatPeride == "0") {
            $data->repeatEachTime = 1;
        }
        
        $typeid = null;
        if(isset($_POST['data']['typeid'])) {
            $typeid = $_POST['data']['typeid'];
        }

        $this->getApi()->getBookingEngine()->saveOpeningHours($this->getSelectedMultilevelDomainName(), $data, $typeid);
        $this->getApi()->getWubookManager()->doUpdateMinStay($this->getSelectedMultilevelDomainName());
    }
    
    public function render() {
        echo "<br>";
        echo "<div class='section'>";
        $this->includefile("createrestriction");
        $this->printRestrictions();
        echo "</div>";
        $this->includefile("wubookrestrictions");
    }
    
    public function removeRestriction() {
        $this->getApi()->getWubookManager()->deleteRestriction($this->getSelectedMultilevelDomainName(), $_POST['data']['id']);
    }

    public function addWubookRestriction() {
        $startTime = $_POST['data']['start'];
        $endTime = $_POST['data']['end'];
        $config = $this->getApi()->getPmsManager()->getConfiguration($this->getSelectedMultilevelDomainName());
        
        if(strtotime($startTime) >= strtotime($endTime)) {
            $this->wubookWarning = "If you want to close for one day, please specify start as check in and end as check out.";
            return;
        }
        
        if(!$endTime || !$startTime) {
            echo "Please select a start and end date";
        } else {
            $restriction = new \core_wubook_WubookAvailabilityRestrictions();
            $restriction->start = $this->convertToJavaDate(strtotime($startTime  . " " . $config->defaultStart));
            $restriction->end = $this->convertToJavaDate(strtotime($endTime . " " . $config->defaultEnd));
            
            foreach($_POST['data'] as $key => $event) {
                if(stristr($key, "type_") && $event == "true") {
                    $typeId = str_replace("type_", "", $key);
                    $restriction->types[] = $typeId;
                }
            }
            $this->getApi()->getWubookManager()->addRestriction($this->getSelectedMultilevelDomainName(), $restriction);
        }        
    }
    
    
    public function getRepeatingSummary($data) {
        $text = "";
        $timePeriodeType[1] = "Closed";
        $timePeriodeType[2] = "Min stay";
        $timePeriodeType[3] = "Max stay";
        $timePeriodeType[4] = "Deny same day booking";
        $timePeriodeType[6] = "Force confirmation same day booking";
        
        $prefix = "Repeats";
        $type = $data->timePeriodeType;
        if($type == 1) { $prefix = "Closed"; }
        if($type == 2) { $prefix = "Minimum stay" . "(" . $data->timePeriodeTypeAttribute ." days)"; }
        if($type == 3) { $prefix = "Maximum stay" . "(" . $data->timePeriodeTypeAttribute ." days)"; }
        if($type == 4) { $prefix = "Deny same day booking"; }
        if($type == 6) { $prefix = "Force confirmation same day booking"; }
        if($type == 7) { $prefix = "No check in"; }
        if($type == 8) { $prefix = "No check out"; }
        
        if($data->repeatPeride == "0") {
            $text = $prefix . " " . " daily";
        }
        
        
        if($data->repeatPeride == "1") {
            $text = $prefix . " " . "every {periode} week" . " (";
            $text = str_replace("{periode}", $data->repeatEachTime, $text);
            if($data->repeatMonday) {
                $text .= strtolower("Mon") . ", ";
            }
            if($data->repeatTuesday) {
                $text .= strtolower("Tue") . ", ";
            }
            if($data->repeatWednesday) {
                $text .= strtolower("Wed") . ", ";
            }
            if($data->repeatThursday) {
                $text .= strtolower("Thu") . ", ";
            }
            if($data->repeatFriday) {
                $text .= strtolower("Fri") . ", ";
            }
            if($data->repeatSaturday) {
                $text .= strtolower("Sat") . ", ";
            }
            if($data->repeatSunday) {
                $text .= strtolower("Sun") . ", ";
            }
            $text = substr($text, 0, -2) . ")";
        }
        if($data->repeatPeride == "2") {
            if($data->repeatAtDayOfWeek) {
                $text = $prefix . " " . "montly same day in week";
            } else {
                $text = $prefix . " " . "montly same date in month";
            }
        }

        $text .= " " . " between " .date("d.m.Y", strtotime($data->firstEvent->start)) . " and " . date("d.m.Y", strtotime($data->endingAt));
        if($type == 4 || $type == 6) {
            $text .= ", time between: " . date("H:i", strtotime($data->firstEvent->start)) . " - " . date("H:i", strtotime($data->firstEvent->end));
        }
        return $text;
    }
    
    public function deleteRestriction() {
        $this->getApi()->getBookingEngine()->deleteOpeningHours($this->getSelectedMultilevelDomainName(), $_POST['data']['id']);
        $this->getApi()->getWubookManager()->updateMinStay($this->getSelectedMultilevelDomainName());
    }
    
    public function printRestrictions() {
        /* @var $this \ns_7db21d0e_6636_4dd3_a767_48b06932416c\PmsRestrictions */
        $restrictions = (array)$this->getApi()->getBookingEngine()->getOpeningHoursWithType($this->getSelectedMultilevelDomainName(), null, null);
        
        $text = array();
        foreach($restrictions as $res) {
            $obj = new \stdClass();
            $obj->text = "<i class='fa fa-trash-o' style='cursor:pointer;' gstype='clicksubmit' gsname='id' method='deleteRestriction' gsvalue='".$res->repeaterId."'></i> " . $this->getRepeatingSummary($res);
            $text[] = $obj;
        }
        
        $attributes = array(
            array('Restriction', "Restriction","text")
        );

        if(!$text) {
            $obj = new \stdClass();
            $obj->text = "No restrictions added yet.";
            $text[] = $obj;
        }
        
        $table = new \GetShopModuleTable($this, 'PmsReport', 'loadIncomeReportCell', null, $attributes);
        $table->setData($text);
        $table->render();
    }

}
?>
