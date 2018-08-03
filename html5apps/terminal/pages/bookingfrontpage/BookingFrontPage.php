<?php

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Description of Home
 *
 * @author ktonder
 */
class BookingFrontPage extends Page{
    public function getId() {
        
    }

    public function preRun() {
        $campingPage = new BookCampingPage();
        $campingPage->clear();
        
        $campingPage = new BookRoomPage();
        $campingPage->clear();
    }
    public function render() {
        $this->includeFile('bookingfrontpage');
    }

    public function hasAnyCampingSpots() {
        $types = $this->getApi()->getBookingEngine()->getBookingItemTypes($this->getBookingEngineName());
        foreach ($types as $type) {
            if ($type->systemCategory == 3) {
                return true;
            }
        }
        
        return false;
    }
}
