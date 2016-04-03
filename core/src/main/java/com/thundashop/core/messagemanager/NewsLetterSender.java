/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.thundashop.core.messagemanager;

import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.ManagerBase;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class NewsLetterSender extends ManagerBase {
    public List<NewsLetterManager> managers = new CopyOnWriteArrayList();
    
    @Scheduled(fixedRate=300000)
    public synchronized void scheduledMailSending() throws ErrorException {
        for (NewsLetterManager manager : managers) {
            manager.run();
        }
    }
    
    public void addManager(NewsLetterManager manager) {
        managers.add(manager);
    }
}
