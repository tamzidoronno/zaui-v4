<?php

chdir("../");
include '../loader.php';

$factory = IocContainer::getFactorySingelton();
$result = $factory->getApi()->getUserManager()->cleanUpUsers("jada");

if (!isset($_GET['sendmail'])) {
    return;
}

$count = 0;
foreach ($result as $email => $content) {
    if (strstr($email, "@")) {
        echo "<div>";
        echo "<div>".$email."</div>";
        echo "<div>".$content."</div>";
        echo "</div>";
        $count++;
    }
}

echo "<h2> Counts : $count ";

?>