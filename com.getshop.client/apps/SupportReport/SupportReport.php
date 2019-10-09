<?php
namespace ns_b372a413_dbbe_44c0_b473_13fd48e2d1ff;

class SupportReport extends \WebshopApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "SupportReport";
    }
    
    
    public function setSelectedTimePeriode() {
        $_SESSION['getshopreportselectedtimeperiode'] = $_POST['data']['selectedtime'];
    }
    
    public function getSelectedTimePeriode() {
        if(isset($_SESSION['getshopreportselectedtimeperiode'])) {
            return $_SESSION['getshopreportselectedtimeperiode'];
        }
        return time();
    }

    public function render() {
        $createdDate = $this->getApi()->getStoreManager()->getMyStore()->rowCreatedDate;
        echo "<div style='margin:auto; width: 1000px;'>";
        $time = strtotime($createdDate);
        echo "<div gstype='form' method='setSelectedTimePeriode'>";
        echo "<h1>Select a time periode</h1>";
        echo "<select class='gsniceselect1' gsname='selectedtime'>";
        while(true) {
            $time = strtotime("+1 month", $time);
            $offset = date("m-Y", $time);
            $selected = (date("my", $time) == date("my", $this->getSelectedTimePeriode())) ? "SELECTED" : "";
            echo "<option value='$time' $selected>" . date("Y M", $time) . "</option>";
            if($time > time()) {
                break;
            }
        }
        echo "</select> ";
        echo "<span class='shop_button' gstype='submit'>Load report</span>";
        echo "</div>";
        
        echo "<div>";
        echo "<div>";
        echo "<br>";
        echo "<br>";
        echo "<br>";
        $this->printReport();
        echo "</div>";
        
    }

    public function printReport() {
        $selectedTime = $this->getSelectedTimePeriode();
        echo "<h1>Support report for periode " . date("01.m.Y", $selectedTime) . " - " . date('t.m.Y', $selectedTime) . "</h1>";
        
        $start = $this->convertToJavaDate(strtotime(date("01.m.Y 00:00", $selectedTime)));
        $end = $this->convertToJavaDate(strtotime(date("t.m.Y 23:59", $selectedTime)));
        
        $report = $this->getSystemGetShopApi()->getCustomerTicketManager()->getTicketReportForCustomer($start, $end, $this->getFactory()->getStore()->id);
        
        echo "Total support " . $report->supportHours . " hours, " . $report->supportMinutes . " minutes, " . $report->supportSeconds. " seconds.<br>";
        echo "Billable support " . $report->billableHours . " hours, " . $report->billableMinutes . " minutes, " . $report->billableSeconds. " seconds.<br>";
        echo "You have " . $report->hoursIncluded . " hours in your subscription plan, this will be deducted on the invoice.<br><br>";
        
        echo "<table width='100%'>";
        echo "<tr>";
        echo "<th align='left'>Start</th>";
        echo "<th align='left'>End</th>";
        echo "<th align='left'>Title</th>";
        echo "<th align='left'>Hours</th>";
        echo "<th align='left'>Minutes</th>";
        echo "<th align='left'>Seconds</th>";
        echo "<th align='left'></th>";
        echo "</tr>";
        
        foreach($report->lines as $line) {
            echo "<tr>";
            echo "<td>" . date("d.m.Y H:i:s", strtotime($line->startSupport)) . "</td>";
            echo "<td>" . date("d.m.Y H:i:s", strtotime($line->endSupport)) . "</td>";
            echo "<td>";
            if(!$line->billable) {
                echo "(free) ";
            }
            echo $line->title . "</td>";
            echo "<td>" . $line->hours . "</td>";
            echo "<td>" . $line->minutes . "</td>";
            echo "<td>" . $line->seconds . "</td>";
            echo "<td><a href='/getshopsupport.php?page=ticketview&ticketToken=".$line->token."'>Open</a></td>";
            echo "</tr>";
        }
        
        echo "</table>";
    }

}
?>
