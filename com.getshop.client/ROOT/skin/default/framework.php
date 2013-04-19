.header .applicationinner .logged_on_menu a {
    padding-right:4px;
    font-size:12px;
    color:#<? echo $this->getColors()->textColor; ?>;
    font-weight: bold;
}

.cke_skin_office2003 { border: solid 1px; }

.upload_information_panel { display:none; position: fixed; bottom: 10px; left: 10px; border: 1px solid; padding: 2px; z-index: 10000000; background-color: rgb(255, 255, 255); border-top-left-radius: 5px; border-top-right-radius: 5px; border-bottom-right-radius: 5px; border-bottom-left-radius: 5px; }
.upload_information_panel .uploading { padding-left: 30px; padding-right: 10px; }
.upload_information_panel .uploading img { position:absolute; left: 5px; }
.header .Login .applicationinner { margin-right: 0px; background-color: #<? echo $this->getColors()->baseColor; ?>; color: #<?  echo $this->getColors()->textColor; ?> }
.header .Login {border: solid 1px #<? echo $this->getColors()->baseColor; ?>; position: absolute; right: 0px; text-align: right; top: 0px; }
.header .Login form { margin: 0px; padding: 0px; }
.header .Login .form { text-align: left; height: 80px; width: 250px ; position: relative; display: none;} 
.header .Login .form .username { position:absolute; left: 5px; top: 5px;} 
.header .Login .form .password { position:absolute; right: 5px; top: 5px; } 
.header .Login .form .tstextfield { height: 18px; font-size: 13px; width: 100px;} 
.header .Login .form .recoverpassword { position:absolute;left:5px;bottom:5px;cursor:pointer; } 
.Login .heading { font-size:16px; font-weight: bold; }
.Login .recover_section { margin-bottom: 20px; padding-left: 10px; }
.Login .recoverpasswordholder { display: inline-block; position: relative; margin-left: 100px; width: 800px; }
.Login .recoverinput { font-size:16px; width: 770px; font-weight: bold; }
.header .Login .form .button { position:absolute; right: 5px; top: 48px; } 
.header .Login .form .button ins { width: 50px;} 
.header .Login .inner .entry { 
    display: inline-block;
    height: 20px; 
    background-repeat: no-repeat; 
    min-width:100px;
    font-weight: bold;
    text-align: center; 
    cursor: pointer; 
    right: 0px;
}
.informationbox .unpayedappheader { font-size:16px; padding-left: 10px; border: solid 1px #BBBBBB; padding-top:10px; padding-bottom: 10px; }
.informationbox .unpayedapptable tr:hover td { background-color: #EFEFEF; }
.inline {
    min-height: 10px;
    display: -moz-inline-stack;
    display: inline-block;
    vertical-align: top;
    zoom: 1;
    *display: inline;
}


.menuapplication {
    cursor: default;
    z-index: 1000;
    background-image: url('/skin/default/images/applicationareasettings.png');
    background-repeat: no-repeat;
    width: 20px;
    height: 17px;
}

.menuapplication .menuelements .firstelement {
    background-image: url('/skin/default/images/leftMenuOver-back.png');
    height: 21px;
    padding-left: 33px;
    padding-top: 5px;
}

.menuapplication .menuelements .entry img {
    float:left;
    margin: 5px;
}

.menuapplication .menuelements .entry {
    clear: both;
    padding-left: 7px;
    padding-top: 5px;
    margin-left: 1px;
    border-bottom: solid 1px #BBBBBB;
    border-top: solid 1px #FFFFFF;
    height: 110px;
    width: 575px;
}
.menuapplication .menuelements .entry:hover {
    border-bottom: solid 1px #42a92e;
    border-top: solid 1px #42a92e;
    cursor:pointer;
    background-color: #d4edcf;
}

.menuapplication .menuelements .entry .title {
    font-weight: bold;
}

.menuelements {
    display:block;
    z-index: 20001;
    width: 580px;
}


.menubox .entry {
    padding: 2px;
    border: solid 1px; 
    background-color: #FAFAFA;
    margin-bottom: 2px;
    margin-top: 2px;
    cursor: pointer;
    font-size: 11px;
}

.applicationarea .add_application_menu {
    position: absolute;
    left: 0px;
    right: 0px;
    bottom: 0px;
    top: 0px;
    z-index: 400;
    display: none;
    background-image: url('/skin/default/images/carbon-glare.png');
}

.applicationarea {
    position: relative;
}

.applicationarea .add_application_menu .infotext {
    color: #FFF;
    width: 165px;
}
.applicationarea .add_application_menu .importtext {
    color:#a090e8;
    cursor:pointer;
}

.applicationarea .add_application_menu .button-large .filler {
    background-color: #009b00;
}

.applicationareahover {
}

.loading {
    background-image: url('/skin/default/images/ajaxloader.gif');
    background-repeat: no-repeat;
    padding-left: 20px;
}

.informationbox-outer {
    position:absolute; 
    width:100%;
    text-align:center;
    z-index: 99999999;
    height: 1px;
}

#informationbox-holder {
    top: 50px;
    padding-top: 5px;
    padding-left: 7px;
    margin: 0 auto;
    overflow:auto;
    display:none;
    width: 1007px;
    text-align:left;
    position:relative;
    z-index: 99999999;
    position: relative;
}

#informationboxtitle {
    height: 30px;
    text-align: center;
    font-size: 16px;
    font-size: 18px;
    font-weight: bold;
    color: #333;
    background-color: #DDD;
}

#infomrationboxclosebutton {
    width: 29px;
    height: 29px; 
    right: 0px;
    top: 0px;
    position: absolute;
    background-repeat: no-repeat;
    cursor: pointer;
    background-image: url('/skin/default/elements/closebutton.png');
}

#informationboxmiddle {
    width: 990px;    
    background-color: #FFF;
    border-left: solid 5px #333333;
    border-right: solid 5px #333333;
}

