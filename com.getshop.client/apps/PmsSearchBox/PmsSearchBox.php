<?php
namespace ns_1ba01a11_1b79_4d80_8fdd_c7c2e286f94c;

class PmsSearchBox extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "PmsSearchBox";
    }

    public function render() {
        $this->includefile("serverwarning");
        $this->includefile("searchbox");
    }
    
    public function sendMessagesOverview() {
        $this->includefile("sendmessages");
    }
    
    public function resetAutoAssignedStatusForCheckinsToday() {
        $this->getApi()->getPmsManager()->resetCheckingAutoAssignedStatus($this->getSelectedMultilevelDomainName());
        $this->runProcessor();
    }
    
    public function runProcessor() {
        $this->getApi()->getPmsManager()->processor($this->getSelectedMultilevelDomainName());
        $this->getApi()->getPmsManager()->hourlyProcessor($this->getSelectedMultilevelDomainName());
        $this->getApi()->getPmsManager()->checkIfGuestHasArrived($this->getSelectedMultilevelDomainName());
        $cards = $this->getApi()->getPmsManager()->getCardsToSave($this->getSelectedMultilevelDomainName());
        print_r($cards);
    }
    
    public function search() {
        $pms = new \ns_961efe75_e13b_4c9a_a0ce_8d3906b4bd73\PmsSearchBooking();
        $pms->searchBooking();
    }
    
    public function quickfilterselection() {
        $pms = new \ns_961efe75_e13b_4c9a_a0ce_8d3906b4bd73\PmsSearchBooking();
        $pms->quickfilterselection();
    }
    
    public function sendMassMessages() {
        $type = $_POST['data']['type'];
        $msg = $_POST['data']['message'];
        $title = $_POST['data']['title'];
        if(!$msg) {
            return;
        }
        if($type == "sms" || $type == "both") {
            foreach($_POST['data']['phonenumbers'] as $phonenumbers) {
                if(!$phonenumbers['prefix'] || !$phonenumbers['phone']) {
                    continue;
                }
                $prefix = $phonenumbers['prefix'];
                $phone = $phonenumbers['phone'];
                $roomid = $phonenumbers['roomid'];
                $this->getApi()->getPmsManager()->sendSmsOnRoom($this->getSelectedMultilevelDomainName(), $prefix, $phone, $msg, $roomid);
            }
        }
        if($type == "email" || $type == "both") {
            foreach($_POST['data']['emails'] as $emails) {
                if(!$emails['email']) {
                    continue;
                }
                $email = $emails['email'];
                $roomid = $emails['roomid'];
                $this->getApi()->getPmsManager()->sendMessageOnRoom($this->getSelectedMultilevelDomainName(), $email, $title, $msg, $roomid);
            }
        }
    }
    
    public function doAdvanceSearch() {
        $pms = new \ns_961efe75_e13b_4c9a_a0ce_8d3906b4bd73\PmsSearchBooking();
        $pms->advanceSearchBooking();
    }
    
    public function clearFilter() {
        $pms = new \ns_961efe75_e13b_4c9a_a0ce_8d3906b4bd73\PmsSearchBooking();
        $pms->clearFilter();
    }
    
    public function showCheckins() {
        $pms = new \ns_961efe75_e13b_4c9a_a0ce_8d3906b4bd73\PmsSearchBooking();
        $pms->showCheckins();
    }
    
    public function showCheckOuts() {
        $pms = new \ns_961efe75_e13b_4c9a_a0ce_8d3906b4bd73\PmsSearchBooking();
        $pms->showCheckouts();
    }

    public function getEndDate() {
        $app = new \ns_28886d7d_91d6_409a_a455_9351a426bed5\PmsAvailability();
        return date('d.m.Y', $app->getEndDate());
    }
    
    public function getStartDate() {
        $app = new \ns_28886d7d_91d6_409a_a455_9351a426bed5\PmsAvailability();
        return date('d.m.Y', $app->getStartDate());
    }
    
    public function getSearchTypes() {
        $searchtypes = array();
        $searchtypes['registered'] = "Registered";
        $searchtypes['active'] = "Active";
        $searchtypes['checkin'] = "Checking in";
        $searchtypes['checkout'] = "Checking out";
        $searchtypes['inhouse'] = "Inhouse";
        return $searchtypes;
    }

    
    public function searchCustomers() {
        $name = $_POST['data']['name'];
        $filterData = new \core_common_FilterOptions();
        $filterData->searchWord = $name;
        $data = $this->getApi()->getUserManager()->getAllUsersFiltered($filterData);
        foreach($data->datas as $user) {
            echo "<div class='addusertofilter addtofilterrow' userid='".$user->id."'><i class='fa fa-trash-o'></i>" . $user->fullName . "</div>";
        }
    }
    
    public function downloadOptInUsers() {
        $pms = new \ns_961efe75_e13b_4c9a_a0ce_8d3906b4bd73\PmsSearchBooking();
        $filter = $pms->getSelectedFilter();
        $rooms = $this->getApi()->getPmsManager()->getSimpleRooms($this->getSelectedMultilevelDomainName(), $filter);
        $userIds = array();
        foreach($rooms as $room) {
            $userIds[$room->userId] = 1;
        }
        
        $rows = array();
        $header = array();
        $header[] = "Name";
        $header[] = "Email";
        $header[] = "Prefix";
        $header[] = "Phone";
        
        $rows[] = $header;
        
        foreach($userIds as $usrId => $val) {
            $user = $this->getApi()->getUserManager()->getUserById($usrId);
            if($user->agreeToSpam) {
                $row = array();
                $row[] = $user->fullName;
                $row[] = $user->emailAddress;
                $row[] = $user->prefix;
                $row[] = $user->cellPhone;
                $rows[] = $row;
            }
        }
        echo json_encode($rows);
    }
    
    public function displayOtherSelection() {
        $this->includefile("otherselection");
    }

    public function getChannelMatrix() {
        return $this->getApi()->getPmsManager()->getChannelMatrix($this->getSelectedMultilevelDomainName());
    }

    public function gsAlsoUpdate() {
        $alsoUpdate = array();
        $alsoUpdate[] = '961efe75-e13b-4c9a-a0ce-8d3906b4bd73';
        return $alsoUpdate;
    }
}
?>
