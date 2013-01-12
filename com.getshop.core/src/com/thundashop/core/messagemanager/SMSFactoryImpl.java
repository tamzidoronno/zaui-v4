/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.messagemanager;

import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.DatabaseSaver;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.Logger;
import com.thundashop.core.common.StoreComponent;
import com.thundashop.core.databasemanager.Database;
import com.thundashop.core.databasemanager.data.Credentials;
import com.thundashop.core.messagehandler.data.Message;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
public class SMSFactoryImpl extends StoreComponent implements SMSFactory, Runnable {
    @Autowired
    public Logger logger;
    
    private String message;
    private String to;
    private String from;
    
    private Credentials credentials;

    @Autowired
    public Database database;
    
    @Autowired
    public DatabaseSaver databaseSaver;

    public SMSFactoryImpl() {
        credentials = new Credentials();
        credentials.manangerName = "MessageManager";
        credentials.storeid = storeId;
    }
    
    @Override
    public void send(String from, String to, String message) {
        SMSFactoryImpl impl = new SMSFactoryImpl();
        impl.from = from;
        impl.to = to;
        impl.message = message;
        impl.databaseSaver = databaseSaver;
        impl.credentials = credentials;
        impl.database = database;
        impl.setStoreId(storeId);
        new Thread(impl).start();
    }

    private String encode(String text) {
        text = text.replaceAll(" " ,"%20");
        text = text.replaceAll("å", "%E5");
        text = text.replaceAll("ø", "%F8");
        text = text.replaceAll("æ", "%E6");
        text = text.replaceAll("Ø", "%D8");
        text = text.replaceAll("Å", "%C5");
        text = text.replaceAll("Æ", "%C6");
        return text;
    }
    
    private void saveMessageSent() throws ErrorException {
        Message message = new Message();
        message.from = from;
        message.to = to;
        message.type = Message.Type.SMS;
        message.content = this.message;
        message.storeId = storeId;
        databaseSaver.saveObject(message, credentials);
    }
    
    private boolean validateNumber() {
        if (to.length() != 8)
            return false;
        
        if (!to.subSequence(0, 1).equals("4") && !to.subSequence(0, 1).equals("9"))
            return false;
        
        try {
            Integer.parseInt(to);
        } catch (NumberFormatException ex) {
            return false;
        }
        
        return true;
    }
    
    public void run() {
        if (storeId == null || storeId.equals("")) {
            logger.error(this, "Could not send sms, the storeId is empty for the component.");
            return;
        }
        
        if (!validateNumber())
            return;
        
        URL url;
        InputStream is = null;
        DataInputStream dis;
        try {
            message = encode(message);
            from = encode(from);
            to = encode(to);
            String urlString = "http://dio.eurobate.com/push.php?bruker=arctic&passord=4gEo2X&til="+to+"&avsender="+from+"&melding="+message;
            url = new URL(urlString);
            is = url.openStream();
            dis = new DataInputStream(new BufferedInputStream(is));
        } catch (IOException ex) {
            logger.error(this, "Could not send sms to " + to + " from " + from + " message: " + message, ex);
            return;
        } finally {
            try { is.close(); } catch (IOException ioe) {}
        }
        
        try {
            saveMessageSent();
        } catch (ErrorException ex) {
            logger.error(this, "Was not able to save sent sms message");
        }
    }

    @Override
    public int messageCount(int year, int month) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, 1, 23, 59, 59);
        cal.add(Calendar.HOUR, -24);
        Date stopDate = cal.getTime();
        cal.add(Calendar.MONTH, -1);
        cal.add(Calendar.HOUR, 23);
        cal.add(Calendar.MINUTE, 59);
        Date startDate = cal.getTime();
        
        Credentials credentials = new Credentials();
        credentials.storeid = storeId;
        credentials.manangerName = "MessageManager";
                
        int count = 0;
        List<DataCommon> messages = database.retreiveData(credentials);
        for (DataCommon msg : messages) {
            if (msg instanceof Message) {
                Message sendtMessage = (Message)msg;
                if (sendtMessage.type.equals(Message.Type.SMS))
                    System.out.println(sendtMessage.to + " :-: " +msg.rowCreatedDate);
                if (sendtMessage.type.equals(Message.Type.SMS) 
                        && sendtMessage.rowCreatedDate.after(startDate) 
                        && sendtMessage.rowCreatedDate.before(stopDate)) 
                {
                    count++;
                }
            }
        }
        
        return count;
    }
}