#informationboxtop {
    width: 1000px;
    margin-top: 7px;
    height: 7px;
    background-image: url('/skin/default/images/informationbox-top.png');
}

#informationboxbottom {
    width: 1000px;
    height: 7px;
    background-image: url('/skin/default/images/informationbox-bottom.png');
}


.normalinformationbox {
    width: 990px;
}

.closeinformationbox {
    display:hidden;
}

.informationboxbackground {
    background-color: #FFFFFF;
    min-height: 500px;
}

.gray {
    color: #BBBBBB;
    border: solid 1px;
}

a {
    text-decoration: none;
    color:#000000;
}

a img {
    border: 0px;
}

#errorbox {
    position: fixed;
    width: 800px;
    top: 10px;
    left: 50%;
    margin-left: -380px;
    z-index: 1010000000000000000000000000;
    display: none;
/*
    color:#FF0000;
    font-weight: bold;
    border:solid 2px;
    padding: 10px;
    background-color: #EFEFEF;
    text-align: center;
    font-size: 16px;    */
}

.errorform .description,
.errorform .title {
    width: 600px;
    font-weight: bold;
    margin-bottom: 4px;
}

.errorform .wrapper {
    margin: 10px;
}

.errorform .close {
    width: 25px;
    height: 25px;
    background-image: url('/skin/default/images/close.png');
    position: absolute;
    right: -11px;
    top: -11px;
    cursor: pointer;
}

.errorform {
    position: relative;
    border: solid 2px red;
    background-color: #fbf9ee;
    padding: 10px;
}

.errorform .image {
    background-image: url('/skin/default/images/error.png');
    height: 66px;
    width: 80px;
    margin: 10px;
}

.display_menu_application_button {
    position:fixed;
    left:0px;
    cursor:pointer;
    top:0px;
    background-color: #FFFFFF;
    padding: 3px;
    width: 100px;
    text-align: center;
    border-right: solid 1px #333333;
    border-bottom: solid 1px #333333;
}

.spacingtop {
    height: 55px;
}

.helpbox .step {
    position: relitive;
}
.helpbox .steps {
    position:absolute; 
    color : #FFFFFF;
    top: 160px;
    font-size: 16px;
}

.helpbox .helpboxarrow {
    position:absolute;
    width: 16px;
    top: 0px;
    left: 60px;
    height: 10px;
    background-image: url("/skin/default/images/helpbox_arrow.png");
}

.helpbox .step2, .helpbox .step3, .helpbox .step4 {
    display:none;
}

