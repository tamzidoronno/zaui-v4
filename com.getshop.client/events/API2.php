<?php

/**
 * This library is built by GetShop and are used to communicate with the GetShop backend. 
 * License: GPLv2
 * License URI: http://www.gnu.org/licenses/gpl-2.0.html
*/

class APIBannerManager {

	var $transport;
	
	function APIBannerManager($transport) {
		$this->transport = $transport;
	}

	/**
	* Add a new image to an existing bannerset.<br>
	* The fileid is just an identifier and should be generated / stored by the one calling this application<br>
	*
	* @param id The id to save on.
	* @param fileId The given file id for this image, fetched from the filemanager when uploading the image.
	* @return app_bannermanager_data_BannerSet
	* @throws ErrorException
	*/

	public function addImage($id, $fileId) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["id"] = json_encode($this->transport->object_unset_nulls($id));
	     $data['args']["fileId"] = json_encode($this->transport->object_unset_nulls($fileId));
	     $data["method"] = "addImage";
	     $data["interfaceName"] = "app.banner.IBannerManager";
	     return $this->transport->cast(new app_bannermanager_data_BannerSet(), $this->transport->sendMessage($data));
	}

	/**
	* Initiate / create a new bannerset.
	* @param width The width for the banners
	* @param height The height for the banners.
	* @param id Specify an id if you want to override the id generation, leave empty otherwhise.
	* @return app_bannermanager_data_BannerSet
	* @throws ErrorException
	*/

	public function createSet($width, $height, $id) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["width"] = json_encode($this->transport->object_unset_nulls($width));
	     $data['args']["height"] = json_encode($this->transport->object_unset_nulls($height));
	     $data['args']["id"] = json_encode($this->transport->object_unset_nulls($id));
	     $data["method"] = "createSet";
	     $data["interfaceName"] = "app.banner.IBannerManager";
	     return $this->transport->cast(new app_bannermanager_data_BannerSet(), $this->transport->sendMessage($data));
	}

	/**
	* Delete a given banner set.
	*
	* @param id The id for the bannerset to delete.
	* @return app_bannermanager_data_BannerSet
	* @throws ErrorException
	*/

	public function deleteSet($id) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["id"] = json_encode($this->transport->object_unset_nulls($id));
	     $data["method"] = "deleteSet";
	     $data["interfaceName"] = "app.banner.IBannerManager";
	     return $this->transport->cast(new app_bannermanager_data_BannerSet(), $this->transport->sendMessage($data));
	}

	/**
	* Fetch an existing bannerset.
	* @param id The id for the bannerset to fetch, if the id does not exists, it will be created.
	* @return app_bannermanager_data_BannerSet
	* @throws ErrorException
	*/

	public function getSet($id) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["id"] = json_encode($this->transport->object_unset_nulls($id));
	     $data["method"] = "getSet";
	     $data["interfaceName"] = "app.banner.IBannerManager";
	     return $this->transport->cast(new app_bannermanager_data_BannerSet(), $this->transport->sendMessage($data));
	}

	/**
	* Add a product to a given image.
	* @param bannerSetId The id of the bannerset which holds the images.
	* @param imageId The image id to link a product to.
	* @param productId The id of the product to be linked.
	* @return app_bannermanager_data_BannerSet
	* @throws ErrorException
	*/

	public function linkProductToImage($bannerSetId, $imageId, $productId) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["bannerSetId"] = json_encode($this->transport->object_unset_nulls($bannerSetId));
	     $data['args']["imageId"] = json_encode($this->transport->object_unset_nulls($imageId));
	     $data['args']["productId"] = json_encode($this->transport->object_unset_nulls($productId));
	     $data["method"] = "linkProductToImage";
	     $data["interfaceName"] = "app.banner.IBannerManager";
	     return $this->transport->cast(new app_bannermanager_data_BannerSet(), $this->transport->sendMessage($data));
	}

	/**
	* Remove an already existing image from a given bannerset.
	* @param bannerSetId The id for the bannerset.
	* @param fileId The file id to remove.
	* @return app_bannermanager_data_BannerSet
	* @throws ErrorException
	*/

	public function removeImage($bannerSetId, $fileId) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["bannerSetId"] = json_encode($this->transport->object_unset_nulls($bannerSetId));
	     $data['args']["fileId"] = json_encode($this->transport->object_unset_nulls($fileId));
	     $data["method"] = "removeImage";
	     $data["interfaceName"] = "app.banner.IBannerManager";
	     return $this->transport->cast(new app_bannermanager_data_BannerSet(), $this->transport->sendMessage($data));
	}

	/**
	* Update a given bannerset.<br>
	* If the id of the bannerset does not exists, it will automatically create one for you.<br>
	*
	* @param set The bannerset to save.
	* @return app_bannermanager_data_BannerSet
	* @throws ErrorException
	*/

	public function saveSet($app_bannermanager_data_BannerSet) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["app_bannermanager_data_BannerSet"] = json_encode($this->transport->object_unset_nulls($app_bannermanager_data_BannerSet));
	     $data["method"] = "saveSet";
	     $data["interfaceName"] = "app.banner.IBannerManager";
	     return $this->transport->cast(new app_bannermanager_data_BannerSet(), $this->transport->sendMessage($data));
	}

}
class APIBigStock {

	var $transport;
	
	function APIBigStock($transport) {
		$this->transport = $transport;
	}

	/**
	* Update the credit account.
	*
	* @param credits
	* @param password
	* @throws ErrorException
	*/

	public function addGetShopImageIdToBigStockOrder($downloadUrl, $imageId) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["downloadUrl"] = json_encode($this->transport->object_unset_nulls($downloadUrl));
	     $data['args']["imageId"] = json_encode($this->transport->object_unset_nulls($imageId));
	     $data["method"] = "addGetShopImageIdToBigStockOrder";
	     $data["interfaceName"] = "core.bigstock.IBigStock";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Update the credit account.
	*
	* @param credits
	* @param password
	* @throws ErrorException
	*/

	public function getAvailableCredits() {
	     $data = array();
	     $data['args'] = array();
	     $data["method"] = "getAvailableCredits";
	     $data["interfaceName"] = "core.bigstock.IBigStock";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Purchases a picture from the bigstock library.
	*
	* @param imageId
	* @param sizeCode
	* @return String
	* @throws ErrorException
	*/

	public function purchaseImage($imageId, $sizeCode) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["imageId"] = json_encode($this->transport->object_unset_nulls($imageId));
	     $data['args']["sizeCode"] = json_encode($this->transport->object_unset_nulls($sizeCode));
	     $data["method"] = "purchaseImage";
	     $data["interfaceName"] = "core.bigstock.IBigStock";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Update the credit account.
	*
	* @param credits
	* @param password
	* @throws ErrorException
	*/

	public function setCreditAccount($credits, $password) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["credits"] = json_encode($this->transport->object_unset_nulls($credits));
	     $data['args']["password"] = json_encode($this->transport->object_unset_nulls($password));
	     $data["method"] = "setCreditAccount";
	     $data["interfaceName"] = "core.bigstock.IBigStock";
	     return $this->transport->sendMessage($data);
	}

}
class APIBrainTreeManager {

	var $transport;
	
	function APIBrainTreeManager($transport) {
		$this->transport = $transport;
	}

	/**
	*
	* @author ktonder
	*/

	public function getClientToken() {
	     $data = array();
	     $data['args'] = array();
	     $data["method"] = "getClientToken";
	     $data["interfaceName"] = "core.braintree.IBrainTreeManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	*
	* @author ktonder
	*/

	public function pay($paymentMethodNonce, $orderId) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["paymentMethodNonce"] = json_encode($this->transport->object_unset_nulls($paymentMethodNonce));
	     $data['args']["orderId"] = json_encode($this->transport->object_unset_nulls($orderId));
	     $data["method"] = "pay";
	     $data["interfaceName"] = "core.braintree.IBrainTreeManager";
	     return $this->transport->sendMessage($data);
	}

}
class APICalendarManager {

	var $transport;
	
	function APICalendarManager($transport) {
		$this->transport = $transport;
	}

	/**
	* return a list of entires that a specified user
	* has been attending to
	*/

	public function addUserSilentlyToEvent($eventId, $userId) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["eventId"] = json_encode($this->transport->object_unset_nulls($eventId));
	     $data['args']["userId"] = json_encode($this->transport->object_unset_nulls($userId));
	     $data["method"] = "addUserSilentlyToEvent";
	     $data["interfaceName"] = "core.calendar.ICalendarManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Add a given user to a given event.
	* @param userId The user id to add to a the given event (see usermanager for more inforamtion about this id)
	* @param eventId The event id to attach to the user.
	* @param password A password you want to attach to the email that is being sent to the user.
	* @param username A username you want to attach to the email that is being sent to the user.
	* @return void
	* @throws ErrorException
	*/

	public function addUserToEvent($userId, $eventId, $password, $username, $source) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["userId"] = json_encode($this->transport->object_unset_nulls($userId));
	     $data['args']["eventId"] = json_encode($this->transport->object_unset_nulls($eventId));
	     $data['args']["password"] = json_encode($this->transport->object_unset_nulls($password));
	     $data['args']["username"] = json_encode($this->transport->object_unset_nulls($username));
	     $data['args']["source"] = json_encode($this->transport->object_unset_nulls($source));
	     $data["method"] = "addUserToEvent";
	     $data["interfaceName"] = "core.calendar.ICalendarManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Adds a user to a page event
	*
	* @param userId
	* @throws ErrorException
	*/

	public function addUserToPageEvent($userId, $bookingAppId) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["userId"] = json_encode($this->transport->object_unset_nulls($userId));
	     $data['args']["bookingAppId"] = json_encode($this->transport->object_unset_nulls($bookingAppId));
	     $data["method"] = "addUserToPageEvent";
	     $data["interfaceName"] = "core.calendar.ICalendarManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Apply a set of filters,
	* if this filters are applied, it will not return entries
	* that does not match the filter criteria.
	*/

	public function applyFilter($filters) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["filters"] = json_encode($this->transport->object_unset_nulls($filters));
	     $data["method"] = "applyFilter";
	     $data["interfaceName"] = "core.calendar.ICalendarManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Confirms a entry.
	*
	* @param entryId
	* @throws ErrorException
	*/

	public function confirmEntry($entryId) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["entryId"] = json_encode($this->transport->object_unset_nulls($entryId));
	     $data["method"] = "confirmEntry";
	     $data["interfaceName"] = "core.calendar.ICalendarManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Create a new entry to the calendar on a given date.
	* @param year The year to attach the entry to
	* @param month The month to attach the entry to
	* @param day The day to attach the entry to
	* @param entry The entry to attach
	* @return core_calendarmanager_data_Entry
	* @throws ErrorException
	*/

	public function createEntry($year, $month, $day) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["year"] = json_encode($this->transport->object_unset_nulls($year));
	     $data['args']["month"] = json_encode($this->transport->object_unset_nulls($month));
	     $data['args']["day"] = json_encode($this->transport->object_unset_nulls($day));
	     $data["method"] = "createEntry";
	     $data["interfaceName"] = "core.calendar.ICalendarManager";
	     return $this->transport->cast(new core_calendarmanager_data_Entry(), $this->transport->sendMessage($data));
	}

	/**
	* Delete an existing entry by a given id from the calendar.
	* @param id The id of the entry to delete.
	* @throws ErrorException
	*/

	public function deleteEntry($id) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["id"] = json_encode($this->transport->object_unset_nulls($id));
	     $data["method"] = "deleteEntry";
	     $data["interfaceName"] = "core.calendar.ICalendarManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Delete a location by id
	*
	* @param location
	* @throws ErrorException
	*/

	public function deleteLocation($locationId) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["locationId"] = json_encode($this->transport->object_unset_nulls($locationId));
	     $data["method"] = "deleteLocation";
	     $data["interfaceName"] = "core.calendar.ICalendarManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Returns a set of filters that
	* has been applied to the current session
	* calendar.
	*
	* @return List
	*/

	public function getActiveFilters() {
	     $data = array();
	     $data['args'] = array();
	     $data["method"] = "getActiveFilters";
	     $data["interfaceName"] = "core.calendar.ICalendarManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Returns a list of
	*/

	public function getAllEventsConnectedToPage($pageId) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["pageId"] = json_encode($this->transport->object_unset_nulls($pageId));
	     $data["method"] = "getAllEventsConnectedToPage";
	     $data["interfaceName"] = "core.calendar.ICalendarManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Get all locations.
	*
	* @return List
	*/

	public function getAllLocations() {
	     $data = array();
	     $data['args'] = array();
	     $data["method"] = "getAllLocations";
	     $data["interfaceName"] = "core.calendar.ICalendarManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Get all entries to a given day
	* @param year The year to fetch the entries on.
	* @param month The month to fetch the entries on.
	* @param day The day to fetch the entries on.
	* @return List
	* @throws ErrorException
	*/

	public function getEntries($year, $month, $day, $filters) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["year"] = json_encode($this->transport->object_unset_nulls($year));
	     $data['args']["month"] = json_encode($this->transport->object_unset_nulls($month));
	     $data['args']["day"] = json_encode($this->transport->object_unset_nulls($day));
	     $data['args']["filters"] = json_encode($this->transport->object_unset_nulls($filters));
	     $data["method"] = "getEntries";
	     $data["interfaceName"] = "core.calendar.ICalendarManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* return a list of entires that a specified user
	* has been attending to
	*/

	public function getEntriesByUserId($userId) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["userId"] = json_encode($this->transport->object_unset_nulls($userId));
	     $data["method"] = "getEntriesByUserId";
	     $data["interfaceName"] = "core.calendar.ICalendarManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Gives you the specified entry by id
	*
	* @param entryId
	* @return core_calendarmanager_data_Entry
	*/

	public function getEntry($entryId) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["entryId"] = json_encode($this->transport->object_unset_nulls($entryId));
	     $data["method"] = "getEntry";
	     $data["interfaceName"] = "core.calendar.ICalendarManager";
	     return $this->transport->cast(new core_calendarmanager_data_Entry(), $this->transport->sendMessage($data));
	}

	/**
	* return a list of entires that a specified user
	* has been attending to
	*/

	public function getEventPartitipatedData($pageId) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["pageId"] = json_encode($this->transport->object_unset_nulls($pageId));
	     $data["method"] = "getEventPartitipatedData";
	     $data["interfaceName"] = "core.calendar.ICalendarManager";
	     return $this->transport->cast(new core_calendarmanager_data_EventPartitipated(), $this->transport->sendMessage($data));
	}

	/**
	* This returns a list of all entries
	* that is connected to a page.
	*
	* @return List
	* @throws ErrorException
	*/

	public function getEventsGroupedByPageId() {
	     $data = array();
	     $data['args'] = array();
	     $data["method"] = "getEventsGroupedByPageId";
	     $data["interfaceName"] = "core.calendar.ICalendarManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Returns a set of filters that
	* can be applied to the Calendar.
	*/

	public function getFilters() {
	     $data = array();
	     $data['args'] = array();
	     $data["method"] = "getFilters";
	     $data["interfaceName"] = "core.calendar.ICalendarManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* When an event is sent it automatically creates and log an history entry.
	* Use this function to get all the history for a given event.
	*
	* @param eventId
	* @return List
	*/

	public function getHistory($eventId) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["eventId"] = json_encode($this->transport->object_unset_nulls($eventId));
	     $data["method"] = "getHistory";
	     $data["interfaceName"] = "core.calendar.ICalendarManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Get all data attached to a given month.
	* @param year The year to fetch
	* @param month The month to fetch
	* @param includeExtraEvents
	* @return core_calendarmanager_data_Month
	* @throws ErrorException
	*/

	public function getMonth($year, $month, $includeExtraEvents) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["year"] = json_encode($this->transport->object_unset_nulls($year));
	     $data['args']["month"] = json_encode($this->transport->object_unset_nulls($month));
	     $data['args']["includeExtraEvents"] = json_encode($this->transport->object_unset_nulls($includeExtraEvents));
	     $data["method"] = "getMonth";
	     $data["interfaceName"] = "core.calendar.ICalendarManager";
	     return $this->transport->cast(new core_calendarmanager_data_Month(), $this->transport->sendMessage($data));
	}

	/**
	* Returns all months with only valid entries
	* and all entries are sorted by date.
	*
	* @return List
	* @throws ErrorException
	*/

	public function getMonths() {
	     $data = array();
	     $data['args'] = array();
	     $data["method"] = "getMonths";
	     $data["interfaceName"] = "core.calendar.ICalendarManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Get all entries
	*/

	public function getMonthsAfter($year, $month) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["year"] = json_encode($this->transport->object_unset_nulls($year));
	     $data['args']["month"] = json_encode($this->transport->object_unset_nulls($month));
	     $data["method"] = "getMonthsAfter";
	     $data["interfaceName"] = "core.calendar.ICalendarManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* return a list of entires that a specified user
	* has been attending to
	*/

	public function getSignature($id) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["id"] = json_encode($this->transport->object_unset_nulls($id));
	     $data["method"] = "getSignature";
	     $data["interfaceName"] = "core.calendar.ICalendarManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Remove a given user from a given event.
	* @param userId The userid for the event to be removed. (see usermanager for more inforamtion about this id)
	* @param eventId The id of the event for the user to be removed from.
	* @return void
	* @throws ErrorException
	*/

	public function removeUserFromEvent($userId, $eventId) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["userId"] = json_encode($this->transport->object_unset_nulls($userId));
	     $data['args']["eventId"] = json_encode($this->transport->object_unset_nulls($eventId));
	     $data["method"] = "removeUserFromEvent";
	     $data["interfaceName"] = "core.calendar.ICalendarManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Save an already existing entry.
	* @param entry
	* @throws ErrorException
	*/

	public function saveEntry($core_calendarmanager_data_Entry) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["core_calendarmanager_data_Entry"] = json_encode($this->transport->object_unset_nulls($core_calendarmanager_data_Entry));
	     $data["method"] = "saveEntry";
	     $data["interfaceName"] = "core.calendar.ICalendarManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Adds a new location to the system.
	*
	* @param location
	* @throws ErrorException
	*/

	public function saveLocation($core_calendarmanager_data_Location) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["core_calendarmanager_data_Location"] = json_encode($this->transport->object_unset_nulls($core_calendarmanager_data_Location));
	     $data["method"] = "saveLocation";
	     $data["interfaceName"] = "core.calendar.ICalendarManager";
	     return $this->transport->cast(new core_calendarmanager_data_Location(), $this->transport->sendMessage($data));
	}

	/**
	Remind a given list of users about a given entry.
	* @param entryId The id of the entry to remind about.
	* @param byEmail Remind users by email (true to send)
	* @param bySMS Remind users by sms (true to send)
	* @param users A list of user ids to remind.
	* @param text The text to send when reminding.
	* @param subject A subject to attach to the email.
	* @return void
	* @throws ErrorException
	*/

	public function sendReminderToUser($byEmail, $bySMS, $users, $text, $subject, $eventId, $attachment, $sendReminderToUser) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["byEmail"] = json_encode($this->transport->object_unset_nulls($byEmail));
	     $data['args']["bySMS"] = json_encode($this->transport->object_unset_nulls($bySMS));
	     $data['args']["users"] = json_encode($this->transport->object_unset_nulls($users));
	     $data['args']["text"] = json_encode($this->transport->object_unset_nulls($text));
	     $data['args']["subject"] = json_encode($this->transport->object_unset_nulls($subject));
	     $data['args']["eventId"] = json_encode($this->transport->object_unset_nulls($eventId));
	     $data['args']["attachment"] = json_encode($this->transport->object_unset_nulls($attachment));
	     $data['args']["sendReminderToUser"] = json_encode($this->transport->object_unset_nulls($sendReminderToUser));
	     $data["method"] = "sendReminderToUser";
	     $data["interfaceName"] = "core.calendar.ICalendarManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* return a list of entires that a specified user
	* has been attending to
	*/

	public function setEventPartitipatedData($core_calendarmanager_data_EventPartitipated) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["core_calendarmanager_data_EventPartitipated"] = json_encode($this->transport->object_unset_nulls($core_calendarmanager_data_EventPartitipated));
	     $data["method"] = "setEventPartitipatedData";
	     $data["interfaceName"] = "core.calendar.ICalendarManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* return a list of entires that a specified user
	* has been attending to
	*/

	public function setSignature($userid, $signature) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["userid"] = json_encode($this->transport->object_unset_nulls($userid));
	     $data['args']["signature"] = json_encode($this->transport->object_unset_nulls($signature));
	     $data["method"] = "setSignature";
	     $data["interfaceName"] = "core.calendar.ICalendarManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Accept a candidate from waitinglist to
	* course.
	*/

	public function transferFromWaitingList($entryId, $userId) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["entryId"] = json_encode($this->transport->object_unset_nulls($entryId));
	     $data['args']["userId"] = json_encode($this->transport->object_unset_nulls($userId));
	     $data["method"] = "transferFromWaitingList";
	     $data["interfaceName"] = "core.calendar.ICalendarManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Transfer a user from one event to another.
	*
	* Needs to be administrator becuase it updating the candidates password.
	*
	* @param evenId
	*/

	public function transferUser($fromEventId, $toEventId, $userId) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["fromEventId"] = json_encode($this->transport->object_unset_nulls($fromEventId));
	     $data['args']["toEventId"] = json_encode($this->transport->object_unset_nulls($toEventId));
	     $data['args']["userId"] = json_encode($this->transport->object_unset_nulls($userId));
	     $data["method"] = "transferUser";
	     $data["interfaceName"] = "core.calendar.ICalendarManager";
	     return $this->transport->sendMessage($data);
	}

}
class APICarTuningManager {

	var $transport;
	
	function APICarTuningManager($transport) {
		$this->transport = $transport;
	}

	/**
	*
	* @author boggi
	*/

	public function getCarTuningData($id) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["id"] = json_encode($this->transport->object_unset_nulls($id));
	     $data["method"] = "getCarTuningData";
	     $data["interfaceName"] = "core.cartuning.ICarTuningManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	*
	* @author boggi
	*/

