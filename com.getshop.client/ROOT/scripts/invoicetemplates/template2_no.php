<?php

/* 
 * This template is used for thermal receipt printer
 * for the cash system.
 */
?>
<head>
  <meta charset="UTF-8">
</head>
<style>
    .title {
        font-size: 150px;
        text-align: center;
    }
    .page {
        width: 940px;
        font-size: 30px;
    }
    
    .orderitem {
        font-size: 40px;
        margin-top: 20px;
    }
    
    .bottom {
        text-align: center;
        padding-bottom: 50px;
        padding-top: 100px;
        font-size: 50px;
    }
    
    .orderitem .orderitemtax, 
    .orderitem .orderitemprice, 
    .orderitem .orderitemtax { display: none; }
    .orderitem .orderitemdescription { width: 550px; display: inline-block; vertical-align: top; padding-right: 50px;}
    .orderitem .orderitemcount { width: 100px; text-align: center; display: inline-block; vertical-align: top;}
    .orderitem .orderitemtotal { width: 200px; display: inline-block; text-align: right;}
    .orderitem .orderitemcount:after {
        content: " x";
    }
    
    .taxlines { padding-top: 50px; }
    .summaryline .summaryleft {display: inline-block; width: 500px; font-size: 50px;}
    .summaryline .summaryright {display: inline-block; width: 400px; font-size: 50px; text-align: right; }
    
    .totalAmountline { padding-top: 50px; font-size: 70px; }
    .totalAmountline .desc { display: inline-block; width: 500px; }
    .totalAmountline .amount { display: inline-block; width: 400px; text-align: right; }
    
    .companyinfo { font-size: 40px; margin-top: 100px; padding-left: 50px;}
    
</style>

<div class="page">
    <div class="title">Kvittering</div>
    
    <div>
        {itemLines}
    </div>
    
    <div class="taxlines">
        {taxLines}
    </div>
    
    <div class="totalAmountline">
        <div class="desc"> Totalt </div> <div class="amount"> {totalAmount} </div>
    </div>
    
    <div class="companyinfo">
        {companyName}
        <br/> Orgnr: {vatNumber}
        <br/> 
        <br/><b> Kontaktinfo: </b>
        <br/> {address}
        <br/> {postCode} - {city}
        <br/> Epost:  {contactEmail}
    </div>
    <div class="bottom">
        Takk for handelen og velkommen igjen.
    </div>
    
    <div>
        
    </div>
</div>