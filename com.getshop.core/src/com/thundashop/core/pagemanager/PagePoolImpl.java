/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.pagemanager;

import com.thundashop.core.common.AppConfiguration;
import com.thundashop.core.common.DatabaseSaver;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.Logger;
import com.thundashop.core.databasemanager.data.Credentials;
import com.thundashop.core.listmanager.ListManager;
import com.thundashop.core.listmanager.data.Entry;
import com.thundashop.core.pagemanager.data.Page;
import com.thundashop.core.pagemanager.data.PageArea;
import com.thundashop.core.usermanager.data.User;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
@Scope("prototype")
public class PagePoolImpl {

    public HashMap<String, Integer> pageLayout = new HashMap<>();
    public HashMap<String, Page> pages = new HashMap<>();
    @Autowired
    public DatabaseSaver databaseSaver;
    private ApplicationPoolImpl applicationPool;
    private Credentials credentials;
    private Logger logger;
    private String storeId;
    public PageManager pageManager;

    @Autowired
    public PagePoolImpl(Logger logger) {
        this.logger = logger;
        setupDefaultLayouts();
    }

    public void setApplicationPool(ApplicationPoolImpl applicationPool) {
        this.applicationPool = applicationPool;
    }

    private void setupDefaultLayouts() {
        pageLayout.put(Page.DefaultPages.CartPage, Page.PageType.HeaderLeftMiddleFooter);
        pageLayout.put(Page.DefaultPages.CheckOut, Page.PageType.HeaderMiddleFooter);
        pageLayout.put(Page.DefaultPages.Home, Page.PageType.HeaderFooterLeftMiddleRight);
        pageLayout.put(Page.DefaultPages.MyAccount, Page.PageType.HeaderMiddleFooter);
        pageLayout.put(Page.DefaultPages.OrderOverviewPageId, Page.PageType.HeaderMiddleFooter);
        pageLayout.put(Page.DefaultPages.Users, Page.PageType.HeaderLeftMiddleFooter);
        pageLayout.put(Page.DefaultPages.Settings, Page.PageType.HeaderMiddleFooter);
        pageLayout.put(Page.DefaultPages.Domain, Page.PageType.HeaderMiddleFooter);
    }

    public void addFromDatabase(Page page) {
        pages.put(page.id, page);
    }

    public void initialize(Credentials credentials, String storeId) {
        this.credentials = credentials;
        this.storeId = storeId;
    }

    public Page createNewPage(int layout, String parentId) throws ErrorException {
        return createNewPage(layout, parentId, UUID.randomUUID().toString());
    }

    public Page get(String id) throws ErrorException {
        setDefaultPages();
        Page page = pages.get(id);
        if (page == null) {
            throw new ErrorException(30);
        }

        return finalizePage(page);
    }

    public Page changeLayout(String pageId, int layout) throws ErrorException {
        Page page = get(pageId);
        page.type = layout;
        databaseSaver.saveObject(page, credentials);
        return finalizePage(page);
    }

    public AppConfiguration addApplicationToPage(String pageId, String pageArea, String applicationSettingsId) throws ErrorException {
        AppConfiguration app = applicationPool.createNewApplication(applicationSettingsId);
        Page page = get(pageId);
        page.pageAreas.get(pageArea).applicationsList.add(app.id);
        databaseSaver.saveObject(page, credentials);
        return app;
    }

    public Page removeApplication(String applicationId) throws ErrorException {
        for (Page page : pages.values()) {
            Page pageres = removeApplicationFromPage(page, applicationId);
            if (pageres != null) {
                return pageres;
            }
        }
        return null;
    }

    public Page removeApplicationFromPage(Page page, String applicationId) throws ErrorException {
        for (PageArea pageArea : page.pageAreas.values()) {
            if (pageArea.applicationsList.contains(applicationId)) {
                pageArea.applicationsList.remove(applicationId);
            }
            if (pageArea.extraApplicationList.containsKey(applicationId)) {
                pageArea.extraApplicationList.remove(applicationId);
            }
            if (pageArea.applications.containsKey(applicationId)) {
                pageArea.applications.remove(applicationId);
            }
        }
        databaseSaver.saveObject(page, credentials);
        return finalizePage(page);
    }

