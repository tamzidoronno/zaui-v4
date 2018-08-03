<?php
class core_ticket_TicketEvent {
	/** @var core_ticket_TicketEventType */
	public $eventType;

	/** @var String */
	public $date;

	/** @var String */
	public $content;

	/** @var core_ticket_TicketState */
	public $state;

	/** @var String */
	public $updatedByUserId;

	/** @var String */
	public $isAnswer;

}
?>