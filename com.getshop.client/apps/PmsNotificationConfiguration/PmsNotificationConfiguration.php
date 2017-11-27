<?php
namespace ns_f29c2731_2574_4ddf_80d1_0bb9ecee3979;

class PmsNotificationConfiguration extends \WebshopApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "PmsNotificationConfiguration";
    }

    public function render() {
        $this->includefile("configurationpanel");
    }
    
    public function saveNotifications() {
        $types = array();
        $types["report"] = "Emails to send statistics report to";
        $types["applogentry"] = "Emails to send app log entries to";
        $types["creditorder"] = "When orders are credited";
        $types["caretaker"] = "When caretaker tasks are added";
        $config = $this->getApi()->getPmsManager()->getConfiguration($this->getSelectedMultilevelDomainName());

        foreach($types as $type => $heading) {
            for($i = 1; $i <= 10; $i++) {
                $config->emailsToNotify->{$type}[$i-1] = $_POST['data']["emailtonotify_".$type."_".$i];
            }
        }
        $this->getApi()->getPmsManager()->saveConfiguration($this->getSelectedMultilevelDomainName(), $config);
    }
}
?>