    private List<String> getDefaultPageList() {
        List<String> defaultPages = new ArrayList<String>();

        for (Field field : Page.DefaultPages.class.getDeclaredFields()) {
            try {
                String pageId = field.get(this).toString();
                defaultPages.add(pageId);
            } catch (Exception ex) {
                logger.error(this, "Could not create defaultpage", ex);
            }
        }

        return defaultPages;
    }

    public void setDefaultPages() throws ErrorException {
        for (String pageId : getDefaultPageList()) {
            if (pages.get(pageId) == null) {
                Page page = createPage(pageLayout.get(pageId), pageId);
                databaseSaver.saveObject(page, credentials);
                pages.put(page.id, page);
                
                addDefaultApplications(page);
                if (page.id.endsWith("contact")) {
                    page.parent = pages.get("about");
                }
            }
        }
    }

    private void addDefaultApplications(Page page) throws ErrorException {
        if (Page.DefaultPages.Home.equals(page.id)) {
            AppConfiguration topMenu = addApplication("1051b4cf-6e9f-475d-aa12-fc83a89d2fd4", page, PageArea.Type.TOP);
            AppConfiguration logo = addApplication("974beda7-eb6e-4474-b991-5dbc9d24db8e", page, PageArea.Type.TOP);

            AppConfiguration footer = addApplication("d54f339d-e1b7-412f-bc34-b1bd95036d83", page, PageArea.Type.BOTTOM);
            AppConfiguration search = addApplication("626ff5c4-60d4-4faf-ac2e-d0f21ffa9e87", page, PageArea.Type.TOP);

            applicationPool.stickApplication(logo.id, 1);
            applicationPool.stickApplication(topMenu.id, 1);
            applicationPool.stickApplication(footer.id, 1);
            applicationPool.stickApplication(search.id, 1);
        }

        if (Page.DefaultPages.Users.equals(page.id)) {
            AppConfiguration app = applicationPool.createNewApplication("00d8f5ce-ed17-4098-8925-5697f6159f66", "users_admin_menu");
            addExistingApplication(app, page, PageArea.Type.LEFT);
            
            app.inheritate = 1;
            app.originalPageId = "users";
            applicationPool.saveApplicationConfiguration(app);
            
            ListManager listManager = pageManager.getManager(ListManager.class);
            Entry entry = new Entry();
            entry.name = "All users";
            listManager.addEntry("users_admin_menu", entry, page.id);
            addApplication("ba6f5e74-87c7-4825-9606-f2d3c93d292f", page, PageArea.Type.MIDDLE);
        }

        if (Page.DefaultPages.CartPage.equals(page.id) || Page.DefaultPages.CheckOut.equals(page.id)) {
            addApplication("900e5f6b-4113-46ad-82df-8dafe7872c99", page, PageArea.Type.MIDDLE);
        }

        if (Page.DefaultPages.OrderOverviewPageId.equals(page.id)) {
            addApplication("27716a58-0749-4601-a1bc-051a43a16d14", page, PageArea.Type.MIDDLE);
        }

        if (Page.DefaultPages.MyAccount.equals(page.id)) {
            addApplication("6c245631-effb-4fe2-abf7-f44c57cb6c5b", page, PageArea.Type.MIDDLE);
        }

        if (Page.DefaultPages.Settings.equals(page.id)) {
            addApplication("d755efca-9e02-4e88-92c2-37a3413f3f41", page, PageArea.Type.MIDDLE);
        }

        if (Page.DefaultPages.Domain.equals(page.id)) {
            addApplication("fb076580-c7df-471c-b6b7-9540e4212441", page, PageArea.Type.MIDDLE);
        }
    }

    private Page createPage(int type, String pageId) throws ErrorException {
        Page existingPage = pages.get(pageId);
        if (existingPage != null) {
            throw new ErrorException(90);
        }

        Page page = new Page();
        page.type = type;
        page.id = pageId;
        page.addAllPageAreas();
        page.storeId = storeId;
        return page;
    }

    private AppConfiguration addApplication(String appName, Page page, String pageArea) throws ErrorException {
        AppConfiguration app = applicationPool.createNewApplication(appName);
        return addExistingApplication(app, page, pageArea);
    }

