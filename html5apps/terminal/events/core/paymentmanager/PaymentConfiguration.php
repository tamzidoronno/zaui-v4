<?php
class core_paymentmanager_PaymentConfiguration extends core_common_DataCommon  {
	/** @var String */
	public $isAllowedToManuallyMarkAsPaid;

	/** @var String */
	public $automaticallyCloseOrderWhenPaid;

	/** @var String */
	public $transferToAccountingBasedOnCreatedDate;

	/** @var String */
	public $transferToAccountingBasedOnPaymentDate;

	/** @var String */
	public $transferCreditNoteToAccountingBasedOnCreatedDate;

	/** @var String */
	public $transferCreditNoteToAccountingBasedOnPaymentDate;

}
?>