/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.utils;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ktonder
 */
public class EniroSearchResult {
	class ComapanyInfo {
		public String companyName;
		public String orgNumber;
		public String companyText;
	}
	
	class Address {
		public String streetName;
		public String postCode;
		public String postArea;
		public String postBox;
	}

	class Adverts {
		public Integer eniroId;
		public ComapanyInfo companyInfo;
		public Address address;
	}

	
	
	public List<Adverts> adverts = new ArrayList();
}
