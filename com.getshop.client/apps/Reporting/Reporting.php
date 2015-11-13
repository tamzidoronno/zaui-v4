<?php
namespace ns_04259325_abfa_4311_ab81_b89c60893ce1;

class Reporting extends \ReportingApplication implements \Application {

    public $singleton = true;
    private $start;
    private $end;
    private $type;
    private $sessionId;

    public function getDescription() {
        return $this->__f("Keep track of your products, orders, pages, etc with this application. Generate reports on a hourly, daily, weekly and monthly basis.");
    }

    public function getName() {
        return $this->__f("Reporting");
    }

    public function addPageFilter($word) {
        if (strlen(trim($word)) > 0) {
            $_SESSION['Reporting']['filteronword'][] = $word;
        }
    }

    public function removePageFilter($word) {
        if (($key = array_search($word, $_SESSION['Reporting']['filteronword'])) !== false) {
            unset($_SESSION['Reporting']['filteronword'][$key]);
        }
        
        $newRes = array();
        foreach($this->getPageFilter() as $page) {
            $newRes[] = $page;
        }
        $_SESSION['Reporting']['filteronword'] = $newRes;
    }

    public function getPageFilter() {
        return $_SESSION['Reporting']['filteronword'];
    }

    public function displaySessionData() {
        $startDate = $_POST['data']['startdate'];
        $stopDate = $_POST['data']['stopdate'];
        $sessionId = $_POST['data']['sessionId'];

        $this->start = $startDate;
        $this->end = $stopDate;
        $this->sessionId = $sessionId;
        $this->type = 1;

        $this->includefile("ReportingTrackedUserData");
    }

    public function convertToGoogleChartData($reports, $type, $timeType) {
        if (!is_array($reports)) {
            return;
        }
        $result = "[";
        $result .= "['Time','Result'],";
        foreach ($reports as $report) {
            /* @var $report core_reportingmanager_data_Report */
            $result .= "['" . $this->convertDateToType($report->timestamp, $timeType) . "',";
            $result .= $report->$type;

            $result .= "],";
        }

        $result = substr($result, 0, -1);
        $result .= "]";
        return $result;
    }

    public function renderStandalone() {
        $this->render();
    }

    public function renderConfig() {
        echo "<h1>" . $this->__f("Reporting") . "</h1>";
        echo $this->__f("Click on the icon in the top admin menu under category More to gain access to your reporting page.");
    }

    public function getAvailablePositions() {
        return "";
    }

    public function getStartDate() {
        return $this->start;
    }

    public function getEndDate() {
        return $this->end;
    }

    public function getType() {
        return $this->type;
    }
    
    public function render() {
        echo "\n" . '<script '.$this->getFactory()->includeSeo() .' type="text/javascript" src="https://www.google.com/jsapi"></script>';
        echo "<script>google.load('visualization', '1.0', {'packages':['corechart']});</script>";
        if (!isset($_SESSION['Reporting']['filteronword'])) {
            $_SESSION['Reporting']['filteronword'] = array();
        }

        $start = date("Y-m-d", time() - (60 * 60 * 24 * 7));
        $end = date("Y-m-d", time());
        $type = 1;

        if (isset($_POST['data']['startdate'])) {
            $start = $_POST['data']['startdate'];
            $end = $_POST['data']['stopdate'];
            $type = $_POST['data']['type'];
        }

        if (isset($_POST['data']['filteronword'])) {
            $this->addPageFilter($_POST['data']['filteronword']);
        }

        if (isset($_POST['data']['removeWord'])) {
            $this->removePageFilter($_POST['data']['removeWord']);
        }

        $this->start = $start;
        $this->end = $end;
        $this->type = $type;


        $this->includefile("ReportingTemplate");

        $page = "overview";
        if (isset($_POST['data']['page'])) {
            $page = $_POST['data']['page'];
        }

        switch ($page) {
            case "overview":
                $this->includefile("ReportingOverview");
                break;
            case "pageviews":
                $this->includefile("ReportingPageViews");
                break;
            case "usersloggedon":
                $this->includefile("ReportingUsersLoggedOn");
                break;
            case "productviews":
                $this->includefile("ReportingProductViewed");
                break;
            case "orderscreated":
                $this->includefile("ReportingOrdersCreated");
                break;
            case "tracker":
                $this->includefile("ReportingTracking");
                break;
        }
    }

    public function printTable($reports) {
        echo "<table>";
        echo "<tr>";
        echo "<th></th>";
        foreach ($reports as $report) {
            /* @var $report core_reportingmanager_data_Report */
            $time = strtotime($report->timestamp);
            echo "<th style='font-size:8px;'>" . date("y-m-d h:m:s", $time) . "</th>";
        }
        echo "</tr>";
        $this->printRow("pagesAccessed", $reports);
        $this->printRow("productsAccess", $reports);
        $this->printRow("usersLoggedOn", $reports);
        $this->printRow("orderCount", $reports);
        $this->printRow("userCreated", $reports);
        echo "</table>";
    }

    public function printRow($row, $reports) {
        echo "<tr>";
        echo "<td>" . $row . "</td>";
        foreach ($reports as $report) {
            echo "<td>" . $report->$row . "</td>";
        }
        echo "</tr>";
    }

    public function reprintReport() {
        
    }

    public function convertDateToType($date, $type) {
        $time = strtotime($date);
        if ($type == 0) {
            return date("Y-m-d h", $time);
        } elseif ($type == 1) {
            return date("Y-m-d", $time);
        } elseif ($type == 2) {
            return "week " . date("W - Y", $time);
        } elseif ($type == 3) {
            return date("M Y", $time);
        } else {
            return $date;
        }
    }

    public function getSessionId() {
        return $this->sessionId;
    }

}

?>
