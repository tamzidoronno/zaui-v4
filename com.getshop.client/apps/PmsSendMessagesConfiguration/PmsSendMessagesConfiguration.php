<?php
namespace ns_624fa4ac_9b27_4166_9fc3_5c1d1831b56b;

class PmsSendMessagesConfiguration extends \WebshopApplication implements \Application {
    public function getDescription() {
        
    }

    public function saveEmailTemplate() {
        $config = $this->getApi()->getPmsManager()->getConfiguration($this->getSelectedMultilevelDomainName());
        $config->emailTemplate = $_POST['data']['email'];
        $this->getApi()->getPmsManager()->saveConfiguration($this->getSelectedMultilevelDomainName(), $config);
    }
    
    public function toggleAvoidSendingConfirmationToOTA() {
        $config = $this->getApi()->getPmsManager()->getConfiguration($this->getSelectedMultilevelDomainName());
        $config->avoidSendingBookingConfigurationsToOTA = !$config->avoidSendingBookingConfigurationsToOTA;
        $this->getApi()->getPmsManager()->saveConfiguration($this->getSelectedMultilevelDomainName(), $config);
    }
    
    public function getName() {
        return "PmsSendMessagesConfiguration";
    }
    
    public function savepaymentlinksetup() {
        $paymentProductConfig = $this->getApi()->getPmsInvoiceManager()->getPaymentLinkConfig($this->getSelectedMultilevelDomainName());
        $paymentProductConfig->webAdress = $_POST['data']['webadress'];
        $this->getApi()->getPmsInvoiceManager()->savePaymentLinkConfig($this->getSelectedMultilevelDomainName(), $paymentProductConfig);
    }
    
    public function updateRoomMessage() {
        $item = $this->getApi()->getPmsManager()->getAdditionalInfo($this->getSelectedMultilevelDomainName(), $_POST['data']['id']);
        $item->textMessageDescription = $_POST['data']['textMessageDescription'];
        $this->getApi()->getPmsManager()->updateAdditionalInformationOnRooms($this->getSelectedMultilevelDomainName(), $item);
    }

    public function render() {
//        if($this->getApi()->getStoreManager()->getMyStore()->id == "7bb18e4a-7a5c-4a0a-9a59-7e7705f0f004") {
            $this->includefile("newemailoverview");
//        } else {
//            $this->includefile("addmessage");
//            $this->includefile("emailtemplate");
//        }
        $this->includefile("roomspecificmessages");
    }
    
    public function changeLanguage() {
        $_SESSION['PmsSendMessagesConfigurationLang'] = $_POST['data']['lang'];
    }
    
    function endsWith($haystack, $needle) {
        $length = strlen($needle);
        return $length === 0 || (substr($haystack, -$length) === $needle);
    }
    
    public function addMessage() {
        $notificationSettings = $this->getApi()->getPmsManager()->getConfiguration($this->getSelectedMultilevelDomainName());
        $notificationSettings->{$_POST['data']['submit']}->{$_POST['data']['type']} = $_POST['data']['message'];
        $this->getApi()->getPmsManager()->saveConfiguration($this->getSelectedMultilevelDomainName(), $notificationSettings);
    }
    
    public function updateMessage() {
        $notificationSettings = $this->getApi()->getPmsManager()->getConfiguration($this->getSelectedMultilevelDomainName());
        if(isset($_POST['data']['submit']) && $_POST['data']['submit'] == "remove") {
            unset($notificationSettings->{$_POST['data']['type']}->{$_POST['data']['key']});
        } else {
            $notificationSettings->{$_POST['data']['type']}->{$_POST['data']['key']} = $_POST['data']['message'];
            if(isset($_POST['data']['title'])) {
                $notificationSettings->{'emailTitles'}->{$_POST['data']['key']} = $_POST['data']['title'];
            }
        }
        $this->getApi()->getPmsManager()->saveConfiguration($this->getSelectedMultilevelDomainName(), $notificationSettings);
    }

    public function getLanguage() {
        if(isset($_SESSION['PmsSendMessagesConfigurationLang'])) {
            return $_SESSION['PmsSendMessagesConfigurationLang'];
        }
        return $this->getFactory()->getMainLanguage();
    }

    public function removeMessage() {
        $this->getApi()->getPmsNotificationManager()->deleteMessage($this->getSelectedMultilevelDomainName(), $_POST['data']['id']);
    }
    