	public function saveCarTuningData($carList) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["carList"] = json_encode($this->transport->object_unset_nulls($carList));
	     $data["method"] = "saveCarTuningData";
	     $data["interfaceName"] = "core.cartuning.ICarTuningManager";
	     return $this->transport->sendMessage($data);
	}

}
class APICartManager {

	var $transport;
	
	function APICartManager($transport) {
		$this->transport = $transport;
	}

	/**
	* Add coupons to the system.
	*/

	public function addCoupon($core_cartmanager_data_Coupon) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["core_cartmanager_data_Coupon"] = json_encode($this->transport->object_unset_nulls($core_cartmanager_data_Coupon));
	     $data["method"] = "addCoupon";
	     $data["interfaceName"] = "core.cartmanager.ICartManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Need to attach a reference number manually to the cart?
	* @throws ErrorException
	*/

	public function addMetaDataToProduct($productId, $metaData) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["productId"] = json_encode($this->transport->object_unset_nulls($productId));
	     $data['args']["metaData"] = json_encode($this->transport->object_unset_nulls($metaData));
	     $data["method"] = "addMetaDataToProduct";
	     $data["interfaceName"] = "core.cartmanager.ICartManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Add a new product to the cart.
	* @param productId The product id generated by the productmanager.
	* @param int Number instances of the product ordered.
	* @return core_cartmanager_data_Cart
	* @throws ErrorException
	*/

	public function addProduct($productId, $count, $variations) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["productId"] = json_encode($this->transport->object_unset_nulls($productId));
	     $data['args']["count"] = json_encode($this->transport->object_unset_nulls($count));
	     $data['args']["variations"] = json_encode($this->transport->object_unset_nulls($variations));
	     $data["method"] = "addProduct";
	     $data["interfaceName"] = "core.cartmanager.ICartManager";
	     return $this->transport->cast(new core_cartmanager_data_Cart(), $this->transport->sendMessage($data));
	}

	/**
	* Add a new product to the cart.
	* @param productId The product id generated by the productmanager.
	* @param int Number instances of the product ordered.
	* @return core_cartmanager_data_CartItem
	* @throws ErrorException
	*/

	public function addProductItem($productId, $count) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["productId"] = json_encode($this->transport->object_unset_nulls($productId));
	     $data['args']["count"] = json_encode($this->transport->object_unset_nulls($count));
	     $data["method"] = "addProductItem";
	     $data["interfaceName"] = "core.cartmanager.ICartManager";
	     return $this->transport->cast(new core_cartmanager_data_CartItem(), $this->transport->sendMessage($data));
	}

	/**
	* Apply the coupon to the cart.
	*/

	public function applyCouponToCurrentCart($code) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["code"] = json_encode($this->transport->object_unset_nulls($code));
	     $data["method"] = "applyCouponToCurrentCart";
	     $data["interfaceName"] = "core.cartmanager.ICartManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Send in a cart and you shall have the total price for all products.
	*/

	public function calculateTotalCost($core_cartmanager_data_Cart) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["core_cartmanager_data_Cart"] = json_encode($this->transport->object_unset_nulls($core_cartmanager_data_Cart));
	     $data["method"] = "calculateTotalCost";
	     $data["interfaceName"] = "core.cartmanager.ICartManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Clear the current shopping cart.
	*/

	public function clear() {
	     $data = array();
	     $data['args'] = array();
	     $data["method"] = "clear";
	     $data["interfaceName"] = "core.cartmanager.ICartManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Fetch the current cart.
	* @return core_cartmanager_data_Cart
	* @throws ErrorException
	*/

	public function getCart() {
	     $data = array();
	     $data['args'] = array();
	     $data["method"] = "getCart";
	     $data["interfaceName"] = "core.cartmanager.ICartManager";
	     return $this->transport->cast(new core_cartmanager_data_Cart(), $this->transport->sendMessage($data));
	}

	/**
	* Returns the current total amount
	* note, this does not include shipping.
	*
	* @return Double
	* @throws ErrorException
	*/

	public function getCartTotalAmount() {
	     $data = array();
	     $data['args'] = array();
	     $data["method"] = "getCartTotalAmount";
	     $data["interfaceName"] = "core.cartmanager.ICartManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Returns a list of all coupons.
	* @return List
	*/

	public function getCoupons() {
	     $data = array();
	     $data['args'] = array();
	     $data["method"] = "getCoupons";
	     $data["interfaceName"] = "core.cartmanager.ICartManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Returns the shipping cost
	* @return Double
	*/

	public function getShippingCost() {
	     $data = array();
	     $data['args'] = array();
	     $data["method"] = "getShippingCost";
	     $data["interfaceName"] = "core.cartmanager.ICartManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Fetch the total amount of price to use when calculating shipping price.
	* @return Double
	* @throws ErrorException
	*/

	public function getShippingPriceBasis() {
	     $data = array();
	     $data['args'] = array();
	     $data["method"] = "getShippingPriceBasis";
	     $data["interfaceName"] = "core.cartmanager.ICartManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Returns the current calculation of taxes.
	* @return List
	*/

	public function getTaxes() {
	     $data = array();
	     $data['args'] = array();
	     $data["method"] = "getTaxes";
	     $data["interfaceName"] = "core.cartmanager.ICartManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Remove all coupons from the system.
	* @throws ErrorException
	*/

	public function removeAllCoupons() {
	     $data = array();
	     $data['args'] = array();
	     $data["method"] = "removeAllCoupons";
	     $data["interfaceName"] = "core.cartmanager.ICartManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Remove a coupon from the system.
	*
	* @param coupon
	* @throws ErrorException
	*/

	public function removeCoupon($code) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["code"] = json_encode($this->transport->object_unset_nulls($code));
	     $data["method"] = "removeCoupon";
	     $data["interfaceName"] = "core.cartmanager.ICartManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Remove an added product from the cart.
	* @param productId The product id generated by the productmanager, that has been added to the cart.
	* @return core_cartmanager_data_Cart
	* @throws ErrorException
	*/

	public function removeProduct($cartItemId) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["cartItemId"] = json_encode($this->transport->object_unset_nulls($cartItemId));
	     $data["method"] = "removeProduct";
	     $data["interfaceName"] = "core.cartmanager.ICartManager";
	     return $this->transport->cast(new core_cartmanager_data_Cart(), $this->transport->sendMessage($data));
	}

	/**
	* Set a new address to the current cart.
	*/

	public function setAddress($core_usermanager_data_Address) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["core_usermanager_data_Address"] = json_encode($this->transport->object_unset_nulls($core_usermanager_data_Address));
	     $data["method"] = "setAddress";
	     $data["interfaceName"] = "core.cartmanager.ICartManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Need to attach a reference number manually to the cart?
	* @throws ErrorException
	*/

	public function setReference($reference) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["reference"] = json_encode($this->transport->object_unset_nulls($reference));
	     $data["method"] = "setReference";
	     $data["interfaceName"] = "core.cartmanager.ICartManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Sets the shipping cost.
	* Should be in base currency.
	*/

	public function setShippingCost($shippingCost) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["shippingCost"] = json_encode($this->transport->object_unset_nulls($shippingCost));
	     $data["method"] = "setShippingCost";
	     $data["interfaceName"] = "core.cartmanager.ICartManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Change the number of instances added to the product.
	* @param productId The product id generated by the productmanager.
	* @param count The number of instances (has to be a positive integer)
	* @return core_cartmanager_data_Cart
	* @throws ErrorException
	*/

	public function updateProductCount($cartItemId, $count) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["cartItemId"] = json_encode($this->transport->object_unset_nulls($cartItemId));
	     $data['args']["count"] = json_encode($this->transport->object_unset_nulls($count));
	     $data["method"] = "updateProductCount";
	     $data["interfaceName"] = "core.cartmanager.ICartManager";
	     return $this->transport->cast(new core_cartmanager_data_Cart(), $this->transport->sendMessage($data));
	}

}
class APIChatManager {

	var $transport;
	
	function APIChatManager($transport) {
		$this->transport = $transport;
	}

	/**
	* Reply to a chat message.
	*
	* @param id - Chatters id
	* @param message
	*/

	public function closeChat($id) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["id"] = json_encode($this->transport->object_unset_nulls($id));
	     $data["method"] = "closeChat";
	     $data["interfaceName"] = "core.chat.IChatManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Returns a list of available poeple to chat with.
	* @return List
	*/

	public function getChatters() {
	     $data = array();
	     $data['args'] = array();
	     $data["method"] = "getChatters";
	     $data["interfaceName"] = "core.chat.IChatManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Get all your chat messages
	*
	* @return List
	*/

	public function getMessages() {
	     $data = array();
	     $data['args'] = array();
	     $data["method"] = "getMessages";
	     $data["interfaceName"] = "core.chat.IChatManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Notify the manager that someone is online, this will effectivly stop the notification to continue.
	*
	* No need to notify everyone if the chat is handeled properly anyway.
	* @throws ErrorException
	*/

	public function pingMobileChat($chatterid) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["chatterid"] = json_encode($this->transport->object_unset_nulls($chatterid));
	     $data["method"] = "pingMobileChat";
	     $data["interfaceName"] = "core.chat.IChatManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Reply to a chat message.
	*
	* @param id - Chatters id
	* @param message
	*/

	public function replyToChat($id, $message) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["id"] = json_encode($this->transport->object_unset_nulls($id));
	     $data['args']["message"] = json_encode($this->transport->object_unset_nulls($message));
	     $data["method"] = "replyToChat";
	     $data["interfaceName"] = "core.chat.IChatManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Send a message to administrator
	*
	* @param message
	* @throws ErrorException
	*/

	public function sendMessage($message) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["message"] = json_encode($this->transport->object_unset_nulls($message));
	     $data["method"] = "sendMessage";
	     $data["interfaceName"] = "core.chat.IChatManager";
	     return $this->transport->sendMessage($data);
	}

}
class APIContentManager {

	var $transport;
	
	function APIContentManager($transport) {
		$this->transport = $transport;
	}

	/**
	* Create a new instance for the content manager.<br>
	* An id will automatically be generated and returned on creation.<br>
	*
	* @param content The content to build upon.
	* @return String
	* @throws ErrorException
	*/

	public function createContent($content) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["content"] = json_encode($this->transport->object_unset_nulls($content));
	     $data["method"] = "createContent";
	     $data["interfaceName"] = "app.content.IContentManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Remove the content for a given id.
	* @param id The id to remove the content for.
	* @throws ErrorException
	*/

	public function deleteContent($id) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["id"] = json_encode($this->transport->object_unset_nulls($id));
	     $data["method"] = "deleteContent";
	     $data["interfaceName"] = "app.content.IContentManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Fetch all content saved until now.
	* @return HashMap
	* @throws ErrorException
	*/

	public function getAllContent() {
	     $data = array();
	     $data['args'] = array();
	     $data["method"] = "getAllContent";
	     $data["interfaceName"] = "app.content.IContentManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Fetch the content for a given id.
	* @param id The id which is identifying the content.
	* @return String
	* @throws ErrorException
	*/

	public function getContent($id) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["id"] = json_encode($this->transport->object_unset_nulls($id));
	     $data["method"] = "getContent";
	     $data["interfaceName"] = "app.content.IContentManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Update / replace the content for a given id.
	* @param id The id to update the content for.
	* @param content The content to update. This could be html / text.
	* @throws ErrorException
	*/

	public function saveContent($id, $content) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["id"] = json_encode($this->transport->object_unset_nulls($id));
	     $data['args']["content"] = json_encode($this->transport->object_unset_nulls($content));
	     $data["method"] = "saveContent";
	     $data["interfaceName"] = "app.content.IContentManager";
	     return $this->transport->sendMessage($data);
	}

}
class APIFooterManager {

	var $transport;
	
	function APIFooterManager($transport) {
		$this->transport = $transport;
	}

	/**
	* Get the current configuration.
	* @return app_footermanager_data_Configuration
	* @throws ErrorException
	*/

	public function getConfiguration() {
	     $data = array();
	     $data['args'] = array();
	     $data["method"] = "getConfiguration";
	     $data["interfaceName"] = "app.footer.IFooterManager";
	     return $this->transport->cast(new app_footermanager_data_Configuration(), $this->transport->sendMessage($data));
	}

	/**
	* Change the layout for the columns.<br>
	* Defaults to 1 if nothing else is set.<br>
	* @param numberOfColumns The number of columns you want to display.
	* @throws ErrorException
	*/

	public function setLayout($numberOfColumns) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["numberOfColumns"] = json_encode($this->transport->object_unset_nulls($numberOfColumns));
	     $data["method"] = "setLayout";
	     $data["interfaceName"] = "app.footer.IFooterManager";
	     return $this->transport->cast(new app_footermanager_data_Configuration(), $this->transport->sendMessage($data));
	}

	/**
	* Change the type of a given column.
	* @param column The column it regards
	* @param type The type,0 for text, 1 for list.
	* @return app_footermanager_data_Configuration
	* @throws ErrorException
	*/

	public function setType($column, $type) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["column"] = json_encode($this->transport->object_unset_nulls($column));
	     $data['args']["type"] = json_encode($this->transport->object_unset_nulls($type));
	     $data["method"] = "setType";
	     $data["interfaceName"] = "app.footer.IFooterManager";
	     return $this->transport->cast(new app_footermanager_data_Configuration(), $this->transport->sendMessage($data));
	}

}
class APIGalleryManager {

	var $transport;
	
	function APIGalleryManager($transport) {
		$this->transport = $transport;
	}

	/**
	* Add a image to a given gallery.
	* @param galleryId The id for the gallery, if this does not exists, it creates a gallery related to this id.
	* @param imageId The image id generated by the filemanager.
	* @param description A description to the image.
	* @param title A title to the image.
	* @throws ErrorException
	*/

	public function addImageToGallery($galleryId, $imageId, $description, $title) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["galleryId"] = json_encode($this->transport->object_unset_nulls($galleryId));
	     $data['args']["imageId"] = json_encode($this->transport->object_unset_nulls($imageId));
	     $data['args']["description"] = json_encode($this->transport->object_unset_nulls($description));
	     $data['args']["title"] = json_encode($this->transport->object_unset_nulls($title));
	     $data["method"] = "addImageToGallery";
	     $data["interfaceName"] = "core.gallerymanager.IGalleryManager";
	     return $this->transport->cast(new app_gallerymanager_data_ImageEntry(), $this->transport->sendMessage($data));
	}

	/**
	* Create a new gallery.
	* @return String
	* @throws ErrorException
	*/

	public function createImageGallery() {
	     $data = array();
	     $data['args'] = array();
	     $data["method"] = "createImageGallery";
	     $data["interfaceName"] = "core.gallerymanager.IGalleryManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Remove an already existing image.
	* @param entryId The id of the image to remove.
	* @throws ErrorException
	*/

	public function deleteImage($entryId) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["entryId"] = json_encode($this->transport->object_unset_nulls($entryId));
	     $data["method"] = "deleteImage";
	     $data["interfaceName"] = "core.gallerymanager.IGalleryManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Fetch all images binded to a given gallery id.
	* @param id The id to fetch the entries from.
	* @return List
	* @throws ErrorException
	*/

	public function getAllImages($id) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["id"] = json_encode($this->transport->object_unset_nulls($id));
	     $data["method"] = "getAllImages";
	     $data["interfaceName"] = "core.gallerymanager.IGalleryManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Find an existing entry.
	* @param id The id for search for (found in the ImageEntry object)
	* @return app_gallerymanager_data_ImageEntry
	* @throws ErrorException
	*/

	public function getEntry($id) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["id"] = json_encode($this->transport->object_unset_nulls($id));
	     $data["method"] = "getEntry";
	     $data["interfaceName"] = "core.gallerymanager.IGalleryManager";
	     return $this->transport->cast(new app_gallerymanager_data_ImageEntry(), $this->transport->sendMessage($data));
	}

	/**
	* Update an already existing image.
	* @param entry The entry to update.
	* @throws ErrorException
	*/

	public function saveEntry($app_gallerymanager_data_ImageEntry) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["app_gallerymanager_data_ImageEntry"] = json_encode($this->transport->object_unset_nulls($app_gallerymanager_data_ImageEntry));
	     $data["method"] = "saveEntry";
	     $data["interfaceName"] = "core.gallerymanager.IGalleryManager";
	     return $this->transport->sendMessage($data);
	}

}
class APIGetShop {

	var $transport;
	
	function APIGetShop($transport) {
		$this->transport = $transport;
	}

	/**
	*
	* @param userId
	* @param partner
	* @param password
	* @throws ErrorException
	*/

	public function addUserToPartner($userId, $partner, $password) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["userId"] = json_encode($this->transport->object_unset_nulls($userId));
	     $data['args']["partner"] = json_encode($this->transport->object_unset_nulls($partner));
	     $data['args']["password"] = json_encode($this->transport->object_unset_nulls($password));
	     $data["method"] = "addUserToPartner";
	     $data["interfaceName"] = "core.getshop.IGetShop";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Create a new webpage
	* @return core_storemanager_data_Store
	*/

	public function createWebPage($core_getshop_data_WebPageData) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["core_getshop_data_WebPageData"] = json_encode($this->transport->object_unset_nulls($core_getshop_data_WebPageData));
	     $data["method"] = "createWebPage";
	     $data["interfaceName"] = "core.getshop.IGetShop";
	     return $this->transport->cast(new core_storemanager_data_Store(), $this->transport->sendMessage($data));
	}

	/**
	* Find the store address for a given application.
	* @param uuid The appid.
	* @return String
	* @throws ErrorException
	*/

	public function findAddressForApplication($uuid) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["uuid"] = json_encode($this->transport->object_unset_nulls($uuid));
	     $data["method"] = "findAddressForApplication";
	     $data["interfaceName"] = "core.getshop.IGetShop";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Need to figure out what address is connected to a specific uuid?
	* Remember this is query is quite slow. so cache the result.
	* @param uuid
	* @return String
	* @throws ErrorException
	*/

	public function findAddressForUUID($uuid) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["uuid"] = json_encode($this->transport->object_unset_nulls($uuid));
	     $data["method"] = "findAddressForUUID";
	     $data["interfaceName"] = "core.getshop.IGetShop";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Get partner data for this user.
	* @return core_getshop_data_PartnerData
	* @throws ErrorException
	*/

	public function getPartnerData($partnerId, $password) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["partnerId"] = json_encode($this->transport->object_unset_nulls($partnerId));
	     $data['args']["password"] = json_encode($this->transport->object_unset_nulls($password));
	     $data["method"] = "getPartnerData";
	     $data["interfaceName"] = "core.getshop.IGetShop";
	     return $this->transport->cast(new core_getshop_data_PartnerData(), $this->transport->sendMessage($data));
	}

	/**
	*
	* @param code
	* @return List
	*/

	public function getStores($code) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["code"] = json_encode($this->transport->object_unset_nulls($code));
	     $data["method"] = "getStores";
	     $data["interfaceName"] = "core.getshop.IGetShop";
	     return $this->transport->sendMessage($data);
	}

	/**
	*
	* @param ids
	* @throws ErrorException
	*/

	public function setApplicationList($ids, $partnerId, $password) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["ids"] = json_encode($this->transport->object_unset_nulls($ids));
	     $data['args']["partnerId"] = json_encode($this->transport->object_unset_nulls($partnerId));
	     $data['args']["password"] = json_encode($this->transport->object_unset_nulls($password));
	     $data["method"] = "setApplicationList";
	     $data["interfaceName"] = "core.getshop.IGetShop";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Create a new webpage
	* @return String
	*/

	public function startStoreFromStore($core_getshop_data_StartData) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["core_getshop_data_StartData"] = json_encode($this->transport->object_unset_nulls($core_getshop_data_StartData));
	     $data["method"] = "startStoreFromStore";
	     $data["interfaceName"] = "core.getshop.IGetShop";
	     return $this->transport->sendMessage($data);
	}

}
class APIGetShopApplicationPool {

	var $transport;
	
	function APIGetShopApplicationPool($transport) {
		$this->transport = $transport;
	}

	/**
	* Save an application
	*
	* @param application
	*/

	public function deleteApplication($applicationId) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["applicationId"] = json_encode($this->transport->object_unset_nulls($applicationId));
	     $data["method"] = "deleteApplication";
	     $data["interfaceName"] = "core.applications.IGetShopApplicationPool";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Get an application by an given id.
	*
	* @param applicationId
	* @return core_appmanager_data_Application
	*/

	public function get($applicationId) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["applicationId"] = json_encode($this->transport->object_unset_nulls($applicationId));
	     $data["method"] = "get";
	     $data["interfaceName"] = "core.applications.IGetShopApplicationPool";
	     return $this->transport->cast(new core_appmanager_data_Application(), $this->transport->sendMessage($data));
	}

	/**
	* Returns a list of all available applications.
	*
	* @return List
	*/

	public function getApplications() {
	     $data = array();
	     $data['args'] = array();
	     $data["method"] = "getApplications";
	     $data["interfaceName"] = "core.applications.IGetShopApplicationPool";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Save an application
	*
	* @param application
	*/

	public function saveApplication($core_appmanager_data_Application) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["core_appmanager_data_Application"] = json_encode($this->transport->object_unset_nulls($core_appmanager_data_Application));
	     $data["method"] = "saveApplication";
	     $data["interfaceName"] = "core.applications.IGetShopApplicationPool";
	     return $this->transport->sendMessage($data);
	}

}
class APIHotelBookingManager {

	var $transport;
	
	function APIHotelBookingManager($transport) {
		$this->transport = $transport;
	}

	/**
	* Check if a room is available in the given time periode.
	* @param startDate The first day unix timestamp.
	* @param endDate The last day unix timestamp.
	* @param type The type of room to search for-
	* @return Integer
	* @throws ErrorException
	*/

	public function checkAvailable($startDate, $endDate, $type, $core_hotelbookingmanager_AdditionalBookingInformation) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["startDate"] = json_encode($this->transport->object_unset_nulls($startDate));
	     $data['args']["endDate"] = json_encode($this->transport->object_unset_nulls($endDate));
	     $data['args']["type"] = json_encode($this->transport->object_unset_nulls($type));
	     $data['args']["core_hotelbookingmanager_AdditionalBookingInformation"] = json_encode($this->transport->object_unset_nulls($core_hotelbookingmanager_AdditionalBookingInformation));
	     $data["method"] = "checkAvailable";
	     $data["interfaceName"] = "core.hotelbookingmanager.IHotelBookingManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Get all references
	* @return Integer
	* @throws ErrorException
	*/

