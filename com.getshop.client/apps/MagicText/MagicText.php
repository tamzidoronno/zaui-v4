<?php
namespace ns_9d395292_5ef8_4864_a9b1_949eabafa4f4;

class MagicText extends \WebshopApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "MagicText";
    }

    public function render() {
        $text = $this->getConfigurationSetting("text");
        if(!$text) {
            echo "No text set";
        }
        $lines = explode("\n", $text);
        $i = 1;
        foreach($lines as $line) {
            if(!$line) {
                $line = "&nbsp;";
            }
            echo "<div class='line_$i line'>$line</div>";
            $i++;
        }
        $config = array();
        $config['scrollstart'] = (int)$this->getConfigurationSetting("scrollstart");
        $config['timer'] = (int)$this->getConfigurationSetting("timer");
        if(!$this->getConfigurationSetting("scrollstart")) {
            return;
        }
        ?>


        <script>
            var height = 10;
            
            if(typeof(magictextconfig) === "undefined") {
                magictextconfig = {};
            }
            var width = $(window).width();
            var gsmagicsizeToSet = (width * 0.00052083333)  * <?php echo $this->getConfigurationSetting("magicsize"); ?>;

            if(gsmagicsizeToSet < 14) {
                gsmagicsizeToSet = 16;
            }
            
            $('.app[appid="<?php echo $this->getAppInstanceId(); ?>"]').find('.line').css('font-size', gsmagicsizeToSet);

            <?php 
            if($this->getFactory()->isMobileIgnoreDisabled()) {
                ?>
                    $('.app[appid="<?php echo $this->getAppInstanceId(); ?>"]').find('.line').css('opacity','1');
                    </script>
                <?php
                return;
            }
            ?>
                
            magictextconfig['<?php echo $this->getAppInstanceId(); ?>'] = <?php echo json_encode($config); ?>;
            $(document).bind('DOMMouseScroll', function(e){
                if(e.originalEvent.detail > 0) {
                    app.MagicText.doScrollText(false, '<?php echo $this->getAppInstanceId(); ?>', magictextconfig['<?php echo $this->getAppInstanceId(); ?>']);
                }else {
                    app.MagicText.doScrollText(true, '<?php echo $this->getAppInstanceId(); ?>', magictextconfig['<?php echo $this->getAppInstanceId(); ?>']);
                }
           });

           $(document).bind('mousewheel', function(e){
               if(e.originalEvent.wheelDelta < 0) {
                    app.MagicText.doScrollText(false, '<?php echo $this->getAppInstanceId(); ?>', magictextconfig['<?php echo $this->getAppInstanceId(); ?>']);
               }else {
                    app.MagicText.doScrollText(true, '<?php echo $this->getAppInstanceId(); ?>', magictextconfig['<?php echo $this->getAppInstanceId(); ?>']);
               }
           });
           $(function() {
               app.MagicText.doScrollText(true, '<?php echo $this->getAppInstanceId(); ?>', magictextconfig['<?php echo $this->getAppInstanceId(); ?>']);
           });
           
           PubSub.subscribe("GSPAGEANIMATE_COMPLETED", function() {
               app.MagicText.doScrollText(true, '<?php echo $this->getAppInstanceId(); ?>', magictextconfig['<?php echo $this->getAppInstanceId(); ?>']);
           });
        </script>
        <?php
    }
}
?>
