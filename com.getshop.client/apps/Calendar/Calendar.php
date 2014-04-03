<?php
namespace ns_6f3bc804_02a1_44b0_a17d_4277f0c6dee8;
use \MarketingApplication;
use \Application;


class Calendar extends MarketingApplication implements Application {
    /** @var core_calendarmanager_data_Entry */
    public $currentEntry;
    public $monthObjeBct;
    public $year;
    public $month; 
    public $day;
    private $activeFilters;
    
    public function getDescription() {
        return $this->__("Keep your customers up to date of what events you are hosting with this application");
    }
    
    public function getGroup($user) {
        $retGroups = "";
        if (isset($user->groups) && sizeof($user->groups) > 0) {
            $allGroups = $this->getApi()->getUserManager()->getAllGroups();
            foreach ($user->groups as $groupId) {
                foreach ($allGroups as $groupData) {
                    if ($groupData->id == $groupId) {
                        $retGroups[] = $groupData->groupName;
                    }
                }
            }
            return implode(",", $retGroups);
        }
     
        return $retGroups;
    }
    
    /**
     * 
     * @param \core_calendarmanager_data_Month $month
     * @param \core_calendarmanager_data_Entry $originalEntry
     * @return int
     */
    public function getDaysCount($month, $originalEntry) {
        return count($this->getDaysEntries($month, $originalEntry));
    }
    
    public function getDaysEntries($month, $originalEntry) {
        $ret = [];
        
        foreach ($month->days as $day) {
            $entries = $day->entries;

            foreach ($entries as $entry) {
                if ($originalEntry->entryId == $entry->entryId) {   
                    $ret[] = $entry;
                }
            }
        }
        
        return $ret;
    }

    public function getAvailablePositions() {
        return "middle";
    }
    
    public function getName() {
        return $this->__("Event calendar");
    }
    
    public function postProcess() {
        
    }
    
    public function getStarted() {
        echo $this->__f("Create this application and start showing events on given dates.");
    }    
    
    public function isEntryOutOfData($entry) {
        if (!is_object($entry)) {
            return true;
        }
        
        if ($entry->year < date('Y'))
            return true; 
        
        if ($entry->year == date('Y') && $entry->month < date('m'))
            return true;
        
        if ($entry->year == date('Y') && $entry->month == date('m') && $entry->day <= date('d'))
            return true;
        
        return false;
    }
    
    public function isCurrentEntryOutOfDate() {
        if ($this->year > date('Y'))
            return true; 
        
        if ($this->year == date('Y') && $this->month > date('m'))
            return true;
        
        if ($this->year == date('Y') && $this->month == date('m') && $this->day >= date('d'))
            return true;
        
        return false;
    }
    
    private function initializeDate() {
        if (!isset($_SESSION['calendar_year'])) {
            $_SESSION['calendar_year'] = $this->year = date('Y');
        }
            
        if (!isset($_SESSION['calendar_month'])) {
            $_SESSION['calendar_month'] = date('m');
        }
            
        if (!isset($_SESSION['calendar_day'])) {
            $_SESSION['calendar_day'] = date('d');
        }

        if (isset($_GET['year']))
            $_SESSION['calendar_year'] = $_GET['year'];
        
        if (isset($_GET['month']))
            $_SESSION['calendar_month'] = $_GET['month'];
        
        if (isset($_GET['day']))
            $_SESSION['calendar_day'] = $_GET['day'];
        
        $this->year = $_SESSION['calendar_year'];
        $this->month = $_SESSION['calendar_month'];
        $this->day = $_SESSION['calendar_day'];

        $this->year = (isset($_POST['data']['year'])) ? $_POST['data']['year']  : $this->year;
        $this->month = (isset($_POST['data']['month'])) ? $_POST['data']['month']  : $this->month;
        $this->day = (isset($_POST['data']['day'])) ? $_POST['data']['day']  : $this->day;
        
        $this->year = (int)$this->year;
        $this->month = (int)$this->month;
        $this->day = (int)$this->day;

    }
    
    public function preProcess() {
        $this->initializeDate();
    }
    
    public function initMonth() {
        $this->monthObject = $this->getApi()->getCalendarManager()->getMonth($this->year, $this->month, true);
        if (!\ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::isAdministrator()) {
            $this->removeUnconfirmedEntries();
        }
    }
    