	public function checkAvailableParkingSpots($startDate, $endDate) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["startDate"] = json_encode($this->transport->object_unset_nulls($startDate));
	     $data['args']["endDate"] = json_encode($this->transport->object_unset_nulls($endDate));
	     $data["method"] = "checkAvailableParkingSpots";
	     $data["interfaceName"] = "core.hotelbookingmanager.IHotelBookingManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Get all references
	* @return void
	* @throws ErrorException
	*/

	public function checkForArxTransfer() {
	     $data = array();
	     $data['args'] = array();
	     $data["method"] = "checkForArxTransfer";
	     $data["interfaceName"] = "core.hotelbookingmanager.IHotelBookingManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Get all references
	* @return void
	* @throws ErrorException
	*/

	public function checkForVismaTransfer() {
	     $data = array();
	     $data['args'] = array();
	     $data["method"] = "checkForVismaTransfer";
	     $data["interfaceName"] = "core.hotelbookingmanager.IHotelBookingManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Get all references
	* @return void
	* @throws ErrorException
	*/

	public function checkForWelcomeMessagesToSend() {
	     $data = array();
	     $data['args'] = array();
	     $data["method"] = "checkForWelcomeMessagesToSend";
	     $data["interfaceName"] = "core.hotelbookingmanager.IHotelBookingManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Get all references
	* @return void
	* @throws ErrorException
	*/

	public function deleteReference($reference) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["reference"] = json_encode($this->transport->object_unset_nulls($reference));
	     $data["method"] = "deleteReference";
	     $data["interfaceName"] = "core.hotelbookingmanager.IHotelBookingManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Get all references
	* @return List
	* @throws ErrorException
	*/

	public function getAllReservations() {
	     $data = array();
	     $data['args'] = array();
	     $data["method"] = "getAllReservations";
	     $data["interfaceName"] = "core.hotelbookingmanager.IHotelBookingManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Get all references
	* @return List
	* @throws ErrorException
	*/

	public function getAllReservationsArx() {
	     $data = array();
	     $data['args'] = array();
	     $data["method"] = "getAllReservationsArx";
	     $data["interfaceName"] = "core.hotelbookingmanager.IHotelBookingManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	*
	* @return List
	* @throws ErrorException
	*/

	public function getAllRooms() {
	     $data = array();
	     $data['args'] = array();
	     $data["method"] = "getAllRooms";
	     $data["interfaceName"] = "core.hotelbookingmanager.IHotelBookingManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Get all references
	* @return List
	* @throws ErrorException
	*/

	public function getArxLog() {
	     $data = array();
	     $data['args'] = array();
	     $data["method"] = "getArxLog";
	     $data["interfaceName"] = "core.hotelbookingmanager.IHotelBookingManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	*
	* @param roomType
	* @param startDate The first day unix timestamp.
	* @param endDate The last day unix timestamp.
	* @param count The number of rooms to book.
	* @throws ErrorException
	*/

	public function getBookingConfiguration() {
	     $data = array();
	     $data['args'] = array();
	     $data["method"] = "getBookingConfiguration";
	     $data["interfaceName"] = "core.hotelbookingmanager.IHotelBookingManager";
	     return $this->transport->cast(new core_hotelbookingmanager_GlobalBookingSettings(), $this->transport->sendMessage($data));
	}

	/**
	* Add new room to the manager.
	* @param room
	* @throws ErrorException
	*/

	public function getEmailMessage($language) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["language"] = json_encode($this->transport->object_unset_nulls($language));
	     $data["method"] = "getEmailMessage";
	     $data["interfaceName"] = "core.hotelbookingmanager.IHotelBookingManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Get all references
	* @return core_hotelbookingmanager_BookingReference
	* @throws ErrorException
	*/

	public function getReservationByReferenceId($referenceId) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["referenceId"] = json_encode($this->transport->object_unset_nulls($referenceId));
	     $data["method"] = "getReservationByReferenceId";
	     $data["interfaceName"] = "core.hotelbookingmanager.IHotelBookingManager";
	     return $this->transport->cast(new core_hotelbookingmanager_BookingReference(), $this->transport->sendMessage($data));
	}

	/**
	* Add new room to the manager.
	* @param room
	* @throws ErrorException
	*/

	public function getRoom($id) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["id"] = json_encode($this->transport->object_unset_nulls($id));
	     $data["method"] = "getRoom";
	     $data["interfaceName"] = "core.hotelbookingmanager.IHotelBookingManager";
	     return $this->transport->cast(new core_hotelbookingmanager_Room(), $this->transport->sendMessage($data));
	}

	/**
	* Get all references
	* @return ArrayList
	* @throws ErrorException
	*/

	public function getRoomProductIds() {
	     $data = array();
	     $data['args'] = array();
	     $data["method"] = "getRoomProductIds";
	     $data["interfaceName"] = "core.hotelbookingmanager.IHotelBookingManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Get all references
	* @return String
	* @throws ErrorException
	*/

	public function getUserIdForRoom($roomNumber) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["roomNumber"] = json_encode($this->transport->object_unset_nulls($roomNumber));
	     $data["method"] = "getUserIdForRoom";
	     $data["interfaceName"] = "core.hotelbookingmanager.IHotelBookingManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Get all references
	* @return boolean
	* @throws ErrorException
	*/

	public function isRoomAvailable($roomId, $startDate, $endDate) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["roomId"] = json_encode($this->transport->object_unset_nulls($roomId));
	     $data['args']["startDate"] = json_encode($this->transport->object_unset_nulls($startDate));
	     $data['args']["endDate"] = json_encode($this->transport->object_unset_nulls($endDate));
	     $data["method"] = "isRoomAvailable";
	     $data["interfaceName"] = "core.hotelbookingmanager.IHotelBookingManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Get all references
	* @return void
	* @throws ErrorException
	*/

	public function markRoomAsReady($roomId) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["roomId"] = json_encode($this->transport->object_unset_nulls($roomId));
	     $data["method"] = "markRoomAsReady";
	     $data["interfaceName"] = "core.hotelbookingmanager.IHotelBookingManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Change a room for a reference.
	* @param reference
	* @param oldRoom the old room
	* @param newRoomId
	* @throws ErrorException
	*/

	public function moveRoomOnReference($reference, $oldRoom, $newRoomId) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["reference"] = json_encode($this->transport->object_unset_nulls($reference));
	     $data['args']["oldRoom"] = json_encode($this->transport->object_unset_nulls($oldRoom));
	     $data['args']["newRoomId"] = json_encode($this->transport->object_unset_nulls($newRoomId));
	     $data["method"] = "moveRoomOnReference";
	     $data["interfaceName"] = "core.hotelbookingmanager.IHotelBookingManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Get all references
	* @return void
	* @throws ErrorException
	*/

	public function notifyUserAboutRoom($core_hotelbookingmanager_BookingReference, $core_hotelbookingmanager_RoomInformation, $code) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["core_hotelbookingmanager_BookingReference"] = json_encode($this->transport->object_unset_nulls($core_hotelbookingmanager_BookingReference));
	     $data['args']["core_hotelbookingmanager_RoomInformation"] = json_encode($this->transport->object_unset_nulls($core_hotelbookingmanager_RoomInformation));
	     $data['args']["code"] = json_encode($this->transport->object_unset_nulls($code));
	     $data["method"] = "notifyUserAboutRoom";
	     $data["interfaceName"] = "core.hotelbookingmanager.IHotelBookingManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Add new room to the manager.
	* @param room
	* @throws ErrorException
	*/

	public function removeRoom($id) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["id"] = json_encode($this->transport->object_unset_nulls($id));
	     $data["method"] = "removeRoom";
	     $data["interfaceName"] = "core.hotelbookingmanager.IHotelBookingManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	*
	* @param roomType
	* @param startDate The first day unix timestamp.
	* @param endDate The last day unix timestamp.
	* @param count The number of rooms to book.
	* @throws ErrorException
	*/

	public function reserveRoom($roomType, $startDate, $endDate, $info, $core_hotelbookingmanager_AdditionalBookingInformation) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["roomType"] = json_encode($this->transport->object_unset_nulls($roomType));
	     $data['args']["startDate"] = json_encode($this->transport->object_unset_nulls($startDate));
	     $data['args']["endDate"] = json_encode($this->transport->object_unset_nulls($endDate));
	     $data['args']["info"] = json_encode($this->transport->object_unset_nulls($info));
	     $data['args']["core_hotelbookingmanager_AdditionalBookingInformation"] = json_encode($this->transport->object_unset_nulls($core_hotelbookingmanager_AdditionalBookingInformation));
	     $data["method"] = "reserveRoom";
	     $data["interfaceName"] = "core.hotelbookingmanager.IHotelBookingManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Add new room to the manager.
	* @param room
	* @throws ErrorException
	*/

	public function saveRoom($core_hotelbookingmanager_Room) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["core_hotelbookingmanager_Room"] = json_encode($this->transport->object_unset_nulls($core_hotelbookingmanager_Room));
	     $data["method"] = "saveRoom";
	     $data["interfaceName"] = "core.hotelbookingmanager.IHotelBookingManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Get all references
	* @return void
	* @throws ErrorException
	*/

	public function setArxConfiguration($core_hotelbookingmanager_ArxSettings) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["core_hotelbookingmanager_ArxSettings"] = json_encode($this->transport->object_unset_nulls($core_hotelbookingmanager_ArxSettings));
	     $data["method"] = "setArxConfiguration";
	     $data["interfaceName"] = "core.hotelbookingmanager.IHotelBookingManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Get all references
	* @return void
	* @throws ErrorException
	*/

	public function setBookingConfiguration($core_hotelbookingmanager_GlobalBookingSettings) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["core_hotelbookingmanager_GlobalBookingSettings"] = json_encode($this->transport->object_unset_nulls($core_hotelbookingmanager_GlobalBookingSettings));
	     $data["method"] = "setBookingConfiguration";
	     $data["interfaceName"] = "core.hotelbookingmanager.IHotelBookingManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Get all references
	* @return void
	* @throws ErrorException
	*/

	public function setVismaConfiguration($core_hotelbookingmanager_VismaSettings) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["core_hotelbookingmanager_VismaSettings"] = json_encode($this->transport->object_unset_nulls($core_hotelbookingmanager_VismaSettings));
	     $data["method"] = "setVismaConfiguration";
	     $data["interfaceName"] = "core.hotelbookingmanager.IHotelBookingManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Get all references
	* @return void
	* @throws ErrorException
	*/

	public function updateReservation($core_hotelbookingmanager_BookingReference) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["core_hotelbookingmanager_BookingReference"] = json_encode($this->transport->object_unset_nulls($core_hotelbookingmanager_BookingReference));
	     $data["method"] = "updateReservation";
	     $data["interfaceName"] = "core.hotelbookingmanager.IHotelBookingManager";
	     return $this->transport->sendMessage($data);
	}

}
class APIInformationScreenManager {

	var $transport;
	
	function APIInformationScreenManager($transport) {
		$this->transport = $transport;
	}

	/**
	*
	* @author ktonder
	*/

	public function addSlider($core_informationscreen_Slider, $tvId) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["core_informationscreen_Slider"] = json_encode($this->transport->object_unset_nulls($core_informationscreen_Slider));
	     $data['args']["tvId"] = json_encode($this->transport->object_unset_nulls($tvId));
	     $data["method"] = "addSlider";
	     $data["interfaceName"] = "core.informationscreenmanager.IInformationScreenManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	*
	* @author ktonder
	*/

	public function deleteSlider($sliderId, $tvId) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["sliderId"] = json_encode($this->transport->object_unset_nulls($sliderId));
	     $data['args']["tvId"] = json_encode($this->transport->object_unset_nulls($tvId));
	     $data["method"] = "deleteSlider";
	     $data["interfaceName"] = "core.informationscreenmanager.IInformationScreenManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	*
	* @author ktonder
	*/

	public function getHolders() {
	     $data = array();
	     $data['args'] = array();
	     $data["method"] = "getHolders";
	     $data["interfaceName"] = "core.informationscreenmanager.IInformationScreenManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	*
	* @author ktonder
	*/

	public function getInformationScreens() {
	     $data = array();
	     $data['args'] = array();
	     $data["method"] = "getInformationScreens";
	     $data["interfaceName"] = "core.informationscreenmanager.IInformationScreenManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	*
	* @author ktonder
	*/

	public function getNews() {
	     $data = array();
	     $data['args'] = array();
	     $data["method"] = "getNews";
	     $data["interfaceName"] = "core.informationscreenmanager.IInformationScreenManager";
	     return $this->transport->cast(new core_informationscreen_Feed(), $this->transport->sendMessage($data));
	}

	/**
	*
	* @author ktonder
	*/

	public function getScreen($id) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["id"] = json_encode($this->transport->object_unset_nulls($id));
	     $data["method"] = "getScreen";
	     $data["interfaceName"] = "core.informationscreenmanager.IInformationScreenManager";
	     return $this->transport->cast(new core_informationscreen_InfoScreen(), $this->transport->sendMessage($data));
	}

	/**
	*
	* @author ktonder
	*/

	public function getTypes() {
	     $data = array();
	     $data['args'] = array();
	     $data["method"] = "getTypes";
	     $data["interfaceName"] = "core.informationscreenmanager.IInformationScreenManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	*
	* @author ktonder
	*/

	public function registerTv($customerId) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["customerId"] = json_encode($this->transport->object_unset_nulls($customerId));
	     $data["method"] = "registerTv";
	     $data["interfaceName"] = "core.informationscreenmanager.IInformationScreenManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	*
	* @author ktonder
	*/

	public function saveTv($core_informationscreen_InfoScreen) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["core_informationscreen_InfoScreen"] = json_encode($this->transport->object_unset_nulls($core_informationscreen_InfoScreen));
	     $data["method"] = "saveTv";
	     $data["interfaceName"] = "core.informationscreenmanager.IInformationScreenManager";
	     return $this->transport->sendMessage($data);
	}

}
class APIInvoiceManager {

	var $transport;
	
	function APIInvoiceManager($transport) {
		$this->transport = $transport;
	}

	/**
	*
	* @author ktonder
	*/

	public function createInvoice($orderId) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["orderId"] = json_encode($this->transport->object_unset_nulls($orderId));
	     $data["method"] = "createInvoice";
	     $data["interfaceName"] = "core.pdf.IInvoiceManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	*
	* @author ktonder
	*/

	public function getBase64EncodedInvoice($orderId) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["orderId"] = json_encode($this->transport->object_unset_nulls($orderId));
	     $data["method"] = "getBase64EncodedInvoice";
	     $data["interfaceName"] = "core.pdf.IInvoiceManager";
	     return $this->transport->sendMessage($data);
	}

}
class APIListManager {

	var $transport;
	
	function APIListManager($transport) {
		$this->transport = $transport;
	}

	/**
	* Add a new entry to a given list.<br>
	* For most components like leftmenu, topmenu, footer, category displayer the list id is the same as the application id.<br>
	* When creating an entry, a page will automatically be created and attached to this entry if not exists.<br>
	*
	* @param listId The id for the list to add the entry to, if list does not exists, it will be created automatically.
	* @param entry The entry to append to the list.
	* @param parentPageId See the pagemanager for more information about the page id, when the page to this entry is created set this id as the parent.
	* @return core_listmanager_data_Entry
	*/

	public function addEntry($listId, $core_listmanager_data_Entry, $parentPageId) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["listId"] = json_encode($this->transport->object_unset_nulls($listId));
	     $data['args']["core_listmanager_data_Entry"] = json_encode($this->transport->object_unset_nulls($core_listmanager_data_Entry));
	     $data['args']["parentPageId"] = json_encode($this->transport->object_unset_nulls($parentPageId));
	     $data["method"] = "addEntry";
	     $data["interfaceName"] = "core.listmanager.IListManager";
	     return $this->transport->cast(new core_listmanager_data_Entry(), $this->transport->sendMessage($data));
	}

	/**
	* Remove all list entries for a specified list
	*
	* @param listId
	* @throws ErrorException
	*/

	public function clearList($listId) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["listId"] = json_encode($this->transport->object_unset_nulls($listId));
	     $data["method"] = "clearList";
	     $data["interfaceName"] = "core.listmanager.IListManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* If you would like to combine more lists to a current list, you can do it by using this call.<br>
	*
	* @param toListId The current list to be appended on.
	* @param newListId The list which you would like to combine.
	* @throws ErrorException
	*/

	public function combineList($toListId, $newListId) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["toListId"] = json_encode($this->transport->object_unset_nulls($toListId));
	     $data['args']["newListId"] = json_encode($this->transport->object_unset_nulls($newListId));
	     $data["method"] = "combineList";
	     $data["interfaceName"] = "core.listmanager.IListManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Create a list id, this will create a a new list for you.
	* @return String
	*/

	public function createListId() {
	     $data = array();
	     $data['args'] = array();
	     $data["method"] = "createListId";
	     $data["interfaceName"] = "core.listmanager.IListManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Create new list for a given id
	*
	* @param listName
	* @throws ErrorException
	*/

	public function createMenuList($menuApplicationId) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["menuApplicationId"] = json_encode($this->transport->object_unset_nulls($menuApplicationId));
	     $data["method"] = "createMenuList";
	     $data["interfaceName"] = "core.listmanager.IListManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Delete an already existing entry from a list.
	* @param id The of the entry to delete.
	* @param id The id of the list to remove from.
	*/

	public function deleteEntry($id, $listId) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["id"] = json_encode($this->transport->object_unset_nulls($id));
	     $data['args']["listId"] = json_encode($this->transport->object_unset_nulls($listId));
	     $data["method"] = "deleteEntry";
	     $data["interfaceName"] = "core.listmanager.IListManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Returns the entrylist of a given id.
	*
	* type = MENU
	* type = PRODUCT
	*
	* @return List
	*/

	public function getAllListsByType($type) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["type"] = json_encode($this->transport->object_unset_nulls($type));
	     $data["method"] = "getAllListsByType";
	     $data["interfaceName"] = "core.listmanager.IListManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Fetch a list of all lists combined with a given list.
	* @param listId
	* @throws ErrorException
	*/

	public function getCombinedLists($listId) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["listId"] = json_encode($this->transport->object_unset_nulls($listId));
	     $data["method"] = "getCombinedLists";
	     $data["interfaceName"] = "core.listmanager.IListManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Fetch a list from the system.
	* @param listId The id for the list to fetch
	* @return List
	*/

	public function getList($listId) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["listId"] = json_encode($this->transport->object_unset_nulls($listId));
	     $data["method"] = "getList";
	     $data["interfaceName"] = "core.listmanager.IListManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Fetch a single menu entry.
	* @param id The id for the entry to fetch.
	* @return core_listmanager_data_Entry
	*/

	public function getListEntry($id) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["id"] = json_encode($this->transport->object_unset_nulls($id));
	     $data["method"] = "getListEntry";
	     $data["interfaceName"] = "core.listmanager.IListManager";
	     return $this->transport->cast(new core_listmanager_data_Entry(), $this->transport->sendMessage($data));
	}

	/**
	* Fetch a list of ids that current shop has.<br>
	* This will return a list with the ids for all lists created by this webshop.<br>
	*
	* @return List
	*/

	public function getLists() {
	     $data = array();
	     $data['args'] = array();
	     $data["method"] = "getLists";
	     $data["interfaceName"] = "core.listmanager.IListManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Remove all list entries for a specified list
	*
	* @param listId
	* @throws ErrorException
	*/

	public function getPageIdByName($name) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["name"] = json_encode($this->transport->object_unset_nulls($name));
	     $data["method"] = "getPageIdByName";
	     $data["interfaceName"] = "core.listmanager.IListManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Order a entry on the list.
	* @param id The id for the entry to move / reorder.
	* @param after Put it after a given entry (this will be the id for the given entry). To move the entry to the top leave this empty.
	* @param parent If you want to move the entry into a given entry, then specify the id to the entry here. Leave empty to move to top.
	* @return core_listmanager_data_Entry
	*/

	public function orderEntry($id, $after, $parentId) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["id"] = json_encode($this->transport->object_unset_nulls($id));
	     $data['args']["after"] = json_encode($this->transport->object_unset_nulls($after));
	     $data['args']["parentId"] = json_encode($this->transport->object_unset_nulls($parentId));
	     $data["method"] = "orderEntry";
	     $data["interfaceName"] = "core.listmanager.IListManager";
	     return $this->transport->cast(new core_listmanager_data_Entry(), $this->transport->sendMessage($data));
	}

	/**
	* This function flushes all entries in the list and set this as new entries instead.
	* @param listId The id of the list to be updated
	* @param entries All entries to be included in the list.
	* @throws ErrorException
	*/

	public function setEntries($listId, $entries) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["listId"] = json_encode($this->transport->object_unset_nulls($listId));
	     $data['args']["entries"] = json_encode($this->transport->object_unset_nulls($entries));
	     $data["method"] = "setEntries";
	     $data["interfaceName"] = "core.listmanager.IListManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Translate all antries found in a given list of entry ids.
	* @param entryIds A list of entries id to translate.
	* @return HashMap
	* @throws ErrorException
	*/

	public function translateEntries($entryIds) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["entryIds"] = json_encode($this->transport->object_unset_nulls($entryIds));
	     $data["method"] = "translateEntries";
	     $data["interfaceName"] = "core.listmanager.IListManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Does the exact opposite as combineList(...), removes a list from a combined list.
	* @param fromListId The id of the list to be removed from.
	* @param toRemoveId The id of the list to remove.
	* @throws ErrorException
	*/

	public function unCombineList($fromListId, $toRemoveId) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["fromListId"] = json_encode($this->transport->object_unset_nulls($fromListId));
	     $data['args']["toRemoveId"] = json_encode($this->transport->object_unset_nulls($toRemoveId));
	     $data["method"] = "unCombineList";
	     $data["interfaceName"] = "core.listmanager.IListManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Update an already existing entry
	* @param entry The entry to update.
	* @return void
	*/

	public function updateEntry($core_listmanager_data_Entry) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["core_listmanager_data_Entry"] = json_encode($this->transport->object_unset_nulls($core_listmanager_data_Entry));
	     $data["method"] = "updateEntry";
	     $data["interfaceName"] = "core.listmanager.IListManager";
	     return $this->transport->sendMessage($data);
	}

}
class APILogoManager {

	var $transport;
	
	function APILogoManager($transport) {
		$this->transport = $transport;
	}

	/**
	* Remove the current logo.
	* @return void
	* @throws ErrorException
	*/

	public function deleteLogo() {
	     $data = array();
	     $data['args'] = array();
	     $data["method"] = "deleteLogo";
	     $data["interfaceName"] = "app.logo.ILogoManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Get the logo file id.
	* @return app_logomanager_data_SavedLogo
	* @throws ErrorException
	*/

	public function getLogo() {
	     $data = array();
	     $data['args'] = array();
	     $data["method"] = "getLogo";
	     $data["interfaceName"] = "app.logo.ILogoManager";
	     return $this->transport->cast(new app_logomanager_data_SavedLogo(), $this->transport->sendMessage($data));
	}

	/**
	* Set a logo to your webshop.
	* @param fileId The file id generated by the one storing this.
	* @return void
	* @throws ErrorException
	*/

	public function setLogo($fileId) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["fileId"] = json_encode($this->transport->object_unset_nulls($fileId));
	     $data["method"] = "setLogo";
	     $data["interfaceName"] = "app.logo.ILogoManager";
	     return $this->transport->sendMessage($data);
	}

}
class APIMessageManager {

	var $transport;
	
	function APIMessageManager($transport) {
		$this->transport = $transport;
	}

	/**
	* Get how many messages a user has sent.
	*
	* @param year
	* @param month
	* @return void
	*/

	public function collectEmail($email) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["email"] = json_encode($this->transport->object_unset_nulls($email));
	     $data["method"] = "collectEmail";
	     $data["interfaceName"] = "core.messagemanager.IMessageManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Get how many messages a user has sent.
	*
	* @param year
	* @param month
	* @return int
	*/

	public function getSmsCount($year, $month) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["year"] = json_encode($this->transport->object_unset_nulls($year));
	     $data['args']["month"] = json_encode($this->transport->object_unset_nulls($month));
	     $data["method"] = "getSmsCount";
	     $data["interfaceName"] = "core.messagemanager.IMessageManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Send a mail.
	* @param to The address to send to
	* @param toName The name of the one receiving it.
	* @param subject The subject of the mail.
	* @param content The content to send
	* @param from The email sent from.
	* @param fromName The name of the sender.
	*/

	public function sendMail($to, $toName, $subject, $content, $from, $fromName) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["to"] = json_encode($this->transport->object_unset_nulls($to));
	     $data['args']["toName"] = json_encode($this->transport->object_unset_nulls($toName));
	     $data['args']["subject"] = json_encode($this->transport->object_unset_nulls($subject));
	     $data['args']["content"] = json_encode($this->transport->object_unset_nulls($content));
	     $data['args']["from"] = json_encode($this->transport->object_unset_nulls($from));
	     $data['args']["fromName"] = json_encode($this->transport->object_unset_nulls($fromName));
	     $data["method"] = "sendMail";
	     $data["interfaceName"] = "core.messagemanager.IMessageManager";
	     return $this->transport->sendMessage($data);
	}

}
class APIMobileManager {

	var $transport;
	
	function APIMobileManager($transport) {
		$this->transport = $transport;
	}

	/**
	* Clears the badged number.
	*
	* @param tokenId
	*/

	public function clearBadged($tokenId) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["tokenId"] = json_encode($this->transport->object_unset_nulls($tokenId));
	     $data["method"] = "clearBadged";
	     $data["interfaceName"] = "core.mobilemanager.IMobileManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Register a token to the system.
	* This token is later on used for sending messages
	* back to the unit.
	* s
	* @param token
	*/

	public function registerToken($core_mobilemanager_data_Token) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["core_mobilemanager_data_Token"] = json_encode($this->transport->object_unset_nulls($core_mobilemanager_data_Token));
	     $data["method"] = "registerToken";
	     $data["interfaceName"] = "core.mobilemanager.IMobileManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Sends the message to all registered units.
	*
	* @param message
	*/

	public function sendMessageToAll($message) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["message"] = json_encode($this->transport->object_unset_nulls($message));
	     $data["method"] = "sendMessageToAll";
	     $data["interfaceName"] = "core.mobilemanager.IMobileManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Sends the message to all units that are registered as test units.
	*
	* @param message
	*/

	public function sendMessageToAllTestUnits($message) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["message"] = json_encode($this->transport->object_unset_nulls($message));
	     $data["method"] = "sendMessageToAllTestUnits";
	     $data["interfaceName"] = "core.mobilemanager.IMobileManager";
	     return $this->transport->sendMessage($data);
	}

}
class APINewsLetterManager {

