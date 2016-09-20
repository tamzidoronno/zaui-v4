/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.c3;

import com.getshop.scope.GetShopSession;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.usermanager.data.User;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
@GetShopSession
public class C3Manager extends ManagerBase implements IC3Manager {
    
    public HashMap<String, C3Hour> hours = new HashMap();
    public HashMap<String, C3GroupInformation> groupInfos = new HashMap();
    public HashMap<String, WorkPackage> workPackages = new HashMap();
    public HashMap<String, C3Project> projects = new HashMap();
    public HashMap<String, C3TimeRate> timeRates = new HashMap();
    public HashMap<String, C3RoundSum> roundSums = new HashMap();
    public HashMap<String, C3UserMetadata> usersMetaData = new HashMap();
    public HashMap<String, C3ProjectPeriode> periodes = new HashMap();
    
    @Override
    public void dataFromDatabase(DataRetreived data) {
        data.data.stream().forEach(idata -> addData(idata));
    }
    
    private void addData(DataCommon data) {
        if (data instanceof C3Project)
            projects.put(data.id, ((C3Project)data));
        
        if (data instanceof WorkPackage)
            workPackages.put(data.id, ((WorkPackage)data));
        
        if (data instanceof C3GroupInformation)
            groupInfos.put(data.id, ((C3GroupInformation)data));
        
        if (data instanceof C3Hour)
            hours.put(data.id, ((C3Hour)data));
        
        if (data instanceof C3TimeRate)
            timeRates.put(data.id, ((C3TimeRate)data));
        
        if (data instanceof C3RoundSum)
            roundSums.put(data.id, ((C3RoundSum)data));
        
        if (data instanceof C3UserMetadata)
            usersMetaData.put(data.id, ((C3UserMetadata)data));
       
        if (data instanceof C3ProjectPeriode)
            periodes.put(data.id, ((C3ProjectPeriode)data));
    }    
    
    @Override
    public WorkPackage saveWorkPackage(WorkPackage workPackage) {
        saveObject(workPackage);
        workPackages.put(workPackage.id, workPackage);
        finalizeWorkPackage(workPackage);
        return workPackage;
    }

    @Override
    public void deleteWorkPackage(String workPackageId) {
        WorkPackage workPackage = workPackages.remove(workPackageId);
        if (workPackage != null)
            deleteObject(workPackage);
    }

    @Override
    public List<WorkPackage> getWorkPackages() {
        workPackages.values().stream().forEach(wp -> finalizeWorkPackage(wp));
        return new ArrayList(workPackages.values()); 
    }

    @Override
    public WorkPackage getWorkPackage(String id) {
        WorkPackage workPackage = workPackages.get(id);
        if (workPackage == null)
            return null;
        
        finalizeWorkPackage(workPackage);
        
        return workPackage;
    }

    private void finalizeWorkPackage(WorkPackage workPackage) {
        // TODO, finalize?
    }

    @Override
    public C3Project saveProject(C3Project project) {
        saveObject(project);
        projects.put(project.id, project);
        
        finalizeProject(project);
        return project;
    }

    @Override
    public void deleteProject(String projectId) {
        C3Project project = projects.remove(projectId);
        if (project != null)
            deleteObject(project);
    }

    @Override
    public List<C3Project> getProjects() {
        projects.values().stream().forEach(project -> finalizeProject(project));
        return new ArrayList(projects.values());
    }

    @Override
    public C3Project getProject(String id) {
        C3Project project = projects.get(id);
        finalizeProject(project);
        return project;
    }

    private void finalizeProject(C3Project project) {
        project.currentProjectPeriode = getActivePeriode();
    }

    @Override
    public List<C3Project> search(String searchText) {
        List<C3Project> retProjects = projects.values().stream()
                .filter(project -> project.name.toLowerCase().contains(searchText))
                .collect(Collectors.toList());
        
        retProjects.stream().forEach(project -> finalizeProject(project));
        
        return retProjects;
    }

    @Override
    public void assignProjectToCompany(String companyId, String projectId) {
        C3Project project = projects.get(projectId);
        if (project != null) {
            project.addCompany(companyId);
            saveObject(project);
        }
    }