    public function updateCreateMessage() {
        $msg = $this->getApi()->getPmsNotificationManager()->getMessage($this->getSelectedMultilevelDomainName(), $_POST['data']['id']);
        if($msg->id == "-1") {
            $msg->id = null;
        }
        $msg->title = $_POST['data']['title'];
        $msg->content = $_POST['data']['content'];
        $msg->key = $_POST['data']['key'];
        $msg->type = $_POST['data']['type'];
        $msg->languages = $_POST['data']['languages'];
        $msg->prefixes = $_POST['data']['prefixes'];
        $msg->roomTypes = $_POST['data']['roomtypes'];
        $msg->isManual = $_POST['data']['ismanual'] == "true";
        
        $this->getApi()->getPmsNotificationManager()->saveMessage($this->getSelectedMultilevelDomainName(), $msg);
    }
    
    public function getLanguageCodes() {
        $codes = [
            'ab' => 'Abkhazian',
            'aa' => 'Afar',
            'af' => 'Afrikaans',
            'ak' => 'Akan',
            'sq' => 'Albanian',
            'am' => 'Amharic',
            'ar' => 'Arabic',
            'an' => 'Aragonese',
            'hy' => 'Armenian',
            'as' => 'Assamese',
            'av' => 'Avaric',
            'ae' => 'Avestan',
            'ay' => 'Aymara',
            'az' => 'Azerbaijani',
            'bm' => 'Bambara',
            'ba' => 'Bashkir',
            'eu' => 'Basque',
            'be' => 'Belarusian',
            'bn' => 'Bengali',
            'bh' => 'Bihari languages',
            'bi' => 'Bislama',
            'bs' => 'Bosnian',
            'br' => 'Breton',
            'bg' => 'Bulgarian',
            'my' => 'Burmese',
            'ca' => 'Catalan, Valencian',
            'km' => 'Central Khmer',
            'ch' => 'Chamorro',
            'ce' => 'Chechen',
            'ny' => 'Chichewa, Chewa, Nyanja',
            'zh' => 'Chinese',
            'cu' => 'Church Slavonic',
            'cv' => 'Chuvash',
            'kw' => 'Cornish',
            'co' => 'Corsican',
            'cr' => 'Cree',
            'hr' => 'Croatian',
            'cs' => 'Czech',
            'da' => 'Danish',
            'dv' => 'Maldivian',
            'nl' => 'Dutch, Flemish',
            'dz' => 'Dzongkha',
            'en' => 'English',
            'eo' => 'Esperanto',
            'et' => 'Estonian',
            'ee' => 'Ewe',
            'fo' => 'Faroese',
            'fj' => 'Fijian',
            'fi' => 'Finnish',
            'fr' => 'French',
            'ff' => 'Fulah',
            'gd' => 'Gaelic',
            'gl' => 'Galician',
            'lg' => 'Ganda',
            'ka' => 'Georgian',
            'de' => 'German',
            'ki' => 'Gikuyu, Kikuyu',
            'el' => 'Greek (Modern)',
            'kl' => 'Greenlandic',
            'gn' => 'Guarani',
            'gu' => 'Gujarati',
            'ht' => 'Haitian',
            'ha' => 'Hausa',
            'he' => 'Hebrew',
            'hz' => 'Herero',
            'hi' => 'Hindi',
            'ho' => 'Hiri Motu',
            'hu' => 'Hungarian',
            'is' => 'Icelandic',
            'io' => 'Ido',
            'ig' => 'Igbo',
            'id' => 'Indonesian',
            'ie' => 'Interlingue',
            'iu' => 'Inuktitut',
            'ik' => 'Inupiaq',
            'ga' => 'Irish',
            'it' => 'Italian',
            'ja' => 'Japanese',
            'jv' => 'Javanese',
            'jedi' => 'Jedi',
            'kn' => 'Kannada',
            'kr' => 'Kanuri',
            'ks' => 'Kashmiri',
            'kk' => 'Kazakh',
            'rw' => 'Kinyarwanda',
            'klingon' => 'Klingon',
            'kv' => 'Komi',
            'kg' => 'Kongo',
            'ko' => 'Korean',
            'kj' => 'Kwanyama, Kuanyama',
            'ku' => 'Kurdish',
            'ky' => 'Kyrgyz',
            'lo' => 'Lao',
            'la' => 'Latin',
            'lv' => 'Latvian',
            'lb' => 'Letzeburgesch',
            'li' => 'Limburgish',
            'ln' => 'Lingala',
            'lt' => 'Lithuanian',
            'lu' => 'Luba-Katanga',
            'mk' => 'Macedonian',
            'mg' => 'Malagasy',
            'ms' => 'Malay',
            'ml' => 'Malayalam',
            'mt' => 'Maltese',
            'gv' => 'Manx',
            'mi' => 'Maori',
            'mr' => 'Marathi',
            'mh' => 'Marshallese',
            'ro' => 'Moldovan',
            'mn' => 'Mongolian',
            'na' => 'Nauru',
            'nv' => 'Navajo, Navaho',
            'nd' => 'Northern Ndebele',
            'ng' => 'Ndonga',
            'ne' => 'Nepali',
            'se' => 'Northern Sami',
            'no' => 'Norwegian',
            'ii' => 'Nuosu, Sichuan Yi',
            'oc' => 'Occitan (post 1500)',
            'oj' => 'Ojibwa',
            'or' => 'Oriya',
            'om' => 'Oromo',
            'os' => 'Ossetian, Ossetic',
            'pi' => 'Pali',
            'pa' => 'Panjabi, Punjabi',
            'ps' => 'Pashto, Pushto',
            'fa' => 'Persian',
            'pirate' => 'Pirate',
            'pl' => 'Polish',
            'pt' => 'Portuguese',
            'qu' => 'Quechua',
            'rm' => 'Romansh',
            'rn' => 'Rundi',
            'ru' => 'Russian',
            'sm' => 'Samoan',
            'sg' => 'Sango',
            'sa' => 'Sanskrit',
            'sc' => 'Sardinian',
            'sr' => 'Serbian',
            'sn' => 'Shona',
            'sd' => 'Sindhi',
            'si' => 'Sinhala',
            'sk' => 'Slovak',
            'sl' => 'Slovenian',
            'so' => 'Somali',
            'st' => 'Sotho, Southern',
            'nr' => 'South Ndebele',
            'es' => 'Spanish, Castilian',
            'su' => 'Sundanese',
            'sw' => 'Swahili',
            'ss' => 'Swati',
            'sv' => 'Swedish',
            'tl' => 'Tagalog',
            'ty' => 'Tahitian',
            'tg' => 'Tajik',
            'ta' => 'Tamil',
            'tt' => 'Tatar',
            'te' => 'Telugu',
            'th' => 'Thai',
            'bo' => 'Tibetan',
            'ti' => 'Tigrinya',
            'to' => 'Tonga',
            'ts' => 'Tsonga',
            'tn' => 'Tswana',
            'tr' => 'Turkish',
            'tk' => 'Turkmen',
            'tw' => 'Twi',
            'ug' => 'Uighur, Uyghur',
            'uk' => 'Ukrainian',
            'ur' => 'Urdu',
            'uz' => 'Uzbek',
            've' => 'Venda',
            'vi' => 'Vietnamese',
            'vo' => 'Volap_k',
            'volcan' => 'Volcan',
            'wa' => 'Walloon',
            'cy' => 'Welsh',
            'fy' => 'Western Frisian',
            'wo' => 'Wolof',
            'xh' => 'Xhosa',
            'yi' => 'Yiddish',
            'yo' => 'Yoruba',
            'za' => 'Zhuang, Chuang',
            'zu' => 'Zulu',
            'zu' => 'Zulu',
            'zu' => 'Zulu'
        ];
        return $codes;
    }
    
