<?php
namespace ns_e9864616_96d6_485f_8cb0_e17cdffbcfec;

class WebShopList extends \SystemApplication implements \Application {
    public function getDescription() {
        return "";
    }

    public function getName() {
        return "WebShop list";
    }

    public function render() {
        $stores = $this->getApi()->getUserManager()->getStoresConnectedToMe();
        
        if(!$stores) {
            echo $this->__f("You have not yet created any web shops, create one and they will be listed here.");
            return;
        }
        
        echo "<table width='100%' id='storelist'>";
        echo "<thead>";
        echo "<tr>";
        echo "<th>" . $this->__f("Created when") ."</th>";
        echo "<th>" . $this->__f("Web address") ."</th>";
        echo "<th>" . $this->__f("Last logged on") ."</th>";
        echo "<th>" . $this->__f("Chrome?") ."</th>";
        echo "<th>" . $this->__f("UA") ."</th>";
        echo "</tr>";
        echo "</thead>";
        
        echo "<tbody>";
        foreach($stores as $store) {
            /* @var $store core_getshop_data_GetshopStore */
            echo "<tr>";
            echo "<td>" . $store->created . "</td>";
            echo "<td>" . $store->webaddress . " (" . $store->username  . ")". "</td>";
            echo "<td>" . $store->lastLoggedIn . "</td>";
            echo "<td>";
            if($store->hasChrome) {
                echo "yes";
            } else {
                echo "no";
            }
            echo "</td>";
            echo "<td>";
            foreach($store->userAgents as $ua) {
//                echo $ua;
                if(stristr($ua, "msie")) {
                    echo "<span title='$ua'>I</span> ";
                } else if(stristr($ua, "chrome")) {
                    echo "<span title='$ua'>C</span> ";
                } else if(stristr($ua, "opera")) {
                    echo "<span title='$ua'>O</span> ";
                } else if(stristr($ua, "firefox")) {
                    echo "<span title='$ua'>F</span> ";
                } else if(stristr($ua, "safari")) {
                    echo "<span title='$ua'>S</span> ";
                }
            }
            echo "</td>";
            echo "</tr>";
        }
        echo "</tbody>";
        echo "</table>";
        echo "<script>";
        echo "$('#storelist').dataTable({
            \"bJQueryUI\": true
            });";
        echo "</script>";
    }
}

?>
