<?php
chdir("../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();

$message = new \core_messagemanager_SmsMessage();
$message->to = $_GET['to'];
$message->from = $_GET['msisdn'];
$message->message = $_GET['text'];
$message->outGoing = false;

echo "<pre>"; print_r($message); echo "</pre>";
$factory->getApi()->getMessageManager()->saveIncomingMessage($message, $_GET['accessCode']);
?>