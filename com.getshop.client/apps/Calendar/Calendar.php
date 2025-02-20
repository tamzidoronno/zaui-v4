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
    public $downloadingListAsPdf = false;
    private $activeFilters;
    
    public function getDescription() {
        return $this->__("Keep your customers up to date of what events you are hosting with this application");
    }
    
    public function updateEventHelder() {
        $id = $_POST['data']['id'];
        $helder = $_POST['data']['helder'];
        
        $entry = $this->getApi()->getCalendarManager()->getEntry($id);
        $entry->eventHelder = $helder;
        $this->getApi()->getCalendarManager()->saveEntry($entry);
    }
    
    public function updateUserSettings() {
        $userid = $_POST['data']['userId'];
        $receiveDiploma = $_POST['data']['receiveDiploma'];
        $entryId = $_POST['data']['configentryId'];
        
        $entryObject = $this->getApi()->getCalendarManager()->getEntry($entryId);
        
        $newObject = array();
        foreach($entryObject->dropDiploma as $key => $tmpuserid) {
            $newObject[$tmpuserid] = "";
        }
        
        if($receiveDiploma == "true") {
            unset($newObject[$userid]);
        } else {
            $newObject[$userid] = "";
        }
        
        print_r(array_keys($newObject));
        
        $entryObject->dropDiploma = array_keys($newObject);
        
        $this->getApi()->getCalendarManager()->saveEntry($entryObject);
    }
    
    public function addCommentToEntry() {
        $id = $_POST['data']['id'];
        $text = $_POST['data']['text'];
        $entry = $this->getApi()->getCalendarManager()->getEntry($id);
        
        $comment = new \core_calendarmanager_data_EntryComment;
        $comment->addedWhen = time();
        $comment->text = $text;
        $comment->userId = $this->getUser()->id;
        
        $entry->comments[] = $comment;
        $this->getApi()->getCalendarManager()->saveEntry($entry);
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
        
        if ($entry->year == date('Y') && $entry->month == date('m') && $entry->day < date('d'))
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
    
    public function asList() {
        if (!$this->isShowTabsForViewMode()) {
            return false;
        }
        
        if (isset($_SESSION['calendar_as_calendar'])  && $_SESSION['calendar_as_calendar'] == "true") {
            return false;
        }
        
        return true;
    }
    
    public function setAsCalender() {
        $_SESSION['calendar_as_calendar'] = "true";
    }
    
    public function unsetAsCalender() {
        unset($_SESSION['calendar_as_calendar']);
    }
    
    public function toggleListView() {
        if (!isset($_GET['asList'])) {
            return;
        }
        
        if ($_GET['asList'] == "true") {
            $this->unsetAsCalender();
        } else {
            $this->setAsCalender();
        }
    }
    public function render() {
        $this->toggleListView();
        $this->setFilter();
        $this->initMonth();
        
        $this->includefile('calendar');
        if (\ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::isAdministrator()) {
            $this->includefile("setup");
        }
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
        $this->downloadingListAsPdf = $includeCss;
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
        if (isset($_POST['data']['eventdescription']))
            $entry->description = $_POST['data']['eventdescription'];
        
        if (isset($_POST['data']['eventstart']))
            $entry->starttime = $_POST['data']['eventstart'];
        
        if (isset($_POST['data']['eventstop']))
            $entry->stoptime = $_POST['data']['eventstop'];
        
        if (isset($_POST['data']['maxattendees']))
            $entry->maxAttendees = $_POST['data']['maxattendees'];
        
        if (isset($_POST['data']['locationId']))
            $entry->locationId = $_POST['data']['locationId'];
        
        if (isset($_POST['data']['eventname']))
            $entry->title = $_POST['data']['eventname'];
        
        if (isset($_POST['data']['color']))
            $entry->color = $_POST['data']['color'];
        
        if (isset($_POST['data']['extraText']))
            $entry->extraText = $_POST['data']['extraText'];
        
        if (isset($_POST['data']['linkToPage']))
            $entry->linkToPage = $_POST['data']['linkToPage'];
        
        if (isset($_POST['data']['lockedForSignup']))
            $entry->lockedForSignup = $_POST['data']['lockedForSignup'];
        
        if (isset($_POST['data']['eventHelder']))
            $entry->eventHelder = $_POST['data']['eventHelder'];
        
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

        $entry->eventId = $_POST['data']['eventId'];
        $this->getApi()->getCalendarManager()->saveEntry($entry);
    }
    
    public function deleteEntry() {
        $this->getApi()->getCalendarManager()->deleteEntry($_POST['data']['entryId']);
    }
    
    public function getNextAttr() {
        $page = $this->getPage()->getId();
        $month = $this->month + 1;
        $year = $this->year;
        if ($month > 12) {
            $month = 1;
            $year = $this->year + 1;
        }
        return "year='$year' month='$month'";
    }
    
    public function getPrevAttrs() {
        $page = $this->getPage()->getId();
        $month = $this->month - 1;
        $year = $this->year;
        if ($month < 1) {
            $month = 12;
            $year = $this->year - 1;
        }
        return "year='$year' month='$month'";
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
    
    private function getAttachment() {
        if ($_POST['data']['attachment1']) {
            $data = explode(",", $_POST['data']['attachment1']);
            return $data[1];
        }
        
        return "";
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
                        $_POST['data']['entryid'],
                        $this->getAttachment(),
                        $_POST['data']['filename']
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
        $a = $c->day;
        $b = $d->day;
        
        if ($a == $b) {
            return 0;
        }
        
        return ($b > $a) ? -1 : 1;
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
                            $retdata[$month->year][$month->month][] = $entry;
                        }
                    }
                }
            }
        }
        
        $sortedArray = [];
        
        foreach ($retdata as $year => $data) {
            $sortedMonths = [];
            foreach ($data as $month => $monthData) {
                $res = usort($monthData, array("ns_6f3bc804_02a1_44b0_a17d_4277f0c6dee8\Calendar", "usortCalendarListViewEntry"));
                $sortedMonths[$month] = $monthData;
            }
            
            ksort($sortedMonths);
            $sortedArray[$year] = $sortedMonths;
        }
        
        ksort($sortedArray);
        return $sortedArray;
    }

    public function getCountOfFreePositions($entry) {
        $attendees = $this->getAttendees($entry);
        return $entry->maxAttendees - count($attendees);
    }
    
    public function showCandidateSetting() {
        $this->includefile("candidate_setting");
    }
    
    public function deleteCommentToEntry() {
        $commentid = $_POST['data']['commentid'];
        $entryid = $_POST['data']['entryid'];
        
        $entry = $this->getApi()->getCalendarManager()->getEntry($entryid);
        $newlist = array();
        foreach($entry->comments as $cmnt) {
            if($cmnt->id != $commentid) {
                $newlist[] = $cmnt;
            }
        }
        $entry->comments = $newlist;
        $this->getApi()->getCalendarManager()->saveEntry($entry);
    }
    
    public function showAddUserInterface() {
        $this->includefile("addUser");
    }
    
    public function getSearchAddToUserResult() {
        if (!isset($_POST['data']['search'])) {
            return array();
        }
        
        return $this->getApi()->getUserManager()->findUsers($_POST['data']['search']);
    }
    
    public function addUserToEventSilent() {
        $this->getApi()->getCalendarManager()->addUserSilentlyToEvent($_POST['data']['entryId'], $_POST['data']['userId']);
    }

    public function getSource($user, $entry) {
        if (!$this->hasWriteAccess()) {
            return "";
        }
        
        foreach($entry->metaInfo as $metaInfo) {
            if ($metaInfo->userId == $user->id) {
                if ($metaInfo->source == "webpage") {
                    return "<i class='source fa fa-globe'></i>";
                } else {
                    return "<i class='source fa fa-mobile'></i>";
                }
            }
        }
        
        return "";
    }
    
    public function changeMonth() {
        $_GET['year'] = $_POST['data']['year'];
        $_GET['month'] = $_POST['data']['month'];
        
        $this->initializeDate();
        $this->initMonth();

        $this->includefile('calendar');
    }
    
    public function renderEntry($entry) {
        echo '<div class="day_entry_information"></div>';
    }
    
    public function saveEvent() {
        $event = new \core_calendarmanager_data_Event();
        
        if (isset($_POST['data']['eventId']) && $_POST['data']['eventId']) {
            $event = $this->getApi()->getCalendarManager()->getEvent($_POST['data']['eventId']);
        }
        
        $event->title = $_POST['data']['name'];
        $event->capacity = $_POST['data']['participants'];
        $event->description = $_POST['data']['event_description'];
        $event->priceAdults = $_POST['data']['priceAdults'];
        $event->priceChild = $_POST['data']['priceChild'];
        $content = base64_decode($_POST['data']['iconBase64']);
        if ($content && $_POST['data']['iconBase64'] != "false"){
            $event->iconImageId = \FileUpload::storeFile($content);
        } 
        
        $content2 = base64_decode($_POST['data']['imageBase64']);
        if ($content2 && $_POST['data']['imageBase64'] != "false") {
            $event->imageId = \FileUpload::storeFile($content2);
        }
        
        
        $this->getApi()->getCalendarManager()->addEvent($event);
    }
    
    public function getEvent() {
        $event = $this->getApi()->getCalendarManager()->getEvent($_POST['data']['eventId']);
        echo json_encode($event);
    }
    
    public function showEventNew() {
        $this->includeFile("dayview");
        if (\ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::isAdministrator()) {
            $this->includefile("entrysettings");
        }
    }
    
    public function removeUserFromEvent() {
        $this->getApi()->getCalendarManager()->removeUserFromEvent($_POST['data']['userId'], $_POST['data']['entryId']);
    }

    public function hasPaid($entry, $userid) {
        $foundOrderId = "";
        
        foreach($entry->ordersVsUsers as $orderId => $userList) {
            if (in_array($userid, $userList)) {
                $foundOrderId = $orderId;
                break;
            }
        }
        
        $order = $this->getApi()->getOrderManager()->getOrder($foundOrderId);
        if ($order) {
            return $order->status == 4 || $order->status == 7;
        }
        
        return false;
    }

}
?>