.helpbox .title {
    font-size: 16px;
    padding-bottom: 5px;
    font-weight: bold;
}

.helpbox .number {
    padding-left: 10px;
}

.helpbox {
    position:absolute;
    background-image: url("/skin/default/images/helpbox.png");
    width: 395px;
    display: none;
    height: 166px;
    padding-top: 18px;
    z-index: 1001;
    padding-left: 10px;
    font-size: 14px;
    color: #FFFFFF;
    padding-right: 10px;
    top: 55px;
}

.helpbox .next, .helpbox .close, .helpbox .prev, .helpbox .send_feedback, .helpbox .close_feedback {
    left: 355px;
    position:absolute;
    top: 160px;
    width: 50px;
    cursor:pointer;
    font-size: 16px;
    color: #FFFFFF;
    background-image: url("/skin/default/images/helpbox_arrow_right.png");
    background-repeat: no-repeat;
    background-position: right;
    padding-left: 5px;
}
.helpbox .close_feedback {
    display:none;
    padding-left: 5px;
    left: 0px;
    background-image: none;
}
.helpbox .send_feedback {
    display:none;
    width: 160px;
    left: 250px;
}

.helpbox .feedback {
    display:none;
}

.helpbox .feedbacktext {
    width: 390px;
    height: 90px;
}

.helpbox .prev {
    left: 295px;
    width: 43px;
    padding-left: 15px;
    background-image: url("/skin/default/images/helpbox_arrow_left.png");
    background-repeat: no-repeat;
    background-position: left;
    border-right: solid 1px #4f6bce;
    height: 23px;
}
.helpbox .close {
    display:none;
}

.mainmenu .content {
    position: fixed;
    width: 100%;
    z-index: 15;
    top: 0px;
    background-image: url('/skin/default/images/maintopmenubackground.png');
    height: 60px;
}

.mainmenu .hide div {
    width: 180px;
    background-color: #fff;
    padding-left: 3px;
    padding-bottom: 2px; 
    padding-right: 3px;
    cursor: pointer;
    border-left: solid 1px #000;
    border-right: solid 1px #000;
    border-bottom: solid 1px #000;
}

.mainmenu .hide {
    position: fixed;
    width: 100%;
    z-index: 16;
    top:0px;
}

.mainmenu .group {
    margin-right: 3px;
    border-right: solid 2px #FFF;
    padding-right: 7px;
}

.mainmenu .group .title {
    text-align: center;
    height: 14px;
    font-size: 11px;
}

.mainmenu .group .entry .skeltype1 {
    background-image: url('/skin/default/images/three-column-icon.png');
}

.mainmenu .group .entry .skeltype2 {
    background-image: url('/skin/default/images/two-column-rigth-icon.png');
}

.mainmenu .group .entry .skeltype3 {
    background-image: url('/skin/default/images/two-column-left-icon.png');
}

.mainmenu .group .entry .skeltype4 {
    background-image: url('/skin/default/images/one-column-icon.png');
}

.mainmenu .group .entry .add {
    background-image: url('/skin/default/images/add_plus.png');
}

.mainmenu .group .entry .appmanagement {
    background-image: url('/skin/default/images/appmanagement.png');
}

.mainmenu .group .entry .security {
    background-image: url('/skin/default/images/lock.png');
}

.mainmenu .dropdown .selected {
    background-color:#BBBBBB;
}
.mainmenu .dropdown .selection {
    display:inline-block;
    width: 12px;
    height: 12px;
    border: solid 1px #BBBBBB;
    position:absolute;
    left: 0px;
}
.mainmenu .dropdown .allow_access_entry {
    cursor:pointer;
    padding-left: 18px;
    position: relative;
}

.mainmenu .dropdown {
    position:absolute;
    display:none;
    padding: 5px;
    border: solid 1px;
    width: 270px;
    background-color: #FFFFFF;
}

.mainmenu .group .entry .trashbutton {
    background-image: url('/skin/default/images/trash-can.png');
}

.mainmenu .group .entry .appotionsbutton {
    background-image: url('/skin/default/images/arrowdown.png');
}

.mainmenu .group .entry .reorderbutton {
    background-image: url('/skin/default/images/reorder.png');
}

.mainmenu .appoptionsdropdown .appoptionsentry {
    font-size:16px;
    cursor:pointer;
}