    @Override
    public List<C3Project> getAllProjectsConnectedToCompany(String compnayId) {
        List<C3Project> retPorjects = projects.values()
                .stream()
                .filter(project -> project.isCompanyActivated(compnayId))
                .collect(Collectors.toList());
        
        retPorjects.forEach(project -> finalizeProject(project));
        
        return retPorjects;
    }

    @Override
    public void setProjectAccess(String companyId, String projectId, String workPackageId, boolean value) {

        C3Project project = getProject(projectId);
        if (project != null) {
            project.setCompanyAccess(companyId, workPackageId, value);
            saveObject(project);
        }
    }

    @Override
    public void removeCompanyAccess(String projectId, String companyId) {
        C3Project project = getProject(projectId);
        if (project != null) {
            project.removeCompanyAccess(companyId);
            saveObject(project);
        }
    }

    @Override
    public void setProjectCust(String companyId, String projectId, String workPackageId, int year, int price) {
        C3Project project = getProject(projectId);
        if (project != null) {
            project.setProjectCost(companyId, workPackageId, year, price);
            saveObject(project);
        }
    }

    @Override
    public List<UserProjectAccess> getAccessList() {
        if (getSession() == null || getSession().currentUser == null)
            return null;
        
        User user = getSession().currentUser;
        
        ArrayList<UserProjectAccess> retList = new ArrayList();
        
        for (C3Project project : projects.values()) {
            if (doesUserHaveAccess(project, user)) {
                UserProjectAccess access = new UserProjectAccess();
                access.projectId = project.id;
                access.workPackageIds = project.getWorkPackagesForUser(user);
                retList.add(access);
            }
        }
        
        return retList;
    }

    private boolean doesUserHaveAccess(C3Project project, User user) {
        if (user.companyObject == null)
            return false;
        
        return project.isCompanyActivated(user.companyObject.id);
    }

    @Override
    public int getPercentage(String companyId, String workPackageId, String projectId, int year) {
        C3Project project = getProject(projectId);
        if (project == null)
            return 100;
        
        return project.getPercentage(workPackageId, companyId, year);
    }

    @Override
    public void saveGroupInfo(String groupId, String type, boolean value) {
        C3GroupInformation info = getGroupInformation(groupId);
        
        if (type.equals("egen")) {
            info.financalEgen = value;
        }
        
        if (type.equals("forskning")) {
            info.financalForskning = value;
        }
        
        saveObject(info);
    }

    @Override
    public C3GroupInformation getGroupInformation(String groupId) {
        C3GroupInformation info = groupInfos.get(groupId);
        if (info == null) {
            info = new C3GroupInformation();
            info.id = groupId;
            saveObject(info);
            groupInfos.put(info.id, info);
        }
        
        return groupInfos.get(groupId);
    }

    @Override
    public void addHour(C3Hour hour) {
        C3Project project = projects.get(hour.projectId);
        if (project == null) {
            throw new NullPointerException("Can not add hours to a project that does not exists");
        }
        
        if (!canAdd(hour).isEmpty()) {
            throw new IllegalAccessError("Tried to add hours to a project outside of leagal hours");
        }
        
        hour.registeredByUserId = getSession().currentUser.id;
        saveObject(hour);
        hours.put(hour.id, hour);
        
        String companyId = getSession().currentUser.companyObject.id;
        project.addHour(companyId, hour);
    }