	var $transport;
	
	function APINewsLetterManager($transport) {
		$this->transport = $transport;
	}

	/**
	* Calling this function will start sending newsletter with a five minute interval for all recipients.
	* @param group
	*/

	public function sendNewsLetter($core_messagemanager_NewsLetterGroup) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["core_messagemanager_NewsLetterGroup"] = json_encode($this->transport->object_unset_nulls($core_messagemanager_NewsLetterGroup));
	     $data["method"] = "sendNewsLetter";
	     $data["interfaceName"] = "core.messagemanager.INewsLetterManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Send a preview to the selected contacts.
	* @param group
	*/

	public function sendNewsLetterPreview($core_messagemanager_NewsLetterGroup) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["core_messagemanager_NewsLetterGroup"] = json_encode($this->transport->object_unset_nulls($core_messagemanager_NewsLetterGroup));
	     $data["method"] = "sendNewsLetterPreview";
	     $data["interfaceName"] = "core.messagemanager.INewsLetterManager";
	     return $this->transport->sendMessage($data);
	}

}
class APINewsManager {

	var $transport;
	
	function APINewsManager($transport) {
		$this->transport = $transport;
	}

	/**
	* Add a new news entry.
	* @param news The news object to add.
	* @return String
	* @throws ErrorException
	*/

	public function addNews($app_newsmanager_data_NewsEntry) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["app_newsmanager_data_NewsEntry"] = json_encode($this->transport->object_unset_nulls($app_newsmanager_data_NewsEntry));
	     $data["method"] = "addNews";
	     $data["interfaceName"] = "app.news.INewsManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Add a subscriber.
	* Whenever a new news is updated to this, the subscribe will get an email.
	* @param email The email address for the subscriber.
	* @return app_newsmanager_data_MailSubscription
	* @throws ErrorException
	*/

	public function addSubscriber($email) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["email"] = json_encode($this->transport->object_unset_nulls($email));
	     $data["method"] = "addSubscriber";
	     $data["interfaceName"] = "app.news.INewsManager";
	     return $this->transport->cast(new app_newsmanager_data_MailSubscription(), $this->transport->sendMessage($data));
	}

	/**
	* Delete a given news id.
	* @param id The id for the news to delete.
	* @throws ErrorException
	*/

	public function deleteNews($id) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["id"] = json_encode($this->transport->object_unset_nulls($id));
	     $data["method"] = "deleteNews";
	     $data["interfaceName"] = "app.news.INewsManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Fetch all news added.
	* @return List
	* @throws ErrorException
	*/

	public function getAllNews() {
	     $data = array();
	     $data['args'] = array();
	     $data["method"] = "getAllNews";
	     $data["interfaceName"] = "app.news.INewsManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Get all subscribers.
	* @return List
	* @throws ErrorException
	*/

	public function getAllSubscribers() {
	     $data = array();
	     $data['args'] = array();
	     $data["method"] = "getAllSubscribers";
	     $data["interfaceName"] = "app.news.INewsManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Publishing news.
	*
	* @param id
	* @throws ErrorException
	*/

	public function publishNews($id) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["id"] = json_encode($this->transport->object_unset_nulls($id));
	     $data["method"] = "publishNews";
	     $data["interfaceName"] = "app.news.INewsManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Remove an existing subscriber.
	* @param subscriberId The subscribers id found in the MailSubscriber object.
	* @return void
	* @throws ErrorException
	*/

	public function removeSubscriber($subscriberId) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["subscriberId"] = json_encode($this->transport->object_unset_nulls($subscriberId));
	     $data["method"] = "removeSubscriber";
	     $data["interfaceName"] = "app.news.INewsManager";
	     return $this->transport->sendMessage($data);
	}

}
class APIOrderManager {

	var $transport;
	
	function APIOrderManager($transport) {
		$this->transport = $transport;
	}

	/**
	* Change order status of a specified order.
	* The id could be the orderId or the transaction id.
	*/

	public function changeOrderStatus($id, $status) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["id"] = json_encode($this->transport->object_unset_nulls($id));
	     $data['args']["status"] = json_encode($this->transport->object_unset_nulls($status));
	     $data["method"] = "changeOrderStatus";
	     $data["interfaceName"] = "core.ordermanager.IOrderManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Create an order out of a given cart.
	* @param cart The cart object generated by cartmanager.
	* @return core_ordermanager_data_Order
	* @throws ErrorException
	*/

	public function createOrder($core_usermanager_data_Address) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["core_usermanager_data_Address"] = json_encode($this->transport->object_unset_nulls($core_usermanager_data_Address));
	     $data["method"] = "createOrder";
	     $data["interfaceName"] = "core.ordermanager.IOrderManager";
	     return $this->transport->cast(new core_ordermanager_data_Order(), $this->transport->sendMessage($data));
	}

	/**
	* If a customer is providing a customer reference id, it should be possible to create order by it.
	* @param cart The cart object generated by cartmanager.
	* @return core_ordermanager_data_Order
	* @throws ErrorException
	*/

	public function createOrderByCustomerReference($referenceKey) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["referenceKey"] = json_encode($this->transport->object_unset_nulls($referenceKey));
	     $data["method"] = "createOrderByCustomerReference";
	     $data["interfaceName"] = "core.ordermanager.IOrderManager";
	     return $this->transport->cast(new core_ordermanager_data_Order(), $this->transport->sendMessage($data));
	}

	/**
	* This will create a order for a given userId.
	* To avoid fraud, shipment address and etc will only be
	* able to set to the already registered user in the database.
	*
	* @param userId
	*/

	public function createOrderForUser($userId) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["userId"] = json_encode($this->transport->object_unset_nulls($userId));
	     $data["method"] = "createOrderForUser";
	     $data["interfaceName"] = "core.ordermanager.IOrderManager";
	     return $this->transport->cast(new core_ordermanager_data_Order(), $this->transport->sendMessage($data));
	}

	/**
	* Fetch all orders for a user.
	* @param userId
	* @return List
	* @throws ErrorException
	*/

	public function getAllOrdersForUser($userId) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["userId"] = json_encode($this->transport->object_unset_nulls($userId));
	     $data["method"] = "getAllOrdersForUser";
	     $data["interfaceName"] = "core.ordermanager.IOrderManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Fetch all orders on product.
	* @param userId
	* @return List
	* @throws ErrorException
	*/

	public function getAllOrdersOnProduct($productId) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["productId"] = json_encode($this->transport->object_unset_nulls($productId));
	     $data["method"] = "getAllOrdersOnProduct";
	     $data["interfaceName"] = "core.ordermanager.IOrderManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Returns the total amount of sales for a given year. If you year is left blank you
	* will get the total amount for all years.
	*
	* @param year
	* @return Map
	*/

	public function getMostSoldProducts($numberOfProducts) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["numberOfProducts"] = json_encode($this->transport->object_unset_nulls($numberOfProducts));
	     $data["method"] = "getMostSoldProducts";
	     $data["interfaceName"] = "core.ordermanager.IOrderManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Fetch a single order based on its id.
	* @param orderId
	* @return core_ordermanager_data_Order
	* @throws ErrorException
	*/

	public function getOrder($orderId) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["orderId"] = json_encode($this->transport->object_unset_nulls($orderId));
	     $data["method"] = "getOrder";
	     $data["interfaceName"] = "core.ordermanager.IOrderManager";
	     return $this->transport->cast(new core_ordermanager_data_Order(), $this->transport->sendMessage($data));
	}

	/**
	* Got a reference number for the order, fetch it from here.
	* @param referenceId
	* @throws ErrorException
	*/

	public function getOrderByReference($referenceId) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["referenceId"] = json_encode($this->transport->object_unset_nulls($referenceId));
	     $data["method"] = "getOrderByReference";
	     $data["interfaceName"] = "core.ordermanager.IOrderManager";
	     return $this->transport->cast(new core_ordermanager_data_Order(), $this->transport->sendMessage($data));
	}

	/**
	* @param id
	* @return core_ordermanager_data_Order
	* @throws ErrorException
	*/

	public function getOrderByincrementOrderId($id) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["id"] = json_encode($this->transport->object_unset_nulls($id));
	     $data["method"] = "getOrderByincrementOrderId";
	     $data["interfaceName"] = "core.ordermanager.IOrderManager";
	     return $this->transport->cast(new core_ordermanager_data_Order(), $this->transport->sendMessage($data));
	}

	/**
	* Get a list of already created orders.
	* @param orderIds A list of all orders you want to fetch, all orders are retrieved if this list is empty.
	* @param page What page are you fetching (default 0)
	* @param pageSize Number of entries for each page (default 10)
	* @return List
	* @throws ErrorException
	*/

	public function getOrders($orderIds, $page, $pageSize) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["orderIds"] = json_encode($this->transport->object_unset_nulls($orderIds));
	     $data['args']["page"] = json_encode($this->transport->object_unset_nulls($page));
	     $data['args']["pageSize"] = json_encode($this->transport->object_unset_nulls($pageSize));
	     $data["method"] = "getOrders";
	     $data["interfaceName"] = "core.ordermanager.IOrderManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Returns the total amount of sales for a given year. If you year is left blank you
	* will get the total amount for all years.
	*
	* @param year
	* @return List
	*/

	public function getOrdersNotTransferredToAccountingSystem() {
	     $data = array();
	     $data['args'] = array();
	     $data["method"] = "getOrdersNotTransferredToAccountingSystem";
	     $data["interfaceName"] = "core.ordermanager.IOrderManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Returns how many pages there is for this store with the given pagesize
	* @return int
	*/

	public function getPageCount($pageSize, $searchWord) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["pageSize"] = json_encode($this->transport->object_unset_nulls($pageSize));
	     $data['args']["searchWord"] = json_encode($this->transport->object_unset_nulls($searchWord));
	     $data["method"] = "getPageCount";
	     $data["interfaceName"] = "core.ordermanager.IOrderManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Returns the total amount of sales for a given year. If you year is left blank you
	* will get the total amount for all years.
	*
	* @param year
	* @return List
	*/

	public function getSalesNumber($year) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["year"] = json_encode($this->transport->object_unset_nulls($year));
	     $data["method"] = "getSalesNumber";
	     $data["interfaceName"] = "core.ordermanager.IOrderManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Returns a list over taxes
	* for the specified order.
	*
	* @param order
	* @return List
	* @throws ErrorException
	*/

	public function getTaxes($core_ordermanager_data_Order) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["core_ordermanager_data_Order"] = json_encode($this->transport->object_unset_nulls($core_ordermanager_data_Order));
	     $data["method"] = "getTaxes";
	     $data["interfaceName"] = "core.ordermanager.IOrderManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Calculate the total amount to pay for the order.
	*
	* @param order
	* @return Double
	*/

	public function getTotalAmount($core_ordermanager_data_Order) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["core_ordermanager_data_Order"] = json_encode($this->transport->object_unset_nulls($core_ordermanager_data_Order));
	     $data["method"] = "getTotalAmount";
	     $data["interfaceName"] = "core.ordermanager.IOrderManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Returns the total amount of sales for a given year. If you year is left blank you
	* will get the total amount for all years.
	*
	* @param year
	* @return Double
	*/

	public function getTotalSalesAmount($year) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["year"] = json_encode($this->transport->object_unset_nulls($year));
	     $data["method"] = "getTotalSalesAmount";
	     $data["interfaceName"] = "core.ordermanager.IOrderManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Returns the total amount of sales for a given year. If you year is left blank you
	* will get the total amount for all years.
	*
	* @param year
	* @return void
	*/

	public function logTransactionEntry($orderId, $entry) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["orderId"] = json_encode($this->transport->object_unset_nulls($orderId));
	     $data['args']["entry"] = json_encode($this->transport->object_unset_nulls($entry));
	     $data["method"] = "logTransactionEntry";
	     $data["interfaceName"] = "core.ordermanager.IOrderManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Update or modify an existing order.
	* @param order The order to modify
	* @return void
	* @throws ErrorException
	*/

	public function saveOrder($core_ordermanager_data_Order) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["core_ordermanager_data_Order"] = json_encode($this->transport->object_unset_nulls($core_ordermanager_data_Order));
	     $data["method"] = "saveOrder";
	     $data["interfaceName"] = "core.ordermanager.IOrderManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Returns how many pages there is for this store with the given pagesize
	* @return List
	*/

	public function searchForOrders($searchWord, $page, $pageSize) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["searchWord"] = json_encode($this->transport->object_unset_nulls($searchWord));
	     $data['args']["page"] = json_encode($this->transport->object_unset_nulls($page));
	     $data['args']["pageSize"] = json_encode($this->transport->object_unset_nulls($pageSize));
	     $data["method"] = "searchForOrders";
	     $data["interfaceName"] = "core.ordermanager.IOrderManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* If everything is ok, the price is the same as the order and the currency, then update the status.
	* @param password A predefined password needed to update the status.
	* @param orderId The id of the order to update
	* @param currency The currency the transaction returned
	* @param price The price.
	* @throws ErrorException
	*/

	public function setOrderStatus($password, $orderId, $currency, $price, $status) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["password"] = json_encode($this->transport->object_unset_nulls($password));
	     $data['args']["orderId"] = json_encode($this->transport->object_unset_nulls($orderId));
	     $data['args']["currency"] = json_encode($this->transport->object_unset_nulls($currency));
	     $data['args']["price"] = json_encode($this->transport->object_unset_nulls($price));
	     $data['args']["status"] = json_encode($this->transport->object_unset_nulls($status));
	     $data["method"] = "setOrderStatus";
	     $data["interfaceName"] = "core.ordermanager.IOrderManager";
	     return $this->transport->sendMessage($data);
	}

}
class APIPageManager {

	var $transport;
	
	function APIPageManager($transport) {
		$this->transport = $transport;
	}

	/**
	* Add application
	* @param id
	* @return core_common_ApplicationInstance
	* @throws ErrorException
	*/

