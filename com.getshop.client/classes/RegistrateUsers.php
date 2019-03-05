<?php

class RegistrateUsers {
    public function printRegisterCompanyForm() {
        ?>
        <div class='nextstep nextstepcompany' gstype='form' method='createNewCompanyCustomer'>
            <input type='hidden' gsname='type' value='company'>
            <div>
                <div style='margin-top:10px;'>Enter vat number and company name <span class='searchbrregbutton'>(search brreg)</span></div>
                <span class='searchbrregarea' gstype='form' method='searchbrreg'>
                    <input type='text' class='gsniceinput1' gsname='name'>
                    <span class='shop_button' gstype='submit' gs_callback='searchResult_externalregister'>Search</span>
                    <div class='brregsearchresult' style='max-height: 100px; overflow-y: auto;'></div>
                </span>
                <input type='txt' class='gsniceinput1' gsname='vatnumber' placeholder='Vat number'>
                <input type='txt' class='gsniceinput1' gsname='name' placeholder='Name'>
                <div><br>
                    Address<br>
                    <input type='txt' class='gsniceinput1' gsname='adress' placeholder='Street name'>
                    <input type='txt' class='gsniceinput1' gsname='postcode' placeholder='Postcode'>
                    <input type='txt' class='gsniceinput1' gsname='city' placeholder='City'>
                </div>
                <div><br>
                    Contact details<br>
                    <input type='txt' class='gsniceinput1' gsname='prefix' placeholder='Prefix' style="width:60px;">
                    <input type='txt' class='gsniceinput1' gsname='phone' placeholder='Phone'>
                    <input type='txt' class='gsniceinput1' gsname='email' placeholder='Email'>
                </div>
                <span class='shop_button' gstype='submit'>Register customer</span>
            </div>
        </div>


        <script>
        function searchResult_externalregister(res) {
            if(res) {
                console.log(res);
                $('.brregsearchresult').html('');
                for(var k in res) {
                    var company = res[k];
                    var row = $('<div class="getshopbrregsearchresultrow" data-object=\''+JSON.stringify(company)+'\'><span>' + company.vatNumber + "</span> - <span>" + company.name + "</span></div>");
                    $('.brregsearchresult').append(row);
                }
            }
        }
        
        $(document).on('click', '.getshopbrregsearchresultrow', function() {
            var company = JSON.parse($(this).attr('data-object'));
            console.log(company);
            $('[gsname="vatnumber"]').val(company.vatNumber);
            $('[gsname="name"]').val(company.name);
            $('[gsname="adress"]').val(company.address);
            $('[gsname="postcode"]').val(company.postnumber);
            $('[gsname="city"]').val(company.city);
            $('[gsname="prefix"]').val(company.prefix);
            $('[gsname="phone"]').val(company.phone);
            $('[gsname="email"]').val(company.email);
            $('.searchbrregarea').hide();
        });
        
        </script>
        <?php
    }
    
    /**
     * 
     * @param GetShopApi $api
     * @return boolean
     */
    public function createNewCompanyCustomer($api) {
        $vatnumber = $_POST['data']['vatnumber'];
        $name = $_POST['data']['name'];
        
        $user = $api->getUserManager()->createCompany($vatnumber, $name);
        /* @var $user core_usermanager_data_User */
        if($user) {
            if(!$user->address) {
                $user->address = new core_usermanager_data_Address();
            }
            $user->address->address = $_POST['data']['adress'];
            $user->address->city = $_POST['data']['city'];
            $user->address->postCode = $_POST['data']['postcode'];
            $user->address->phone = $_POST['data']['phone'];
            $user->emailAddress = $_POST['data']['email'];
            $user->cellPhone = $_POST['data']['phone'];
            $user->prefix = $_POST['data']['prefix'];
            $api->getUserManager()->saveUser($user);
            return $user;
        }
        return false;
    }

