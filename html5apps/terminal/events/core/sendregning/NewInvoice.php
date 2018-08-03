<?php
class core_sendregning_NewInvoice {
	/** @var String */
	public $orderNumber;

	/** @var String */
	public $InvoiceDate;

	/** @var String */
	public $dueDate;

	/** @var String */
	public $orderDate;

	/** @var String */
	public $ourReference;

	/** @var String */
	public $yourReference;

	/** @var String */
	public $invoiceText;

	/** @var String */
	public $deliveryDate;

	/** @var core_sendregning_NewInvoiceAddress */
	public $deliveryAddress;

	/** @var core_sendregning_NewInvoiceRecipient */
	public $recipient;

	/** @var core_sendregning_NewInvoiceShipment */
	public $shipment;

	/** @var core_sendregning_NewInvoiceItem[] */
	public $items;

}
?>