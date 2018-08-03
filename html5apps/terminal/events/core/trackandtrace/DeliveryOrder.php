<?php
class core_trackandtrace_DeliveryOrder extends core_trackandtrace_TntOrder  {
	/** @var String */
	public $quantity;

	/** @var String */
	public $orderOdds;

	/** @var String */
	public $orderFull;

	/** @var String */
	public $orderLargeDisplays;

	/** @var String */
	public $orderDriverDeliveries;

	/** @var core_trackandtrace_ContainerType */
	public $containerType;

	/** @var String */
	public $orderType;

	/** @var String */
	public $driverDeliveryCopiesCounted;

}
?>