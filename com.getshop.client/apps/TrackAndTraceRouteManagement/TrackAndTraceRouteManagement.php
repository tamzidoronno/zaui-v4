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
        } else if ($this->getState() === "addDestionation") {
            $this->includefile("addDestionation");
        } else if ($this->getState() === "editdestination") {
            $this->includefile("editdestination");
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
    
    public function addDestinationToRoute() {
        $route = $this->getRoute();
        $this->getApi()->getTrackAndTraceManager()->moveDestinationFromPoolToRoute($_POST['data']['destid'], $route->id);
    }
    
    public function showAddDrivers() {
        $_SESSION['track_trace_route_view'] = "addUsers";
    }

    public function moveToPool() {
        $route = $this->getRoute();
        $this->getApi()->getTrackAndTraceManager()->moveDesitinationToPool($route->id, $_POST['data']['destionationid']);
    }
    
    /**
     * 
     * @return \core_trackandtrace_Route
     */
    public function getRoute() {
        $routes = $this->getApi()->getTrackAndTraceManager()->getRoutesById($_SESSION['track_trace_route_view_id']); 
        return $routes[0];
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
        $this->getApi()->getTrackAndTraceManager()->addDriverToRoute($_POST['data']['userid'], $route->id);
        
        $this->changeToRouteView();
    }
    
    public function backToRoutes() {
        unset($_SESSION['track_trace_route_view']);
        unset($_SESSION['track_trace_route_view_id']);
    }
    
    public function showAddDestionation() {
        $_SESSION['track_trace_route_view'] = "addDestionation";
    }
    
    public function searchForCompanies() {
        
    }
    
    public function showEditDestionation() {
        $_SESSION['track_trace_route_view'] = "editdestination";
        $_SESSION['track_trace_route_view_destid'] = $_POST['data']['destionationid'];
    }
    
    public function addCompany() {
        $route = $this->getRoute();
        $this->getApi()->getTrackAndTraceManager()->addCompanyToRoute($route->id, $_POST['data']['companyid']);
        $this->changeToRouteView();
    }

    /**
     * 
     * @return \core_trackandtrace_Destination
     */
    public function getDestionation() {
        $route = $this->getRoute();
        foreach ($route->destinations as $destionation) {
            if ($destionation->id === $_SESSION['track_trace_route_view_destid']) {
                return $destionation;
            }
        }
        
        return null;
    }

    public function addDeliveryTask() {
        $task = new \core_trackandtrace_DeliveryTask();
        $task->cage = false;
        $task->quantity = 0;
        
        $destination = $this->getDestionation();
        $this->getApi()->getTrackAndTraceManager()->addDeliveryTaskToDestionation($destination->id, $task);
    }
    
    public function updateTasks() {
        $destionation = $this->getDestionation();
        foreach ($destionation->tasks as $task) {
            if (strstr($task->className, "DeliveryTask")) {
                $task->quantity = $_POST['data'][$task->id."_quantity"];
                $task->cage = $_POST['data'][$task->id."_cage"];
                $this->getApi()->getTrackAndTraceManager()->addDeliveryTaskToDestionation($destionation->id, $task);
            }
        }
    }
    
    public function saveRoute() {
        $route = $this->getRoute();
        $route->instruction = $_POST['data']['instruction'];
        $this->getApi()->getTrackAndTraceManager()->saveRoute($route);
    }
    
    public function deleteRoute() {
        $this->getApi()->getTrackAndTraceManager()->deleteRoute($_POST['data']['routeid']);
    }
    
    public function removeUser() {
        $route = $this->getRoute();
        $this->getApi()->getTrackAndTraceManager()->removeDriverToRoute($_POST['data']['userid'], $route->id);
        
        
    }
}
?>
