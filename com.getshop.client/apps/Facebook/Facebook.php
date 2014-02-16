<?php
namespace ns_ba885f72_f571_4a2e_8770_e91cbb16b4ad;

class Facebook extends \MarketingApplication implements \Application {
    public $singleton = true;

    //put your code here
    public function getDescription() {
        return $this->__("Tightly integrates facebook into your webshop, give users options to comment product, like products, share discounts, etc");
    }

    public function getName() {
        return "Facebook";
    }
    
    public function getYoutubeId() {
        return "K7xJbWta7-k";
    }

    public function postProcess() {
        
    }

    public function preProcess() {
        
    }

    public function render() {
        
    }
    
    public function renderBottom() {
        $appid = "";
        if (isset($this->getConfiguration()->settings->facebookappid)) {
            $value = $this->getConfiguration()->settings->facebookappid->value;
            if ($value != "") {
                $appid ="&appid=".$value;
            }
        }
        
        echo '<div id="fb-root"></div>
        <script>(function(d, s, id) {
              var js, fjs = d.getElementsByTagName(s)[0];
              if (d.getElementById(id)) return;
              js = d.createElement(s); js.id = id;
              js.src = "//connect.facebook.net/en_GB/all.js#xfbml=1'.$appid.'";
              fjs.parentNode.insertBefore(js, fjs);
            }(document, \'script\', \'facebook-jssdk\'));
            
            $(document).ajaxComplete(function(){
                try{
                    FB.XFBML.parse(); 
                }catch(ex){}
            });
        
        </script>';
    }

    /**
     * Should display you configuration page.
     */
    public function renderConfig() {
        $this->includefile("facebookconfig");
    }
    
    function curPageURL() {
        $pageURL = 'http';
        if (isset($_SERVER["HTTPS"]) && $_SERVER["HTTPS"] == "on") {$pageURL .= "s";}
        $pageURL .= "://";
        if ($_SERVER["SERVER_PORT"] != "80") {
         $pageURL .= $_SERVER["SERVER_NAME"].":".$_SERVER["SERVER_PORT"];
        } else {
            $pageURL .= $_SERVER["SERVER_NAME"];
        }
        return $pageURL;
    }

    public function productAboveShortDesc($product) {
        if ($this->getConfiguration()->settings->likebutton->value != "true") {
            return;
        }
        
        $page = $this->curPageURL()."/?page=".$this->getPage()->id;
        echo '<div class="fb-like" data-href="'.$page.'" data-send="false" data-width="450" data-show-faces="true"></div>';
    }
    
}

?>
