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
require_once "Mail.php";


class contactus extends PageCommon {
    public $title = "GetShop - Contact us";
    public $title_rewrite = "pms";
    public $subpageof = "";
    
    public $menuSequence = 3;
    
    /** Menu settings */
    public $menuEntries = array('en' => "Contact us", "no" => "Kontakt oss");

    public function sendEmail() {
        $name = false;
        $email = false;
        $content = false;
        $phone = false;
        
        $tryingToSend = false;
        
        if (isset($_POST['name'])) {
            $tryingToSend = true;
            $name = $_POST['name'];
            $email = $_POST['email'];
            $content = $_POST['content'];
            $phone = $_POST['phone'];
        }
        
        if ($name && $email && content && phone) {
            $msg = "Name: ".$name."<br/>";
            $msg .= "Email: ".$email ."<br/>";
            $msg .= "Phone: ".$phone ."<br/>";
            $msg .= "Content<br/>";
            $msg .= "<br/>";
            $msg .= $content;
      
            $from = '<webpage@getshop.com>';
            $to = '<post@xprosjet.no>';
            $subject = 'Melding fra nettsiden';
            $body = $msg;

            $headers = array(
                'From' => $from,
                'To' => $to,
                'Subject' => $subject,
                'Content-Type'  => 'text/html; charset=UTF-8'
            );

            $smtp = Mail::factory('smtp', array(
                    'host' => 'ssl://smtp.gmail.com',
                    'port' => '465',
                    'auth' => true,
                    'username' => 'webpage@getshop.com',
                    'password' => 'asdfklajsdfoasd8fj48392512o43j'
                ));

            $mail = $smtp->send($to, $headers, $body);
            
            return true;
        }
        
        if ($tryingToSend) {
            echo "Please check your details above";
        }
        
        return false;
    }

}