    public function isShowTabsForViewMode() {
        $showTabsForViewMode = $this->getConfigurationSetting("showTabsForViewMode");
        if ($showTabsForViewMode && $showTabsForViewMode != "false") {
            return true;
        }
        return false;
    }
    
    
    private function removeUnconfirmedEntries() {
        foreach ($this->monthObject->days as $day) {
            /* @var $day core_calendarmanager_data_Day */
            if (isset($day->entries)) {
                foreach ($day->entries as $key => $entry) {
                    if ($entry->needConfirmation) {
                        unset($day->entries[$key]);
                    }
                }
            }
        }
    }
    
    public function getLocation($locationId) {
        $locations = $this->getApi()->getCalendarManager()->getAllLocations();

        foreach ($locations as $ilocation) {
            if ($ilocation->id == $locationId) {
                return $ilocation;
            }
        }
        
        return null;
    }
    
    private function saveLocationData() {
        $location = $this->getLocation($_POST['data']['locationId']);
        if (!$location) {
            $location = new \core_calendarmanager_data_Location();
        }
        
        $location->location = $_POST['data']['locationName'];
        $location->locationExtra = $_POST['data']['locationExtra'];
        
        if ($_POST['data']['commentText']) {
            $comment = new \core_usermanager_data_Comment();
            $comment->comment = nl2br($_POST['data']['commentText']);
            $location->comments[] = $comment;
        }
        
        $location = $this->getApi()->getCalendarManager()->saveLocation($location);
        $_POST['data']['locationId'] = $location->id;
    }
    
    public function saveLocation() {
        $this->saveLocationData();
        $this->includefile("editLocation");
    }
    
    public function showEditLocation() {
        $this->includefile("editLocation");
    }
    
    public function showLocationsEditor() {
        $this->includefile("locations");
    }
    
    public function deleteLocation() {
        $this->getApi()->getCalendarManager()->deleteLocation($_POST['data']['locationId']);
        $this->includefile("locations");
    }
    
    public function setFilter() {
        if (isset($_GET['filter'])) {
            $word = strtolower(base64_decode($_GET['filter']));
            $this->activeFilters = [];
            $this->activeFilters[] = ucwords($word);
            $this->getApi()->getCalendarManager()->applyFilter($this->activeFilters);
            $this->activeFilters = $this->getApi()->getCalendarManager()->getActiveFilters();
        }
    }
    
    public function render() {
        $this->setFilter();
        $this->initMonth();
        
        echo "<table><tr><td valign='top'>";
        $this->showFilter();
        echo "</td><td valign='top'>";
        $this->includefile('calendar');
        echo "</td></tr></table>";
    }
    