    public function getPhonePrefixes() {
        $array = [
	'44' => 'UK (+44)',
	'1' => 'USA (+1)',
	'213' => 'Algeria (+213)',
	'376' => 'Andorra (+376)',
	'244' => 'Angola (+244)',
	'1264' => 'Anguilla (+1264)',
	'1268' => 'Antigua & Barbuda (+1268)',
	'54' => 'Argentina (+54)',
	'374' => 'Armenia (+374)',
	'297' => 'Aruba (+297)',
	'61' => 'Australia (+61)',
	'43' => 'Austria (+43)',
	'994' => 'Azerbaijan (+994)',
	'1242' => 'Bahamas (+1242)',
	'973' => 'Bahrain (+973)',
	'880' => 'Bangladesh (+880)',
	'1246' => 'Barbados (+1246)',
	'375' => 'Belarus (+375)',
	'32' => 'Belgium (+32)',
	'501' => 'Belize (+501)',
	'229' => 'Benin (+229)',
	'1441' => 'Bermuda (+1441)',
	'975' => 'Bhutan (+975)',
	'591' => 'Bolivia (+591)',
	'387' => 'Bosnia Herzegovina (+387)',
	'267' => 'Botswana (+267)',
	'55' => 'Brazil (+55)',
	'673' => 'Brunei (+673)',
	'359' => 'Bulgaria (+359)',
	'226' => 'Burkina Faso (+226)',
	'257' => 'Burundi (+257)',
	'855' => 'Cambodia (+855)',
	'237' => 'Cameroon (+237)',
	'1' => 'Canada (+1)',
	'238' => 'Cape Verde Islands (+238)',
	'1345' => 'Cayman Islands (+1345)',
	'236' => 'Central African (+236)',
	'56' => 'Chile (+56)',
	'86' => 'China (+86)',
	'57' => 'Colombia (+57)',
	'269' => 'Comoros (+269)',
	'242' => 'Congo (+242)',
	'682' => 'Cook Islands (+682)',
	'506' => 'Costa Rica (+506)',
	'385' => 'Croatia (+385)',
	'53' => 'Cuba (+53)',
	'90392' => 'Cyprus North (+90392)',
	'357' => 'Cyprus South (+357)',
	'42' => 'Czech Republic (+42)',
	'45' => 'Denmark (+45)',
	'253' => 'Djibouti (+253)',
	'1809' => 'Dominica (+1809)',
	'1809' => 'Dominican Republic (+1809)',
	'593' => 'Ecuador (+593)',
	'20' => 'Egypt (+20)',
	'503' => 'El Salvador (+503)',
	'240' => 'Equatorial Guinea (+240)',
	'291' => 'Eritrea (+291)',
	'372' => 'Estonia (+372)',
	'251' => 'Ethiopia (+251)',
	'500' => 'Falkland Islands (+500)',
	'298' => 'Faroe Islands (+298)',
	'679' => 'Fiji (+679)',
	'358' => 'Finland (+358)',
	'33' => 'France (+33)',
	'594' => 'French Guiana (+594)',
	'689' => 'French Polynesia (+689)',
	'241' => 'Gabon (+241)',
	'220' => 'Gambia (+220)',
	'7880' => 'Georgia (+7880)',
	'49' => 'Germany (+49)',
	'233' => 'Ghana (+233)',
	'350' => 'Gibraltar (+350)',
	'30' => 'Greece (+30)',
	'299' => 'Greenland (+299)',
	'1473' => 'Grenada (+1473)',
	'590' => 'Guadeloupe (+590)',
	'671' => 'Guam (+671)',
	'502' => 'Guatemala (+502)',
	'224' => 'Guinea (+224)',
	'245' => 'Guinea - Bissau (+245)',
	'592' => 'Guyana (+592)',
	'509' => 'Haiti (+509)',
	'504' => 'Honduras (+504)',
	'852' => 'Hong Kong (+852)',
	'36' => 'Hungary (+36)',
	'354' => 'Iceland (+354)',
	'91' => 'India (+91)',
	'62' => 'Indonesia (+62)',
	'98' => 'Iran (+98)',
	'964' => 'Iraq (+964)',
	'353' => 'Ireland (+353)',
	'972' => 'Israel (+972)',
	'39' => 'Italy (+39)',
	'1876' => 'Jamaica (+1876)',
	'81' => 'Japan (+81)',
	'962' => 'Jordan (+962)',
	'7' => 'Kazakhstan (+7)',
	'254' => 'Kenya (+254)',
	'686' => 'Kiribati (+686)',
	'850' => 'Korea North (+850)',
	'82' => 'Korea South (+82)',
	'965' => 'Kuwait (+965)',
	'996' => 'Kyrgyzstan (+996)',
	'856' => 'Laos (+856)',
	'371' => 'Latvia (+371)',
	'961' => 'Lebanon (+961)',
	'266' => 'Lesotho (+266)',
	'231' => 'Liberia (+231)',
	'218' => 'Libya (+218)',
	'417' => 'Liechtenstein (+417)',
	'370' => 'Lithuania (+370)',
	'352' => 'Luxembourg (+352)',
	'853' => 'Macao (+853)',
	'389' => 'Macedonia (+389)',
	'261' => 'Madagascar (+261)',
	'265' => 'Malawi (+265)',
	'60' => 'Malaysia (+60)',
	'960' => 'Maldives (+960)',
	'223' => 'Mali (+223)',
	'356' => 'Malta (+356)',
	'692' => 'Marshall Islands (+692)',
	'596' => 'Martinique (+596)',
	'222' => 'Mauritania (+222)',
	'269' => 'Mayotte (+269)',
	'52' => 'Mexico (+52)',
	'691' => 'Micronesia (+691)',
	'373' => 'Moldova (+373)',
	'377' => 'Monaco (+377)',
	'976' => 'Mongolia (+976)',
	'1664' => 'Montserrat (+1664)',
	'212' => 'Morocco (+212)',
	'258' => 'Mozambique (+258)',
	'95' => 'Myanmar (+95)',
	'264' => 'Namibia (+264)',
	'674' => 'Nauru (+674)',
	'977' => 'Nepal (+977)',
	'31' => 'Netherlands (+31)',
	'687' => 'New Caledonia (+687)',
	'64' => 'New Zealand (+64)',
	'505' => 'Nicaragua (+505)',
	'227' => 'Niger (+227)',
	'234' => 'Nigeria (+234)',
	'683' => 'Niue (+683)',
	'672' => 'Norfolk Islands (+672)',
	'670' => 'Northern Marianas (+670)',
	'47' => 'Norway (+47)',
	'968' => 'Oman (+968)',
	'680' => 'Palau (+680)',
	'507' => 'Panama (+507)',
	'675' => 'Papua New Guinea (+675)',
	'595' => 'Paraguay (+595)',
	'51' => 'Peru (+51)',
	'63' => 'Philippines (+63)',
	'48' => 'Poland (+48)',
	'351' => 'Portugal (+351)',
	'1787' => 'Puerto Rico (+1787)',
	'974' => 'Qatar (+974)',
	'262' => 'Reunion (+262)',
	'40' => 'Romania (+40)',
	'7' => 'Russia (+7)',
	'250' => 'Rwanda (+250)',
	'378' => 'San Marino (+378)',
	'239' => 'Sao Tome & Principe (+239)',
	'966' => 'Saudi Arabia (+966)',
	'221' => 'Senegal (+221)',
	'381' => 'Serbia (+381)',
	'248' => 'Seychelles (+248)',
	'232' => 'Sierra Leone (+232)',
	'65' => 'Singapore (+65)',
	'421' => 'Slovak Republic (+421)',
	'386' => 'Slovenia (+386)',
	'677' => 'Solomon Islands (+677)',
	'252' => 'Somalia (+252)',
	'27' => 'South Africa (+27)',
	'34' => 'Spain (+34)',
	'94' => 'Sri Lanka (+94)',
	'290' => 'St. Helena (+290)',
	'1869' => 'St. Kitts (+1869)',
	'1758' => 'St. Lucia (+1758)',
	'249' => 'Sudan (+249)',
	'597' => 'Suriname (+597)',
	'268' => 'Swaziland (+268)',
	'46' => 'Sweden (+46)',
	'41' => 'Switzerland (+41)',
	'963' => 'Syria (+963)',
	'886' => 'Taiwan (+886)',
	'7' => 'Tajikstan (+7)',
	'66' => 'Thailand (+66)',
	'228' => 'Togo (+228)',
	'676' => 'Tonga (+676)',
	'1868' => 'Trinidad & Tobago (+1868)',
	'216' => 'Tunisia (+216)',
	'90' => 'Turkey (+90)',
	'7' => 'Turkmenistan (+7)',
	'993' => 'Turkmenistan (+993)',
	'1649' => 'Turks & Caicos Islands (+1649)',
	'688' => 'Tuvalu (+688)',
	'256' => 'Uganda (+256)',
	'380' => 'Ukraine (+380)',
	'971' => 'United Arab Emirates (+971)',
	'598' => 'Uruguay (+598)',
	'7' => 'Uzbekistan (+7)',
	'678' => 'Vanuatu (+678)',
	'379' => 'Vatican City (+379)',
	'58' => 'Venezuela (+58)',
	'84' => 'Vietnam (+84)',
	'1284' => 'Virgin Islands - British (+1284)',
	'1340' => 'Virgin Islands - US (+1340)',
	'681' => 'Wallis & Futuna (+681)',
	'969' => 'Yemen (North)(+969)',
	'967' => 'Yemen (South)(+967)',
	'260' => 'Zambia (+260)',
	'263' => 'Zimbabwe (+263)',
];
        return $array;
    }
    
    
    public function loadCreatedEditEvent() {
        $this->includefile("editcreatenotification");
    }
    