.mainmenu .appoptionsdropdown .dropdown_entry_text, .mainmenu .appoptionsdropdown .save_dropdown_entry_text {
    padding-top: 5px;
    display:inline-block;
}

.mainmenu .appoptionsdropdown .save_dropdown_entry_text {
    display:none;
}

.mainmenu .appoptionsdropdown .appoptionsentry:hover {
    background-color:#EFEFEF;
}

.mainmenu .group .entry .settings {
    background-image: url('/skin/default/images/settings.png');
}

.mainmenu .group .entry .themes {
    background-image: url('/skin/default/images/color-palette.png');
}

.mainmenu .group .entry .domain {
    background-image: url('/skin/default/images/domain-names.png');
}
.mainmenu .group .entry .intromovie {
    background-image: url('/skin/default/images/movie.png');
}
.mainmenu .group .entry .envelope {
    background-image: url('/skin/default/images/env.png');
}
.mainmenu .group .entry .supportcenter {
    background-image: url('/skin/default/images/question.png');
}

.mainmenu .group .entry .themes,
.mainmenu .group .entry .settings,
.mainmenu .group .entry .trashbutton,
.mainmenu .group .entry .domain,
.mainmenu .group .entry .security,
.mainmenu .group .entry .appmanagement,
.mainmenu .group .entry .add
{
    margin-left: 5px;
    margin-top: 4px;
    height: 28px;
}

.mainmenu .group .entry div {
    height: 20px;
    width: 26px;
    margin-left: 6px;
    margin-top: 8px;
}

.mainmenu .group .entry {
    background-image: url('/skin/default/images/buttongroup-blue.png');
    width: 38px;
    height: 37px;
    margin: 1px;
    text-indent: -3000px;
    cursor: pointer;
}

.mainmenu .appsright {
    position:absolute; 
    right: 10px;
    border-left: solid 2px #FFFFFF;
    padding-left: 7px;
    top: 0px;
}

.mainmenu .group .entry:hover {
    background-image: url('/skin/default/images/buttongroup-blue-hover.png');
}

.mainmenu .group .entry.disabled {
    background-image: url('/skin/default/images/buttongroup-disabled.png');
}

.mainmenu .group .entry.active {
    background-image: url('/skin/default/images/buttongroup-green.png');
}

.app.indicateapp { 
    border:solid 1px;
}

.mask {
    border: dotted 1px;
    cursor: pointer;
    display: none;
    position: absolute;
    width: 99.9%;
    height: 100%;
    z-index: 99999999;
    background-image: url('/skin/default/images/black-transparent.png');
}

.mask .inner,.order_mask .inner {
    display: table-cell;
    vertical-align: middle;
}


.order_mask .inner .reorder_up, .order_mask .inner .reorder_down {
    display:inline-block;
    width: 15px;
    cursor:pointer;
    height: 20px;
}
.order_mask .inner .reorder_up { float:left; margin-left: 5px; background-image: url('/skin/default/images/arrow_green_up.png'); }
.order_mask .inner .reorder_down { float:right; margin-right: 10px; background-image: url('/skin/default/images/arrow_green_down.png'); }

.applicationarea .mask, .applicationarea .order_mask {
    z-index: 10;
    position: absolute;
    display: none;
    opacity : 0.9;
    background-color : #2b42b8;
    color : #FFFFFF;
    border-radius: 5px;
    border: solid 1px #000000;
    text-align: center;
    font-size: 16px;
    vertical-align: middle;
    height: 0px;
}

.applicationarea .redmask {
    background-image: url('/skin/default/images/red-transparent.png');
}

.configable {
    position: relative;
}

.configable .configuration {
    background-image: url("/skin/default/images/editsettings.png");
    cursor:pointer;
    position:absolute;
    display:none;
    width:20px;
    height:20px;
    right: 5px;
    top: 5px;
}   

.configable .configuration .entries {
    z-index: 1000000000000000000000000000;
    right: 10px;
    position: absolute;    
    background-color: #FFFFFF;
    padding: 5px;
    display:none;
    top: 10px;
    border: solid 1px;
    width: 80px;
}

.configable .configuration .entries .entry {
    color: #333;
}

