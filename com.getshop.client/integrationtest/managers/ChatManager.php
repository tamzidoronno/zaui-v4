<?php

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Description of ChatManager
 *
 * @author ktonder
 */
class ChatManager {
    /** @var GetShopApi - api from getshop */
    private $api;
    
    function __construct($api) {
        $this->api = $api;
    }
    
    /**
     * We want to send a message to 
     * anyone that listens for messages. This is normally administrators
     * of the webshop.
     */
    public function test_sendMessage() {
        $this->api->getChatManager()->sendMessage("Hello");
    }

    /**
     * Retreive all messages for the current logged in user.
     * 
     */
    public function test_getMessages() {
        $this->api->getChatManager()->sendMessage("Next message");
        $messages = $this->api->getChatManager()->getMessages(); 
       /**
        * The messages in the list is the messages admins have
        * answered and the messages that has been sent by using sendMessage($string);
        */
        
        assert(count($messages) == 2);
    }
    
    /**
     * If you are logged in as an administrator,
     * it is possible to get all active chatters 
     * from the API
     */
    public function test_getChatters() {
        $chatters = $this->api->getChatManager()->getChatters();
        assert(count($chatters) == 1);
    }
    
    /**
     * As an administrator, you want to reply to a specific client.
     * This is done by using the replyToChat($id, $message);
     */
    public function test_replyToChat() {
        $chatters = $this->api->getChatManager()->getChatters();
        assert(is_string($chatters[0]->id));
        
        $chatter = $chatters[0];
        $this->api->getChatManager()->replyToChat($chatter->id, "Replying to your message now");
        $messages = $this->api->getChatManager()->getMessages(); 
        
        assert(count($messages) == 3);
    }
    
    /**
     * When a chat is done, you wish to store the conversation to
     * the database and get it out of your active list.
     * You do that by using the closeChat 
     */
    public function test_closeChat() {
        $chatters = $this->api->getChatManager()->getChatters();
        assert(is_string($chatters[0]->id));
        
        $this->api->getChatManager()->closeChat($chatters[0]->id);
        
        $messages = $this->api->getChatManager()->getMessages(); 
        assert(count($messages) == 0);
    }
}

?>
