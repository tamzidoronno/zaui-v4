
.button {
    display: inline-block;
    height: 28px;
    line-height: 28px;
    position: relative;
    text-align: center;
    background:url(/skin/default/elements/button-small-glare.png) no-repeat right top;
}

.button .rightglare {
    position: absolute;
    right: 3px;
    height: 20px;
    top: 5px;
    width: 3px;
    z-index: 100;
    background: url(/skin/default/elements/button-small-glare-right.png) no-repeat right top;
}

.button .filler {
    position: absolute;
    left: 3px;
    top: 5px;
    right: 3px;
    bottom: 3px;
    background-color:#<? echo $this->getColors()->buttonBackgroundColor; ?>;
}   

.button ins {
    position: relative;
    padding-right: 3px;
    height: 28px;
    margin-right: 6px;
    line-height: 28px;
    cursor: pointer;
    padding-left: 9px;
    font-size: 10px;
    font-weight: bold;
    text-decoration: none;
    color:#<? echo $this->getColors()->buttonTextColor; ?>;
    display: inline-block;
    background:url(/skin/default/elements/button-small-glare.png) no-repeat left bottom;
}

.button-large {
    display: inline-block;
    height: 49px;
    line-height: 49px;
    position: relative;
    background:url(/skin/default/elements/button-large-glare.png) no-repeat right top;
}

.button-large .filler {
    position: absolute;
    left: 7px;
    top: 5px;
    right: 7px;
    bottom: 5px;
    border: 0px;
    border: solid 1px;
    background-color:#<? echo $this->getColors()->buttonBackgroundColor; ?>;
}  

.button-large .rightglare {
    position: absolute;
    right: 7px;
    height: 38px;
    top: 6px;
    width: 7px;
    z-index: 1000000;
    background: url(/skin/default/elements/button-large-glare-right.png);
}

.button-large ins {
    text-align: center;
    position: relative;
    padding-right: 3px;
    height: 49px;
    margin-right: 14px;
    line-height: 49px;
    cursor: pointer;
    padding-left: 16px;
    font-size: 13px;
    font-weight: bold;
    text-decoration: none;
    color:#<? echo $this->getColors()->buttonTextColor; ?>;
    display: inline-block;
    background:url(/skin/default/elements/button-large-glare.png) no-repeat left bottom;
}

.button.disabled {
    cursor: default;
    background:url(/skin/default/images/button-grey.png) no-repeat right top;
}

.button.disabled ins {
    background:url(/skin/default/images/button-grey.png) no-repeat left bottom;
}


.systembutton { cursor: pointer; }
.systembutton { 
    height: 10px;
    background-image: url('/skin/default/elements/sysbuttonbg.png');
    font-size: 10px;
    text-decoration: none;
    padding-left: 2px; padding-right: 2px;
    padding-bottom: 4px;
    text-align: center;
    
    color :#FFFFFF;
    border-left: solid 2px #a58986;
    border-top: solid 2px #a58986;
    
    border-bottom: solid 2px #633524;
    border-right: solid 2px #633524; 
}
.systembutton ins {
    text-decoration: none;
}


#ImportNextApp,
#ImportPrevApp {
    height: 40px;
    width: 50px;
    cursor: pointer;
}

#ImportPrevApp { background-image: url('skin/default/elements/ImportButtonLeft.png'); }
#ImportNextApp { background-image: url('skin/default/elements/ImportButtonRight.png'); }

#ImportPrevApp:hover { background-image: url('skin/default/elements/ImportButtonLeft-hover.png'); }
#ImportNextApp:hover { background-image: url('skin/default/elements/ImportButtonRight-hover.png'); }

#ImportPrevApp.disabled { background-image: url('skin/default/elements/ImportButtonLeft-disabled.png') !important; }
#ImportNextApp.disabled { background-image: url('skin/default/elements/ImportButtonRight-disabled.png') !important; }

#ImportSave,
#ImportCancel {
    width: 100px;
    height: 40px;
    background-image: url('skin/default/elements/ImportButton.png');
    text-align: center;
    line-height: 40px;
    font-size: 16px;
    cursor: pointer;
}

#ImportSave:hover,
#ImportCancel:hover {
    background-image: url('skin/default/elements/ImportButton-hover.png');
}