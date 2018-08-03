/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

pga.page = {
    init: function() {
        $(document).ready(function() {
            pga.page.refresh();
        })
        
        $(document).on('click', '.successdebug', pga.page.debugSuccess);
        $(document).on('click', '.faileddebug', pga.page.debugFailed);
        $(document).on('click', '.messagedebug', pga.page.debugOther);
        $(document).on('click', '.cancelpaymentprocess', pga.page.cancelPaymentProcess);
    },
    
    cancelPaymentProcess: function() {
        var data = pga.page.createData();
        data.cancelPayment = true;
        pga.post(data);
    },
    
    refresh: function() {
        var data = pga.page.createData();
        
        pga.post(data);
        setTimeout(pga.page.refresh, 500);
    },
    
    createData: function() {
        var orderid = $('.data').attr('orderid');
        var amount = $('.data').attr('amount');
        
        var data = {
            orderid : orderid,
            amount: amount
        }
        
        return data;
    },
    
    debugSuccess: function() {
        var data = pga.page.createData();
        data.debug = true;
        data.action = "success";
        pga.post(data);
    },
    
    debugFailed: function() {
        var data = pga.page.createData();
        data.debug = true;
        data.action = "failed";
        pga.post(data);
    },
    
    debugOther: function() {
        var data = pga.page.createData();
        data.debug = true;
        data.action = "other";
        pga.post(data);
    }
}

pga.page.init();