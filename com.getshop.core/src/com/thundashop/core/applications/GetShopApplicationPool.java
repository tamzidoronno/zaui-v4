/*
 * This is the main pool for applications. All available applications are available from here.
 */
package com.thundashop.core.applications;

import com.thundashop.core.appmanager.data.Application;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.databasemanager.Database;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */

@Component
public class GetShopApplicationPool implements IGetShopApplicationPool {

	@Autowired
	private Database database;
	
	private Map<String, Application> applications;
	
	@PostConstruct
	public void loadData() {
		Stream<DataCommon> dataStream = database.getAll("ApplicationPool", "all");
		applications = dataStream.map(o -> (Application)o).collect(Collectors.toMap(o -> o.id, o -> o));
	}
	
	@Override
	public List<Application> getApplications() {
		return new ArrayList(applications.values());
	}

	@Override
	public Application get(String applicationId) {
		return applications.get(applicationId);
	}

	@Override
	public void saveApplication(Application application) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	
}
