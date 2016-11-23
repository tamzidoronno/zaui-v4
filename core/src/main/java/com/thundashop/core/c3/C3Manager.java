/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.c3;

import com.getshop.scope.GetShopSession;
import com.ibm.icu.util.Calendar;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.DoubleKeyMap;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.usermanager.UserManager;
import com.thundashop.core.usermanager.data.Company;
import com.thundashop.core.usermanager.data.User;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
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
    public HashMap<String, C3OtherCosts> otherCosts = new HashMap();
    public HashMap<String, C3ForskningsUserPeriode> forskningUsersPeriodes = new HashMap();
    public HashMap<String, C3UserNfrAccess> nfrAccess = new HashMap();
    
    @Autowired
    private UserManager userManager;
    
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
        
        if (data instanceof C3OtherCosts)
            otherCosts.put(data.id, ((C3OtherCosts)data));
        
        if (data instanceof C3ForskningsUserPeriode)
            forskningUsersPeriodes.put(data.id, ((C3ForskningsUserPeriode)data));
        
        if (data instanceof C3UserNfrAccess)
            nfrAccess.put(data.id, ((C3UserNfrAccess)data));
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
        
        return workPackages.values()
                .stream()
                .sorted(( o1, o2) -> o1.name.compareTo(o2.name))
                .collect(Collectors.toList());
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
        
        List<C3Project> retList = new ArrayList(projects.values());
        Collections.sort(retList, (o1, o2) -> {
            try {
                Integer projectnumber1 = Integer.parseInt(o1.projectNumber);
                Integer projectnumber2 = Integer.parseInt(o2.projectNumber);
                
                return projectnumber1.compareTo(projectnumber2);
            } catch (Exception ex) {
                return 0;
            }
        });
        
        return retList;
    }

    @Override
    public C3Project getProject(String id) {
        C3Project project = projects.get(id);
        finalizeProject(project);
        return project;
    }

    private void finalizeProject(C3Project project) {
        project.currentProjectPeriode = getActivePeriode();
        project.finalize();
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
        
        return getAcceListForUser(user.id);
    }

    @Override
    public List<UserProjectAccess> getAcceListForUser(String userId) {
        User user = userManager.getUserById(userId);
        userManager.checkUserAccess(user);
        
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
    public Double getPercentage(String companyId, String workPackageId, String projectId, int year) {
        C3Project project = getProject(projectId);
        if (project == null)
            return new Double(0);
        
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
        
        if (hour.nfr && !allowedNfrHourCurrentUser()) {
            throw new IllegalAccessError("Not allowed to put hours without access");
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

    public List<C3Hour> getHoursForCurrentUser(String projectId, Date from, Date to ) {
        return getHoursForUser(projectId, from, to, getSession().currentUser.id);
    }

    private List<C3Hour> getHoursForUser(String projectId, Date from, Date to, String userId) {
        List<C3Hour> retHours = hours.values().stream()
                .filter(hour -> hour.registeredByUserId != null && hour.registeredByUserId.equals(userId))
                .filter(hour -> hour.projectId != null && hour.projectId.equals(projectId))
                .filter(hour -> hour.within(from, to))
                .collect(Collectors.toList());
        
        retHours.stream().forEach(o -> finalize(o));
        return retHours;
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
        
        C3TimeRate rate = timeRates.get(meta.timeRateId);
        
        if (rate == null)
            return new C3TimeRate();
        
        return rate;
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
    public String canAdd(ProjectCost hour) {
        C3Project project = getProject(hour.projectId);
        
        
        if (!hour.completlyWithin(project.startDate, project.endDate)) {
            return "PROJECT_PERIODE_INVALIDE";
        }
        
        if (project.currentProjectPeriode == null) {
            return "OUTSIDE_OF_OPEN_PERIODE";
        }
        
        System.out.println("Current periode: " + project.currentProjectPeriode.from + " - " + project.currentProjectPeriode.to);
        System.out.println(hour.from + " - " + hour.to);
        
        
        if (!hour.completlyWithin(project.currentProjectPeriode.from, project.currentProjectPeriode.to)) {
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

    @Override
    public C3OtherCosts saveOtherCosts(C3OtherCosts otherCost) {
        
        C3Project project = projects.get(otherCost.projectId);
        if (project == null) {
            throw new NullPointerException("Can not add hours to a project that does not exists");
        }
        
        if (!canAdd(otherCost).isEmpty()) {
            throw new IllegalAccessError("Tried to add hours to a project outside of leagal hours");
        }
        
        otherCost.registeredByUserId = getSession().currentUser.id;
        saveObject(otherCost);
        otherCosts.put(otherCost.id, otherCost);
        finalize(otherCost);
        return otherCost;
    }

    private void finalize(C3OtherCosts otherCost) {
        // TODO.
    }

    @Override
    public List<ProjectCost> getProjectCostsForCurrentUser(String projectId, Date from, Date to) {
        List<ProjectCost> projectCosts = new ArrayList();
        
        projectCosts.addAll(getHoursForCurrentUser(projectId, from, to));
        projectCosts.addAll(getOtherCostsForCurrentUser(projectId, from, to));
        
        Collections.sort(projectCosts, (o1, o2) -> {
            return o1.from.compareTo(o2.from);
        });
        
        return projectCosts;
    }

    private List<C3OtherCosts> getOtherCostsForCurrentUser(String projectId, Date from, Date to) {
        return getOtherCostsForUser(projectId, from, to, getSession().currentUser.id);
    }

    private List<C3OtherCosts> getOtherCostsForUser(String projectId, Date from, Date to, String userId) {
        return otherCosts.values().stream()
                .filter(hour -> hour.registeredByUserId != null && hour.registeredByUserId.equals(userId))
                .filter(hour -> hour.projectId != null && hour.projectId.equals(projectId))
                .filter(hour -> hour.within(from, to))
                .collect(Collectors.toList());
    }

    private void finalize(C3Hour o) {
        C3TimeRate timeRate = getTimeRate(o.registeredByUserId);
        
        if (timeRate != null) {
            o.cost = o.hours * timeRate.rate;
        }
    }

    @Override
    public C3OtherCosts getOtherCost(String otherCostId) {
        C3OtherCosts otherCost = otherCosts.get(otherCostId);
        finalize(otherCost);
        return otherCost;
    }

    @Override
    public void addForskningsUserPeriode(C3ForskningsUserPeriode periode) {
        saveObject(periode);
        
        Calendar cal = Calendar.getInstance();
        cal.setTime(periode.start);
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        periode.start = cal.getTime();
        
        
        cal.setTime(periode.end);
        cal.set(Calendar.HOUR, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        periode.end = cal.getTime();
        
        forskningUsersPeriodes.put(periode.id, periode);
    }
    
    @Override
    public List<C3ForskningsUserPeriode> getForskningsPeriodesForUser(String userId) {
        List<C3ForskningsUserPeriode> retArray = forskningUsersPeriodes.values().stream()
                .filter(periode -> periode.userId != null && periode.userId.equals(userId))
                .collect(Collectors.toList());
        
        return retArray;
    }

    @Override
    public void deleteForskningsUserPeriode(String periodeId) {
        C3ForskningsUserPeriode periode = forskningUsersPeriodes.remove(periodeId);
        if (periode != null) {
            deleteObject(periode);
        }
    }

    @Override
    public C3Report getReportForUserProject(String userId, String projectId, Date start, Date end, String forWorkPackageId) {
        C3Report report = new C3Report();
        
        report.startDate = start;
        report.endDate = end;
        report.hours.addAll(getHoursForUser(projectId, start, end, userId));
        report.otherCosts.addAll(getOtherCostsForUser(projectId, start, end, userId));
        report.userId = userId;
        
        report.roundSum = calculateRoundSum(userId, projectId, start, end);
        
        report.hours.stream()
                .forEach(hour -> finalize(hour));
        
        report.sumHours = report.hours.stream()
                .mapToInt(hour -> (int)hour.hours).sum();
        
        report.sumPost11 = report.hours.stream()
                .mapToInt(hour -> (int)hour.hours * getTimeRate(userId).rate).sum();
        
        if (forWorkPackageId != null) {
            User user = userManager.getUserById(userId);
            if (user == null || user.companyObject == null) {
                throw new RuntimeException("Generating a report with users that are not connected to a company");
            }
            
            double percent = getPercentage(user.companyObject.id, forWorkPackageId, projectId, getYear(start));
            report.recalcuate(percent);
        }
        return report;
    }

    private double calculateRoundSum(String userId, String projectId, Date start, Date end) {
        int year = getYear(start);
        C3RoundSum roundSumForYear = getRoundSum(year);
        C3Project project = getProject(projectId);
        List<C3ForskningsUserPeriode> forskningsPeriodes = getForskningsPeriodesForUser(userId);
        
        int totalForPeriode = 0;
        for (C3ForskningsUserPeriode forskningsPeriode : forskningsPeriodes) {
            if (!project.interCepts(forskningsPeriode.start, forskningsPeriode.end)) {
                continue;
            }
            
            Date toCalclateFrom = getHighestDate(project.startDate, forskningsPeriode.start);
            Date toCalculateTo = getLowestDate(project.endDate, forskningsPeriode.end);
            int days = daysBetween(toCalclateFrom, toCalculateTo);
            double priceEachDay = (double)roundSumForYear.sum / (double)365;
            double fullPrice = (priceEachDay * days);
            totalForPeriode += fullPrice * ((double)forskningsPeriode.percents/(double)100);
        }
        
        return totalForPeriode;
    }
    
    private int daysBetween(Date d1, Date d2){
        return (int)( (d2.getTime() - d1.getTime()) / (1000 * 60 * 60 * 24));
    }

    private int getYear(Date start) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(start);
        int year = cal.get(Calendar.YEAR);
        return year;
    }

    private Date getHighestDate(Date start, Date startDate) {
        if (start.after(startDate))
            return start;
        
        return startDate;
    }

    private Date getLowestDate(Date endDate, Date end) {
        if (end.equals(endDate))
            return end;
        
        if (endDate.before(end))
            return endDate;
        
        return end;
    }

    public UserManager getUserManager() {
        return userManager;
    }

    @Override
    public String getBase64SFIExcelReport(String companyId, Date start, Date end) {
        SFIExcelReportData reportData = createReportData(start, end, companyId, null);
        SFIExcelReport report = new SFIExcelReport(reportData);
        return report.getBase64Encoded();
    }

    private SFIExcelReportData createReportData(Date start, Date end, String companyId, String forWorkPackageId) {
        List<C3Project> projectsToUse = getAllProjectsConnectedToCompany(companyId);
        List<User> users = userManager.getUsersByCompanyId(companyId);
        SFIExcelReportData reportData = new SFIExcelReportData();
        List<WorkPackage> workPackages = getWorkPackages(projectsToUse, users, start, end);
        
        HashMap<String, Double> roundsums = getRoundSums(projectsToUse, users, start, end, forWorkPackageId);
        HashMap<String, Double> hoursInKind = getHours(projectsToUse, users, start, end, false, forWorkPackageId);
        HashMap<String, Double> hoursNfr = getHours(projectsToUse, users, start, end, true, forWorkPackageId);
        
        for (String userId : hoursNfr.keySet()) {
            SFIExcelReportDataPost11 post11 = createPost11(userId);
            post11.timer = hoursNfr.get(userId);
            post11.totalt = post11.timer * getTimeRate(userId).rate;
            post11.nfr = post11.totalt;
            if (post11.totalt > 0) {
                reportData.post11.add(post11);
            }
        }
        for (String userId : roundsums.keySet()) {
            SFIExcelReportDataPost11 post11 = createPost11(userId);
            post11.totalt = roundsums.get(userId);
            post11.nfr = post11.totalt;
            post11.timesats = 0;
            if (post11.totalt > 0) {
                reportData.post11.add(post11);
            }
        }
        for (String userId : hoursInKind.keySet()) {
            SFIExcelReportDataPost11 post11 = createPost11(userId);
            post11.timer = hoursInKind.get(userId);
            post11.totalt = post11.timer * getTimeRate(userId).rate;
            post11.inkind = post11.totalt;
            if (post11.totalt > 0) {
                reportData.post11.add(post11);
            }
        }
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM-yyyy");
        reportData.delprosjekter = workPackages.stream().filter(wp -> wp != null).map(wp -> wp.name).collect(Collectors.joining(","));
        reportData.nameOfPartner = userManager.getCompany(companyId).name;
        reportData.ansvarlig = userManager.getCompany(companyId).contactPerson;
        reportData.periode = sdf.format(start) + " - " + sdf.format(end);
        for (C3Project project : projectsToUse) {
            for (User user : users) {
                C3Report report = getReportForUserProject(user.id, project.id, start, end, forWorkPackageId);
                report.otherCosts.stream().filter(cost -> cost.nfr).forEach(cost -> addOtherCost(cost, reportData));
            }
            for (User user : users) {
                C3Report report = getReportForUserProject(user.id, project.id, start, end, forWorkPackageId);
                report.otherCosts.stream().filter(cost -> !cost.nfr).forEach(cost -> addOtherCost(cost, reportData));
            }
        }
        return reportData;
    }

    private void addOtherCost(C3OtherCosts cost, SFIExcelReportData reportData) {
        SFIExcelReportDataPost14 data = new SFIExcelReportDataPost14();
        data.navn = cost.comment;
        data.totalt = cost.cost;
        if (cost.nfr) {
            data.nfr = data.totalt;
        } else {
            data.inkind = data.totalt;
        }
        
        reportData.post14.add(data);
    }

    private void addSum(HashMap<String, Double> map, double hours, String userId) {
        Double sum = map.get(userId);
        if (sum == null) {
            map.put(userId, hours);
        } else {
            map.put(userId, hours + map.get(userId));
        }
    }

    private SFIExcelReportDataPost11 createPost11(String userId) {
        User user = userManager.getUserById(userId);
        SFIExcelReportDataPost11 data = new SFIExcelReportDataPost11();
        data.navn = user.fullName;
        data.timesats = getTimeRate(user.id).rate;
        return data;
    }
    
    private HashMap<String, Double> getRoundSums(List<C3Project> projectsToUse, List<User> users, Date start, Date end, String forWorkPackageId) {
        HashMap<String, Double> roundsums = new HashMap();
        
        for (C3Project project : projectsToUse) {
            for (User user : users) {
                C3Report report = getReportForUserProject(user.id, project.id, start, end, forWorkPackageId);
                
                if (report.roundSum > 0) {
                    addSum(roundsums, report.roundSum, user.id);
                }
            }
        }
        
        return roundsums;
    }

    private HashMap<String, Double> getHours(List<C3Project> projectsToUse, List<User> users, Date start, Date end, boolean isNfr, String forWorkPackageId) {
        HashMap<String, Double> retMap = new HashMap();
        
        for (C3Project project : projectsToUse) {
            for (User user : users) {
                C3Report report = getReportForUserProject(user.id, project.id, start, end, forWorkPackageId);
                double addHours = report.hours.stream()
                        .filter(hour -> hour.nfr == isNfr)
                        .mapToDouble(hour -> hour.hours).sum();
                addSum(retMap, addHours, user.id);
            }
        }
        
        return retMap;
    }

    private List<WorkPackage> getWorkPackages(List<C3Project> projectsToUse, List<User> users, Date start, Date end) {
        TreeSet<String> wpIds = new TreeSet();
        
        for (C3Project project : projectsToUse) {
            for (String wpId : project.workPackages) {
                wpIds.add(wpId);
            }
        }
        
        return wpIds.stream().map(wpId -> workPackages.get(wpId)).collect(Collectors.toList());
    }

    @Override
    public boolean allowedNfrHour(String userId) {
        if (nfrAccess.get(userId) == null) {
            return false;
        }
        
        return nfrAccess.get(userId).hour;
    }

    @Override
    public boolean allowedNfrOtherCost(String userId) {
        if (nfrAccess.get(userId) == null) {
            return false;
        }
        
        return nfrAccess.get(userId).otherCost;
    }

    @Override
    public void setNfrAccess(C3UserNfrAccess access) {
        access.id = access.userId;
        saveObject(access);
        nfrAccess.put(access.id, access);
    }

    @Override
    public boolean allowedNfrHourCurrentUser() {
        return allowedNfrHour(getSession().currentUser.id);
    }

    @Override
    public boolean allowedNfrOtherCostCurrentUser() {
        return allowedNfrOtherCost(getSession().currentUser.id);
    }

    @Override
    public String getBase64ESAExcelReport(Date start, Date end) {
        Set<String> allCompaniesIds = new TreeSet();
        List<Company> allCompanies = new ArrayList();
        
        DoubleKeyMap<String, String, Double> totalCosts = new DoubleKeyMap();
        DoubleKeyMap<String, String, Double> inKind = new DoubleKeyMap();
        
        for (WorkPackage workPackage : workPackages.values()) {
            List<Company> companies = getAllCompaniesThatHasRelationToWorkpackage(workPackage);
            allCompaniesIds.addAll(companies.stream().map(comp -> comp.id).collect(Collectors.toList()));
        }
        
        allCompanies = allCompaniesIds.stream().map(o -> userManager.getCompany(o)).collect(Collectors.toList());
        
        HashMap<String, Double> rcnGrantWorkPackage = new HashMap();
        
        for (WorkPackage workPackage : workPackages.values()) {
            for (Company company : allCompanies) {
                SFIExcelReportData reportData = createReportData(start, end, company.id, workPackage.id);

                totalCosts.put(workPackage.id, company.id, reportData.getTotal());
                inKind.put(workPackage.id, company.id, reportData.getTotalInkind());
                
                double rcnGrant = reportData.getTotal() - reportData.getTotalInkind();
                
                if (inKind.get(workPackage.id, "rcngrant") == null) {
                    inKind.put(workPackage.id, "rcngrant", rcnGrant);
                } else {
                    double addedRcnGrant = inKind.get(workPackage.id, "rcngrant");
                    inKind.put(workPackage.id, "rcngrant", rcnGrant + addedRcnGrant);
                }
                
            }
        }
        
        
        ESAReport report = new ESAReport(allCompanies, getWorkPackages(), totalCosts, inKind);
        return report.getBase64Encoded();
    }

    private List<Company> getAllCompaniesThatHasRelationToWorkpackage(WorkPackage workPackage) {
        Set<String> companyIds = new TreeSet();
        
        for (C3Project project : projects.values()) {
            if (project.workPackages.contains(workPackage.id)) {
                companyIds.addAll(project.getCompanyIds());
            }
        }
        
        return companyIds.stream().map(companyId -> userManager.getCompany(companyId)).collect(Collectors.toList());
    }

    @Override
    public List<C3Project> getAllProjectsConnectedToWorkPackage(String wpId) {
        
        List<C3Project> retList = projects.values().stream()
                .filter(project -> project.workPackages.contains(wpId))
                .sorted( C3Project.comperatorByProjectNumber() )
                .collect(Collectors.toList());
        
        retList.stream().forEach(pro -> finalizeProject(pro));
        return retList;
    }
    
}
