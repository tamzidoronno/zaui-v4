<?php
class core_ordermanager_data_Order extends core_common_DataCommon  {
	/** @var String */
	public $triedTransferredToAccountingSystem;

	/** @var String */
	public $transferredToAccountingSystem;

	/** @var String */
	public $transferredToCreditor;

	/** @var String */
	public $transferedToAccountingSystem;

	/** @var String */
	public $transferToAccountingDate;

	/** @var String */
	public $shouldHaveBeenTransferredToAccountingOnDate;

	/** @var String */
	public $needCollectingDate;

	/** @var String */
	public $paymentTransactionId;

	/** @var core_ordermanager_data_Shipping */
	public $shipping;

	/** @var String */
	public $shippingDate;

	/** @var core_ordermanager_data_Payment */
	public $payment;

	/** @var String */
	public $session;

	/** @var String */
	public $recieptEmail;

	/** @var String */
	public $trackingNumber;

	/** @var String */
	public $incrementOrderId;

	/** @var String */
	public $reference;

	/** @var String */
	public $activated;

	/** @var String */
	public $testOrder;

	/** @var String */
	public $captured;

	/** @var core_ordermanager_data_CardTransaction[] */
	public $transactions;

	/** @var core_ordermanager_data_OrderLog[] */
	public $logLines;

	/** @var String */
	public $notifications;

	/** @var String */
	public $invoiceNote;

	/** @var String */
	public $closed;

	/** @var String */
	public $creditOrderId;

	/** @var String */
	public $isCreditNote;

	/** @var String */
	public $startDate;

	/** @var String */
	public $endDate;

	/** @var String */
	public $paymentDate;

	/** @var String */
	public $markedAsPaidByUserId;

	/** @var String */
	public $paymentTerms;

	/** @var String */
	public $parentOrder;

	/** @var String */
	public $sentToCustomer;

	/** @var String */
	public $cleaned;

	/** @var String */
	public $dateTransferredToAccount;

	/** @var String */
	public $avoidAutoSending;

	/** @var String */
	public $dueDays;

	/** @var String */
	public $createByManager;

	/** @var String */
	public $kid;

	/** @var String */
	public $isVirtual;

	/** @var String */
	public $forcedOpen;

	/** @var String */
	public $warnedNotAbleToPay;

	/** @var String */
	public $attachedToRoom;

	/** @var core_ordermanager_data_OrderShipmentLogEntry[] */
	public $shipmentLog;

	/** @var class com_thundashop_core_ordermanager_data_OrderTransaction[] */
	public $orderTransactions;

	/** @var String */
	public $createdBasedOnOrderIds;

	/** @var String */
	public $bookingHasBeenDeleted;

	/** @var String */
	public $sendRegningId;

	/** @var String */
	public $sentToCustomerDate;

	/** @var String */
	public $sentToEmail;

	/** @var String */
	public $sentToPhone;

	/** @var String */
	public $sentToPhonePrefix;

	/** @var String */
	public $chargeAfterDate;

	/** @var String */
	public $warnedNotPaid;

	/** @var String */
	public $tryAutoPayWithDibs;

	/** @var String */
	public $accountingReference;

	/** @var String */
	public $wubookid;

	/** @var String */
	public $warnedNotAbleToCapture;

	/** @var String */
	public $periodeDaySleptStart;

	/** @var String */
	public $periodeDaySleptEnd;

	/** @var String */
	public $cal2;

	/** @var String */
	public $cal1;

	/** @var String */
	public $isUnderConstruction;

	/** @var String */
	public $createdDate;

	/** @var String */
	public $expiryDate;

	/** @var String */
	public $recurringDays;

	/** @var String */
	public $recurringMonths;

	/** @var String */
	public $userId;

	/** @var String */
	public $status;

	/** @var core_cartmanager_data_Cart */
	public $cart;

}
?>