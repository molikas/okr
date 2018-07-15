package okr.etl.providers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;

import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.SearchResult;
import com.atlassian.util.concurrent.Promise;

import okr.domain.Objective;
import okr.repository.ObjectiveRepository;

/**
 * Provider that takes all the data from Jira. It is possible to extend the
 * implementation to perform custom mapping and more complex data processing.
 * TODO: Deployment configuration and orchestration
 * @author isidenica
 */
@Component
public class JiraDataProvider extends JiraInvoker implements DataProvider {

	private static final Logger log = Logger.getLogger(JiraDataProvider.class.getName());
	
	@Value(value="${etl.import.jql}")
	private String importJql;
	
	@Autowired
	private ObjectiveRepository oRepo;

	/**
	 * Load data once on application startup
	 * TODO: Move to scheduled task
	 */
	@EventListener(ApplicationReadyEvent.class)
	public void oneBigImportJob() {
		if (StringUtils.isEmpty(importJql)) return; //TODO: Proper configuration loading
		
		StopWatch watch = new StopWatch();
		Set<String> fields = new HashSet<>(CollectionUtils.arrayToList(new String[]{"*navigable"}));

		int i, total;
		i = total = 0;
		List<Objective> objectiveList = new ArrayList<>();
		do {
			watch.start("batch: "+i);
			// get initial data
			Promise<SearchResult> searchJqlPromise = client().getSearchClient().searchJql(importJql, 100, 0, fields );
			SearchResult srez = searchJqlPromise.claim();
			total = srez.getTotal();
			for (Issue issue :srez.getIssues()) {
				Objective obj = new Objective(issue.getKey(), issue.getSummary(), "");
				obj.setProperties(SpelUtils.extractFieldValue(issue));
				objectiveList.add(obj);
			}
			
			watch.stop();
			log.info("Jira import Batch: "+i+" - "+(i+100)+"; in seconds: "+watch.getLastTaskTimeMillis()/1000);
			i += 100;
		}while (i < total);
		log.info("Total tasks processed: "+total+"; in seconds: "+watch.getTotalTimeSeconds()/1000);
		// just to make sure there is no magic
		if (objectiveList.size() != total) {
			log.info("Retrieved issues sizes do not match total= "+total+ "; actual size="+objectiveList.size());
		}
		oRepo.save(objectiveList, 1);
	}

	// Getters - Setters
	public String getImportJql() {
		return importJql;
	}

	public void setImportJql(String importJql) {
		this.importJql = importJql;
	}

}
