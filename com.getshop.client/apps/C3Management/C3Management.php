<?php
namespace ns_e913b056_a5e1_4ca8_83b6_92f192e2a06b;

class C3Management extends \MarketingApplication implements \Application {

    private $currentUser;

    public function getDescription() {
        
    }

    public function getName() {
        return "C3Management";
    }

    public function render() {
        
    }
    
    public function saveRoundSums() {
        foreach ($_POST as $year => $sum) {
            if (is_integer($year) && $year && $sum) {
                $this->getApi()->getC3Manager()->setC3RoundSum($year, $sum);
            }
        }
        
    }
    
    public function renderConfig() {
        $this->includefile("overview");
    }
    
    public function addTimeRate() {
        $this->getApi()->getC3Manager()->addTimeRate($_POST['timeratenavn'], $_POST['timeratekr']);
    }
    
    public function deleteTimeRate() {
        $this->getApi()->getC3Manager()->deleteTimeRate($_POST['value']);
    }
    
    public function saveTimeRates() {
        $rates = $this->getApi()->getC3Manager()->getTimeRates();
        foreach ($rates as $rate) {
            $rate->rate = $_POST['timerate_'.$rate->id];
            $this->getApi()->getC3Manager()->saveRate($rate);
        }
        
    }

    public function getRoundSum($year) {
        $roundSum = $this->getApi()->getC3Manager()->getRoundSum($year);
        if ($roundSum)
            return $roundSum->sum;
        
        return "";
    }

    public function renderUserSettings($user) {
        $this->currentUser = $user;
        $this->includefile("usersettings");
    }
    
    public function getCurrentUser() {
        return $this->currentUser;
    }
    
    public function saveHourRate() {
        $this->getApi()->getC3Manager()->setRateToUser($_POST['userid'], $_POST['hourrate']);
    }

    public function isSelected($rate, $user) {
        $timeRate = $this->getApi()->getC3Manager()->getTimeRate($user->id);
        if ($timeRate->id == $rate->id) {
            return true;
        }
        
        return false;
    }

    public function addPeriode() {
        $periode = new \core_c3_C3ProjectPeriode();
        $periode->from = $this->convertToJavaDate(strtotime($_POST['from']));
        $periode->to = $this->convertToJavaDate(strtotime($_POST['to']));
        $this->getApi()->getC3Manager()->savePeriode($periode);
    }

    public function getActivePeriodeId() {
        if (!isset($this->activePeriodeId)) {
            $activePeriode = $this->getApi()->getC3Manager()->getActivePeriode();
            if ($activePeriode) {
                $this->activePeriodeId = $activePeriode->id;
            } else {
                $this->activePeriodeId = "";
            }
        }
        
        return $this->activePeriodeId;
    }
    
    public function saveActivePeriode() {
        $periodes = $this->getApi()->getC3Manager()->getPeriodes();
        foreach ($periodes as $periode) {
            if ($_POST['active_periode_'.$periode->id] === "true") {
                $this->getApi()->getC3Manager()->setActivePeriode($periode->id);
            }
        }
    }

    public function getUserSettingsOrder() {
        return -10;
    }
    
    public function addNewPeriode() {
        $periode = new \core_c3_C3ForskningsUserPeriode();
        $periode->start = $this->convertToJavaDate(strtotime($_POST['from']));
        $periode->end = $this->convertToJavaDate(strtotime($_POST['to']));
        $periode->projectId = $_POST['projectid'];
        $periode->percents = $_POST['percent'];
        $periode->userId = $_POST['value'];
        $this->getApi()->getC3Manager()->addForskningsUserPeriode($periode);
    }
    
    public function deletePeriode() {
        $this->getApi()->getC3Manager()->deleteForskningsUserPeriode($_POST['value2']);
    }
}
?>