.information {
    border: dotted 1px; 
    padding-bottom: 10px; 
    margin-bottom: 10px;
    background-image: url('/skin/default/images/information.png');
    background-repeat: no-repeat;
    padding-left: 75px;
    min-height: 70px;
    background-position: 5 5;
    padding-top: 5px;
    font-size: 14px;
    padding-right: 5px;
    margin-top: 10px;
}


.theeme_selection {
    cursor:pointer;
}
.theeme_selection_design {
    padding-top: 10px;
    margin-bottom: 10px;
    border-top: 1px dotted;
    width: 100%;
}

.theeme_selection_design img {
    margin-right: 10px;
    padding-bottom:5px;
}
.theeme_selection_design .title {
    width:400px;
}

.upload_image_text {
    cursor: pointer;
    width: 190px;
    overflow:hidden;
}

.entries #fileupload {
    height: 20px;
}

#pagelayout {
    position: relative;
}

.submenu .entry {
    width: 130px;
}

.submenu {
    position: absolute;
    right: -128px;
    top: 0px;
    display: none;
    background-color: #cee2f0;
    border: solid 1px #8ebfdd;
}

.submenu .active {
    color: green;
    font-weight: bold;
}

.create_product {
    cursor: pointer;
}

.deleteable {
    position: relative;
}

.trash {
    background-image: url('/skin/default/images/trash.png');
    height: 20px;
    width:20px;
    top: 5px;
    left: 5px;
    position:absolute;
    z-index: 100000000;
    display: none;
}

#informationbox .settings .entry {
    position: relative;
    border-bottom: dotted 1;
    padding: 3px;
    margin-bottom: 5px;
    line-height: 20px;
    cursor: pointer;
}

#informationbox .settings .entry .extrainfo {
    padding-left: 20px; padding-right: 60px;
}

#informationbox .settings .onoff {
    position: absolute;
    right: 0px;
    width: 50px;
    height: 20px;
}

#informationbox .settings .onoff.on { 
    background-image: url('/skin/default/images/onbutton.png');
}

#informationbox .settings .onoff.off { 
    background-image: url('/skin/default/images/offbutton.png');
}

.wizard {
    position:absolute;
    top: 20px;
    left: 30%;
    border: solid 1px #FFFFFF;
    padding: 20px;
    z-index: 1001;
    border-radius: 15px;
    background-color:#FFFFFF;
}

.wizard .title {
    font-size:16px;
    font-weight: bold;
    margin-bottom: 10px;
}

.wizard .thankyou {
    font-size: 14px;
    margin-bottom: 10px;
}

.wizard .setuppoint {
    margin-top: 10px;
    font-weight: bold;
    font-size: 16px;
}

.wizard input {
    padding: 10px;
    width: 500px;
}

.wizard .information_text {
    font-size:16px;
    font-weight: bold;
    padding-top: 10px;
}

.wizard .information_text_url {
    font-size:20px;
}

.wizard .backbutton {
    float:right;
}

.wizard-overlay {
    position:absolute;
    width:100%;
    border: solid 1px;
    top: 0px;
    z-index:1000;
    left:0px;
    opacity: 0.8;
    height: 100%;
    background-color: #000000;
}

.wizard .continue {
    float:right;
    margin-top: 3px;
    margin-right: 15px;
}

.administration_menu_wizard {
    position:absolute;
    left: 120px;
    top: -5px;
    border: solid 1px;
    padding: 10px;
    background-color: #FFFFFF;
    z-index: 3;
    border-radius: 5px;
    display:none;
    background-color:#5e5e76;
    color:#FFFFFF;
}

.administration_menu_wizard .option {
    font-weight: bold;
    font-size:12px;
}

.emptytext { font-size: 16px; padding-left: 15px; padding-right: 15px; }
.empty_footer { padding-top: 30px; padding-bottom: 30px; }
.empty_left { padding-top: 60px; padding-left: 10px; padding-right: 10px; padding-bottom: 60px; }
.empty_middle { padding-top: 60px; padding-bottom: 60px; }
.empty_right { padding-top: 60px; padding-bottom: 60px; }

