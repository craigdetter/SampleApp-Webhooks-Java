package com.intuit.developer.sampleapp.webhooks.service.qbo;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.intuit.developer.sampleapp.webhooks.domain.CompanyConfig;
import com.intuit.ipp.services.DataService;
import com.intuit.ipp.services.QueryResult;
import com.intuit.ipp.util.Logger;

/**
 * Class for implementing QBO Query api 
 * 
 * @author dderose
 *
 */
@Service(value="QueryAPI")
public class QueryService implements QBODataService {
	
	private static final String TAG = QueryService.class.getSimpleName();

	private static final org.slf4j.Logger LOG = Logger.getLogger();
	
	@Autowired
    DataServiceFactory dataServiceFactory;

	@Override
	public void callDataService(CompanyConfig companyConfig) throws Exception {
		
		// create data service
		DataService service = dataServiceFactory.getDataService(companyConfig);

		String query = "select * from ";
		try {
			LOG.info(TAG + "Calling Query API ");
			// Build query list for each subscribed entities
			List<String> subscribedEntities = Arrays.asList(companyConfig.getWebhooksSubscribedEntites().split(","));
			for (String entity : subscribedEntities) {
				executeQuery(query + entity, service);
				LOG.info(TAG + ".callDataService - query:" + query + ", e:" + entity);
			}
		} catch (Exception ex) {
			LOG.error(TAG + "Error loading app configs, query:" + query + " cause:" + ex.getCause(), ex.getCause());
		}
		
	}
	
	/**
	 * Call executeQuery api for each entity
	 * 
	 * @param query
	 * @param service
	 */
	public void executeQuery(String query, DataService service) {
		QueryResult result = null;
		try {
			LOG.info("Executing Query " + query);
			result = service.executeQuery(query);
			LOG.info(" Query complete, result:" + result + " query:" + query);
		} catch (Exception ex) {
			LOG.error(".executeQuery - Error loading app configs, q:" + query + " result:" + result, ex.getCause());
		}
	}

}
