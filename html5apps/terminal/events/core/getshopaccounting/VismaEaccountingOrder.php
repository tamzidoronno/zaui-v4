<?php
class core_getshopaccounting_VismaEaccountingOrder {
	/** @var String */
	public $Id;

	/** @var String */
	public $Amount;

	/** @var String */
	public $VatAmount;

	/** @var String */
	public $customerId;

	/** @var String */
	public $currencyCode;

	/** @var String */
	public $OrderDate;

	/** @var String */
	public $RoundingsAmount;

	/** @var String */
	public $EuThirdParty;

	/** @var String */
	public $status;

	/** @var core_getshopaccounting_VismaEaccountingOrderLine[] */
	public $Rows;

	/** @var String */
	public $CustomerIsPrivatePerson;

	/** @var String */
	public $RotReducedInvoicingType;

	/** @var String */
	public $RotPropertyType;

	/** @var String */
	public $ReverseChargeOnConstructionServices;

	/** @var String */
	public $InvoiceCountryCode;

	/** @var String */
	public $InvoiceCustomerName;

	/** @var String */
	public $InvoicePostalCode;

	/** @var String */
	public $InvoiceAddress1;

	/** @var String */
	public $InvoiceCity;

	/** @var String */
	public $DeliveryPostalCode;

}
?>