/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.calendar;

import com.thundashop.core.calendarmanager.data.Day;
import com.thundashop.core.calendarmanager.data.Entry;
import com.thundashop.core.calendarmanager.data.Month;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.usermanager.UserManager;
import com.thundashop.core.usermanager.data.Company;
import com.thundashop.core.usermanager.data.User;
import com.thundashop.core.utils.BrRegEngine;
import com.thundashop.core.utils.CompanySearchEngine;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ktonder
 */

class CalendarCandidate {
	public String name;
	public String companynumber;
	public String eventName;
	public String groupid;
}

class TheReport {

	private final HashMap<String, Month> months;
	private final UserManager userManager;
	private final CompanySearchEngine brRegEngine;

	
	TheReport(HashMap<String, Month> months, UserManager userManager, CompanySearchEngine brRegEngine) {
		this.months = months;
		this.brRegEngine = brRegEngine;
		this.userManager = userManager;
		try {
			generate();
		} catch (ErrorException ex) {
			Logger.getLogger(TheReport.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
	
	private void generate() throws ErrorException {
		List<CalendarCandidate> users = new ArrayList();
		
		for (Month month : months.values()) {
			if (month.year < 2014) {
				continue;
			}
			
			for (Day day : month.days.values()) {
				for (Entry entry : day.entries) {
					for (String attendee : entry.attendees) {
						User user = userManager.getUserById(attendee);
				
						CalendarCandidate candidate = new CalendarCandidate();
						candidate.name = user.fullName;
						candidate.eventName = entry.title;
						candidate.groupid = user.groups != null && user.groups.size() > 0 ? user.groups.iterator().next() : "";
						candidate.companynumber = user.birthDay;
						users.add(candidate);
					}
				}
			}		
		}
		
		generateReport(users);
	}

	private void generateReport(List<CalendarCandidate> candidates) {
		System.out.println("========================================");
		Map<String, Map<String, List<CalendarCandidate>>> candiatesMaps = new HashMap();
		for (CalendarCandidate candidate : candidates) {
			if (candidate.companynumber == null || candidate.companynumber.equals("")) {
				continue;
			}

			candidate.companynumber = candidate.companynumber.replace(" ", "").trim();
			
			// Group by groupid
			Map<String, List<CalendarCandidate>> candidatesGroupedOuter = candiatesMaps.get(candidate.groupid);
			if (candidatesGroupedOuter == null) {
				candidatesGroupedOuter = new HashMap<String, List<CalendarCandidate>>();
				candiatesMaps.put(candidate.groupid, candidatesGroupedOuter);
			}
			
			// Group by vat number
			List<CalendarCandidate> candidatesGrouped = candidatesGroupedOuter.get(candidate.companynumber);
			if (candidatesGrouped == null ) {
				candidatesGrouped = new ArrayList();
				candidatesGroupedOuter.put(candidate.companynumber, candidatesGrouped);
			}
			candidatesGrouped.add(candidate);
		}
		
		Map<String, String> groups = new HashMap();
		groups.put("b66cf951-0ede-4be8-9e13-73f6069a9566", "Mekonomen");
		groups.put("ddcdcab9-dedf-42e1-a093-667f1f091311", "Meca");
		groups.put("a220d5a5-729d-4546-be0b-6ca447659ed6", "BilXtra");
		
		for (String groupIds : groups.keySet()) {
			String groupName = groups.get(groupIds);
			System.out.println("Group," + groupName);
		
			Map<String, List<CalendarCandidate>> candiatesMapsInner = candiatesMaps.get(groupIds);
			for (String vatNumber : candiatesMapsInner.keySet()) {
				System.out.println("");
				List<CalendarCandidate> candidates2 = candiatesMapsInner.get(vatNumber);
				Company company = null;
				try {
					 company = brRegEngine.getCompany(vatNumber, false);
				} catch (Exception x) {
				}
				if (company != null) {
					System.out.println("Company,"+stripComma(company.name + " - deltagere: " + candidates2.size()));
					System.out.println("Org nr,"+stripComma(company.vatNumber));
					System.out.println("Address,"+stripComma(company.streetAddress));
					System.out.println("Postnumber,"+stripComma(company.postnumber));
					System.out.println("By,"+stripComma(company.city));
				} else {
					System.out.println("Company orgnr,"+stripComma(vatNumber + " - deltagere: " + candidates2.size()));
				}
				
				System.out.println("Candidates,");
				for (CalendarCandidate candidate : candidates2) {
					System.out.println(candidate.eventName+","+stripComma(candidate.name));
				}
			}
		}
		
	}
	
	private String stripComma(String in) {
		String out = in.replace(",", ".");
		return out;
	}
}