    public function printPrivateForm() {
        ?>
        <div class='nextstep nextstepprivate' style='margin-top: 25px;' gstype='form' method='registerprivateperson'>
            <span class='selecteduser' style='display:none;'><i class='fa fa-trash-o' style='cursor:pointer;'></i> <span class='name'></span></span>
            <input type='text' class='gsniceinput1' placeholder="Name of group holder" gsname='nameofholder'>
            <span class='shop_button' gstype='submit'>Continue, reservation details is added in the next step <i class='fa fa-arrow-right'></i></span>
            <div><br>
                Address<br>
                <input type='txt' class='gsniceinput1' gsname='adress' placeholder='Street name'>
                <input type='txt' class='gsniceinput1' gsname='postcode' placeholder='Postcode'>
                <input type='txt' class='gsniceinput1' gsname='city' placeholder='City'>
            </div>
            <div><br>
                Contact details<br>
                <input type='txt' class='gsniceinput1' gsname='prefix' placeholder='Prefix' style="width:50px;">
                <input type='txt' class='gsniceinput1' gsname='phone' placeholder='Phone'>
                <input type='txt' class='gsniceinput1' gsname='email' placeholder='Email'>
            </div>
            <div class='alreadyexists'></div>
        </div>
        
        <script>
            $(document).on('keyup','.PmsNewBooking [gsname="nameofholder"]',getshop_checkForExisting);

            function getshop_checkForExisting() {
                var text = $(this).val();
                if(text.length < 3) {
                    return;
                }

                var event = thundashop.Ajax.createEvent('','checkForExisiting',$('.PmsNewBooking'), {
                    "text" : text
                });
                thundashop.Ajax.postWithCallBack(event, function(res) {
                    $('.PmsNewBooking .alreadyexists').html(res);
                });
            }
        </script>
        <?php
    }

    /**
     * @param GetShopApi $api
     * @return type
     */
    public function registerprivateperson($api) {
        
        
        $user = new \core_usermanager_data_User();
        $user->fullName = $_POST['data']['nameofholder'];
        $user->address = new core_usermanager_data_Address();
        $user->address->address = $_POST['data']['adress'];
        $user->address->postCode = $_POST['data']['postcode'];
        $user->address->city = $_POST['data']['city'];
        $user->emailAddress = $_POST['data']['email'];
        $user->cellPhone = $_POST['data']['phone'];
        $user->prefix = $_POST['data']['prefix'];
        
        if(strlen($user->fullName) == 36) {
            $splitted = explode("-", $user->fullName);
            if(sizeof($splitted) == 5) {
                return $api->getUserManager()->getUserById($user->fullName)->id;
            }
        }
        
        $user = $api->getUserManager()->createUser($user);
        return $user->id;
    }

    public function checkForExisiting($api) {
        $text = $_POST['data']['text'];
        $users = $api->getUserManager()->findUsers($text);
        if(sizeof($users) == 0) {
            return;
        }
        
        echo "<div style='text-align:left; width: 100%; display:inline-block;font-size: 20px; margin-top: 20px; font-weight:bold;'>We already have users matching this search, is it one of the below?</div>";
        echo "<table style=width:100%;text-align:left; margin-bottom: 50px;'>";
        echo "<tr>";
        echo "<th>Name</th>";
        echo "<th>Email</th>";
        echo "<th>Prefix</th>";
        echo "<th>Phone</th>";
        echo "<th>Address</th>";
        echo "<th>Postcal code</th>";
        echo "<th>City</th>";
        echo "<th></th>";
        echo "</tr>";
        
        foreach($users as $user) {
            echo "<tr userid='".$user->id."' name='".$user->fullName."'>";
            echo "<td>" . $user->fullName . "</td>";
            echo "<td>" . $user->emailAddress . "</td>";
            echo "<td>" . $user->prefix . "</td>";
            echo "<td>" . $user->cellPhone . "</td>";
            echo "<td>"; if(isset($user->address)) { echo $user->address->address; } echo "</td>";
            echo "<td>"; if(isset($user->address)) { echo $user->address->postCode; } echo "</td>";
            echo "<td>"; if(isset($user->address)) { echo $user->address->city; } echo "</td>";
            echo "<td style='color:blue; cursor:pointer;' class='selectuser'>Select</td>";
            echo "</tr>";
        }
        echo "</table>";
        echo "<br>";
        echo "<br>";
        echo "<br>";
        echo "<br>";
        echo "<br>";    }

}
