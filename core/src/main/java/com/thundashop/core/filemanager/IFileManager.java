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
    public void addFileEntry(String listId, FileEntry entry);
    public void deleteFileEntry(String fileId);
    public FileEntry getFile(String fileId);
}
