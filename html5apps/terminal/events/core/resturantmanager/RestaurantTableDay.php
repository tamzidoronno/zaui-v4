<?php
class core_resturantmanager_RestaurantTableDay extends core_common_DataCommon  {
	/** @var String */
	public $date;

	/** @var String */
	public $tableId;

	/** @var core_resturantmanager_TableReservation[] */
	public $events;

}
?>