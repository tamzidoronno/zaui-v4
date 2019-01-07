<?php
namespace ns_805bb430_bdcf_4db9_8d3a_f227ee6d1799;

class EmbedPmsBookingApp extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "EmbedPmsBookingApp";
    }

    public function saveendpoint() {
        $this->setConfigurationSetting("endpoint", $_POST['data']['endpoint']);
    }
    
    public function saveSettings() {
        $this->setConfigurationSetting("endpoint", $_POST['data']['endpoint']);
        $this->setConfigurationSetting("websocket", $_POST['data']['websocket']);
        $this->setConfigurationSetting("domain", $_POST['data']['domain']);
    }
    
    public function showSettings() {
        $this->includefile("settings");
    }
    
    public function render() {
        $addr = $this->getConfigurationSetting("endpoint");
        if(!$addr) {
            $this->includefile("setupbookingform");
            return;
        }
        $language = $this->getFactory()->getSelectedTranslation();
        $domain = $this->getConfigurationSetting("domain");
        if(!$domain) { $domain = "default"; }
        $websocket = $this->getConfigurationSetting("websocket");
        ?>
<div id='bookingprocess'><div style="text-align: center"><i class="fa fa-spinner"></i></div></div>

        <script>
        //    gsisdevmode = true;
            $ = jQuery;
            jQuery(function ($) {
                $.getScript("https://www.getshop.com/js/getshop.bookingembed.js", function (data, textStatus, jqxhr) {
                    $("#bookingprocess").getshopbooking(
                            {
                                "endpoint": "<?php echo $addr; ?>",
                                "jsendpoint": "<?php echo $addr; ?>",
                                "domain" : "<?php echo $domain; ?>",
                                <?php if($websocket) { echo "\"websocket\" : \"".$websocket."\",\n"; } ?>
                                "language" : "<?php echo $language; ?>"
                            }
                    );
                });
            });
        </script>
        <?php
    }
}
?>
