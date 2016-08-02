package com.thundashop.core.filemanager;

import com.thundashop.core.common.GetShopApi;
import java.util.List;

/**
 *
 * @author ptonder
 */
@GetShopApi
public interface IFileManager {
    public List<FileEntry> getFiles(String listId);
    public String addFileEntry(String listId, FileEntry entry);
    public void deleteFileEntry(String fileId);
    public void renameFileEntry(String fileId, String newName);
    public FileEntry getFile(String fileId);
}
