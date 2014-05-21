<?php
namespace ns_d16b27d9_579f_4d44_b90b_4223de0eb6f2;

class Hotelbooking extends \ApplicationBase implements \Application {
    var $entries;
    var $dept;
    var $currentMenuEntry;
    
    function __construct() {
    }
 
   public function getDescription() {
        return "Hotelbooking";
    }

    public function getAvailablePositions() {
        return "left";
    }
    
    function getNumberOfAvailableRooms($type) {
        return $this->getApi()->getHotelBookingManager()->checkAvailable($this->getStart(),$this->getEnd(),$type);
    }
    
    function checkavailability() {
        $start = strtotime($_POST['data']['start']);
        $end =  strtotime($_POST['data']['stop']);
        $product = $this->getApi()->getProductManager()->getProduct($_POST['data']['roomProduct']);
        
        $numbers = $this->getApi()->getHotelBookingManager()->checkAvailable($start,$end,$product->sku);
        if($numbers) {
            echo $numbers;
        }
        
        $this->setStartDate($start);
        $this->setEndDate($end);
        $this->setProductId($product->id);
    }
    
    public function getProduct() {
        return $this->getApi()->getProductManager()->getProduct($_SESSION['hotelbooking']['product']);
    }
    
    public function getStart() {
        return $_SESSION['hotelbooking']['start'];
    }
    
    public function getEnd() {
        return $_SESSION['hotelbooking']['end'];
    }
    
    public function getType() {
        return $_SESSION['hotelbooking']['type'];
    }
    
    public function getName() {
        return "Hotelbooking";
    }

    public function postProcess() {
        
    }

    public function preProcess() {
        
    }
    
    public function getStarted() { 
   }

   public function getContinuePage() {
       return $this->getConfigurationSetting("contine_page");
   }
   
    public function render() {
        if(isset($_GET['set_order_page'])) {
            $this->setConfigurationSetting("contine_page", $_GET['set_order_page']);
        }
        if($this->getPage()->id == "home") {
            $this->includefile("Hotelbooking");
        } else {
            $this->includefile("booking_part2");
        }
    }

    public function updateCalendarDate() {
        $type = $_POST['data']['type'];
        $day = $_POST['data']['day'];
        $month = $_POST['data']['month'];
        $year = $_POST['data']['year'];
        
        if($type == "startDate") {
            $this->setStartDate(strtotime($year."-".$month."-".$day));
        }
        if($type == "endDate") {
            $this->setEndDate(strtotime($year."-".$month."-".$day));
        }
    }
    
    public function printCalendar($time, $checkbefore, $id) {
        $month = date("m", $time);
        $timestamp = mktime(0,0,0,$month,1,date("y", $time));
        $maxday = date("t",$timestamp);
        $thismonth = getdate ($timestamp);
        $startday = $thismonth['wday'];
        $year = date('y', $time);
        if($startday == 0) {
            $startday = 7;
        }
        echo "<table width='100%' class='booking_table' type='$id' year='$year' month='$month'>";
        echo "<tr>";
        echo "<th>".$this->__w("Mo")."</th>";
        echo "<th>".$this->__w("Tu")."</th>";
        echo "<th>".$this->__w("We")."</th>";
        echo "<th>".$this->__w("Th")."</th>";
        echo "<th>".$this->__w("Fr")."</th>";
        echo "<th>".$this->__w("Sa")."</th>";
        echo "<th>".$this->__w("Su")."</th>";
        echo "</tr>";
        echo "<tr>";
        for ($i=1; $i<($maxday+$startday); $i++) {
            if($i < $startday) 
                echo "<td></td>";
            else {
                $class = "";
                $day = $i - $startday + 1;
                if(date('d', $time) ==(($i - $startday)+1)) {
                    $class = "selected";
                }
                if($checkbefore) {
                    $aftertime = strtotime($year . "-" . $month . "-" . $day);
                    if($checkbefore > $aftertime || date("d", $checkbefore) == date("d",$aftertime)) {
                        $class .= " disabled";
                    }
                }
                
                echo "<td align='center' valign='middle' height='20px'><span class='cal_field $class' day='$day'>".$day."</span></td>";
            }
            if(($i % 7) == 0) echo "</tr><tr>";
        }
        echo "</tr>";
        echo "</table>";
    }
    

    public function updateRoomCount() {
        $this->setRoomCount($_POST['data']['count']);
    }
    
    public function getRoomCount() {
        if(isset($_SESSION['hotelbooking']['count'])) {
            return $_SESSION['hotelbooking']['count'];
        }
        return 1;
    }
    
    public function changeProduct() {
        $this->setProductId($_POST['data']['productid']);
    }
    
    private function setRoomCount($count) {
        $_SESSION['hotelbooking']['count'] = $count;
    }
    
    public function setStartDate($start) {
        $_SESSION['hotelbooking']['start'] = $start;
    }

    public function setEndDate($end) {
        $_SESSION['hotelbooking']['end'] = $end;
    }

    public function setProductId($id) {
        $_SESSION['hotelbooking']['product'] = $id;
    }
}
?>