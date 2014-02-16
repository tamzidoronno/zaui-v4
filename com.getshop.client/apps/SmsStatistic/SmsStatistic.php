<?php
namespace ns_39f1485a_85b8_4f09_ba70_0e33c16f8dc6;

class SmsStatistic extends \MarketingApplication implements \Application {
    public $singleton = true;
    
    public function getDescription() {
        return $this->__f("By adding this application you will see some static over your sms usage.");
    }
    
    public function getName() {
        return $this->__f("Sms Statistic");
    }
    
    public function render() {
    }
    
    public function renderConfig() {
        $messageManager = $this->getApi()->getMessageManager();
        
        echo "<table>";
        echo "<th> Month <th> Count";
        $year = (int)date('Y');
        $month = (int)date('m');
        $month++;
        
        for ($i=0; $i<12; $i++) {
            $month--;
            if ($month == 0) {
                $month = 12;
                $year--;
            }
            $monthName = date("F",mktime(0,0,0,$month,1,$year));
            $count = $messageManager->getSmsCount($year, $month);
            echo "
            <tr>
                <td style='border-bottom: dashed 1px;' >$monthName - $year</td>
                <td style='border-bottom: dashed 1px;' align='center'>$count</td>
            </tr>";
        }
        echo "</table>";
        
        
    }
}

?>
