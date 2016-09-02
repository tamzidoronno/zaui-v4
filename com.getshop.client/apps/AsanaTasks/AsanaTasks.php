<?php
namespace ns_8f873890_b872_421c_8294_77fc2af9be38;

class AsanaTasks extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "AsanaTasks";
    }

    public function render() {
        $this->includefile("tasks");
    }
    
    public function loadTasks() {
    }

    public function getHours($task) {
        foreach ($task->custom_fields as $field) {

            if ($field->name == "hours")
                return $field->number_value !== null ? $field->number_value : 0;
        }
                
        return 0;
    }

    public function getMinutes($task) {
        foreach ($task->custom_fields as $field) {

            if ($field->name == "minutes")
                return $field->number_value !== null ? $field->number_value : 0;
        }
    }

}
?>
