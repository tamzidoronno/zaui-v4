<?php
class core_pmsmanager_PmsConfiguration extends core_common_DataCommon  {
	/** @var String */
	public $timezone;

	/** @var String */
	public $isPikStore;

	/** @var String */
	public $emails;

	/** @var String */
	public $emailTitles;

	/** @var String */
	public $smses;

	/** @var String */
	public $adminmessages;

	/** @var String */
	public $defaultMessage;

	/** @var String */
	public $priceCalcPlugins;

	/** @var String */
	public $emailTemplate;

	/** @var java.util.HashMap */
	public $budget;

	/** @var String */
	public $sendMessagesRegardlessOfPayments;

	/** @var core_pmsmanager_TimeRepeaterData[] */
	public $closedOfPeriode;

	/** @var String */
	public $contracts;

	/** @var String */
	public $fireinstructions;

	/** @var String */
	public $otherinstructions;

	/** @var String */
	public $cleaninginstruction;

	/** @var String */
	public $maintanceinstruction;

	/** @var String */
	public $needConfirmation;

	/** @var String */
	public $needToAgreeOnContract;

	/** @var String */
	public $exposeUnsecureBookings;

	/** @var String */
	public $autoconfirmRegisteredUsers;

	/** @var String */
	public $avoidRandomRoomAssigning;

	/** @var String */
	public $numberOfHoursToExtendLateCheckout;

	/** @var String */
	public $minStay;

	/** @var String */
	public $defaultNumberOfDaysBack;

	/** @var String */
	public $hourOfDayToStartBoarding;

	/** @var String */
	public $supportMoreDates;

	/** @var String */
	public $isItemBookingInsteadOfTypes;

	/** @var String */
	public $autoExtend;

	/** @var String */
	public $copyEmailsToOwnerOfStore;

	/** @var String */
	public $ignoreTimeIntervalsOnNotification;

	/** @var String */
	public $hasNoEndDate;

	/** @var String */
	public $autoDeleteUnpaidBookings;

	/** @var String */
	public $deleteAllWhenAdded;

	/** @var String */
	public $manualcheckincheckout;

	/** @var String */
	public $markBookingsWithNoOrderAsUnpaid;

	/** @var String */
	public $fastCheckIn;

	/** @var String */
	public $denyUpdateUserWhenTransferredToAccounting;

	/** @var String */
	public $functionsEnabled;

	/** @var String */
	public $needConfirmationInWeekEnds;

	/** @var String */
	public $closedUntil;

	/** @var java.util.HashMap */
	public $addonConfiguration;

	/** @var java.util.HashMap */
	public $inventoryList;

	/** @var java.util.HashMap */
	public $cleaningPriceConfig;

	/** @var String */
	public $emailsToNotify;

	/** @var String */
	public $extraCleaningCost;

	/** @var java.util.HashMap */
	public $mobileViews;

	/** @var String */
	public $mobileViewRestrictions;

	/** @var String */
	public $bookingProfile;

	/** @var String */
	public $markDirtyEvenWhenCodeNotPressed;

	/** @var String */
	public $doNotRecommendBestPrice;

	/** @var String */
	public $fastOrderCreation;

	/** @var String */
	public $autoSendPaymentReminder;

	/** @var String */
	public $supportRemoveWhenFull;

	/** @var String */
	public $supportDiscounts;

	/** @var String */
	public $autoSendInvoice;

	/** @var String */
	public $autoMarkCreditNotesAsPaidFor;

	/** @var String */
	public $autoCreateInvoices;

	/** @var String */
	public $usePriceMatrixOnOrder;

	/** @var String */
	public $splitOrderIntoMonths;

	/** @var String */
	public $orderEndsFirstInMonth;

	/** @var String */
	public $whenInfinteDateFirstOrderTimeUnits;

	/** @var String */
	public $autoGenerateChangeOrders;

	/** @var String */
	public $grantAccessEvenWhenNotPaid;

