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
        if (isset($_POST['data']['instruction'])) {
            $route->instruction = $_POST['data']['instruction'];
        }
        if (isset($_POST['data']['deliveryServiceDate'])) {
            $deliveryServiceDate = $this->convertToJavaDate(strtotime($_POST['data']['deliveryServiceDate']));
//            $route->deliveryServiceDate = $deliveryServiceDate;
        }
        $this->getApi()->getTrackAndTraceManager()->saveRoute($route);
    }
    
    public function deleteRoute() {
        $this->getApi()->getTrackAndTraceManager()->deleteRoute($_POST['data']['routeid']);
    }
    
    public function removeUser() {
        $route = $this->getRoute();
        $this->getApi()->getTrackAndTraceManager()->removeDriverToRoute($_POST['data']['userid'], $route->id);
        
        
    }
    
    public function renderUserSettings($user) {
        $this->user = $user;
        $this->includefile("usersetting");
    }
    
    /**
     * @return \core_usermanager_data_User
     */
    public function getCurrentUser() {
        return $this->user;
    }
    
    public function saveUser() {
        $this->setUser();
        $this->getApi()->getUserManager()->addMetaData($this->user->id, "depotId", $_POST['depotId']);
    }
    
    public function setUser() {
        if (isset($_POST['userid'])) {
            $this->user = $this->getApi()->getUserManager()->getUserById($_POST['userid']);
        }
    }
    
    public function toggleSortByDeliveryDate() {
        if (isset($_SESSION['sorting'])) {
            unset($_SESSION['sorting']);
        } else {
            $_SESSION['sorting'] = "servicedatedescending";
        }
    }

    public static function sortByName($a, $b) {
        $routeA = substr($a->name, 0, 4);
        $routeB = substr($b->name, 0, 4);
        
        if ($routeA == $routeB) {
            if (!$a->deliveryServiceDate) {
                return 1;
            }
            if (!$b->deliveryServiceDate) {
                return -1;
            }
            $time1 = strtotime($a->deliveryServiceDate);
            $time2 = strtotime($b->deliveryServiceDate);
            return $time2 - $time1;
        }
        return strcmp($routeA, $routeB);
    }
    
    public static function sortByServiceDate($a, $b) {
        if (!$a->deliveryServiceDate) {
            return 1;
        }
        if (!$b->deliveryServiceDate) {
            return -1;
        }

        $time1 = strtotime($a->deliveryServiceDate);
        $time2 = strtotime($b->deliveryServiceDate);
        
        if ($time1 == $time2) {
            return strcmp($a->name, $b->name);
        }
        return $time2 - $time1;
    }
    
    public function getSortingFunction() {
        if (isset($_SESSION['sorting']) && $_SESSION['sorting'] == "servicedatedescending") {
            return array("ns_5ebb7b42_35b7_48d3_b047_825dc2b30b5f\TrackAndTraceRouteManagement", "sortByServiceDate");    
        }
        
        return array("ns_5ebb7b42_35b7_48d3_b047_825dc2b30b5f\TrackAndTraceRouteManagement", "sortByName");
    }

}
?>
