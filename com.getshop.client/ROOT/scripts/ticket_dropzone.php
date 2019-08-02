<?php

/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

chdir("../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();

function uuidv4()
{
    return implode('-', array(
        bin2hex(random_bytes(4)),
        bin2hex(random_bytes(2)),
        bin2hex(chr((ord(random_bytes(1)) & 0x0F) | 0x40)) . bin2hex(random_bytes(1)),
        bin2hex(chr((ord(random_bytes(1)) & 0x3F) | 0x80)) . bin2hex(random_bytes(1)),
        bin2hex(random_bytes(6))
    ));
}

$attachment = new \core_ticket_TicketAttachment();
$attachment->id = $_POST['uuid'];
$attachment->name = $_FILES['file']['name'];
$attachment->type = $_FILES['file']['type'];
$attachment->size = $_FILES['file']['size'];
$attachment->base64Content = base64_encode(file_get_contents( $_FILES['file']['tmp_name'] ));
$factory->getSystemGetShopApi()->getTicketManager()->uploadAttachment($attachment);
echo "OK";