	/** @var String */
	public $runAutoPayWithCard;

	/** @var String */
	public $numberOfDaysToTryToPayWithCardAfterStayOrderHasBeenCreated;

	/** @var String */
	public $warnWhenOrderNotPaidInDays;

	/** @var String */
	public $chargeOrderAtDayInMonth;

	/** @var String */
	public $prepayment;

	/** @var String */
	public $payAfterBookingCompleted;

	/** @var String */
	public $requirePayments;

	/** @var String */
	public $updatePriceWhenChangingDates;

	/** @var String */
	public $prepaymentDaysAhead;

	/** @var String */
	public $increaseUnits;

	/** @var String */
	public $substractOneDayOnOrder;

	/** @var String */
	public $includeGlobalOrderCreationPanel;

	/** @var String */
	public $autoSendToCreditor;

	/** @var String */
	public $forceRequiredFieldsForEditors;

	/** @var String */
	public $automarkInvoicesAsPaid;

	/** @var String */
	public $notifyGetShopAboutCriticalTransactions;

	/** @var String */
	public $autoSumarizeCartItems;

	/** @var String */
	public $numberOfDaysToSendPaymentLinkAheadOfStay;

	/** @var String */
	public $ignorePaymentWindowDaysAheadOfStay;

	/** @var String */
	public $ignoreRoomToEndDate;

	/** @var String */
	public $createVirtualOrders;

	/** @var String */
	public $enableCoveragePrices;

	/** @var String */
	public $warnIfOrderNotPaidFirstTimeInHours;

	/** @var String */
	public $bookingTimeInterval;

	/** @var String */
	public $defaultStart;

	/** @var String */
	public $defaultEnd;

	/** @var String */
	public $extraField;

	/** @var String */
	public $smsName;

	/** @var String */
	public $childMaxAge;

	/** @var java.util.HashMap */
	public $lockServerConfigs;

	/** @var String */
	public $useNewQueueCheck;

	/** @var String */
	public $locktype;

	/** @var String */
	public $arxHostname;

	/** @var String */
	public $arxUsername;

	/** @var String */
	public $arxPassword;

	/** @var String */
	public $arxCardFormat;

	/** @var String */
	public $arxCardFormatsAvailable;

	/** @var String */
	public $codeSize;

	/** @var String */
	public $keepDoorOpenWhenCodeIsPressed;

	/** @var String */
	public $closeAllDoorsAfterTime;

	/** @var String */
	public $closeRoomNotCleanedAtHour;

	/** @var String */
	public $cleaningInterval;

	/** @var String */
	public $cleaningDays;

	/** @var String */
	public $numberOfCheckoutCleanings;

	/** @var String */
	public $numberOfIntervalCleaning;

	/** @var String */
	public $cleaningNextDay;

	/** @var String */
	public $unsetCleaningIfJustSetWhenChangingRooms;

	/** @var String */
	public $automaticallyCloseRoomIfDirtySameDay;

	/** @var String */
	public $autoAddMissingItemsToRoom;

	/** @var String */
	public $autoNotifyCareTakerForMissingInventory;

	/** @var String */
	public $whenCleaningEndStayForGuestCheckinOut;

	/** @var String */
	public $senderName;

	/** @var String */
	public $senderEmail;

	/** @var String */
	public $sendAdminTo;

	/** @var String */
	public $wubookusername;

	/** @var String */
	public $wubookpassword;

	/** @var String */
	public $wubookproviderkey;

	/** @var String */
	public $wubooklcode;

	/** @var String */
	public $numberOfRoomsToRemoveFromBookingCom;

	/** @var String */
	public $usePricesFromChannelManager;

	/** @var String */
	public $useGetShopPricesOnExpedia;

	/** @var String */
	public $ignoreNoShow;

	/** @var String */
	public $increaseByPercentage;

	/** @var String */
	public $tripTeaseHotelId;

	/** @var java.util.HashMap */
	public $channelConfiguration;

}
?>