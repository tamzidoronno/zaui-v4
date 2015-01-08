<?php

namespace ns_b81bfb16_8066_4bea_a3c6_c155fa7119f8;

class DashBoard extends \ApplicationBase implements \Application {
    
    public function getDescription() {
        return $this->__f("A dashboad application");
    }

    public function getName() {
        return $this->__f("DashBoard");
    }

    public function render() {
        // Nothing to render
    }    
    
    public function renderConfig() {
        $this->includefile("dashboard");
    }
    
    public function getDashboardChart($year=false) {
        echo "<script>";
        echo "app.DashBoard.logins=".json_encode($this->getApi()->getUserManager()->getLogins($year));
        echo "</script>";
        return ['fa-area-chart', 'app.DashBoard.drawChart', $this->__f("Overview")];
    }

    public function setDashBoardChart() {
        $_SESSION['gss_dashboard_chart_app'] = $_POST['value'];
        $_SESSION['gss_dashboard_chart_app_year'] = $_POST['value2'];
    }
    
    public function getSetYear() {
        return isset($_SESSION['gss_dashboard_chart_app_year']) ? $_SESSION['gss_dashboard_chart_app_year'] : date("Y",strtotime("0 year"));
    }
    
    public function getCurrentChartApp() {
        if (!isset($_SESSION['gss_dashboard_chart_app'])) {
            return $this;
        }
        
        $app = $this->getApi()->getStoreApplicationPool()->getApplication($_SESSION['gss_dashboard_chart_app']);
        return $this->getFactory()->getApplicationPool()->createInstace($app);
    }
}
?>