	public function addApplication($applicationId, $pageCellId) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["applicationId"] = json_encode($this->transport->object_unset_nulls($applicationId));
	     $data['args']["pageCellId"] = json_encode($this->transport->object_unset_nulls($pageCellId));
	     $data["method"] = "addApplication";
	     $data["interfaceName"] = "core.pagemanager.IPageManager";
	     return $this->transport->cast(new core_common_ApplicationInstance(), $this->transport->sendMessage($data));
	}

	/**
	* Add an existing application to the application area
	*
	* @param pageId
	* @param appId
	* @param area
	* @throws ErrorException
	*/

	public function addExistingApplicationToPageArea($pageId, $appId, $area) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["pageId"] = json_encode($this->transport->object_unset_nulls($pageId));
	     $data['args']["appId"] = json_encode($this->transport->object_unset_nulls($appId));
	     $data['args']["area"] = json_encode($this->transport->object_unset_nulls($area));
	     $data["method"] = "addExistingApplicationToPageArea";
	     $data["interfaceName"] = "core.pagemanager.IPageManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Add an cell to an specific earea.
	* @param pageId
	* @param incell
	* @param beforecell
	* @param direction
	* @param area header/footer/body if nothing set it will default to body.
	* @return String
	* @throws ErrorException
	*/

	public function addLayoutCell($pageId, $incell, $beforecell, $direction, $area) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["pageId"] = json_encode($this->transport->object_unset_nulls($pageId));
	     $data['args']["incell"] = json_encode($this->transport->object_unset_nulls($incell));
	     $data['args']["beforecell"] = json_encode($this->transport->object_unset_nulls($beforecell));
	     $data['args']["direction"] = json_encode($this->transport->object_unset_nulls($direction));
	     $data['args']["area"] = json_encode($this->transport->object_unset_nulls($area));
	     $data["method"] = "addLayoutCell";
	     $data["interfaceName"] = "core.pagemanager.IPageManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Change the userlevel for a given page. Make it accessible for only administrators / editors / customers.<br>
	* Everyone with a higher userlevel will allways gain access to the userlevels below.
	* @param pageId The id of the page to change.
	* @param userLevel The userlevel to set ADMINISTRATOR = 100, EDITOR = 50, CUSTOMER = 10
	* @return core_pagemanager_data_Page
	* @throws ErrorException
	*/

	public function changePageUserLevel($pageId, $userLevel) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["pageId"] = json_encode($this->transport->object_unset_nulls($pageId));
	     $data['args']["userLevel"] = json_encode($this->transport->object_unset_nulls($userLevel));
	     $data["method"] = "changePageUserLevel";
	     $data["interfaceName"] = "core.pagemanager.IPageManager";
	     return $this->transport->cast(new core_pagemanager_data_Page(), $this->transport->sendMessage($data));
	}

	/**
	* Remove all content on all page areas for this page.
	* @param pageId
	* @throws ErrorException
	*/

	public function clearPage($pageId) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["pageId"] = json_encode($this->transport->object_unset_nulls($pageId));
	     $data["method"] = "clearPage";
	     $data["interfaceName"] = "core.pagemanager.IPageManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Remove all applications for specified page area at specified page.
	*
	* @param pageId
	* @throws ErrorException
	*/

	public function clearPageArea($pageId, $pageArea) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["pageId"] = json_encode($this->transport->object_unset_nulls($pageId));
	     $data['args']["pageArea"] = json_encode($this->transport->object_unset_nulls($pageArea));
	     $data["method"] = "clearPageArea";
	     $data["interfaceName"] = "core.pagemanager.IPageManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Add an cell to an specific earea.
	* @param pageId
	* @param incell
	* @param beforecell
	* @param direction
	* @param area header/footer/body if nothing set it will default to body.
	* @return void
	* @throws ErrorException
	*/

	public function createHeaderFooter($type) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["type"] = json_encode($this->transport->object_unset_nulls($type));
	     $data["method"] = "createHeaderFooter";
	     $data["interfaceName"] = "core.pagemanager.IPageManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Create a new row to add application areas to for a given page.
	* @param pageId
	* @return String
	* @throws ErrorException
	*/

	public function createNewRow($pageId) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["pageId"] = json_encode($this->transport->object_unset_nulls($pageId));
	     $data["method"] = "createNewRow";
	     $data["interfaceName"] = "core.pagemanager.IPageManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Create a new page.
	* This page can be used to stick applications to it.
	*
	* @return core_pagemanager_data_Page
	* @throws ErrorException
	*/

	public function createPage() {
	     $data = array();
	     $data['args'] = array();
	     $data["method"] = "createPage";
	     $data["interfaceName"] = "core.pagemanager.IPageManager";
	     return $this->transport->cast(new core_pagemanager_data_Page(), $this->transport->sendMessage($data));
	}

	/**
	* Delete an application from the store
	* removes all references where it has been used.
	*
	* Suitable for singleton applications
	*
	* @param id
	* @throws ErrorException
	*/

	public function deleteApplication($id) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["id"] = json_encode($this->transport->object_unset_nulls($id));
	     $data["method"] = "deleteApplication";
	     $data["interfaceName"] = "core.pagemanager.IPageManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Delete the page with the id.
	*
	* @param id
	*/

	public function deletePage($id) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["id"] = json_encode($this->transport->object_unset_nulls($id));
	     $data["method"] = "deletePage";
	     $data["interfaceName"] = "core.pagemanager.IPageManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Add an cell to an specific earea.
	* @param pageId
	* @param incell
	* @param beforecell
	* @param direction
	* @param area header/footer/body if nothing set it will default to body.
	* @return core_pagemanager_data_Page
	* @throws ErrorException
	*/

	public function dropCell($pageId, $cellId) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["pageId"] = json_encode($this->transport->object_unset_nulls($pageId));
	     $data['args']["cellId"] = json_encode($this->transport->object_unset_nulls($cellId));
	     $data["method"] = "dropCell";
	     $data["interfaceName"] = "core.pagemanager.IPageManager";
	     return $this->transport->cast(new core_pagemanager_data_Page(), $this->transport->sendMessage($data));
	}

	/**
	* Get all applications from the applicationPool.
	*
	* @return List
	*/

	public function getApplications() {
	     $data = array();
	     $data['args'] = array();
	     $data["method"] = "getApplications";
	     $data["interfaceName"] = "core.pagemanager.IPageManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Get all applications from the applicationPool.
	* based on the specified ApplicationSettingsId
	*
	* @return List
	*/

	public function getApplicationsBasedOnApplicationSettingsId($appSettingsId) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["appSettingsId"] = json_encode($this->transport->object_unset_nulls($appSettingsId));
	     $data["method"] = "getApplicationsBasedOnApplicationSettingsId";
	     $data["interfaceName"] = "core.pagemanager.IPageManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Get all applications from the applicationPool.
	* based on the specified ApplicationSettingsId
	*
	* @return List
	*/

	public function getApplicationsByPageAreaAndSettingsId($appSettingsId, $pageArea) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["appSettingsId"] = json_encode($this->transport->object_unset_nulls($appSettingsId));
	     $data['args']["pageArea"] = json_encode($this->transport->object_unset_nulls($pageArea));
	     $data["method"] = "getApplicationsByPageAreaAndSettingsId";
	     $data["interfaceName"] = "core.pagemanager.IPageManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Fetch all application from the applicationPool (added applications) which has a given type.
	* @param type
	* @return List
	* @throws ErrorException
	*/

	public function getApplicationsByType($type) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["type"] = json_encode($this->transport->object_unset_nulls($type));
	     $data["method"] = "getApplicationsByType";
	     $data["interfaceName"] = "core.pagemanager.IPageManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Get all applications that is needed to render a page.
	*
	* @param pageId
	* @return List
	* @throws ErrorException
	*/

	public function getApplicationsForPage($pageId) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["pageId"] = json_encode($this->transport->object_unset_nulls($pageId));
	     $data["method"] = "getApplicationsForPage";
	     $data["interfaceName"] = "core.pagemanager.IPageManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Set the carousel configuration.
	* @param pageId
	* @return core_pagemanager_data_PageCell
	* @throws ErrorException
	*/

	public function getCell($pageId, $cellId) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["pageId"] = json_encode($this->transport->object_unset_nulls($pageId));
	     $data['args']["cellId"] = json_encode($this->transport->object_unset_nulls($cellId));
	     $data["method"] = "getCell";
	     $data["interfaceName"] = "core.pagemanager.IPageManager";
	     return $this->transport->cast(new core_pagemanager_data_PageCell(), $this->transport->sendMessage($data));
	}

	/**
	* fetch an existing page.
	* @param id The id for the page to fetch.
	* @return core_pagemanager_data_Page
	* @throws ErrorException
	*/

	public function getPage($id) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["id"] = json_encode($this->transport->object_unset_nulls($id));
	     $data["method"] = "getPage";
	     $data["interfaceName"] = "core.pagemanager.IPageManager";
	     return $this->transport->cast(new core_pagemanager_data_Page(), $this->transport->sendMessage($data));
	}

	/**
	* Fetch a list of all pages found for a list of applications.<br>
	* The key is the application id, the list combined with the key a list of page ids found for the specified applications.
	* @param appIds A list of application ids to resolve pages for.
	* @throws ErrorException
	*/

	public function getPagesForApplications($appIds) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["appIds"] = json_encode($this->transport->object_unset_nulls($appIds));
	     $data["method"] = "getPagesForApplications";
	     $data["interfaceName"] = "core.pagemanager.IPageManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Get secured settings
	*/

	public function getSecuredSettings($applicationInstanceId) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["applicationInstanceId"] = json_encode($this->transport->object_unset_nulls($applicationInstanceId));
	     $data["method"] = "getSecuredSettings";
	     $data["interfaceName"] = "core.pagemanager.IPageManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Change the userlevel for a given page. Make it accessible for only administrators / editors / customers.<br>
	* Everyone with a higher userlevel will allways gain access to the userlevels below.
	* @param pageId The id of the page to change.
	* @param userLevel The userlevel to set ADMINISTRATOR = 100, EDITOR = 50, CUSTOMER = 10
	* @return HashMap
	* @throws ErrorException
	*/

	public function getSecuredSettingsInternal($appName) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["appName"] = json_encode($this->transport->object_unset_nulls($appName));
	     $data["method"] = "getSecuredSettingsInternal";
	     $data["interfaceName"] = "core.pagemanager.IPageManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Remove all content on all page areas for this page.
	* @param pageId
	* @throws ErrorException
	*/

	public function linkPageCell($pageId, $cellId, $link) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["pageId"] = json_encode($this->transport->object_unset_nulls($pageId));
	     $data['args']["cellId"] = json_encode($this->transport->object_unset_nulls($cellId));
	     $data['args']["link"] = json_encode($this->transport->object_unset_nulls($link));
	     $data["method"] = "linkPageCell";
	     $data["interfaceName"] = "core.pagemanager.IPageManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Move a cell either up or down.
	* @param pageId
	* @return void
	* @throws ErrorException
	*/

	public function moveCell($pageId, $cellId, $up) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["pageId"] = json_encode($this->transport->object_unset_nulls($pageId));
	     $data['args']["cellId"] = json_encode($this->transport->object_unset_nulls($cellId));
	     $data['args']["up"] = json_encode($this->transport->object_unset_nulls($up));
	     $data["method"] = "moveCell";
	     $data["interfaceName"] = "core.pagemanager.IPageManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Set the carousel configuration.
	* @param pageId
	* @return void
	* @throws ErrorException
	*/

	public function moveCellMobile($pageId, $cellId, $moveUp) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["pageId"] = json_encode($this->transport->object_unset_nulls($pageId));
	     $data['args']["cellId"] = json_encode($this->transport->object_unset_nulls($cellId));
	     $data['args']["moveUp"] = json_encode($this->transport->object_unset_nulls($moveUp));
	     $data["method"] = "moveCellMobile";
	     $data["interfaceName"] = "core.pagemanager.IPageManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Remove an application
	*
	* @param pageAreaId The id of the page area to remove.
	* @return core_pagemanager_data_Page
	* @throws ErrorException
	*/

	public function removeAppFromCell($pageId, $cellid) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["pageId"] = json_encode($this->transport->object_unset_nulls($pageId));
	     $data['args']["cellid"] = json_encode($this->transport->object_unset_nulls($cellid));
	     $data["method"] = "removeAppFromCell";
	     $data["interfaceName"] = "core.pagemanager.IPageManager";
	     return $this->transport->cast(new core_pagemanager_data_Page(), $this->transport->sendMessage($data));
	}

	/**
	* For each instance of the application, there is an configuration object attached.<br>
	* Modify this object to set an application sticky, inheritable etc.
	* @param config The appconfiguration object to update / save.
	* @throws ErrorException
	*/

	public function saveApplicationConfiguration($core_common_ApplicationInstance) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["core_common_ApplicationInstance"] = json_encode($this->transport->object_unset_nulls($core_common_ApplicationInstance));
	     $data["method"] = "saveApplicationConfiguration";
	     $data["interfaceName"] = "core.pagemanager.IPageManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Set the carousel configuration.
	* @param pageId
	* @return void
	* @throws ErrorException
	*/

	public function saveCellPosition($pageId, $cellId, $core_pagemanager_data_FloatingData) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["pageId"] = json_encode($this->transport->object_unset_nulls($pageId));
	     $data['args']["cellId"] = json_encode($this->transport->object_unset_nulls($cellId));
	     $data['args']["core_pagemanager_data_FloatingData"] = json_encode($this->transport->object_unset_nulls($core_pagemanager_data_FloatingData));
	     $data["method"] = "saveCellPosition";
	     $data["interfaceName"] = "core.pagemanager.IPageManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Save the page
	*/

	public function savePage($core_pagemanager_data_Page) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["core_pagemanager_data_Page"] = json_encode($this->transport->object_unset_nulls($core_pagemanager_data_Page));
	     $data["method"] = "savePage";
	     $data["interfaceName"] = "core.pagemanager.IPageManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Set the carousel configuration.
	* @param pageId
	* @return void
	* @throws ErrorException
	*/

	public function setCarouselConfig($pageId, $cellId, $core_pagemanager_data_CarouselConfig) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["pageId"] = json_encode($this->transport->object_unset_nulls($pageId));
	     $data['args']["cellId"] = json_encode($this->transport->object_unset_nulls($cellId));
	     $data['args']["core_pagemanager_data_CarouselConfig"] = json_encode($this->transport->object_unset_nulls($core_pagemanager_data_CarouselConfig));
	     $data["method"] = "setCarouselConfig";
	     $data["interfaceName"] = "core.pagemanager.IPageManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Set the carousel configuration.
	* @param pageId
	* @return void
	* @throws ErrorException
	*/

	public function setCellMode($pageId, $cellId, $mode) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["pageId"] = json_encode($this->transport->object_unset_nulls($pageId));
	     $data['args']["cellId"] = json_encode($this->transport->object_unset_nulls($cellId));
	     $data['args']["mode"] = json_encode($this->transport->object_unset_nulls($mode));
	     $data["method"] = "setCellMode";
	     $data["interfaceName"] = "core.pagemanager.IPageManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Set the carousel configuration.
	* @param pageId
	* @return void
	* @throws ErrorException
	*/

	public function setCellName($pageId, $cellId, $cellName) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["pageId"] = json_encode($this->transport->object_unset_nulls($pageId));
	     $data['args']["cellId"] = json_encode($this->transport->object_unset_nulls($cellId));
	     $data['args']["cellName"] = json_encode($this->transport->object_unset_nulls($cellName));
	     $data["method"] = "setCellName";
	     $data["interfaceName"] = "core.pagemanager.IPageManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Set the page description.
	* @param description The description to add.
	* @param pageId The id of the page.
	* @throws ErrorException
	*/

	public function setPageDescription($pageId, $description) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["pageId"] = json_encode($this->transport->object_unset_nulls($pageId));
	     $data['args']["description"] = json_encode($this->transport->object_unset_nulls($description));
	     $data["method"] = "setPageDescription";
	     $data["interfaceName"] = "core.pagemanager.IPageManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Update a page and give it a parent page. <br>
	* This is used to figure out a hiarcy for the menues.<br>
	* @param pageId The page to have a parent page.
	* @param parentPageId The id of the page to be set as the parent page.
	* @throws ErrorException
	*/

	public function setParentPage($pageId, $parentPageId) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["pageId"] = json_encode($this->transport->object_unset_nulls($pageId));
	     $data['args']["parentPageId"] = json_encode($this->transport->object_unset_nulls($parentPageId));
	     $data["method"] = "setParentPage";
	     $data["interfaceName"] = "core.pagemanager.IPageManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Add application
	* @param id
	* @return void
	* @throws ErrorException
	*/

	public function setStylesOnCell($pageId, $cellId, $styles, $innerStyles, $width) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["pageId"] = json_encode($this->transport->object_unset_nulls($pageId));
	     $data['args']["cellId"] = json_encode($this->transport->object_unset_nulls($cellId));
	     $data['args']["styles"] = json_encode($this->transport->object_unset_nulls($styles));
	     $data['args']["innerStyles"] = json_encode($this->transport->object_unset_nulls($innerStyles));
	     $data['args']["width"] = json_encode($this->transport->object_unset_nulls($width));
	     $data["method"] = "setStylesOnCell";
	     $data["interfaceName"] = "core.pagemanager.IPageManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Set the carousel configuration.
	* @param pageId
	* @return void
	* @throws ErrorException
	*/

	public function setWidth($pageId, $cellId, $outerWidth, $outerWidthWithMargins) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["pageId"] = json_encode($this->transport->object_unset_nulls($pageId));
	     $data['args']["cellId"] = json_encode($this->transport->object_unset_nulls($cellId));
	     $data['args']["outerWidth"] = json_encode($this->transport->object_unset_nulls($outerWidth));
	     $data['args']["outerWidthWithMargins"] = json_encode($this->transport->object_unset_nulls($outerWidthWithMargins));
	     $data["method"] = "setWidth";
	     $data["interfaceName"] = "core.pagemanager.IPageManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Add application
	* @param id
	* @return void
	* @throws ErrorException
	*/

	public function swapAppWithCell($pageId, $fromCellId, $toCellId) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["pageId"] = json_encode($this->transport->object_unset_nulls($pageId));
	     $data['args']["fromCellId"] = json_encode($this->transport->object_unset_nulls($fromCellId));
	     $data['args']["toCellId"] = json_encode($this->transport->object_unset_nulls($toCellId));
	     $data["method"] = "swapAppWithCell";
	     $data["interfaceName"] = "core.pagemanager.IPageManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Set the carousel configuration.
	* @param pageId
	* @return void
	* @throws ErrorException
	*/

	public function toggleHiddenOnMobile($pageId, $cellId, $hide) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["pageId"] = json_encode($this->transport->object_unset_nulls($pageId));
	     $data['args']["cellId"] = json_encode($this->transport->object_unset_nulls($cellId));
	     $data['args']["hide"] = json_encode($this->transport->object_unset_nulls($hide));
	     $data["method"] = "toggleHiddenOnMobile";
	     $data["interfaceName"] = "core.pagemanager.IPageManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Set the carousel configuration.
	* @param pageId
	* @return void
	* @throws ErrorException
	*/

	public function togglePinArea($pageId, $cellId) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["pageId"] = json_encode($this->transport->object_unset_nulls($pageId));
	     $data['args']["cellId"] = json_encode($this->transport->object_unset_nulls($cellId));
	     $data["method"] = "togglePinArea";
	     $data["interfaceName"] = "core.pagemanager.IPageManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Need to translate a set of page ids?
	* @param pages A list (array) of page ids to translate.
	* @return HashMap
	* @throws ErrorException
	*/

	public function translatePages($pages) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["pages"] = json_encode($this->transport->object_unset_nulls($pages));
	     $data["method"] = "translatePages";
	     $data["interfaceName"] = "core.pagemanager.IPageManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Add application
	* @param id
	* @return void
	* @throws ErrorException
	*/

	public function updateCellLayout($layout, $pageId, $cellId) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["layout"] = json_encode($this->transport->object_unset_nulls($layout));
	     $data['args']["pageId"] = json_encode($this->transport->object_unset_nulls($pageId));
	     $data['args']["cellId"] = json_encode($this->transport->object_unset_nulls($cellId));
	     $data["method"] = "updateCellLayout";
	     $data["interfaceName"] = "core.pagemanager.IPageManager";
	     return $this->transport->sendMessage($data);
	}

}
class APIPkkControlManager {

	var $transport;
	
	function APIPkkControlManager($transport) {
		$this->transport = $transport;
	}

	/**
	*
	* @author ktonder
	*/

	public function getPkkControlData($licensePlate) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["licensePlate"] = json_encode($this->transport->object_unset_nulls($licensePlate));
	     $data["method"] = "getPkkControlData";
	     $data["interfaceName"] = "core.pkk.IPkkControlManager";
	     return $this->transport->cast(new core_pkkcontrol_PkkControlData(), $this->transport->sendMessage($data));
	}

	/**
	*
	* @author ktonder
	*/

	public function getPkkControls() {
	     $data = array();
	     $data['args'] = array();
	     $data["method"] = "getPkkControls";
	     $data["interfaceName"] = "core.pkk.IPkkControlManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	*
	* @author ktonder
	*/

	public function registerPkkControl($core_pkkcontrol_PkkControlData) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["core_pkkcontrol_PkkControlData"] = json_encode($this->transport->object_unset_nulls($core_pkkcontrol_PkkControlData));
	     $data["method"] = "registerPkkControl";
	     $data["interfaceName"] = "core.pkk.IPkkControlManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	*
	* @author ktonder
	*/

	public function removePkkControl($id) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["id"] = json_encode($this->transport->object_unset_nulls($id));
	     $data["method"] = "removePkkControl";
	     $data["interfaceName"] = "core.pkk.IPkkControlManager";
	     return $this->transport->sendMessage($data);
	}

}
class APIProductManager {

	var $transport;
	
	function APIProductManager($transport) {
		$this->transport = $transport;
	}

	/**
	* You can use this function to change the stock quantity for a given product
	*
	* @param productId The id for the product to change.
	* @param count Number of entries to substract from the product stock quantity, an be negative number to decrease the stock quantity.
	* @return void
	* @throws ErrorException
	*/

	public function changeStockQuantity($productId, $count) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["productId"] = json_encode($this->transport->object_unset_nulls($productId));
	     $data['args']["count"] = json_encode($this->transport->object_unset_nulls($count));
	     $data["method"] = "changeStockQuantity";
	     $data["interfaceName"] = "core.productmanager.IProductManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Create a new product instance.
	* @return core_productmanager_data_Product
	* @throws ErrorException
	*/

	public function createProduct() {
	     $data = array();
	     $data['args'] = array();
	     $data["method"] = "createProduct";
	     $data["interfaceName"] = "core.productmanager.IProductManager";
	     return $this->transport->cast(new core_productmanager_data_Product(), $this->transport->sendMessage($data));
	}

	/**
	* Create a new product list.
	*
	* @param listName
	*/

