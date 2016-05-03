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
            echo "<div class='line_$i line'>$line</div>";
            $i++;
        }
        $config = array();
        $config['scrollstart'] = (int)$this->getConfigurationSetting("scrollstart");
        $config['timer'] = (int)$this->getConfigurationSetting("timer");
        ?>
        <script>
            var magictextconfig = <?php echo json_encode($config); ?>;
            $(document).bind('DOMMouseScroll', function(e){
                if(e.originalEvent.detail > 0) {
                    app.MagicText.doScrollText(false, '<?php echo $this->getAppInstanceId(); ?>', magictextconfig);
                }else {
                    app.MagicText.doScrollText(true, '<?php echo $this->getAppInstanceId(); ?>', magictextconfig);
                }
           });

           $(document).bind('mousewheel', function(e){
               if(e.originalEvent.wheelDelta < 0) {
                    app.MagicText.doScrollText(false, '<?php echo $this->getAppInstanceId(); ?>', magictextconfig);
               }else {
                    app.MagicText.doScrollText(true, '<?php echo $this->getAppInstanceId(); ?>', magictextconfig);
               }
           });
           $(function() {
               app.MagicText.doScrollText(true, '<?php echo $this->getAppInstanceId(); ?>', magictextconfig);
           });
        </script>
        <?php
    }
}
?>
