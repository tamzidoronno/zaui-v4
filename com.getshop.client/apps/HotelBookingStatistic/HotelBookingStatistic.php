<?php

/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

namespace ns_8b42b5cf_189c_4382_ba52_c1919841aad4;

class HotelBookingStatistic extends \WebshopApplication implements \Application {
    public function getDescription() {
        return "Desc";
    }

    public function getName() {
        return "HotelBookingStatistic";
    }

    public function render() {
        if (!isset($_SESSION['hotel_statistic_start']))
            $_SESSION['hotel_statistic_start'] = "";
        
        if (!isset($_SESSION['hotel_statistic_end']))
            $_SESSION['hotel_statistic_end'] = "";
        
        $this->includefile("statistic");
    }
    
    public function setDates() {
        $_SESSION['hotel_statistic_start'] = $_POST['data']['start'];
        $_SESSION['hotel_statistic_end'] = $_POST['data']['end'];
    }

    public function getOrders() {
        $orders = $this->getApi()->getOrderManager()->getOrders(null, null, null);
        
        $retOrders = array();
        
        if (!$_SESSION['hotel_statistic_start'] || !$_SESSION['hotel_statistic_end']) {
            return $retOrders;
        }
        
        $fromDate = strtotime($_SESSION['hotel_statistic_start']);
        $toDate = strtotime($_SESSION['hotel_statistic_end']);
        
        foreach ($orders as $order) {
            
            $startDate = strtotime($order->startDate);
            if ($startDate > $fromDate && $startDate < $toDate) {
                $retOrders[] = $order;
            }
        }
        
        return $retOrders;
    }
}