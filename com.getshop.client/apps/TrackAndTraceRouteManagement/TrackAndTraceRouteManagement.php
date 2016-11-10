<?php
namespace ns_5ebb7b42_35b7_48d3_b047_825dc2b30b5f;

class TrackAndTraceRouteManagement extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "TrackAndTraceRouteManagement";
    }

    public function render() {
        if ($this->getState() === "routeview") {
            $this->includefile("routeview");
        } else if ($this->getState() === "addUsers") {
            $this->includefile("addUsers");
        } else {
            $this->includefile("overview");
        }
    }

    public function getState() {
        if (!isset($_SESSION['track_trace_route_view'])) {
            return false;
        }
        
        
        return $_SESSION['track_trace_route_view'];
    }
   
     
    public function createRoute() {
        $route = new \core_trackandtrace_Route();
        $route->name = $_POST['data']['routename'];
        $this->getApi()->getTrackAndTraceManager()->saveRoute($route);
    }

    public function changeToRouteView() {
        $_SESSION['track_trace_route_view'] = "routeview";
        if (isset($_POST['data']['routeid'])) {
            $_SESSION['track_trace_route_view_id'] = $_POST['data']['routeid'];
        }
    }
    
    public function showAddDrivers() {
        $_SESSION['track_trace_route_view'] = "addUsers";
    }

    
    /**
     * 
     * @return \core_trackandtrace_Route
     */
    public function getRoute() {
        return $this->getApi()->getTrackAndTraceManager()->getRouteById($_SESSION['track_trace_route_view_id']);
    }

    /**
     * 
     * @return \core_usermanager_data_User[]
     */
    public function getSearchedForUsers() {
        if (isset($_POST['data']['searchfield'])) {
            return $this->getApi()->getUserManager()->findUsers($_POST['data']['searchfield']);
        }
        
        return [];
    }

    public function searchForUsers() {
        
    }
    
    public function addUser() {
        $route = $this->getRoute();
        $route->userIds[] = $_POST['data']['userid'];
        $this->getApi()->getTrackAndTraceManager()->saveRoute($route);
        $this->changeToRouteView();
    }
    
}
?>
