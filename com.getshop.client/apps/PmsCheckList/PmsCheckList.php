<?php
namespace ns_24206ea4_45f2_4a08_ac57_ed2c6c8b22f5;

class PmsCheckList extends \MarketingApplication implements \Application {
    private $currentError = null;
    
    public function getDescription() {
        
    }

    public function getName() {
        return "PmsCheckList";
    }

    public function render() {
        $this->printMonthSelector();
        if (isset($_SESSION['ns_24206ea4_45f2_4a08_ac57_ed2c6c8b22f5'])) {
            $this->printResult();
        }
    }

    /**
     * 
     * @return \core_checklist_CheckListError
     */
    public function getCurrentError() {
        return $this->currentError;
    }

    public function printMonthSelector() {
        $this->getCurrentMonth();
        echo "<div style='text-align: center' gstype='form' method='dateChanged'>";
            echo "Select month<br/> <select gsname='date' class='gsniceselect1'>";
            for ($i=0;$i<10;$i++) {
                $year = date('Y', strtotime('-'.$i.' month'));
                $month = date('m', strtotime('-'.$i.' month'));
                $selected = $this->getCurrentMonth() == $year."_".$month ? "selected='true'" : "";
                echo "<option $selected value='".$year."_".$month."'>".date('M Y', strtotime('-'.$i.' month'))."</option>";
            }
            echo "</select>";
            echo "<br/><div class='shop_button' gstype='submit'>Show</div>";
        echo "</div>";
        
    }

    public function printResult() {
        $monthText = $this->getCurrentMonth();
        $monthText = explode("_", $monthText);
        $year = $monthText[0];
        $month = $monthText[1];
        $monthEnd = (int)$monthText[1]+1;
        
        $from = $this->convertToJavaDate(strtotime("$month/01-$year 00:00:00"));
        $end = $this->convertToJavaDate(strtotime("$monthEnd/01-$year 00:00:00"));
        $result = $this->getApi()->getChecklistManager()->getErrors($this->getSelectedMultilevelDomainName(), $from, $end);

        $grouped = array();
        
        foreach ($result as $error) {
            if (!isset($grouped[$error->filterType])) {
                $grouped[$error->filterType] = array();
            }
            
            $grouped[$error->filterType][] = $error;
        }
        
        foreach ($grouped as $type => $errors) {
            echo "<div>";
                echo "<h2>".$type."</h2>";
                foreach ($errors as $error) {
                    $this->currentError = $error;
                    echo "<div class='errorrow'>";
                        $this->includefile($error->filterType);
                    echo "</div>";
                }
            echo "</div>";
        }
    }

    public function getCurrentMonth() {
        if (isset($_SESSION['ns_24206ea4_45f2_4a08_ac57_ed2c6c8b22f5'])) {
            return $_SESSION['ns_24206ea4_45f2_4a08_ac57_ed2c6c8b22f5'];
        }
        
        $year = date('Y');
        $month = date('m');
        return $year."_".$month;
   }

   public function dateChanged() {
       $_SESSION['ns_24206ea4_45f2_4a08_ac57_ed2c6c8b22f5'] = $_POST['data']['date'];
   }
}
?>
