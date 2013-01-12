package com.thundashop.core.messagemanager;

import com.thundashop.core.common.DatabaseSaver;
import com.thundashop.core.common.Logger;
import com.thundashop.core.common.ManagerBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 */
@Component
@Scope("prototype")
public class MessageManager extends ManagerBase implements IMessageManager {
    @Autowired
    public MailFactory mailFactory;
    
    @Autowired
    public SMSFactory smsFactory;
    
    @Autowired
    public MessageManager(Logger log, DatabaseSaver databaseSaver) {
        super(log, databaseSaver);
    }

    @Override
    public void sendMail(String to, String toName, String subject, String content, String from, String fromName) {
        mailFactory.send(from, to, subject, content);
    }

    @Override
    public int getSmsCount(int year, int month) {
        return smsFactory.messageCount(year, month);
    }
}