    public function renderList($includeCss=false) {
        if ($includeCss) {
            echo "<style>";
            echo '
                .listview_month_location { margin-bottom: 20px; margin-left: 10px; border: solid 1px #333; padding: 3px; margin-top: 5px; }
                .listview_month_location td { vertical-align: top; font-size: 15px; padding: 5px; }
                .listview_month_location th {  background-color: #CCC; padding: 5px; }
                .listview_month_location tr:nth-child(odd) td { background-color: #EEE; }
                ';
            echo "</style>";
        }
        $this->includefile('calendarlist');
    }
    
    public function showFilter() {
        $showFilter = $this->getConfigurationSetting("showfilter");
        if ($showFilter && $showFilter != "false") {
            $this->activeFilters = $this->getApi()->getCalendarManager()->getActiveFilters();
            $this->includefile("leftfilter");
        }
    }
    
    /**
     * @return core_calendarmanager_data_Day
     */
    public function getSelectedDay() {
        return $this->getDay($this->day);
    }
    
    private function loadData() {
        $this->initializeDate();
        if (isset($_POST['data']['entryid'])) {
            $this->currentEntry = $this->getApi()->getCalendarManager()->getEntry($_POST['data']['entryid']);
        }
    }
    
    public function showCalenderEvent() {
        $this->loadData();
        $this->includefile('addEvent');
    }
    
    private function populateEvent($entry) {
        $entry->description = $_POST['data']['eventdescription'];
        $entry->starttime = $_POST['data']['eventstart'];
        $entry->stoptime = $_POST['data']['eventstop'];
        $entry->maxAttendees = $_POST['data']['maxattendees'];
        $entry->locationId = $_POST['data']['locationId'];
        $entry->title = $_POST['data']['eventname'];
        $entry->color = $_POST['data']['color'];
        $entry->extraText = $_POST['data']['extraText'];
        $entry->linkToPage = $_POST['data']['linkToPage'];
        $entry->lockedForSignup = $_POST['data']['lockedForSignup'];
        
        return $entry;
    }
    
    public function registerEvent() {
        $day = $_POST['data']['day'];
        $month = $_POST['data']['month'];
        $year = $_POST['data']['year'];
        
        if ($_POST['data']['entryid']) {
            $entry = $this->getApi()->getCalendarManager()->getEntry($_POST['data']['entryid']);
        } else {
            $entry = $this->getApi()->getCalendarManager()->createEntry($year, $month, $day);
        }
        
        $entry = $this->populateEvent($entry);
        
        $entry->otherDays = array();
        if (isset($_POST['data']['extraDays'])) {
            foreach ($_POST['data']['extraDays'] as $postExtraDay) {
                $extraDay = $this->getApiObject()->core_calendarmanager_data_ExtraDay();
                $extraDay->day = $postExtraDay['day']; 
                $extraDay->month = $postExtraDay['month']; 
                $extraDay->year = $postExtraDay['year']; 
                $extraDay->starttime = $postExtraDay['starttime']; 
                $extraDay->stoptime = $postExtraDay['stoptime']; 
                $entry->otherDays[] = $extraDay;
           }
        }

        $this->getApi()->getCalendarManager()->saveEntry($entry);
    }
    
    public function deleteEntry() {
        $this->getApi()->getCalendarManager()->deleteEntry($_POST['data']['entryId']);
    }
    
    public function getNextLink() {
        $page = $this->getPage()->getId();
        $month = $this->month + 1;
        $year = $this->year;
        if ($month > 12) {
            $month = 1;
            $year = $this->year + 1;
        }
        return "?page=$page&year=$year&month=$month";
    }
    
    public function getPrevLink() {
        $page = $this->getPage()->getId();
        $month = $this->month - 1;
        $year = $this->year;
        if ($month < 1) {
            $month = 12;
            $year = $this->year - 1;
        }
        return "?page=$page&year=$year&month=$month";
    }

    public function getAttendees($entry) {
        if (count($entry->attendees) == 0) {
            return array();
        }
        
        if (!$this->hasReadAccess()) {
            return $entry->attendees;
        }
        return $this->getApi()->getUserManager()->getUserList($entry->attendees);
    }
    
    public function removeAttendees() {
        $this->getApi()->getCalendarManager()->removeUserFromEvent($_POST['data']['userid'], $_POST['data']['entryid']);
    }
    
    public function getDay($listDay) {
        foreach ($this->monthObject->days as $day) {
            /* @var $day core_calendarmanager_data_Day */
            if ($day->day == $listDay) {
                if (!isset($day->entries))
                    $day->entries = array();
                return $day;
            }
        }

        $day =  $this->getApiObject()->core_calendarmanager_data_Day();
        $day->day = $listDay;
        $day->entries = array();
        return $day;
    }
    
    public function showSendReminder() {
        $this->loadData();
        $this->includefile("sendreminder");
    }
    
    private function sendReminderEvent($byEmail, $bySms) {
        $text = $byEmail ? nl2br($_POST['data']['text']) : $_POST['data']['text'];
        $this->getApi()
                ->getCalendarManager()
                ->sendReminderToUser(
                        $byEmail, 
                        $bySms, 
                        $_POST['data']['users'], 
                        $text, 
                        $_POST['data']['subject'],
                        $_POST['data']['entryid']
                    );
    }
    
    public function sendReminderBySms() {
        $this->sendReminderEvent(false, true);
    }
    
    public function sendReminderByEmail() {
        $this->sendReminderEvent(true, false);
    }
    
    public function confirmEntry() {
        $entryId = $_POST['data']['entryid'];
        $this->getApi()->getCalendarManager()->confirmEntry($entryId);
    }
    
    public function showSettings() {
        $this->includeFile('config');
    }
    
    public function applyFilter() {
        $this->activeFilters = $this->getApi()->getCalendarManager()->getActiveFilters();
        $this->activeFilters[] = $_POST['data']['filter'];
        $this->getApi()->getCalendarManager()->applyFilter($this->activeFilters);
    }
    
    public function removeFilter() {
        $filters = array();
        
        $this->activeFilters = $this->getApi()->getCalendarManager()->getActiveFilters();
        foreach($this->activeFilters as $filter) {
            if ($filter == $_POST['data']['filter']) {
                continue;
            }
            
            $filters[] = $filter;
        }
        
        
        $this->getApi()->getCalendarManager()->applyFilter($filters);
    }
    
    public function isSelected($filter) {
        if ($this->activeFilters)
            return in_array($filter, $this->activeFilters);
        else
            return false;
    }
    
    public function getReminderHistory() {
        $this->loadData();
        $this->includefile("reminderhistory");
    }
    
    public function getBookingLink($entry) {
        $showBookingLink = $this->getConfigurationSetting("showBookingLink");
        
        if (!$showBookingLink) {
            return "";
        }
        
        $pageId = $this->getConfigurationSetting("linkToBookingPage");
        
        return "?page=".$pageId."&entry=".$entry->entryId;
    }
    
    public function getPageName($pageId) {
        $ids = array();
        $ids[] = $pageId;
        $restults = $this->getApi()->getListManager()->translateEntries($ids);
        
        if (!isset($restults->$pageId)) {
            return "";
        }
        
        return $restults->$pageId;
    }

    public function getWaitingList($entry) {
        if (count($entry->waitingList) == 0) {
            return array();
        }
        
        if (!$this->hasReadAccess()) {
            return $entry->waitingList;
        }
        
        return $this->getApi()->getUserManager()->getUserList($entry->waitingList);
    }
    
    public function transferUserFromWaitingList() {
        $entryId =  $_POST['data']['entryId'];
        $userId =  $_POST['data']['userId'];
        $this->getApi()->getCalendarManager()->transferFromWaitingList($entryId, $userId);
    }
    
    public function getHistory() {
        return $this->getApi()->getCalendarManager()->getHistory($this->currentEntry->entryId);
    }
    
    public function showTransferUser() {
        $this->loadData();
        $this->includefile("transferuserdialog");
    }
    
    public function transferUser() {
        $fromEventId = $_POST['data']['fromEntryId'];
        $toEventId = $_POST['data']['toEntryId'];
        $userId = $_POST['data']['userid'];
        $this->getApi()->getCalendarManager()->transferUser($fromEventId, $toEventId, $userId);
    }
    
    public function saveComment() {
        $comment = new \core_usermanager_data_Comment();
        $comment->comment = nl2br($_POST['data']['comment']);
        $comment->extraInformation = $_POST['data']['entryId'];
        $comment->appId = $this->getConfiguration()->id;
        
        $this->getApi()->getUserManager()->addComment($_POST['data']['userId'], $comment);
    }
    
    public function showEvent() {
        $this->includefile("showComments");
    }
    
    public function filterComments($user, $entryId) {
        $comments = array();
        
        if (!count($user->comments)) {
            return $comments;
        }
        
        foreach ($user->comments as $comment) {
            if ($comment->appId === $this->getConfiguration()->id && $comment->extraInformation == $entryId) {
                $comments[] = $comment;
            }
        }
        
        return $comments;
    }
    
    public function isActiveComment($user, $entryId) {
        $comments = $this->filterComments($user, $entryId);
        return count($comments);
    }
    
    public function deleteComment() {
        $commentId = $_POST['data']['commentId'];
        $userId = $_POST['data']['userId'];
        $this->getApi()->getUserManager()->removeComment($userId, $commentId);
    }
    
    /**
     * Function that is needed to sort the calender listview correctly.
     * @param type $a
     * @param type $b
     * @return type
     */
    static function usortCalendarListViewEntry($c, $d) {
        $a = $c[0]->day;
        $b = $d[0]->day;
        
        if ($a == $b) {
            return 0;
        }
        
        return ($b < $a) ? -1 : 1;
    }
    
    public function getListViewData() {
        $year = (int)date('Y');
        $month = (int)date('m');
        $months = $this->getApi()->getCalendarManager()->getMonthsAfter($year, $month);
        $this->locations = $this->getApi()->getCalendarManager()->getAllLocations();
        $retdata = [];
        foreach (array_reverse($months) as $month) {
            foreach ($this->locations as $location) {
                if (!isset($retdata[$month->year])) {
                    $retdata[$month->year] = [];
                }
                
                if (!isset($retdata[$month->year][$month->month])) {
                    $retdata[$month->year][$month->month] = [];
                }
                
                foreach ($month->days as $day) {
                    foreach ($day->entries as $entry) {
                        if ($entry->locationId == $location->id) {
                            $retdata[$month->year][$month->month][$location->id][] = $entry;
                        }
                    }
                }
            }
        }
        
        foreach ($retdata as $year => $data) {
            foreach ($data as $month => $monthData) {
                $res = usort($monthData, array("ns_6f3bc804_02a1_44b0_a17d_4277f0c6dee8\Calendar", "usortCalendarListViewEntry"));
                $data[$year][$month] = $monthData;
            }
            
            ksort($data);
            $retdata[$year] = $data;
        }
        
        return $retdata;
    }

    public function getCountOfFreePositions($entry) {
        $attendees = $this->getAttendees($entry);
        return $entry->maxAttendees - count($attendees);
    }
}



?>