	public function createProductList($listName) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["listName"] = json_encode($this->transport->object_unset_nulls($listName));
	     $data["method"] = "createProductList";
	     $data["interfaceName"] = "core.productmanager.IProductManager";
	     return $this->transport->cast(new core_productmanager_data_ProductList(), $this->transport->sendMessage($data));
	}

	/**
	* Create a new product list.
	*
	* @param listName
	*/

	public function deleteProductList($listId) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["listId"] = json_encode($this->transport->object_unset_nulls($listId));
	     $data["method"] = "deleteProductList";
	     $data["interfaceName"] = "core.productmanager.IProductManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Fetch all attributes connected to all products.
	* @return List
	* @throws ErrorException
	*/

	public function getAllAttributes() {
	     $data = array();
	     $data['args'] = array();
	     $data["method"] = "getAllAttributes";
	     $data["interfaceName"] = "core.productmanager.IProductManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Fetch all products the store has available.
	* @return List
	* @throws ErrorException
	*/

	public function getAllProducts() {
	     $data = array();
	     $data['args'] = array();
	     $data["method"] = "getAllProducts";
	     $data["interfaceName"] = "core.productmanager.IProductManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Returns a list of all products with only name, price and id.
	* @return List
	* @throws ErrorException
	*/

	public function getAllProductsLight() {
	     $data = array();
	     $data['args'] = array();
	     $data["method"] = "getAllProductsLight";
	     $data["interfaceName"] = "core.productmanager.IProductManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Whenever you use the getproducts function call, you will be able to fetch a summary of the attributes.
	* Comes in handy to filter result when fetching data from for example a product list.
	* @return core_productmanager_data_AttributeSummary
	* @throws ErrorException
	*/

	public function getAttributeSummary() {
	     $data = array();
	     $data['args'] = array();
	     $data["method"] = "getAttributeSummary";
	     $data["interfaceName"] = "core.productmanager.IProductManager";
	     return $this->transport->cast(new core_productmanager_data_AttributeSummary(), $this->transport->sendMessage($data));
	}

	/**
	* Fetch a list of all the latest products.
	* @param count Number of products to fetch.
	* @return List
	* @throws ErrorException
	*/

	public function getLatestProducts($count) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["count"] = json_encode($this->transport->object_unset_nulls($count));
	     $data["method"] = "getLatestProducts";
	     $data["interfaceName"] = "core.productmanager.IProductManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Get page by name
	*/

	public function getPageIdByName($productName) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["productName"] = json_encode($this->transport->object_unset_nulls($productName));
	     $data["method"] = "getPageIdByName";
	     $data["interfaceName"] = "core.productmanager.IProductManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Get price for a product with variations
	*/

	public function getPrice($productId, $variations) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["productId"] = json_encode($this->transport->object_unset_nulls($productId));
	     $data['args']["variations"] = json_encode($this->transport->object_unset_nulls($variations));
	     $data["method"] = "getPrice";
	     $data["interfaceName"] = "core.productmanager.IProductManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Fetch one single product by id
	* If product does not exists, null is returned.
	*
	* @param id
	* @return core_productmanager_data_Product
	* @throws ErrorException
	*/

	public function getProduct($id) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["id"] = json_encode($this->transport->object_unset_nulls($id));
	     $data["method"] = "getProduct";
	     $data["interfaceName"] = "core.productmanager.IProductManager";
	     return $this->transport->cast(new core_productmanager_data_Product(), $this->transport->sendMessage($data));
	}

	/**
	* Returns a product connected to a specific page.
	*/

	public function getProductByPage($id) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["id"] = json_encode($this->transport->object_unset_nulls($id));
	     $data["method"] = "getProductByPage";
	     $data["interfaceName"] = "core.productmanager.IProductManager";
	     return $this->transport->cast(new core_productmanager_data_Product(), $this->transport->sendMessage($data));
	}

	/**
	* Find the product uuid set for an application.
	* @param uuid
	* @return core_productmanager_data_Product
	* @throws ErrorException
	*/

	public function getProductFromApplicationId($app_uuid) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["app_uuid"] = json_encode($this->transport->object_unset_nulls($app_uuid));
	     $data["method"] = "getProductFromApplicationId";
	     $data["interfaceName"] = "core.productmanager.IProductManager";
	     return $this->transport->cast(new core_productmanager_data_Product(), $this->transport->sendMessage($data));
	}

	/**
	* Create a new product list.
	*
	* @param listName
	*/

	public function getProductList($listId) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["listId"] = json_encode($this->transport->object_unset_nulls($listId));
	     $data["method"] = "getProductList";
	     $data["interfaceName"] = "core.productmanager.IProductManager";
	     return $this->transport->cast(new core_productmanager_data_ProductList(), $this->transport->sendMessage($data));
	}

	/**
	* Create a new product list.
	*
	* @param listName
	*/

	public function getProductLists() {
	     $data = array();
	     $data['args'] = array();
	     $data["method"] = "getProductLists";
	     $data["interfaceName"] = "core.productmanager.IProductManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Fetch products
	*
	* @param productCriteria
	* @return List
	* @throws ErrorException
	*/

	public function getProducts($core_productmanager_data_ProductCriteria) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["core_productmanager_data_ProductCriteria"] = json_encode($this->transport->object_unset_nulls($core_productmanager_data_ProductCriteria));
	     $data["method"] = "getProducts";
	     $data["interfaceName"] = "core.productmanager.IProductManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Returns a random set of products
	* If the store does not have enough products it will return as close as possible to
	* the fetchsize specified.
	*
	* @param fetchSize Amount of products that you wish to fetch.
	* @param ignoreProductId Will skip this id, can be the productId or the productPageId.
	* @return ArrayList
	*/

	public function getRandomProducts($fetchSize, $ignoreProductId) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["fetchSize"] = json_encode($this->transport->object_unset_nulls($fetchSize));
	     $data['args']["ignoreProductId"] = json_encode($this->transport->object_unset_nulls($ignoreProductId));
	     $data["method"] = "getRandomProducts";
	     $data["interfaceName"] = "core.productmanager.IProductManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Get a list of all the taxes set for this store.
	* @return List
	* @throws ErrorException
	*/

	public function getTaxes() {
	     $data = array();
	     $data['args'] = array();
	     $data["method"] = "getTaxes";
	     $data["interfaceName"] = "core.productmanager.IProductManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Remove an existing product.
	*
	* @param productId The id of the product to remove.
	* @return void
	* @throws ErrorException
	*/

	public function removeProduct($productId) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["productId"] = json_encode($this->transport->object_unset_nulls($productId));
	     $data["method"] = "removeProduct";
	     $data["interfaceName"] = "core.productmanager.IProductManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Save a product.
	*
	* @param product The product to save, if the id for the product is not set.
	* @param parentPageId Attach this product to a given page, leave this empty to avoid attaching it.
	* @return core_productmanager_data_Product
	* @throws ErrorException
	*/

	public function saveProduct($core_productmanager_data_Product) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["core_productmanager_data_Product"] = json_encode($this->transport->object_unset_nulls($core_productmanager_data_Product));
	     $data["method"] = "saveProduct";
	     $data["interfaceName"] = "core.productmanager.IProductManager";
	     return $this->transport->cast(new core_productmanager_data_Product(), $this->transport->sendMessage($data));
	}

	/**
	* Create a new product list.
	*
	* @param listName
	*/

	public function saveProductList($core_productmanager_data_ProductList) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["core_productmanager_data_ProductList"] = json_encode($this->transport->object_unset_nulls($core_productmanager_data_ProductList));
	     $data["method"] = "saveProductList";
	     $data["interfaceName"] = "core.productmanager.IProductManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Returns a list of products for a given searchword,
	* if blank all products will be returned.
	*
	* @param searchWord
	* @param pageSize
	* @param page
	* @return core_productmanager_data_SearchResult
	*/

	public function search($searchWord, $pageSize, $page) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["searchWord"] = json_encode($this->transport->object_unset_nulls($searchWord));
	     $data['args']["pageSize"] = json_encode($this->transport->object_unset_nulls($pageSize));
	     $data['args']["page"] = json_encode($this->transport->object_unset_nulls($page));
	     $data["method"] = "search";
	     $data["interfaceName"] = "core.productmanager.IProductManager";
	     return $this->transport->cast(new core_productmanager_data_SearchResult(), $this->transport->sendMessage($data));
	}

	/**
	* Method for setting a known product image as main image.
	*
	* @param productId
	* @param imageId
	* @throws ErrorException
	*/

	public function setMainImage($productId, $imageId) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["productId"] = json_encode($this->transport->object_unset_nulls($productId));
	     $data['args']["imageId"] = json_encode($this->transport->object_unset_nulls($imageId));
	     $data["method"] = "setMainImage";
	     $data["interfaceName"] = "core.productmanager.IProductManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Returns a list of products for a given searchword,
	* if blank all products will be returned.
	*
	* @param searchWord
	* @param pageSize
	* @param page
	* @return void
	*/

	public function setProductDynamicPrice($productId, $count) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["productId"] = json_encode($this->transport->object_unset_nulls($productId));
	     $data['args']["count"] = json_encode($this->transport->object_unset_nulls($count));
	     $data["method"] = "setProductDynamicPrice";
	     $data["interfaceName"] = "core.productmanager.IProductManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Set the tax groups for the the products, (0-5).
	* @param group
	* @throws ErrorException
	*/

	public function setTaxes($group) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["group"] = json_encode($this->transport->object_unset_nulls($group));
	     $data["method"] = "setTaxes";
	     $data["interfaceName"] = "core.productmanager.IProductManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Translate all antries found in a given list of entry ids.
	* @param entryIds A list of entries id to translate.
	* @return HashMap
	* @throws ErrorException
	*/

	public function translateEntries($entryIds) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["entryIds"] = json_encode($this->transport->object_unset_nulls($entryIds));
	     $data["method"] = "translateEntries";
	     $data["interfaceName"] = "core.productmanager.IProductManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Update the attribute pool. This will replace the old one, so all entries has to be included here.
	* @param groups
	* @throws ErrorException
	*/

	public function updateAttributePool($groups) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["groups"] = json_encode($this->transport->object_unset_nulls($groups));
	     $data["method"] = "updateAttributePool";
	     $data["interfaceName"] = "core.productmanager.IProductManager";
	     return $this->transport->sendMessage($data);
	}

}
class APIReportingManager {

	var $transport;
	
	function APIReportingManager($transport) {
		$this->transport = $transport;
	}

	/**
	* Fetch all activity data for a given session at a given period in time.
	* @param startDate "yyyy-mm-dd"
	* @param stopDate "yyyy-mm-dd"
	* @param searchSessionId The id of the session to fetch data from.
	* @return List
	* @throws ErrorException
	*/

	public function getAllEventsFromSession($startDate, $stopDate, $searchSessionId) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["startDate"] = json_encode($this->transport->object_unset_nulls($startDate));
	     $data['args']["stopDate"] = json_encode($this->transport->object_unset_nulls($stopDate));
	     $data['args']["searchSessionId"] = json_encode($this->transport->object_unset_nulls($searchSessionId));
	     $data["method"] = "getAllEventsFromSession";
	     $data["interfaceName"] = "core.reportingmanager.IReportingManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Fetch all users which connected at a given time period.
	* These are users who has been logging on to your store.
	* @param startdate "yyyy-mm-dd"
	* @param stopDate "yyyy-mm-dd"
	* @param filter A report filter if you want to filter out more information, use null to avoid the filter.
	* @return List
	* @throws ErrorException
	*/

	public function getConnectedUsers($startdate, $stopDate, $core_reportingmanager_data_ReportFilter) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["startdate"] = json_encode($this->transport->object_unset_nulls($startdate));
	     $data['args']["stopDate"] = json_encode($this->transport->object_unset_nulls($stopDate));
	     $data['args']["core_reportingmanager_data_ReportFilter"] = json_encode($this->transport->object_unset_nulls($core_reportingmanager_data_ReportFilter));
	     $data["method"] = "getConnectedUsers";
	     $data["interfaceName"] = "core.reportingmanager.IReportingManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* List all orders created at a given time period.
	* @param startDate "yyyy-mm-dd"
	* @param stopDate "yyyy-mm-dd"
	* @return List
	* @throws ErrorException
	*/

	public function getOrdersCreated($startDate, $stopDate) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["startDate"] = json_encode($this->transport->object_unset_nulls($startDate));
	     $data['args']["stopDate"] = json_encode($this->transport->object_unset_nulls($stopDate));
	     $data["method"] = "getOrdersCreated";
	     $data["interfaceName"] = "core.reportingmanager.IReportingManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Fetch the page id for all page
	* @param startDate "yyyy-mm-dd"
	* @param stopDate "yyyy-mm-dd"
	* @return List
	* @throws ErrorException
	*/

	public function getPageViews($startDate, $stopDate) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["startDate"] = json_encode($this->transport->object_unset_nulls($startDate));
	     $data['args']["stopDate"] = json_encode($this->transport->object_unset_nulls($stopDate));
	     $data["method"] = "getPageViews";
	     $data["interfaceName"] = "core.reportingmanager.IReportingManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Fetch all viewed product for a given time period.
	* @param startDate "yyyy-mm-dd"
	* @param stopDate "yyyy-mm-dd"
	* @return List
	* @throws ErrorException
	*/

	public function getProductViewed($startDate, $stopDate) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["startDate"] = json_encode($this->transport->object_unset_nulls($startDate));
	     $data['args']["stopDate"] = json_encode($this->transport->object_unset_nulls($stopDate));
	     $data["method"] = "getProductViewed";
	     $data["interfaceName"] = "core.reportingmanager.IReportingManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Fetch a report for a given time period.
	* @param startDate "yyyy-mm-dd"
	* @param stopDate "yyyy-mm-dd"
	* @param type 0, hourly, 1. daily, 2. weekly, 3. monthly
	* @return List
	* @throws ErrorException
	*/

	public function getReport($startDate, $stopDate, $type) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["startDate"] = json_encode($this->transport->object_unset_nulls($startDate));
	     $data['args']["stopDate"] = json_encode($this->transport->object_unset_nulls($stopDate));
	     $data['args']["type"] = json_encode($this->transport->object_unset_nulls($type));
	     $data["method"] = "getReport";
	     $data["interfaceName"] = "core.reportingmanager.IReportingManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Fetch a list of all users trying / logging on at a given time interval.
	* @param startDate "yyyy-mm-dd"
	* @param stopDate "yyyy-mm-dd"
	* @return List
	* @throws ErrorException
	*/

	public function getUserLoggedOn($startDate, $stopDate) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["startDate"] = json_encode($this->transport->object_unset_nulls($startDate));
	     $data['args']["stopDate"] = json_encode($this->transport->object_unset_nulls($stopDate));
	     $data["method"] = "getUserLoggedOn";
	     $data["interfaceName"] = "core.reportingmanager.IReportingManager";
	     return $this->transport->sendMessage($data);
	}

}
class APISedoxProductManager {

	var $transport;
	
	function APISedoxProductManager($transport) {
		$this->transport = $transport;
	}

	/**
	* Developers is simply an getshop user that is registered as an developer.
	* Active developers are administrators that has an SedoxUser with the flag
	* isActiveDeveloper = true
	*
	* @return void
	* @throws ErrorException
	*/

	public function addCreditToSlave($slaveId, $amount) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["slaveId"] = json_encode($this->transport->object_unset_nulls($slaveId));
	     $data['args']["amount"] = json_encode($this->transport->object_unset_nulls($amount));
	     $data["method"] = "addCreditToSlave";
	     $data["interfaceName"] = "core.sedox.ISedoxProductManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Return the products created by days back.
	* day = 0 // Means that it will returns the list of todays files
	* day = 1 // Means that it will returns the list of yesterdays files
	*
	* @param day
	* @return void
	* @throws ErrorException
	*/

	public function addFileToProduct($base64EncodedFile, $fileName, $fileType, $productId) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["base64EncodedFile"] = json_encode($this->transport->object_unset_nulls($base64EncodedFile));
	     $data['args']["fileName"] = json_encode($this->transport->object_unset_nulls($fileName));
	     $data['args']["fileType"] = json_encode($this->transport->object_unset_nulls($fileType));
	     $data['args']["productId"] = json_encode($this->transport->object_unset_nulls($productId));
	     $data["method"] = "addFileToProduct";
	     $data["interfaceName"] = "core.sedox.ISedoxProductManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Developers is simply an getshop user that is registered as an developer.
	* Active developers are administrators that has an SedoxUser with the flag
	* isActiveDeveloper = true
	*
	* @return void
	* @throws ErrorException
	*/

	public function addSlaveToUser($masterUserId, $slaveUserId) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["masterUserId"] = json_encode($this->transport->object_unset_nulls($masterUserId));
	     $data['args']["slaveUserId"] = json_encode($this->transport->object_unset_nulls($slaveUserId));
	     $data["method"] = "addSlaveToUser";
	     $data["interfaceName"] = "core.sedox.ISedoxProductManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Return the products created by days back.
	* day = 0 // Means that it will returns the list of todays files
	* day = 1 // Means that it will returns the list of yesterdays files
	*
	* @param day
	* @return void
	* @throws ErrorException
	*/

	public function addUserCredit($id, $description, $amount) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["id"] = json_encode($this->transport->object_unset_nulls($id));
	     $data['args']["description"] = json_encode($this->transport->object_unset_nulls($description));
	     $data['args']["amount"] = json_encode($this->transport->object_unset_nulls($amount));
	     $data["method"] = "addUserCredit";
	     $data["interfaceName"] = "core.sedox.ISedoxProductManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* This will disable/enable the developer. Useful if a developer goes on vacation
	* or needs an hour sleep.
	*/

	public function changeDeveloperStatus($userId, $disabled) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["userId"] = json_encode($this->transport->object_unset_nulls($userId));
	     $data['args']["disabled"] = json_encode($this->transport->object_unset_nulls($disabled));
	     $data["method"] = "changeDeveloperStatus";
	     $data["interfaceName"] = "core.sedox.ISedoxProductManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Return the products created by days back.
	* day = 0 // Means that it will returns the list of todays files
	* day = 1 // Means that it will returns the list of yesterdays files
	*
	* @param day
	* @return void
	* @throws ErrorException
	*/

	public function createSedoxProduct($core_sedox_SedoxProduct, $base64encodedOriginalFile, $originalFileName, $forSlaveId, $origin) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["core_sedox_SedoxProduct"] = json_encode($this->transport->object_unset_nulls($core_sedox_SedoxProduct));
	     $data['args']["base64encodedOriginalFile"] = json_encode($this->transport->object_unset_nulls($base64encodedOriginalFile));
	     $data['args']["originalFileName"] = json_encode($this->transport->object_unset_nulls($originalFileName));
	     $data['args']["forSlaveId"] = json_encode($this->transport->object_unset_nulls($forSlaveId));
	     $data['args']["origin"] = json_encode($this->transport->object_unset_nulls($origin));
	     $data["method"] = "createSedoxProduct";
	     $data["interfaceName"] = "core.sedox.ISedoxProductManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	*
	* @author ktonder
	*/

	public function getAllUsersWithNegativeCreditLimit() {
	     $data = array();
	     $data['args'] = array();
	     $data["method"] = "getAllUsersWithNegativeCreditLimit";
	     $data["interfaceName"] = "core.sedox.ISedoxProductManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Developers is simply an getshop user that is registered as an developer.
	* Active developers are administrators that has an SedoxUser with the flag
	* isActiveDeveloper = true
	*
	* @return List
	* @throws ErrorException
	*/

	public function getDevelopers() {
	     $data = array();
	     $data['args'] = array();
	     $data["method"] = "getDevelopers";
	     $data["interfaceName"] = "core.sedox.ISedoxProductManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Developers is simply an getshop user that is registered as an developer.
	* Active developers are administrators that has an SedoxUser with the flag
	* isActiveDeveloper = true
	*
	* @return String
	* @throws ErrorException
	*/

	public function getExtraInformationForFile($productId, $fileId) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["productId"] = json_encode($this->transport->object_unset_nulls($productId));
	     $data['args']["fileId"] = json_encode($this->transport->object_unset_nulls($fileId));
	     $data["method"] = "getExtraInformationForFile";
	     $data["interfaceName"] = "core.sedox.ISedoxProductManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Return the products created by days back.
	* day = 0 // Means that it will returns the list of todays files
	* day = 1 // Means that it will returns the list of yesterdays files
	*
	* @param day
	* @return core_sedox_SedoxProduct
	* @throws ErrorException
	*/

	public function getProductById($id) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["id"] = json_encode($this->transport->object_unset_nulls($id));
	     $data["method"] = "getProductById";
	     $data["interfaceName"] = "core.sedox.ISedoxProductManager";
	     return $this->transport->cast(new core_sedox_SedoxProduct(), $this->transport->sendMessage($data));
	}

	/**
	* Return the products created by days back.
	* day = 0 // Means that it will returns the list of todays files
	* day = 1 // Means that it will returns the list of yesterdays files
	*
	* @param day
	* @return List
	* @throws ErrorException
	*/

	public function getProductsByDaysBack($day) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["day"] = json_encode($this->transport->object_unset_nulls($day));
	     $data["method"] = "getProductsByDaysBack";
	     $data["interfaceName"] = "core.sedox.ISedoxProductManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	*
	* @author ktonder
	*/

	public function getProductsFirstUploadedByCurrentUser() {
	     $data = array();
	     $data['args'] = array();
	     $data["method"] = "getProductsFirstUploadedByCurrentUser";
	     $data["interfaceName"] = "core.sedox.ISedoxProductManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Return the products created by days back.
	* day = 0 // Means that it will returns the list of todays files
	* day = 1 // Means that it will returns the list of yesterdays files
	*
	* @param day
	* @return core_sedox_SedoxProduct
	* @throws ErrorException
	*/

	public function getSedoxProductByMd5Sum($md5sum) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["md5sum"] = json_encode($this->transport->object_unset_nulls($md5sum));
	     $data["method"] = "getSedoxProductByMd5Sum";
	     $data["interfaceName"] = "core.sedox.ISedoxProductManager";
	     return $this->transport->cast(new core_sedox_SedoxProduct(), $this->transport->sendMessage($data));
	}

	/**
	*
	* @author ktonder
	*/

	public function getSedoxUserAccount() {
	     $data = array();
	     $data['args'] = array();
	     $data["method"] = "getSedoxUserAccount";
	     $data["interfaceName"] = "core.sedox.ISedoxProductManager";
	     return $this->transport->cast(new core_sedox_SedoxUser(), $this->transport->sendMessage($data));
	}

	/**
	*
	* @author ktonder
	*/

	public function getSedoxUserAccountById($userid) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["userid"] = json_encode($this->transport->object_unset_nulls($userid));
	     $data["method"] = "getSedoxUserAccountById";
	     $data["interfaceName"] = "core.sedox.ISedoxProductManager";
	     return $this->transport->cast(new core_sedox_SedoxUser(), $this->transport->sendMessage($data));
	}

	/**
	* Developers is simply an getshop user that is registered as an developer.
	* Active developers are administrators that has an SedoxUser with the flag
	* isActiveDeveloper = true
	*
	* @return List
	* @throws ErrorException
	*/

	public function getSlaves($masterUserId) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["masterUserId"] = json_encode($this->transport->object_unset_nulls($masterUserId));
	     $data["method"] = "getSlaves";
	     $data["interfaceName"] = "core.sedox.ISedoxProductManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Return the products created by days back.
	* day = 0 // Means that it will returns the list of todays files
	* day = 1 // Means that it will returns the list of yesterdays files
	*
	* @param day
	* @return core_usermanager_data_User
	* @throws ErrorException
	*/

