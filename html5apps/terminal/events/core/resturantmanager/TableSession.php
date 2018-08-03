<?php
class core_resturantmanager_TableSession extends core_common_DataCommon  {
	/** @var core_resturantmanager_ResturantCartItem[] */
	public $cartItems;

	/** @var String */
	public $tableId;

	/** @var String */
	public $createdByUserId;

	/** @var String */
	public $started;

	/** @var String */
	public $ended;

	/** @var core_usermanager_data_User */
	public $createByUser;

	/** @var String */
	public $first;

	/** @var core_resturantmanager_ResturantCartItem[] */
	public $itemsAdded;

	/** @var core_resturantmanager_ResturantCartItem[] */
	public $itemsRemoved;

}
?>