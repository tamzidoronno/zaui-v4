<?php
namespace ns_89a38877_4b75_456c_9dc8_a55ff0e7dfef;

class ServerList extends \WebshopApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "ServerList";
    }

    public function render() {
        echo "<div class='serverlist'>";
        $this->includefile("serverlist");
        echo "</div>";
    }

    public function printServerList($list, $type) {
        echo "<table width='100%'>";
        foreach($list->entries as $l) {
            if($type != $l->status) {
                continue;
            }
            echo "<tr>";
            echo "<td>".$l->givenName."</td>";
            echo "<td>".$l->hostname."</td>";
            echo "<td>".$l->status."</td>";
            echo "<td>".date("d.m.Y H:i", strtotime($l->lastPing))."</td>";
            echo "<td>".$l->webaddr."</td>";
            echo "</tr>";
        }
        echo "</table>";

    }

}
?>