	public function login($emailAddress, $password) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["emailAddress"] = json_encode($this->transport->object_unset_nulls($emailAddress));
	     $data['args']["password"] = json_encode($this->transport->object_unset_nulls($password));
	     $data["method"] = "login";
	     $data["interfaceName"] = "core.sedox.ISedoxProductManager";
	     return $this->transport->cast(new core_usermanager_data_User(), $this->transport->sendMessage($data));
	}

	/**
	* Return the products created by days back.
	* day = 0 // Means that it will returns the list of todays files
	* day = 1 // Means that it will returns the list of yesterdays files
	*
	* @param day
	* @return void
	* @throws ErrorException
	*/

	public function notifyForCustomer($productId, $extraText) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["productId"] = json_encode($this->transport->object_unset_nulls($productId));
	     $data['args']["extraText"] = json_encode($this->transport->object_unset_nulls($extraText));
	     $data["method"] = "notifyForCustomer";
	     $data["interfaceName"] = "core.sedox.ISedoxProductManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Return the products created by days back.
	* day = 0 // Means that it will returns the list of todays files
	* day = 1 // Means that it will returns the list of yesterdays files
	*
	* @param day
	* @return core_sedox_SedoxOrder
	* @throws ErrorException
	*/

	public function purchaseOnlyForCustomer($productId, $files) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["productId"] = json_encode($this->transport->object_unset_nulls($productId));
	     $data['args']["files"] = json_encode($this->transport->object_unset_nulls($files));
	     $data["method"] = "purchaseOnlyForCustomer";
	     $data["interfaceName"] = "core.sedox.ISedoxProductManager";
	     return $this->transport->cast(new core_sedox_SedoxOrder(), $this->transport->sendMessage($data));
	}

	/**
	* Return the products created by days back.
	* day = 0 // Means that it will returns the list of todays files
	* day = 1 // Means that it will returns the list of yesterdays files
	*
	* @param day
	* @return String
	* @throws ErrorException
	*/

	public function purchaseProduct($productId, $files) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["productId"] = json_encode($this->transport->object_unset_nulls($productId));
	     $data['args']["files"] = json_encode($this->transport->object_unset_nulls($files));
	     $data["method"] = "purchaseProduct";
	     $data["interfaceName"] = "core.sedox.ISedoxProductManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Developers is simply an getshop user that is registered as an developer.
	* Active developers are administrators that has an SedoxUser with the flag
	* isActiveDeveloper = true
	*
	* @return void
	* @throws ErrorException
	*/

	public function removeBinaryFileFromProduct($productId, $fileId) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["productId"] = json_encode($this->transport->object_unset_nulls($productId));
	     $data['args']["fileId"] = json_encode($this->transport->object_unset_nulls($fileId));
	     $data["method"] = "removeBinaryFileFromProduct";
	     $data["interfaceName"] = "core.sedox.ISedoxProductManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Return the products created by days back.
	* day = 0 // Means that it will returns the list of todays files
	* day = 1 // Means that it will returns the list of yesterdays files
	*
	* @param day
	* @return void
	* @throws ErrorException
	*/

	public function requestSpecialFile($productId, $comment) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["productId"] = json_encode($this->transport->object_unset_nulls($productId));
	     $data['args']["comment"] = json_encode($this->transport->object_unset_nulls($comment));
	     $data["method"] = "requestSpecialFile";
	     $data["interfaceName"] = "core.sedox.ISedoxProductManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	*
	* @author ktonder
	*/

	public function search($core_sedox_SedoxSearch) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["core_sedox_SedoxSearch"] = json_encode($this->transport->object_unset_nulls($core_sedox_SedoxSearch));
	     $data["method"] = "search";
	     $data["interfaceName"] = "core.sedox.ISedoxProductManager";
	     return $this->transport->cast(new core_sedox_SedoxProductSearchPage(), $this->transport->sendMessage($data));
	}

	/**
	* Return the products created by days back.
	* day = 0 // Means that it will returns the list of todays files
	* day = 1 // Means that it will returns the list of yesterdays files
	*
	* @param day
	* @return List
	* @throws ErrorException
	*/

	public function searchForUsers($searchString) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["searchString"] = json_encode($this->transport->object_unset_nulls($searchString));
	     $data["method"] = "searchForUsers";
	     $data["interfaceName"] = "core.sedox.ISedoxProductManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Return the products created by days back.
	* day = 0 // Means that it will returns the list of todays files
	* day = 1 // Means that it will returns the list of yesterdays files
	*
	* @param day
	* @return void
	* @throws ErrorException
	*/

	public function sendProductByMail($productId, $extraText, $files) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["productId"] = json_encode($this->transport->object_unset_nulls($productId));
	     $data['args']["extraText"] = json_encode($this->transport->object_unset_nulls($extraText));
	     $data['args']["files"] = json_encode($this->transport->object_unset_nulls($files));
	     $data["method"] = "sendProductByMail";
	     $data["interfaceName"] = "core.sedox.ISedoxProductManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Return the products created by days back.
	* day = 0 // Means that it will returns the list of todays files
	* day = 1 // Means that it will returns the list of yesterdays files
	*
	* @param day
	* @return void
	* @throws ErrorException
	*/

	public function setChecksum($productId, $checksum) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["productId"] = json_encode($this->transport->object_unset_nulls($productId));
	     $data['args']["checksum"] = json_encode($this->transport->object_unset_nulls($checksum));
	     $data["method"] = "setChecksum";
	     $data["interfaceName"] = "core.sedox.ISedoxProductManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Developers is simply an getshop user that is registered as an developer.
	* Active developers are administrators that has an SedoxUser with the flag
	* isActiveDeveloper = true
	*
	* @return void
	* @throws ErrorException
	*/

	public function setExtraInformationForFile($productId, $fileId, $text) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["productId"] = json_encode($this->transport->object_unset_nulls($productId));
	     $data['args']["fileId"] = json_encode($this->transport->object_unset_nulls($fileId));
	     $data['args']["text"] = json_encode($this->transport->object_unset_nulls($text));
	     $data["method"] = "setExtraInformationForFile";
	     $data["interfaceName"] = "core.sedox.ISedoxProductManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	*
	* @author ktonder
	*/

	public function sync($option) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["option"] = json_encode($this->transport->object_unset_nulls($option));
	     $data["method"] = "sync";
	     $data["interfaceName"] = "core.sedox.ISedoxProductManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Developers is simply an getshop user that is registered as an developer.
	* Active developers are administrators that has an SedoxUser with the flag
	* isActiveDeveloper = true
	*
	* @return void
	* @throws ErrorException
	*/

	public function toggleAllowNegativeCredit($userId, $allow) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["userId"] = json_encode($this->transport->object_unset_nulls($userId));
	     $data['args']["allow"] = json_encode($this->transport->object_unset_nulls($allow));
	     $data["method"] = "toggleAllowNegativeCredit";
	     $data["interfaceName"] = "core.sedox.ISedoxProductManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Developers is simply an getshop user that is registered as an developer.
	* Active developers are administrators that has an SedoxUser with the flag
	* isActiveDeveloper = true
	*
	* @return void
	* @throws ErrorException
	*/

	public function toggleAllowWindowsApp($userId, $allow) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["userId"] = json_encode($this->transport->object_unset_nulls($userId));
	     $data['args']["allow"] = json_encode($this->transport->object_unset_nulls($allow));
	     $data["method"] = "toggleAllowWindowsApp";
	     $data["interfaceName"] = "core.sedox.ISedoxProductManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Developers is simply an getshop user that is registered as an developer.
	* Active developers are administrators that has an SedoxUser with the flag
	* isActiveDeveloper = true
	*
	* @return void
	* @throws ErrorException
	*/

	public function toggleIsNorwegian($userId, $isNorwegian) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["userId"] = json_encode($this->transport->object_unset_nulls($userId));
	     $data['args']["isNorwegian"] = json_encode($this->transport->object_unset_nulls($isNorwegian));
	     $data["method"] = "toggleIsNorwegian";
	     $data["interfaceName"] = "core.sedox.ISedoxProductManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Developers is simply an getshop user that is registered as an developer.
	* Active developers are administrators that has an SedoxUser with the flag
	* isActiveDeveloper = true
	*
	* @return void
	* @throws ErrorException
	*/

	public function togglePassiveSlaveMode($userId, $toggle) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["userId"] = json_encode($this->transport->object_unset_nulls($userId));
	     $data['args']["toggle"] = json_encode($this->transport->object_unset_nulls($toggle));
	     $data["method"] = "togglePassiveSlaveMode";
	     $data["interfaceName"] = "core.sedox.ISedoxProductManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Developers is simply an getshop user that is registered as an developer.
	* Active developers are administrators that has an SedoxUser with the flag
	* isActiveDeveloper = true
	*
	* @return void
	* @throws ErrorException
	*/

	public function toggleSaleableProduct($productId, $saleable) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["productId"] = json_encode($this->transport->object_unset_nulls($productId));
	     $data['args']["saleable"] = json_encode($this->transport->object_unset_nulls($saleable));
	     $data["method"] = "toggleSaleableProduct";
	     $data["interfaceName"] = "core.sedox.ISedoxProductManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Developers is simply an getshop user that is registered as an developer.
	* Active developers are administrators that has an SedoxUser with the flag
	* isActiveDeveloper = true
	*
	* @return void
	* @throws ErrorException
	*/

	public function toggleStartStop($productId, $toggle) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["productId"] = json_encode($this->transport->object_unset_nulls($productId));
	     $data['args']["toggle"] = json_encode($this->transport->object_unset_nulls($toggle));
	     $data["method"] = "toggleStartStop";
	     $data["interfaceName"] = "core.sedox.ISedoxProductManager";
	     return $this->transport->sendMessage($data);
	}

}
class APIStoreApplicationInstancePool {

	var $transport;
	
	function APIStoreApplicationInstancePool($transport) {
		$this->transport = $transport;
	}

	/**
	*
	* @author ktonder
	*/

	public function createNewInstance($applicationId) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["applicationId"] = json_encode($this->transport->object_unset_nulls($applicationId));
	     $data["method"] = "createNewInstance";
	     $data["interfaceName"] = "core.applications.IStoreApplicationInstancePool";
	     return $this->transport->cast(new core_common_ApplicationInstance(), $this->transport->sendMessage($data));
	}

	/**
	*
	* @author ktonder
	*/

	public function getApplicationInstance($applicationInstanceId) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["applicationInstanceId"] = json_encode($this->transport->object_unset_nulls($applicationInstanceId));
	     $data["method"] = "getApplicationInstance";
	     $data["interfaceName"] = "core.applications.IStoreApplicationInstancePool";
	     return $this->transport->cast(new core_common_ApplicationInstance(), $this->transport->sendMessage($data));
	}

	/**
	*
	* @author ktonder
	*/

	public function setApplicationSettings($core_common_Settings) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["core_common_Settings"] = json_encode($this->transport->object_unset_nulls($core_common_Settings));
	     $data["method"] = "setApplicationSettings";
	     $data["interfaceName"] = "core.applications.IStoreApplicationInstancePool";
	     return $this->transport->cast(new core_common_ApplicationInstance(), $this->transport->sendMessage($data));
	}

}
class APIStoreApplicationPool {

	var $transport;
	
	function APIStoreApplicationPool($transport) {
		$this->transport = $transport;
	}

	/**
	* Activate an application.
	*
	* @param applicationId
	*/

	public function activateApplication($applicationId) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["applicationId"] = json_encode($this->transport->object_unset_nulls($applicationId));
	     $data["method"] = "activateApplication";
	     $data["interfaceName"] = "core.applications.IStoreApplicationPool";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Actiave a module by a given module id.
	*
	* @param module
	*/

	public function activateModule($module) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["module"] = json_encode($this->transport->object_unset_nulls($module));
	     $data["method"] = "activateModule";
	     $data["interfaceName"] = "core.applications.IStoreApplicationPool";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Actiave a module by a given module id.
	*
	* @param module
	*/

	public function deactivateApplication($applicationId) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["applicationId"] = json_encode($this->transport->object_unset_nulls($applicationId));
	     $data["method"] = "deactivateApplication";
	     $data["interfaceName"] = "core.applications.IStoreApplicationPool";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Returns a list of all available applications.
	*
	* @return List
	*/

	public function getActivatedModules() {
	     $data = array();
	     $data['args'] = array();
	     $data["method"] = "getActivatedModules";
	     $data["interfaceName"] = "core.applications.IStoreApplicationPool";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Return a list of all applucation modules available
	*
	* @return List
	*/

	public function getAllAvailableModules() {
	     $data = array();
	     $data['args'] = array();
	     $data["method"] = "getAllAvailableModules";
	     $data["interfaceName"] = "core.applications.IStoreApplicationPool";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Return an activated application by the given Id.
	*
	* @param id
	* @return core_appmanager_data_Application
	*/

	public function getApplication($id) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["id"] = json_encode($this->transport->object_unset_nulls($id));
	     $data["method"] = "getApplication";
	     $data["interfaceName"] = "core.applications.IStoreApplicationPool";
	     return $this->transport->cast(new core_appmanager_data_Application(), $this->transport->sendMessage($data));
	}

	/**
	* Returns a list of all applications this store has activated.
	*
	* @return List
	*/

	public function getApplications() {
	     $data = array();
	     $data['args'] = array();
	     $data["method"] = "getApplications";
	     $data["interfaceName"] = "core.applications.IStoreApplicationPool";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Returns a list of all applications that are available for this store.
	* This also includes applications that has not yet been activated by the
	* administrator.
	*
	* @return List
	*/

	public function getAvailableApplications() {
	     $data = array();
	     $data['args'] = array();
	     $data["method"] = "getAvailableApplications";
	     $data["interfaceName"] = "core.applications.IStoreApplicationPool";
	     return $this->transport->sendMessage($data);
	}

	/**
	* This is a filtered list of the getAvailableApplications function.
	*
	* @return List
	*/

	public function getAvailableApplicationsThatIsNotActivated() {
	     $data = array();
	     $data['args'] = array();
	     $data["method"] = "getAvailableApplicationsThatIsNotActivated";
	     $data["interfaceName"] = "core.applications.IStoreApplicationPool";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Returns a list of all available theme applications.
	*
	* @return List
	*/

	public function getAvailableThemeApplications() {
	     $data = array();
	     $data['args'] = array();
	     $data["method"] = "getAvailableThemeApplications";
	     $data["interfaceName"] = "core.applications.IStoreApplicationPool";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Returns shipment applications.
	*
	* @return List
	*/

	public function getShippingApplications() {
	     $data = array();
	     $data['args'] = array();
	     $data["method"] = "getShippingApplications";
	     $data["interfaceName"] = "core.applications.IStoreApplicationPool";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Return the themeapplication that is currently set.
	*
	* @return core_appmanager_data_Application
	*/

	public function getThemeApplication() {
	     $data = array();
	     $data['args'] = array();
	     $data["method"] = "getThemeApplication";
	     $data["interfaceName"] = "core.applications.IStoreApplicationPool";
	     return $this->transport->cast(new core_appmanager_data_Application(), $this->transport->sendMessage($data));
	}

	/**
	* Actiave a module by a given module id.
	*
	* @param module
	*/

	public function setSetting($applicationId, $core_common_Setting) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["applicationId"] = json_encode($this->transport->object_unset_nulls($applicationId));
	     $data['args']["core_common_Setting"] = json_encode($this->transport->object_unset_nulls($core_common_Setting));
	     $data["method"] = "setSetting";
	     $data["interfaceName"] = "core.applications.IStoreApplicationPool";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Use this function to change or set the theme application you wish to use.
	*
	* @param applicationId
	*/

	public function setThemeApplication($applicationId) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["applicationId"] = json_encode($this->transport->object_unset_nulls($applicationId));
	     $data["method"] = "setThemeApplication";
	     $data["interfaceName"] = "core.applications.IStoreApplicationPool";
	     return $this->transport->sendMessage($data);
	}

}
class APIStoreManager {

	var $transport;
	
	function APIStoreManager($transport) {
		$this->transport = $transport;
	}

	/**
	* Create a new store / webshop with a given name.
	* @param hostname The hostname to the webshop.
	* @param email The email to identify the first user with,
	* @param password The password to logon the first user added to this webshop.
	* @param notify Notify the web shop owner by email about this new store.
	* @return core_storemanager_data_Store
	* @throws ErrorException
	*/

	public function createStore($hostname, $email, $password, $notify) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["hostname"] = json_encode($this->transport->object_unset_nulls($hostname));
	     $data['args']["email"] = json_encode($this->transport->object_unset_nulls($email));
	     $data['args']["password"] = json_encode($this->transport->object_unset_nulls($password));
	     $data['args']["notify"] = json_encode($this->transport->object_unset_nulls($notify));
	     $data["method"] = "createStore";
	     $data["interfaceName"] = "core.storemanager.IStoreManager";
	     return $this->transport->cast(new core_storemanager_data_Store(), $this->transport->sendMessage($data));
	}

	/**
	* Enable extended support for this webshop.
	* Extended mode is a more advanced version of the ui where there is no limitation to what can be created / made.
	* @param toggle True or false depending if this webshop should have access to the extended mode.
	* @param password A password given by getshop to toggle this option.
	* @return void
	* @throws ErrorException
	*/

	public function delete() {
	     $data = array();
	     $data['args'] = array();
	     $data["method"] = "delete";
	     $data["interfaceName"] = "core.storemanager.IStoreManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Enable extended support for this webshop.
	* Extended mode is a more advanced version of the ui where there is no limitation to what can be created / made.
	* @param toggle True or false depending if this webshop should have access to the extended mode.
	* @param password A password given by getshop to toggle this option.
	* @return core_storemanager_data_Store
	* @throws ErrorException
	*/

	public function enableExtendedMode($toggle, $password) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["toggle"] = json_encode($this->transport->object_unset_nulls($toggle));
	     $data['args']["password"] = json_encode($this->transport->object_unset_nulls($password));
	     $data["method"] = "enableExtendedMode";
	     $data["interfaceName"] = "core.storemanager.IStoreManager";
	     return $this->transport->cast(new core_storemanager_data_Store(), $this->transport->sendMessage($data));
	}

	/**
	* Enable support to send sms for this webshop.
	* This option is not free since there is a cost for each sms sent.
	* @param toggle true or false depending on if this webshop should have access to sms.
	* @param password A password given by getshop to toggle this option.
	* @return core_storemanager_data_Store
	* @throws ErrorException
	*/

	public function enableSMSAccess($toggle, $password) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["toggle"] = json_encode($this->transport->object_unset_nulls($toggle));
	     $data['args']["password"] = json_encode($this->transport->object_unset_nulls($password));
	     $data["method"] = "enableSMSAccess";
	     $data["interfaceName"] = "core.storemanager.IStoreManager";
	     return $this->transport->cast(new core_storemanager_data_Store(), $this->transport->sendMessage($data));
	}

	/**
	* On registration, generate a new id this store, which will become a part of the hostname.
	* @return int
	* @throws ErrorException
	*/

	public function generateStoreId() {
	     $data = array();
	     $data['args'] = array();
	     $data["method"] = "generateStoreId";
	     $data["interfaceName"] = "core.storemanager.IStoreManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Get the store added to this session.
	* @return core_storemanager_data_Store
	* @throws ErrorException
	*/

	public function getMyStore() {
	     $data = array();
	     $data['args'] = array();
	     $data["method"] = "getMyStore";
	     $data["interfaceName"] = "core.storemanager.IStoreManager";
	     return $this->transport->cast(new core_storemanager_data_Store(), $this->transport->sendMessage($data));
	}

	/**
	* Fetch the store id identified to this user.
	* @return String
	* @throws ErrorException
	*/

	public function getStoreId() {
	     $data = array();
	     $data['args'] = array();
	     $data["method"] = "getStoreId";
	     $data["interfaceName"] = "core.storemanager.IStoreManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Initializing this api. This will identify the webshop and will act as the root for everything in this api.
	* @param initSessionId The session id to identify to this user.
	* @return core_storemanager_data_Store
	* @throws ErrorException
	*/

	public function initializeStore($webAddress, $initSessionId) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["webAddress"] = json_encode($this->transport->object_unset_nulls($webAddress));
	     $data['args']["initSessionId"] = json_encode($this->transport->object_unset_nulls($initSessionId));
	     $data["method"] = "initializeStore";
	     $data["interfaceName"] = "core.storemanager.IStoreManager";
	     return $this->transport->cast(new core_storemanager_data_Store(), $this->transport->sendMessage($data));
	}

	/**
	* Check if a web shop address has already been taken.
	* @param address The address to check for.
	* @throws ErrorException
	*/

	public function isAddressTaken($address) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["address"] = json_encode($this->transport->object_unset_nulls($address));
	     $data["method"] = "isAddressTaken";
	     $data["interfaceName"] = "core.storemanager.IStoreManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Remove an already added domain name.
	* @param domainName The domain name to remove.
	* @return core_storemanager_data_Store
	* @throws ErrorException
	*/

	public function removeDomainName($domainName) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["domainName"] = json_encode($this->transport->object_unset_nulls($domainName));
	     $data["method"] = "removeDomainName";
	     $data["interfaceName"] = "core.storemanager.IStoreManager";
	     return $this->transport->cast(new core_storemanager_data_Store(), $this->transport->sendMessage($data));
	}

	/**
	* Update the current store with new configuration data.
	* @param config The configuration data to update.
	* @return core_storemanager_data_Store
	* @throws ErrorException
	*/

	public function saveStore($core_storemanager_data_StoreConfiguration) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["core_storemanager_data_StoreConfiguration"] = json_encode($this->transport->object_unset_nulls($core_storemanager_data_StoreConfiguration));
	     $data["method"] = "saveStore";
	     $data["interfaceName"] = "core.storemanager.IStoreManager";
	     return $this->transport->cast(new core_storemanager_data_Store(), $this->transport->sendMessage($data));
	}

	/**
	* This option will enable / disable the deepfreeze mode.
	* if a websolution is set to deepfreeze, it will automatically be
	* reverted to the original state each hour. No options will be stored.
	*
	* @param mode - true / false
	*/

	public function setDeepFreeze($mode, $password) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["mode"] = json_encode($this->transport->object_unset_nulls($mode));
	     $data['args']["password"] = json_encode($this->transport->object_unset_nulls($password));
	     $data["method"] = "setDeepFreeze";
	     $data["interfaceName"] = "core.storemanager.IStoreManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* This will set the readintroduction variable in the Store object to true.
	* @return core_storemanager_data_Store
	* @throws ErrorException
	*/

	public function setIntroductionRead() {
	     $data = array();
	     $data['args'] = array();
	     $data["method"] = "setIntroductionRead";
	     $data["interfaceName"] = "core.storemanager.IStoreManager";
	     return $this->transport->cast(new core_storemanager_data_Store(), $this->transport->sendMessage($data));
	}

	/**
	* Setting this store to be a template or not.
	*
	* @param storeId
	* @param isTemplate
	*/

	public function setIsTemplate($storeId, $isTemplate) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["storeId"] = json_encode($this->transport->object_unset_nulls($storeId));
	     $data['args']["isTemplate"] = json_encode($this->transport->object_unset_nulls($isTemplate));
	     $data["method"] = "setIsTemplate";
	     $data["interfaceName"] = "core.storemanager.IStoreManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Set a new domain name to this store / webshop
	* @param domainName The domain name to identify this shop with.
	* @return core_storemanager_data_Store
	* @throws ErrorException
	*/

	public function setPrimaryDomainName($domainName) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["domainName"] = json_encode($this->transport->object_unset_nulls($domainName));
	     $data["method"] = "setPrimaryDomainName";
	     $data["interfaceName"] = "core.storemanager.IStoreManager";
	     return $this->transport->cast(new core_storemanager_data_Store(), $this->transport->sendMessage($data));
	}

