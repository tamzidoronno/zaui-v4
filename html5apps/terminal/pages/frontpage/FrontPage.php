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
class FrontPage extends Page {
    public function preRun() {
        $this->clearAll();
    }
    
    public function render() {
        $this->includeFile("frontpage");
    }

    public function getId() {
        
    }

    public function clearAll() {
        $campBook = new BookCampingPage();
        $campBook->clear();
        
        $roomBook = new BookRoomPage();
        $roomBook->clear();
        
        unset($_SESSION['currentlang']);
    }

}