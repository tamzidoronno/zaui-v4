<?php
namespace ns_07c7565c_040d_4425_962d_a326c782bb4e;

class SedoxCreditHistory extends \ns_5278fb21_3c0a_4ea1_b282_be1b76896a4b\SedoxCommon implements \Application {
    private $currentSedoxUser = null;
    private $creditHistory = null;
    
    public function getDescription() {
        
    }

    public function getName() {
        return "SedoxCreditHistory";
    }

    public function render() {
        $this->includefile("credithistory");
    }

    private function loadData() {
        if (!$this->creditHistory) {
            $this->creditHistory = $this->getApi()
                ->getSedoxProductManager()
                ->getCurrentUserCreditHistory($this->createFilterData());
        }
    }
    
    public function getUserSettingsOrder() {
        return -2;
    }
    
    public function getCreditHistory() {
        $this->loadData();
        return $this->creditHistory;
    }
    
    public function defaultSorting() {
        return "sedoxcredithist_date";
    }
    
    public function getTotalPages() {
        return $this->getApi()
                ->getSedoxProductManager()
                ->getCurrentUserCreditHistoryCount($this->createFilterData());
    }

    public function renderUserSettings($user) {
        $this->currentSedoxUser = $user;
        $this->includefile("usersettings");
    }

    public function getCurrentUser() {
        return $this->currentSedoxUser;
    }
    
    public function updateCredit() {
        $this->getApi()->getSedoxProductManager()->addUserCredit($_POST['userid'], $_POST['creditDescription'], $_POST['creditAmount']);
    }

    public function downloadHistory() {
        
        $filter = $this->createFilterData();
        $filter->pageNumber = 1;
        $filter->pageSize = 9999999;
        
        $creditHistories =  $this->creditHistory = $this->getApi()
                ->getSedoxProductManager()
                ->getCurrentUserCreditHistory($filter);
        
        $rows = [];
        
        foreach ($creditHistories as $creditHistory) {
            $time = $this->formatJavaDateToTime($creditHistory->dateCreated);
            $date = date("d M Y", $time);

            $row = [];
            $row[] = $creditHistory->transactionReference;
            $row[] = $date;
            $row[] = $creditHistory->description;
            $row[] = $creditHistory->amount;
            $rows[] = $row;
        }
        
        echo json_encode($rows);
    }
}
?>
