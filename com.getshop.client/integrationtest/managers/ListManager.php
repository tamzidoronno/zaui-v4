<?php
class ListManager extends TestBase {
    public function ListManager($api) {
        $this->api = $api;
    }

    /**
     * Add some entries to the list manager.
     * @addEntry
     */
    public function test_addEntry() {
        $api = $this->getApi();
        $listmanager = $api->getListManager();
        
        //Add the first entry.
        $entry = $this->getApiObject()->core_listmanager_data_Entry();
        $entry->name = "My entry 1";
        $entry = $listmanager->addEntry("my_awesome_list", $entry, "home");
        
        //Add a subentry attached to the first entry.
        $subentry = $this->getApiObject()->core_listmanager_data_Entry();
        $subentry->name = "My subentry";
        $subentry->parentId = $entry->id;
        $listmanager->addEntry("my_awesome_list", $subentry, "home");
        
        //Add a second entry
        $entry = $this->getApiObject()->core_listmanager_data_Entry();
        $entry->name = "My entry 2";
        $entry = $listmanager->addEntry("my_awesome_list", $entry, "home");
    }
    
    /**
     * Fetching an already created list is extremly easy.
     * Just call the getList argument.
     */
    public function test_getList() {
        $api = $this->getApi();
        $list = $api->getListManager()->getList("my_awesome_list");
    }
    
    /**
     * Let me show you how to update an already existing entry.
     */
    public function test_updateEntry() {
        //First just fetch the list to update one entry.
        $api = $this->getApi();
        $list = $api->getListManager()->getList("my_awesome_list");
        
        //Now update the first entry in the list.
        $list[0]->name = "New name";
        $api->getListManager()->updateEntry($list[0]);
    }
    
    /**
     * If you have the id for an entry you would like to fetch.
     * Then you are able to fetch this single entry only.
     * This might be useful when updating a given entry.
     */
    public function test_getListEntry() {
        //First just fetch the list to update find an entry to update.
        $api = $this->getApi();
        $list = $api->getListManager()->getList("my_awesome_list");
        
        $id = $list[0]->id;
        $entry = $api->getListManager()->getListEntry($id);
    }
    
    /**
     * Fetch all the lists created.
     */
    public function test_getLists() {
        $api = $this->getApi();
        $lists = $api->getListManager()->getLists();
        
        //Just for the fun of, fetch one of the lists.
        $list = $api->getListManager()->getList($lists[0]);
    }
    
    /**
     * If you need create a new id for a list then just do this.
     */
    public function test_createListId() {
        $api = $this->getApi();
        $id = $api->getListManager()->createListId();
    }
    
    /**
     * Need a human readable representation of a given entry id?
     * No problem, just do this.
     */
    public function test_translateEntries() {
        //First just fetch the list to update find an entry to update.
        $api = $this->getApi();
        $list = $api->getListManager()->getList("my_awesome_list");
        $id = $list[0]->pageId;
        $list = array();
        $list[] = $id;
        $text = $api->getListManager()->translateEntries($list);
    }
    
    /**
     * This example shows you how you can move entries in the list.
     */
    public function test_orderEntry() {
        //First fetch a couple of entries.
        //First just fetch the list to update find an entry to update.
        $api = $this->getApi();
        $list = $api->getListManager()->getList("my_awesome_list");
        $id1 = $list[0]->id;
        $id2 = $list[1]->id;
        
        //Move entry 1 after entry 2.
        $api->getListManager()->orderEntry($id1, $id2, "");
        $list = $api->getListManager()->getList("my_awesome_list");
    }

    /**
     * 
     */
    public function test_deleteEntry() {
        //First just fetch the list to update find an entry to update.
        $api = $this->getApi();
        $list = $api->getListManager()->getList("my_awesome_list");
        $id1 = $list[0]->id;
        $id2 = $list[1]->id;
        
        $api->getListManager()->deleteEntry($id1, "my_awesome_list");
        $api->getListManager()->deleteEntry($id2, "my_awesome_list");
        
        $list = $api->getListManager()->getList("my_awesome_list");
        
        //This is for integration testing.
        if(sizeof($list) > 0) {
            $api->transport->errors[] = "List manager where not delete entries";
        }
    }
    
    /**
     * Somethimes it is cool to append a different list to this list.<br>
     * For example if you would like to show all products in a different product list when listing this list.<br>
     */
    public function test_combineList() {
        $api = $this->getApi();
        
        $list = $api->getListManager()->getList("my_awesome_list");
        $different_list = $api->getListManager()->getList("another_list");
        //Add some entries to both of this lists... shee adEntry for more informatino about that
        
        //Combine them
        $api->getListManager()->combineList("my_awesome_list", "another_list");
        
        //Now both lists will be fetch.
        $list = $api->getListManager()->getList("my_awesome_list");
    }
    
    
    /**
     * When combining a list, it might be handy to remove a combination.
     */
    public function test_unCombineList() {
        $api = $this->getApi();
        
        $list = $api->getListManager()->getList("my_awesome_list");
        $different_list = $api->getListManager()->getList("another_list");
        //Add some entries to both of this lists... shee adEntry for more informatino about that
        
        //Combine them
        $api->getListManager()->combineList("my_awesome_list", "another_list");
        
        //Now decouple them
        $api->getListManager()->unCombineList("my_awesome_list", "another_list");
    }

    /**
     * If you combine a list, you can fetch all the combined lists to a given list.
     */
    public function test_getCombinedLists() {
         $api = $this->getApi();
        
        $list = $api->getListManager()->getList("my_awesome_list");
        $different_list = $api->getListManager()->getList("another_list");
        
        //Returns an array with the entry : another_list
        $lists = $api->getListManager()->getCombinedLists("my_awesome_list");
    }
    
}
?>
