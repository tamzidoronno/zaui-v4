<?php

class CalendarManager extends TestBase {

    public function CalendarManager($api) {
        $this->api = $api;
    }
    
    /**
     * Add your first entry to the calendar at 2015-01-01
     */
    public function test_createEntry() {
        $manager = $this->getApi()->getCalendarManager();
        
        $entry = $manager->createEntry(2015, 01, 01);
    }
    
    /**
     * Delete a given entry.
     */
    public function test_deleteEntry() {
        $manager = $this->getApi()->getCalendarManager();
        
        //First create an entry to delete.
        $entry = $manager->createEntry(2014, 01, 01);
        
        //And just delete it.
        $manager->deleteEntry($entry->entryId);
    }
    
    /**
     * Add a user to an event.
     */
    public function test_addUserToEvent() {
        $manager = $this->getApi()->getCalendarManager();
        
        //First create where a user can be attached to.
        $entry = $manager->createEntry(2014, 01, 01); 
        
        //Make sure there is room for this user.
        $entry->maxAttendees = 1;
        $manager->saveEntry($entry);
        
        //Create a user to attach.
        $user = $this->getApiObject()->core_usermanager_data_User();
        $user->emailAddress = "test@getshop.com"; //Need a valid email address.
        $user = $this->getApi()->getUserManager()->createUser($user);
        
        
        //Add a user to an event.
        $manager->addUserToEvent($user->id, 
                $entry->entryId, 
                "somepassword", 
                "someusername");
    }

    
    /**
     * It is fully possible to fetch all entries for a given day.
     */
    public function test_getEntries() {
        $manager = $this->getApi()->getCalendarManager();
        
        //First create a couple of entries.
        $manager->createEntry(2014, 01, 01); 
        $manager->createEntry(2014, 01, 01); 
        
        //Now fetch all entries created to the day.
        $entries = $manager->getEntries(2014, 01, 01);
    }
    
    /**
     * Only got the entryid, and need to fetch the entry?
     */
    public function test_getEntry() {
        $manager = $this->getApi()->getCalendarManager();
        
        //First create an entry.
        $entry = $manager->createEntry(2014, 01, 01); 
        
        //Fetch it
        $fetched_entry = $manager->getEntry($entry->entryId);
    }
    
    /**
     * example on : Fetch a month by giving a year / day.
     */
    public function test_getMonth() {
        $manager = $this->getApi()->getCalendarManager();
        
        //Fetch a month.
        $month = $manager->getMonth(2014, 01, true);
    }
    
    /**
     * Remove a user from a given event
     */
    public function test_removeUserFromEvent() {
        $manager = $this->getApi()->getCalendarManager();
        
        //First create where a user can be attached to.
        $entry = $manager->createEntry(2014, 01, 01); 
        
        //Make sure there is room for this user.
        $entry->maxAttendees = 1;
        $manager->saveEntry($entry);
        
        //Create a user to attach.
        $user = $this->getApiObject()->core_usermanager_data_User();
        $user->emailAddress = "test@getshop.com"; //Need a valid email address.
        $user = $this->getApi()->getUserManager()->createUser($user);
        
        
        //Add a user to an event.
        $manager->addUserToEvent($user->id, 
                $entry->entryId, 
                "somepassword", 
                "someusername");
        
        
        //Now remove it
        $manager->removeUserFromEvent($user->id, $entry->entryId);
    }
    
    /**
     * Send reminders to a list of users.
     */
    public function test_sendReminderToUser() {
        $manager = $this->getApi()->getCalendarManager();
        
        //Fill this array with userids 
        //(see the usernamer about how to create users)
        $userList = array();
        
        $manager->sendReminderToUser(false,
                false,
                $userList,
                "Some text",
                "Some subject");
    }
    
    /**
     * Save an already existing entry
     */
    public function test_saveEntry() {
        $manager = $this->getApi()->getCalendarManager();
        
        //First create an entry.
        $entry = $manager->createEntry(2015, 01, 01);
        
        $entry->description = "A new description";
        $manager->saveEntry($entry);
    }
    
    /**
     * If an administrator / editor need to confirm the entry in the calendar.
     * This might be due to someone else which is not editor has tried to add an entry
     * to the calendar. Then the editor need to confirm that this entry is valid.
     */
    public function test_confirmEntry() {
        $manager = $this->getApi()->getCalendarManager();
        
        //First create an entry.
        $entry = $manager->createEntry(2015, 01, 01);
        $manager->confirmEntry($entry->entryId);
    }
    
}

?>