.errors { background-color: #FFF; font-weight: bold; margin: 0 auto; width: <?php echo $this->getWidthTotal(); ?>; border-left: solid 2px red; border-bottom: solid 2px red; border-right: solid 2px red; margin-bottom: 5px; text-align: center}

.tabs .tabset { width: 200px; padding-top:10px;}
.tabs .tabset .tab {  border-top: solid 1px transparent;  border-bottom: solid 1px transparent; border-right: solid 1px transparent;  cursor: pointer; font-size: 15px; line-height: 40px; padding-left: 10px; padding-right: 10px; }
.tabs .tabset .tab:hover { background-color: #EFEFEF; }
.tabs .tabset .tab.active { background-color: #DDD; border-top: solid 1px #BBB;  border-bottom: solid 1px #BBB; border-right: solid 1px #BBB; }

.tabs .tab_content .content_holder { display: none; width: 775px;}
.tabs .tab_content .active { display: block; }

.tabs .tab_content .application { padding-left: 10px; }
.tabs .tab_content .application .title { line-height: 40px; font-size: 15px; font-weight: bold; }
.tabs .tab_content .application .descriptionholder { font-size: 14px; background-color:#EFEFEF; padding: 5px; border: solid 1px #DDD; padding:10px; padding-right: 0px; }
.tabs .tab_content .application .description { width: 418px; padding-right: 10px; }
#informationbox .application .allFieldsNeedToBeFilled { font-size: 16px; float:left; padding-top: 10px; display:none; font-weight: bold; color:#FF0000; }
.tabs .tab_content .application .image { width: 320px; height: 320px; text-align:center; border-left: solid 1px #DDD; }

.commonleftmenu { margin-left: 8px; width: 150px; border-left: solid 1px #a4a4a4; border-right: solid 1px #a4a4a4; border-top: solid 1px #a4a4a4; }
.commonleftmenu .entry { width: 150px; background-image: url('/skin/default/images/standardleftmenuitembackground.png'); height: 34px; line-height: 34px; text-align: center; border-bottom: solid 1px #a4a4a4; border-top: solid 1px #efefef; }
.commonleftmenu .active { background-color: #d9d9d9; background-image: none; }

#messagebox { 
    width: 613px; 
    height: 186px; 
    position: fixed;
    margin: 0 auto;
    z-index: 100000000;
    left: 50%; margin: 0 0 0 -306px; 
    top: 50px;
}
#messagebox.ok { 
    background-image: url('/skin/default/images/messageboxbackground.png'); 
}

#messagebox.error { 
    background-image: url('/skin/default/images/messageboxbackground-error.png'); 
}

#messagebox {
    display: none;
}

#messagebox .inner {
    position: relative;
    width: 613px;
    height: 186px;
}

#messagebox .icon { 
    background-image: url('/skin/default/images/checkicon.png'); 
    width: 120px;
    height: 120px;
    margin-left: 30px;
    margin-top: 0px;
}

#messagebox.error .icon { 
    background-image: url('/skin/default/images/warning.png'); 
}

#messagebox.ok .okbutton { 
    display: none;
}

#messagebox.error .okbutton { 
    display: block;    
    position: absolute;
    right: 22px;
    bottom: 44px;
    background-image: url('/skin/default/images/grey-ok-button.png'); 
    width: 106px;
    height: 35px;
    cursor: pointer;
}

#messagebox .description { 
    width: 420px;
    margin-left: 10px;
    margin-top: 6px;
    font-size: 14px;
}

#messagebox .title {
    margin-top: 23px;
    margin-left: 22px;
    width: 565px;
    font-size: 15px;
    font-weight: bold;
    text-align: center;
}

.ApplicationDisplayer .applications .even { margin-left: 50px; margin-right: 50px;}

.applications_shortcuts .contentbox:hover { 
    background-image: url('/skin/default/images/shortcutapplication-background-hover.png'); 
}

.applications_shortcuts .contentbox { 
    cursor: pointer;    
    width: 138px; 
    height: 146px; 
    background-image: url('/skin/default/images/shortcutapplication-background.png'); 
    position: relative;
    margin-left: 15px;
    margin-right: 15px;
    padding-left: 6px;
}

.applications_shortcuts .contentbox .image { 
    position: absolute;
    top: 40px;
    left: 40px;
}

