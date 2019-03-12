<?php
namespace ns_39f77b93_59f5_4178_9963_34ba254aea42;

class ComfortStateSettings extends \WebshopApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "ComfortStateSettings";
    }

    public function render() {
        $this->includefile("configurationpanel");
        $this->includefile("addededstates");
    }
    
    public function updateState() {
        $state = $this->getApi()->getComfortManager()->getState($_POST['data']['stateid']);
        $state->lightShutDown = $_POST['data']['lightShutDown'];
        $state->event = $_POST['data']['event'];
        $state->temperature = $_POST['data']['temperature'];
        $this->getApi()->getComfortManager()->saveState($state);
    }
    

    public function removeConfig() {
        $this->getApi()->getComfortManager()->deleteState($_POST['data']['stateid']);
    }
    
    public function createState() {
        $this->getApi()->getComfortManager()->createState($_POST['data']['statename']);
    }
}
?>
