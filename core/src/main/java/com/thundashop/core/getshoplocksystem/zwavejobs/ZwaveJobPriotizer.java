/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.getshoplocksystem.zwavejobs;

import com.thundashop.core.getshoplocksystem.LocstarLock;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author ktonder
 */
public class ZwaveJobPriotizer {
    private List<LocstarLock> locks;

    public ZwaveJobPriotizer(List<LocstarLock> locks) {
        this.locks = new ArrayList(locks);
    }
    
    public LocstarLock getNextLock() {
        locks.stream().forEach(l -> l.finalize());
        
        Collections.sort(locks, (LocstarLock l1, LocstarLock l2) -> {
            return l1.getJobSize().compareTo(l2.getJobSize());
        });
        
        for (LocstarLock lock : locks) {
            if (lock.shouldPrioty())
                return lock;
        }
        
        List<LocstarLock> toUpdate = locks.stream()
                .filter(l -> l.canUpdate())
                .filter(l -> l.getJobSize() > 0)
                .collect(Collectors.toList());
        
        // Make sure that all has atleast 10 codes set on door before
        // continueing.
        for (LocstarLock lock : toUpdate) {
            if (lock.getJobSize() > 10) {
                return lock;
            }
        }
        
        for (LocstarLock lock : toUpdate) {
            lock.finalize();
            return lock;
        }
        
        Collections.sort(locks, (LocstarLock l1, LocstarLock l2) -> {
            return Comparator.nullsFirst(Date::compareTo).compare(l1.getDontUpdateUntil(), l2.getDontUpdateUntil());
        });
        
        return locks.stream()
                .filter(l -> l.getJobSize() > 0)
                .findAny()
                .orElse(null);
    }
}