.applications_shortcuts .contentbox .arrow { 
    width: 30px;
    height: 30px; 
    background-image: url('/skin/default/images/greenarrowdown.png');
    position: absolute;
    left: 58px;
    top: 8px;
}

.applications_shortcuts .contentbox .title { 
    position: absolute;
    bottom: 10px;
    width: 133px;
    font-size: 14px;
    padding-top: 3px;
    color: #FFF;
    border-top: solid 1px #BBB;
}

.applciations_shortcut_other:hover {
    background-image: url('/skin/default/images/addotherapplicationsbackground-hover.png');
}

.applciations_shortcut_other {
    cursor: pointer;
    background-image: url('/skin/default/images/addotherapplicationsbackground.png');
    width: 500px;
    height: 50px;
    color: #FFF;
    font-size: 14px;
    line-height: 50px;
    margin-top: 30px;
}

.applciations_shortcut_other_column {
    width: 170px;
    background-image: url('/skin/default/images/addotherapplicationsbackground_small.png');
}
.applciations_shortcut_other_column:hover {
    background-image: url('/skin/default/images/addotherapplicationsbackground_small_hover.png');
}

.navigation span.breadcrumbentry.link  {
    background-image: url('/skin/default/images/breadcrumbseperator.png');
    background-position:right center;
    background-repeat: no-repeat;
}

.navigation span.breadcrumbentry  {
    display: inline-block;
    padding: 5px;
    padding-left: 3px;    
    padding-right: 10px;    
}

.breadcrumb-inner .navigation .breadcrumbentry a {
    color:#<? echo $this->getColors()->textColor; ?>;
}

.cke_dialog_body {
    z-index: 10000000000000000000000000000;
}

.middle .accessdenied {
    text-align: center;
    font-size: 16px;
}
#informationbox .GetShopColorPicker {
    text-align:center;
    position:relative;
}

#informationbox .GetShopColorPicker .colorbox input { width: 80px; }

#informationbox .GetShopColorPicker .colorbox div { border-bottom: solid 1px; background-color:#EFEFEF; }

#informationbox .GetShopColorPicker .colorbox { 
    font-size: 12px;
    color: #000000;
    display:inline-block;
    margin-right: 10px;
    border: solid 1px #BBBBBB;
}

.mainmenu-outer .content {
    z-index: 500;
}

#informationbox .applicationamanagement .title { font-size: 20px; color:#0A0A0A;margin-top: 5px; border-top: solid 1px #DDDDDD;padding-top: 5px; }
#informationbox .applicationamanagement .appname { border-radius: 3px; bordeR: solid 1px #BBBBBB; position:absolute; right: 35px; font-size: 20px; width: 300px; }
#informationbox .applicationamanagement #buildapplication { position:absolute; right: 30px; }
#informationbox .applicationamanagement .description { height: 60px; border-radius: 3px; border: solid 1px #DDDDDD; padding: 5px; padding-left: 80px;background-image: url('skin/default/startappcreate.png');background-repeat: no-repeat; background-position-x: 5px;background-position-y: 5px; }
#informationbox .applicationamanagement .inner_content { padding:10px; }
#informationbox .applicationamanagement .overviewbox { border: solid 1px; padding: 5px; display:inline-block; width: 350px; height: 150px; float:left; margin-right: 10px; margin-bottom: 10px; border-radius: 3px; border: solid 1px #DDDDDD; }
#informationbox .applicationamanagement .additonaltext {font-size:12px;padding-left: 10px; color:#A0A0A0; }

.selectablegroup .selectable {
    border: solid 2px transparent;
    cursor: pointer;
}

.selectablegroup .selectable.selected {
    border: solid 2px #000;
}

.ui-timepicker-div .ui-widget-header { margin-bottom: 8px; }
.ui-timepicker-div dl { text-align: left; }
.ui-timepicker-div dl dt { height: 25px; margin-bottom: -25px; }
.ui-timepicker-div dl dd { margin: 0 10px 10px 65px; }
.ui-timepicker-div td { font-size: 90%; }
.ui-tpicker-grid-label { background: none; border: none; margin: 0; padding: 0; }

.ui-timepicker-rtl{ direction: rtl; }
.ui-timepicker-rtl dl { text-align: right; }
.ui-timepicker-rtl dl dd { margin: 0 65px 10px 10px; }
