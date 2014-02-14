<?php
namespace ns_2afb045b_fa01_4398_8582_33f212bb8db8;

use \MarketingApplication;
use \Application;

class Chat extends MarketingApplication implements Application {
    public $singleton = true;
    private $messages = array();
    
    public function getDescription() {
        return $this->__f("Answering your customers questions, means more sale. By adding this excellent chat application, you can answer on request at any time from you mobile devices or your stationary computer.");   
    }

    public function getName() {
        return $this->__f("Chat");
    }

    public function render() {
        $this->includefile("standalonechat");
    }
    
    public function renderMobile() {
        $this->includefile("mobile_main");
    }
    
    public function printMobileChat() {
        /* @var $this ns_2afb045b_fa01_4398_8582_33f212bb8db8\Chat */
        $chatManager = $this->getApi()->getChatManager();
        $chatManager->pingMobileChat($_POST['data']['chatterid']);
        $chatters = $this->getChatters();
        foreach($chatters as $chatter) {
            /* @var $chatter core_chatmanager_Chatter */
            if($chatter->id == $_POST['data']['chatterid']) {
                $messages = $chatter->messages;
                foreach($messages as $msg) {
                    echo "<div style='border: solid 1px; margin-top: 10px; padding: 5px; border-radius: 5px; background-color:#FFFFFF;' class='chatmessage'>";
                    echo "<div style='text-align:center'>" . $msg->rowCreatedDate . "</div>";
                    echo $msg->message . "<br>";
                    echo "</div>";
                }
           }
        }
    }
    
    
    public function renderStandalone() {
    }
    
    public function renderConfig() {
    }
  
    public function sendMessage() {
        $message = htmlentities($_POST['data']['text'], ENT_QUOTES, "UTF-8");
        $this->getApi()->getChatManager()->sendMessage($message);
        $this->renderContent();
    }
  
    public function sendGreeting() {
        $chatterid = $_POST['data']['chatterid'];
        $name = $this->getApi()->getUserManager()->getLoggedOnUser()->fullName;
        $this->getApi()->getChatManager()->replyToChat($chatterid, "You are now talking to" . " " . $name);
    }
    
    public function renderContent() {
        $this->loadMessages();
        $this->includefile("dialog");
    }
    
    private function loadMessages() {
        $this->messages = $this->getApi()->getChatManager()->getMessages();
    }
    
    public function getChatters() {
        return $this->getApi()->getChatManager()->getChatters();
    }
    
    public function renderBottom() {
        $this->loadMessages();
        $this->includefile("chatwindow");
        echo "<script>";
        echo 'com.getshop.app.Chat.scrollBottom();';
        echo "</script>";
    }
    
    public function getMessages() {
        return $this->messages;
    }

    private function getChatDialog() {
        ob_start();
        $this->includefile("dialog");
        $html = ob_get_contents();
        ob_end_clean();
        return $html;
    }
    
    private function getLeftMenuChatters($chatters) {
        $retchatters = array();
        
        foreach ($chatters as $chatter) {
            $retchatter['id'] = $chatter->id;
            $retchatter['username'] = $chatter->username;
            $retchatter['messages'] = count($chatter->messages);
            $retchatters[] = $retchatter;
        }
        
        return $retchatters;
    }
    
    public function showChatter() {
        $chatters = $this->getApi()->getChatManager()->getChatters();
        foreach ($chatters as $chatter) {
            if ($chatter->id == $_POST['data']['chatterid']) {
                $this->messages = $chatter->messages;
            }
        }
        
        $data = array();
        $data['chatdata'] = $this->getChatDialog();
        $data['chatters'] = $this->getLeftMenuChatters($chatters);
        
        echo json_encode($data);
        die;
    }
    
    public function replyMessage() {
        $chatterid = $_POST['data']['chatterid'];
        $message = $_POST['data']['text'];
        $this->getApi()->getChatManager()->replyToChat($chatterid, $message);
    }
    
    public function closeChat() {
        $this->getApi()->getChatManager()->closeChat($_POST['data']['chattrid']);
    }
}
?>