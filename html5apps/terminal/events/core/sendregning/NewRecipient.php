<?php
class core_sendregning_NewRecipient {
	/** @var String */
	public $number;

	/** @var String */
	public $customerNumber;

	/** @var String */
	public $name;

	/** @var String */
	public $email;

	/** @var String */
	public $organisationNumber;

	/** @var core_sendregning_NewInvoiceRecipientContact */
	public $contact;

	/** @var core_sendregning_NewInvoiceRecipientAdress */
	public $address;

}
?>