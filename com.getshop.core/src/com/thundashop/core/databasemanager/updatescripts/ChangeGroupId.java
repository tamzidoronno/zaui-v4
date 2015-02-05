/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.databasemanager.updatescripts;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.hotelbookingmanager.BookingReference;
import com.thundashop.core.usermanager.data.User;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.mongodb.morphia.Morphia;

/**
 *
 * @author ktonder
 */
public class ChangeGroupId {
    public static void main(String args[]) throws UnknownHostException {
        Mongo m = new Mongo("localhost", 27017);
        DB db = m.getDB("UserManager");

        Morphia morphia = new Morphia();
        morphia.map(DataCommon.class);

        Map<String, Integer> set = new HashMap();
        DBCollection colectioninstance = db.getCollection("col_2fac0e57-de1d-4fdf-b7e4-5f93e3225445");
        DBCursor documents = colectioninstance.find();
        while (documents.hasNext()) {
            DBObject document = documents.next();
            DataCommon dataCommon = morphia.fromDBObject(DataCommon.class, document);
            if (dataCommon instanceof User) {
                User user = (User)dataCommon;
//                if (user.groups != null && user.groups.size() > 2) {
//                    continue;
//                }
                
                if (user.groups != null && user.groups.size() == 2) {
                    String id1 = user.groups.get(0);
                    String id2 = user.groups.get(1);
                    
                    if (id1.equals("b66cf951-0ede-4be8-9e13-73f6069a9566") && id2.equals("e65cf6cf-da84-4908-8ffb-d923f2a3d93a")) {
                        user.groups = new ArrayList();
                        user.groups.add("e65cf6cf-da84-4908-8ffb-d923f2a3d93a");
                        colectioninstance.save(morphia.toDBObject(user));
                    }
                    
                    if (id2.equals("b66cf951-0ede-4be8-9e13-73f6069a9566") && id1.equals("e65cf6cf-da84-4908-8ffb-d923f2a3d93a")) {
                        user.groups = new ArrayList();
                        user.groups.add("e65cf6cf-da84-4908-8ffb-d923f2a3d93a");
                        colectioninstance.save(morphia.toDBObject(user));
                    }
                    
                    if (id1.equals("ddcdcab9-dedf-42e1-a093-667f1f091311") && id2.equals("ca6a4104-5ead-41b2-9aa3-d01bb2672017")) {
                        user.groups = new ArrayList();
                        user.groups.add("ca6a4104-5ead-41b2-9aa3-d01bb2672017");
                        colectioninstance.save(morphia.toDBObject(user));
                    }
                    
                    if (id2.equals("ddcdcab9-dedf-42e1-a093-667f1f091311") && id1.equals("ca6a4104-5ead-41b2-9aa3-d01bb2672017")) {
                        user.groups = new ArrayList();
                        user.groups.add("ca6a4104-5ead-41b2-9aa3-d01bb2672017");
                        colectioninstance.save(morphia.toDBObject(user));
                    }
                }
                
//                if (user.groups != null && user.groups.size() == 1) {
//                    String groupId = user.groups.iterator().next();
//                    
//                    if (groupId.equals("b66cf951-0ede-4be8-9e13-73f6069a9566")) {
//                        user.groups = new ArrayList();
//                        user.groups.add("e65cf6cf-da84-4908-8ffb-d923f2a3d93a");
//                        colectioninstance.save(morphia.toDBObject(user));
//                    }
//                    
//                    if (groupId.equals("ddcdcab9-dedf-42e1-a093-667f1f091311")) {
//                        user.groups = new ArrayList();
//                        user.groups.add("ca6a4104-5ead-41b2-9aa3-d01bb2672017");
//                        colectioninstance.save(morphia.toDBObject(user));
//                    }
//                }
            }
        }
        
        System.out.println(set);

    }
}