	/**
	* A user can set a different language for its session.
	* @param id
	* @throws ErrorException
	*/

	public function setSessionLanguage($id) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["id"] = json_encode($this->transport->object_unset_nulls($id));
	     $data["method"] = "setSessionLanguage";
	     $data["interfaceName"] = "core.storemanager.IStoreManager";
	     return $this->transport->sendMessage($data);
	}

}
class APIUserManager {

	var $transport;
	
	function APIUserManager($transport) {
		$this->transport = $transport;
	}

	/**
	* Add a comment to a specific user.
	*
	* @param userId
	* @param comment
	* @throws ErrorException
	*/

	public function addComment($userId, $core_usermanager_data_Comment) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["userId"] = json_encode($this->transport->object_unset_nulls($userId));
	     $data['args']["core_usermanager_data_Comment"] = json_encode($this->transport->object_unset_nulls($core_usermanager_data_Comment));
	     $data["method"] = "addComment";
	     $data["interfaceName"] = "core.usermanager.IUserManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Add priviliges to a another admin user.
	*
	* If a user is given a privilege, all the defaults are removed.
	* @param userId
	* @param managerName
	* @param managerFunction
	* @throws ErrorException
	*/

	public function addUserPrivilege($userId, $managerName, $managerFunction) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["userId"] = json_encode($this->transport->object_unset_nulls($userId));
	     $data['args']["managerName"] = json_encode($this->transport->object_unset_nulls($managerName));
	     $data['args']["managerFunction"] = json_encode($this->transport->object_unset_nulls($managerFunction));
	     $data["method"] = "addUserPrivilege";
	     $data["interfaceName"] = "core.usermanager.IUserManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Cancel the impersonation of a user.
	*
	* @throws ErrorException
	*/

	public function cancelImpersonating() {
	     $data = array();
	     $data['args'] = array();
	     $data["method"] = "cancelImpersonating";
	     $data["interfaceName"] = "core.usermanager.IUserManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Create a new user to your webshop.<br>
	* This will fail if you are trying to create a user which is granted more access then you have yourself.<br>
	* If no users has been created, then the user object will automatically be set as an administrator.<br>
	* That is how you create your first user, set the User.type field to 0.
	* @param user The new user to be created. and the password is sent as plain text.
	* @return core_usermanager_data_User
	* @throws ErrorException
	*/

	public function createUser($core_usermanager_data_User) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["core_usermanager_data_User"] = json_encode($this->transport->object_unset_nulls($core_usermanager_data_User));
	     $data["method"] = "createUser";
	     $data["interfaceName"] = "core.usermanager.IUserManager";
	     return $this->transport->cast(new core_usermanager_data_User(), $this->transport->sendMessage($data));
	}

	/**
	* Delete a registered user.
	* This will fail if you are trying to create a user which is granted more access then you have yourself.
	* @param userId The id for the user to delete.
	* @return void
	* @throws ErrorException
	*/

	public function deleteUser($userId) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["userId"] = json_encode($this->transport->object_unset_nulls($userId));
	     $data["method"] = "deleteUser";
	     $data["interfaceName"] = "core.usermanager.IUserManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Check if a user already exists with the given email.
	* @param email The email used when registering.
	* @return boolean
	* @throws ErrorException
	*/

	public function doEmailExists($email) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["email"] = json_encode($this->transport->object_unset_nulls($email));
	     $data["method"] = "doEmailExists";
	     $data["interfaceName"] = "core.usermanager.IUserManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Find all users with a given search criteria.
	* @param searchCriteria The criteria to search for
	* @return List
	* @throws ErrorException
	*/

	public function findUsers($searchCriteria) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["searchCriteria"] = json_encode($this->transport->object_unset_nulls($searchCriteria));
	     $data["method"] = "findUsers";
	     $data["interfaceName"] = "core.usermanager.IUserManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Gets the count of how many adminsitrators
	* is available for the page
	* @return int
	*/

	public function getAdministratorCount() {
	     $data = array();
	     $data['args'] = array();
	     $data["method"] = "getAdministratorCount";
	     $data["interfaceName"] = "core.usermanager.IUserManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Returns all the groups
	* that has been created for this
	* webpage.
	*
	* @return List
	*/

	public function getAllGroups() {
	     $data = array();
	     $data['args'] = array();
	     $data["method"] = "getAllGroups";
	     $data["interfaceName"] = "core.usermanager.IUserManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Fetch all the users registered to this webshop.
	* @return List
	* @throws ErrorException
	*/

	public function getAllUsers() {
	     $data = array();
	     $data['args'] = array();
	     $data["method"] = "getAllUsers";
	     $data["interfaceName"] = "core.usermanager.IUserManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Find all users that has one or more comments
	* connected to the specified appId.
	*
	* @param appId
	* @return List
	*/

	public function getAllUsersWithCommentToApp($appId) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["appId"] = json_encode($this->transport->object_unset_nulls($appId));
	     $data["method"] = "getAllUsersWithCommentToApp";
	     $data["interfaceName"] = "core.usermanager.IUserManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Gets the count of how many customers
	* is available for the page
	* @return int
	*/

	public function getCustomersCount() {
	     $data = array();
	     $data['args'] = array();
	     $data["method"] = "getCustomersCount";
	     $data["interfaceName"] = "core.usermanager.IUserManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Gets the count of how many editors
	* is available for the page
	* @return int
	*/

	public function getEditorCount() {
	     $data = array();
	     $data['args'] = array();
	     $data["method"] = "getEditorCount";
	     $data["interfaceName"] = "core.usermanager.IUserManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Fetch the currently logged on user.
	* @return core_usermanager_data_User
	*/

	public function getLoggedOnUser() {
	     $data = array();
	     $data['args'] = array();
	     $data["method"] = "getLoggedOnUser";
	     $data["interfaceName"] = "core.usermanager.IUserManager";
	     return $this->transport->cast(new core_usermanager_data_User(), $this->transport->sendMessage($data));
	}

	/**
	* If an administrator is impersonating a lower user,
	* this function will return true.
	*
	* @return List
	* @throws ErrorException
	*/

	public function getLogins($year) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["year"] = json_encode($this->transport->object_unset_nulls($year));
	     $data["method"] = "getLogins";
	     $data["interfaceName"] = "core.usermanager.IUserManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Fetch a user
	* @param id
	* @return core_usermanager_data_User
	* @throws ErrorException
	*/

	public function getUserById($id) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["id"] = json_encode($this->transport->object_unset_nulls($id));
	     $data["method"] = "getUserById";
	     $data["interfaceName"] = "core.usermanager.IUserManager";
	     return $this->transport->cast(new core_usermanager_data_User(), $this->transport->sendMessage($data));
	}

	/**
	* Fetch all users with the given user ids.
	* @param userIds A list of user ids.
	* @return List
	* @throws ErrorException
	*/

	public function getUserList($userIds) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["userIds"] = json_encode($this->transport->object_unset_nulls($userIds));
	     $data["method"] = "getUserList";
	     $data["interfaceName"] = "core.usermanager.IUserManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Switch the context of what user you are logged in as.
	*
	* @throws ErrorException
	*/

	public function impersonateUser($userId) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["userId"] = json_encode($this->transport->object_unset_nulls($userId));
	     $data["method"] = "impersonateUser";
	     $data["interfaceName"] = "core.usermanager.IUserManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Check if the user is a real star trek hero!
	* @param id The id of the user to check on.
	* @return boolean
	* @throws ErrorException
	*/

	public function isCaptain($id) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["id"] = json_encode($this->transport->object_unset_nulls($id));
	     $data["method"] = "isCaptain";
	     $data["interfaceName"] = "core.usermanager.IUserManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* If an administrator is impersonating a lower user,
	* this function will return true.
	*
	* @return boolean
	* @throws ErrorException
	*/

	public function isImpersonating() {
	     $data = array();
	     $data['args'] = array();
	     $data["method"] = "isImpersonating";
	     $data["interfaceName"] = "core.usermanager.IUserManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Check if this session is logged on or not.
	* @return boolean
	* @throws ErrorException
	*/

	public function isLoggedIn() {
	     $data = array();
	     $data['args'] = array();
	     $data["method"] = "isLoggedIn";
	     $data["interfaceName"] = "core.usermanager.IUserManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Logon a given user.
	* @param email The username to use when logging on, an also be the users email.
	* @param password The password for this user in plain text.
	* @return core_usermanager_data_User
	* @throws ErrorException
	*/

	public function logOn($username, $password) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["username"] = json_encode($this->transport->object_unset_nulls($username));
	     $data['args']["password"] = json_encode($this->transport->object_unset_nulls($password));
	     $data["method"] = "logOn";
	     $data["interfaceName"] = "core.usermanager.IUserManager";
	     return $this->transport->cast(new core_usermanager_data_User(), $this->transport->sendMessage($data));
	}

	/**
	* Sometimes it is needed for someone to logon using a generated key instead.<br>
	* The key is unique and attached to the user trying to logon.<br>
	* Whenever someone logs on using the key,<br> it will automatically be removed, this it is only valid once.<br>
	* @param logonKey A unique key identifying the user which is trying to logon.
	* @return core_usermanager_data_User
	* @throws ErrorException
	*/

	public function logonUsingKey($logonKey) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["logonKey"] = json_encode($this->transport->object_unset_nulls($logonKey));
	     $data["method"] = "logonUsingKey";
	     $data["interfaceName"] = "core.usermanager.IUserManager";
	     return $this->transport->cast(new core_usermanager_data_User(), $this->transport->sendMessage($data));
	}

	/**
	* Logout the currently logged on user.
	* @return void
	* @throws ErrorException
	*/

	public function logout() {
	     $data = array();
	     $data['args'] = array();
	     $data["method"] = "logout";
	     $data["interfaceName"] = "core.usermanager.IUserManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Removes the comment from a user
	* @param userId
	* @param commentId
	* @throws ErrorException
	*/

	public function removeComment($userId, $commentId) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["userId"] = json_encode($this->transport->object_unset_nulls($userId));
	     $data['args']["commentId"] = json_encode($this->transport->object_unset_nulls($commentId));
	     $data["method"] = "removeComment";
	     $data["interfaceName"] = "core.usermanager.IUserManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Delete a specified group.
	*
	* @param groupId
	* @throws ErrorException
	*/

	public function removeGroup($groupId) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["groupId"] = json_encode($this->transport->object_unset_nulls($groupId));
	     $data["method"] = "removeGroup";
	     $data["interfaceName"] = "core.usermanager.IUserManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* This function will return a user new admin user that has access to only invoke the function
	* specified in the paramters.
	*
	* The password field on the user will be in cleartext so it can be saved by the application
	* that request this feature.
	*
	* @param managerName
	* @param managerFunction
	* @return core_usermanager_data_User
	* @throws ErrorException
	*/

	public function requestAdminRight($managerName, $managerFunction, $applicationInstanceId) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["managerName"] = json_encode($this->transport->object_unset_nulls($managerName));
	     $data['args']["managerFunction"] = json_encode($this->transport->object_unset_nulls($managerFunction));
	     $data['args']["applicationInstanceId"] = json_encode($this->transport->object_unset_nulls($applicationInstanceId));
	     $data["method"] = "requestAdminRight";
	     $data["interfaceName"] = "core.usermanager.IUserManager";
	     return $this->transport->cast(new core_usermanager_data_User(), $this->transport->sendMessage($data));
	}

	/**
	* When the reset code has been sent, you can reset your password with the given reset code.
	* @param resetCode The code sent by sendResetCode call.
	* @param username The username for the user to update, the email address is the most common username.
	* @param newPassword The new password to send as plain text.
	* @return void
	* @throws ErrorException
	*/

	public function resetPassword($resetCode, $username, $newPassword) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["resetCode"] = json_encode($this->transport->object_unset_nulls($resetCode));
	     $data['args']["username"] = json_encode($this->transport->object_unset_nulls($username));
	     $data['args']["newPassword"] = json_encode($this->transport->object_unset_nulls($newPassword));
	     $data["method"] = "resetPassword";
	     $data["interfaceName"] = "core.usermanager.IUserManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Create a new group.
	* A group is a way to collect all users
	* together. If an administrator belongs to a
	* group, it will only be able to see/modify the
	* users that are within the same group.
	*
	* @param groupName
	* @param imageId
	* @throws ErrorException
	*/

	public function saveGroup($core_usermanager_data_Group) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["core_usermanager_data_Group"] = json_encode($this->transport->object_unset_nulls($core_usermanager_data_Group));
	     $data["method"] = "saveGroup";
	     $data["interfaceName"] = "core.usermanager.IUserManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* Update a given user.<br>
	* This will fail if you are trying to update a user which is granted more access then you have yourself.
	* @param user You can not change the password, use updatePassword to change the password.
	* @return void
	* @throws ErrorException
	*/

	public function saveUser($core_usermanager_data_User) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["core_usermanager_data_User"] = json_encode($this->transport->object_unset_nulls($core_usermanager_data_User));
	     $data["method"] = "saveUser";
	     $data["interfaceName"] = "core.usermanager.IUserManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* If you need to reset the password for a given user, you need fetch a reset code by calling this call.
	* @param title The title of the message to attach to the reset code.
	* @param text The text to attach to the mail being sent with the reset code.
	* @param username The username to identify the user, the email address is the most common username.
	* @return void
	* @throws ErrorException
	*/

	public function sendResetCode($title, $text, $username) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["title"] = json_encode($this->transport->object_unset_nulls($title));
	     $data['args']["text"] = json_encode($this->transport->object_unset_nulls($text));
	     $data['args']["username"] = json_encode($this->transport->object_unset_nulls($username));
	     $data["method"] = "sendResetCode";
	     $data["interfaceName"] = "core.usermanager.IUserManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	*
	* @param userId The id for the user to modify.
	* @param oldPassword The old password as plain text, if you have a userlevel above, the oldpassword will be ignored.
	* @param newPassword The new password as plain text.
	* @throws ErrorException
	*/

	public function updatePassword($userId, $oldPassword, $newPassword) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["userId"] = json_encode($this->transport->object_unset_nulls($userId));
	     $data['args']["oldPassword"] = json_encode($this->transport->object_unset_nulls($oldPassword));
	     $data['args']["newPassword"] = json_encode($this->transport->object_unset_nulls($newPassword));
	     $data["method"] = "updatePassword";
	     $data["interfaceName"] = "core.usermanager.IUserManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	* If an administrator is impersonating a lower user,
	* this function will return true.
	*
	* @return void
	* @throws ErrorException
	*/

	public function upgradeUserToGetShopAdmin($password) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["password"] = json_encode($this->transport->object_unset_nulls($password));
	     $data["method"] = "upgradeUserToGetShopAdmin";
	     $data["interfaceName"] = "core.usermanager.IUserManager";
	     return $this->transport->sendMessage($data);
	}

}
class APIUtilManager {

	var $transport;
	
	function APIUtilManager($transport) {
		$this->transport = $transport;
	}

	/**
	*
	* @author ktonder
	*/

	public function getCompaniesFromBrReg($search) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["search"] = json_encode($this->transport->object_unset_nulls($search));
	     $data["method"] = "getCompaniesFromBrReg";
	     $data["interfaceName"] = "core.utils.IUtilManager";
	     return $this->transport->sendMessage($data);
	}

	/**
	*
	* @author ktonder
	*/

	public function getCompanyFromBrReg($companyVatNumber) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["companyVatNumber"] = json_encode($this->transport->object_unset_nulls($companyVatNumber));
	     $data["method"] = "getCompanyFromBrReg";
	     $data["interfaceName"] = "core.utils.IUtilManager";
	     return $this->transport->cast(new core_usermanager_data_Company(), $this->transport->sendMessage($data));
	}

	/**
	*
	* @author ktonder
	*/

	public function getFile($id) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["id"] = json_encode($this->transport->object_unset_nulls($id));
	     $data["method"] = "getFile";
	     $data["interfaceName"] = "core.utils.IUtilManager";
	     return $this->transport->cast(new core_utilmanager_data_FileObject(), $this->transport->sendMessage($data));
	}

	/**
	*
	* @author ktonder
	*/

	public function saveFile($core_utilmanager_data_FileObject) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["core_utilmanager_data_FileObject"] = json_encode($this->transport->object_unset_nulls($core_utilmanager_data_FileObject));
	     $data["method"] = "saveFile";
	     $data["interfaceName"] = "core.utils.IUtilManager";
	     return $this->transport->sendMessage($data);
	}

}
class APIYouTubeManager {

	var $transport;
	
	function APIYouTubeManager($transport) {
		$this->transport = $transport;
	}

	/**
	* The youtube manager handles the communication between the google youtube api and the frontend.
	*/

	public function searchYoutube($searchword) {
	     $data = array();
	     $data['args'] = array();
	     $data['args']["searchword"] = json_encode($this->transport->object_unset_nulls($searchword));
	     $data["method"] = "searchYoutube";
	     $data["interfaceName"] = "core.youtubemanager.IYouTubeManager";
	     return $this->transport->sendMessage($data);
	}

}
class GetShopApi {

      var $transport;
      function GetShopApi($port, $host="localhost", $sessionId) {
           $this->transport = new CommunicationHelper();
           $this->transport->port = $port;
           $this->transport->sessionId = $sessionId;
           $this->transport->host = $host;
           $this->transport->connect();
      }
      /**
      * @return BannerManager
      */
      public function getBannerManager() {
           return new APIBannerManager($this->transport);
      }
      /**
      * @return BigStock
      */
      public function getBigStock() {
           return new APIBigStock($this->transport);
      }
      /**
      * @return BrainTreeManager
      */
      public function getBrainTreeManager() {
           return new APIBrainTreeManager($this->transport);
      }
      /**
      * @return CalendarManager
      */
      public function getCalendarManager() {
           return new APICalendarManager($this->transport);
      }
      /**
      * @return CarTuningManager
      */
      public function getCarTuningManager() {
           return new APICarTuningManager($this->transport);
      }
      /**
      * @return CartManager
      */
      public function getCartManager() {
           return new APICartManager($this->transport);
      }
      /**
      * @return ChatManager
      */
      public function getChatManager() {
           return new APIChatManager($this->transport);
      }
      /**
      * @return ContentManager
      */
      public function getContentManager() {
           return new APIContentManager($this->transport);
      }
      /**
      * @return FooterManager
      */
      public function getFooterManager() {
           return new APIFooterManager($this->transport);
      }
      /**
      * @return GalleryManager
      */
      public function getGalleryManager() {
           return new APIGalleryManager($this->transport);
      }
      /**
      * @return GetShop
      */
      public function getGetShop() {
           return new APIGetShop($this->transport);
      }
      /**
      * @return GetShopApplicationPool
      */
      public function getGetShopApplicationPool() {
           return new APIGetShopApplicationPool($this->transport);
      }
      /**
      * @return HotelBookingManager
      */
      public function getHotelBookingManager() {
           return new APIHotelBookingManager($this->transport);
      }
      /**
      * @return InformationScreenManager
      */
      public function getInformationScreenManager() {
           return new APIInformationScreenManager($this->transport);
      }
      /**
      * @return InvoiceManager
      */
      public function getInvoiceManager() {
           return new APIInvoiceManager($this->transport);
      }
      /**
      * @return ListManager
      */
      public function getListManager() {
           return new APIListManager($this->transport);
      }
      /**
      * @return LogoManager
      */
      public function getLogoManager() {
           return new APILogoManager($this->transport);
      }
      /**
      * @return MessageManager
      */
      public function getMessageManager() {
           return new APIMessageManager($this->transport);
      }
      /**
      * @return MobileManager
      */
      public function getMobileManager() {
           return new APIMobileManager($this->transport);
      }
      /**
      * @return NewsLetterManager
      */
      public function getNewsLetterManager() {
           return new APINewsLetterManager($this->transport);
      }
      /**
      * @return NewsManager
      */
      public function getNewsManager() {
           return new APINewsManager($this->transport);
      }
      /**
      * @return OrderManager
      */
      public function getOrderManager() {
           return new APIOrderManager($this->transport);
      }
      /**
      * @return PageManager
      */
      public function getPageManager() {
           return new APIPageManager($this->transport);
      }
      /**
      * @return PkkControlManager
      */
      public function getPkkControlManager() {
           return new APIPkkControlManager($this->transport);
      }
      /**
      * @return ProductManager
      */
      public function getProductManager() {
           return new APIProductManager($this->transport);
      }
      /**
      * @return ReportingManager
      */
      public function getReportingManager() {
           return new APIReportingManager($this->transport);
      }
      /**
      * @return SedoxProductManager
      */
      public function getSedoxProductManager() {
           return new APISedoxProductManager($this->transport);
      }
      /**
      * @return StoreApplicationInstancePool
      */
      public function getStoreApplicationInstancePool() {
           return new APIStoreApplicationInstancePool($this->transport);
      }
      /**
      * @return StoreApplicationPool
      */
      public function getStoreApplicationPool() {
           return new APIStoreApplicationPool($this->transport);
      }
      /**
      * @return StoreManager
      */
      public function getStoreManager() {
           return new APIStoreManager($this->transport);
      }
      /**
      * @return UserManager
      */
      public function getUserManager() {
           return new APIUserManager($this->transport);
      }
      /**
      * @return UtilManager
      */
      public function getUtilManager() {
           return new APIUtilManager($this->transport);
      }
      /**
      * @return YouTubeManager
      */
      public function getYouTubeManager() {
           return new APIYouTubeManager($this->transport);
      }
}
?>