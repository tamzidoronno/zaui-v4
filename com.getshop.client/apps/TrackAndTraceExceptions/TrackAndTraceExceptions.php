<?php
namespace ns_ba031207_4b03_4248_a0dd_9c2d150a5679;

class TrackAndTraceExceptions extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "TrackAndTraceExceptions";
    }

    public function render() {
        if ($this->getModalVariable("type")) {
            $this->includefile("createException");
        } else {
            $this->includefile("exceptionlist");
        }   
    }
    
    public function updateSequence() {
        $this->getApi()->getTrackAndTraceManager()->setSequence($_POST['data']['expid'], $_POST['data']['gsvalue']);
    }
    
    public function createException() {
        $ex = new \core_trackandtrace_TrackAndTraceException();
        
        if ($_POST['data']['exid']) {
            $ex = $this->getException($_POST['data']['exid']);
        }
        
        $ex->type = $_POST['data']['type'];
        $ex->name = $_POST['data']['name'];
        
        $this->getApi()->getTrackAndTraceManager()->saveException($ex);
    }

    public function getException($exid) {
        $exceptions = $this->getApi()->getTrackAndTraceManager()->getExceptions();
        foreach ($exceptions as $ex) {
            if ($ex->id === $exid) {
                return $ex;
            }
        }
        
        return null;
    }

}
?>