    private AppConfiguration addExistingApplication(AppConfiguration app, Page page, String pageArea) throws ErrorException {
        PageArea area = page.pageAreas.get(pageArea);
        if (area.applicationsList == null) {
            area.applicationsList = new ArrayList();
        }
        
        area.applicationsList.add(app.id);
        databaseSaver.saveObject(page, credentials);
        return app;
    }

    private boolean shouldOnlyContainExtraApplications(Page page) {
        if (page.userLevel == 0) {
            return false;
        }

        User user = pageManager.getSession().currentUser;
        if (user == null) {
            return true;
        }

        if (user.type < page.userLevel) {
            return true;
        }

        return false;
    }

    public Page finalizePage(Page page) throws ErrorException {
        page.clear();
        addInheritatedApplications(page, page.parent);
        addStickedApplications(page);
        boolean onlyExtraApplications = shouldOnlyContainExtraApplications(page);
        page.populateApplications(applicationPool.getApplications(), onlyExtraApplications);
        page.sortApplications();
        return page;
    }

    private void addInheritatedApplications(Page currentPage, Page page) {
        if (page == null) {
            return;
        }

        for (PageArea pageArea : page.pageAreas.values()) {
            for (String application : pageArea.applicationsList) {
                System.out.println(application);
                try {
                    AppConfiguration appConfig = applicationPool.get(application);
                    if (appConfig.inheritate > 0) {
                        System.out.println(appConfig.appName);
                    }
                    if (appConfig != null
                            && appConfig.inheritate > 0
                            && currentPage.pageAreas.get(pageArea.type) != null
                            && currentPage.pageAreas.get(pageArea.type).applicationsList != null
                            && !currentPage.pageAreas.get(pageArea.type).applicationsList.contains(appConfig.id)) {
                        currentPage.pageAreas.get(pageArea.type).extraApplicationList.put(appConfig.id, page.id);
                    }
                } catch (ErrorException ex) {
                    ex.printStackTrace();
                }
            }
        }

        addInheritatedApplications(currentPage, page.parent);
    }

    private void addStickedApplications(Page page) {
        List<AppConfiguration> applications = applicationPool.getStickedApplications();
        for (AppConfiguration application : applications) {
            PageArea onPageArea = getPageArea(application);
            if (onPageArea != null) {
                PageArea addToPageArea = page.pageAreas.get(onPageArea.type);
                if (addToPageArea != null
                        && !addToPageArea.applicationsList.contains(application.id)
                        && !addToPageArea.extraApplicationList.containsKey(application.id)) {
                    addToPageArea.extraApplicationList.put(application.id, page.id);
                }
            }
        }
    }

    private PageArea getPageArea(AppConfiguration application) {
        for (Page page : pages.values()) {
            for (PageArea pageArea : page.pageAreas.values()) {
                if (pageArea.applicationsList.contains(application.id)) {
                    return pageArea;
                }
            }
        }

        return null;
    }

    public Page changePageUserLevel(String pageId, int userLevel) throws ErrorException {
        Page page = get(pageId);
        page.userLevel = userLevel;
        databaseSaver.saveObject(page, credentials);
        return finalizePage(page);
    }

    public Page reOrderApplication(String appId, String pageId, Boolean moveUp) throws ErrorException {
        Page page = get(pageId);
        if (moveUp) {
            page.moveApplicationUp(appId);
        } else {
            page.moveApplicationDown(appId);
        }

        databaseSaver.saveObject(page, credentials);
        return finalizePage(page);
    }

    public Page createNewPage(int layout, String parentId, String id) throws ErrorException {
        Page page = createPage(layout, id);
        Page parentPage = pages.get(parentId);

        if (parentPage != null) {
            page.parent = parentPage;
        }

        databaseSaver.saveObject(page, credentials);
        pages.put(page.id, page);
        return finalizePage(page);
    }

    public void deletePage(String id) throws ErrorException {
        Page page = get(id);
        pages.remove(page);
        databaseSaver.deleteObject(page, credentials);
    }

    public void addExistingApplicationToArea(String pageId, String appId, String area) throws ErrorException {
        AppConfiguration app = applicationPool.get(appId);
        Page page = get(pageId);
        addExistingApplication(app, page, area);
    }

    void savePage(Page page) throws ErrorException {
        databaseSaver.saveObject(page, credentials);
    }

    

}