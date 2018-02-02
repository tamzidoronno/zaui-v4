package com.thundashop.core.filemanager;

import com.getshop.scope.GetShopSession;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.usermanager.UserManager;
import com.thundashop.core.usermanager.data.User;
import com.thundashop.core.uuidsecuritymanager.UUIDSecurityManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/*
 * File management
 */

@Component
@GetShopSession
public class FileManager extends ManagerBase implements IFileManager {
    HashMap<String, FileEntry> entries = new HashMap();
    
    @Autowired
    UserManager UserManager;
    
    @Autowired
    UUIDSecurityManager uUIDSecurityManager;

    @Override
    public void dataFromDatabase(DataRetreived data) {
        for (DataCommon retData : data.data) {
            if (retData instanceof FileEntry) {
                FileEntry toAdd = (FileEntry)retData;
                entries.put(toAdd.id, toAdd);
            }
        }
    }
    
    @Override
    public List<FileEntry> getFiles(String listId) {
        List<FileEntry> res = new ArrayList();
        for(FileEntry entry : entries.values()) {
            if(entry == null) {
                continue;
            }
            
            if(entry.listId != null && listId != null && !entry.listId.equals(listId)) {
                continue;
            }
            if(uUIDSecurityManager.hasAccess(entry.id, true, false)) {
                res.add(entry);
            }
        }
        
        Collections.sort(res, new Comparator<FileEntry>(){
            public int compare(FileEntry o1, FileEntry o2){
                return o2.rowCreatedDate.compareTo(o1.rowCreatedDate);
            }
       });
        
        
        return res;
    }

    @Override
    public String addFileEntry(String listId, FileEntry entry) {
        if(entry.id != null || !entry.id.isEmpty()) {
            if(!uUIDSecurityManager.hasAccess(entry.id, true, false)) {
                return null;
            }
        }
        entry.listId = listId;
        entry.storeId = storeId;
        saveObject(entry);
        
        User loggedOnUser = UserManager.getLoggedOnUser();
        if(loggedOnUser != null) {
            entry.uploadedByUserId = loggedOnUser.id;
        }
        entries.put(entry.id, entry);
        return entry.id;
    }

    @Override
    public void deleteFileEntry(String fileId) {
        if(!uUIDSecurityManager.hasAccess(fileId, false, true)) {
            return;
        }
        FileEntry toRemove = entries.get(fileId);
        if(toRemove != null) {
            deleteObject(toRemove);
            entries.remove(fileId);
        }
    }

    @Override
    public FileEntry getFile(String fileId) {
        if(!uUIDSecurityManager.hasAccess(fileId, true, false)) {
            return null;
        }
        return entries.get(fileId);
    }

    @Override
    public void renameFileEntry(String fileId, String newName) {
        if(!uUIDSecurityManager.hasAccess(fileId, false, true)) {
            return;
        }
        FileEntry toRename = entries.get(fileId);
        toRename.name = newName;
        saveObject(toRename);
    }
    
}
