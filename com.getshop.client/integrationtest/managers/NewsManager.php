<?php
class NewsManager extends TestBase {
    public function NewsManager($api) {
        $this->api = $api;
    }
    
    /**
     * Adding news is awesome!
     */
    public function test_addNews() {
        $manager = $this->getApi()->getNewsManager();
        
        $news = "My first news entry!";
        $newsId = $manager->addNews($news);
    }
    
    /**
     * You wish to add a subscriber to the news?
     */
    public function test_addSubscriber() {
        $manager = $this->getApi()->getNewsManager();
        
        $email = "email@to.subscriber";
        $manager->addSubscriber($email);
    }
    
    /**
     * Removing news is simple.
     */
    public function test_deleteNews() {
        $manager = $this->getApi()->getNewsManager();
     
        //First just add a news you can delete.
        $news = "My first news entry!";
        $newsId = $manager->addNews($news);
        
        $manager->deleteNews($newsId);
    }
    
    /**
     * It might be useful for you to fetch all the news added.
     */
    public function test_getAllNews() {
        $manager = $this->getApi()->getNewsManager();
        
        $allNews = $manager->getAllNews();
        foreach($allNews as $news) {
            /* @var $news app_newsmanager_data_NewsEntry */
        }
    }
    
    /**
     * Fetch all subscribers added.
     */
    public function test_getAllSubscribers() {
        $manager = $this->getApi()->getNewsManager();
        $subscribers = $manager->getAllSubscribers();
        
        foreach($subscribers as $subscriber) {
            /* @var $subscriber app_news_data_MailSubscription */
        }
    }
    
    /**
     * Remove a given subscriber.
     */
    public function test_removeSubscriber() {
        $manager = $this->getApi()->getNewsManager();
        
        //First just add one.
        $email = "subscriber@to.remove";
        $subscriber = $manager->addSubscriber($email);
        
        //And now, remove it.
        $manager->removeSubscriber($subscriber->id);
    }
}
?>