    public function getNotifications($langauge, $hasArx, $hasEventCalendar) {
        $notificationSettings = $this->getApi()->getPmsManager()->getConfiguration($this->getSelectedMultilevelDomainName());        
        $notifications = array();
        $notifications['room_starting_0_hours_'.$langauge] = "Booking is started.";
        $notifications['room_morning_message_'.$langauge] = "Greeting message sent in the morning same day check in (sent at 07:00) to guests.";
        $notifications['room_starting_4_hours_'.$langauge] = "Booking starting in 4 hours";
        $notifications['room_starting_12_hours_'.$langauge] = "Booking starting in 12 hours";
        $notifications['room_starting_24_hours_'.$langauge] = "Booking starting in 1 day";
        $notifications['room_starting_48_hours_'.$langauge] = "Booking starting in 2 days";
        $notifications['room_started_4_hours_'.$langauge] = "Booking started 4 hours ago";
        $notifications['room_started_12_hours_'.$langauge] = "Booking started 12 hours ago";
        $notifications['room_started_24_hours_'.$langauge] = "Booking started 24 hours ago";
        $notifications['room_started_48_hours_'.$langauge] = "Booking started 48 hours ago";
        $notifications['room_changed_'.$langauge] = "Room has been changed while stay is started";
        $notifications['room_resendcode_'.$langauge] = "When resending a code to the guest";
        $notifications['room_dooropenedfirsttime_'.$langauge] = "When guest opens the door";
        $notifications['date_changed_'.$langauge] = "Stay dates has been changed";
        $notifications['booking_completed_'.$langauge] = "Booking has been completed";
        $notifications['booking_completed_ota_'.$langauge] = "Booking has been completed by OTA (overrides standard complete message)";
        $notifications['booking_completed_payed_ota_'.$langauge] = "Booking has been completed and charged by OTA (overrides standard both ota and completed message)";
        $notifications['booking_notconfirmed_'.$langauge] = "Booking has been rejected";
        $notifications['room_ended_0_hours_'.$langauge] = "Booking has ended.";
        $notifications['room_ended_24_hours_'.$langauge] = "Booking ended 1 day ago";
        $notifications['room_ended_48_hours_'.$langauge] = "Booking ended two days ago";
        $notifications['room_cancelled_'.$langauge] = "Room has been cancelled (ignored if booked by ota)";
        $notifications['sendreciept_'.$langauge] = "When sending a reciept";
        $notifications['sendinvoice_'.$langauge] = "When sending an invoice";
        $notifications['warnfirstordernotpaid_'.$langauge] = "If order has not been paid for.";
        if($notificationSettings->runAutoPayWithCard) {
            $notifications['order_unabletopaywithsavecardwarning_'.$langauge] = "Warning sent 3 days before order expire (unable to pay with saved card)";
            $notifications['order_unabletopaywithsavecard_'.$langauge] = "Message sent when the order has expired and payment has not been completed.";
        }
        $notifications['room_added_to_arx_'.$langauge] = "When code is being sent out (integrated lock system)";
        $notifications['room_removed_from_arx_'.$langauge] = "When code is removed from the door (integrated lock system)";
        if($hasEventCalendar) {
            $notifications['booking_eventcalendar_'.$langauge] = "Message to send to event helder in the event calendar";
        }
        $notifications['booking_paymentmissing_'.$langauge] = "When payment is missing (autosending)";
        $notifications['booking_sendpaymentlink_'.$langauge] = "Sending the payment link (manually sending)";
        $notifications['booking_unabletochargecard_'.$langauge] = "Not able to charge card";
        return $notifications;
    }

    public function printLanguagesSelected($msg) {
        $alllanguages = $this->getLanguageCodes();
        echo "<div>";
        if(sizeof((array)$msg->languages) == 0) {
            $langs = $this->getApi()->getPmsNotificationManager()->getLanguagesForMessage($this->getSelectedMultilevelDomainName(), $msg->key, $msg->type);
            echo "All languages";
        } else {
            $toprint = array();
            foreach($msg->languages as $code) {
                $toprint[] = $alllanguages[$code];
            }
            echo "Language(s): " . strtolower(join(",", $toprint));
        }
        echo "&nbsp;&nbsp;&nbsp;";
        echo "</div>";
    }

    public function printPrefixesSelected($msg) {
        echo "<div>";
        if(sizeof((array)$msg->prefixes) == 0) {
            echo "All phone prefixes";
        } else {
            echo "For phone prefix: " . join(",", $msg->prefixes);
        }
        echo "&nbsp;&nbsp;&nbsp;";
        echo "</div>";
    }

}
?>
