<?php

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Description of PgaCommon
 *
 * @author ktonder
 */

namespace ns_752aee89_0abc_43cf_9067_5aeadfe07cc1;

class PgaCommon extends \MarketingApplication {
    private $conference = null;
    private $booking = null;
    private $paymentSummaries = null;
    
    /**
     * 
     * @return \core_pmsmanager_PmsConference
     */
    public function getConference() {
        
        if ($this->conference) {
            return $this->conference;
        }
        
        $this->conference = $this->getApi()->getPgaManager()->getConference($this->getSelectedMultilevelDomainName(), $_GET['token']);
        
        return $this->conference;
    }
    
    /**
     * 
     * @return \core_pmsmanager_PmsBooking
     */
    public function getBooking() {
        if (!$this->booking) {
            $this->booking = $this->getApi()->getPgaManager()->getBooking($this->getSelectedMultilevelDomainName(), $_GET['token']);
        }
        
        return $this->booking;
    }
    
    /**
     * 
     * @return \core_pmsmanager_PmsRoomPaymentSummary[]
     */
    public function getPaymentSummaries() {
        if (!$this->paymentSummaries) {
            $this->paymentSummaries = array();
            
            $toCheck = $this->getApi()->getPgaManager()->getSummaries($this->getSelectedMultilevelDomainName(), $_GET['token']);
            
            
            if (!$toCheck || !is_array($toCheck)) {
                $toCheck = array();
            }
            
            foreach ($toCheck as $summary) {
                $rowsToUse = array();
                $total = 0;
                
                foreach ($summary->rows as $row) {
                    if ($row->priceToCreateOrders) {
                        $rowsToUse[] = $row;
                        $total += $row->priceToCreateOrders * $row->count;
                    }
                }
                
                $summary->total = $total;
                $summary->rows = $rowsToUse;
                
                if (is_array($summary->rows) && count($summary->rows)) {
                    $this->paymentSummaries[] = $summary;
                }
            }
        }
        
        return $this->paymentSummaries;
    }
    
    public function getTotalUnpaid() {
        $totalUpaid = 0;
        
        foreach ($this->getPaymentSummaries() as $summary) {
            $totalUpaid += $summary->total;
        }
        
        return $totalUpaid;
    }
    
    public function checkForceGuestInformation() {
        $booking = $this->getBooking();
        $rooms = $booking->rooms;
        $token = $_GET['token'];
        
        $i = 0;
        if ($booking->forceGuestsPassportVerification) {
            $count = 0;
            foreach ($rooms as $room) {
                if ($room->deleted) {
                    continue;
                }
                foreach ($room->guests as $guest) {
                    if (!$guest->passportId) {
                        $count++;
                    }
                }
            }
            
            foreach ($rooms as $room) {
                if ($room->deleted) {
                    continue;
                }
                $i++;
                foreach ($room->guests as $guest) {
                    if (!$guest->passportId) {
                        $hasNext = $count > 1 ? "hasnext='true'" : "";
                        echo "<div $hasNext id='showForceUpdateInformation' gs_show_modal='updateguestinformation' token='$token' roomnumber='$i' roomid='$room->pmsBookingRoomId'></div>";
                        echo "<script>$('#showForceUpdateInformation').click();</script>";
                        return;
                    }
                    
                }
            }
        }
    }

}