    @Override
    public UserProjectAccess getAccessListByProjectId(String projectId) {
        return getAccessList().stream()
                .filter(accessList -> accessList.projectId.equals(projectId))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<C3Hour> getHoursForCurrentUser(String projectId, Date from, Date to ) {
        return hours.values().stream()
                .filter(hour -> hour.registeredByUserId != null && hour.registeredByUserId.equals(getSession().currentUser.id))
                .filter(hour -> hour.projectId != null && hour.projectId.equals(projectId))
                .filter(hour -> hour.within(from, to))
                .collect(Collectors.toList());
    }

    @Override
    public C3Hour getHourById(String hourId) {
        return hours.get(hourId);
    }

    @Override
    public void addTimeRate(String name, int rateKr) {
        C3TimeRate rate = new C3TimeRate();
        rate.name = name;
        rate.rate = rateKr;
        
        saveObject(rate);
        timeRates.put(rate.id, rate);
    }

    @Override
    public List<C3TimeRate> getTimeRates() {
        return new ArrayList(timeRates.values());
    }

    @Override
    public void deleteTimeRate(String id) {
        C3TimeRate rate = timeRates.remove(id);
        if (rate != null) {
            deleteObject(rate);
        }
    }

    @Override
    public void saveRate(C3TimeRate rate) {
        saveObject(rate);
        timeRates.put(rate.id, rate);
    }

    @Override
    public void setC3RoundSum(int year, int sum) {
        C3RoundSum roundSum = roundSums.values().stream()
                .filter(r -> r.year == year)
                .findFirst()
                .orElse(new C3RoundSum());
        
        roundSum.sum = sum;
        roundSum.year = year;
        
        saveObject(roundSum);
        
        roundSums.put(roundSum.id, roundSum);
    }

    @Override
    public C3RoundSum getRoundSum(int year) {
        return roundSums.values().stream()
                .filter(r -> r.year == year)
                .findFirst()
                .orElse(null);
    }

    @Override
    public void setRateToUser(String userId, String rateId) {
        C3UserMetadata meta = usersMetaData.values().stream()
                .filter(c3 -> c3.userId.equals(userId))
                .findFirst()
                .orElse(new C3UserMetadata());
        
        meta.userId = userId;
        meta.timeRateId = rateId;
        
        saveObject(meta);
        usersMetaData.put(meta.id, meta);
    }

    @Override
    public C3TimeRate getTimeRate(String userId) {
        C3UserMetadata meta = usersMetaData.values().stream()
                .filter(c3 -> c3.userId.equals(userId))
                .findFirst()
                .orElse(new C3UserMetadata());
        
        return timeRates.get(meta.timeRateId);
    }

    @Override
    public void savePeriode(C3ProjectPeriode periode) {
        saveObject(periode);
        periodes.put(periode.id, periode);
    }

    @Override
    public List<C3ProjectPeriode> getPeriodes() {
        List<C3ProjectPeriode> ret = new ArrayList(periodes.values());
        
        Collections.sort(ret, (o1, o2) -> {
            return o1.from.compareTo(o2.from);
        });
        
        return ret;
    }

    @Override
    public C3ProjectPeriode getActivePeriode() {
        return periodes.values().stream()
                .filter(per -> per.isActive())
                .findFirst()
                .orElse(null);
    }
    
    public void setActivePeriode(String periodeId) {
        periodes.values().stream()
                .forEach(periode -> deactivateAndSave(periode));
        
        C3ProjectPeriode per = periodes.get(periodeId);
        if (per != null) {
            per.isActive = true;
            saveObject(per);
        }
        
        
    }

    private void deactivateAndSave(C3ProjectPeriode periode) {
        periode.isActive = false;
        saveObject(periode);
    }

    @Override
    public String canAdd(C3Hour hour) {
        C3Project project = getProject(hour.projectId);
        
        if (!hour.within(project.startDate, project.endDate)) {
            return "PROJECT_PERIODE_INVALIDE";
        }
        
        if (project.currentProjectPeriode == null) {
            return "OUTSIDE_OF_OPEN_PERIODE";
        }
        
        if (!hour.within(project.currentProjectPeriode.from, project.currentProjectPeriode.to)) {
            return "OUTSIDE_OF_OPEN_PERIODE";
        }
        
        return "";
    }

    @Override
    public List<C3ProjectPeriode> getPeriodesForProject(String projectId) {
        C3Project project = projects.get(projectId);
        
        List<C3ProjectPeriode> retPeriodes = periodes.values().stream()
                .filter(per -> per.isDateWithin(project.startDate))
                .collect(Collectors.toList());
        
        return retPeriodes;
    }
}
