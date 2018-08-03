<?php

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Description of Router
 *
 * @author ktonder
 */
class Router {
    public function getPageName() {
        if (!isset($_POST['page']) && !isset($_GET['page'])) {
            return "home";
        }
        
        if (isset($_POST['page'])) {
            $_GET['page'] = $_POST['page'];
        }
        
        return $_GET['page'];
    }
    
    public function getPage() {
        switch ($this->getPageName()) {
            case "bookingfrontpage":
                return new BookingFrontPage();
            
            case "alreadybooked":
                return new AlreadyBookedPage();
                
            case "paymentprocesspage":
                return new PaymentProcessPage();
                
            case "paymentfailedpage":
                return new PaymentFailedPage();
                
            case "paymentsuccesspage":
                return new PaymentSuccessPage();
                
            case "checkbookingpage":
                return new CheckBookingPage();
                
            case "bookcampingpage":
                return new BookCampingPage();
                
            case "bookroompage":
                return new BookRoomPage();
                
            case "settings":
                return new Settings();
        }
        
        return new FrontPage();
    }
    
}