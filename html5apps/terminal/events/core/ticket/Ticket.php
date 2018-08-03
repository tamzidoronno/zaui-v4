<?php
class core_ticket_Ticket extends core_common_DataCommon  {
	/** @var core_ticket_TicketEvent[] */
	public $events;

	/** @var String */
	public $userId;

	/** @var core_ticket_TicketType */
	public $type;

	/** @var core_ticket_TicketState */
	public $currentState;

	/** @var String */
	public $title;

	/** @var String */
	public $incrementalId;

	/** @var String */
	public $externalId;

	/** @var String */
	public $dateCompleted;

	/** @var String */
	public $timeSpent;

	/** @var String */
	public $timeInvoice;

	/** @var String */
	public $transferredToAccounting;

	/** @var String */
	public $productId;

}
?>