<?php
include '../loader.php';
$factory = IocContainer::getFactorySingelton();

$first = true;
if (($handle = fopen("mecaliste.des.csv", "r")) !== FALSE) {
    while (($data = fgetcsv($handle, 1000, ",")) !== FALSE) {
        if($first) {
            $first = false;
            continue;
        }
//        print_r($data);
        $email = $data[10];
        $name = $data[0];
        
        $address = new core_usermanager_data_Address();
        $address->postCode = $data[2];
        $address->city = $data[3];
        $address->countrycode = "NO";
        
        $user = new core_usermanager_data_User();
        $user->address = $address;
        $user->fullName = $data[0];
        $user->emailAddress = $data[10];
        $user->cellPhone = $data[7];
        $user->cellPhone = str_replace(" ", "", $user->cellPhone);
        $user->username = $user->emailAddress;
        $user->password = rand(100000, 100000000);
        $factory->getApi()->getUserManager()->createUser($user);
    }
}

?>
