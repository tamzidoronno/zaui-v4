<?php
namespace ns_d02c93ec_ce45_450d_a58d_bbdb15c0830b;

class PmsGuestCounter extends \WebshopApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "PmsGuestCounter";
    }

    public function render() {
        echo "<div id='pmsguestcounter'>";
        $this->getContent();
        echo "</div>";
        ?>
        <script>
            setInterval(function() {
                var event = thundashop.Ajax.createEvent('','getContent', $('#pmsguestcounter'), {});
                thundashop.Ajax.postWithCallBack(event, function(res) {
                    $('#pmsguestcounter').html(res);
                }, true);
            }, "5000");
        </script>
        <?php
    }
    
    public function getContent() {
         $content = file_get_contents("http://10.0.3.95:8086/query?u=root&p=root&db=pmsmanager&q=SELECT%20sum(%22guests%22)%20FROM%20%22booking%22%20WHERE%20time%20%3E%201358279144s%20and%20time%20%3C%20now()%20GROUP%20BY%20time(3650d)%20fill(0)&epoch=ms");
        $content = json_decode($content,true);

        echo "<center>";
        echo "<h3>Since august 2016 have <b style='color:#98048c; font-size: 32px;padding: 10px;'>" . $content['results'][0]['series'][0]['values'][0][1] . "</b> guests put their trust in the GetShops automated check in system.</h3>";
        echo "</center>";
    }
}
?>
